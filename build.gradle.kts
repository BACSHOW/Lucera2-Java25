plugins {
    java
    kotlin("jvm") version "2.1.10"
    id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "l2.lucera2"
version = "1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Netty moderno 4.1.x para transporte de alta performance (KQueue, Epoll, NIO)
    implementation("io.netty:netty-all:4.1.115.Final")
    // LMAX Disruptor 4.0.0 para anel circular lock-free zero-alloc
    implementation("com.lmax:disruptor:4.0.0")
    // OSHI 6.6.5 para introspecção de topologia de CPU (Mechanical Sympathy, P-Cores e SMT filter)
    implementation("com.github.oshi:oshi-core:6.6.5")
    // Agrona 1.21.2 para Ring Buffers Off-Heap contíguos (Zero-GC e alinhamento em Cache Line)
    implementation("org.agrona:agrona:1.21.2")
    // Java-Thread-Affinity para CPU Pinning determinístico em núcleos físicos
    implementation("net.openhft:affinity:3.23.3")
    // Cap'n Proto para serialização zero-alloc e buffers de waypoints binários
    implementation("org.capnproto:runtime:0.1.16")
    // Zstandard JNI para compressão de alta performance de blocos de Geodata
    implementation("com.github.luben:zstd-jni:1.5.6-3")
    // Fastutil para coleções primitivas Zero-GC (Long2Int, Long2Float, OpenHashMap)
    implementation("it.unimi.dsi:fastutil:8.5.14")
    // HikariCP 6.3.0 e MariaDB Connector 3.5.1 (Compatibilidade nativa GraalVM AOT)
    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.1")

    // Dependências de libs/ (inclui todos os jars existentes + kotlin e coroutines)
    implementation(fileTree("libs") {
        include("*.jar")
        exclude("server.jar", "scripts.jar", "mysql-connector-java-*.jar")
    })

    // Testes unitários (JUnit 5 + AssertJ)
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
    main {
        java.srcDirs("java")
        kotlin.srcDirs("java")
        resources {
            srcDir("database")
            include("authd.json", "gamed.json")
        }
    }
    test {
        java.srcDirs("test/java")
    }
}

tasks.compileKotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        freeCompilerArgs.addAll(listOf("-Xjsr305=strict"))
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:-options", "-Xlint:-deprecation", "-Xlint:-unchecked"))
}

val jarServer = tasks.register<Jar>("jarServer") {
    archiveFileName.set("server.jar")
    destinationDirectory.set(file("libs"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output) {
        include("l2/**", "authd.json", "gamed.json", "META-INF/**")
    }
    from({
        configurations.runtimeClasspath.get()
            .filter { (it.name.contains("netty") || it.name.contains("disruptor") || it.name.contains("oshi") || it.name.contains("jna") || it.name.contains("agrona") || it.name.contains("affinity") || it.name.contains("capnproto") || it.name.contains("zstd") || it.name.contains("fastutil") || it.name.contains("HikariCP") || it.name.contains("mariadb")) && !it.name.contains("netty-3") }
            .map { zipTree(it) }
    })
    manifest {
        attributes(
            "Main-Class" to "l2.gameserver.GameServer",
            "Created-By" to "L2JLucera2 Gradle Engine",
            "Implementation-Version" to "1.0",
            "Implementation-Build" to "agaze33",
            "Build-Date" to "23/09 22:31",
            "Build-Jdk" to "GraalVM Java 25 (-O3 PGO NEON)"
        )
    }
}

val jarScripts = tasks.register<Jar>("jarScripts") {
    archiveFileName.set("scripts.jar")
    destinationDirectory.set(file("libs"))
    from(sourceSets.main.get().output) {
        exclude("l2/**", "authd.json", "gamed.json")
    }
    manifest {
        attributes(
            "Created-By" to "L2JLucera2 Gradle Engine",
            "Implementation-Version" to project.version
        )
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("dist") {
    dependsOn(jarServer, jarScripts)
    description = "Compila com Gradle sob Java 25 e gera server.jar e scripts.jar em libs/"
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("gameserver")
            mainClass.set("l2.gameserver.GameServer")
            sharedLibrary.set(false)
            configurationFileDirectories.from(
                rootProject.file("native-image-config/gameserver"),
                rootProject.file("native-image-config/manual"),
                rootProject.file("native-image-config/disruptor")
            )
            classpath.from(jarScripts)
            buildArgs.addAll(
                "-march=native",
                "-O3",
                "-H:+AddAllCharsets",
                "-H:IncludeResourceBundles=com.sun.org.apache.xerces.internal.impl.msg.SAXMessages,com.sun.org.apache.xerces.internal.impl.msg.XMLMessages,com.sun.org.apache.xerces.internal.impl.msg.XMLSchemaMessages,com.sun.org.apache.xerces.internal.impl.xpath.regex.message",
                "--enable-native-access=ALL-UNNAMED",
                "--initialize-at-run-time=l2.gameserver.Config,io.netty.channel.epoll.Epoll,io.netty.channel.kqueue.KQueue,com.lmax.disruptor.Sequence",
                "--initialize-at-build-time=com.lmax.disruptor.util.Util,com.lmax.disruptor.SleepingWaitStrategy,org.dom4j.DocumentFactory,org.dom4j.util.SimpleSingleton"
            )

            val extraArgs = (project.findProperty("nativeExtraArgs") as String?)?.trim().orEmpty()
            val instrumenting = extraArgs.contains("--pgo-instrument")
            if (extraArgs.isNotEmpty()) {
                buildArgs.addAll(extraArgs.split(" ").filter { it.isNotBlank() })
            }

            val pgoProfile = rootProject.file("default.iprof")
            if (!instrumenting && pgoProfile.exists()) {
                buildArgs.add("--pgo=${pgoProfile.absolutePath}")
            }
        }
    }
}

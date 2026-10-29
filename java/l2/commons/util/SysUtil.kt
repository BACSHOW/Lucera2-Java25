package l2.commons.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory

/**
 * Utilitário de Hardware, Sistema Operacional e JVM para L2JLucera2 (padrão BrProject-Native).
 * Detecta arquitetura de SO (macOS/Darwin, Linux, Windows), memória física e aplica a trava obrigatória de Java 25.
 */
object SysUtil {
    private val _log: Logger = LoggerFactory.getLogger(SysUtil::class.java)

    @JvmField val OS_NAME: String = System.getProperty("os.name", "").lowercase()
    @JvmField val OS_ARCH: String = System.getProperty("os.arch", "").lowercase()
    @JvmField val JAVA_VERSION: String = System.getProperty("java.version", "")
    @JvmField val JAVA_VENDOR: String = System.getProperty("java.vendor", "")

    @JvmField val IS_WINDOWS: Boolean = OS_NAME.contains("win")
    @JvmField val IS_MAC: Boolean = OS_NAME.contains("mac") || OS_NAME.contains("darwin")
    @JvmField val IS_LINUX: Boolean = OS_NAME.contains("nux") || OS_NAME.contains("nix")

    @JvmField val CPU_CORES: Int = Runtime.getRuntime().availableProcessors()

    /** Obtém o número principal da versão do Java (ex: 25). */
    @JvmStatic
    val javaFeatureVersion: Int
        get() {
            return try {
                Runtime.version().feature()
            } catch (_: Throwable) {
                val v = System.getProperty("java.version", "1.8")
                if (v.startsWith("1.")) v.substring(2, 3).toIntOrNull() ?: 8
                else v.split(".").firstOrNull()?.toIntOrNull() ?: 8
            }
        }

    /**
     * Trava estrita de Java 25:
     * Aborta a inicialização imediatamente se a JVM for inferior ao Java 25.
     */
    @JvmStatic
    fun enforceJava25() {
        val feature = javaFeatureVersion
        if (feature < 25) {
            _log.error("================================================================================")
            _log.error(" FATAL: L2JLucera2 requires Java 25 LTS or higher!")
            _log.error(" Detected JVM: Java {} ({})")
            _log.error(" Boot aborted. Please execute with Oracle GraalVM 25 or OpenJDK 25.")
            _log.error("================================================================================")
            System.err.println("\n[FATAL] L2JLucera2 requires Java 25 LTS or higher! Current: Java " + JAVA_VERSION + " (" + JAVA_VENDOR + ")")
            System.exit(1)
        }
    }

    /** Memória física total do sistema (em MB). Retorna -1 se indisponível. */
    @JvmStatic
    val totalPhysicalMemoryMB: Long
        get() {
            return try {
                val osBean = ManagementFactory.getOperatingSystemMXBean()
                val method = osBean.javaClass.getMethod("getTotalPhysicalMemorySize")
                method.isAccessible = true
                val bytes = method.invoke(osBean) as Long
                bytes / (1024 * 1024)
            } catch (_: Throwable) {
                -1L
            }
        }

    /** Memória física livre do sistema (em MB). */
    @JvmStatic
    val freePhysicalMemoryMB: Long
        get() {
            return try {
                val osBean = ManagementFactory.getOperatingSystemMXBean()
                val method = osBean.javaClass.getMethod("getFreePhysicalMemorySize")
                method.isAccessible = true
                val bytes = method.invoke(osBean) as Long
                bytes / (1024 * 1024)
            } catch (_: Throwable) {
                -1L
            }
        }

    /** Memória Heap usada pela JVM (em MB). */
    @JvmStatic
    val usedHeapMemoryMB: Long
        get() {
            val rt = Runtime.getRuntime()
            return (rt.totalMemory() - rt.freeMemory()) / (1024 * 1024)
        }

    /** Memória Heap máxima alocada para a JVM (em MB). */
    @JvmStatic
    val maxHeapMemoryMB: Long
        get() = Runtime.getRuntime().maxMemory() / (1024 * 1024)

    /** Nome canônico do sistema operacional para logs. */
    @JvmStatic
    val osLabel: String
        get() = when {
            IS_WINDOWS -> "Windows ($OS_ARCH)"
            IS_MAC -> "macOS / Darwin ($OS_ARCH)"
            IS_LINUX -> "Linux ($OS_ARCH)"
            else -> "$OS_NAME ($OS_ARCH)"
        }

    /** Exibe o banner de diagnóstico de sistema no boot. */
    @JvmStatic
    fun printSystemInfo() {
        _log.debug("----------------------------------------------------------------")
        _log.debug(" OS: {} | CPU Cores: {}", osLabel, CPU_CORES)
        val phys = totalPhysicalMemoryMB
        if (phys > 0) {
            _log.debug(" Physical RAM: {} MB (Free: {} MB) | JVM Heap: {} / {} MB", phys, freePhysicalMemoryMB, usedHeapMemoryMB, maxHeapMemoryMB)
        } else {
            _log.debug(" JVM Heap: {} / {} MB", usedHeapMemoryMB, maxHeapMemoryMB)
        }
        _log.debug(" JVM: {} ({}) on Java {}", JAVA_VENDOR, System.getProperty("java.vm.name", ""), JAVA_VERSION)
        _log.debug(" Java 25 LTS Runtime Locked: ACTIVE [100% OK]")
        _log.debug("----------------------------------------------------------------")
    }
}

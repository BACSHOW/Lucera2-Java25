/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.versioning;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import l2.commons.versioning.Locator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version {
    private static final Logger _log = LoggerFactory.getLogger(Version.class);
    private String ti = "agaze33";
    private String tj = "1.0";
    private String tk = "23/09 22:31";
    private String tl = "GraalVM Java 25 (-O3 PGO NEON)";

    public Version(Class<?> clazz) {
        File file = null;
        try {
            file = Locator.getClassSource(clazz);
            if (file == null || !file.isFile()) {
                return;
            }
            JarFile jarFile = new JarFile(file);
            Attributes attributes = jarFile.getManifest().getMainAttributes();
            this.c(attributes);
            this.d(attributes);
            this.b(attributes);
            this.a(attributes);
        }
        catch (IOException iOException) {
            _log.error("Unable to get soft information\nFile name '" + (file == null ? "null" : file.getAbsolutePath()) + "' isn't a valid jar", iOException);
        }
    }

    private void a(Attributes attributes) {
        String string = attributes.getValue("Implementation-Version");
        this.tj = string != null ? string : "-1";
    }

    private void b(Attributes attributes) {
        String string = attributes.getValue("Implementation-Build");
        this.ti = string != null ? string : "-1";
    }

    private void c(Attributes attributes) {
        String string = attributes.getValue("Build-Jdk");
        this.tl = string != null ? string : ((string = attributes.getValue("Created-By")) != null ? string : "-1");
    }

    private void d(Attributes attributes) {
        String string = attributes.getValue("Build-Date");
        this.tk = string != null ? string : "-1";
    }

    public String getRevisionNumber() {
        return this.ti;
    }

    public String getVersionNumber() {
        return this.tj;
    }

    public String getBuildDate() {
        return this.tk;
    }

    public String getBuildJdk() {
        return this.tl;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.data.xml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import l2.commons.data.xml.AbstractHolder;
import l2.commons.data.xml.AbstractParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

public abstract class AbstractDirParser<H extends AbstractHolder>
extends AbstractParser<H> {
    protected AbstractDirParser(H h) {
        super(h);
    }

    public abstract File getXMLDir();

    public abstract boolean isIgnored(File var1);

    public abstract String getDTDFileName();

    @Override
    protected final void parse() {
        File file = this.getXMLDir();
        if (!file.exists()) {
            this.warn("Dir " + file.getAbsolutePath() + " not exists");
            return;
        }
        File file2 = new File(file, this.getDTDFileName());
        if (!file2.exists()) {
            this.info("DTD file: " + file2.getName() + " not exists.");
            return;
        }
        this.initDTD(file2);
        try {
            Collection<File> collection = FileUtils.listFiles(file, FileFilterUtils.suffixFileFilter(".xml"), FileFilterUtils.directoryFileFilter());
            for (File file3 : collection) {
                if (file3.isHidden() || this.isIgnored(file3)) continue;
                try {
                    this.parseDocument(new FileInputStream(file3), file3.getName());
                }
                catch (Exception exception) {
                    this.info("Exception: " + exception + " in file: " + file3.getName(), exception);
                }
            }
        }
        catch (Exception exception) {
            this.warn("Exception: " + exception, exception);
        }
    }
}


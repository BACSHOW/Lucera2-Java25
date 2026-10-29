/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.data.xml;

import java.io.File;
import java.io.FileInputStream;
import l2.commons.data.xml.AbstractHolder;
import l2.commons.data.xml.AbstractParser;

public abstract class AbstractFileParser<H extends AbstractHolder>
extends AbstractParser<H> {
    protected AbstractFileParser(H h) {
        super(h);
    }

    public abstract File getXMLFile();

    public abstract String getDTDFileName();

    @Override
    protected final void parse() {
        File file = this.getXMLFile();
        if (!file.exists()) {
            this.warn("file " + file.getAbsolutePath() + " not exists");
            return;
        }
        File file2 = new File(file.getParent(), this.getDTDFileName());
        if (!file2.exists()) {
            this.info("DTD file: " + file2.getName() + " not exists.");
            return;
        }
        this.initDTD(file2);
        try {
            this.parseDocument(new FileInputStream(file), file.getName());
        }
        catch (Exception exception) {
            this.warn("Exception: " + exception, exception);
        }
    }
}


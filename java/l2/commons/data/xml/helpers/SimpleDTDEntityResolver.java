/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.data.xml.helpers;

import java.io.File;
import java.io.IOException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SimpleDTDEntityResolver
implements EntityResolver {
    private String _fileName;

    public SimpleDTDEntityResolver(File file) {
        this._fileName = file.getAbsolutePath();
    }

    @Override
    public InputSource resolveEntity(String string, String string2) throws SAXException, IOException {
        return new InputSource(this._fileName);
    }
}


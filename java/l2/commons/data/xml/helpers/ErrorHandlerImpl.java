/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.data.xml.helpers;

import l2.commons.data.xml.AbstractParser;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ErrorHandlerImpl
implements ErrorHandler {
    private AbstractParser<?> qd;

    public ErrorHandlerImpl(AbstractParser<?> abstractParser) {
        this.qd = abstractParser;
    }

    @Override
    public void warning(SAXParseException sAXParseException) throws SAXException {
        this.qd.warn("File: " + this.qd.getCurrentFileName() + ":" + sAXParseException.getLineNumber() + " warning: " + sAXParseException.getMessage());
    }

    @Override
    public void error(SAXParseException sAXParseException) throws SAXException {
        this.qd.error("File: " + this.qd.getCurrentFileName() + ":" + sAXParseException.getLineNumber() + " error: " + sAXParseException.getMessage());
    }

    @Override
    public void fatalError(SAXParseException sAXParseException) throws SAXException {
        this.qd.error("File: " + this.qd.getCurrentFileName() + ":" + sAXParseException.getLineNumber() + " fatal: " + sAXParseException.getMessage());
    }
}


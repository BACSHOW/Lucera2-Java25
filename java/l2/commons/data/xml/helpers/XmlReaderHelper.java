package l2.commons.data.xml.helpers;

import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Factory de SAXReader seguro para GraalVM Native Image (AOT).
 * Blinda contra NullPointerExceptions de DTD e Namespace/QName no DOM4J.
 */
public final class XmlReaderHelper {
    private XmlReaderHelper() {
    }

    public static SAXReader createNonValidatingReader() {
        SAXReader reader = new SafeSAXReader();
        reader.setValidation(false);
        reader.setIncludeInternalDTDDeclarations(false);
        reader.setIncludeExternalDTDDeclarations(false);
        try {
            reader.setFeature("http://xml.org/sax/features/namespaces", true);
            reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception ignored) {
        }
        return reader;
    }

    private static final class SafeSAXReader extends SAXReader {
        SafeSAXReader() {
            super(false);
        }

        @Override
        protected SAXContentHandler createContentHandler(XMLReader xmlReader) {
            return new SAXContentHandler(this.getDocumentFactory()) {
                @Override
                public void startDTD(String name, String publicId, String systemId) throws SAXException {
                    // No-op seguro em GraalVM AOT
                }

                @Override
                public void endDTD() throws SAXException {
                    // No-op
                }

                @Override
                public void startElement(String namespaceURI, String localName, String qName, Attributes attributes) throws SAXException {
                    String safeQName = (qName != null && !qName.isEmpty()) ? qName : ((localName != null && !localName.isEmpty()) ? localName : "node");
                    String safeLocal = (localName != null && !localName.isEmpty()) ? localName : safeQName;
                    String safeUri = namespaceURI != null ? namespaceURI : "";
                    super.startElement(safeUri, safeLocal, safeQName, attributes);
                }
            };
        }
    }
}

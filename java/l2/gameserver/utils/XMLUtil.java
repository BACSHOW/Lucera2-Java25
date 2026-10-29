/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class XMLUtil {
    private static final Logger _log = LoggerFactory.getLogger(XMLUtil.class);

    public static String getAttributeValue(Node node, String string) {
        Node node2 = node.getAttributes().getNamedItem(string);
        if (node2 == null) {
            return "";
        }
        String string2 = node2.getNodeValue();
        if (string2 == null) {
            return "";
        }
        return string2;
    }

    public static boolean getAttributeBooleanValue(Node node, String string, boolean bl) {
        Node node2 = node.getAttributes().getNamedItem(string);
        if (node2 == null) {
            return bl;
        }
        String string2 = node2.getNodeValue();
        if (string2 == null) {
            return bl;
        }
        return Boolean.parseBoolean(string2);
    }

    public static int getAttributeIntValue(Node node, String string, int n) {
        Node node2 = node.getAttributes().getNamedItem(string);
        if (node2 == null) {
            return n;
        }
        String string2 = node2.getNodeValue();
        if (string2 == null) {
            return n;
        }
        return Integer.parseInt(string2);
    }

    public static long getAttributeLongValue(Node node, String string, long l) {
        Node node2 = node.getAttributes().getNamedItem(string);
        if (node2 == null) {
            return l;
        }
        String string2 = node2.getNodeValue();
        if (string2 == null) {
            return l;
        }
        return Long.parseLong(string2);
    }
}


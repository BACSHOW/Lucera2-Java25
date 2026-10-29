/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.data.xml;

import l2.commons.logging.LoggerObject;

public abstract class AbstractHolder
extends LoggerObject {
    public void log() {
        this.info(String.format("loaded %d%s(s) count.", this.size(), AbstractHolder.formatOut(this.getClass().getSimpleName().replace("Holder", "")).toLowerCase()));
    }

    protected void process() {
    }

    public abstract int size();

    public abstract void clear();

    private static String formatOut(String string) {
        char[] cArray = string.toCharArray();
        StringBuffer stringBuffer = new StringBuffer(cArray.length);
        for (char c : cArray) {
            if (Character.isUpperCase(c)) {
                stringBuffer.append(" ");
            }
            stringBuffer.append(Character.toLowerCase(c));
        }
        return stringBuffer.toString();
    }
}


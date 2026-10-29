/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.net.utils;

public class Net {
    private final int rr;
    private final int rs;

    public Net(int n, int n2) {
        this.rr = n;
        this.rs = n2;
    }

    public int address() {
        return this.rr;
    }

    public int netmask() {
        return this.rs;
    }

    public boolean isInRange(int n) {
        return (n & this.rs) == this.rr;
    }

    public boolean isInRange(String string) {
        return this.isInRange(Net.parseAddress(string));
    }

    public static Net valueOf(String string) {
        int n = 0;
        int n2 = 0;
        String[] stringArray = string.trim().split("\\b\\/\\b");
        if (stringArray.length < 1 || stringArray.length > 2) {
            throw new IllegalArgumentException("For input string: \"" + string + "\"");
        }
        if (stringArray.length == 1) {
            String[] stringArray2 = stringArray[0].split("\\.");
            if (stringArray2.length < 1 || stringArray2.length > 4) {
                throw new IllegalArgumentException("For input string: \"" + string + "\"");
            }
            for (int i = 1; i <= stringArray2.length; ++i) {
                if (stringArray2[i - 1].equals("*")) continue;
                n |= Integer.parseInt(stringArray2[i - 1]) << 32 - i * 8;
                n2 |= 255 << 32 - i * 8;
            }
        } else {
            n = Net.parseAddress(stringArray[0]);
            n2 = Net.parseNetmask(stringArray[1]);
        }
        return new Net(n, n2);
    }

    public static int parseAddress(String string) throws IllegalArgumentException {
        int n = 0;
        String[] stringArray = string.split("\\.");
        if (stringArray.length != 4) {
            throw new IllegalArgumentException("For input string: \"" + string + "\"");
        }
        for (int i = 1; i <= stringArray.length; ++i) {
            n |= Integer.parseInt(stringArray[i - 1]) << 32 - i * 8;
        }
        return n;
    }

    public static int parseNetmask(String string) throws IllegalArgumentException {
        int n = 0;
        String[] stringArray = string.split("\\.");
        if (stringArray.length == 1) {
            int n2 = Integer.parseInt(stringArray[0]);
            if (n2 < 0 || n2 > 32) {
                throw new IllegalArgumentException("For input string: \"" + string + "\"");
            }
            n = -1 << 32 - n2;
        } else {
            for (int i = 1; i <= stringArray.length; ++i) {
                n |= Integer.parseInt(stringArray[i - 1]) << 32 - i * 8;
            }
        }
        return n;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object instanceof Net) {
            return ((Net)object).address() == this.rr && ((Net)object).netmask() == this.rs;
        }
        return false;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.rr >>> 24).append(".");
        stringBuilder.append(this.rr << 8 >>> 24).append(".");
        stringBuilder.append(this.rr << 16 >>> 24).append(".");
        stringBuilder.append(this.rr << 24 >>> 24);
        stringBuilder.append("/");
        stringBuilder.append(this.rs >>> 24).append(".");
        stringBuilder.append(this.rs << 8 >>> 24).append(".");
        stringBuilder.append(this.rs << 16 >>> 24).append(".");
        stringBuilder.append(this.rs << 24 >>> 24);
        return stringBuilder.toString();
    }
}


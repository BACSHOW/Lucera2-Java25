/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

public class SqlBatch {
    private String awZ;
    private String axa;
    private StringBuilder axb;
    private StringBuilder axc;
    private long axd = Long.MAX_VALUE;
    private boolean isEmpty = true;

    public SqlBatch(String string, String string2) {
        this.awZ = string + "\n";
        this.axa = string2 != null && string2.length() > 0 ? " " + string2 + ";\n" : ";\n";
        this.axb = new StringBuilder(this.awZ);
        this.axc = new StringBuilder();
    }

    public SqlBatch(String string) {
        this(string, null);
    }

    public void writeStructure(String string) {
        this.axc.append(string);
    }

    public void write(String string) {
        this.isEmpty = false;
        if ((long)(this.axb.length() + string.length()) < this.axd - (long)this.axa.length()) {
            this.axb.append(string + ",\n");
        } else {
            this.axb.append(string + this.axa);
            this.axc.append(this.axb.toString());
            this.axb = new StringBuilder(this.awZ);
        }
    }

    public void writeBuffer() {
        String string = this.axb.toString();
        if (string.length() > 0) {
            this.axc.append(string.substring(0, string.length() - 2) + this.axa);
        }
        this.axb = new StringBuilder(this.awZ);
    }

    public String close() {
        if (this.axb.length() > this.awZ.length()) {
            this.writeBuffer();
        }
        return this.axc.toString();
    }

    public void setLimit(long l) {
        this.axd = l;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.telnet;

import org.apache.commons.lang3.ArrayUtils;

public abstract class TelnetCommand
implements Comparable<TelnetCommand> {
    private final String anW;
    private final String[] anX;

    public TelnetCommand(String string) {
        this(string, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public TelnetCommand(String string, String ... stringArray) {
        this.anW = string;
        this.anX = stringArray;
    }

    public String getCommand() {
        return this.anW;
    }

    public String[] getAcronyms() {
        return this.anX;
    }

    public abstract String getUsage();

    public abstract String handle(String[] var1);

    public boolean equals(String string) {
        for (String string2 : this.anX) {
            if (!string.equals(string2)) continue;
            return true;
        }
        return this.anW.equalsIgnoreCase(string);
    }

    public String toString() {
        return this.anW;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return true;
        }
        if (object instanceof TelnetCommand) {
            return this.anW.equals(((TelnetCommand)object).anW);
        }
        return false;
    }

    @Override
    public int compareTo(TelnetCommand telnetCommand) {
        return this.anW.compareTo(telnetCommand.anW);
    }
}


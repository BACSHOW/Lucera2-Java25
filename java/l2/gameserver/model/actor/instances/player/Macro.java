/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.instances.player;

public class Macro {
    public static final int CMD_TYPE_SKILL = 1;
    public static final int CMD_TYPE_ACTION = 3;
    public static final int CMD_TYPE_SHORTCUT = 4;
    public int id;
    public final int icon;
    public final String name;
    public final String descr;
    public final String acronym;
    public final L2MacroCmd[] commands;

    public Macro(int n, int n2, String string, String string2, String string3, L2MacroCmd[] l2MacroCmdArray) {
        this.id = n;
        this.icon = n2;
        this.name = string;
        this.descr = string2;
        this.acronym = string3.length() > 4 ? string3.substring(0, 4) : string3;
        this.commands = l2MacroCmdArray;
    }

    public String toString() {
        return "macro id=" + this.id + " icon=" + this.icon + "name=" + this.name + " descr=" + this.descr + " acronym=" + this.acronym + " commands=" + this.commands;
    }

    public static class L2MacroCmd {
        public final int entry;
        public final int type;
        public final int d1;
        public final int d2;
        public final String cmd;

        public L2MacroCmd(int n, int n2, int n3, int n4, String string) {
            this.entry = n;
            this.type = n2;
            this.d1 = n3;
            this.d2 = n4;
            this.cmd = string;
        }
    }
}


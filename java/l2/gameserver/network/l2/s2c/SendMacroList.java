/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.actor.instances.player.Macro;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SendMacroList
extends L2GameServerPacket {
    private final int amd;
    private final int _count;
    private final Macro acf;

    public SendMacroList(int n, int n2, Macro macro) {
        this.amd = n;
        this._count = n2;
        this.acf = macro;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(231);
        this.writeD(this.amd);
        this.writeC(0);
        this.writeC(this._count);
        this.writeC(this.acf != null ? 1 : 0);
        if (this.acf != null) {
            this.writeD(this.acf.id);
            this.writeS(this.acf.name);
            this.writeS(this.acf.descr);
            this.writeS(this.acf.acronym);
            this.writeC(this.acf.icon);
            this.writeC(this.acf.commands.length);
            for (int i = 0; i < this.acf.commands.length; ++i) {
                Macro.L2MacroCmd l2MacroCmd = this.acf.commands[i];
                this.writeC(i + 1);
                this.writeC(l2MacroCmd.type);
                this.writeD(l2MacroCmd.d1);
                this.writeC(l2MacroCmd.d2);
                this.writeS(l2MacroCmd.cmd);
            }
        }
    }
}


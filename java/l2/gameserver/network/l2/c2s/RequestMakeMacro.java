/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.Macro;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;

public class RequestMakeMacro
extends L2GameClientPacket {
    private Macro acf;

    @Override
    protected void readImpl() {
        int n = this.readD();
        String string = this.readS(32);
        String string2 = this.readS(64);
        String string3 = this.readS(4);
        int n2 = this.readC();
        int n3 = this.readC();
        if (n3 > 12) {
            n3 = 12;
        }
        Macro.L2MacroCmd[] l2MacroCmdArray = new Macro.L2MacroCmd[n3];
        for (int i = 0; i < n3; ++i) {
            int n4 = this.readC();
            int n5 = this.readC();
            int n6 = this.readD();
            int n7 = this.readC();
            String string4 = this.readS().replace(";", "").replace(",", "");
            l2MacroCmdArray[i] = new Macro.L2MacroCmd(n4, n5, n6, n7, string4);
        }
        this.acf = new Macro(n, n2, string, string2, string3, l2MacroCmdArray);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getMacroses().getAllMacroses().length > 48) {
            player.sendPacket((IStaticPacket)Msg.YOU_MAY_CREATE_UP_TO_48_MACROS);
            return;
        }
        if (this.acf.name.length() == 0) {
            player.sendPacket((IStaticPacket)Msg.ENTER_THE_NAME_OF_THE_MACRO);
            return;
        }
        if (this.acf.descr.length() > 32) {
            player.sendPacket((IStaticPacket)Msg.MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS);
            return;
        }
        player.registerMacro(this.acf);
    }
}


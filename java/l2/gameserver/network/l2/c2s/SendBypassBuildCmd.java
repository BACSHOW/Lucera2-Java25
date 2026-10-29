/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class SendBypassBuildCmd
extends L2GameClientPacket {
    private String adf;

    @Override
    protected void readImpl() {
        this.adf = this.readS();
        if (this.adf != null) {
            this.adf = this.adf.trim();
        }
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        String string = this.adf;
        if (!string.contains("admin_")) {
            string = "admin_" + string;
        }
        AdminCommandHandler.getInstance().useAdminCommandHandler(player, string);
    }
}


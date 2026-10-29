/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.RecipeBookItemList;

public class RequestRecipeBookOpen
extends L2GameClientPacket {
    private boolean acE;

    @Override
    protected void readImpl() {
        if (this._buf.hasRemaining()) {
            this.acE = this.readD() == 0;
        }
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        this.sendPacket((L2GameServerPacket)new RecipeBookItemList(player, this.acE));
    }
}


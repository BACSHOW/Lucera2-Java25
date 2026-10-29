/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestWriteHeroWords
extends L2GameClientPacket {
    private String ada;

    @Override
    protected void readImpl() {
        this.ada = this.readS();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || !player.isHero()) {
            return;
        }
        if (this.ada == null || this.ada.length() > 300) {
            return;
        }
        HeroController.getInstance().setHeroMessage(player.getObjectId(), this.ada);
    }
}


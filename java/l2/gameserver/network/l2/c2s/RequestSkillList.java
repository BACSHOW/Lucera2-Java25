/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SkillList;

public final class RequestSkillList
extends L2GameClientPacket {
    private static final String acT = "[C] 50 RequestSkillList";

    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player != null) {
            player.sendPacket((IStaticPacket)new SkillList(player));
        }
    }

    @Override
    public String getType() {
        return acT;
    }
}


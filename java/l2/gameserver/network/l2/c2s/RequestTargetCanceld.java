/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestTargetCanceld
extends L2GameClientPacket {
    private int acV;

    @Override
    protected void readImpl() {
        this.acV = this.readH();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (this.acV == 0) {
            if (player.isCastingNow()) {
                Skill skill = player.getCastingSkill();
                player.abortCast(skill != null && (skill.isHandler() || skill.getHitTime() > 1000), false);
            } else if (player.getTarget() != null) {
                player.setTarget(null);
            }
        } else if (player.getTarget() != null) {
            player.setTarget(null);
        }
    }
}


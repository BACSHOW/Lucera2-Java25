/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class MagicSkillCanceled
extends L2GameServerPacket {
    private int ajD;
    private final int ajE;
    private final int ajF;

    public MagicSkillCanceled(Creature creature) {
        this.ajD = creature.getObjectId();
        this.ajE = creature.getX();
        this.ajF = creature.getY();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(73);
        this.writeD(this.ajD);
    }

    @Override
    public L2GameServerPacket packet(Player player) {
        if (player != null && !player.isInObserverMode()) {
            if (player.getBuffAnimRange() < 0) {
                return null;
            }
            if (player.getBuffAnimRange() == 0) {
                return this.ajD == player.getObjectId() ? super.packet(player) : null;
            }
            int n = player.getBuffAnimRange();
            return player.getXYDeltaSq(this.ajE, this.ajF) < (long)(n * n) ? super.packet(player) : null;
        }
        return super.packet(player);
    }
}


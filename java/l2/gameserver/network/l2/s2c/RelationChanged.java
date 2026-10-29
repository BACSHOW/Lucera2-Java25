/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class RelationChanged
extends L2GameServerPacket {
    public static final int RELATION_PARTY1 = 1;
    public static final int RELATION_PARTY2 = 2;
    public static final int RELATION_PARTY3 = 4;
    public static final int RELATION_PARTY4 = 8;
    public static final int RELATION_PARTYLEADER = 16;
    public static final int RELATION_HAS_PARTY = 32;
    public static final int RELATION_CLAN_MEMBER = 64;
    public static final int RELATION_LEADER = 128;
    public static final int RELATION_INSIEGE = 512;
    public static final int RELATION_ATTACKER = 1024;
    public static final int RELATION_ALLY = 2048;
    public static final int RELATION_ENEMY = 4096;
    public static final int RELATION_MUTUAL_WAR = 32768;
    public static final int RELATION_1SIDED_WAR = 65536;
    private final int agM;
    private final boolean alU;
    private final int alV;
    private final int De;
    private final int _pvpFlag;

    protected RelationChanged(Playable playable, boolean bl, int n) {
        this.alU = bl;
        this.alV = n;
        this.agM = playable.getObjectId();
        this.De = playable.getKarma();
        this._pvpFlag = playable.getPvpFlag();
    }

    @Override
    protected void writeImpl() {
        this.writeC(206);
        this.writeD(this.agM);
        this.writeD(this.alV);
        this.writeD(this.alU);
        this.writeD(this.De);
        this.writeD(this._pvpFlag);
    }

    public static L2GameServerPacket create(Player player, Playable playable, Player player2) {
        if (player == null || playable == null || player2 == null) {
            return null;
        }
        Player player3 = playable.getPlayer();
        int n = player3 == null ? 0 : player3.getRelation(player2);
        return new RelationChanged(playable, playable.isAutoAttackable(player2), n);
    }
}


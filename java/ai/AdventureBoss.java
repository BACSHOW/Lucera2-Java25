/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.utils.NpcUtils;

public class AdventureBoss
extends Fighter {
    public AdventureBoss(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        Clan clan = creature.getClan();
        if (clan != null && clan.getCastle() > 0) {
            if (clan.getCastle() == 1) {
                NpcUtils.spawnSingle(32074, -14336, 121184, -2992, 16500, 3600000L);
            }
            if (clan.getCastle() == 2) {
                NpcUtils.spawnSingle(32082, 17344, 147360, -3120, 53000, 3600000L);
            }
            if (clan.getCastle() == 3) {
                NpcUtils.spawnSingle(32083, 85744, 147488, -3408, 33000, 3600000L);
            }
            if (clan.getCastle() == 4) {
                NpcUtils.spawnSingle(32084, 81152, 53168, -1558, 16500, 3600000L);
            }
            if (clan.getCastle() == 5) {
                NpcUtils.spawnSingle(32085, 147664, 20144, -2012, 16500, 3600000L);
            }
            if (clan.getCastle() == 6) {
                NpcUtils.spawnSingle(32086, 115741, 219435, -3664, 32000, 3600000L);
            }
            if (clan.getCastle() == 7) {
                NpcUtils.spawnSingle(32088, 150528, -57696, -2979, 25000, 3600000L);
            }
            if (clan.getCastle() == 8) {
                NpcUtils.spawnSingle(32089, 40560, -52342, -837, 5500, 3600000L);
            }
            if (clan.getCastle() == 9) {
                NpcUtils.spawnSingle(32087, 84596, -141092, -1542, 57000, 3600000L);
            }
        }
        super.onEvtDead(creature);
    }
}


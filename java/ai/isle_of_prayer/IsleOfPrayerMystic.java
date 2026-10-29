/*
 * Decompiled with CFR 0.152.
 */
package ai.isle_of_prayer;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Mystic;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;

public class IsleOfPrayerMystic
extends Mystic {
    private boolean eb = true;
    private static final int[] ec = new int[]{18364, 18365, 18366};
    private static final int ed = 9593;
    private static final int ee = 9594;
    private static final int dV = 9596;

    public IsleOfPrayerMystic(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        Party party;
        NpcInstance npcInstance = this.getActor();
        if (this.eb && creature.isPlayable() && creature.getPlayer() != null && (party = creature.getPlayer().getParty()) != null && party.getMemberCount() > 2) {
            this.eb = false;
            for (int i = 0; i < 2; ++i) {
                try {
                    MonsterInstance monsterInstance = new MonsterInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(ec[Rnd.get(ec.length)]));
                    monsterInstance.setSpawnedLoc(((MonsterInstance)npcInstance).getMinionPosition());
                    monsterInstance.setReflection(npcInstance.getReflection());
                    monsterInstance.setCurrentHpMp(monsterInstance.getMaxHp(), monsterInstance.getMaxMp(), true);
                    monsterInstance.spawnMe(monsterInstance.getSpawnedLoc());
                    monsterInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, Rnd.get(1, 100));
                    continue;
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
        super.onEvtAttacked(creature, n);
    }

    @Override
    protected void onEvtDead(Creature creature) {
        Player player;
        this.eb = true;
        if (creature != null && (player = creature.getPlayer()) != null) {
            NpcInstance npcInstance = this.getActor();
            switch (npcInstance.getNpcId()) {
                case 22261: {
                    if (!Rnd.chance(12)) break;
                    npcInstance.dropItem(player, 9594, 1L);
                    break;
                }
                case 22265: {
                    if (!Rnd.chance(6)) break;
                    npcInstance.dropItem(player, 9596, 1L);
                    break;
                }
                case 22260: {
                    if (!Rnd.chance(23)) break;
                    npcInstance.dropItem(player, 9593, 1L);
                    break;
                }
                case 22262: {
                    if (!Rnd.chance(12)) break;
                    npcInstance.dropItem(player, 9594, 1L);
                    break;
                }
                case 22264: {
                    if (!Rnd.chance(12)) break;
                    npcInstance.dropItem(player, 9594, 1L);
                    break;
                }
                case 22266: {
                    if (!Rnd.chance(5)) break;
                    npcInstance.dropItem(player, 9596, 1L);
                    break;
                }
                case 22257: {
                    if (!Rnd.chance(21)) break;
                    npcInstance.dropItem(player, 9593, 1L);
                    break;
                }
                case 22258: {
                    if (!Rnd.chance(22)) break;
                    npcInstance.dropItem(player, 9593, 1L);
                }
            }
        }
        super.onEvtDead(creature);
    }
}


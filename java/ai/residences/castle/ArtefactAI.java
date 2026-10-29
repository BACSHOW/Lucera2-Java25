/*
 * Decompiled with CFR 0.152.
 */
package ai.residences.castle;

import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.NpcInstance;

public class ArtefactAI
extends CharacterAI {
    public ArtefactAI(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
        NpcInstance npcInstance;
        Player player;
        if (creature == null || (player = creature.getPlayer()) == null || (npcInstance = (NpcInstance)this.getActor()) == null) {
            return;
        }
        SiegeEvent siegeEvent = npcInstance.getEvent(SiegeEvent.class);
        SiegeEvent siegeEvent2 = player.getEvent(SiegeEvent.class);
        Object s = siegeEvent.getSiegeClan("attackers", player.getClan());
        if (siegeEvent2 == null || siegeEvent == siegeEvent2 && s != null) {
            ThreadPoolManager.getInstance().schedule(new notifyGuard(player), 1000L);
        }
    }

    class notifyGuard
    extends RunnableImpl {
        private HardReference<Player> e;

        public notifyGuard(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() throws Exception {
            NpcInstance npcInstance;
            Player player = this.e.get();
            if (player == null || (npcInstance = (NpcInstance)ArtefactAI.this.getActor()) == null) {
                return;
            }
            for (NpcInstance npcInstance2 : npcInstance.getAroundNpc(1500, 200)) {
                if (!npcInstance2.isSiegeGuard() || !Rnd.chance(20)) continue;
                npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 5000);
            }
            if (player.getCastingSkill() != null && player.getCastingSkill().getTargetType() == Skill.SkillTargetType.TARGET_HOLY) {
                ThreadPoolManager.getInstance().schedule(this, 10000L);
            }
        }
    }
}


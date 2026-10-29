/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;

public class FrostBuffalo
extends Fighter {
    private boolean cb = true;
    private static final int cc = 22093;
    private static final int cd = 4;

    public FrostBuffalo(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtSeeSpell(Skill skill, Creature creature) {
        NpcInstance npcInstance = this.getActor();
        if (skill.isMagic()) {
            return;
        }
        if (this.cb) {
            this.cb = false;
            for (int i = 0; i < 4; ++i) {
                try {
                    SimpleSpawner simpleSpawner = new SimpleSpawner(NpcHolder.getInstance().getTemplate(22093));
                    simpleSpawner.setLoc(Location.findPointToStay(npcInstance, 100, 120));
                    NpcInstance npcInstance2 = simpleSpawner.doSpawn(true);
                    if (creature.isPet() || creature.isSummon()) {
                        npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature, Rnd.get(2, 100));
                    }
                    npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, creature.getPlayer(), Rnd.get(1, 100));
                    continue;
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.cb = true;
        super.onEvtDead(creature);
    }

    @Override
    protected boolean randomWalk() {
        return this.cb;
    }
}


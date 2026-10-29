/*
 * Decompiled with CFR 0.152.
 */
package ai.isle_of_prayer;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;

public class Shade
extends Fighter {
    private long bf = 0L;
    private boolean dm = false;
    private static final int ch = 300000;
    private static final int ef = 9595;

    public Shade(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance.isDead()) {
            return true;
        }
        if (this._def_think) {
            this.doTask();
            this.dm = false;
            return true;
        }
        if (!this.dm) {
            this.dm = true;
            this.bf = System.currentTimeMillis() + 300000L;
        }
        if (this.bf != 0L && this.dm && this.bf < System.currentTimeMillis()) {
            npcInstance.deleteMe();
            return true;
        }
        return super.thinkActive();
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected void onEvtDead(Creature creature) {
        Player player;
        if (creature != null && (player = creature.getPlayer()) != null) {
            NpcInstance npcInstance = this.getActor();
            if (Rnd.chance(10)) {
                npcInstance.dropItem(player, 9595, 1L);
            }
        }
        super.onEvtDead(creature);
    }
}


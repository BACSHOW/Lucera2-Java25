/*
 * Decompiled with CFR 0.152.
 */
package ai.primeval_isle;

import l2.gameserver.ai.DefaultAI;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.tables.SkillTable;

public class SprigantStun
extends DefaultAI {
    private final Skill SKILL = SkillTable.getInstance().getInfo(5085, 1);
    private long el;
    private static final int em = 15000;

    public SprigantStun(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        if (System.currentTimeMillis() > this.el) {
            NpcInstance npcInstance = this.getActor();
            npcInstance.doCast(this.SKILL, npcInstance, false);
            this.el = System.currentTimeMillis() + 15000L;
            return true;
        }
        return false;
    }
}


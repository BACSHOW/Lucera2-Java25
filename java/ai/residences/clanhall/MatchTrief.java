/*
 * Decompiled with CFR 0.152.
 */
package ai.residences.clanhall;

import ai.residences.clanhall.MatchFighter;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2.gameserver.tables.SkillTable;

public class MatchTrief
extends MatchFighter {
    public static final Skill HOLD = SkillTable.getInstance().getInfo(4047, 6);

    public MatchTrief(NpcInstance npcInstance) {
        super(npcInstance);
    }

    public void hold() {
        CTBBossInstance cTBBossInstance = this.getActor();
        this.addTaskCast(cTBBossInstance, HOLD);
        this.doTask();
    }
}


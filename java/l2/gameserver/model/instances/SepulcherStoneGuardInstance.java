/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.SepulcherMonsterInstance;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public class SepulcherStoneGuardInstance
extends SepulcherMonsterInstance {
    public SepulcherStoneGuardInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        Skill skill = SkillTable.getInstance().getInfo(4616, 1);
        skill.getEffects(this, this, false, false);
    }
}


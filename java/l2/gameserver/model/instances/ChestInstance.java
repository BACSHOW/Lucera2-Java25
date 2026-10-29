/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class ChestInstance
extends MonsterInstance {
    public ChestInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    public void tryOpen(Player player, Skill skill) {
        this.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 100);
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}


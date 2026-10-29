/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.Config;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class SpecialMonsterInstance
extends MonsterInstance {
    public SpecialMonsterInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public boolean canChampion() {
        return Config.ALT_CHAMPION_CAN_BE_SPECIAL_MONSTERS;
    }
}


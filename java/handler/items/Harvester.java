/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.SimpleItemHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.tables.SkillTable;

public class Harvester
extends SimpleItemHandler {
    private static final int[] lS = new int[]{5125};

    @Override
    public int[] getItemIds() {
        return lS;
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance itemInstance, boolean bl) {
        GameObject gameObject = player.getTarget();
        if (gameObject == null || !gameObject.isMonster()) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }
        MonsterInstance monsterInstance = (MonsterInstance)player.getTarget();
        if (!monsterInstance.isDead()) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }
        Skill skill = SkillTable.getInstance().getInfo(2098, 1);
        if (skill != null && skill.checkCondition(player, monsterInstance, false, false, true)) {
            player.getAI().Cast(skill, monsterInstance);
            return true;
        }
        return false;
    }
}


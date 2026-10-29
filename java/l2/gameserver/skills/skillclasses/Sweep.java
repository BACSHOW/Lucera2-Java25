/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.reward.RewardItem;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.ItemFunctions;

public class Sweep
extends Skill {
    public Sweep(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        if (this.isNotTargetAoE()) {
            return super.checkCondition(creature, creature2, bl, bl2, bl3);
        }
        if (creature2 == null) {
            return false;
        }
        if (!creature2.isMonster() || !creature2.isDead()) {
            creature.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return false;
        }
        if (!((MonsterInstance)creature2).isSpoiled()) {
            creature.sendPacket((IStaticPacket)Msg.SWEEPER_FAILED_TARGET_NOT_SPOILED);
            return false;
        }
        if (!((MonsterInstance)creature2).isSpoiled((Player)creature)) {
            creature.sendPacket((IStaticPacket)Msg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (!creature.isPlayer()) {
            return;
        }
        Player player = (Player)creature;
        for (Creature creature2 : list) {
            if (creature2 == null || !creature2.isMonster() || !creature2.isDead() || !((MonsterInstance)creature2).isSpoiled()) continue;
            MonsterInstance monsterInstance = (MonsterInstance)creature2;
            if (!monsterInstance.isSpoiled(player)) {
                creature.sendPacket((IStaticPacket)Msg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
                continue;
            }
            List<RewardItem> list2 = monsterInstance.takeSweep();
            if (list2 == null) {
                creature.getAI().setAttackTarget(null);
                monsterInstance.endDecayTask();
                continue;
            }
            for (RewardItem rewardItem : list2) {
                SystemMessage systemMessage;
                ItemInstance itemInstance = ItemFunctions.createItem(rewardItem.itemId);
                itemInstance.setCount(rewardItem.count);
                if (player.isInParty() && player.getParty().isDistributeSpoilLoot()) {
                    player.getParty().distributeItem(player, itemInstance, null);
                    continue;
                }
                if (!player.getInventory().validateCapacity(itemInstance) || !player.getInventory().validateWeight(itemInstance)) {
                    itemInstance.dropToTheGround(player, monsterInstance);
                    continue;
                }
                player.getInventory().addItem(itemInstance);
                if (rewardItem.count == 1L) {
                    systemMessage = new SystemMessage(30);
                    systemMessage.addItemName(rewardItem.itemId);
                    player.sendPacket((IStaticPacket)systemMessage);
                } else {
                    systemMessage = new SystemMessage(29);
                    systemMessage.addItemName(rewardItem.itemId);
                    systemMessage.addNumber(rewardItem.count);
                    player.sendPacket((IStaticPacket)systemMessage);
                }
                if (!player.isInParty()) continue;
                if (rewardItem.count == 1L) {
                    systemMessage = new SystemMessage(609);
                    systemMessage.addName(player);
                    systemMessage.addItemName(rewardItem.itemId);
                    player.getParty().broadcastToPartyMembers(player, systemMessage);
                    continue;
                }
                systemMessage = new SystemMessage(608);
                systemMessage.addName(player);
                systemMessage.addItemName(rewardItem.itemId);
                systemMessage.addNumber(rewardItem.count);
                player.getParty().broadcastToPartyMembers(player, systemMessage);
            }
            creature.getAI().setAttackTarget(null);
            monsterInstance.endDecayTask();
        }
    }
}


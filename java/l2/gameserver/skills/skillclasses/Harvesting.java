/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.reward.RewardItem;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.ItemFunctions;

public class Harvesting
extends Skill {
    public Harvesting(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (!creature.isPlayer()) {
            return;
        }
        Player player = (Player)creature;
        for (Creature creature2 : list) {
            if (creature2 == null || !creature2.isMonster()) continue;
            MonsterInstance monsterInstance = (MonsterInstance)creature2;
            if (!monsterInstance.isSeeded()) {
                creature.sendPacket((IStaticPacket)Msg.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
                continue;
            }
            if (!monsterInstance.isSeeded(player)) {
                creature.sendPacket((IStaticPacket)Msg.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
                continue;
            }
            double d = Config.MANOR_HARVESTING_BASIC_SUCCESS;
            int n = Math.abs(creature.getLevel() - monsterInstance.getLevel());
            if (n > Config.MANOR_DIFF_PLAYER_TARGET) {
                d -= (double)(n - Config.MANOR_DIFF_PLAYER_TARGET) * Config.MANOR_DIFF_PLAYER_TARGET_PENALTY;
            }
            if (d < 1.0) {
                d = 1.0;
            }
            if (player.isGM()) {
                player.sendMessage(new CustomMessage("l2p.gameserver.skills.skillclasses.Harvesting.Chance", player, new Object[0]).addNumber((long)d));
            }
            if (!Rnd.chance(d)) {
                creature.sendPacket((IStaticPacket)Msg.THE_HARVEST_HAS_FAILED);
                monsterInstance.clearHarvest();
                continue;
            }
            RewardItem rewardItem = monsterInstance.takeHarvest();
            if (rewardItem == null) continue;
            if (!player.getInventory().validateCapacity(rewardItem.itemId, rewardItem.count) || !player.getInventory().validateWeight(rewardItem.itemId, rewardItem.count)) {
                ItemInstance itemInstance = ItemFunctions.createItem(rewardItem.itemId);
                itemInstance.setCount(rewardItem.count);
                itemInstance.dropToTheGround(player, monsterInstance);
                continue;
            }
            player.getInventory().addItem(rewardItem.itemId, (long)((double)rewardItem.count * Config.MANOR_HARVESTING_REWARD_RATE));
            player.sendPacket((IStaticPacket)new SystemMessage(1137).addName(player).addNumber((long)((double)rewardItem.count * Config.MANOR_HARVESTING_REWARD_RATE)).addItemName(rewardItem.itemId));
            if (!player.isInParty()) continue;
            SystemMessage systemMessage = new SystemMessage(1137).addString(player.getName()).addNumber((long)((double)rewardItem.count * Config.MANOR_HARVESTING_REWARD_RATE)).addItemName(rewardItem.itemId);
            player.getParty().broadcastToPartyMembers(player, systemMessage);
        }
    }
}


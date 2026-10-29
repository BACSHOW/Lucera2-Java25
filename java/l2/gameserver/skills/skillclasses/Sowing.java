/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Manor;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.StatsSet;

public class Sowing
extends Skill {
    public Sowing(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (!creature.isPlayer()) {
            return;
        }
        Player player = (Player)creature;
        int n = player.getUseSeed();
        boolean bl = ItemHolder.getInstance().getTemplate(n).isAltSeed();
        if (!player.getInventory().destroyItemByItemId(n, 1L)) {
            creature.sendActionFailed();
            return;
        }
        player.sendPacket((IStaticPacket)SystemMessage2.removeItems(n, 1L));
        for (Creature creature2 : list) {
            MonsterInstance monsterInstance;
            if (creature2 == null || (monsterInstance = (MonsterInstance)creature2).isSeeded()) continue;
            double d = Config.MANOR_SOWING_BASIC_SUCCESS;
            double d2 = Math.abs(creature.getLevel() - creature2.getLevel());
            double d3 = Math.abs(Manor.getInstance().getSeedLevel(n) - creature2.getLevel());
            if (d2 > (double)Config.MANOR_DIFF_PLAYER_TARGET) {
                d -= (d2 - (double)Config.MANOR_DIFF_PLAYER_TARGET) * Config.MANOR_DIFF_PLAYER_TARGET_PENALTY;
            }
            if (d3 > (double)Config.MANOR_DIFF_SEED_TARGET) {
                d -= (d3 - (double)Config.MANOR_DIFF_SEED_TARGET) * Config.MANOR_DIFF_SEED_TARGET_PENALTY;
            }
            if (bl) {
                d *= Config.MANOR_SOWING_ALT_BASIC_SUCCESS / Config.MANOR_SOWING_BASIC_SUCCESS;
            }
            if (d < 1.0) {
                d = 1.0;
            }
            if (player.isGM()) {
                creature.sendMessage(new CustomMessage("l2p.gameserver.skills.skillclasses.Sowing.Chance", player, new Object[0]).addNumber((long)d));
            }
            if (Rnd.chance(d) && monsterInstance.setSeeded(player, n, bl)) {
                creature.sendPacket((IStaticPacket)Msg.THE_SEED_WAS_SUCCESSFULLY_SOWN);
                continue;
            }
            creature.sendPacket((IStaticPacket)Msg.THE_SEED_WAS_NOT_SOWN);
        }
    }
}


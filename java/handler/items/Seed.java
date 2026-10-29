/*
 * Decompiled with CFR 0.152.
 */
package handler.items;

import handler.items.ScriptItemHandler;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.model.Manor;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.ChestInstance;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.RaidBossInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.mapregion.DomainArea;

public class Seed
extends ScriptItemHandler {
    private static int[] iv = new int[0];

    public Seed() {
        iv = new int[Manor.getInstance().getAllSeeds().size()];
        int n = 0;
        for (Integer n2 : Manor.getInstance().getAllSeeds().keySet()) {
            Seed.iv[n++] = n2.shortValue();
        }
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance itemInstance, boolean bl) {
        int n;
        if (playable == null || !playable.isPlayer()) {
            return false;
        }
        Player player = (Player)playable;
        if (playable.getTarget() == null) {
            player.sendActionFailed();
            return false;
        }
        if (!player.getTarget().isMonster() || player.getTarget() instanceof RaidBossInstance || player.getTarget() instanceof MinionInstance && ((MinionInstance)player.getTarget()).getLeader() instanceof RaidBossInstance || player.getTarget() instanceof ChestInstance || ((MonsterInstance)playable.getTarget()).getChampion() > 0 && !itemInstance.isAltSeed()) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
            return false;
        }
        MonsterInstance monsterInstance = (MonsterInstance)playable.getTarget();
        if (monsterInstance == null) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return false;
        }
        if (monsterInstance.isDead()) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return false;
        }
        if (monsterInstance.isSeeded()) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_SEED_HAS_BEEN_SOWN);
            return false;
        }
        int n2 = itemInstance.getItemId();
        if (n2 == 0 || player.getInventory().getItemByItemId(itemInstance.getItemId()) == null) {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            return false;
        }
        DomainArea domainArea = MapRegionManager.getInstance().getRegionData(DomainArea.class, player);
        int n3 = n = domainArea == null ? 0 : domainArea.getId();
        if (Manor.getInstance().getCastleIdForSeed(n2) != n) {
            player.sendPacket((IStaticPacket)SystemMsg.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
            return false;
        }
        Skill skill = SkillTable.getInstance().getInfo(2097, 1);
        if (skill == null) {
            player.sendActionFailed();
            return false;
        }
        if (skill.checkCondition(player, monsterInstance, false, false, true)) {
            player.setUseSeed(n2);
            player.getAI().Cast(skill, monsterInstance);
        }
        return true;
    }

    @Override
    public final int[] getItemIds() {
        return iv;
    }
}


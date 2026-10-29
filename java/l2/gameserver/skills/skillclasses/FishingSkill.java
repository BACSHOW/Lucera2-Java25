/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.commons.collections.LazyArrayList;
import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Fishing;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.tables.FishTable;
import l2.gameserver.templates.FishTemplate;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;

public class FishingSkill
extends Skill {
    public FishingSkill(StatsSet statsSet) {
        super(statsSet);
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        Player player = (Player)creature;
        if (player.getSkillLevel(1315) == -1) {
            return false;
        }
        if (player.isFishing()) {
            player.stopFishing();
            player.sendPacket((IStaticPacket)Msg.CANCELS_FISHING);
            return false;
        }
        if (player.isInBoat()) {
            creature.sendPacket((IStaticPacket)Msg.YOU_CANT_FISH_WHILE_YOU_ARE_ON_BOARD);
            return false;
        }
        if (player.getPrivateStoreType() != 0) {
            creature.sendPacket((IStaticPacket)Msg.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_MANUFACTURE_OR_PRIVATE_STORE);
            return false;
        }
        Zone zone = player.getZone(Zone.ZoneType.FISHING);
        if (zone == null) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANT_FISH_HERE);
            return false;
        }
        if (player.isInWater()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANT_FISH_HERE);
            return false;
        }
        WeaponTemplate weaponTemplate = player.getActiveWeaponItem();
        if (weaponTemplate == null || weaponTemplate.getItemType() != WeaponTemplate.WeaponType.ROD) {
            player.sendPacket((IStaticPacket)Msg.FISHING_POLES_ARE_NOT_INSTALLED);
            return false;
        }
        ItemInstance itemInstance = player.getInventory().getPaperdollItem(8);
        if (itemInstance == null || itemInstance.getCount() < 1L) {
            player.sendPacket((IStaticPacket)Msg.BAITS_ARE_NOT_PUT_ON_A_HOOK);
            return false;
        }
        int n = Rnd.get(50) + 150;
        double d = PositionUtils.convertHeadingToDegree(player.getHeading());
        double d2 = Math.toRadians(d - 90.0);
        double d3 = Math.sin(d2);
        double d4 = Math.cos(d2);
        int n2 = -((int)(d3 * (double)n));
        int n3 = (int)(d4 * (double)n);
        int n4 = player.getX() + n2;
        int n5 = player.getY() + n3;
        int n6 = GeoEngine.getHeight(n4, n5, player.getZ(), player.getGeoIndex()) + 1;
        boolean bl4 = zone.getParams().getInteger("fishing_place_type") == 2;
        LazyArrayList<Zone> lazyArrayList = LazyArrayList.newInstance();
        World.getZones(lazyArrayList, new Location(n4, n5, n6), player.getReflection());
        for (Zone zone2 : lazyArrayList) {
            if (!zone2.isType(Zone.ZoneType.water)) continue;
            n6 = zone2.getTerritory().getZmax();
            bl4 = true;
            break;
        }
        LazyArrayList.recycle(lazyArrayList);
        if (!bl4) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANT_FISH_HERE);
            return false;
        }
        player.getFishing().setFishLoc(new Location(n4, n5, n6));
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (creature == null || !creature.isPlayer()) {
            return;
        }
        Player player = (Player)creature;
        ItemInstance itemInstance = player.getInventory().getPaperdollItem(8);
        if (itemInstance == null || itemInstance.getCount() < 1L) {
            player.sendPacket((IStaticPacket)Msg.BAITS_ARE_NOT_PUT_ON_A_HOOK);
            return;
        }
        Zone zone = player.getZone(Zone.ZoneType.FISHING);
        if (zone == null) {
            return;
        }
        int n = zone.getParams().getInteger("distribution_id");
        int n2 = itemInstance.getItemId();
        int n3 = Fishing.getRandomFishLvl(player);
        int n4 = Fishing.getFishGroup(n2);
        int n5 = Fishing.getRandomFishType(n2, n3, n);
        List<FishTemplate> list2 = FishTable.getInstance().getFish(n4, n5, n3);
        if (list2 == null || list2.size() == 0) {
            player.sendPacket((IStaticPacket)Msg.SYSTEM_ERROR);
            return;
        }
        if (!player.getInventory().destroyItemByObjectId(player.getInventory().getPaperdollObjectId(8), 1L)) {
            player.sendPacket((IStaticPacket)Msg.NOT_ENOUGH_BAIT);
            return;
        }
        int n6 = Rnd.get(list2.size());
        FishTemplate fishTemplate = list2.get(n6);
        player.startFishing(fishTemplate, n2);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.templates.StatsSet;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.StringUtils;

public class Recall
extends Skill {
    private final int apq;
    private final boolean apr;
    private final boolean aps;
    private final Location cH;
    private final List<Location> apt;

    public Recall(StatsSet statsSet) {
        super(statsSet);
        this.apq = statsSet.getInteger("townId", 0);
        this.apr = statsSet.getBool("clanhall", false);
        this.aps = statsSet.getBool("castle", false);
        String string = statsSet.getString("random_loc", null);
        String[] stringArray = statsSet.getString("loc", "").split(";");
        this.cH = stringArray.length == 3 ? new Location(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[2])) : null;
        if (string == null) {
            this.apt = Collections.emptyList();
        } else {
            ArrayList<Location> arrayList = new ArrayList<Location>();
            for (String string2 : StringUtils.split(string, " ")) {
                arrayList.add(Location.parseLoc(string2));
            }
            this.apt = arrayList;
        }
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        Player player;
        if (this.getHitTime() == 200) {
            player = creature.getPlayer();
            if (this.apr) {
                if (player.getClan() == null || player.getClan().getHasHideout() == 0 || player.getReflection() == ReflectionManager.JAIL) {
                    creature.sendPacket((IStaticPacket)new SystemMessage(113).addItemName(this._itemConsumeId[0]));
                    return false;
                }
            } else if (this.aps && (player.getClan() == null || player.getClan().getCastle() == 0 || player.getReflection() == ReflectionManager.JAIL)) {
                creature.sendPacket((IStaticPacket)new SystemMessage(113).addItemName(this._itemConsumeId[0]));
                return false;
            }
        }
        if (creature.isPlayer()) {
            player = (Player)creature;
            if (player.getActiveWeaponFlagAttachment() != null) {
                creature.sendPacket((IStaticPacket)SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
                return false;
            }
            if (player.isInDuel() || player.getTeam() != TeamType.NONE || this.ae(player)) {
                creature.sendMessage(new CustomMessage("common.RecallInDuel", player, new Object[0]));
                return false;
            }
            if (player.isOlyParticipant()) {
                creature.sendPacket((IStaticPacket)Msg.THIS_SKILL_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
                return false;
            }
        }
        if (creature.isInZone(Zone.ZoneType.no_escape) || this.apq > 0 && creature.getReflection() != null && creature.getReflection().getCoreLoc() != null || creature.getReflection() == ReflectionManager.JAIL) {
            if (creature.isPlayer()) {
                creature.sendMessage(new CustomMessage("l2p.gameserver.skills.skillclasses.Recall.Here", (Player)creature, new Object[0]));
            }
            return false;
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        for (Creature creature2 : list) {
            Player player;
            if (creature2 == null || (player = creature2.getPlayer()) == null || !player.getPlayerAccess().UseTeleport) continue;
            if (player.getActiveWeaponFlagAttachment() != null) {
                creature.sendPacket((IStaticPacket)Msg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
                continue;
            }
            if (player.isFestivalParticipant()) {
                creature.sendMessage(new CustomMessage("l2p.gameserver.skills.skillclasses.Recall.Festival", (Player)creature, new Object[0]));
                continue;
            }
            if (player.isOlyParticipant()) {
                creature.sendPacket((IStaticPacket)Msg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD);
                return;
            }
            if (player.isInObserverMode()) {
                creature.sendPacket((IStaticPacket)new SystemMessage(113).addSkillName(this.getId(), this.getLevel()));
                return;
            }
            if (player.isInDuel() || player.getTeam() != TeamType.NONE) {
                creature.sendMessage(new CustomMessage("common.RecallInDuel", (Player)creature, new Object[0]));
                return;
            }
            if (this._isItemHandler) {
                if (this._itemConsumeId[0] == 7125) {
                    player.teleToLocation(17144, 170156, -3502, 0);
                    return;
                }
                if (this._itemConsumeId[0] == 7127) {
                    player.teleToLocation(105918, 109759, -3207, 0);
                    return;
                }
                if (this._itemConsumeId[0] == 7130) {
                    player.teleToLocation(85475, 16087, -3672, 0);
                    return;
                }
                if (this._itemConsumeId[0] == 7618) {
                    player.teleToLocation(149864, -81062, -5618, 0);
                    return;
                }
                if (this._itemConsumeId[0] == 7619) {
                    player.teleToLocation(108275, -53785, -2524, 0);
                    return;
                }
            }
            if (this.cH != null) {
                player.teleToLocation(this.cH);
                return;
            }
            if (!this.apt.isEmpty()) {
                Location location = this.apt.get(Rnd.get(this.apt.size()));
                player.teleToLocation(location.x, location.y, location.z, 0);
                return;
            }
            switch (this.apq) {
                case 1: {
                    player.teleToLocation(-83990, 243336, -3700, 0);
                    return;
                }
                case 2: {
                    player.teleToLocation(45576, 49412, -2950, 0);
                    return;
                }
                case 3: {
                    player.teleToLocation(12501, 16768, -4500, 0);
                    return;
                }
                case 4: {
                    player.teleToLocation(-44884, -115063, -80, 0);
                    return;
                }
                case 5: {
                    player.teleToLocation(115790, -179146, -890, 0);
                    return;
                }
                case 6: {
                    player.teleToLocation(-14279, 124446, -3000, 0);
                    return;
                }
                case 7: {
                    player.teleToLocation(-82909, 150357, -3000, 0);
                    return;
                }
                case 8: {
                    player.teleToLocation(19025, 145245, -3107, 0);
                    return;
                }
                case 9: {
                    player.teleToLocation(82272, 147801, -3350, 0);
                    return;
                }
                case 10: {
                    player.teleToLocation(82323, 55466, -1480, 0);
                    return;
                }
                case 11: {
                    player.teleToLocation(144526, 24661, -2100, 0);
                    return;
                }
                case 12: {
                    player.teleToLocation(117189, 78952, -2210, 0);
                    return;
                }
                case 13: {
                    player.teleToLocation(110768, 219824, -3624, 0);
                    return;
                }
                case 14: {
                    player.teleToLocation(43536, -50416, -800, 0);
                    return;
                }
                case 15: {
                    player.teleToLocation(148288, -58304, -2979, 0);
                    return;
                }
                case 16: {
                    player.teleToLocation(87776, -140384, -1536, 0);
                    return;
                }
                case 17: {
                    player.teleToLocation(10568, -24600, -3648, 0);
                    return;
                }
                case 18: {
                    player.teleToLocation(19025, 145245, -3107, 0);
                    return;
                }
            }
            if (this.aps) {
                player.teleToCastle();
                return;
            }
            if (this.apr) {
                player.teleToClanhall();
                return;
            }
            player.teleToClosestTown();
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }

    private boolean ae(Player player) {
        return (Boolean)Scripts.getInstance().callScripts(player, "events.TvT2.PvPEvent", "isEventPartisipant") != false;
    }
}


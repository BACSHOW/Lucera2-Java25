/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.skillclasses;

import java.util.List;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.templates.StatsSet;

public class Call
extends Skill {
    private final boolean aoZ;
    private final int apa;

    public Call(StatsSet statsSet) {
        super(statsSet);
        this.aoZ = statsSet.getBool("party", false);
        this.apa = statsSet.getInteger("requestWithCrystal", -1);
    }

    @Override
    public boolean checkCondition(Creature creature, Creature creature2, boolean bl, boolean bl2, boolean bl3) {
        if (creature.isPlayer()) {
            if (this.aoZ && ((Player)creature).getParty() == null) {
                return false;
            }
            SystemMessage systemMessage = Call.canSummonHere((Player)creature);
            if (systemMessage != null) {
                creature.sendPacket((IStaticPacket)systemMessage);
                return false;
            }
            if (!this.aoZ) {
                if (creature == creature2) {
                    return false;
                }
                systemMessage = Call.canBeSummoned(creature2);
                if (systemMessage != null) {
                    creature.sendPacket((IStaticPacket)systemMessage);
                    return false;
                }
            }
        }
        return super.checkCondition(creature, creature2, bl, bl2, bl3);
    }

    @Override
    public void useSkill(Creature creature, List<Creature> list) {
        if (!creature.isPlayer()) {
            return;
        }
        SystemMessage systemMessage = Call.canSummonHere((Player)creature);
        if (systemMessage != null) {
            creature.sendPacket((IStaticPacket)systemMessage);
            return;
        }
        if (this.aoZ) {
            if (((Player)creature).getParty() != null) {
                for (Player player : ((Player)creature).getParty().getPartyMembers()) {
                    if (player.equals(creature) || Call.canBeSummoned(player) != null) continue;
                    if (this.apa >= 0) {
                        player.summonCharacterRequest(creature, creature.getLoc(), this.apa);
                    } else {
                        player.stopMove();
                        player.teleToLocation(creature.getLoc(), creature.getGeoIndex());
                    }
                    this.getEffects(creature, player, this.getActivateRate() > 0, false);
                }
            }
            if (this.isSSPossible()) {
                creature.unChargeShots(this.isMagic());
            }
            return;
        }
        for (Creature creature2 : list) {
            if (creature2 == null || Call.canBeSummoned(creature2) != null) continue;
            if (this.apa >= 0) {
                ((Player)creature2).summonCharacterRequest(creature, creature.getLoc(), this.apa);
            } else {
                creature2.stopMove();
                creature2.teleToLocation(creature.getLoc(), creature.getGeoIndex());
            }
            this.getEffects(creature, creature2, this.getActivateRate() > 0, false);
        }
        if (this.isSSPossible()) {
            creature.unChargeShots(this.isMagic());
        }
    }

    public static SystemMessage canSummonHere(Player player) {
        if (player.isAlikeDead() || player.isOlyParticipant() || player.isInObserverMode() || player.isFlying() || player.isFestivalParticipant()) {
            return Msg.NOTHING_HAPPENED;
        }
        if (player.isInZoneBattle() || player.isInZone(Zone.ZoneType.SIEGE) || player.isInZone(Zone.ZoneType.no_restart) || player.isInZone(Zone.ZoneType.no_summon) || player.isInBoat() || player.getReflection() != ReflectionManager.DEFAULT || player.isInZone(Zone.ZoneType.fun)) {
            return Msg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION;
        }
        if (player.isInStoreMode() || player.isProcessingRequest()) {
            return Msg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS;
        }
        return null;
    }

    public static SystemMessage canBeSummoned(Creature creature) {
        if (creature == null || !creature.isPlayer() || creature.isFlying() || creature.isInObserverMode() || creature.getPlayer().isFestivalParticipant() || !creature.getPlayer().getPlayerAccess().UseTeleport) {
            return Msg.INVALID_TARGET;
        }
        if (creature.isOlyParticipant()) {
            return Msg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD;
        }
        if (creature.isInZoneBattle() || creature.isInZone(Zone.ZoneType.SIEGE) || creature.isInZone(Zone.ZoneType.no_restart) || creature.isInZone(Zone.ZoneType.no_summon) || creature.getReflection() != ReflectionManager.DEFAULT || creature.isInBoat() || creature.isInZone(Zone.ZoneType.fun)) {
            return Msg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING;
        }
        if (creature.isAlikeDead()) {
            return new SystemMessage(1844).addString(creature.getName());
        }
        if (creature.getPvpFlag() != 0 || creature.isInCombat()) {
            return new SystemMessage(1843).addString(creature.getName());
        }
        Player player = (Player)creature;
        if (player.getPrivateStoreType() != 0 || player.isProcessingRequest()) {
            return new SystemMessage(1898).addString(creature.getName());
        }
        return null;
    }
}


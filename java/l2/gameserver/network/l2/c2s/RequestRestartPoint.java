/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.Die;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.TeleportUtils;
import org.apache.commons.lang3.tuple.Pair;

public class RequestRestartPoint
extends L2GameClientPacket {
    private RestartType acH;

    @Override
    protected void readImpl() {
        this.acH = ArrayUtils.valid(RestartType.VALUES, this.readD());
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (this.acH == null || player == null) {
            return;
        }
        if (player.isFakeDeath()) {
            player.breakFakeDeath();
            return;
        }
        if (!player.isDead() && !player.isGM()) {
            player.sendActionFailed();
            return;
        }
        if (player.isFestivalParticipant()) {
            player.doRevive();
            return;
        }
        if (player.isResurectProhibited()) {
            player.sendActionFailed();
            return;
        }
        switch (this.acH) {
            case FIXED: {
                if (player.getPlayerAccess().ResurectFixed) {
                    player.doRevive(100.0);
                    break;
                }
                if (!(!Config.SERVICE_FEATHER_REVIVE_ENABLE || Config.SERVICE_DISABLE_FEATHER_ON_SIEGES_AND_EPIC && (player.isOnSiegeField() || player.isInZone(Zone.ZoneType.epic)) || player.getInventory().isLockedItem(Config.SERVICE_FEATHER_ITEM_ID) || ItemFunctions.removeItem(player, Config.SERVICE_FEATHER_ITEM_ID, 1L, true) != 1L)) {
                    player.sendMessage(new CustomMessage("YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT", player, new Object[0]));
                    player.doRevive(100.0);
                    break;
                }
                if (Config.ALT_REVIVE_WINDOW_TO_TOWN) {
                    player.setPendingRevive(true);
                    player.teleToLocation(Config.ALT_REVIVE_WINDOW_TO_TOWN_LOCATION, ReflectionManager.DEFAULT);
                    break;
                }
                player.sendPacket(ActionFail.STATIC, new Die(player));
                break;
            }
            default: {
                Location location = null;
                Reflection reflection = player.getReflection();
                if (reflection == ReflectionManager.DEFAULT) {
                    for (GlobalEvent globalEvent : player.getEvents()) {
                        location = globalEvent.getRestartLoc(player, this.acH);
                    }
                }
                if (location == null) {
                    location = RequestRestartPoint.defaultLoc(this.acH, player);
                }
                if (location != null) {
                    Pair<Integer, OnAnswerListener> pair = player.getAskListener(false);
                    if (pair != null && pair.getValue() instanceof ReviveAnswerListener && !((ReviveAnswerListener)pair.getValue()).isForPet()) {
                        player.getAskListener(true);
                    }
                    player.setPendingRevive(true);
                    player.teleToLocation(location, ReflectionManager.DEFAULT);
                    break;
                }
                player.sendPacket(ActionFail.STATIC, new Die(player));
            }
        }
    }

    public static Location defaultLoc(RestartType restartType, Player player) {
        Location location = null;
        Clan clan = player.getClan();
        switch (restartType) {
            case TO_CLANHALL: {
                if (clan == null || clan.getHasHideout() == 0) break;
                ClanHall clanHall = player.getClanHall();
                location = TeleportUtils.getRestartLocation(player, RestartType.TO_CLANHALL);
                if (clanHall.getFunction(5) == null) break;
                player.restoreExp(clanHall.getFunction(5).getLevel());
                break;
            }
            case TO_CASTLE: {
                if (clan == null || clan.getCastle() == 0) break;
                Castle castle = player.getCastle();
                location = TeleportUtils.getRestartLocation(player, RestartType.TO_CASTLE);
                if (castle.getFunction(5) == null) break;
                player.restoreExp(castle.getFunction(5).getLevel());
                break;
            }
            default: {
                location = TeleportUtils.getRestartLocation(player, RestartType.TO_VILLAGE);
            }
        }
        return location;
    }
}


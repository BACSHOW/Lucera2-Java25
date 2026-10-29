/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.RestartType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.templates.mapregion.RestartArea;
import l2.gameserver.templates.mapregion.RestartPoint;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeleportUtils {
    private static final Logger _log = LoggerFactory.getLogger(TeleportUtils.class);
    public static final Location DEFAULT_RESTART = new Location(17817, 170079, -3530);

    private TeleportUtils() {
    }

    public static Location getRestartLocation(Player player, RestartType restartType) {
        return TeleportUtils.getRestartLocation(player, player.getLoc(), restartType);
    }

    public static Location getRestartLocation(Player player, Location location, RestartType restartType) {
        RestartArea restartArea;
        Clan clan;
        Reflection reflection = player.getReflection();
        if (reflection != ReflectionManager.DEFAULT) {
            if (reflection.getCoreLoc() != null) {
                return reflection.getCoreLoc();
            }
            if (reflection.getReturnLoc() != null) {
                return reflection.getReturnLoc();
            }
        }
        if ((clan = player.getClan()) != null) {
            if (restartType == RestartType.TO_CLANHALL && clan.getHasHideout() != 0) {
                return ((Residence)ResidenceHolder.getInstance().getResidence(clan.getHasHideout())).getOwnerRestartPoint();
            }
            if (restartType == RestartType.TO_CASTLE && clan.getCastle() != 0) {
                return ((Residence)ResidenceHolder.getInstance().getResidence(clan.getCastle())).getOwnerRestartPoint();
            }
        }
        if (player.getKarma() > 1) {
            if (player.getPKRestartPoint() != null) {
                return player.getPKRestartPoint();
            }
        } else if (player.getRestartPoint() != null) {
            return player.getRestartPoint();
        }
        if ((restartArea = MapRegionManager.getInstance().getRegionData(RestartArea.class, location)) != null) {
            RestartPoint restartPoint = restartArea.getRestartPoint().get((Object)player.getRace());
            Location location2 = Rnd.get(restartPoint.getRestartPoints());
            Location location3 = Rnd.get(restartPoint.getPKrestartPoints());
            return player.getKarma() > 1 ? location3 : location2;
        }
        _log.warn("Cannot find restart location from coordinates: " + location + "!");
        return DEFAULT_RESTART;
    }
}


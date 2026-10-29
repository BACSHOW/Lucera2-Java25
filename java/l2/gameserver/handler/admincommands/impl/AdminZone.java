/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.util.ArrayList;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.DropItem;
import l2.gameserver.templates.mapregion.DomainArea;

public class AdminZone
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (player == null || !player.getPlayerAccess().CanTeleport) {
            return false;
        }
        switch (commands) {
            case admin_zone_check: {
                player.sendMessage("Current region: " + player.getCurrentRegion());
                player.sendMessage("Zone list:");
                ArrayList<Zone> arrayList = new ArrayList<Zone>();
                World.getZones(arrayList, player.getLoc(), player.getReflection());
                for (Zone zone : arrayList) {
                    player.sendMessage(zone.getType().toString() + ", name: " + zone.getName() + ", state: " + (zone.isActive() ? "active" : "not active") + ", inside: " + zone.checkIfInZone(player) + "/" + zone.checkIfInZone(player.getX(), player.getY(), player.getZ()));
                }
                break;
            }
            case admin_region: {
                player.sendMessage("Current region: " + player.getCurrentRegion());
                player.sendMessage("Objects list:");
                for (GameObject gameObject : player.getCurrentRegion()) {
                    if (gameObject == null) continue;
                    player.sendMessage(gameObject.toString());
                }
                break;
            }
            case admin_vis_count: {
                player.sendMessage("Current region: " + player.getCurrentRegion());
                player.sendMessage("Players count: " + World.getAroundPlayers(player).size());
                break;
            }
            case admin_pos: {
                String string2 = player.getX() + ", " + player.getY() + ", " + player.getZ() + ", " + player.getHeading() + " Geo [" + (player.getX() - World.MAP_MIN_X >> 4) + ", " + (player.getY() - World.MAP_MIN_Y >> 4) + "] Ref " + player.getReflectionId();
                player.sendMessage("Pos: " + string2);
                player.sendPacket((IStaticPacket)new DropItem(0, 0xFFFFFF & Rnd.nextInt(), 57, player.getLoc().clone().setZ(player.getZ() + 64), false, 1));
                break;
            }
            case admin_domain: {
                Castle castle;
                DomainArea domainArea = MapRegionManager.getInstance().getRegionData(DomainArea.class, player);
                Castle castle2 = castle = domainArea != null ? ResidenceHolder.getInstance().getResidence(Castle.class, domainArea.getId()) : null;
                if (castle != null) {
                    player.sendMessage("Domain: " + castle.getName());
                    break;
                }
                player.sendMessage("Domain: Unknown");
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_zone_check,
        admin_region,
        admin_pos,
        admin_vis_count,
        admin_domain;

    }
}


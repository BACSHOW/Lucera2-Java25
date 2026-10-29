/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;

public class AdminGeodata
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanReload) {
            return false;
        }
        switch (commands) {
            case admin_geo_z: {
                if (stringArray.length > 1) {
                    player.sendMessage("GeoEngine: Geo_Z = " + GeoEngine.getHeight(player.getX(), player.getY(), Integer.parseInt(stringArray[1]), player.getReflectionId()) + " Loc_Z = " + player.getZ());
                    break;
                }
                player.sendMessage("GeoEngine: Geo_Z = " + GeoEngine.getHeight(player.getLoc(), player.getReflectionId()) + " Loc_Z = " + player.getZ());
                break;
            }
            case admin_geo_type: {
                short s = GeoEngine.getType(player.getX(), player.getY(), player.getReflectionId());
                player.sendMessage("GeoEngine: Geo_Type = " + s);
                break;
            }
            case admin_geo_nswe: {
                String string2 = "";
                byte by = GeoEngine.getNSWE(player.getX(), player.getY(), player.getZ(), player.getReflectionId());
                if ((by & 8) == 0) {
                    string2 = string2 + " N";
                }
                if ((by & 4) == 0) {
                    string2 = string2 + " S";
                }
                if ((by & 2) == 0) {
                    string2 = string2 + " W";
                }
                if ((by & 1) == 0) {
                    string2 = string2 + " E";
                }
                player.sendMessage("GeoEngine: Geo_NSWE -> " + by + "->" + string2);
                break;
            }
            case admin_geo_los: {
                if (player.getTarget() != null) {
                    if (GeoEngine.canSeeTarget(player, player.getTarget(), false)) {
                        player.sendMessage("GeoEngine: Can See Target");
                        break;
                    }
                    player.sendMessage("GeoEngine: Can't See Target");
                    break;
                }
                player.sendMessage("None Target!");
                break;
            }
            case admin_geo_move: {
                if (player.getTarget() != null) {
                    if (GeoEngine.canMoveToCoord(player.getX(), player.getY(), player.getZ(), player.getTarget().getX(), player.getTarget().getY(), player.getTarget().getZ(), player.getGeoIndex())) {
                        player.sendMessage("GeoEngine: Can move to target.");
                        break;
                    }
                    player.sendMessage("GeoEngine: Can't move to target.");
                    break;
                }
                player.sendMessage("None target!");
                break;
            }
            case admin_geo_trace: {
                if (stringArray.length < 2) {
                    player.sendMessage("Usage: //geo_trace on|off");
                    return false;
                }
                if (stringArray[1].equalsIgnoreCase("on")) {
                    player.setVar("trace", "1", -1L);
                    break;
                }
                if (stringArray[1].equalsIgnoreCase("off")) {
                    player.unsetVar("trace");
                    break;
                }
                player.sendMessage("Usage: //geo_trace on|off");
                break;
            }
            case admin_geo_map: {
                int n = (player.getX() - World.MAP_MIN_X >> 15) + Config.GEO_X_FIRST;
                int n2 = (player.getY() - World.MAP_MIN_Y >> 15) + Config.GEO_Y_FIRST;
                player.sendMessage("GeoMap: " + n + "_" + n2);
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_geo_z,
        admin_geo_type,
        admin_geo_nswe,
        admin_geo_los,
        admin_geo_move,
        admin_geo_trace,
        admin_geo_map;

    }
}


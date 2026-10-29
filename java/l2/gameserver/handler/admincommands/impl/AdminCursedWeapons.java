/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.CursedWeaponsManager;
import l2.gameserver.model.CursedWeapon;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.ItemFunctions;

public class AdminCursedWeapons
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().Menu) {
            return false;
        }
        Object object = null;
        switch (commands) {
            case admin_cw_remove: 
            case admin_cw_goto: 
            case admin_cw_add: 
            case admin_cw_drop: {
                if (stringArray.length < 2) {
                    player.sendMessage("You did not specify id");
                    return false;
                }
                for (CursedWeapon object2 : CursedWeaponsManager.getInstance().getCursedWeapons()) {
                    if (!object2.getName().toLowerCase().contains(stringArray[1].toLowerCase())) continue;
                    object = object2;
                }
                if (object != null) break;
                player.sendMessage("Unknown id");
                return false;
            }
        }
        switch (commands) {
            case admin_cw_info: {
                player.sendMessage("======= Cursed Weapons: =======");
                for (CursedWeapon cursedWeapon : CursedWeaponsManager.getInstance().getCursedWeapons()) {
                    player.sendMessage("> " + cursedWeapon.getName() + " (" + cursedWeapon.getItemId() + ")");
                    if (cursedWeapon.isActivated()) {
                        Player player2 = cursedWeapon.getPlayer();
                        player.sendMessage("  Player holding: " + player2.getName());
                        player.sendMessage("  Player karma: " + cursedWeapon.getPlayerKarma());
                        player.sendMessage("  Time Remaining: " + cursedWeapon.getTimeLeft() / 60000L + " min.");
                        player.sendMessage("  Kills : " + cursedWeapon.getNbKills());
                        continue;
                    }
                    if (cursedWeapon.isDropped()) {
                        player.sendMessage("  Lying on the ground.");
                        player.sendMessage("  Time Remaining: " + cursedWeapon.getTimeLeft() / 60000L + " min.");
                        player.sendMessage("  Kills : " + cursedWeapon.getNbKills());
                        continue;
                    }
                    player.sendMessage("  Don't exist in the world.");
                }
                break;
            }
            case admin_cw_reload: {
                player.sendMessage("Cursed weapons can't be reloaded.");
                break;
            }
            case admin_cw_remove: {
                if (object == null) {
                    return false;
                }
                CursedWeaponsManager.getInstance().endOfLife((CursedWeapon)object);
                break;
            }
            case admin_cw_goto: {
                if (object == null) {
                    return false;
                }
                player.teleToLocation(((CursedWeapon)object).getLoc());
                break;
            }
            case admin_cw_add: {
                if (object == null) {
                    return false;
                }
                if (((CursedWeapon)object).isActive()) {
                    player.sendMessage("This cursed weapon is already active.");
                    break;
                }
                Object object3 = player.getTarget();
                if (object3 == null || !((GameObject)object3).isPlayer() || ((Player)object3).isOlyParticipant()) break;
                Player player3 = (Player)object3;
                ItemInstance itemInstance = ItemFunctions.createItem(((CursedWeapon)object).getItemId());
                if (player3.isCursedWeaponEquipped()) {
                    CursedWeaponsManager.getInstance().dropPlayer(player3);
                }
                if (((CursedWeapon)object).getEndTime() > 0L && ((CursedWeapon)object).getTimeLeft() > 0L) {
                    CursedWeaponsManager.getInstance().endOfLife((CursedWeapon)object);
                }
                ((CursedWeapon)object).activate(player3, itemInstance);
                ((CursedWeapon)object).setLoc(player3.getLoc());
                CursedWeaponsManager.getInstance().saveData((CursedWeapon)object);
                CursedWeaponsManager.getInstance().announce(new SystemMessage(1816).addZoneName(player3.getLoc()).addItemName(((CursedWeapon)object).getItemId()));
                CursedWeaponsManager.getInstance().showUsageTime(player3, (CursedWeapon)object);
                break;
            }
            case admin_cw_drop: {
                if (object == null) {
                    return false;
                }
                if (((CursedWeapon)object).isActive()) {
                    player.sendMessage("This cursed weapon is already active.");
                    break;
                }
                Object object3 = player.getTarget();
                if (object3 == null || !((GameObject)object3).isPlayer() || ((Player)object3).isOlyParticipant()) break;
                Player player4 = (Player)object3;
                ((CursedWeapon)object).create(null, player4);
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_cw_info,
        admin_cw_remove,
        admin_cw_goto,
        admin_cw_reload,
        admin_cw_add,
        admin_cw_drop;

    }
}


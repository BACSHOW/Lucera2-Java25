/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.entity.oly.NoblesController;
import l2.gameserver.model.entity.oly.OlyController;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SkillList;

public class AdminOlympiad
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        switch (commands) {
            case admin_oly_save: {
                if (!Config.OLY_ENABLED) {
                    return false;
                }
                try {
                    OlyController.getInstance().save();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                player.sendMessage("olympaid data saved.");
                break;
            }
            case admin_add_oly_points: {
                int n;
                if (stringArray.length < 3) {
                    player.sendMessage("Command syntax: //add_oly_points <char_name> <point_to_add>");
                    player.sendMessage("This command can be applied only for online players.");
                    return false;
                }
                Player player2 = World.getPlayer(stringArray[1]);
                if (player2 == null) {
                    player.sendMessage("Character " + stringArray[1] + " not found in game.");
                    return false;
                }
                try {
                    n = Integer.parseInt(stringArray[2]);
                }
                catch (NumberFormatException numberFormatException) {
                    player.sendMessage("Please specify integer value for olympiad points.");
                    return false;
                }
                int n2 = NoblesController.getInstance().getPointsOf(player2.getObjectId());
                int n3 = n2 + n;
                NoblesController.getInstance().setPointsOf(player2.getObjectId(), n3);
                player.sendMessage("Added " + n + " points to character " + player2.getName());
                player.sendMessage("Old points: " + n2 + ", new points: " + n3);
                break;
            }
            case admin_oly_start: {
                Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_STARTED);
                break;
            }
            case admin_oly_stop: {
                Announcements.getInstance().announceToAll(Msg.THE_OLYMPIAD_GAME_HAS_ENDED);
                try {
                    OlyController.getInstance().save();
                }
                catch (Exception exception) {}
                break;
            }
            case admin_add_hero: {
                if (stringArray.length < 2) {
                    player.sendMessage("Command syntax: //add_hero <char_name>");
                    player.sendMessage("This command can be applied only for online players.");
                    return false;
                }
                Player player3 = World.getPlayer(stringArray[1]);
                if (player3 == null) {
                    player.sendMessage("Character " + stringArray[1] + " not found in game.");
                    return false;
                }
                if (player3.isHero()) {
                    player3.setHero(false);
                    player3.updatePledgeClass();
                    HeroController.removeSkills(player3, true);
                } else {
                    player3.setHero(true);
                    player3.updatePledgeClass();
                    HeroController.addSkills(player3, true);
                }
                player3.sendPacket((IStaticPacket)new SkillList(player3));
                player3.sendMessage("Admin has changed your hero status.");
                player3.broadcastUserInfo(true);
                break;
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_oly_save,
        admin_add_oly_points,
        admin_oly_start,
        admin_add_hero,
        admin_oly_stop;

    }
}


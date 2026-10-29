/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.PetitionManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import org.apache.commons.lang3.math.NumberUtils;

public class AdminPetition
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        if (!player.getPlayerAccess().CanEditChar) {
            return false;
        }
        int n = NumberUtils.toInt(stringArray.length > 1 ? stringArray[1] : "-1", -1);
        Commands commands = (Commands)enum_;
        switch (commands) {
            case admin_view_petitions: {
                PetitionManager.getInstance().sendPendingPetitionList(player);
                break;
            }
            case admin_view_petition: {
                PetitionManager.getInstance().viewPetition(player, n);
                break;
            }
            case admin_accept_petition: {
                if (n < 0) {
                    player.sendMessage("Usage: //accept_petition id");
                    return false;
                }
                if (PetitionManager.getInstance().isPlayerInConsultation(player)) {
                    player.sendPacket((IStaticPacket)new SystemMessage(390));
                    return true;
                }
                if (PetitionManager.getInstance().isPetitionInProcess(n)) {
                    player.sendPacket((IStaticPacket)new SystemMessage(407));
                    return true;
                }
                if (PetitionManager.getInstance().acceptPetition(player, n)) break;
                player.sendPacket((IStaticPacket)new SystemMessage(388));
                break;
            }
            case admin_reject_petition: {
                if (n < 0) {
                    player.sendMessage("Usage: //accept_petition id");
                    return false;
                }
                if (!PetitionManager.getInstance().rejectPetition(player, n)) {
                    player.sendPacket((IStaticPacket)new SystemMessage(393));
                }
                PetitionManager.getInstance().sendPendingPetitionList(player);
                break;
            }
            case admin_reset_petitions: {
                if (PetitionManager.getInstance().isPetitionInProcess()) {
                    player.sendPacket((IStaticPacket)new SystemMessage(407));
                    return false;
                }
                PetitionManager.getInstance().clearPendingPetitions();
                PetitionManager.getInstance().sendPendingPetitionList(player);
                break;
            }
            case admin_force_peti: {
                if (string.length() < 11) {
                    player.sendMessage("Usage: //force_peti text");
                    return false;
                }
                try {
                    GameObject gameObject = player.getTarget();
                    if (gameObject == null || !(gameObject instanceof Player)) {
                        player.sendPacket((IStaticPacket)new SystemMessage(109));
                        return false;
                    }
                    Player player2 = (Player)gameObject;
                    n = PetitionManager.getInstance().submitPetition(player2, string.substring(10), 9);
                    PetitionManager.getInstance().acceptPetition(player, n);
                    break;
                }
                catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
                    player.sendMessage("Usage: //force_peti text");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_view_petitions,
        admin_view_petition,
        admin_accept_petition,
        admin_reject_petition,
        admin_reset_petitions,
        admin_force_peti;

    }
}


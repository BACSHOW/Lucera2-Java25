package services;

import services.TeleporterInterface.TeleportLocationData2;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;

public class AdminReloadTeleports implements IAdminCommandHandler {
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        AdminReloadTeleports.Commands command = (AdminReloadTeleports.Commands)comm;
        switch (command) {
            case admin_reloadteleports: {
                TeleportLocationData2.getInstance().reload();
                // a janela de teleporte de quem esta online tambem recebe a lista nova
                int sent = TeleportZones.sendToAll();
                activeChar.sendMessage("Interface Teleport List Reloaded: " + TeleportLocationData2.getInstance().size()
                        + " zonas, enviadas a " + sent + " jogadores.");
                return true;
            }
            case admin_reloadmultisells:
                AllowedConfigs.init();
                activeChar.sendMessage("Allowed Multisells config loaded.");
                return true;
            default:
                return false;
        }
    }

    public Enum[] getAdminCommandEnum() {
        return AdminReloadTeleports.Commands.values();
    }

    private enum Commands {
        admin_reloadteleports,
        admin_reloadmultisells;
    }
}
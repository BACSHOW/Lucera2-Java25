/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;

public class AdminDisconnect
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanKick) {
            return false;
        }
        switch (commands) {
            case admin_disconnect: 
            case admin_kick: {
                Player player2;
                if (stringArray.length == 1) {
                    GameObject gameObject = player.getTarget();
                    if (gameObject == null) {
                        player.sendMessage("Select character or specify player name.");
                        break;
                    }
                    if (!gameObject.isPlayer()) {
                        player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                        break;
                    }
                    player2 = (Player)gameObject;
                } else {
                    player2 = World.getPlayer(stringArray[1]);
                    if (player2 == null) {
                        player.sendMessage("Character " + stringArray[1] + " not found in game.");
                        break;
                    }
                }
                if (player2.getObjectId() == player.getObjectId()) {
                    player.sendMessage("You can't logout your character.");
                    break;
                }
                player.sendMessage("Character " + player2.getName() + " disconnected from server.");
                if (player2.isInOfflineMode()) {
                    player2.setOfflineMode(false);
                    player2.kick();
                    return true;
                }
                player2.sendMessage(new CustomMessage("admincommandhandlers.AdminDisconnect.YoureKickedByGM", player2, new Object[0]));
                player2.sendPacket((IStaticPacket)Msg.YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_PLEASE_LOGIN_AGAIN);
                ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

                    @Override
                    public void runImpl() throws Exception {
                        player2.kick();
                    }
                }, 500L);
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_disconnect,
        admin_kick;

    }
}


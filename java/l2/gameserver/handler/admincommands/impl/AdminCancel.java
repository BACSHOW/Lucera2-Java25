/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.IStaticPacket;

public class AdminCancel
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanEditChar) {
            return false;
        }
        switch (commands) {
            case admin_cancel: {
                this.b(player, stringArray.length > 1 ? stringArray[1] : null);
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void b(Player player, String string) {
        GameObject gameObject = player.getTarget();
        if (string != null) {
            Player player2 = World.getPlayer(string);
            if (player2 != null) {
                gameObject = player2;
            } else {
                try {
                    int n = Math.max(Integer.parseInt(string), 100);
                    for (Creature creature : player.getAroundCharacters(n, 200)) {
                        creature.getEffectList().stopAllEffects();
                    }
                    player.sendMessage("Apply Cancel within " + n + " unit radius.");
                    return;
                }
                catch (NumberFormatException numberFormatException) {
                    player.sendMessage("Enter valid player name or radius");
                    return;
                }
            }
        }
        if (gameObject == null) {
            gameObject = player;
        }
        if (gameObject.isCreature()) {
            ((Creature)gameObject).getEffectList().stopAllEffects();
        } else {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
        }
    }

    private static enum Commands {
        admin_cancel;

    }
}


/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;

public class AdminRes
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().Res) {
            return false;
        }
        if (string.startsWith("admin_res ")) {
            this.e(player, stringArray[1]);
        }
        if (string.equals("admin_res")) {
            this.u(player);
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void u(Player player) {
        this.e(player, null);
    }

    private void e(Player player, String string) {
        GameObject gameObject = player.getTarget();
        if (string != null) {
            Player player2 = World.getPlayer(string);
            if (player2 != null) {
                gameObject = player2;
            } else {
                try {
                    int n = Math.max(Integer.parseInt(string), 100);
                    for (Creature creature : player.getAroundCharacters(n, n)) {
                        this.e(creature);
                    }
                    player.sendMessage("Resurrected within " + n + " unit radius.");
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
        if (gameObject instanceof Creature) {
            this.e((Creature)gameObject);
        } else {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
        }
    }

    private void e(Creature creature) {
        if (!creature.isDead()) {
            return;
        }
        if (creature.isPlayable()) {
            if (creature.isPlayer()) {
                ((Player)creature).doRevive(100.0);
            } else {
                ((Playable)creature).doRevive();
            }
        } else if (creature.isNpc()) {
            ((NpcInstance)creature).stopDecay();
        }
        creature.setCurrentHpMp(creature.getMaxHp(), creature.getMaxMp(), true);
        creature.setCurrentCp(creature.getMaxCp());
    }

    private static enum Commands {
        admin_res;

    }
}


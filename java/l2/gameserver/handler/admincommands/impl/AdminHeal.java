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

public class AdminHeal
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().Heal) {
            return false;
        }
        switch (commands) {
            case admin_heal: {
                if (stringArray.length == 1) {
                    this.u(player);
                    break;
                }
                this.e(player, stringArray[1]);
            }
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
        Creature creature;
        GameObject gameObject = player.getTarget();
        if (string != null) {
            creature = World.getPlayer(string);
            if (creature != null) {
                gameObject = creature;
            } else {
                int n = Math.max(Integer.parseInt(string), 100);
                for (Creature creature2 : player.getAroundCharacters(n, 200)) {
                    creature2.setCurrentHpMp(creature2.getMaxHp(), creature2.getMaxMp());
                    if (!creature2.isPlayer()) continue;
                    creature2.setCurrentCp(creature2.getMaxCp());
                }
                player.sendMessage("Healed within " + n + " unit radius.");
                return;
            }
        }
        if (gameObject == null) {
            gameObject = player;
        }
        if (gameObject instanceof Creature) {
            creature = (Creature)gameObject;
            creature.setCurrentHpMp(creature.getMaxHp(), creature.getMaxMp());
            if (creature.isPlayer()) {
                creature.setCurrentCp(creature.getMaxCp());
            }
        } else {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
        }
    }

    private static enum Commands {
        admin_heal;

    }
}


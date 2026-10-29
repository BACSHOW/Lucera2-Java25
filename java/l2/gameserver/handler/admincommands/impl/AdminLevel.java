/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.tables.PetDataTable;

public class AdminLevel
implements IAdminCommandHandler {
    private void a(Player player, GameObject gameObject, int n) {
        if (gameObject == null || !gameObject.isPlayer() && !gameObject.isPet()) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return;
        }
        if (n < 1 || n > Experience.getMaxLevel()) {
            player.sendMessage("You must specify level 1 - " + Experience.getMaxLevel());
            return;
        }
        if (gameObject.isPlayer()) {
            Long l = Experience.LEVEL[n] - ((Player)gameObject).getExp();
            ((Player)gameObject).addExpAndSp(l, 0L);
            return;
        }
        if (gameObject.isPet()) {
            Long l = PetDataTable.getInstance().getInfo(((PetInstance)gameObject).getNpcId(), n).getExp() - ((PetInstance)gameObject).getExp();
            ((PetInstance)gameObject).addExpAndSp(l, 0L);
        }
    }

    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanEditChar) {
            return false;
        }
        GameObject gameObject = player.getTarget();
        if (gameObject == null || !gameObject.isPlayer() && !gameObject.isPet()) {
            player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
            return false;
        }
        switch (commands) {
            case admin_add_level: 
            case admin_addLevel: {
                int n;
                if (stringArray.length < 2) {
                    player.sendMessage("USAGE: //addLevel level");
                    return false;
                }
                try {
                    n = Integer.parseInt(stringArray[1]);
                }
                catch (NumberFormatException numberFormatException) {
                    player.sendMessage("You must specify level");
                    return false;
                }
                this.a(player, gameObject, n + ((Creature)gameObject).getLevel());
                break;
            }
            case admin_set_level: 
            case admin_setLevel: {
                int n;
                if (stringArray.length < 2) {
                    player.sendMessage("USAGE: //setLevel level");
                    return false;
                }
                try {
                    n = Integer.parseInt(stringArray[1]);
                }
                catch (NumberFormatException numberFormatException) {
                    player.sendMessage("You must specify level");
                    return false;
                }
                this.a(player, gameObject, n);
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_add_level,
        admin_addLevel,
        admin_set_level,
        admin_setLevel;

    }
}


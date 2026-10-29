/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;

public class ManaRegen
extends Functions {
    private static final int bqG = 57;

    public void DoManaRegen() {
        Player player = this.getSelf();
        long l = (long)Math.floor((double)player.getMaxMp() - player.getCurrentMp());
        long l2 = l * (long)Config.SERVICES_GIRAN_HARBOR_MP_REG_PRICE;
        if (l2 <= 0L) {
            player.sendPacket((IStaticPacket)Msg.NOTHING_HAPPENED);
            return;
        }
        if (ManaRegen.getItemCount(player, 57) >= l2) {
            ManaRegen.removeItem(player, 57, l2);
            player.sendPacket((IStaticPacket)new SystemMessage(1068).addNumber(l));
            player.setCurrentMp(player.getMaxMp());
        } else {
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
        }
    }
}


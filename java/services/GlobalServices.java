/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.scripts.Functions;

public class GlobalServices
extends Functions {
    public static boolean makeCustomHero(Player player, long l) {
        if (player.isHero() || l <= 0L) {
            return false;
        }
        player.setCustomHero(true, l, Config.ALT_ALLOW_CUSTOM_HERO_SKILLS);
        player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
        player.broadcastUserInfo(true);
        return true;
    }
}


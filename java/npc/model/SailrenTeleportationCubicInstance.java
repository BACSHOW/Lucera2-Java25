/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.commons.util.Rnd;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class SailrenTeleportationCubicInstance
extends MerchantInstance {
    private static final Location[] cD = new Location[]{new Location(10610, -24035, -3676), new Location(10703, -24041, -3673), new Location(10769, -24107, -3672)};

    public SailrenTeleportationCubicInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!SailrenTeleportationCubicInstance.canBypassCheck(player, this)) {
            return;
        }
        player.teleToLocation(Location.findPointToStay(Rnd.get(cD), 100, player.getGeoIndex()));
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExChangeClientEffectInfo;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;

public class SirraInstance
extends NpcInstance {
    private static final int[] ayi = new int[]{140, 138, 141};
    private static final int[] ayj = new int[]{139, 144};

    public SirraInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        DoorInstance doorInstance;
        String string = null;
        string = ArrayUtils.contains(ayi, this.getReflection().getInstancedZoneId()) ? "default/32762.htm" : (ArrayUtils.contains(ayj, this.getReflection().getInstancedZoneId()) ? ((doorInstance = this.getReflection().getDoor(23140101)).isOpen() ? "default/32762_opened.htm" : "default/32762_closed.htm") : "default/32762.htm");
        return string;
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!SirraInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equalsIgnoreCase("teleport_in")) {
            for (NpcInstance npcInstance : this.getReflection().getNpcs()) {
                if (npcInstance.getNpcId() != 29179 && npcInstance.getNpcId() != 29180) continue;
                player.sendPacket((IStaticPacket)new ExChangeClientEffectInfo(2));
            }
            player.teleToLocation(new Location(114712, -113544, -11225));
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}


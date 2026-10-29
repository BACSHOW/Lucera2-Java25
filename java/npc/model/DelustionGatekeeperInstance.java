/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.DimensionalRiftManager;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.DelusionChamber;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.templates.npc.NpcTemplate;

public final class DelustionGatekeeperInstance
extends NpcInstance {
    public DelustionGatekeeperInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!DelustionGatekeeperInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.startsWith("enterDC")) {
            Party party;
            int n = Integer.parseInt(string.substring(8));
            int n2 = n - 120;
            Map<Integer, DimensionalRiftManager.DimensionalRiftRoom> map = DimensionalRiftManager.getInstance().getRooms(n2);
            if (map == null) {
                player.sendPacket((IStaticPacket)Msg.SYSTEM_ERROR);
                return;
            }
            Reflection reflection = player.getActiveReflection();
            if (reflection != null) {
                if (player.canReenterInstance(n)) {
                    player.teleToLocation(reflection.getTeleportLoc(), reflection);
                }
            } else if (player.canEnterInstance(n) && (party = player.getParty()) != null) {
                new DelusionChamber(party, n2, Rnd.get(1, map.size() - 1));
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}


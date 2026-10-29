/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.util.ArrayList;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.MapUtils;

public class Misc
extends Functions {
    private static final Location bXx = new Location(11416, -23432, -3640);

    public void doSummon() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null || !NpcInstance.canBypassCheck(player, player.getLastNpc())) {
            return;
        }
        Party party = player.getParty();
        ArrayList<Player> arrayList = new ArrayList<Player>();
        if (party != null) {
            for (Player player2 : party.getPartyMembers()) {
                if (!player2.isDead() || MapUtils.regionX(player2) != 20 || MapUtils.regionY(player2) != 17) continue;
                arrayList.add(player2);
            }
        }
        if (arrayList.isEmpty() || !player.reduceAdena(200000L, true)) {
            this.show("default/32104-fail.htm", player);
            return;
        }
        for (Player player2 : arrayList) {
            player2.teleToLocation(bXx);
        }
        this.show("default/32104-success.htm", player);
    }
}


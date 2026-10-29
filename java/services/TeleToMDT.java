/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.Location;

public class TeleToMDT
extends Functions {
    public void toMDT() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!NpcInstance.canBypassCheck(player, npcInstance)) {
            return;
        }
        player.setVar("backCoords", player.getLoc().toXYZString(), -1L);
        player.teleToLocation(12661, 181687, -3560);
    }

    public void fromMDT() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!NpcInstance.canBypassCheck(player, npcInstance)) {
            return;
        }
        String string = player.getVar("backCoords");
        if (string == null || string.equals("")) {
            this.teleOut();
            return;
        }
        player.teleToLocation(Location.parseLoc(string));
    }

    public void teleOut() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        player.teleToLocation(12902, 181011, -3563);
        TeleToMDT.show(player.isLangRus() ? "\u042f \u043d\u0435 \u0437\u043d\u0430\u044e, \u043a\u0430\u043a \u0412\u044b \u043f\u043e\u043f\u0430\u043b\u0438 \u0441\u044e\u0434\u0430, \u043d\u043e \u044f \u043c\u043e\u0433\u0443 \u0412\u0430\u0441 \u043e\u0442\u043f\u0440\u0430\u0432\u0438\u0442\u044c \u0437\u0430 \u043e\u0433\u0440\u0430\u0436\u0434\u0435\u043d\u0438\u0435." : "I don't know from where you came here, but I can teleport you the another border side.", player, npcInstance, new Object[0]);
    }
}


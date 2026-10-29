/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.ReflectionUtils;

public class PaganDoormans
extends Functions {
    private static final int bXD = 19160001;
    private static final int bXE = 19160011;
    private static final int bXF = 19160010;
    private static final int bQE = 8064;
    private static final int bQF = 8065;
    private static final int bQD = 8067;

    public void openMainDoor() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!NpcInstance.canBypassCheck(player, npcInstance)) {
            return;
        }
        long l = PaganDoormans.getItemCount(player, 8064);
        if (l == 0L && PaganDoormans.getItemCount(player, 8067) == 0L) {
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
            return;
        }
        if (l > 0L) {
            PaganDoormans.removeItem(player, 8064, l);
            PaganDoormans.addItem(player, 8065, 1L);
        }
        PaganDoormans.openDoor(19160001);
        PaganDoormans.show("default/32034-1.htm", player, npcInstance, new Object[0]);
    }

    public void openSecondDoor() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!NpcInstance.canBypassCheck(player, npcInstance)) {
            return;
        }
        if (PaganDoormans.getItemCount(player, 8067) == 0L) {
            PaganDoormans.show("default/32036-2.htm", player, npcInstance, new Object[0]);
            return;
        }
        PaganDoormans.openDoor(19160011);
        PaganDoormans.openDoor(19160010);
        PaganDoormans.show("default/32036-1.htm", player, npcInstance, new Object[0]);
    }

    public void pressSkull() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!NpcInstance.canBypassCheck(player, npcInstance)) {
            return;
        }
        PaganDoormans.openDoor(19160001);
        PaganDoormans.show("default/32035-1.htm", player, npcInstance, new Object[0]);
    }

    public void press2ndSkull() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!NpcInstance.canBypassCheck(player, npcInstance)) {
            return;
        }
        PaganDoormans.openDoor(19160011);
        PaganDoormans.openDoor(19160010);
        PaganDoormans.show("default/32037-1.htm", player, npcInstance, new Object[0]);
    }

    private static void openDoor(int n) {
        DoorInstance doorInstance = ReflectionUtils.getDoor(n);
        doorInstance.openMe();
    }
}


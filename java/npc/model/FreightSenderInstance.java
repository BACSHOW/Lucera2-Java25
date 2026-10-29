/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.PackageToList;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.WarehouseFunctions;

public class FreightSenderInstance
extends MerchantInstance {
    public FreightSenderInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!FreightSenderInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equalsIgnoreCase("deposit_items")) {
            player.sendPacket((IStaticPacket)new PackageToList(player));
        } else if (string.equalsIgnoreCase("withdraw_items")) {
            WarehouseFunctions.showFreightWindow(player);
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}


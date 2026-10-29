/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import services.Buffer;

public class NpcBufferInstance
extends MerchantInstance {
    public NpcBufferInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        if (n == 0) {
            Buffer.showPage(player, "npc-" + this.getNpcId(), this);
            return;
        }
        super.showChatWindow(player, n, objectArray);
    }
}


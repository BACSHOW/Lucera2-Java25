/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import services.pawnshop.PawnShop;

public class PawnShopInstance
extends NpcInstance {
    public PawnShopInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        if (n == 0 && Config.PAWNSHOP_ENABLED) {
            PawnShop.showStartPage(player, this);
            return;
        }
        super.showChatWindow(player, n, objectArray);
    }
}


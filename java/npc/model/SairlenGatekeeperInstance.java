/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import bosses.SailrenManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;

public final class SairlenGatekeeperInstance
extends NpcInstance {
    private static final int ayh = 8784;

    public SairlenGatekeeperInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!SairlenGatekeeperInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.startsWith("request_entrance")) {
            if (player.getLevel() < 75) {
                this.showChatWindow(player, "default/32109-3.htm", new Object[0]);
            } else if (ItemFunctions.getItemCount(player, 8784) > 0L) {
                switch (SailrenManager.canIntoSailrenLair(player)) {
                    case Fail_Awake: 
                    case Fail_InUse: {
                        this.showChatWindow(player, "default/32109-5.htm", new Object[0]);
                        break;
                    }
                    case Fail_NoParty: {
                        this.showChatWindow(player, "default/32109-1.htm", new Object[0]);
                        break;
                    }
                    case Fail_Reborn: {
                        this.showChatWindow(player, "default/32109-4.htm", new Object[0]);
                        break;
                    }
                    case Ok: {
                        if (ItemFunctions.removeItem(player, 8784, 1L, true) <= 0L) break;
                        SailrenManager.setSailrenSpawnTask();
                        SailrenManager.entryToSailrenLair(player);
                    }
                }
            } else {
                this.showChatWindow(player, "default/32109-2.htm", new Object[0]);
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}


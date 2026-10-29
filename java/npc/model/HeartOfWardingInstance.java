/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import bosses.AntharasManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public final class HeartOfWardingInstance
extends NpcInstance {
    public HeartOfWardingInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!HeartOfWardingInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equalsIgnoreCase("enter_lair")) {
            AntharasManager.enterTheLair(player);
            return;
        }
        super.onBypassFeedback(player, string);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ReflectionUtils;

public final class EventReflectionManagerInstance
extends NpcInstance {
    public EventReflectionManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!EventReflectionManagerInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.startsWith("event_instance")) {
            int n = Integer.parseInt(string.substring(15));
            Reflection reflection = player.getActiveReflection();
            if (reflection != null) {
                if (player.canReenterInstance(n)) {
                    player.teleToLocation(reflection.getTeleportLoc(), reflection);
                }
            } else if (player.canEnterInstance(n)) {
                ReflectionUtils.enterReflection(player, n);
            }
        } else if (string.startsWith("escape_event_instance")) {
            if (player.getParty() == null || !player.getParty().isLeader(player)) {
                this.showChatWindow(player, "not_party_leader.htm", new Object[0]);
                return;
            }
            player.getReflection().collapse();
        } else if (string.startsWith("return")) {
            Reflection reflection = player.getReflection();
            if (reflection.getReturnLoc() != null) {
                player.teleToLocation(reflection.getReturnLoc(), ReflectionManager.DEFAULT);
            } else {
                player.setReflection(ReflectionManager.DEFAULT);
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        super.showChatWindow(player, n, new Object[0]);
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = n2 == 0 ? "" + n : n + "-" + n2;
        return "events/instances/" + string + ".htm";
    }
}


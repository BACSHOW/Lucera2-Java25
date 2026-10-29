/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.GuardInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ReflectionUtils;

public class BorderOutpostDoormanInstance
extends GuardInstance {
    public BorderOutpostDoormanInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!BorderOutpostDoormanInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equals("openDoor")) {
            DoorInstance doorInstance = ReflectionUtils.getDoor(24170001);
            doorInstance.openMe();
        } else if (string.equals("closeDoor")) {
            DoorInstance doorInstance = ReflectionUtils.getDoor(24170001);
            doorInstance.closeMe();
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}


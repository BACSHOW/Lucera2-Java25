/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.SepulcherNpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class SepulcherTriggerBoxInstance
extends SepulcherNpcInstance {
    public SepulcherTriggerBoxInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        this.deleteMe();
    }
}


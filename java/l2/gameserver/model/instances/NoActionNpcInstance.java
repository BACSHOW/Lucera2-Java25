/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

@Deprecated
public class NoActionNpcInstance
extends NpcInstance {
    public NoActionNpcInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onAction(Player player, boolean bl) {
        player.sendActionFailed();
    }
}


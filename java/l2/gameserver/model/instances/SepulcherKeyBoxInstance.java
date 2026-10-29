/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.instancemanager.sepulchers.FourSepulchers;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.SepulcherTriggerBoxInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;

public class SepulcherKeyBoxInstance
extends SepulcherTriggerBoxInstance {
    private static final int VP = 7260;

    public SepulcherKeyBoxInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        Player player2 = player.getParty() != null ? player.getParty().getPartyLeader() : player;
        ItemFunctions.addItem((Playable)player2, 7260, 1L, true);
        this.deleteMe();
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        FourSepulchers.getInstance().log("Box spawned " + this);
    }
}


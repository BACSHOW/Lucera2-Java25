/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.instancemanager.sepulchers.model.SepulcherRoom;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.SepulcherNpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;

public class SepulcherDoormanInstance
extends SepulcherNpcInstance {
    private static final int VP = 7260;
    private SepulcherRoom VQ;

    public SepulcherDoormanInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!string.startsWith("open_gate")) {
            super.onBypassFeedback(player, string);
            return;
        }
        if (player.getInventory().getCountOf(7260) == 0L) {
            this.showHtmlFile(player, "Gatekeeper-no.htm");
            return;
        }
        if (ItemFunctions.removeItem(player, 7260, 1L, true) > 0L) {
            this.VQ.activate();
            Functions.npcSayCustomMessage((NpcInstance)this, "ROYAL_RUSH_502_MONSTERS_SPAWNED", new Object[0]);
        }
    }

    public void showHtmlFile(Player player, String string) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile("SepulcherNpc/" + string);
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = n2 == 0 ? String.valueOf(n) : n + "-" + n2;
        return "SepulcherNpc/" + string + ".htm";
    }

    public void setHandlingRoom(SepulcherRoom sepulcherRoom) {
        this.VQ = sepulcherRoom;
    }
}


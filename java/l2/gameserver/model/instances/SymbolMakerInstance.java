/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.HennaEquipList;
import l2.gameserver.network.l2.s2c.HennaUnequipList;
import l2.gameserver.templates.npc.NpcTemplate;

public class SymbolMakerInstance
extends NpcInstance {
    public SymbolMakerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!SymbolMakerInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.equals("Draw")) {
            player.sendPacket((IStaticPacket)new HennaEquipList(player));
        } else if (string.equals("RemoveList")) {
            player.sendPacket((IStaticPacket)new HennaUnequipList(player));
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = n2 == 0 ? "SymbolMaker" : "SymbolMaker-" + n2;
        return "symbolmaker/" + string + ".htm";
    }
}


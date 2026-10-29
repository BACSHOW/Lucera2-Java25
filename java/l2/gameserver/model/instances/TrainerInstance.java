/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public final class TrainerInstance
extends NpcInstance {
    public TrainerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = "";
        string = n2 == 0 ? "" + n : n + "-" + n2;
        return "trainer/" + string + ".htm";
    }
}


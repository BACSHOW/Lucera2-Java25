/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import l2.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.clanhall._34BossMinionInstance;

public class DietrichInstance
extends _34BossMinionInstance {
    public DietrichInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public String spawnChatSay() {
        return "Soldiers of Gustav, go forth and destroy the invaders!";
    }

    @Override
    public String teleChatSay() {
        return "Ah, the bitter taste of defeat... I fear my torments are not over...";
    }
}


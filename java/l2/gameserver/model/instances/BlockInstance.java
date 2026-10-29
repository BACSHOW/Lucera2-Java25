/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class BlockInstance
extends NpcInstance {
    private boolean TA;

    public BlockInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    public boolean isRed() {
        return this.TA;
    }

    public void setRed(boolean bl) {
        this.TA = bl;
        this.broadcastCharInfo();
    }

    public void changeColor() {
        this.setRed(!this.TA);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
    }

    @Override
    public boolean isNameAbove() {
        return false;
    }

    @Override
    public int getFormId() {
        return this.TA ? 83 : 0;
    }

    @Override
    public boolean isInvul() {
        return true;
    }
}


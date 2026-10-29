/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.npc;

import l2.gameserver.listener.NpcListener;
import l2.gameserver.model.instances.NpcInstance;

public interface OnDecayListener
extends NpcListener {
    public void onDecay(NpcInstance var1);
}


/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.listener;

import l2.gameserver.listener.actor.npc.OnDecayListener;
import l2.gameserver.listener.actor.npc.OnSpawnListener;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.NpcInstance;

public class NpcListenerList
extends CharListenerList {
    public NpcListenerList(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public NpcInstance getActor() {
        return (NpcInstance)this.actor;
    }

    public void onSpawn() {
        this.forEachListenerWithGlobal(OnSpawnListener.class, onSpawnListener -> onSpawnListener.onSpawn(this.getActor()));
    }

    public void onDecay() {
        this.forEachListenerWithGlobal(OnDecayListener.class, onDecayListener -> onDecayListener.onDecay(this.getActor()));
    }
}


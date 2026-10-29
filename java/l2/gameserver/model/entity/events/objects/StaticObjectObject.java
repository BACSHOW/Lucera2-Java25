/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import l2.gameserver.data.xml.holder.StaticObjectHolder;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.SpawnableObject;
import l2.gameserver.model.instances.StaticObjectInstance;

public class StaticObjectObject
implements SpawnableObject {
    private int Rm;
    private StaticObjectInstance Rn;

    public StaticObjectObject(int n) {
        this.Rm = n;
    }

    @Override
    public void spawnObject(GlobalEvent globalEvent) {
        this.Rn = StaticObjectHolder.getInstance().getObject(this.Rm);
    }

    @Override
    public void despawnObject(GlobalEvent globalEvent) {
    }

    @Override
    public void refreshObject(GlobalEvent globalEvent) {
        if (!globalEvent.isInProgress()) {
            this.Rn.removeEvent(globalEvent);
        } else {
            this.Rn.addEvent(globalEvent);
        }
    }

    public void setMeshIndex(int n) {
        this.Rn.setMeshIndex(n);
        this.Rn.broadcastInfo(false);
    }

    public int getUId() {
        return this.Rm;
    }
}


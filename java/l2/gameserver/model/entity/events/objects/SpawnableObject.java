/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import java.io.Serializable;
import l2.gameserver.model.entity.events.GlobalEvent;

public interface SpawnableObject
extends Serializable {
    public void spawnObject(GlobalEvent var1);

    public void despawnObject(GlobalEvent var1);

    public void refreshObject(GlobalEvent var1);
}


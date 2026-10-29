/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.SpawnableObject;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;

public class SpawnSimpleObject
implements SpawnableObject {
    private int _npcId;
    private Location cH;
    private NpcInstance _npc;

    public SpawnSimpleObject(int n, Location location) {
        this._npcId = n;
        this.cH = location;
    }

    @Override
    public void spawnObject(GlobalEvent globalEvent) {
        this._npc = NpcUtils.spawnSingle(this._npcId, this.cH, globalEvent.getReflection());
        this._npc.addEvent(globalEvent);
    }

    @Override
    public void despawnObject(GlobalEvent globalEvent) {
        this._npc.removeEvent(globalEvent);
        this._npc.deleteMe();
    }

    @Override
    public void refreshObject(GlobalEvent globalEvent) {
    }
}


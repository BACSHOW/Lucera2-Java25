/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager.sepulchers.model;

import java.util.List;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.utils.Location;

public class WayData {
    private final Location BY;
    private final Location BZ;
    private final List<DoorInstance> Ca;
    private final Zone Cb;

    public WayData(Location location, Location location2, List<DoorInstance> list, Zone zone) {
        this.BY = location;
        this.BZ = location2;
        this.Ca = list;
        this.Cb = zone;
    }

    public List<DoorInstance> getDoors() {
        return this.Ca;
    }

    public Location getEntry() {
        return this.BY;
    }

    public Location getEscape() {
        return this.BZ;
    }

    public Zone getZone() {
        return this.Cb;
    }
}


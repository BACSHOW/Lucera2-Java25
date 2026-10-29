/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager.sepulchers.model;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.sepulchers.FourSepulchers;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnHandler;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnSelector;
import l2.gameserver.model.instances.DoorInstance;

public class SepulcherRoom {
    private static final int BQ = 15000;
    private final SepulcherSpawnSelector BR;
    private final List<SepulcherSpawnHandler> BS = new ArrayList<SepulcherSpawnHandler>();
    private DoorInstance BT;

    public SepulcherRoom(SepulcherSpawnHandler sepulcherSpawnHandler) {
        this.BR = new SepulcherSpawnSelector(sepulcherSpawnHandler);
    }

    public SepulcherRoom(SepulcherSpawnSelector sepulcherSpawnSelector) {
        this.BR = sepulcherSpawnSelector;
    }

    public void spawn(SepulcherSpawnSelector sepulcherSpawnSelector) {
        sepulcherSpawnSelector.get().spawn();
    }

    public void spawn(SepulcherSpawnHandler sepulcherSpawnHandler) {
        sepulcherSpawnHandler.spawn();
    }

    public List<SepulcherSpawnHandler> getResponsibleMobSpawns() {
        return this.BS;
    }

    public void activate() {
        if (this.BT != null) {
            this.bL();
        }
        this.BR.get().spawn();
        FourSepulchers.getInstance().log("Room activated. door id: " + this.BT);
    }

    public void deactivate() {
        this.BS.forEach(SepulcherSpawnHandler::despawn);
        FourSepulchers.getInstance().log("Room deactivated. door id: " + this.BT);
    }

    public void setEnterDoor(DoorInstance doorInstance) {
        this.BT = doorInstance;
    }

    private void bL() {
        this.BT.openMe();
        ThreadPoolManager.getInstance().schedule(this.BT::closeMe, 15000L);
    }
}


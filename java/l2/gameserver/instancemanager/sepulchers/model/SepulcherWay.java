/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager.sepulchers.model;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.instancemanager.sepulchers.FourSepulchers;
import l2.gameserver.instancemanager.sepulchers.model.SepulcherRoom;
import l2.gameserver.instancemanager.sepulchers.model.WayData;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnHandler;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.instances.SepulcherDoormanInstance;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.scripts.Functions;

public class SepulcherWay {
    private final SepulcherSpawnHandler BU;
    private final SepulcherSpawnHandler BV;
    private final List<SepulcherRoom> BW = new ArrayList<SepulcherRoom>();
    private final WayData BX;

    public SepulcherWay(SepulcherSpawnHandler sepulcherSpawnHandler, SepulcherSpawnHandler sepulcherSpawnHandler2, WayData wayData) {
        this.BU = sepulcherSpawnHandler;
        this.BV = sepulcherSpawnHandler2;
        this.BX = wayData;
    }

    public void init() {
        this.BU.spawn();
        this.BV.spawn();
        List<NpcInstance> list = this.BV.collectAlive();
        for (int i = 0; i < list.size() && i + 1 < this.BW.size(); ++i) {
            ((SepulcherDoormanInstance)list.get(i)).setHandlingRoom(this.BW.get(i + 1));
            this.BW.get(i + 1).setEnterDoor(this.BX.getDoors().get(i));
        }
    }

    public List<SepulcherRoom> getRooms() {
        return this.BW;
    }

    public void activate() {
        FourSepulchers.getInstance().log("Activating way. Entrance npc: " + this.BU.getName());
        this.BW.get(0).activate();
    }

    public void deactivate() {
        this.BW.forEach(SepulcherRoom::deactivate);
    }

    public WayData getWayData() {
        return this.BX;
    }

    public SepulcherSpawnHandler getEntranceNpcSpawn() {
        return this.BU;
    }

    public void sayByEntrance(String string) {
        Functions.npcSayInRangeCustomMessage(this.getEntranceNpc(), ChatType.SHOUT, 15000, string, new Object[0]);
    }

    public NpcInstance getEntranceNpc() {
        return this.BU.collectAlive().get(0);
    }
}


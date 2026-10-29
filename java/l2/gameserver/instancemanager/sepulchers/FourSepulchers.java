/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager.sepulchers;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.sepulchers.SepulcherActivityRunner;
import l2.gameserver.instancemanager.sepulchers.event.OnEveryoneDespawned;
import l2.gameserver.instancemanager.sepulchers.model.SepulcherRoom;
import l2.gameserver.instancemanager.sepulchers.model.SepulcherWay;
import l2.gameserver.instancemanager.sepulchers.model.WayData;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherParser;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnHandler;
import l2.gameserver.instancemanager.sepulchers.spawn.SepulcherSpawnSelector;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FourSepulchers {
    private static final boolean BE = false;
    private static final FourSepulchers BF = new FourSepulchers();
    private final String[] BG = new String[]{"1st", "2nd", "3rd", "4th"};
    private final List<SepulcherWay> BH = new ArrayList<SepulcherWay>();
    private static final Logger _log = LoggerFactory.getLogger(FourSepulchers.class);

    public static FourSepulchers getInstance() {
        return BF;
    }

    public void init() {
        try {
            SepulcherParser.getInstance().load();
            for (int i = 0; i < this.BG.length; ++i) {
                String string = this.BG[i];
                SepulcherSpawnHandler sepulcherSpawnHandler = SepulcherParser.getInstance().getSpawns().get(string + "_entrance");
                SepulcherSpawnHandler sepulcherSpawnHandler2 = SepulcherParser.getInstance().getSpawns().get(string + "_doormans");
                WayData wayData = SepulcherParser.getInstance().getWayData().get(string);
                SepulcherWay sepulcherWay = new SepulcherWay(sepulcherSpawnHandler, sepulcherSpawnHandler2, wayData);
                sepulcherWay.getRooms().add(this.k(string));
                sepulcherWay.getRooms().add(this.l(string));
                sepulcherWay.getRooms().add(this.m(string));
                sepulcherWay.getRooms().add(this.n(string));
                sepulcherWay.getRooms().add(this.o(string));
                sepulcherWay.getRooms().add(this.p(string));
                sepulcherWay.init();
                this.BH.add(sepulcherWay);
            }
            SepulcherActivityRunner.getInstance().start();
        }
        catch (Exception exception) {
            _log.error(exception.getMessage(), exception);
        }
    }

    public void activate() {
        this.BH.forEach(SepulcherWay::activate);
    }

    public void deactivate() {
        this.BH.forEach(SepulcherWay::deactivate);
    }

    public void collapse() {
        this.BH.forEach(sepulcherWay -> {
            sepulcherWay.getWayData().getZone().getInsidePlayables().forEach(playable -> playable.teleToLocation(sepulcherWay.getWayData().getEscape()));
            sepulcherWay.sayByEntrance("ROYAL_RUSH_457_COLLAPSE");
        });
    }

    public void openRegistration() {
        this.BH.forEach(sepulcherWay -> {
            sepulcherWay.sayByEntrance("ROYAL_RUSH_500_MAY_ENTER");
            sepulcherWay.sayByEntrance("ROYAL_RUSH_501_PLACE_HAND");
            sepulcherWay.getEntranceNpc().av_quest0.set(0);
        });
    }

    private SepulcherRoom k(String string) {
        return this.a(string, "_trigger_a", "_type1_a", "_type2_a", "_type3_a");
    }

    private SepulcherRoom l(String string) {
        SepulcherSpawnHandler sepulcherSpawnHandler = SepulcherParser.getInstance().getSpawns().get(string + "_trigger_b");
        SepulcherRoom sepulcherRoom = new SepulcherRoom(sepulcherSpawnHandler);
        SepulcherSpawnHandler sepulcherSpawnHandler2 = SepulcherParser.getInstance().getSpawns().get(string + "_type1_b1");
        SepulcherSpawnHandler sepulcherSpawnHandler3 = SepulcherParser.getInstance().getSpawns().get(string + "_type1_b2");
        SepulcherSpawnHandler sepulcherSpawnHandler4 = SepulcherParser.getInstance().getSpawns().get(string + "_type1_b3");
        sepulcherSpawnHandler2.getEventHandler().add(OnEveryoneDespawned.class, () -> sepulcherRoom.spawn(sepulcherSpawnHandler3));
        sepulcherSpawnHandler3.getEventHandler().add(OnEveryoneDespawned.class, () -> sepulcherRoom.spawn(sepulcherSpawnHandler4));
        SepulcherSpawnSelector sepulcherSpawnSelector = new SepulcherSpawnSelector();
        sepulcherSpawnSelector.getOptions().add(sepulcherSpawnHandler2);
        sepulcherSpawnSelector.getOptions().add(sepulcherSpawnHandler3);
        sepulcherSpawnHandler.getEventHandler().add(OnEveryoneDespawned.class, () -> sepulcherRoom.spawn(sepulcherSpawnSelector));
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler2);
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler3);
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler4);
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler);
        return sepulcherRoom;
    }

    private SepulcherRoom m(String string) {
        return this.a(string, "_trigger_c", "_type1_c", "_type2_c");
    }

    private SepulcherRoom n(String string) {
        return this.a(string, "_trigger_d", "_type1_d", "_type2_d", "_type3_d");
    }

    private SepulcherRoom o(String string) {
        SepulcherSpawnHandler sepulcherSpawnHandler = SepulcherParser.getInstance().getSpawns().get(string + "_trigger_e");
        SepulcherRoom sepulcherRoom = new SepulcherRoom(sepulcherSpawnHandler);
        SepulcherSpawnHandler sepulcherSpawnHandler2 = SepulcherParser.getInstance().getSpawns().get(string + "_type1_e");
        SepulcherSpawnHandler sepulcherSpawnHandler3 = SepulcherParser.getInstance().getSpawns().get(string + "_type1_boss_e");
        sepulcherSpawnHandler2.getEventHandler().add(OnEveryoneDespawned.class, () -> sepulcherRoom.spawn(sepulcherSpawnHandler3));
        SepulcherSpawnHandler sepulcherSpawnHandler4 = SepulcherParser.getInstance().getSpawns().get(string + "_type2_e");
        SepulcherSpawnHandler sepulcherSpawnHandler5 = SepulcherParser.getInstance().getSpawns().get(string + "_type2_boss_e");
        sepulcherSpawnHandler4.getEventHandler().add(OnEveryoneDespawned.class, () -> sepulcherRoom.spawn(sepulcherSpawnHandler5));
        SepulcherSpawnSelector sepulcherSpawnSelector = new SepulcherSpawnSelector();
        sepulcherSpawnSelector.getOptions().add(sepulcherSpawnHandler2);
        sepulcherSpawnSelector.getOptions().add(sepulcherSpawnHandler4);
        sepulcherSpawnHandler.getEventHandler().add(OnEveryoneDespawned.class, () -> sepulcherRoom.spawn(sepulcherSpawnSelector));
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler2);
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler4);
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler3);
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler5);
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler);
        return sepulcherRoom;
    }

    private SepulcherRoom p(String string) {
        SepulcherSpawnSelector sepulcherSpawnSelector = new SepulcherSpawnSelector();
        sepulcherSpawnSelector.getOptions().add(SepulcherParser.getInstance().getSpawns().get(string + "_boss_type1"));
        sepulcherSpawnSelector.getOptions().add(SepulcherParser.getInstance().getSpawns().get(string + "_boss_type2"));
        sepulcherSpawnSelector.getOptions().add(SepulcherParser.getInstance().getSpawns().get(string + "_boss_type3"));
        sepulcherSpawnSelector.getOptions().add(SepulcherParser.getInstance().getSpawns().get(string + "_boss_type4"));
        SepulcherRoom sepulcherRoom = new SepulcherRoom(sepulcherSpawnSelector);
        SepulcherSpawnHandler sepulcherSpawnHandler = SepulcherParser.getInstance().getSpawns().get(string + "_boss_treasure");
        sepulcherSpawnSelector.getOptions().forEach(sepulcherSpawnHandler2 -> sepulcherSpawnHandler2.getEventHandler().add(OnEveryoneDespawned.class, () -> sepulcherRoom.spawn(sepulcherSpawnHandler)));
        sepulcherRoom.getResponsibleMobSpawns().addAll(sepulcherSpawnSelector.getOptions());
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler);
        return sepulcherRoom;
    }

    private SepulcherRoom a(String string, String string2, String ... stringArray) {
        SepulcherSpawnHandler sepulcherSpawnHandler = SepulcherParser.getInstance().getSpawns().get(string + string2);
        SepulcherRoom sepulcherRoom = new SepulcherRoom(sepulcherSpawnHandler);
        SepulcherSpawnSelector sepulcherSpawnSelector = new SepulcherSpawnSelector();
        for (String string3 : stringArray) {
            sepulcherSpawnSelector.getOptions().add(SepulcherParser.getInstance().getSpawns().get(string + string3));
        }
        sepulcherSpawnHandler.getEventHandler().add(OnEveryoneDespawned.class, () -> sepulcherRoom.spawn(sepulcherSpawnSelector));
        sepulcherRoom.getResponsibleMobSpawns().addAll(sepulcherSpawnSelector.getOptions());
        sepulcherRoom.getResponsibleMobSpawns().add(sepulcherSpawnHandler);
        return sepulcherRoom;
    }

    public void log(String string) {
        if (Config.FOUR_DEBUG_SEPULCHER_ENABLE) {
            Log.debug("[FS_DBG] " + string);
        }
    }
}


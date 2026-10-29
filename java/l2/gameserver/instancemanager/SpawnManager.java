/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import l2.gameserver.Config;
import l2.gameserver.GameTimeController;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.data.xml.holder.SpawnHolder;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.game.OnDayNightChangeListener;
import l2.gameserver.listener.game.OnSSPeriodListener;
import l2.gameserver.model.HardSpawner;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.templates.spawn.PeriodOfDay;
import l2.gameserver.templates.spawn.SpawnTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnManager {
    private static final Logger _log = LoggerFactory.getLogger(SpawnManager.class);
    private static SpawnManager Bb = new SpawnManager();
    private static final String Bc = "[ssq_event]";
    private static final String Bd = "[ssq_seal1_none]";
    private static final String Be = "[ssq_seal1_twilight]";
    private static final String Bf = "[ssq_seal1_dawn]";
    private static final String Bg = "[ssq_seal2_none]";
    private static final String Bh = "[ssq_seal2_twilight]";
    private static final String Bi = "[ssq_seal2_dawn]";
    private static final String Bj = "dawn_spawn";
    private static final String Bk = "dusk_spawn";
    private Map<String, List<Spawner>> wX = new ConcurrentHashMap<String, List<Spawner>>();
    private Listeners Bl = new Listeners();

    public static SpawnManager getInstance() {
        return Bb;
    }

    private SpawnManager() {
        for (Map.Entry<String, List<SpawnTemplate>> entry : SpawnHolder.getInstance().getSpawns().entrySet()) {
            this.fillSpawn(entry.getKey(), entry.getValue());
        }
        GameTimeController.getInstance().addListener(this.Bl);
        SevenSigns.getInstance().addListener(this.Bl);
    }

    public List<Spawner> fillSpawn(String string, List<SpawnTemplate> list) {
        if (Config.DONTLOADSPAWN) {
            return Collections.emptyList();
        }
        List<Spawner> list2 = this.wX.get(string);
        if (list2 == null) {
            list2 = new ArrayList<Spawner>(list.size());
            this.wX.put(string, list2);
        }
        for (SpawnTemplate spawnTemplate : list) {
            try {
                HardSpawner hardSpawner = new HardSpawner(spawnTemplate);
                list2.add(hardSpawner);
                NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(hardSpawner.getCurrentNpcId());
                if (Config.RATE_MOB_SPAWN > 1 && npcTemplate.getInstanceClass() == MonsterInstance.class && npcTemplate.level >= Config.RATE_MOB_SPAWN_MIN_LEVEL && npcTemplate.level <= Config.RATE_MOB_SPAWN_MAX_LEVEL) {
                    hardSpawner.setAmount(spawnTemplate.getCount() * Config.RATE_MOB_SPAWN);
                } else {
                    hardSpawner.setAmount(spawnTemplate.getCount());
                }
                hardSpawner.setRespawnDelay(spawnTemplate.getRespawn(), spawnTemplate.getRespawnRandom());
                hardSpawner.setRespawnCron(spawnTemplate.getRespawnCron());
                hardSpawner.setReflection(ReflectionManager.DEFAULT);
                hardSpawner.setRespawnTime(0);
                if (!npcTemplate.isRaid || !string.equals(PeriodOfDay.ALL.name())) continue;
                RaidBossSpawnManager.getInstance().addNewSpawn(npcTemplate.getNpcId(), hardSpawner);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return list2;
    }

    public void spawnAll() {
        this.spawn(PeriodOfDay.ALL.name());
        if (Config.ALLOW_EVENT_GATEKEEPER) {
            this.spawn("[event_gate]");
        }
        if (Config.ALLOW_GLOBAL_GK) {
            this.spawn("[global_gatekeeper]");
        }
        if (Config.ALLOW_BUFFER) {
            this.spawn("[npc_buffer]");
        }
        if (Config.ALLOW_GMSHOP) {
            this.spawn("[gm_shop]");
        }
        if (Config.ALLOW_AUCTIONER) {
            this.spawn("[auctioner]");
        }
        if (Config.ALLOW_GLOBAL_SERVICES) {
            this.spawn("[global_services]");
        }
        if (Config.ALLOW_PVP_EVENT_MANAGER) {
            this.spawn("[pvp_event_manager]");
        }
        if (Config.ALLOW_TREASURE_BOX) {
            this.spawn("[treasure_box]");
        }
        if (Config.SERVICES_ALLOW_LOTTERY) {
            this.spawn("[lotto_manager]");
        }
        if (!Config.CLASS_MASTERS_CLASSES.isEmpty()) {
            this.spawn("class_master");
        }
    }

    public void spawn(String string) {
        List<Spawner> list = this.getSpawners(string);
        if (list == null) {
            return;
        }
        int n = 0;
        for (Spawner spawner : list) {
            if ((n += spawner.init()) % 1000 != 0 || n == 0) continue;
            _log.debug("SpawnManager: spawned " + n + " npc for group: " + string);
        }
        if (n > 0) {
            _log.info("SpawnManager: spawned " + n + " npc; spawns: " + list.size() + "; group: " + string);
        }
    }

    public void spawnDay() {
        this.spawn(PeriodOfDay.DAY.name());
    }

    public void spawnNight() {
        this.spawn(PeriodOfDay.NIGHT.name());
    }

    public void despawnAll() {
        this.despawn(PeriodOfDay.ALL.name());
        if (Config.ALLOW_EVENT_GATEKEEPER) {
            this.despawn("[event_gate]");
        }
        if (Config.ALLOW_GLOBAL_GK) {
            this.despawn("[global_gatekeeper]");
        }
        if (Config.ALLOW_BUFFER) {
            this.despawn("[npc_buffer]");
        }
        if (Config.ALLOW_GMSHOP) {
            this.despawn("[gm_shop]");
        }
        if (Config.ALLOW_AUCTIONER) {
            this.despawn("[auctioner]");
        }
        if (Config.ALLOW_GLOBAL_SERVICES) {
            this.despawn("[global_services]");
        }
        if (Config.ALLOW_PVP_EVENT_MANAGER) {
            this.despawn("[pvp_event_manager]");
        }
        if (Config.ALLOW_TREASURE_BOX) {
            this.despawn("[treasure_box]");
        }
        if (Config.SERVICES_ALLOW_LOTTERY) {
            this.despawn("[lotto_manager]");
        }
        if (!Config.CLASS_MASTERS_CLASSES.isEmpty()) {
            this.despawn("class_master");
        }
        _log.info("SpawnManager: All NPC removed from the world");
    }

    public void despawn(String string) {
        List<Spawner> list = this.wX.get(string);
        if (list == null) {
            return;
        }
        for (Spawner spawner : list) {
            spawner.deleteAll();
        }
    }

    public List<Spawner> getSpawners(String string) {
        List<Spawner> list = this.wX.get(string);
        return list == null ? Collections.emptyList() : list;
    }

    public void reloadAll() {
        RaidBossSpawnManager.getInstance().cleanUp();
        for (List<Spawner> list : this.wX.values()) {
            for (Spawner spawner : list) {
                spawner.deleteAll();
            }
        }
        RaidBossSpawnManager.getInstance().reloadBosses();
        this.spawnAll();
        if (SevenSigns.getInstance().getCurrentPeriod() == 3) {
            SevenSigns.getInstance().getCabalHighestScore();
        }
        this.Bl.onPeriodChange(SevenSigns.getInstance().getCurrentPeriod());
        if (GameTimeController.getInstance().isNowNight()) {
            this.Bl.onNight();
        } else {
            this.Bl.onDay();
        }
    }

    private class Listeners
    implements OnDayNightChangeListener,
    OnSSPeriodListener {
        private Listeners() {
        }

        @Override
        public void onDay() {
            SpawnManager.this.despawn(PeriodOfDay.NIGHT.name());
            SpawnManager.this.spawn(PeriodOfDay.DAY.name());
        }

        @Override
        public void onNight() {
            SpawnManager.this.despawn(PeriodOfDay.DAY.name());
            SpawnManager.this.spawn(PeriodOfDay.NIGHT.name());
        }

        @Override
        public void onPeriodChange(int n) {
            SpawnManager.this.despawn(SpawnManager.Bc);
            SpawnManager.this.despawn(SpawnManager.Bd);
            SpawnManager.this.despawn(SpawnManager.Be);
            SpawnManager.this.despawn(SpawnManager.Bf);
            SpawnManager.this.despawn(SpawnManager.Bg);
            SpawnManager.this.despawn(SpawnManager.Bh);
            SpawnManager.this.despawn(SpawnManager.Bi);
            block0 : switch (SevenSigns.getInstance().getCurrentPeriod()) {
                case 0: 
                case 2: {
                    if (!Config.ALT_SEVEN_SING_STATIC_EVENT_PERIOD_SPAWN) break;
                    SpawnManager.this.spawn(SpawnManager.Bc);
                    break;
                }
                case 1: {
                    SpawnManager.this.spawn(SpawnManager.Bc);
                    break;
                }
                case 3: {
                    if (Config.ALT_SEVEN_SING_STATIC_EVENT_PERIOD_SPAWN) {
                        SpawnManager.this.spawn(SpawnManager.Bc);
                        break;
                    }
                    switch (SevenSigns.getInstance().getSealOwner(1)) {
                        case 0: {
                            SpawnManager.this.spawn(SpawnManager.Bd);
                            break;
                        }
                        case 1: {
                            SpawnManager.this.spawn(SpawnManager.Be);
                            break;
                        }
                        case 2: {
                            SpawnManager.this.spawn(SpawnManager.Bf);
                        }
                    }
                    switch (SevenSigns.getInstance().getSealOwner(2)) {
                        case 0: {
                            SpawnManager.this.spawn(SpawnManager.Bg);
                            break block0;
                        }
                        case 1: {
                            SpawnManager.this.spawn(SpawnManager.Bh);
                            break block0;
                        }
                        case 2: {
                            SpawnManager.this.spawn(SpawnManager.Bi);
                        }
                    }
                }
            }
        }
    }
}


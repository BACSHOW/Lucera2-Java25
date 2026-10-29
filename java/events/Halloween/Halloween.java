/*
 * Decompiled with CFR 0.152.
 */
package events.Halloween;

import events.Halloween.PumpkinGhostAI;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Halloween
extends Functions
implements ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(Halloween.class);
    private static final String EVENT_NAME = "HalloweenEvent";
    private static final int[][] hB = new int[][]{{81945, 148597, -3472, 600, 15}, {147456, 27480, -2229, 500, 12}, {82610, 55643, -1550, 200, 6}, {18650, 145436, -3153, 350, 9}, {111389, 220161, -3700, 420, 10}, {-14225, 123540, -3121, 200, 7}, {147723, -56388, -2807, 150, 5}, {87356, -141767, -1344, 220, 7}};
    private static final String[] hC = new String[]{"Hot Springs:147607,-114850,-2002", "Rainbow Springs Chateau:152587,-126642,-2315", "Forge of the Gods:167693,-113024,-2844", "Wall of Argos:176338,-49702,-3334", "Swamp of Screams:94637,-60140,-2475", "Restless Forest:65725,-48176,-2823", "Valley of Saints:66767,-73106,-3718", "Windtail Waterfall:41303,-92076,-3703", "Cursed Village:57129,-41255,-3177", "Stakato Nest:88413,-43714,-2193", "Beast Farm:44735,-87574,-2578", "Grave Robber Hideout:46146,-106761,-1516", "Crypts of Disgrace:43242,-120040,-3408", "Den of Evil:67288,-112072,-2176", "Archaic Laboratory:92168,-115144,-3344", "Plunderous Plains:127736,-149416,-3736", "Brigand Stronghold:124520,-161496,-1168", "Deamon Fortress:100006,-52612,-673", "Borderland Fortress:155951,-70319,-2804", "Lost Nest:24044,-10452,-2589", "Primeval Plains Waterfall:6810,-12014,-3674", "Mimir's Forest:-82051,51084,-3339", "Chromatic Highlands:154735,152587,-3684"};
    private static final String hD = "<br1>[scripts_events.Halloween.Halloween:show|\"Halloween Event\"]<br1>";
    private static final String hE = "<br1>[scripts_events.Halloween.Halloween:show|\"\u0418\u0432\u0435\u043d\u0442 \u0425\u044d\u043b\u043b\u043e\u0443\u0438\u043d\"]<br1>";
    private static boolean _active = false;
    private static ScheduledFuture<?> hF;
    private static ArrayList<NpcInstance> hG;
    private static NpcInstance hH;

    private static boolean isActive() {
        return Halloween.IsActive(EVENT_NAME);
    }

    public static void OnPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.Halloween.EventActive", null);
        }
    }

    public static void SpawnGhosts(int n) {
        ArrayList<Location> arrayList = new ArrayList<Location>();
        double d = Math.PI * 2 / (double)hB[n][4];
        for (int i = 0; i < hB[n][4]; ++i) {
            double d2 = d * (double)i;
            arrayList.add(new Location(hB[n][0] + (int)((double)hB[n][3] * Math.cos(d2)), hB[n][1] + (int)((double)hB[n][3] * Math.sin(d2)), hB[n][2]));
        }
        Location[] locationArray = arrayList.toArray(new Location[arrayList.size()]);
        arrayList.clear();
        for (int i = 0; i < locationArray.length; ++i) {
            NpcInstance npcInstance = NpcHolder.getInstance().getTemplate(Config.EVENT_PUMPKIN_GHOST_ID).getNewInstance();
            npcInstance.setAI(new PumpkinGhostAI(npcInstance, locationArray, i, Config.EVENT_PUMPKIN_DROP_CHANCE, Config.EVENT_PUMPKIN_DROP_ITEMS));
            npcInstance.setFlying(false);
            npcInstance.setTargetable(false);
            npcInstance.setShowName(false);
            npcInstance.spawnMe(locationArray[i]);
            npcInstance.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            hG.add(npcInstance);
        }
        Announcements.getInstance().announceByCustomMessage("scripts.events.Halloween.PumpkinGhost.spawn." + n, null);
    }

    public static void DespawnGhost() {
        if (hG == null || hG.isEmpty()) {
            return;
        }
        for (NpcInstance npcInstance : hG) {
            npcInstance.deleteMe();
        }
        hG.clear();
    }

    public static void RunEvent() {
        Halloween.SkooldieDespawn();
        Halloween.DespawnGhost();
        Halloween.SpawnGhosts(Rnd.get(hB.length));
        Halloween.executeTask("events.Halloween.Halloween", "DespawnGhost", new Object[0], Config.EVENT_PUMPKIN_GHOST_SHOW_TIME);
        Halloween.executeTask("events.Halloween.Halloween", "SkooldieSpawn", new Object[0], Config.EVENT_PUMPKIN_GHOST_SHOW_TIME - 30000);
        Halloween.executeTask("events.Halloween.Halloween", "SkooldieDespawn", new Object[0], Config.EVENT_PUMPKIN_GHOST_SHOW_TIME + Config.EVENT_SKOOLDIE_TIME);
    }

    public static void SkooldieSpawn() {
        if (hH != null) {
            hH.deleteMe();
        }
        int n = Rnd.get(hC.length);
        String[] stringArray = hC[n].split(":");
        hH = Functions.spawn(Location.parseLoc(stringArray[1]), Config.EVENT_SKOOLDIE_REWARDER);
        Announcements.getInstance().announceByCustomMessage("scripts.events.Halloween.SkooldieSpawned", new String[]{stringArray[0]});
    }

    public static void SkooldieDespawn() {
        if (hH != null) {
            hH.deleteMe();
        }
        hH = null;
    }

    public static void start() {
        Halloween.stop();
        hF = ThreadPoolManager.getInstance().scheduleAtFixedRate(new EventRunner(), 120000L, Config.EVENT_PUMPKIN_DELAY);
    }

    public static void stop() {
        if (hF != null) {
            hF.cancel(true);
            hF = null;
        }
        Halloween.DespawnGhost();
        Halloween.SkooldieDespawn();
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (Halloween.SetActive(EVENT_NAME, true)) {
            Halloween.start();
            _log.info("Event: 'HalloweenEvent' started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.Halloween.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'HalloweenEvent' already started.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        Halloween.DespawnGhost();
        Halloween.SkooldieDespawn();
        if (Halloween.SetActive(EVENT_NAME, false)) {
            Halloween.stop();
            _log.info("Event: 'HalloweenEvent' stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.Halloween.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'HalloweenEvent' not started.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    public String DialogAppend_40029(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String getHtmlAppends(Integer n) {
        if (!_active || n != 0) {
            return "";
        }
        Player player = this.getSelf();
        if (player == null) {
            return "";
        }
        return player.isLangRus() ? hE : hD;
    }

    public void show() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        this.show("scripts/events/halloween/exchange.htm", player);
    }

    public void exchange() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        long l = Functions.getItemCount(player, Config.EVENT_HALLOWEEN_CANDY);
        if (l > 0L) {
            Functions.removeItem(player, Config.EVENT_HALLOWEEN_CANDY, Config.EVENT_HALLOWEEN_CANDY_ITEM_COUNT_NEEDED);
            Functions.addItem(player, Config.EVENT_HALLOWEEN_TOY_CHEST, Config.EVENT_HALLOWEEN_TOY_CHEST_REWARD_AMOUNT);
            Announcements.getInstance().announceByCustomMessage("scripts.events.Halloween.SkooldieFind", new String[]{player.getName()});
            Halloween.SkooldieDespawn();
            Halloween.DespawnGhost();
        } else {
            this.show("scripts/events/halloween/noitem.htm", player);
        }
    }

    @Override
    public void onLoad() {
        if (Halloween.isActive()) {
            _active = true;
            Halloween.start();
            _log.info("Loaded Event: 'HalloweenEvent' [state: activated]");
        } else {
            _log.debug("Loaded Event: 'HalloweenEvent' [state: deactivated]");
        }
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    static {
        hG = new ArrayList();
        hH = null;
    }

    public static class EventRunner
    implements Runnable {
        @Override
        public void run() {
            Halloween.RunEvent();
        }
    }
}


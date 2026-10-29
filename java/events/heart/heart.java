/*
 * Decompiled with CFR 0.152.
 */
package events.heart;

import java.util.HashMap;
import java.util.Map;
import l2.commons.text.PrintfFormat;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class heart
extends Functions
implements OnDeathListener,
OnPlayerEnterListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(heart.class);
    private static boolean _active = false;
    private static final Map<Integer, Integer> kR = new HashMap<Integer, Integer>();
    private static String kS = "";
    private static String kT = "";
    private static final String[][] kU = new String[][]{{"Rock", "\u041a\u0430\u043c\u0435\u043d\u044c"}, {"Scissors", "\u041d\u043e\u0436\u043d\u0438\u0446\u044b"}, {"Paper", "\u0411\u0443\u043c\u0430\u0433\u0430"}};
    private static final String kV = "[event_heart_spawn]";
    private static final int[] kW;
    private static final int[] kX;
    private static final int[] kY;

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (heart.SetActive("heart", true)) {
            this.spawnEventManagers();
            System.out.println("Event 'Change of Heart' started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.ChangeofHeart.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'Change of Heart' already started.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (heart.SetActive("heart", false)) {
            this.unSpawnEventManagers();
            System.out.println("Event 'Change of Heart' stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.ChangeofHeart.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'Change of Heart' not started.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    public void letsplay() {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        this.g(player);
        if (this.i(player)) {
            this.show(this.a(HtmCache.getInstance().getNotNull("scripts/events/heart/hearts_01.htm", player), this.d(player)), player);
        } else {
            this.show("scripts/events/heart/hearts_00.htm", player);
        }
    }

    public void play(String[] stringArray) {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true) || stringArray.length == 0) {
            return;
        }
        if (!this.i(player)) {
            if (stringArray[0].equalsIgnoreCase("Quit")) {
                this.show("scripts/events/heart/hearts_00b.htm", player);
            } else {
                this.show("scripts/events/heart/hearts_00a.htm", player);
            }
            return;
        }
        if (stringArray[0].equalsIgnoreCase("Quit")) {
            int n = this.e(player);
            this.h(player);
            this.b(player, n);
            this.show("scripts/events/heart/hearts_reward_" + n + ".htm", player);
            this.g(player);
            return;
        }
        int n = Rnd.get(kU.length);
        int n2 = 0;
        try {
            n2 = Integer.parseInt(stringArray[0]);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        if (n2 == n) {
            this.show(this.a(HtmCache.getInstance().getNotNull("scripts/events/heart/hearts_same.htm", player), n2, n, player), player);
            return;
        }
        if (this.c(n2, n)) {
            this.f(player);
            int n3 = this.e(player);
            if (n3 == 10) {
                this.h(player);
                this.b(player, n3);
                this.g(player);
            }
            this.show(this.a(HtmCache.getInstance().getNotNull("scripts/events/heart/hearts_level_" + n3 + ".htm", player), n2, n, player), player);
            return;
        }
        this.h(player);
        this.b(player, this.e(player) - 1);
        this.show(this.a(HtmCache.getInstance().getNotNull("scripts/events/heart/hearts_loose.htm", player), n2, n, player), player);
        this.g(player);
    }

    private void b(Player player, int n) {
        switch (n) {
            case -1: 
            case 0: {
                heart.addItem(player, kY[Rnd.get(kY.length)], 1L);
                break;
            }
            case 1: {
                heart.addItem(player, kX[Rnd.get(kX.length)], 10L);
                break;
            }
            case 2: {
                heart.addItem(player, 1538, 1L);
                break;
            }
            case 3: {
                heart.addItem(player, 3936, 1L);
                break;
            }
            case 4: {
                heart.addItem(player, 951, 2L);
                break;
            }
            case 5: {
                heart.addItem(player, 948, 4L);
                break;
            }
            case 6: {
                heart.addItem(player, 947, 1L);
                break;
            }
            case 7: {
                heart.addItem(player, 730, 3L);
                break;
            }
            case 8: {
                heart.addItem(player, 729, 1L);
                break;
            }
            case 9: {
                heart.addItem(player, 960, 2L);
                break;
            }
            case 10: {
                heart.addItem(player, 959, 1L);
            }
        }
    }

    private String a(String string, int n, int n2, Player player) {
        boolean bl = this.d(player);
        return this.a(string.replaceFirst("Player", player.getName()).replaceFirst("%var_payer%", kU[n][bl ? 1 : 0]).replaceFirst("%var_cat%", kU[n2][bl ? 1 : 0]), bl);
    }

    private boolean d(Player player) {
        return player.isLangRus();
    }

    private String a(String string, boolean bl) {
        return string.replaceFirst("%links%", bl ? kT : kS);
    }

    private boolean c(int n, int n2) {
        if (n == 0) {
            return n2 == 1;
        }
        if (n == 1) {
            return n2 == 2;
        }
        if (n == 2) {
            return n2 == 0;
        }
        return false;
    }

    private int e(Player player) {
        return kR.containsKey(player.getObjectId()) ? kR.get(player.getObjectId()) : 0;
    }

    private void f(Player player) {
        int n = 1;
        if (kR.containsKey(player.getObjectId())) {
            n = kR.remove(player.getObjectId()) + 1;
        }
        kR.put(player.getObjectId(), n);
    }

    private void g(Player player) {
        if (kR.containsKey(player.getObjectId())) {
            kR.remove(player.getObjectId());
        }
    }

    private void h(Player player) {
        for (int n : kW) {
            heart.removeItem(player, n, 1L, false);
        }
    }

    private boolean i(Player player) {
        for (int n : kW) {
            if (player.getInventory().getCountOf(n) >= 1L) continue;
            return false;
        }
        return true;
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (_active && heart.SimpleCheckDrop(creature, creature2)) {
            ((NpcInstance)creature).dropItem(creature2.getPlayer(), kW[Rnd.get(kW.length)], Util.rollDrop(1L, 1L, Config.EVENT_CHANGE_OF_HEART_CHANCE * creature2.getPlayer().getRateItems() * ((MonsterInstance)creature).getTemplate().rateHp * 10000.0, true));
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.ChangeofHeart.AnnounceEventStarted", null);
        }
    }

    private static boolean isActive() {
        return heart.IsActive("heart");
    }

    private void spawnEventManagers() {
        SpawnManager.getInstance().spawn(kV);
    }

    private void unSpawnEventManagers() {
        SpawnManager.getInstance().despawn(kV);
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (heart.isActive()) {
            _active = true;
            this.spawnEventManagers();
            _log.info("Loaded Event: Change of Heart [state: activated]");
        } else {
            _log.debug("Loaded Event: Change of Heart[state: deactivated]");
        }
    }

    @Override
    public void onReload() {
        this.unSpawnEventManagers();
    }

    @Override
    public void onShutdown() {
        this.unSpawnEventManagers();
    }

    static {
        PrintfFormat printfFormat = new PrintfFormat("<br><a action=\"bypass -h scripts_events.heart.heart:play %d\">\"%s!\"</a>");
        for (int i = 0; i < kU.length; ++i) {
            kS = kS + printfFormat.sprintf(i, kU[i][0]);
            kT = kT + printfFormat.sprintf(i, kU[i][1]);
        }
        kW = new int[]{4209, 4210, 4211, 4212, 4213, 4214, 4215, 4216, 4217};
        kX = new int[]{1374, 1375, 6036, 1539};
        kY = new int[]{3926, 3927, 3928, 3929, 3930, 3931, 3932, 3933, 3934, 3935};
    }
}


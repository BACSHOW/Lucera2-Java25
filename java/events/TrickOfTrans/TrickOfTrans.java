/*
 * Decompiled with CFR 0.152.
 */
package events.TrickOfTrans;

import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrickOfTrans
extends Functions
implements OnDeathListener,
OnPlayerEnterListener,
ScriptFile {
    private static final Logger _log = LoggerFactory.getLogger(TrickOfTrans.class);
    private static String iS = "[trick_of_trans_spawn]";
    private static int iT = 9162;
    private static int iU = 9163;
    private static int iV = 9164;
    private static int iW = 9165;
    private static int iX = 9166;
    private static int iY = 9167;
    private static int iZ = 9171;
    private static int ja = 9172;
    private static int jb = 9173;
    private static int jc = 9174;
    private static int jd = 9175;
    private static int je = 9176;
    private static int jf = 9205;
    private static boolean _active = false;
    private static int jg = 9168;
    private static int jh = 17;
    private static int ji = 9169;
    private static int jj = 9170;
    private static int jk = 30;

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (TrickOfTrans.isActive()) {
            _active = true;
            this.spawnEventManagers();
            _log.info("Loaded Event: Trick of Trnasmutation [state: activated]");
        } else {
            _log.debug("Loaded Event: Trick of Trnasmutation [state: deactivated]");
        }
    }

    private static boolean isActive() {
        return TrickOfTrans.IsActive("trickoftrans");
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (TrickOfTrans.SetActive("trickoftrans", true)) {
            this.spawnEventManagers();
            System.out.println("Event 'Trick of Transmutation' started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.TrickOfTrans.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'Trick of Transmutation' already started.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (TrickOfTrans.SetActive("trickoftrans", false)) {
            this.unSpawnEventManagers();
            System.out.println("Event 'Trick of Transmutation' stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.TrickOfTrans.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'Trick of Transmutation' not started.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.TrickOfTrans.AnnounceEventStarted", null);
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

    private void spawnEventManagers() {
        SpawnManager.getInstance().spawn(iS);
    }

    private void unSpawnEventManagers() {
        SpawnManager.getInstance().despawn(iS);
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (_active && TrickOfTrans.SimpleCheckDrop(creature, creature2) && Rnd.chance(Config.EVENT_TRICK_OF_TRANS_CHANCE * creature2.getPlayer().getRateItems() * Config.RATE_DROP_ITEMS * ((NpcInstance)creature).getTemplate().rateHp)) {
            ((NpcInstance)creature).dropItem(creature2.getPlayer(), jf, 1L);
        }
    }

    public void accept() {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        if (!player.findRecipe(iZ)) {
            TrickOfTrans.addItem(player, iT, 1L);
        }
        if (!player.findRecipe(jc)) {
            TrickOfTrans.addItem(player, iW, 1L);
        }
        if (!player.findRecipe(ja)) {
            TrickOfTrans.addItem(player, iU, 1L);
        }
        if (!player.findRecipe(je)) {
            TrickOfTrans.addItem(player, iY, 1L);
        }
        if (!player.findRecipe(jb)) {
            TrickOfTrans.addItem(player, iV, 1L);
        }
        if (!player.findRecipe(jd)) {
            TrickOfTrans.addItem(player, iX, 1L);
        }
        this.show("scripts/events/TrickOfTrans/TrickOfTrans_01.htm", player);
    }

    public void open() {
        Player player = this.getSelf();
        if (TrickOfTrans.getItemCount(player, jf) > 0L) {
            TrickOfTrans.removeItem(player, jf, 1L);
            TrickOfTrans.addItem(player, jg, Rnd.get(1, jh));
            TrickOfTrans.addItem(player, jj, Rnd.get(1, jk));
            if (Rnd.chance(80)) {
                TrickOfTrans.addItem(player, ji, 1L);
            }
            this.show("scripts/events/TrickOfTrans/TrickOfTrans_02.htm", player);
        } else {
            this.show("scripts/events/TrickOfTrans/TrickOfTrans_03.htm", player);
        }
    }
}


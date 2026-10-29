/*
 * Decompiled with CFR 0.152.
 */
package events.glitmedal;

import java.io.File;
import l2.commons.util.Rnd;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.instancemanager.SpawnManager;
import l2.gameserver.listener.actor.OnDeathListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class glitmedal
extends Functions
implements OnDeathListener,
OnPlayerEnterListener,
ScriptFile {
    private static String kH = "[event_giltmedal_roy_spawn]";
    private static String kI = "[event_giltmedal_winnie_spawn]";
    private static final Logger _log = LoggerFactory.getLogger(glitmedal.class);
    private int kJ;
    private static int kK = 6392;
    private static int kL = 6393;
    private static int kM = 6399;
    private static int kN = 6400;
    private static int kO = 6401;
    private static int kP = 6402;
    private static boolean _active = false;
    private static boolean iP = false;
    private static File[] kQ = new File[]{new File(Config.DATAPACK_ROOT, "data/html-en/scripts/events/glitmedal/502.xml"), new File(Config.DATAPACK_ROOT, "data/html-en/scripts/events/glitmedal/503.xml"), new File(Config.DATAPACK_ROOT, "data/html-en/scripts/events/glitmedal/504.xml"), new File(Config.DATAPACK_ROOT, "data/html-en/scripts/events/glitmedal/505.xml"), new File(Config.DATAPACK_ROOT, "data/html-en/scripts/events/glitmedal/506.xml")};

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (glitmedal.isActive()) {
            _active = true;
            glitmedal.Y();
            this.spawnEventManagers();
            _log.info("Loaded Event: L2 Medal Collection Event [state: activated]");
        } else {
            _log.debug("Loaded Event: L2 Medal Collection Event [state: deactivated]");
        }
    }

    private static boolean isActive() {
        return glitmedal.IsActive("glitter");
    }

    public void startEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (glitmedal.SetActive("glitter", true)) {
            glitmedal.Y();
            this.spawnEventManagers();
            System.out.println("Event 'L2 Medal Collection Event' started.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.glitmedal.AnnounceEventStarted", null);
        } else {
            player.sendMessage("Event 'L2 Medal Collection Event' already started.");
        }
        _active = true;
        this.show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = this.getSelf();
        if (!player.getPlayerAccess().IsEventGm) {
            return;
        }
        if (glitmedal.SetActive("glitter", false)) {
            this.unSpawnEventManagers();
            System.out.println("Event 'L2 Medal Collection Event' stopped.");
            Announcements.getInstance().announceByCustomMessage("scripts.events.glitmedal.AnnounceEventStoped", null);
        } else {
            player.sendMessage("Event 'L2 Medal Collection Event' not started.");
        }
        _active = false;
        this.show("admin/events/events.htm", player);
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active) {
            Announcements.getInstance().announceToPlayerByCustomMessage(player, "scripts.events.glitmedal.AnnounceEventStarted", null);
        }
    }

    private void spawnEventManagers() {
        SpawnManager.getInstance().spawn(kH);
        SpawnManager.getInstance().spawn(kI);
    }

    private void unSpawnEventManagers() {
        SpawnManager.getInstance().despawn(kH);
        SpawnManager.getInstance().despawn(kI);
    }

    private static void Y() {
        if (iP) {
            return;
        }
        for (File file : kQ) {
            MultiSellHolder.getInstance().parseFile(file);
        }
        iP = true;
    }

    @Override
    public void onReload() {
        this.unSpawnEventManagers();
        if (iP) {
            for (File file : kQ) {
                MultiSellHolder.getInstance().remove(file);
            }
            iP = false;
        }
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void onDeath(Creature creature, Creature creature2) {
        if (_active && glitmedal.SimpleCheckDrop(creature, creature2)) {
            long l = Util.rollDrop(1L, 1L, Config.EVENT_GLITTMEDAL_NORMAL_CHANCE * creature2.getPlayer().getRateItems() * ((MonsterInstance)creature).getTemplate().rateHp * 10000.0, true);
            if (l > 0L) {
                glitmedal.addItem(creature2.getPlayer(), kK, l);
            }
            if (creature2.getPlayer().getInventory().getCountOf(kP) == 0L && Rnd.chance(Config.EVENT_GLITTMEDAL_GLIT_CHANCE * creature2.getPlayer().getRateItems() * ((MonsterInstance)creature).getTemplate().rateHp)) {
                glitmedal.addItem(creature2.getPlayer(), kL, 1L);
            }
        }
    }

    public void glitchang() {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        if (glitmedal.getItemCount(player, kK) >= 1000L) {
            glitmedal.removeItem(player, kK, 1000L);
            glitmedal.addItem(player, kL, 10L);
            return;
        }
        player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
    }

    public void medal() {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        if (glitmedal.getItemCount(player, kP) >= 1L) {
            this.show("scripts/events/glitmedal/event_col_agent1_q0996_05.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kO) >= 1L) {
            this.show("scripts/events/glitmedal/event_col_agent1_q0996_04.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kN) >= 1L) {
            this.show("scripts/events/glitmedal/event_col_agent1_q0996_03.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kM) >= 1L) {
            this.show("scripts/events/glitmedal/event_col_agent1_q0996_02.htm", player);
            return;
        }
        this.show("scripts/events/glitmedal/event_col_agent1_q0996_01.htm", player);
    }

    public void medalb() {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        if (glitmedal.getItemCount(player, kP) >= 1L) {
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_05.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kO) >= 1L) {
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_04.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kN) >= 1L) {
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_03.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kM) >= 1L) {
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_02.htm", player);
            return;
        }
        this.show("scripts/events/glitmedal/event_col_agent2_q0996_01.htm", player);
    }

    public void game() {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        if (glitmedal.getItemCount(player, kO) >= 1L) {
            if (glitmedal.getItemCount(player, kL) >= 40L) {
                this.show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
                return;
            }
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kN) >= 1L) {
            if (glitmedal.getItemCount(player, kL) >= 20L) {
                this.show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
                return;
            }
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kM) >= 1L) {
            if (glitmedal.getItemCount(player, kL) >= 10L) {
                this.show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
                return;
            }
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kL) >= 5L) {
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
            return;
        }
        this.show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
    }

    public void gamea() {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        this.kJ = Rnd.get(2);
        if (glitmedal.getItemCount(player, kO) >= 1L) {
            if (glitmedal.getItemCount(player, kL) >= 40L) {
                if (this.kJ == 1) {
                    glitmedal.removeItem(player, kO, 1L);
                    glitmedal.removeItem(player, kL, glitmedal.getItemCount(player, kL));
                    glitmedal.addItem(player, kP, 1L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_24.htm", player);
                    return;
                }
                if (this.kJ == 0) {
                    glitmedal.removeItem(player, kL, 40L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                    return;
                }
            }
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kN) >= 1L) {
            if (glitmedal.getItemCount(player, kL) >= 20L) {
                if (this.kJ == 1) {
                    glitmedal.removeItem(player, kN, 1L);
                    glitmedal.removeItem(player, kL, 20L);
                    glitmedal.addItem(player, kO, 1L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_23.htm", player);
                    return;
                }
                if (this.kJ == 0) {
                    glitmedal.removeItem(player, kL, 20L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                    return;
                }
            }
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kM) >= 1L) {
            if (glitmedal.getItemCount(player, kL) >= 10L) {
                if (this.kJ == 1) {
                    glitmedal.removeItem(player, kM, 1L);
                    glitmedal.removeItem(player, kL, 10L);
                    glitmedal.addItem(player, kN, 1L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_22.htm", player);
                    return;
                }
                if (this.kJ == 0) {
                    glitmedal.removeItem(player, kL, 10L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                    return;
                }
            }
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kL) >= 5L) {
            if (this.kJ == 1) {
                glitmedal.removeItem(player, kL, 5L);
                glitmedal.addItem(player, kM, 1L);
                this.show("scripts/events/glitmedal/event_col_agent2_q0996_21.htm", player);
                return;
            }
            if (this.kJ == 0) {
                glitmedal.removeItem(player, kL, 5L);
                this.show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                return;
            }
        }
        this.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
    }

    public void gameb() {
        Player player = this.getSelf();
        if (!player.isQuestContinuationPossible(true)) {
            return;
        }
        this.kJ = Rnd.get(2);
        if (glitmedal.getItemCount(player, kO) >= 1L) {
            if (glitmedal.getItemCount(player, kL) >= 40L) {
                if (this.kJ == 1) {
                    glitmedal.removeItem(player, kO, 1L);
                    glitmedal.removeItem(player, kL, 40L);
                    glitmedal.addItem(player, kP, 1L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_34.htm", player);
                    return;
                }
                if (this.kJ == 0) {
                    glitmedal.removeItem(player, kL, 40L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
                    return;
                }
            }
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kN) >= 1L) {
            if (glitmedal.getItemCount(player, kL) >= 20L) {
                if (this.kJ == 1) {
                    glitmedal.removeItem(player, kN, 1L);
                    glitmedal.removeItem(player, kL, 20L);
                    glitmedal.addItem(player, kO, 1L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_33.htm", player);
                    return;
                }
                if (this.kJ == 0) {
                    glitmedal.removeItem(player, kL, 20L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
                    return;
                }
            }
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kM) >= 1L) {
            if (glitmedal.getItemCount(player, kL) >= 10L) {
                if (this.kJ == 1) {
                    glitmedal.removeItem(player, kM, 1L);
                    glitmedal.removeItem(player, kL, 10L);
                    glitmedal.addItem(player, kN, 1L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_32.htm", player);
                    return;
                }
                if (this.kJ == 0) {
                    glitmedal.removeItem(player, kL, 10L);
                    this.show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
                    return;
                }
            }
            this.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        }
        if (glitmedal.getItemCount(player, kL) >= 5L) {
            if (this.kJ == 1) {
                glitmedal.removeItem(player, kL, 5L);
                glitmedal.addItem(player, kM, 1L);
                this.show("scripts/events/glitmedal/event_col_agent2_q0996_31.htm", player);
                return;
            }
            if (this.kJ == 0) {
                glitmedal.removeItem(player, kL, 5L);
                this.show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
                return;
            }
        }
        this.show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
    }
}


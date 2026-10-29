/*
 * Decompiled with CFR 0.152.
 */
package zones;

import java.util.Collection;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.Experience;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevelLimitZone
implements ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(LevelLimitZone.class);
    private static final String cao = "playerMinLevel";
    private static final String cap = "playerMaxLevel";
    private static final String caq = "playerLevelLimitBackLoc";

    private static void init() {
        int n = 0;
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            boolean bl = zone.getParams().isSet(cao);
            boolean bl2 = zone.getParams().isSet(cap);
            boolean bl3 = zone.getParams().isSet(caq);
            if (bl != bl2) {
                LOG.warn("Min or max level not set for zone " + zone.toString());
            }
            if (!bl || !bl2) continue;
            if (!bl3) {
                LOG.warn("Back coord not set for player level limited zone " + zone);
                continue;
            }
            int n2 = zone.getParams().getInteger(cao, 1);
            int n3 = zone.getParams().getInteger(cap, Experience.getMaxLevel());
            Location location = Location.parseLoc(zone.getParams().getString(caq));
            zone.addListener(new LevelLimitZoneListener(n2, n3, location));
            ++n;
        }
        if (n > 0) LOG.info("LevelLimitZone: Loaded " + n + " player level limit zone(s).");
    }

    @Override
    public void onLoad() {
        LevelLimitZone.init();
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private static class LevelLimitZoneListener
    implements OnZoneEnterLeaveListener {
        private final int car;
        private final int cas;
        private final Location bZJ;

        private LevelLimitZoneListener(int n, int n2, Location location) {
            this.car = n;
            this.cas = n2;
            this.bZJ = location;
        }

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            Object object;
            Playable playable;
            if (creature == null) {
                return;
            }
            if (creature.isPlayer()) {
                playable = creature.getPlayer();
                if (((Player)playable).isGM()) {
                    return;
                }
                if (((Player)playable).getLevel() < this.car || ((Player)playable).getLevel() > this.cas) {
                    ((Player)playable).sendMessage(new CustomMessage("scripts.zones.epic.banishMsg", (Player)playable, new Object[0]));
                    playable.teleToLocation(this.bZJ);
                    object = new CustomMessage("zone.services.LevelLimitZone.CountLimit", (Player)playable, new Object[0]).toString();
                    ((Player)playable).sendPacket((IStaticPacket)new ExShowScreenMessage((String)object, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
                    ((Player)playable).sendMessage((String)object);
                    Summon summon = ((Player)playable).getPet();
                    if (summon != null) {
                        summon.teleToLocation(this.bZJ);
                    }
                }
            }
            if ((creature.isSummon() || creature.isPet()) && ((playable = (Summon)creature).getLevel() < this.car || playable.getLevel() > this.cas)) {
                object = ((Summon)playable).getPlayer();
                ((Summon)playable).unSummon();
                if (object != null) {
                    ((Player)object).sendMessage(new CustomMessage("scripts.zones.epic.banishSummonMsg", (Player)object, new Object[0]));
                }
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
        }
    }
}


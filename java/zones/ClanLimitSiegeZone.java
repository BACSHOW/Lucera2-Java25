/*
 * Decompiled with CFR 0.152.
 */
package zones;

import java.util.Collection;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClanLimitSiegeZone
implements ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(ClanLimitSiegeZone.class);
    private static final String bZH = "clanSiegeLimitBackLoc";
    private static final String bZI = "clanSiegeLimitZone";

    private static void init() {
        int n = 0;
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            if (!zone.getParams().getBool(bZI, false)) continue;
            boolean bl = zone.getParams().isSet(bZH);
            if (!bl) {
                LOG.warn("Back coord not set for clan siege limit zone " + zone);
                continue;
            }
            Location location = Location.parseLoc(zone.getParams().getString(bZH));
            zone.addListener(new ClanLimitSiegeZoneListener(location));
            ++n;
        }
        if (n > 0) LOG.info("Siege Limit Zone: Loaded " + n + " clan siege limit zone(s).");
    }

    @Override
    public void onLoad() {
        ClanLimitSiegeZone.init();
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private static class ClanLimitSiegeZoneListener
    implements OnZoneEnterLeaveListener {
        private final Location bZJ;

        private ClanLimitSiegeZoneListener(Location location) {
            this.bZJ = location;
        }

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            if (creature != null && creature.isPlayer()) {
                Player player = creature.getPlayer();
                if (player.isGM()) {
                    return;
                }
                SiegeEvent siegeEvent = player.getEvent(SiegeEvent.class);
                if (siegeEvent == null || player.getClan() == null) {
                    player.teleToLocation(this.bZJ);
                    String string = new CustomMessage("zone.services.ClanLimitSiegeZone", player, new Object[0]).toString();
                    player.sendPacket((IStaticPacket)new ExShowScreenMessage(string, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
                    player.sendMessage(string);
                    Summon summon = player.getPet();
                    if (summon != null) {
                        summon.teleToLocation(this.bZJ);
                    }
                }
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
        }
    }
}


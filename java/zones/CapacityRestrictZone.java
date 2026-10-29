/*
 * Decompiled with CFR 0.152.
 */
package zones;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapacityRestrictZone
extends Functions
implements ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(CapacityRestrictZone.class);
    private static final CapacityRestrictZone bZr = new CapacityRestrictZone();
    private static final String bZs = "restrictPlayerCapacityValue";
    private static final String bZt = "restrictPlayerCapacityBackLoc";
    private volatile boolean bZu = false;
    private List<CapacityRestrictZoneListener> bZv = new ArrayList<CapacityRestrictZoneListener>();

    private void init() throws Exception {
        if (!this.bZu) {
            for (Zone zone : ReflectionManager.DEFAULT.getZones()) {
                Location location;
                boolean bl = zone.getParams().isSet(bZs);
                if (!bl) continue;
                int n = zone.getParams().getInteger(bZs);
                if (n == 0) {
                    sQ.warn("Capacity restricted for zone {} is 0.", (Object)zone);
                    continue;
                }
                Location location2 = location = zone.getParams().isSet(bZt) ? Location.parseLoc(zone.getParams().getString(bZt)) : null;
                if (location == null) {
                    sQ.warn("Capacity restricted zone {} have no back loc. Will use closest town.", (Object)zone);
                }
                CapacityRestrictZoneListener capacityRestrictZoneListener = new CapacityRestrictZoneListener(n, location);
                capacityRestrictZoneListener.addTo(zone);
                this.bZv.add(capacityRestrictZoneListener);
            }
            this.bZu = true;
            sQ.info("Player limit zone : Loaded {} capacity restricted zones.", (Object)this.bZv.size());
        }
    }

    private void done() throws Exception {
        if (this.bZu) {
            for (CapacityRestrictZoneListener capacityRestrictZoneListener : this.bZv) {
                capacityRestrictZoneListener.remove();
            }
            this.bZv.clear();
            this.bZu = false;
        }
    }

    @Override
    public void onLoad() {
        try {
            bZr.init();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
        try {
            bZr.done();
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static class CapacityRestrictZoneListener
    implements OnZoneEnterLeaveListener {
        private final AtomicInteger bZw;
        private final int bZx;
        private final Location bZy;
        private Zone Cb;

        private CapacityRestrictZoneListener(int n, int n2, Location location) {
            this.bZw = new AtomicInteger(n);
            this.bZx = n2;
            this.bZy = location;
        }

        public CapacityRestrictZoneListener(int n, Location location) {
            this(0, n, location);
        }

        public void addTo(Zone zone) {
            this.bZw.set(zone.getInsidePlayers().size());
            zone.addListener(this);
            this.Cb = zone;
        }

        public boolean remove() {
            return this.Cb.removeListener(this);
        }

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            if (!creature.isPlayer() || creature.getPlayer().isGM()) {
                return;
            }
            HardReference<Player> hardReference = creature.getPlayer().getRef();
            if (this.bZw.incrementAndGet() > this.bZx) {
                ThreadPoolManager.getInstance().execute(() -> {
                    Player player = (Player)hardReference.get();
                    if (player == null) {
                        return;
                    }
                    player.sendMessage(new CustomMessage("zone.playerslimit", player, new Object[0]).addNumber(this.bZx));
                    if (this.bZy == null) {
                        String string = new CustomMessage("zone.services.CapacityRestrictZone.CountLimit", player, new Object[0]).toString();
                        player.sendPacket((IStaticPacket)new ExShowScreenMessage(string, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
                        player.sendMessage(string);
                        player.teleToClosestTown();
                    } else {
                        String string = new CustomMessage("zone.services.CapacityRestrictZone.CountLimit", player, new Object[0]).toString();
                        player.sendPacket((IStaticPacket)new ExShowScreenMessage(string, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
                        player.sendMessage(string);
                        player.teleToLocation(this.bZy);
                    }
                });
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
            if (!creature.isPlayer() || creature.getPlayer().isGM()) {
                return;
            }
            this.bZw.decrementAndGet();
        }
    }
}


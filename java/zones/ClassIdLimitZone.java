/*
 * Decompiled with CFR 0.152.
 */
package zones;

import gnu.trove.TIntHashSet;
import java.util.Collection;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.Zone;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassIdLimitZone
implements ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(ClassIdLimitZone.class);
    private static final String bZK = "playerClassIdsLimit";
    private static final String bZL = "playerClassIdsLimitBackLoc";

    private static void init() {
        int n = 0;
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            boolean bl = zone.getParams().isSet(bZK);
            boolean bl2 = zone.getParams().isSet(bZL);
            if (!bl && !bl2) continue;
            if (!bl) {
                LOG.warn("Class ids not set for zone " + zone.toString());
                continue;
            }
            if (!bl2) {
                LOG.warn("Back location not set for \u0441lassId limit zone " + zone);
                continue;
            }
            int[] nArray = zone.getParams().getIntegerArray(bZK, ArrayUtils.EMPTY_INT_ARRAY);
            Location location = Location.parseLoc(zone.getParams().getString(bZL));
            zone.addListener(new ClassIdLimitZoneListener(nArray, location));
            ++n;
        }
        if (n > 0) LOG.info("ClassIdLimitZone: Loaded " + n + " player class id limit zone(s).");
    }

    @Override
    public void onLoad() {
        ClassIdLimitZone.init();
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private static class ClassIdLimitZoneListener
    implements OnZoneEnterLeaveListener {
        private final TIntHashSet bZM;
        private final Location bZJ;

        private ClassIdLimitZoneListener(int[] nArray, Location location) {
            this.bZM = new TIntHashSet(nArray);
            this.bZJ = location;
        }

        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
            if (creature != null && creature.isPlayer()) {
                Player player = creature.getPlayer();
                if (player.isGM()) {
                    return;
                }
                if (this.bZM.contains(player.getActiveClassId())) {
                    player.sendMessage(new CustomMessage("scripts.zones.epic.banishClassMsg", player, new Object[0]));
                    player.teleToLocation(this.bZJ);
                    String string = new CustomMessage("zone.services.ClassIdLimitZone.ClassLimit", player, new Object[0]).toString();
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


/*
 * Decompiled with CFR 0.152.
 */
package zones;

import gnu.trove.TIntHashSet;
import gnu.trove.TIntIterator;
import java.util.Collection;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveBuffZone
implements OnZoneEnterLeaveListener,
ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(RemoveBuffZone.class);
    private static final String cax = "removeEffects";
    private TIntHashSet cay;

    public RemoveBuffZone() {
        this.cay = new TIntHashSet();
    }

    public RemoveBuffZone(int[] nArray) {
        this.cay = new TIntHashSet(nArray);
    }

    @Override
    public void onLoad() {
        int n = 0;
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            if (zone.getParams().getString(cax, "").isEmpty()) continue;
            zone.addListener(new RemoveBuffZone(zone.getParams().getIntegerArray(cax)));
        }
        if (n > 0) {
            sQ.info("RemoveBuffZone: added {} remove effect zone(s).", (Object)n);
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public void onZoneEnter(Zone zone, Creature creature) {
        if (!creature.isPlayer()) {
            return;
        }
        Player player = creature.getPlayer();
        TIntIterator tIntIterator = this.cay.iterator();
        while (tIntIterator.hasNext()) {
            player.getEffectList().stopEffect(tIntIterator.next());
        }
    }

    @Override
    public void onZoneLeave(Zone zone, Creature creature) {
    }
}


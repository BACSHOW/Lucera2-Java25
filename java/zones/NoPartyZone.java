/*
 * Decompiled with CFR 0.152.
 */
package zones;

import java.util.Collection;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoPartyZone
implements OnZoneEnterLeaveListener,
ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(NoPartyZone.class);
    private static final NoPartyZone cat = new NoPartyZone();
    private static final String cau = "noParty";

    @Override
    public void onLoad() {
        int n = 0;
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            if (!zone.getParams().getBool(cau, false)) continue;
            zone.addListener(cat);
        }
        if (n > 0) {
            sQ.info("NoPartyZone: added {} no party zone(s).", (Object)n);
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            if (!zone.getParams().getBool(cau, false)) continue;
            zone.removeListener(cat);
        }
    }

    @Override
    public void onZoneEnter(Zone zone, Creature creature) {
        if (!creature.isPlayer()) {
            return;
        }
        Player player = creature.getPlayer();
        Party party = player.getParty();
        if (party != null) {
            party.removePartyMember(player, true);
        }
        player.setPartyRefusal(true);
    }

    @Override
    public void onZoneLeave(Zone zone, Creature creature) {
        if (!creature.isPlayer()) {
            return;
        }
        Player player = creature.getPlayer();
        player.setPartyRefusal(false);
    }
}


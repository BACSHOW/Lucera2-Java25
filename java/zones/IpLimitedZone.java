/*
 * Decompiled with CFR 0.152.
 */
package zones;

import java.util.Collection;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpLimitedZone
implements ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(IpLimitedZone.class);
    private static final String cab = "uniqIpLimit";

    @Override
    public void onLoad() {
        int n = 0;
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            int n2 = zone.getParams().getInteger(cab, 0);
            if (n2 <= 0) continue;
            OnZoneEnterLeaveListener onZoneEnterLeaveListener = new OnZoneEnterLeaveListener(){

                @Override
                public void onZoneEnter(Zone zone, Creature creature) {
                    String string;
                    if (creature == null || !creature.isPlayer() || creature.getPlayer().isGM()) {
                        return;
                    }
                    Player player = creature.getPlayer();
                    GameClient gameClient = player.getNetConnection();
                    if (gameClient == null || StringUtils.isBlank(string = gameClient.getIpAddr())) {
                        return;
                    }
                    for (Player player2 : zone.getInsidePlayers()) {
                        String string2;
                        GameClient gameClient2;
                        if (player2 == null || player2 == player || (gameClient2 = player2.getNetConnection()) == null || StringUtils.isBlank(string2 = gameClient2.getIpAddr()) || !StringUtils.equals(string, string2)) continue;
                        String string3 = new CustomMessage("zone.services.IPLimitZone", player, new Object[0]).toString();
                        player.sendPacket((IStaticPacket)new ExShowScreenMessage(string3, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
                        player.teleToClosestTown();
                        return;
                    }
                }

                @Override
                public void onZoneLeave(Zone zone, Creature creature) {
                }
            };
            zone.addListener(onZoneEnterLeaveListener);
            ++n;
        }
        if (n > 0) {
            sQ.info("IpLimitedZone: added {} uniq IP zone(s).", (Object)n);
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
}


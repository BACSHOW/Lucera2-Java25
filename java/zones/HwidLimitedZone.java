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
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HwidLimitedZone
implements ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(HwidLimitedZone.class);
    private static final String bZZ = "uniqHwidLimit";
    private static final String caa = "uniqHwidBackLoc";

    @Override
    public void onLoad() {
        int n = 0;
        Collection<Zone> collection = ReflectionManager.DEFAULT.getZones();
        for (Zone zone : collection) {
            int n2 = zone.getParams().getInteger(bZZ, 0);
            if (n2 <= 0) continue;
            String string = zone.getParams().getString(caa, null);
            final Location location = !StringUtils.isEmpty(string) ? Location.parseLoc(string) : null;
            OnZoneEnterLeaveListener onZoneEnterLeaveListener = new OnZoneEnterLeaveListener(){

                @Override
                public void onZoneEnter(Zone zone, Creature creature) {
                    String string;
                    if (creature == null || !creature.isPlayer() || creature.getPlayer().isGM()) {
                        return;
                    }
                    Player player = creature.getPlayer();
                    GameClient gameClient = player.getNetConnection();
                    if (gameClient == null || StringUtils.isBlank(string = gameClient.getHwid())) {
                        return;
                    }
                    for (Player player2 : zone.getInsidePlayers()) {
                        String string2;
                        GameClient gameClient2;
                        if (player2 == null || player2 == player || (gameClient2 = player2.getNetConnection()) == null || StringUtils.isBlank(string2 = gameClient2.getHwid()) || !StringUtils.equals(string, string2)) continue;
                        String string3 = new CustomMessage("zone.services.HWIDLimitZone", player, new Object[0]).toString();
                        player.sendPacket((IStaticPacket)new ExShowScreenMessage(string3, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
                        if (location != null) {
                            player.teleToLocation(location);
                        } else {
                            player.teleToClosestTown();
                        }
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
            sQ.info("HwidLimitedZone: added {} uniq HWID zone(s).", (Object)n);
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


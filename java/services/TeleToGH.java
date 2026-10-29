/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.ReflectionUtils;

public class TeleToGH
extends Functions
implements ScriptFile {
    private static Zone _zone = ReflectionUtils.getZone("[giran_harbor_offshore]");
    private static ZoneListener bYj;
    private static final String hD;
    private static final String hE;
    private static final String bYk;
    private static final String bYl;

    @Override
    public void onLoad() {
        if (!Config.SERVICES_GIRAN_HARBOR_ENABLED) {
            return;
        }
        ReflectionManager.GIRAN_HARBOR.setCoreLoc(new Location(47416, 186568, -3480));
        bYj = new ZoneListener();
        _zone.addListener(bYj);
        _zone.setReflection(ReflectionManager.GIRAN_HARBOR);
        _zone.setActive(true);
        Zone zone = ReflectionUtils.getZone("[giran_harbor_peace_alt]");
        zone.setReflection(ReflectionManager.GIRAN_HARBOR);
        zone.setActive(true);
        zone = ReflectionUtils.getZone("[giran_harbor_no_trade]");
        zone.setReflection(ReflectionManager.GIRAN_HARBOR);
        zone.setActive(true);
    }

    @Override
    public void onReload() {
        _zone.removeListener(bYj);
    }

    @Override
    public void onShutdown() {
    }

    public void toGH() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (player.getAdena() < (long)Config.SERVICES_GIRAN_HARBOR_PRICE) {
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }
        player.reduceAdena(Config.SERVICES_GIRAN_HARBOR_PRICE, true);
        player.setVar("backCoords", player.getLoc().toXYZString(), -1L);
        player.teleToLocation(Location.findPointToStay(_zone.getSpawn(), 30, 200, ReflectionManager.GIRAN_HARBOR.getGeoIndex()), ReflectionManager.GIRAN_HARBOR);
    }

    public void fromGH() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        if (!NpcInstance.canBypassCheck(player, npcInstance)) {
            return;
        }
        String string = player.getVar("backCoords");
        if (string == null || string.equals("")) {
            this.teleOut();
            return;
        }
        player.teleToLocation(Location.parseLoc(string), 0);
    }

    public void teleOut() {
        Player player = this.getSelf();
        NpcInstance npcInstance = this.getNpc();
        if (player == null || npcInstance == null) {
            return;
        }
        player.teleToLocation(46776, 185784, -3528, 0);
        TeleToGH.show(player.isLangRus() ? "\u042f \u043d\u0435 \u0437\u043d\u0430\u044e, \u043a\u0430\u043a \u0412\u044b \u043f\u043e\u043f\u0430\u043b\u0438 \u0441\u044e\u0434\u0430, \u043d\u043e \u044f \u043c\u043e\u0433\u0443 \u0412\u0430\u0441 \u043e\u0442\u043f\u0440\u0430\u0432\u0438\u0442\u044c \u0437\u0430 \u043e\u0433\u0440\u0430\u0436\u0434\u0435\u043d\u0438\u0435." : "I don't know from where you came here, but I can teleport you the another border side.", player, npcInstance, new Object[0]);
    }

    public String DialogAppend_30059(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30080(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30177(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30233(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30256(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30320(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30848(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30878(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30899(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_31210(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_31275(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_31320(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_31964(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30006(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30134(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30146(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30576(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30540(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String getHtmlAppends(Integer n) {
        if (n != 0 || !Config.SERVICES_GIRAN_HARBOR_ENABLED) {
            return "";
        }
        Player player = this.getSelf();
        if (player == null) {
            return "";
        }
        return player.isLangRus() ? hE : hD;
    }

    public String DialogAppend_40030(Integer n) {
        return this.getHtmlAppends2(n);
    }

    public String getHtmlAppends2(Integer n) {
        if (n != 0 || !Config.SERVICES_GIRAN_HARBOR_ENABLED) {
            return "";
        }
        Player player = this.getSelf();
        if (player == null || player.getReflectionId() != -1) {
            return "";
        }
        return player.isLangRus() ? bYl : bYk;
    }

    static {
        hD = "<br>[scripts_services.TeleToGH:toGH @811;Giran Harbor|\"Move to Giran Harbor (offshore zone) - " + Config.SERVICES_GIRAN_HARBOR_PRICE + " Adena.\"]";
        hE = "<br>[scripts_services.TeleToGH:toGH @811;Giran Harbor|\"Giran Harbor (\u0442\u043e\u0440\u0433\u043e\u0432\u0430\u044f \u0437\u043e\u043d\u0430 \u0431\u0435\u0437 \u043d\u0430\u043b\u043e\u0433\u043e\u0432) - " + Config.SERVICES_GIRAN_HARBOR_PRICE + " Adena.\"]";
        bYk = "Altar Gatekeeper:<center><br>[scripts_services.ManaRegen:DoManaRegen|Full MP Regeneration. (1 MP for " + Config.SERVICES_GIRAN_HARBOR_MP_REG_PRICE + " Adena)]<br>[scripts_services.TeleToGH:fromGH @811;From Giran Harbor|\"Exit the Giran Harbor.\"]<br></center>";
        bYl = "Altar Gatekeeper:<center><br>[scripts_services.ManaRegen:DoManaRegen|\u041f\u043e\u043b\u043d\u043e\u0435 \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u0435 MP. (1 MP \u0437\u0430 " + Config.SERVICES_GIRAN_HARBOR_MP_REG_PRICE + " Adena)]<br>[scripts_services.TeleToGH:fromGH @811;From Giran Harbor|\"\u041f\u043e\u043a\u0438\u043d\u0443\u0442\u044c Giran Harbor.\"]<br></center>";
    }

    public class ZoneListener
    implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature creature) {
        }

        @Override
        public void onZoneLeave(Zone zone, Creature creature) {
            Player player = creature.getPlayer();
            if (player != null && Config.SERVICES_GIRAN_HARBOR_ENABLED && player.getReflection() == ReflectionManager.GIRAN_HARBOR && player.isVisible()) {
                double d = PositionUtils.convertHeadingToDegree(creature.getHeading());
                double d2 = Math.toRadians(d - 90.0);
                creature.teleToLocation((int)((double)creature.getX() + 50.0 * Math.sin(d2)), (int)((double)creature.getY() - 50.0 * Math.cos(d2)), creature.getZ());
            }
        }
    }
}


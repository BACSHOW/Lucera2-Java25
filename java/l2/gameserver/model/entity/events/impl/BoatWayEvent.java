/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.List;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.Config;
import l2.gameserver.data.BoatHolder;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.entity.boat.Boat;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.objects.BoatPoint;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.MapUtils;

public class BoatWayEvent
extends GlobalEvent {
    public static final String BOAT_POINTS = "boat_points";
    private final int QE;
    private final Location PF;
    private final Boat IG;

    public BoatWayEvent(MultiValueSet<String> multiValueSet) {
        super(multiValueSet);
        this.QE = multiValueSet.getInteger("ticketId", 0);
        this.PF = Location.parseLoc(multiValueSet.getString("return_point"));
        String string = multiValueSet.getString("class", null);
        if (string != null) {
            this.IG = BoatHolder.getInstance().initBoat(this.getName(), string);
            Location location = Location.parseLoc(multiValueSet.getString("spawn_point"));
            this.IG.setLoc(location, true);
            this.IG.setHeading(location.h);
        } else {
            this.IG = BoatHolder.getInstance().getBoat(this.getName());
        }
        this.IG.setWay(string != null ? 1 : 0, this);
    }

    @Override
    public void initEvent() {
    }

    @Override
    public void startEvent() {
        L2GameServerPacket l2GameServerPacket = this.IG.startPacket();
        for (Player player : this.IG.getPlayers()) {
            if (this.QE > 0) {
                if (player.consumeItem(this.QE, 1L)) {
                    if (l2GameServerPacket == null) continue;
                    player.sendPacket((IStaticPacket)l2GameServerPacket);
                    continue;
                }
                player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT);
                this.IG.oustPlayer(player, this.PF, true);
                continue;
            }
            if (l2GameServerPacket == null) continue;
            player.sendPacket((IStaticPacket)l2GameServerPacket);
        }
        this.moveNext();
    }

    public void moveNext() {
        List list = this.getObjects(BOAT_POINTS);
        if (this.IG.getRunState() >= list.size()) {
            this.IG.trajetEnded(true);
            return;
        }
        BoatPoint boatPoint = (BoatPoint)list.get(this.IG.getRunState());
        if (boatPoint.getSpeed1() >= 0) {
            this.IG.setMoveSpeed(boatPoint.getSpeed1());
        }
        if (boatPoint.getSpeed2() >= 0) {
            this.IG.setRotationSpeed(boatPoint.getSpeed2());
        }
        if (this.IG.getRunState() == 0) {
            this.IG.broadcastCharInfo();
        }
        this.IG.setRunState(this.IG.getRunState() + 1);
        if (boatPoint.isTeleport()) {
            this.IG.teleportShip(boatPoint.getX(), boatPoint.getY(), boatPoint.getZ());
        } else {
            this.IG.moveToLocation(boatPoint.getX(), boatPoint.getY(), boatPoint.getZ(), 0, false);
        }
    }

    @Override
    public void reCalcNextTime(boolean bl) {
        this.registerActions();
    }

    @Override
    protected long startTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public List<Player> broadcastPlayers(int n) {
        if (n <= 0) {
            ArrayList<Player> arrayList = new ArrayList<Player>();
            int n2 = MapUtils.regionX(this.IG.getX());
            int n3 = MapUtils.regionY(this.IG.getY());
            int n4 = Config.SHOUT_OFFSET;
            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                if (player.getReflection() != this.IG.getReflection()) continue;
                int n5 = MapUtils.regionX(player);
                int n6 = MapUtils.regionY(player);
                if (n5 < n2 - n4 || n5 > n2 + n4 || n6 < n3 - n4 || n6 > n3 + n4) continue;
                arrayList.add(player);
            }
            return arrayList;
        }
        return World.getAroundPlayers(this.IG, n, Math.max(n / 2, 200));
    }

    @Override
    protected void printInfo() {
    }

    public Location getReturnLoc() {
        return this.PF;
    }
}


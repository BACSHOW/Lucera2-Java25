/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.oly;

import java.util.ArrayList;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.oly.Participant;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.InstantZone;
import l2.gameserver.utils.Location;

public class Stadium
extends Reflection {
    public static final int OLYMPIAD_HOST = 36402;
    private int SB;
    private boolean SC = true;
    private static final int[] SD = new int[0];

    public Stadium(int n, int n2) {
        InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(n2);
        this.init(instantZone);
        this.setName("OlyStadium-" + n);
        this.SB = n;
        this.setCollapseIfEmptyTime(0);
        this.SC = true;
    }

    public boolean isFree() {
        return this.SC;
    }

    public void setFree(boolean bl) {
        this.SC = bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        ArrayList<GameObject> arrayList2 = new ArrayList<GameObject>();
        this.lock.lock();
        try {
            for (GameObject gameObject : this._objects) {
                if (gameObject == null || gameObject instanceof DoorInstance || gameObject.isNpc() && ((NpcInstance)gameObject).getNpcId() == 36402) continue;
                if (gameObject.isPlayer() && !gameObject.getPlayer().isOlyObserver()) {
                    arrayList.add((Player)gameObject);
                    continue;
                }
                if (gameObject.isPlayer()) continue;
                arrayList2.add(gameObject);
            }
        }
        finally {
            this.lock.unlock();
        }
        for (Player player : arrayList) {
            if (player.getParty() != null && this.equals(player.getParty().getReflection())) {
                player.getParty().setReflection(null);
            }
            if (!this.equals(player.getReflection())) continue;
            if (this.getReturnLoc() != null) {
                player.teleToLocation(this.getReturnLoc(), 0);
                continue;
            }
            player.teleToClosestTown();
        }
        for (GameObject gameObject : arrayList2) {
            gameObject.deleteMe();
        }
    }

    public final int getStadiumId() {
        return this.SB;
    }

    public Location getLocForParticipant(Participant participant) {
        return Location.findPointToStay(this.getInstancedZone().getTeleportCoords().get(participant.getSide() - 1), 50, 50, this.getGeoIndex());
    }

    public Location getObservingLoc() {
        return this.getInstancedZone().getTeleportCoords().get(this.getInstancedZone().getTeleportCoords().size() - 1).clone();
    }

    public int getObserverCount() {
        int n = 0;
        for (Player player : this.getPlayers()) {
            if (!player.isOlyObserver()) continue;
            ++n;
        }
        return n;
    }

    public void setZonesActive(boolean bl) {
        for (Zone zone : this.getZones()) {
            zone.setActive(bl);
        }
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public int[] getVisitors() {
        return SD;
    }
}


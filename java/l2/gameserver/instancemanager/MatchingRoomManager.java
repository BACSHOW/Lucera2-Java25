/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import l2.gameserver.instancemanager.MapRegionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.templates.mapregion.RestartArea;
import l2.gameserver.templates.mapregion.RestartPoint;
import org.apache.commons.lang3.ArrayUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

public class MatchingRoomManager {
    private static final MatchingRoomManager AA = new MatchingRoomManager();
    private RoomsHolder[] AB = new RoomsHolder[2];
    private Set<Player> _players = new CopyOnWriteArraySet<Player>();

    public static MatchingRoomManager getInstance() {
        return AA;
    }

    public MatchingRoomManager() {
        this.AB[MatchingRoom.PARTY_MATCHING] = new RoomsHolder();
        this.AB[MatchingRoom.CC_MATCHING] = new RoomsHolder();
    }

    public void addToWaitingList(Player player) {
        this._players.add(player);
    }

    public void removeFromWaitingList(Player player) {
        this._players.remove(player);
    }

    public List<Player> getWaitingList(int n, int n2, int[] nArray) {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        for (Player player : this._players) {
            if (player.getLevel() < n || player.getLevel() > n2 || nArray.length != 0 && !ArrayUtils.contains(nArray, player.getClassId().getId())) continue;
            arrayList.add(player);
        }
        return arrayList;
    }

    public List<MatchingRoom> getMatchingRooms(int n, int n2, boolean bl, Player player) {
        ArrayList<MatchingRoom> arrayList = new ArrayList<MatchingRoom>();
        for (MatchingRoom matchingRoom : this.AB[n].AC.values()) {
            if (n2 > 0 && matchingRoom.getLocationId() != n2 || n2 == -2 && matchingRoom.getLocationId() != MatchingRoomManager.getInstance().getLocation(player) || !bl && (matchingRoom.getMinLevel() > player.getLevel() || matchingRoom.getMaxLevel() < player.getLevel())) continue;
            arrayList.add(matchingRoom);
        }
        return arrayList;
    }

    public int addMatchingRoom(MatchingRoom matchingRoom) {
        return this.AB[matchingRoom.getType()].addRoom(matchingRoom);
    }

    public void removeMatchingRoom(MatchingRoom matchingRoom) {
        this.AB[matchingRoom.getType()].AC.remove(matchingRoom.getId());
    }

    public MatchingRoom getMatchingRoom(int n, int n2) {
        return (MatchingRoom)this.AB[n].AC.get(n2);
    }

    public int getLocation(Player player) {
        if (player == null) {
            return 0;
        }
        RestartArea restartArea = MapRegionManager.getInstance().getRegionData(RestartArea.class, player);
        if (restartArea != null) {
            RestartPoint restartPoint = restartArea.getRestartPoint().get((Object)player.getRace());
            return restartPoint.getBbs();
        }
        return 0;
    }

    private class RoomsHolder {
        private int _id = 1;
        private IntObjectMap<MatchingRoom> AC = new CHashIntObjectMap<MatchingRoom>();

        private RoomsHolder() {
        }

        public int addRoom(MatchingRoom matchingRoom) {
            int n = this._id++;
            this.AC.put(n, matchingRoom);
            return n;
        }
    }
}


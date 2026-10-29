/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.pledge.Clan;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

public class CMGSiegeClanObject
extends SiegeClanObject {
    private IntSet QX = new HashIntSet();
    private long QY;

    public CMGSiegeClanObject(String string, Clan clan, long l, long l2) {
        super(string, clan, l, l2);
        this.QY = l;
    }

    public CMGSiegeClanObject(String string, Clan clan, long l) {
        super(string, clan, l);
        this.QY = l;
    }

    public void addPlayer(int n) {
        this.QX.add(n);
    }

    @Override
    public long getParam() {
        return this.QY;
    }

    @Override
    public boolean isParticle(Player player) {
        return this.QX.contains(player.getObjectId());
    }

    @Override
    public void setEvent(boolean bl, SiegeEvent siegeEvent) {
        for (int n : this.QX.toArray()) {
            Player player = GameObjectsStorage.getPlayer(n);
            if (player == null) continue;
            if (bl) {
                player.addEvent(siegeEvent);
            } else {
                player.removeEvent(siegeEvent);
            }
            player.broadcastCharInfo();
        }
    }

    public void setParam(long l) {
        this.QY = l;
    }

    public IntSet getPlayers() {
        return this.QX;
    }
}


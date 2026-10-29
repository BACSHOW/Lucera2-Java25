/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.dao.SiegePlayerDAO;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Clan;

public class CTBSiegeClanObject
extends SiegeClanObject {
    private List<Integer> eM = new ArrayList<Integer>();
    private long QZ;

    public CTBSiegeClanObject(String string, Clan clan, long l, long l2) {
        super(string, clan, l, l2);
        this.QZ = l;
    }

    public CTBSiegeClanObject(String string, Clan clan, long l) {
        this(string, clan, l, System.currentTimeMillis());
    }

    public void select(Residence residence) {
        this.eM.addAll(SiegePlayerDAO.getInstance().select(residence, this.getObjectId()));
    }

    public List<Integer> getPlayers() {
        return this.eM;
    }

    @Override
    public void setEvent(boolean bl, SiegeEvent siegeEvent) {
        for (int n : this.getPlayers()) {
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

    @Override
    public boolean isParticle(Player player) {
        return this.eM.contains(player.getObjectId());
    }

    @Override
    public long getParam() {
        return this.QZ;
    }

    public void setParam(int n) {
        this.QZ = n;
    }
}


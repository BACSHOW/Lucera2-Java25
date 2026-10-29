/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.Comparator;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SiegeClanObject
implements Serializable {
    private String Or;
    private Clan Ia;
    private NpcInstance Re;
    private final long Rj;

    public SiegeClanObject(String string, Clan clan, long l) {
        this(string, clan, 0L, System.currentTimeMillis());
    }

    public SiegeClanObject(String string, Clan clan, long l, long l2) {
        this.Or = string;
        this.Ia = clan;
        this.Rj = l2;
    }

    public int getObjectId() {
        return this.Ia.getClanId();
    }

    public Clan getClan() {
        return this.Ia;
    }

    public NpcInstance getFlag() {
        return this.Re;
    }

    public void deleteFlag() {
        if (this.Re != null) {
            this.Re.deleteMe();
            this.Re = null;
        }
    }

    public void setFlag(NpcInstance npcInstance) {
        this.Re = npcInstance;
    }

    public void setType(String string) {
        this.Or = string;
    }

    public String getType() {
        return this.Or;
    }

    public void broadcast(IStaticPacket ... iStaticPacketArray) {
        this.getClan().broadcastToOnlineMembers(iStaticPacketArray);
    }

    public void broadcast(L2GameServerPacket ... l2GameServerPacketArray) {
        this.getClan().broadcastToOnlineMembers(l2GameServerPacketArray);
    }

    public void setEvent(boolean bl, SiegeEvent siegeEvent) {
        if (bl) {
            for (Player player : this.Ia.getOnlineMembers(0)) {
                player.addEvent(siegeEvent);
                player.broadcastCharInfo();
            }
        } else {
            for (Player player : this.Ia.getOnlineMembers(0)) {
                player.removeEvent(siegeEvent);
                player.broadcastCharInfo();
            }
        }
    }

    public boolean isParticle(Player player) {
        return true;
    }

    public long getParam() {
        return 0L;
    }

    public long getDate() {
        return this.Rj;
    }

    public static class SiegeClanComparatorImpl
    implements Comparator<SiegeClanObject> {
        private static final SiegeClanComparatorImpl Rk = new SiegeClanComparatorImpl();

        public static SiegeClanComparatorImpl getInstance() {
            return Rk;
        }

        @Override
        public int compare(SiegeClanObject siegeClanObject, SiegeClanObject siegeClanObject2) {
            return siegeClanObject2.getParam() < siegeClanObject.getParam() ? -1 : (siegeClanObject2.getParam() == siegeClanObject.getParam() ? 0 : 1);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class CastleSiegeDefenderList
extends L2GameServerPacket {
    public static int OWNER = 1;
    public static int WAITING = 2;
    public static int ACCEPTED = 3;
    public static int REFUSE = 4;
    private int _id;
    private int adU;
    private List<DefenderClan> adW = Collections.emptyList();

    public CastleSiegeDefenderList(Castle castle) {
        this._id = castle.getId();
        this.adU = !((SiegeEvent)castle.getSiegeEvent()).isRegistrationOver() && castle.getOwner() != null ? 1 : 0;
        List list = ((GlobalEvent)castle.getSiegeEvent()).getObjects("defenders");
        List list2 = ((GlobalEvent)castle.getSiegeEvent()).getObjects("defenders_waiting");
        List list3 = ((GlobalEvent)castle.getSiegeEvent()).getObjects("defenders_refused");
        this.adW = new ArrayList<DefenderClan>(list.size() + list2.size() + list3.size());
        if (castle.getOwner() != null) {
            this.adW.add(new DefenderClan(castle.getOwner(), OWNER, 0));
        }
        for (Object __raw_siegeClanObject_37 : (Iterable<?>)(Object)(list)) {
           SiegeClanObject siegeClanObject = (SiegeClanObject)__raw_siegeClanObject_37;
            this.adW.add(new DefenderClan(siegeClanObject.getClan(), ACCEPTED, (int)(siegeClanObject.getDate() / 1000L)));
        }
        for (Object __raw_siegeClanObject_40 : (Iterable<?>)(Object)(list2)) {
           SiegeClanObject siegeClanObject = (SiegeClanObject)__raw_siegeClanObject_40;
            this.adW.add(new DefenderClan(siegeClanObject.getClan(), WAITING, (int)(siegeClanObject.getDate() / 1000L)));
        }
        for (Object __raw_siegeClanObject_43 : (Iterable<?>)(Object)(list3)) {
           SiegeClanObject siegeClanObject = (SiegeClanObject)__raw_siegeClanObject_43;
            this.adW.add(new DefenderClan(siegeClanObject.getClan(), REFUSE, (int)(siegeClanObject.getDate() / 1000L)));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(203);
        this.writeD(this._id);
        this.writeD(0);
        this.writeD(this.adU);
        this.writeD(0);
        this.writeD(this.adW.size());
        this.writeD(this.adW.size());
        for (DefenderClan defenderClan : this.adW) {
            Clan clan = defenderClan.Ia;
            this.writeD(clan.getClanId());
            this.writeS(clan.getName());
            this.writeS(clan.getLeaderName());
            this.writeD(clan.getCrestId());
            this.writeD(defenderClan._time);
            this.writeD(defenderClan._type);
            this.writeD(clan.getAllyId());
            Alliance alliance = clan.getAlliance();
            if (alliance != null) {
                this.writeS(alliance.getAllyName());
                this.writeS(alliance.getAllyLeaderName());
                this.writeD(alliance.getAllyCrestId());
                continue;
            }
            this.writeS("");
            this.writeS("");
            this.writeD(0);
        }
    }

    private static class DefenderClan {
        private Clan Ia;
        private int _type;
        private int _time;

        public DefenderClan(Clan clan, int n, int n2) {
            this.Ia = clan;
            this._type = n;
            this._time = n2;
        }
    }
}


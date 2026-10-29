/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import l2.commons.collections.CollectionUtils;
import l2.commons.collections.JoinedIterator;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.data.xml.holder.InstantZoneHolder;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.World;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.events.impl.DuelEvent;
import l2.gameserver.model.entity.events.objects.DuelSnapshotObject;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExDuelAskStart;
import l2.gameserver.network.l2.s2c.ExDuelEnd;
import l2.gameserver.network.l2.s2c.ExDuelReady;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.InstantZone;

public class PartyVsPartyDuelEvent
extends DuelEvent {
    public PartyVsPartyDuelEvent(MultiValueSet<String> multiValueSet) {
        super(multiValueSet);
    }

    protected PartyVsPartyDuelEvent(int n, String string) {
        super(n, string);
    }

    @Override
    public void stopEvent() {
        this.clearActions();
        if (this._duelState.compareAndSet(DuelEvent.DuelState.EInProgress, DuelEvent.DuelState.EEnd)) {
            Serializable serializable;
            this.updatePlayers(false, false);
            for (Object object : this) {
                ((DuelSnapshotObject)object).getPlayer().sendPacket((IStaticPacket)new ExDuelEnd(this));
                serializable = ((DuelSnapshotObject)object).getPlayer().getTarget();
                if (serializable == null) continue;
                ((DuelSnapshotObject)object).getPlayer().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, serializable);
            }
            switch (this._winner) {
                case NONE: {
                    this.sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
                    break;
                }
                case RED: 
                case BLUE: {
                    Object object;
                    List list = this.getObjects(this._winner.name());
                    object = this.getObjects(this._winner.revert().name());
                    serializable = (DuelSnapshotObject)CollectionUtils.safeGet(list, 0);
                    if (serializable != null) {
                        this.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1S_PARTY_HAS_WON_THE_DUEL).addName(((DuelSnapshotObject)list.get(0)).getPlayer()));
                        Iterator iterator = ((List)object).iterator();
                        while (iterator.hasNext()) {
                            DuelSnapshotObject duelSnapshotObject = (DuelSnapshotObject)iterator.next();
                            duelSnapshotObject.getPlayer().broadcastPacket(new SocialAction(duelSnapshotObject.getPlayer().getObjectId(), 7));
                        }
                        break;
                    }
                    this.sendPacket(SystemMsg.THE_DUEL_HAS_ENDED_IN_A_TIE);
                }
            }
            this.updatePlayers(false, true);
        }
        this.removeObjects(RED_TEAM);
        this.removeObjects(BLUE_TEAM);
    }

    @Override
    public void teleportPlayers(String string) {
        DuelSnapshotObject duelSnapshotObject;
        int n;
        InstantZone instantZone = InstantZoneHolder.getInstance().getInstantZone(1);
        Reflection reflection = new Reflection();
        reflection.init(instantZone);
        List list = this.getObjects(BLUE_TEAM);
        for (n = 0; n < list.size(); ++n) {
            duelSnapshotObject = (DuelSnapshotObject)list.get(n);
            duelSnapshotObject.getPlayer()._stablePoint = duelSnapshotObject.getLoc();
            duelSnapshotObject.getPlayer().teleToLocation(instantZone.getTeleportCoords().get(n), reflection);
        }
        list = this.getObjects(RED_TEAM);
        for (n = 0; n < list.size(); ++n) {
            duelSnapshotObject = (DuelSnapshotObject)list.get(n);
            duelSnapshotObject.getPlayer()._stablePoint = duelSnapshotObject.getLoc();
            duelSnapshotObject.getPlayer().teleToLocation(instantZone.getTeleportCoords().get(9 + n), reflection);
        }
    }

    @Override
    public boolean canDuel(Player player, Player player2, boolean bl) {
        if (player.getParty() == null) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
            return false;
        }
        if (player2.getParty() == null) {
            player.sendPacket((IStaticPacket)SystemMsg.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY);
            return false;
        }
        Party party = player.getParty();
        Party party2 = player2.getParty();
        if (player != party.getPartyLeader() || player2 != party2.getPartyLeader()) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
            return false;
        }
        JoinedIterator joinedIterator = new JoinedIterator(party.iterator(), party2.iterator());
        while (joinedIterator.hasNext()) {
            Player player3 = (Player)joinedIterator.next();
            IStaticPacket iStaticPacket = null;
            iStaticPacket = this.canDuel0(player, player3);
            if (iStaticPacket == null) continue;
            player.sendPacket(iStaticPacket);
            player2.sendPacket(iStaticPacket);
            return false;
        }
        return true;
    }

    @Override
    public void askDuel(Player player, Player player2) {
        Request request = new Request(Request.L2RequestType.DUEL, player, player2).setTimeout(10000L);
        request.set("duelType", 1);
        player.setRequest(request);
        player2.setRequest(request);
        player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL).addName(player2));
        player2.sendPacket(new IStaticPacket[]{new SystemMessage2(SystemMsg.C1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL).addName(player), new ExDuelAskStart(player.getName(), 1)});
    }

    @Override
    public void createDuel(Player player, Player player2) {
        PartyVsPartyDuelEvent partyVsPartyDuelEvent = new PartyVsPartyDuelEvent(this.getDuelType(), player.getObjectId() + "_" + player2.getObjectId() + "_duel");
        this.cloneTo(partyVsPartyDuelEvent);
        for (Player player3 : player.getParty()) {
            partyVsPartyDuelEvent.addObject(BLUE_TEAM, new DuelSnapshotObject(player3, TeamType.BLUE));
        }
        for (Player player3 : player2.getParty()) {
            partyVsPartyDuelEvent.addObject(RED_TEAM, new DuelSnapshotObject(player3, TeamType.RED));
        }
        partyVsPartyDuelEvent.sendPacket(new ExDuelReady(this));
        partyVsPartyDuelEvent.reCalcNextTime(false);
    }

    @Override
    public void playerExit(Player player) {
        for (DuelSnapshotObject duelSnapshotObject : this) {
            List list;
            if (duelSnapshotObject.getPlayer() == player) {
                this.removeObject(duelSnapshotObject.getTeam().name(), duelSnapshotObject);
            }
            if (!(list = this.getObjects(duelSnapshotObject.getTeam().name())).isEmpty()) continue;
            this._winner = duelSnapshotObject.getTeam().revert();
            this.stopEvent();
        }
    }

    @Override
    public void packetSurrender(Player player) {
    }

    @Override
    public void onDie(Player player) {
        TeamType teamType = player.getTeam();
        if (teamType == TeamType.NONE || this._aborted) {
            return;
        }
        this.sendPacket(SystemMsg.THE_OTHER_PARTY_IS_FROZEN, teamType.revert().name());
        player.stopAttackStanceTask();
        player.startFrozen();
        player.setTeam(TeamType.NONE);
        for (Player iterable2 : World.getAroundPlayers(player)) {
            iterable2.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player);
            if (player.getPet() == null) continue;
            iterable2.getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, player.getPet());
        }
        player.sendChanges();
        boolean bl = true;
        List list = this.getObjects(teamType.name());
        for (Object __raw_duelSnapshotObject_191 : (Iterable<?>)(Object)(list)) {
           DuelSnapshotObject duelSnapshotObject = (DuelSnapshotObject)__raw_duelSnapshotObject_191;
            if (duelSnapshotObject.getPlayer() == player) {
                duelSnapshotObject.setDead();
            }
            if (duelSnapshotObject.isDead()) continue;
            bl = false;
        }
        if (bl) {
            this._winner = teamType.revert();
            this.stopEvent();
        }
    }

    @Override
    public int getDuelType() {
        return 1;
    }

    @Override
    protected long startTimeMillis() {
        return System.currentTimeMillis() + 30000L;
    }
}


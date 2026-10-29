/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.impl;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import l2.commons.collections.JoinedIterator;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.impl.ClanHallNpcSiegeEvent;
import l2.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.DuelSnapshotObject;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExDuelStart;
import l2.gameserver.network.l2.s2c.ExDuelUpdateUserInfo;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public abstract class DuelEvent
extends GlobalEvent
implements Iterable<DuelSnapshotObject> {
    public static final String RED_TEAM = TeamType.RED.name();
    public static final String BLUE_TEAM = TeamType.BLUE.name();
    protected OnPlayerExitListener _playerExitListener = new OnPlayerExitListenerImpl();
    protected TeamType _winner = TeamType.NONE;
    protected boolean _aborted;
    protected final AtomicReference<DuelState> _duelState = new AtomicReference<DuelState>(DuelState.EPrepare);

    public DuelEvent(MultiValueSet<String> multiValueSet) {
        super(multiValueSet);
    }

    protected DuelEvent(int n, String string) {
        super(n, string);
    }

    @Override
    public void initEvent() {
    }

    public abstract boolean canDuel(Player var1, Player var2, boolean var3);

    public abstract void askDuel(Player var1, Player var2);

    public abstract void createDuel(Player var1, Player var2);

    public abstract void playerExit(Player var1);

    public abstract void packetSurrender(Player var1);

    public abstract void onDie(Player var1);

    public abstract int getDuelType();

    private boolean cL() {
        if (this._duelState.get() != DuelState.EPrepare) {
            return false;
        }
        for (DuelSnapshotObject duelSnapshotObject : this) {
            Player player = duelSnapshotObject.getPlayer();
            if (player == null) {
                return false;
            }
            IStaticPacket iStaticPacket = this.checkPlayer(player);
            if (iStaticPacket == null) continue;
            this.sendPacket(iStaticPacket);
            this.abortDuel(player);
            return false;
        }
        return true;
    }

    @Override
    public void action(String string, boolean bl) {
        if (string.equalsIgnoreCase("event")) {
            if (bl) {
                if (this.cL()) {
                    this.startEvent();
                }
            } else {
                this.stopEvent();
            }
        }
    }

    @Override
    public void startEvent() {
        if (this._duelState.compareAndSet(DuelState.EPrepare, DuelState.EInProgress)) {
            this.updatePlayers(true, false);
            this.sendPackets(new ExDuelStart(this), PlaySound.B04_S01, SystemMsg.LET_THE_DUEL_BEGIN);
            for (DuelSnapshotObject duelSnapshotObject : this) {
                this.sendPacket(new ExDuelUpdateUserInfo(duelSnapshotObject.getPlayer()), duelSnapshotObject.getTeam().revert().name());
            }
        }
    }

    public void sendPacket(IStaticPacket iStaticPacket, String ... stringArray) {
        for (String string : stringArray) {
            List list = this.getObjects(string);
            for (Object __raw_duelSnapshotObject_111 : (Iterable<?>)(Object)(list)) {
               DuelSnapshotObject duelSnapshotObject = (DuelSnapshotObject)__raw_duelSnapshotObject_111;
                duelSnapshotObject.getPlayer().sendPacket(iStaticPacket);
            }
        }
    }

    public void sendPacket(IStaticPacket iStaticPacket) {
        this.sendPackets(iStaticPacket);
    }

    public void sendPackets(IStaticPacket ... iStaticPacketArray) {
        for (DuelSnapshotObject duelSnapshotObject : this) {
            duelSnapshotObject.getPlayer().sendPacket(iStaticPacketArray);
        }
    }

    public void abortDuel(Player player) {
        this._aborted = true;
        this._winner = TeamType.NONE;
        this.stopEvent();
    }

    protected IStaticPacket checkPlayer(Player player) {
        IStaticPacket iStaticPacket = null;
        if (player.isInCombat()) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE).addName(player);
        } else if (player.isDead() || player.isAlikeDead() || player.getCurrentHpPercents() < 50.0 || player.getCurrentMpPercents() < 50.0 || player.getCurrentCpPercents() < 50.0) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1S_HP_OR_MP_IS_BELOW_50).addName(player);
        } else if (player.getEvent(DuelEvent.class) != null) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL).addName(player);
        } else if (player.getEvent(ClanHallSiegeEvent.class) != null || player.getEvent(ClanHallNpcSiegeEvent.class) != null) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_A_CLAN_HALL_WAR).addName(player);
        } else if (player.getEvent(SiegeEvent.class) != null) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_A_SIEGE_WAR).addName(player);
        } else if (player.isOlyParticipant()) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_THE_OLYMPIAD).addName(player);
        } else if (player.isCursedWeaponEquipped() || player.getKarma() > 0 || player.getPvpFlag() > 0) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_IN_A_CHAOTIC_STATE).addName(player);
        } else if (player.isInStoreMode()) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE).addName(player);
        } else if (player.isMounted() || player.isInBoat()) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_RIDING_A_BOAT_STEED_OR_STRIDER).addName(player);
        } else if (player.isFishing()) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_FISHING).addName(player);
        } else if (player.isInCombatZone() || player.isInPeaceZone() || player.isInWater() || player.isInZone(Zone.ZoneType.no_restart)) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_MAKE_A_CHALLENGE_TO_A_DUEL_BECAUSE_C1_IS_CURRENTLY_IN_A_DUELPROHIBITED_AREA_PEACEFUL_ZONE__SEVEN_SIGNS_ZONE__NEAR_WATER__RESTART_PROHIBITED_AREA).addName(player);
        } else if (player.getTransformation() != 0) {
            iStaticPacket = (IStaticPacket)new SystemMessage2(SystemMsg.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_POLYMORPHED).addName(player);
        }
        return iStaticPacket;
    }

    protected IStaticPacket canDuel0(Player player, Player player2) {
        IStaticPacket iStaticPacket = this.checkPlayer(player2);
        if (iStaticPacket == null && !player.isInRangeZ(player2, 1200L)) {
            iStaticPacket = new SystemMessage2(SystemMsg.C1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_C1_IS_TOO_FAR_AWAY).addName(player2);
        }
        return iStaticPacket;
    }

    protected void updatePlayers(boolean bl, boolean bl2) {
        for (DuelSnapshotObject duelSnapshotObject : this) {
            Player player = duelSnapshotObject.getPlayer();
            if (player == null) continue;
            if (bl2) {
                duelSnapshotObject.teleport();
                continue;
            }
            if (bl) {
                player.addEvent(this);
                player.setTeam(duelSnapshotObject.getTeam());
                continue;
            }
            player.removeEvent(this);
            duelSnapshotObject.restore(this._aborted);
            player.setTeam(TeamType.NONE);
        }
    }

    @Override
    public SystemMsg checkForAttack(Creature creature, Creature creature2, Skill skill, boolean bl) {
        if (creature.getTeam() == TeamType.NONE || creature2.getTeam() == TeamType.NONE || creature.getTeam() == creature2.getTeam()) {
            return SystemMsg.INVALID_TARGET;
        }
        DuelEvent duelEvent = creature.getEvent(DuelEvent.class);
        if (duelEvent == null || duelEvent != this) {
            return SystemMsg.INVALID_TARGET;
        }
        return null;
    }

    @Override
    public boolean canAttack(Creature creature, Creature creature2, Skill skill, boolean bl) {
        if (creature.getTeam() == TeamType.NONE || creature2.getTeam() == TeamType.NONE || creature.getTeam() == creature2.getTeam()) {
            return false;
        }
        DuelEvent duelEvent = creature.getEvent(DuelEvent.class);
        return duelEvent != null && duelEvent == this;
    }

    @Override
    public void onAddEvent(GameObject gameObject) {
        if (gameObject.isPlayer()) {
            gameObject.getPlayer().addListener(this._playerExitListener);
        }
    }

    @Override
    public void onRemoveEvent(GameObject gameObject) {
        if (gameObject.isPlayer()) {
            gameObject.getPlayer().removeListener(this._playerExitListener);
        }
    }

    @Override
    public Iterator<DuelSnapshotObject> iterator() {
        List list = this.getObjects(BLUE_TEAM);
        List list2 = this.getObjects(RED_TEAM);
        return new JoinedIterator<DuelSnapshotObject>(list.iterator(), list2.iterator());
    }

    @Override
    public void reCalcNextTime(boolean bl) {
        this.registerActions();
    }

    @Override
    public void announce(int n) {
        this.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.THE_DUEL_WILL_BEGIN_IN_S1_SECONDS).addInteger(n));
    }

    protected static enum DuelState {
        EPrepare,
        EInProgress,
        EEnd;

    }

    private class OnPlayerExitListenerImpl
    implements OnPlayerExitListener {
        private OnPlayerExitListenerImpl() {
        }

        @Override
        public void onPlayerExit(Player player) {
            DuelEvent.this.playerExit(player);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.oly.participants;

import java.util.concurrent.atomic.AtomicBoolean;
import l2.commons.lang.reference.HardReference;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.entity.oly.Participant;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class SinglePlayerParticipant
extends Participant {
    private final HardReference<Player> e;
    private final int SI;
    private double SJ;
    private AtomicBoolean SK = new AtomicBoolean(true);
    private String _name;

    public SinglePlayerParticipant(int n, Competition competition, Player player) {
        super(n, competition);
        this.e = player.getRef();
        this.SI = player.getActiveClassId();
        this._name = player.getName();
        this.SK.set(true);
    }

    private Player getPlayer() {
        return this.e.get();
    }

    @Override
    public void OnStart() {
        Player player = this.getPlayer();
        if (player != null) {
            player.setOlyParticipant(this);
        }
    }

    @Override
    public void OnFinish() {
        Player player = this.getPlayer();
        if (player != null) {
            player.setOlyParticipant(null);
        }
    }

    @Override
    public boolean OnDamaged(Player player, Creature creature, double d, double d2, double d3) {
        if (!player.isOlyCompetitionStarted()) {
            return true;
        }
        if (d2 - d <= 1.0) {
            player.setCurrentHp(1.0, false);
            if (this.SK.compareAndSet(true, false)) {
                creature.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                creature.abortAttack(true, true);
                if (creature.isCastingNow()) {
                    creature.abortCast(true, false);
                }
                creature.sendActionFailed();
                this.getCompetition().ValidateWinner();
            }
            return false;
        }
        if (creature.isPlayable()) {
            this.SJ += Math.min(d3, d2);
        }
        return true;
    }

    @Override
    public void OnDisconnect(Player player) {
        if (player.isOlyCompetitionFinished()) {
            return;
        }
        this.SK.set(false);
        this.getCompetition().ValidateWinner();
    }

    @Override
    public void sendPacket(L2GameServerPacket l2GameServerPacket) {
        Player player = this.getPlayer();
        if (player != null) {
            player.sendPacket((IStaticPacket)l2GameServerPacket);
        }
    }

    @Override
    public String getName() {
        return this._name;
    }

    @Override
    public boolean isAlive() {
        return this.SK.get();
    }

    @Override
    public boolean isPlayerLoose(Player player) {
        if (player != null && player == this.e.get()) {
            return !this.SK.get();
        }
        return false;
    }

    @Override
    public double getDamageOf(Player player) {
        if (player != null && player == this.e.get()) {
            return this.SJ;
        }
        return 0.0;
    }

    @Override
    public Player[] getPlayers() {
        if (this.getPlayer() != null) {
            return new Player[]{this.getPlayer()};
        }
        return new Player[0];
    }

    @Override
    public double getTotalDamage() {
        return this.SJ;
    }

    @Override
    public boolean validateThis() {
        Participant participant = null;
        for (Participant participant2 : this.getCompetition().getParticipants()) {
            if (participant2 == this) continue;
            participant = participant2;
        }
        if (participant == null) {
            return false;
        }
        Player player = this.e.get();
        if (player == null || !player.isOnline() || player.isLogoutStarted()) {
            participant.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
            return false;
        }
        if (player.isDead()) {
            this.sendPacket(new SystemMessage(1858).addName(player));
            participant.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
            return false;
        }
        if (player.getActiveClassId() != this.SI || !player.getActiveClass().isBase()) {
            player.sendPacket((IStaticPacket)new SystemMessage(1692).addName(player));
            participant.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
            return false;
        }
        if (player.isCursedWeaponEquipped()) {
            this.sendPacket(new SystemMessage(1857).addName(player).addItemName(player.getCursedWeaponEquippedId()));
            participant.sendPacket(new SystemMessage(1856).addItemName(player.getCursedWeaponEquippedId()));
            return false;
        }
        if (!player.isInPeaceZone()) {
            participant.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
            return false;
        }
        SystemMessage systemMessage = Competition.checkPlayer(player);
        if (systemMessage != null) {
            this.sendPacket(systemMessage);
            participant.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
            return false;
        }
        return true;
    }
}


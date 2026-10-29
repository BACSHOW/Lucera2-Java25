/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.oly.participants;

import java.util.ArrayList;
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

public class TeamParticipant
extends Participant {
    private final HardReference<Player>[] SL;
    private final int[] SM;
    private double[] SN;
    private boolean[] SO;
    private String _name = "";

    public TeamParticipant(int n, Competition competition, Player[] playerArray) {
        super(n, competition);
        this.SN = new double[playerArray.length];
        this.SO = new boolean[playerArray.length];
        this.SM = new int[playerArray.length];
        this.SL = new HardReference[playerArray.length];
        this._name = playerArray[0].getName();
        for (int i = 0; i < playerArray.length; ++i) {
            this.SL[i] = playerArray[i].getRef();
            this.SM[i] = playerArray[i].getActiveClassId();
            this.SN[i] = 0.0;
            this.SO[i] = true;
        }
    }

    @Override
    public void OnStart() {
        for (Player player : this.getPlayers()) {
            player.setOlyParticipant(this);
        }
    }

    @Override
    public void OnFinish() {
        for (Player player : this.getPlayers()) {
            if (player.isDead()) {
                player.doRevive(100.0);
            }
            player.setOlyParticipant(null);
        }
    }

    @Override
    public boolean OnDamaged(Player player, Creature creature, double d, double d2, double d3) {
        if (player.isOlyCompetitionFinished()) {
            return false;
        }
        for (int i = 0; i < this.SL.length; ++i) {
            if (!this.SO[i] || this.SL[i].get() != player) continue;
            if (creature.isPlayer()) {
                int n = i;
                this.SN[n] = this.SN[n] + Math.min(d, d2);
            }
            if (!(d2 - d <= 0.5)) continue;
            this.SO[i] = false;
            creature.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            creature.abortAttack(true, true);
            if (creature.isCastingNow()) {
                creature.abortCast(true, false);
            }
            creature.sendActionFailed();
            this.getCompetition().ValidateWinner();
        }
        return true;
    }

    @Override
    public void OnDisconnect(Player player) {
        if (player.isOlyCompetitionFinished()) {
            return;
        }
        for (int i = 0; i < this.SL.length; ++i) {
            this.SO[i] = false;
        }
        this.getCompetition().ValidateWinner();
    }

    @Override
    public void sendPacket(L2GameServerPacket l2GameServerPacket) {
        for (Player player : this.getPlayers()) {
            player.sendPacket((IStaticPacket)l2GameServerPacket);
        }
    }

    @Override
    public String getName() {
        return this._name;
    }

    @Override
    public boolean isAlive() {
        for (boolean bl : this.SO) {
            if (!bl) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isPlayerLoose(Player player) {
        for (int i = 0; i < this.SL.length; ++i) {
            if (this.SL[i].get() != player) continue;
            return !this.SO[i];
        }
        return false;
    }

    @Override
    public double getDamageOf(Player player) {
        for (int i = 0; i < this.SL.length; ++i) {
            if (this.SL[i].get() != player) continue;
            return this.SN[i];
        }
        return 0.0;
    }

    @Override
    public Player[] getPlayers() {
        ArrayList<Player> arrayList = new ArrayList<Player>();
        for (HardReference<Player> hardReference : this.SL) {
            Player player = hardReference.get();
            if (player == null) continue;
            arrayList.add(player);
        }
        return arrayList.toArray(new Player[arrayList.size()]);
    }

    @Override
    public double getTotalDamage() {
        double d = 0.0;
        for (double d2 : this.SN) {
            d += d2;
        }
        return d;
    }

    @Override
    public boolean validateThis() {
        Participant participant = null;
        for (Participant participant2 : this.getCompetition().getParticipants()) {
            if (participant2 == this) continue;
            participant = participant2;
        }
        for (int i = 0; i < this.SL.length; ++i) {
            Player player = this.SL[i].get();
            if (player == null || !player.isOnline() || player.isLogoutStarted()) {
                participant.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
                return false;
            }
            if (player.isDead()) {
                this.sendPacket(new SystemMessage(1858).addName(player));
                participant.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
                return false;
            }
            if (player.getActiveClassId() != this.SM[i] || !player.getActiveClass().isBase()) {
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
            if (systemMessage == null) continue;
            this.sendPacket(systemMessage);
            participant.sendPacket(Msg.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
            return false;
        }
        return true;
    }
}


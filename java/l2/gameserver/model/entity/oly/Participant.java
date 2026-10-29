/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.oly;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public abstract class Participant {
    public static int SIDE_RED = 2;
    public static int SIDE_BLUE = 1;
    private final int Sx;
    private final Competition Sy;

    protected Participant(int n, Competition competition) {
        this.Sx = n;
        this.Sy = competition;
    }

    public final Competition getCompetition() {
        return this.Sy;
    }

    public final int getSide() {
        return this.Sx;
    }

    public abstract void OnStart();

    public abstract void OnFinish();

    public abstract boolean OnDamaged(Player var1, Creature var2, double var3, double var5, double var7);

    public abstract void OnDisconnect(Player var1);

    public abstract void sendPacket(L2GameServerPacket var1);

    public abstract String getName();

    public abstract boolean isAlive();

    public abstract boolean isPlayerLoose(Player var1);

    public abstract double getDamageOf(Player var1);

    public abstract Player[] getPlayers();

    public abstract double getTotalDamage();

    public abstract boolean validateThis();
}


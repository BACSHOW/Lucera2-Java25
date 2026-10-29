/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Creature;

public interface OnKillListener
extends CharListener {
    public void onKill(Creature var1, Creature var2);

    public boolean ignorePetOrSummon();
}


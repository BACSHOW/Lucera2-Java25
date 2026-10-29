/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Creature;

public interface OnDeathListener
extends CharListener {
    public void onDeath(Creature var1, Creature var2);
}


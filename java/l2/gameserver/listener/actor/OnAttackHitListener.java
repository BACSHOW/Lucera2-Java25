/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Creature;

public interface OnAttackHitListener
extends CharListener {
    public void onAttackHit(Creature var1, Creature var2);
}


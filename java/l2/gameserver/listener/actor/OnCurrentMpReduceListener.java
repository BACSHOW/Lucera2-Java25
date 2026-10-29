/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Creature;

public interface OnCurrentMpReduceListener
extends CharListener {
    public void onCurrentMpReduce(Creature var1, double var2, Creature var4);
}


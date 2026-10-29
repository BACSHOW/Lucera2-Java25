/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Creature;
import l2.gameserver.utils.Location;

public interface OnMoveListener
extends CharListener {
    public void onMove(Creature var1, Location var2);
}


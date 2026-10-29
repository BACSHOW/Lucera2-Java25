/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.CharListener;
import l2.gameserver.model.Player;

public interface OnAutoSoulShotListener
extends CharListener {
    public void onAutoSoulShot(Player var1, int var2, boolean var3);
}


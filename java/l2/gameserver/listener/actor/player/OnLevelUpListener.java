/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;

@FunctionalInterface
public interface OnLevelUpListener
extends PlayerListener {
    public void onLevelUp(Player var1, int var2);
}


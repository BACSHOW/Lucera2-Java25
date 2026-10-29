/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.game;

import l2.gameserver.listener.GameListener;

public interface OnDayNightChangeListener
extends GameListener {
    public void onDay();

    public void onNight();
}


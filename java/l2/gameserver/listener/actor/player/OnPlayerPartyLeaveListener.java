/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;

public interface OnPlayerPartyLeaveListener
extends PlayerListener {
    public void onPartyLeave(Player var1);
}


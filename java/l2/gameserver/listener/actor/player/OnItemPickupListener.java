/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;

@FunctionalInterface
public interface OnItemPickupListener
extends PlayerListener {
    public void onItemPickup(Player var1, ItemInstance var2);
}


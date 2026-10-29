/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.player;

import l2.gameserver.listener.PlayerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.oly.Competition;

public interface OnOlyCompetitionListener
extends PlayerListener {
    public void onOlyCompetitionCompleted(Player var1, Competition var2, boolean var3);
}


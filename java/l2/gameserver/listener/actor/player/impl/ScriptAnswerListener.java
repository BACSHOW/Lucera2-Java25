/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.player.impl;

import l2.commons.lang.reference.HardReference;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Scripts;

public class ScriptAnswerListener
implements OnAnswerListener {
    private HardReference<Player> e;
    private String CB;
    private Object[] CC;
    private long AI;

    public ScriptAnswerListener(Player player, String string, Object[] objectArray, long l) {
        this.CB = string;
        this.CC = objectArray;
        this.e = player.getRef();
        this.AI = System.currentTimeMillis() + l;
    }

    @Override
    public void sayYes() {
        Player player = this.e.get();
        if (player == null || System.currentTimeMillis() > this.AI) {
            return;
        }
        Scripts.getInstance().callScripts(player, this.CB.split(":")[0], this.CB.split(":")[1], this.CC);
    }

    @Override
    public void sayNo() {
    }
}


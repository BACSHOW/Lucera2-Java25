/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.instances.player;

import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.gameserver.model.Player;

public class Friend {
    private final int CS;
    private String _name;
    private int CW;
    private int _level;
    private HardReference<Player> e = HardReferences.emptyRef();

    public Friend(int n, String string, int n2, int n3) {
        this.CS = n;
        this._name = string;
        this.CW = n2;
        this._level = n3;
    }

    public Friend(Player player) {
        this.CS = player.getObjectId();
        this.update(player, true);
    }

    public void update(Player player, boolean bl) {
        this._level = player.getLevel();
        this._name = player.getName();
        this.CW = player.getActiveClassId();
        this.e = bl ? player.getRef() : HardReferences.emptyRef();
    }

    public String getName() {
        Player player = this.getPlayer();
        return player == null ? this._name : player.getName();
    }

    public int getObjectId() {
        return this.CS;
    }

    public int getClassId() {
        Player player = this.getPlayer();
        return player == null ? this.CW : player.getActiveClassId();
    }

    public int getLevel() {
        Player player = this.getPlayer();
        return player == null ? this._level : player.getLevel();
    }

    public boolean isOnline() {
        Player player = this.e.get();
        return player != null && !player.isInOfflineMode();
    }

    public Player getPlayer() {
        Player player = this.e.get();
        return player != null && !player.isInOfflineMode() ? player : null;
    }
}


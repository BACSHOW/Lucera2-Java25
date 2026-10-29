/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.listener.actor.player.impl;

import l2.commons.lang.reference.HardReference;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.PetInstance;

public class ReviveAnswerListener
implements OnAnswerListener {
    private HardReference<Player> e;
    private double _power;
    private boolean Cz;
    private final long CA;

    public ReviveAnswerListener(Player player, double d, boolean bl, int n) {
        this.e = player.getRef();
        this.Cz = bl;
        this._power = d;
        this.CA = n > 0 ? System.currentTimeMillis() + (long)n : Long.MAX_VALUE;
    }

    @Override
    public void sayYes() {
        Player player = this.e.get();
        if (player == null) {
            return;
        }
        if (System.currentTimeMillis() > this.CA) {
            return;
        }
        if (!player.isDead() && !this.Cz || this.Cz && player.getPet() != null && !player.getPet().isDead()) {
            return;
        }
        if (!this.Cz) {
            player.doRevive(this._power);
        } else if (player.getPet() != null) {
            ((PetInstance)player.getPet()).doRevive(this._power);
        }
    }

    @Override
    public void sayNo() {
    }

    public double getPower() {
        return this._power;
    }

    public boolean isForPet() {
        return this.Cz;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ChangeWaitType;
import l2.gameserver.network.l2.s2c.Revive;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectFakeDeath
extends Effect {
    public static final int FAKE_DEATH_OFF = 0;
    public static final int FAKE_DEATH_ON = 1;
    public static final int FAKE_DEATH_FAILED = 2;
    private final int aoI;

    public EffectFakeDeath(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
        this.aoI = effectTemplate.getParam().getInteger("failChance", 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        Player player = (Player)this.getEffected();
        player.abortAttack(true, false);
        if (player.isMoving()) {
            player.stopMove();
        }
        if (this.aoI > 0 && Rnd.chance(this.aoI)) {
            player.setFakeDeath(2);
        } else {
            player.setFakeDeath(1);
            player.getAI().notifyEvent(CtrlEvent.EVT_FAKE_DEATH, null, null);
        }
        player.broadcastPacket(new ChangeWaitType(player, 2));
        player.broadcastCharInfo();
    }

    @Override
    public void onExit() {
        super.onExit();
        Player player = (Player)this.getEffected();
        player.setNonAggroTime(System.currentTimeMillis() + 5000L);
        player.setFakeDeath(0);
        player.broadcastPacket(new ChangeWaitType(player, 3));
        player.broadcastPacket(new Revive(player));
        player.broadcastCharInfo();
    }

    @Override
    public boolean onActionTime() {
        if (this.getEffected().isDead()) {
            return false;
        }
        double d = this.calc();
        if (d > this.getEffected().getCurrentMp() && this.getSkill().isToggle()) {
            this.getEffected().sendPacket((IStaticPacket)Msg.NOT_ENOUGH_MP);
            this.getEffected().sendPacket((IStaticPacket)new SystemMessage(749).addSkillName(this.getSkill().getId(), this.getSkill().getDisplayLevel()));
            return false;
        }
        this.getEffected().reduceCurrentMp(d, null);
        return true;
    }

    @Override
    public boolean checkCondition() {
        if (this.getEffected().isPlayer() && this.getEffected().getPlayer().getPrivateStoreType() != 0) {
            return false;
        }
        return super.checkCondition();
    }
}


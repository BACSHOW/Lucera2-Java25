/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances.residences;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.SpecialEffectState;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.npc.NpcTemplate;

public class SiegeFlagInstance
extends NpcInstance {
    private SiegeClanObject Wt;
    private long Wu = 0L;

    public SiegeFlagInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this.setUndying(SpecialEffectState.FALSE);
        this.setHasChatWindow(false);
    }

    @Override
    public String getName() {
        return this.Wt.getClan().getName();
    }

    @Override
    public Clan getClan() {
        return this.Wt.getClan();
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        Player player = creature.getPlayer();
        if (player == null || this.isInvul()) {
            return false;
        }
        Clan clan = player.getClan();
        return clan == null || this.Wt.getClan() != clan;
    }

    @Override
    public boolean isAttackable(Creature creature) {
        return true;
    }

    @Override
    protected void onDeath(Creature creature) {
        this.Wt.setFlag(null);
        super.onDeath(creature);
    }

    @Override
    protected void onReduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3) {
        if (System.currentTimeMillis() - this.Wu > 120000L) {
            this.Wu = System.currentTimeMillis();
            this.Wt.getClan().broadcastToOnlineMembers(SystemMsg.YOUR_BASE_IS_BEING_ATTACKED);
        }
        super.onReduceCurrentHp(d, creature, skill, bl, bl2, bl3);
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean isInvul() {
        return this._isInvul;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public boolean isHealBlocked() {
        return true;
    }

    @Override
    public boolean isEffectImmune() {
        return true;
    }

    public void setClan(SiegeClanObject siegeClanObject) {
        this.Wt = siegeClanObject;
    }
}


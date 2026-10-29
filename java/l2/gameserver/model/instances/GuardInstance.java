/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.SpecialEffectState;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class GuardInstance
extends NpcInstance {
    public GuardInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this.setUndying(SpecialEffectState.FALSE);
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        return creature.isMonster() && ((MonsterInstance)creature).isAggressive() || creature.isPlayable() && creature.getKarma() > 0;
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = n2 == 0 ? "" + n : n + "-" + n2;
        return "guard/" + string + ".htm";
    }

    @Override
    public boolean isInvul() {
        return false;
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
    protected void onReduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3) {
        this.getAggroList().addDamageHate(creature, (int)d, 0);
        super.onReduceCurrentHp(d, creature, skill, bl, bl2, bl3);
    }
}


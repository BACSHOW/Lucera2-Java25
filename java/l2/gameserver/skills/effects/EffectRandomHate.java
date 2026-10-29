/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.model.AggroList;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public class EffectRandomHate
extends Effect {
    public EffectRandomHate(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public boolean checkCondition() {
        return this.getEffected().isMonster() && Rnd.chance(this._template.chance(100));
    }

    @Override
    public void onStart() {
        MonsterInstance monsterInstance = (MonsterInstance)this.getEffected();
        Creature creature = monsterInstance.getAggroList().getMostHated();
        if (creature == null) {
            return;
        }
        AggroList.AggroInfo aggroInfo = monsterInstance.getAggroList().get(creature);
        List<Creature> list = monsterInstance.getAggroList().getHateList(monsterInstance.getAggroRange());
        list.remove(creature);
        if (!list.isEmpty()) {
            AggroList.AggroInfo aggroInfo2 = monsterInstance.getAggroList().get(list.get(Rnd.get(list.size())));
            int n = aggroInfo2.hate;
            aggroInfo2.hate = aggroInfo.hate;
            aggroInfo.hate = n;
        }
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import l2.gameserver.model.Effect;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;

public final class EffectGrow
extends Effect {
    public EffectGrow(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this._effected.isNpc()) {
            NpcInstance npcInstance = (NpcInstance)this._effected;
            npcInstance.setCollisionHeight(npcInstance.getCollisionHeight() * 1.24);
            npcInstance.setCollisionRadius(npcInstance.getCollisionRadius() * 1.19);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (this._effected.isNpc()) {
            NpcInstance npcInstance = (NpcInstance)this._effected;
            npcInstance.setCollisionHeight(npcInstance.getTemplate().collisionHeight);
            npcInstance.setCollisionRadius(npcInstance.getTemplate().collisionRadius);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}


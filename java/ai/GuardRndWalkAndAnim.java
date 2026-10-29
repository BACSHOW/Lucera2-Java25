/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Guard;
import l2.gameserver.model.instances.NpcInstance;

public class GuardRndWalkAndAnim
extends Guard {
    public GuardRndWalkAndAnim(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected boolean thinkActive() {
        if (super.thinkActive()) {
            return true;
        }
        if (this.randomAnimation()) {
            return true;
        }
        return this.randomWalk();
    }
}


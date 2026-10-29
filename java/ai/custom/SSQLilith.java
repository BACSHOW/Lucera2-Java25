/*
 * Decompiled with CFR 0.152.
 */
package ai.custom;

import l2.commons.util.Rnd;
import l2.gameserver.ai.Mystic;
import l2.gameserver.model.Creature;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.scripts.Functions;

public class SSQLilith
extends Mystic {
    private final String[] dB = new String[]{"You, such a fool! The victory over this war belongs to Shilen!!!", "How dare you try to contend against me in strength? Ridiculous.", "Anakim! In the name of Great Shilien, I will cut your throat!", "You cannot be the match of Lilith. I'll teach you a lesson!"};
    private long dD = 0L;
    private long dF = 0L;

    public SSQLilith(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.setHasChatWindow(false);
    }

    @Override
    protected boolean thinkActive() {
        if (this.dD < System.currentTimeMillis()) {
            Functions.npcSay(this.getActor(), this.dB[Rnd.get(this.dB.length)]);
            this.dD = System.currentTimeMillis() + 15000L;
        }
        if (this.dF < System.currentTimeMillis()) {
            Reflection reflection = this.getActor().getReflection();
            if (reflection != null) {
                NpcInstance npcInstance = null;
                for (NpcInstance npcInstance2 : reflection.getNpcs()) {
                    if (npcInstance2.getNpcId() != 32718) continue;
                    npcInstance = npcInstance2;
                    break;
                }
                if (npcInstance != null) {
                    this.getActor().broadcastPacket(new MagicSkillUse(this.getActor(), npcInstance, 6187, 1, 5000, 10L));
                }
            }
            this.dF = System.currentTimeMillis() + 6500L;
        }
        return true;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
    }

    @Override
    protected void onEvtAggression(Creature creature, int n) {
    }
}


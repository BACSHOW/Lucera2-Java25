/*
 * Decompiled with CFR 0.152.
 */
package ai.custom;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.ai.Mystic;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.scripts.Functions;

public class SSQAnakim
extends Mystic {
    private static final String dA = "%playerName%";
    private static final String[] dB = new String[]{"For the eternity of Einhasad!!!", "Dear Shillien's offspring! You are not capable of confronting us!", "I'll show you the real power of Einhasad!", "Dear Military Force of Light! Go destroy the offspring of Shillien!!!"};
    private static final String[] dC = new String[]{"My power's weakening.. Hurry and turn on the sealing device!!!", "All 4 sealing devices must be turned on!!!", "Lilith's attack is getting stronger! Go ahead and turn it on!", "%playerName%, hold on. We're almost done!"};
    private long dD = 0L;
    private long dE = 0L;
    private long dF = 0L;

    public SSQAnakim(NpcInstance npcInstance) {
        super(npcInstance);
        npcInstance.setHasChatWindow(false);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
    }

    @Override
    protected boolean thinkActive() {
        if (this.dD < System.currentTimeMillis()) {
            Functions.npcSay(this.getActor(), dB[Rnd.get(dB.length)]);
            this.dD = System.currentTimeMillis() + 12000L;
        }
        if (this.dE < System.currentTimeMillis()) {
            Player player = this.getPlayer();
            if (player != null) {
                String string = dC[Rnd.get(dC.length)];
                if (string.contains(dA)) {
                    string = string.replace(dA, player.getName());
                }
                Functions.npcSayToPlayer(this.getActor(), player, string);
            }
            this.dE = System.currentTimeMillis() + 20000L;
        }
        if (this.dF < System.currentTimeMillis()) {
            if (this.p() != null) {
                this.getActor().broadcastPacket(new MagicSkillUse(this.getActor(), this.p(), 6191, 1, 5000, 10L));
            }
            this.dF = System.currentTimeMillis() + 6500L;
        }
        return true;
    }

    private NpcInstance p() {
        List<NpcInstance> list = this.getActor().getAroundNpc(1000, 300);
        if (list != null && !list.isEmpty()) {
            for (NpcInstance npcInstance : list) {
                if (npcInstance.getNpcId() != 32715) continue;
                return npcInstance;
            }
        }
        return null;
    }

    private Player getPlayer() {
        Reflection reflection = this.getActor().getReflection();
        if (reflection == null) {
            return null;
        }
        List<Player> list = reflection.getPlayers();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
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


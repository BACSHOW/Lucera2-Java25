/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.commons.collections.LazyArrayList;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.model.MinionList;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;

public class Kama56Boss
extends Fighter {
    private long cn = 0L;
    private HardReference<Player> co = HardReferences.emptyRef();

    public Kama56Boss(NpcInstance npcInstance) {
        super(npcInstance);
    }

    private void b(NpcInstance npcInstance) {
        Iterable<Player> iterable;
        if (!npcInstance.isInCombat()) {
            this.co = HardReferences.emptyRef();
            return;
        }
        MinionList minionList = npcInstance.getMinionList();
        if (minionList == null || !minionList.hasMinions()) {
            this.co = HardReferences.emptyRef();
            return;
        }
        long l = System.currentTimeMillis();
        if (this.cn > l && this.co.get() != null && (iterable = this.co.get()) != null && !((Player)iterable).isAlikeDead()) {
            for (MinionInstance minionInstance : minionList.getAliveMinions()) {
                if (minionInstance.getAI().getAttackTarget() == iterable) continue;
                minionInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, iterable, 10000000);
            }
            return;
        }
        this.cn = l + 30000L;
        iterable = World.getAroundPlayers(npcInstance);
        if (!iterable.iterator().hasNext()) {
            this.co = HardReferences.emptyRef();
            return;
        }
        LazyArrayList<Player> lazyArrayList = new LazyArrayList<Player>();
        for (Player object : iterable) {
            if (object.isAlikeDead()) continue;
            lazyArrayList.add(object);
        }
        if (lazyArrayList.isEmpty()) {
            this.co = HardReferences.emptyRef();
            return;
        }
        Player player = (Player)lazyArrayList.get(Rnd.get(lazyArrayList.size()));
        this.co = player.getRef();
        Functions.npcSayCustomMessage(npcInstance, "Kama56Boss.attack", player.getName());
        for (MinionInstance minionInstance : minionList.getAliveMinions()) {
            minionInstance.getAggroList().clear();
            minionInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 10000000);
        }
    }

    @Override
    protected void thinkAttack() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance == null) {
            return;
        }
        this.b(npcInstance);
        super.thinkAttack();
    }
}


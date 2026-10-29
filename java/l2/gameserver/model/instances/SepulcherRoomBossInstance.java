/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.instancemanager.sepulchers.FourSepulchers;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.SepulcherMonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SepulcherRoomBossInstance
extends SepulcherMonsterInstance {
    private static final int cN = 31455;
    private static final Logger sQ = LoggerFactory.getLogger(SepulcherRoomBossInstance.class);

    public SepulcherRoomBossInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    protected void onDeath(Creature creature) {
        try {
            super.onDeath(creature);
            FourSepulchers.getInstance().log("Boss death: " + this + ". Spawning keybox " + 31455);
            this.define.getHandler().spawnCustomSingle(31455, this.getLoc());
        }
        catch (Exception exception) {
            sQ.error(exception.getMessage(), exception);
        }
    }
}


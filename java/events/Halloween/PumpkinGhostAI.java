/*
 * Decompiled with CFR 0.152.
 */
package events.Halloween;

import java.util.concurrent.atomic.AtomicInteger;
import l2.commons.util.Rnd;
import l2.gameserver.ai.DefaultAI;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;

public class PumpkinGhostAI
extends DefaultAI {
    private Location[] hI;
    private int hJ;
    private int cJ;
    private int[] hK;
    private static String[] hL = new String[]{"PumpkinGhostAI.SongText1", "PumpkinGhostAI.SongText2", "PumpkinGhostAI.SongText3", "PumpkinGhostAI.SongText4", "PumpkinGhostAI.SongText5", "PumpkinGhostAI.SongText6", "PumpkinGhostAI.SongText7", "PumpkinGhostAI.SongText8", "PumpkinGhostAI.SongText9", "PumpkinGhostAI.SongText10", "PumpkinGhostAI.SongText11", "PumpkinGhostAI.SongText12", "PumpkinGhostAI.SongText13", "PumpkinGhostAI.SongText14", "PumpkinGhostAI.SongText15", "PumpkinGhostAI.SongText16", "PumpkinGhostAI.SongText17", "PumpkinGhostAI.SongText18", "PumpkinGhostAI.SongText19", "PumpkinGhostAI.SongText20", "PumpkinGhostAI.SongText21", "PumpkinGhostAI.SongText22", "PumpkinGhostAI.SongText23", "PumpkinGhostAI.SongText24", "PumpkinGhostAI.SongText25", "PumpkinGhostAI.SongText26", "PumpkinGhostAI.SongText27", "PumpkinGhostAI.SongText28", "PumpkinGhostAI.SongText29", "PumpkinGhostAI.SongText30", "PumpkinGhostAI.SongText31", "PumpkinGhostAI.SongText32", "PumpkinGhostAI.SongText33", "PumpkinGhostAI.SongText34", "PumpkinGhostAI.SongText35", "PumpkinGhostAI.SongText36", "PumpkinGhostAI.SongText37", "PumpkinGhostAI.SongText38", "PumpkinGhostAI.SongText39", "PumpkinGhostAI.SongText40", "PumpkinGhostAI.SongText41", "PumpkinGhostAI.SongText42", "PumpkinGhostAI.SongText43", "PumpkinGhostAI.SongText44", "PumpkinGhostAI.SongText45", "PumpkinGhostAI.SongText46", "PumpkinGhostAI.SongText47", "PumpkinGhostAI.SongText48"};
    private static AtomicInteger hM = new AtomicInteger(0);
    private static final int hN = 2024;
    private long hO = System.currentTimeMillis();

    public PumpkinGhostAI(NpcInstance npcInstance, Location[] locationArray, int n, int n2, int[] nArray) {
        super(npcInstance);
        this.hI = locationArray;
        this.hJ = n + 1;
        if (this.hJ >= this.hI.length) {
            this.hJ = 0;
        }
        this.cJ = n2;
        this.hK = nArray;
    }

    @Override
    protected void onEvtSpawn() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance == null) {
            return;
        }
        npcInstance.setWalking();
        this.addTaskMove(this.hI[this.hJ], false);
        super.onEvtSpawn();
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance == null || npcInstance.isDead()) {
            return true;
        }
        if (this._def_think) {
            return this.doTask();
        }
        long l = System.currentTimeMillis();
        if (l - this.hO > 3000L) {
            if (Rnd.chance(30)) {
                npcInstance.broadcastPacket(new MagicSkillUse(npcInstance, npcInstance, 2024, 1, 0, 0L), new MagicSkillLaunched((Creature)npcInstance, 2024, 1, npcInstance));
            } else if (this.hJ == 0 && Rnd.chance(40)) {
                Functions.npcSayInRangeCustomMessage(this.getActor(), 300, hL[hM.getAndIncrement() % hL.length], new Object[0]);
            } else if (Rnd.chance(this.cJ)) {
                ItemInstance itemInstance = new ItemInstance(IdFactory.getInstance().getNextId(), this.hK[Rnd.get(this.hK.length)]);
                itemInstance.setCount(1L);
                itemInstance.dropToTheGround(npcInstance, Location.coordsRandomize(npcInstance.getLoc(), 10, 50));
            }
            if (this.V()) {
                this.addTaskMove(this.hI[this.hJ], false);
            }
            this.hO = l;
        }
        return true;
    }

    private boolean V() {
        NpcInstance npcInstance = this.getActor();
        if (npcInstance == null || npcInstance.isDead()) {
            return false;
        }
        return npcInstance.getLoc().distance(this.hI[this.hJ]) > 512.0;
    }

    @Override
    protected void onEvtArrived() {
        ++this.hJ;
        if (this.hJ >= this.hI.length) {
            this.hJ = 0;
        }
        this.addTaskMove(this.hI[this.hJ], false);
        super.onEvtArrived();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        if (creature != null) {
            SkillTable.getInstance().getInfo(4515, 1).getEffects(creature, creature, false, false);
        }
        this.doTask();
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}


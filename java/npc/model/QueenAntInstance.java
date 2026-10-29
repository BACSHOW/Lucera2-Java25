/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import java.util.ArrayList;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.SimpleSpawner;
import l2.gameserver.model.instances.BossInstance;
import l2.gameserver.model.instances.MinionInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;

public class QueenAntInstance
extends BossInstance {
    private static final int ayf = 29002;
    private List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();
    private NpcInstance ayg = null;

    public QueenAntInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    public NpcInstance getLarva() {
        if (this.ayg == null) {
            this.ayg = this.b(29002, new Location(-21600, 179482, -5846, Rnd.get(0, 65535)));
        }
        return this.ayg;
    }

    @Override
    protected int getKilledInterval(MinionInstance minionInstance) {
        return minionInstance.getNpcId() == 29003 ? 130000 : 130000;
    }

    @Override
    protected void onDeath(Creature creature) {
        this.broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, this.getLoc()));
        Functions.deSpawnNPCs(this._spawns);
        this.ayg = null;
        super.onDeath(creature);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        this.getLarva();
        this.broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS01_A", 1, 0, this.getLoc()));
    }

    private NpcInstance b(int n, Location location) {
        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(n);
        if (npcTemplate == null) {
            System.out.println("WARNING! template is null for npc: " + n);
            Thread.dumpStack();
            return null;
        }
        try {
            SimpleSpawner simpleSpawner = new SimpleSpawner(npcTemplate);
            simpleSpawner.setLoc(location);
            simpleSpawner.setAmount(1);
            simpleSpawner.setRespawnDelay(0);
            this._spawns.add(simpleSpawner);
            return simpleSpawner.spawnOne();
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}


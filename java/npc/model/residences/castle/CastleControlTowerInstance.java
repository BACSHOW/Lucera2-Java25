/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.castle;

import java.util.HashSet;
import java.util.Set;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class CastleControlTowerInstance
extends SiegeToggleNpcInstance {
    private Set<Spawner> aym = new HashSet<Spawner>();

    public CastleControlTowerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onDeathImpl(Creature creature) {
        for (Spawner spawner : this.aym) {
            spawner.stopRespawn();
        }
        this.aym.clear();
    }

    @Override
    public void register(Spawner spawner) {
        this.aym.add(spawner);
    }
}


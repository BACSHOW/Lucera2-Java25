/*
 * Decompiled with CFR 0.152.
 */
package ai;

import l2.gameserver.ai.Fighter;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;

public class ImperialGravekeeper
extends Fighter {
    private int ck;
    private int cl;
    private int cm;

    public ImperialGravekeeper(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    protected void onEvtSpawn() {
        this.ck = 1;
        this.cl = 50;
        this.cm = 80;
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtAttacked(Creature creature, int n) {
        NpcInstance npcInstance = this.getActor();
        if (!npcInstance.isInZonePeace()) {
            if (npcInstance.getCurrentHpPercents() <= (double)this.cl) {
                if (this.ck == 1 || this.ck == 3) {
                    this.ck = 2;
                    for (Player player : World.getAroundPlayers(npcInstance, 200, 200)) {
                        if (player == null) continue;
                        player.teleToLocation(179520, 6464, -2706);
                    }
                } else {
                    this.ck = 3;
                    for (Player player : World.getAroundPlayers(npcInstance, 200, 200)) {
                        if (player == null) continue;
                        player.teleToLocation(171104, 6496, -2706);
                    }
                }
            }
            if (this.cl == 50) {
                this.cl = 30;
            } else if (this.cl == 30) {
                this.cl = -1;
            }
        }
        if (npcInstance.getCurrentHpPercents() <= (double)this.cm) {
            this.cm = this.cm == 80 ? 40 : (this.cm == 40 ? 20 : -1);
            NpcUtils.spawnSingle(27180, Location.findAroundPosition(npcInstance, 150), npcInstance.getReflection(), 120000L);
            NpcUtils.spawnSingle(27180, Location.findAroundPosition(npcInstance, 150), npcInstance.getReflection(), 120000L);
            NpcUtils.spawnSingle(27180, Location.findAroundPosition(npcInstance, 150), npcInstance.getReflection(), 120000L);
            NpcUtils.spawnSingle(27180, Location.findAroundPosition(npcInstance, 150), npcInstance.getReflection(), 120000L);
        }
        if (npcInstance.getCurrentHpPercents() > 50.0) {
            this.cl = 50;
        } else if (npcInstance.getCurrentHpPercents() > 30.0) {
            this.cl = 30;
        }
        if (npcInstance.getCurrentHpPercents() > 80.0) {
            this.cm = 80;
        } else if (npcInstance.getCurrentHpPercents() > 40.0) {
            this.cm = 40;
        } else if (npcInstance.getCurrentHpPercents() > 20.0) {
            this.cm = 20;
        }
        super.onEvtAttacked(creature, n);
    }
}


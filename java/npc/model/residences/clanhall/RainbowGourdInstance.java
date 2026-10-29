/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import java.util.List;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Spawner;
import l2.gameserver.model.World;
import l2.gameserver.model.base.SpecialEffectState;
import l2.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2.gameserver.model.entity.events.objects.SpawnExObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.NpcUtils;
import npc.model.residences.clanhall.RainbowYetiInstance;

public class RainbowGourdInstance
extends NpcInstance {
    private CMGSiegeClanObject azg;

    public RainbowGourdInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this.setHasChatWindow(false);
        this.setUndying(SpecialEffectState.FALSE);
    }

    public void doDecrease(Creature creature) {
        if (this.isDead()) {
            return;
        }
        this.reduceCurrentHp((double)this.getMaxHp() * 0.2, creature, null, false, false, false, false, false, false, false);
    }

    public void doHeal() {
        if (this.isDead()) {
            return;
        }
        this.setCurrentHp(this.getCurrentHp() + (double)this.getMaxHp() * 0.2, false);
    }

    public void doSwitch(RainbowGourdInstance rainbowGourdInstance) {
        if (this.isDead() || rainbowGourdInstance.isDead()) {
            return;
        }
        double d = this.getCurrentHp();
        this.setCurrentHp(rainbowGourdInstance.getCurrentHp(), false);
        rainbowGourdInstance.setCurrentHp(d, false);
    }

    @Override
    public void onDeath(Creature creature) {
        super.onDeath(creature);
        ClanHallMiniGameEvent clanHallMiniGameEvent = this.getEvent(ClanHallMiniGameEvent.class);
        if (clanHallMiniGameEvent == null) {
            return;
        }
        Player player = creature.getPlayer();
        CMGSiegeClanObject cMGSiegeClanObject = (CMGSiegeClanObject)clanHallMiniGameEvent.getSiegeClan("attackers", player.getClan());
        if (cMGSiegeClanObject == null) {
            return;
        }
        this.azg = cMGSiegeClanObject;
        List list = clanHallMiniGameEvent.getObjects("attackers");
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) == cMGSiegeClanObject) continue;
            String string = "arena_" + i;
            SpawnExObject spawnExObject = (SpawnExObject)clanHallMiniGameEvent.getFirstObject(string);
            RainbowYetiInstance rainbowYetiInstance = (RainbowYetiInstance)spawnExObject.getSpawns().get(0).getFirstSpawned();
            rainbowYetiInstance.teleportFromArena();
            clanHallMiniGameEvent.spawnAction(string, false);
        }
    }

    @Override
    public void onDecay() {
        super.onDecay();
        final ClanHallMiniGameEvent clanHallMiniGameEvent = this.getEvent(ClanHallMiniGameEvent.class);
        if (clanHallMiniGameEvent == null) {
            return;
        }
        if (this.azg == null) {
            return;
        }
        List list = clanHallMiniGameEvent.getObjects("attackers");
        int n = list.indexOf(this.azg);
        String string = "arena_" + n;
        clanHallMiniGameEvent.spawnAction(string, false);
        SpawnExObject spawnExObject = (SpawnExObject)clanHallMiniGameEvent.getFirstObject(string);
        Spawner spawner = spawnExObject.getSpawns().get(0);
        Location location = (Location)spawner.getCurrentSpawnRange();
        clanHallMiniGameEvent.removeBanishItems();
        final NpcInstance npcInstance = NpcUtils.spawnSingle(35600, location.x, location.y, location.z, 0L);
        ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                List<Player> list = World.getAroundPlayers(npcInstance, 750, 100);
                npcInstance.deleteMe();
                for (Player player : list) {
                    player.teleToLocation(((ClanHall)clanHallMiniGameEvent.getResidence()).getOwnerRestartPoint());
                }
                clanHallMiniGameEvent.processStep(RainbowGourdInstance.this.azg.getClan());
            }
        }, 10000L);
    }

    @Override
    public boolean isAttackable(Creature creature) {
        return false;
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        return false;
    }

    @Override
    public boolean isInvul() {
        return false;
    }
}


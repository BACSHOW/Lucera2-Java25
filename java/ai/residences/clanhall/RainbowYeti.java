/*
 * Decompiled with CFR 0.152.
 */
package ai.residences.clanhall;

import java.util.List;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CharacterAI;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2.gameserver.model.entity.events.objects.SpawnExObject;
import l2.gameserver.model.entity.events.objects.ZoneObject;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.utils.NpcUtils;
import npc.model.residences.clanhall.RainbowGourdInstance;
import npc.model.residences.clanhall.RainbowYetiInstance;

public class RainbowYeti
extends CharacterAI {
    public RainbowYeti(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature creature) {
        RainbowYetiInstance rainbowYetiInstance = (RainbowYetiInstance)this.getActor();
        ClanHallMiniGameEvent clanHallMiniGameEvent = rainbowYetiInstance.getEvent(ClanHallMiniGameEvent.class);
        if (clanHallMiniGameEvent == null) {
            return;
        }
        if (!creature.isPlayer()) {
            return;
        }
        Player player = creature.getPlayer();
        CMGSiegeClanObject cMGSiegeClanObject = null;
        List list = clanHallMiniGameEvent.getObjects("attackers");
        for (Object __raw_cMGSiegeClanObject2_42 : (Iterable<?>)(Object)(list)) {
           CMGSiegeClanObject cMGSiegeClanObject2 = (CMGSiegeClanObject)__raw_cMGSiegeClanObject2_42;
            if (!cMGSiegeClanObject2.isParticle(player)) continue;
            cMGSiegeClanObject = cMGSiegeClanObject2;
        }
        if (cMGSiegeClanObject == null) {
            return;
        }
        int n = list.indexOf(cMGSiegeClanObject);
        int n2 = Integer.MIN_VALUE;
        RainbowGourdInstance rainbowGourdInstance = null;
        RainbowGourdInstance rainbowGourdInstance2 = null;
        switch (skill.getId()) {
            case 2240: {
                if (Rnd.chance(90)) {
                    rainbowGourdInstance = this.e(n);
                    if (rainbowGourdInstance == null) {
                        return;
                    }
                    rainbowGourdInstance.doDecrease(player);
                    break;
                }
                rainbowYetiInstance.addMob(NpcUtils.spawnSingle(35592, rainbowYetiInstance.getX() + 10, rainbowYetiInstance.getY() + 10, rainbowYetiInstance.getZ(), 0L));
                break;
            }
            case 2241: {
                n2 = this.b(list.size(), n);
                if (n2 == Integer.MIN_VALUE) {
                    return;
                }
                rainbowGourdInstance2 = this.e(n2);
                if (rainbowGourdInstance2 == null) {
                    return;
                }
                rainbowGourdInstance2.doHeal();
                break;
            }
            case 2242: {
                n2 = this.b(list.size(), n);
                if (n2 == Integer.MIN_VALUE) {
                    return;
                }
                rainbowGourdInstance = this.e(n);
                rainbowGourdInstance2 = this.e(n2);
                if (rainbowGourdInstance2 == null || rainbowGourdInstance == null) {
                    return;
                }
                rainbowGourdInstance.doSwitch(rainbowGourdInstance2);
                break;
            }
            case 2243: {
                n2 = this.b(list.size(), n);
                if (n2 == Integer.MIN_VALUE) {
                    return;
                }
                ZoneObject zoneObject = (ZoneObject)clanHallMiniGameEvent.getFirstObject("zone_" + n2);
                if (zoneObject == null) {
                    return;
                }
                zoneObject.setActive(true);
                ThreadPoolManager.getInstance().schedule(new ZoneDeactive(zoneObject), 60000L);
            }
        }
    }

    private RainbowGourdInstance e(int n) {
        ClanHallMiniGameEvent clanHallMiniGameEvent = this.getActor().getEvent(ClanHallMiniGameEvent.class);
        SpawnExObject spawnExObject = (SpawnExObject)clanHallMiniGameEvent.getFirstObject("arena_" + n);
        return (RainbowGourdInstance)spawnExObject.getSpawns().get(1).getFirstSpawned();
    }

    private int b(int n, int n2) {
        int n3 = Integer.MIN_VALUE;
        for (int i = 0; i < 127 && (n3 = Rnd.get(n)) == n2; ++i) {
        }
        return n3;
    }

    private static class ZoneDeactive
    extends RunnableImpl {
        private final ZoneObject eq;

        public ZoneDeactive(ZoneObject zoneObject) {
            this.eq = zoneObject;
        }

        @Override
        public void runImpl() throws Exception {
            this.eq.setActive(false);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package ai.residences.clanhall;

import ai.residences.SiegeGuardFighter;
import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2.gameserver.model.entity.events.objects.SpawnExObject;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.PositionUtils;
import l2.gameserver.utils.ReflectionUtils;

public class AlfredVonHellmann
extends SiegeGuardFighter {
    public static final Skill DAMAGE_SKILL = SkillTable.getInstance().getInfo(5000, 1);
    public static final Skill DRAIN_SKILL = SkillTable.getInstance().getInfo(5001, 1);
    private static Zone en = ReflectionUtils.getZone("lidia_zone3");

    public AlfredVonHellmann(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        en.setActive(true);
        Functions.npcShoutCustomMessage(this.getActor(), "clanhall.AlfredVonHellmann.HEH_HEH_I_SEE_THAT_THE_FEAST_HAS_BEGAN_BE_WARY_THE_CURSE_OF_THE_HELLMANN_FAMILY_HAS_POISONED_THIS_LAND", new Object[0]);
    }

    @Override
    public void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        super.onEvtDead(creature);
        en.setActive(false);
        Functions.npcShoutCustomMessage(npcInstance, "clanhall.AlfredVonHellmann.AARGH_IF_I_DIE_THEN_THE_MAGIC_FORCE_FIELD_OF_BLOOD_WILL", new Object[0]);
        ClanHallSiegeEvent clanHallSiegeEvent = npcInstance.getEvent(ClanHallSiegeEvent.class);
        if (clanHallSiegeEvent == null) {
            return;
        }
        SpawnExObject spawnExObject = (SpawnExObject)clanHallSiegeEvent.getFirstObject("boss");
        NpcInstance npcInstance2 = spawnExObject.getFirstSpawned();
        if (npcInstance2.getCurrentHpRatio() == 1.0) {
            npcInstance2.setCurrentHp(npcInstance2.getMaxHp() / 2, true);
        }
    }

    @Override
    public void onEvtAttacked(Creature creature, int n) {
        Creature creature2;
        NpcInstance npcInstance = this.getActor();
        super.onEvtAttacked(creature, n);
        if (PositionUtils.calculateDistance(creature, npcInstance, false) > 300.0 && Rnd.chance(0.13)) {
            this.addTaskCast(creature, DRAIN_SKILL);
        }
        if ((creature2 = npcInstance.getAggroList().getMostHated()) == creature && Rnd.chance(0.3)) {
            this.addTaskCast(creature, DAMAGE_SKILL);
        }
    }
}


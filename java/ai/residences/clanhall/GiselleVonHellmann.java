/*
 * Decompiled with CFR 0.152.
 */
package ai.residences.clanhall;

import ai.residences.SiegeGuardMystic;
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

public class GiselleVonHellmann
extends SiegeGuardMystic {
    private static final Skill DAMAGE_SKILL = SkillTable.getInstance().getInfo(5003, 1);
    private static final Zone eo = ReflectionUtils.getZone("lidia_zone1");
    private static final Zone ep = ReflectionUtils.getZone("lidia_zone2");

    public GiselleVonHellmann(NpcInstance npcInstance) {
        super(npcInstance);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        eo.setActive(true);
        ep.setActive(true);
        Functions.npcShoutCustomMessage(this.getActor(), "clanhall.GiselleVonHellmann.ARISE_MY_FAITHFUL_SERVANTS_YOU_MY_PEOPLE_WHO_HAVE_INHERITED_THE_BLOOD", new Object[0]);
    }

    @Override
    public void onEvtDead(Creature creature) {
        NpcInstance npcInstance = this.getActor();
        super.onEvtDead(creature);
        eo.setActive(false);
        ep.setActive(false);
        Functions.npcShoutCustomMessage(npcInstance, "clanhall.GiselleVonHellmann.AARGH_IF_I_DIE_THEN_THE_MAGIC_FORCE_FIELD_OF_BLOOD_WILL", new Object[0]);
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
        NpcInstance npcInstance = this.getActor();
        super.onEvtAttacked(creature, n);
        if (PositionUtils.calculateDistance(creature, npcInstance, false) > 300.0 && Rnd.chance(0.13)) {
            this.addTaskCast(creature, DAMAGE_SKILL);
        }
    }
}


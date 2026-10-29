/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances.residences.clanhall;

import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import l2.gameserver.model.entity.events.objects.CTBSiegeClanObject;
import l2.gameserver.model.entity.events.objects.CTBTeamObject;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public abstract class CTBBossInstance
extends MonsterInstance {
    public static final Skill SKILL = SkillTable.getInstance().getInfo(5456, 1);
    private CTBTeamObject Ww;

    public CTBBossInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
        this.setHasChatWindow(false);
    }

    @Override
    public void reduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7) {
        if (creature.getLevel() > this.getLevel() + 8 && creature.getEffectList().getEffectsCountForSkill(SKILL.getId()) == 0) {
            this.doCast(SKILL, creature, false);
            return;
        }
        super.reduceCurrentHp(d, creature, skill, bl, bl2, bl3, bl4, bl5, bl6, bl7);
    }

    @Override
    public boolean isAttackable(Creature creature) {
        Player player;
        CTBSiegeClanObject cTBSiegeClanObject = this.Ww.getSiegeClan();
        return cTBSiegeClanObject == null || !creature.isPlayable() || (player = creature.getPlayer()).getClan() != cTBSiegeClanObject.getClan();
    }

    @Override
    public boolean isAutoAttackable(Creature creature) {
        return this.isAttackable(creature);
    }

    @Override
    public void onDeath(Creature creature) {
        ClanHallTeamBattleEvent clanHallTeamBattleEvent = this.getEvent(ClanHallTeamBattleEvent.class);
        clanHallTeamBattleEvent.processStep(this.Ww);
        super.onDeath(creature);
    }

    @Override
    public String getTitle() {
        CTBSiegeClanObject cTBSiegeClanObject = this.Ww.getSiegeClan();
        return cTBSiegeClanObject == null ? "" : cTBSiegeClanObject.getClan().getName();
    }

    public void setMatchTeamObject(CTBTeamObject cTBTeamObject) {
        this.Ww = cTBTeamObject;
    }
}


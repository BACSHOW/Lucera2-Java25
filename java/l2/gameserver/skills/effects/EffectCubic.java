/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.data.xml.holder.CubicHolder;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.MagicSkillLaunched;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.templates.CubicTemplate;

public class EffectCubic
extends Effect {
    private final CubicTemplate aot = CubicHolder.getInstance().getTemplate(this.getTemplate().getParam().getInteger("cubicId"), this.getTemplate().getParam().getInteger("cubicLevel"));
    private Future<?> to = null;
    private long cM = 0L;

    public EffectCubic(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
    }

    @Override
    public void onStart() {
        super.onStart();
        Player player = this._effected.getPlayer();
        if (player == null) {
            return;
        }
        player.addCubic(this);
        this.to = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ActionTask(), 1000L, 1000L);
    }

    @Override
    public void onExit() {
        super.onExit();
        Player player = this._effected.getPlayer();
        if (player == null) {
            return;
        }
        player.removeCubic(this.getId());
        this.to.cancel(true);
        this.to = null;
    }

    public void doAction(Player player) {
        if (this.cM > System.currentTimeMillis()) {
            return;
        }
        boolean bl = false;
        int n = Rnd.get(1000);
        for (Map.Entry<Integer, List<CubicTemplate.SkillInfo>> entry : this.aot.getSkills()) {
            if ((n -= entry.getKey().intValue()) >= 0) continue;
            for (CubicTemplate.SkillInfo skillInfo : entry.getValue()) {
                switch (skillInfo.getActionType()) {
                    case ATTACK: {
                        bl = EffectCubic.b(player, skillInfo);
                        break;
                    }
                    case DEBUFF: {
                        bl = EffectCubic.c(player, skillInfo);
                        break;
                    }
                    case HEAL: {
                        bl = EffectCubic.a(player, skillInfo);
                        break;
                    }
                    case CANCEL: {
                        bl = EffectCubic.d(player, skillInfo);
                    }
                }
            }
        }
        if (bl) {
            this.cM = System.currentTimeMillis() + (long)this.aot.getDelay() * 1000L;
        }
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    public int getId() {
        return this.aot.getId();
    }

    private static boolean a(final Player player, CubicTemplate.SkillInfo skillInfo) {
        final Skill skill = skillInfo.getSkill();
        Player player2 = null;
        if (player.getParty() == null) {
            if (!player.isCurrentHpFull() && !player.isDead()) {
                player2 = player;
            }
        } else {
            double d = 2.147483647E9;
            for (Player player3 : player.getParty().getPartyMembers()) {
                if (player3 == null || !player.isInRange(player3, (long)skillInfo.getSkill().getCastRange()) || player3.isCurrentHpFull() || player3.isDead() || !(player3.getCurrentHp() < d)) continue;
                d = player3.getCurrentHp();
                player2 = player3;
            }
        }
        if (player2 == null) {
            return false;
        }
        int n = skillInfo.getChance((int)player2.getCurrentHpPercents());
        if (!Rnd.chance(n)) {
            return false;
        }
        final Player player4 = player2;
        player.broadcastPacket(new MagicSkillUse(player, player4, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0L));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                ArrayList<Creature> arrayList = new ArrayList<Creature>(1);
                arrayList.add(player4);
                player.broadcastPacket(new MagicSkillLaunched((Creature)player, skill.getDisplayId(), skill.getDisplayLevel(), arrayList));
                player.callSkill(skill, arrayList, false);
            }
        }, skill.getHitTime());
        return true;
    }

    private static boolean b(final Player player, CubicTemplate.SkillInfo skillInfo) {
        if (!Rnd.chance(skillInfo.getChance())) {
            return false;
        }
        Creature creature = EffectCubic.e(player, skillInfo);
        if (creature == null) {
            return false;
        }
        final Creature creature2 = creature;
        final Skill skill = skillInfo.getSkill();
        player.broadcastPacket(new MagicSkillUse(player, creature, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0L));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                ArrayList<Creature> arrayList = new ArrayList<Creature>(1);
                arrayList.add(creature2);
                player.broadcastPacket(new MagicSkillLaunched((Creature)player, skill.getDisplayId(), skill.getDisplayLevel(), arrayList));
                player.callSkill(skill, arrayList, false);
                if (creature2.isNpc()) {
                    if (creature2.paralizeOnAttack(player)) {
                        if (Config.PARALIZE_ON_RAID_DIFF) {
                            player.paralizeMe(creature2);
                        }
                    } else {
                        int n = skill.getEffectPoint() != 0 ? skill.getEffectPoint() : (int)skill.getPower();
                        creature2.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player, n);
                    }
                }
            }
        }, skill.getHitTime());
        return true;
    }

    private static boolean c(final Player player, CubicTemplate.SkillInfo skillInfo) {
        if (!Rnd.chance(skillInfo.getChance())) {
            return false;
        }
        Creature creature = EffectCubic.e(player, skillInfo);
        if (creature == null) {
            return false;
        }
        final Creature creature2 = creature;
        final Skill skill = skillInfo.getSkill();
        player.broadcastPacket(new MagicSkillUse(player, creature, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0L));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                ArrayList<Creature> arrayList = new ArrayList<Creature>(1);
                arrayList.add(creature2);
                player.broadcastPacket(new MagicSkillLaunched((Creature)player, skill.getDisplayId(), skill.getDisplayLevel(), arrayList));
                player.callSkill(skill, arrayList, false);
            }
        }, skill.getHitTime());
        return true;
    }

    private static boolean d(final Player player, CubicTemplate.SkillInfo skillInfo) {
        if (!Rnd.chance(skillInfo.getChance())) {
            return false;
        }
        boolean bl = false;
        for (Effect effect : player.getEffectList().getAllEffects()) {
            if (!effect.isOffensive() || !effect.isCancelable() || effect.getTemplate()._applyOnCaster) continue;
            bl = true;
            break;
        }
        if (!bl) {
            return false;
        }
        final Skill skill = skillInfo.getSkill();
        player.broadcastPacket(new MagicSkillUse(player, player, skill.getDisplayId(), skill.getDisplayLevel(), skill.getHitTime(), 0L));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl(){

            @Override
            public void runImpl() throws Exception {
                ArrayList<Creature> arrayList = new ArrayList<Creature>(1);
                arrayList.add(player);
                player.broadcastPacket(new MagicSkillLaunched((Creature)player, skill.getDisplayId(), skill.getDisplayLevel(), arrayList));
                player.callSkill(skill, arrayList, false);
            }
        }, skill.getHitTime());
        return true;
    }

    private static final Creature e(Player player, CubicTemplate.SkillInfo skillInfo) {
        if (!player.isInCombat()) {
            return null;
        }
        GameObject gameObject = player.getTarget();
        if (gameObject == null || !gameObject.isCreature()) {
            return null;
        }
        Creature creature = (Creature)gameObject;
        if (creature.isDead()) {
            return null;
        }
        if (creature.getCurrentHp() < (double)skillInfo.getMinHp() && creature.getCurrentHpPercents() < (double)skillInfo.getMinHpPercent()) {
            return null;
        }
        if (creature.isDoor() && !skillInfo.isCanAttackDoor()) {
            return null;
        }
        if (!player.isInRangeZ(creature, (long)skillInfo.getSkill().getCastRange())) {
            return null;
        }
        Player player2 = creature.getPlayer();
        if (player2 != null && !player2.isInCombat()) {
            return null;
        }
        if (!creature.isAutoAttackable(player)) {
            return null;
        }
        return creature;
    }

    private class ActionTask
    extends RunnableImpl {
        private ActionTask() {
        }

        @Override
        public void runImpl() throws Exception {
            Player player;
            if (!EffectCubic.this.isActive()) {
                return;
            }
            Player player2 = player = EffectCubic.this._effected != null && EffectCubic.this._effected.isPlayer() ? (Player)EffectCubic.this._effected : null;
            if (player == null) {
                return;
            }
            EffectCubic.this.doAction(player);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.base.TeamType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.skills.effects.EffectTemplate;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import org.apache.commons.lang3.ArrayUtils;

public class EffectDispelEffects
extends Effect {
    private final String aoz;
    private final int aoA;
    private final String[] aoB;
    private final int aoC;
    private final int aoD;

    public EffectDispelEffects(Env env, EffectTemplate effectTemplate) {
        super(env, effectTemplate);
        this.aoz = effectTemplate.getParam().getString("dispelType", "");
        this.aoA = effectTemplate.getParam().getInteger("cancelRate", 0);
        this.aoC = effectTemplate.getParam().getInteger("negateCount", 5);
        this.aoB = effectTemplate.getParam().getString("negateStackTypes", "").split(";");
        this.aoD = effectTemplate.getParam().getInteger("reApplyDelay", 0);
    }

    @Override
    public void onStart() {
        ArrayList<Effect> arrayList = new ArrayList<Effect>();
        ArrayList<Effect> arrayList2 = new ArrayList<Effect>();
        for (Effect effect : this._effected.getEffectList().getAllEffects()) {
            if (this.aoz.equals("cancellation")) {
                if (effect.isOffensive() || effect.getSkill().isToggle() || !effect.isCancelable()) continue;
                if (effect.getSkill().isMusic()) {
                    arrayList.add(effect);
                    continue;
                }
                arrayList2.add(effect);
                continue;
            }
            if (this.aoz.equals("bane")) {
                if (!effect.isCancelable() || !ArrayUtils.contains(this.aoB, effect.getStackType()) && !ArrayUtils.contains(this.aoB, effect.getStackType2())) continue;
                arrayList2.add(effect);
                continue;
            }
            if (!this.aoz.equals("cleanse") || !effect.isOffensive() || !effect.isCancelable()) continue;
            arrayList2.add(effect);
        }
        List list = new ArrayList();
        list.addAll(arrayList);
        list.addAll(arrayList2);
        if (list.isEmpty()) {
            return;
        }
        double d = this._effected.calcStat(Stats.CANCEL_RESIST, 0.0, null, null);
        Collections.shuffle(list);
        list = list.subList(0, Math.min(this.aoC, list.size()));
        LinkedHashSet<Skill> linkedHashSet = new LinkedHashSet<Skill>();
        LinkedHashMap<Skill, List<StopEffectRecord>> linkedHashMap = new LinkedHashMap<Skill, List<StopEffectRecord>>();
        for (Object __raw_object_79 : (Iterable<?>)(Object)(list)) {
           Effect object = (Effect)__raw_object_79;
            int n;
            double d2 = object.getSkill().getMagicLevel();
            double d3 = (double)this.getSkill().getMagicLevel() - (d2 == 0.0 ? (double)this._effected.getLevel() : d2);
            double d4 = (2.0 * d3 + (double)this.aoA + (double)((n = object.getTimeLeft()) / 120)) * (d = 1.0 - d * 0.01);
            if (!Rnd.chance(this.a(d4, this._effector.isPlayable()))) continue;
            linkedHashSet.add(object.getSkill());
        }
        for (Skill skill : linkedHashSet) {
            List<Effect> list2 = this._effected.getEffectList().getEffectsBySkill(skill);
            ArrayList<StopEffectRecord> arrayList3 = new ArrayList<StopEffectRecord>();
            if (list2 == null || list2.isEmpty()) continue;
            for (Effect effect : list2) {
                if (this.aoD > 0) {
                    arrayList3.add(new StopEffectRecord(effect));
                }
                effect.exit();
            }
            if (!(arrayList3.isEmpty() || skill.isOffensive() || skill.isToggle() || skill.isTrigger())) {
                linkedHashMap.put(skill, arrayList3);
            }
            this._effected.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(skill));
        }
        if (!linkedHashMap.isEmpty() && this._effected.isPlayer()) {
            ThreadPoolManager.getInstance().schedule(new ReApplyEffects(this._effected.getPlayer(), linkedHashMap), (long)this.aoD * 1000L);
        }
    }

    private double a(double d, boolean bl) {
        if (this.aoz.equals("bane")) {
            if (d < (double)Config.SKILLS_BANE_MOD_MIN) {
                return Config.SKILLS_BANE_MOD_MIN;
            }
            if (d > (double)Config.SKILLS_BANE_MOD_MAX) {
                return Config.SKILLS_BANE_MOD_MAX;
            }
        } else {
            if (this.aoz.equals("cancellation")) {
                return Math.max((double)Config.SKILLS_DISPEL_MOD_MIN, Math.min((double)Config.SKILLS_DISPEL_MOD_MAX, d));
            }
            if (this.aoz.equals("cleanse")) {
                return this.aoA;
            }
        }
        return d;
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }

    private static final class ReApplyEffects
    extends RunnableImpl {
        private final HardReference<Player> aoE;
        private final Map<Skill, List<StopEffectRecord>> aoF;

        private ReApplyEffects(HardReference<Player> hardReference, Map<Skill, List<StopEffectRecord>> map) {
            this.aoE = hardReference;
            this.aoF = map;
        }

        public ReApplyEffects(Player player, Map<Skill, List<StopEffectRecord>> map) {
            this(player.getRef(), map);
        }

        @Override
        public void runImpl() throws Exception {
            Player player = this.aoE.get();
            if (player == null || player.isLogoutStarted() || player.isOutOfControl() || player.isDead() || player.isInDuel() || player.isAlikeDead() || player.isOlyParticipant() || player.isFlying() || player.isSitting() || player.getTeam() != TeamType.NONE || player.isInStoreMode()) {
                return;
            }
            for (Map.Entry<Skill, List<StopEffectRecord>> entry : this.aoF.entrySet()) {
                Skill skill = entry.getKey();
                List<StopEffectRecord> list = entry.getValue();
                for (StopEffectRecord stopEffectRecord : list) {
                    stopEffectRecord.apply(skill, player);
                }
            }
        }
    }

    private static final class StopEffectRecord {
        private final EffectTemplate aoG;
        private final int count;
        private final long time;
        private final long aoH;

        public StopEffectRecord(EffectTemplate effectTemplate, int n, long l, long l2) {
            this.aoG = effectTemplate;
            this.count = n;
            this.time = l;
            this.aoH = l2;
        }

        public StopEffectRecord(Effect effect) {
            this(effect.getTemplate(), effect.getCount(), effect.getTime(), effect.getPeriod());
        }

        public void apply(Skill skill, Player player) {
            Env env = new Env(player, player, skill);
            Effect effect = this.aoG.getEffect(env);
            if (effect == null || effect.isOneTime()) {
                return;
            }
            effect.setCount(this.count);
            effect.setPeriod(this.count == 1 ? this.aoH - this.time : this.aoH);
            player.getEffectList().addEffect(effect);
        }
    }
}


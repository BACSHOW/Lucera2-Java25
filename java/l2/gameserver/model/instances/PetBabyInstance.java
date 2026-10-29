/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.concurrent.Future;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.EffectList;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PetBabyInstance
extends PetInstance {
    private static final Logger _log = LoggerFactory.getLogger(PetBabyInstance.class);
    private Future<?> UQ;
    private boolean UR = true;
    private static final int US = 4717;
    private static final int UT = 4718;
    private static final int UU = 5195;
    private static final int UV = 5590;
    private static final int UW = 5200;
    private static final int UX = 5186;
    private static final int UY = 5187;
    private static final int UZ = 5188;
    private static final int Va = 5189;
    private static final int Vb = 5190;
    private static final int Vc = 5191;
    private static final int Vd = 5192;
    private static final int Ve = 5193;
    private static final int Vf = 5194;
    private static final int Vg = 5201;
    private static final int Vh = 5586;
    private static final int Vi = 5587;
    private static final int Vj = 5588;
    private static final int Vk = 5589;
    private static final int Vl = 5987;
    private static final int Vm = 5988;
    private static final int Vn = 5196;
    private static final int Vo = 5197;
    private static final int Vp = 5198;
    private static final int Vq = 5199;
    private static final Skill[][] Vr = new Skill[][]{{SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5586, 3)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5189, 6)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(5186, 2)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(5186, 2), SkillTable.getInstance().getInfo(5187, 4), SkillTable.getInstance().getInfo(5588, 3)}};
    private static final Skill[][] Vs = new Skill[][]{{SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5189, 6)}, {SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5191, 3)}, {SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5191, 3), SkillTable.getInstance().getInfo(5187, 4), SkillTable.getInstance().getInfo(5186, 2)}, {SkillTable.getInstance().getInfo(5586, 3), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5191, 3), SkillTable.getInstance().getInfo(5187, 4), SkillTable.getInstance().getInfo(5186, 2), SkillTable.getInstance().getInfo(5588, 3), SkillTable.getInstance().getInfo(5589, 3)}};
    private static final Skill[][] Vt = new Skill[][]{{SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(5201, 6)}, {SkillTable.getInstance().getInfo(5194, 3), SkillTable.getInstance().getInfo(5190, 6), SkillTable.getInstance().getInfo(5189, 6), SkillTable.getInstance().getInfo(5587, 3), SkillTable.getInstance().getInfo(5193, 3), SkillTable.getInstance().getInfo(5201, 6)}};

    public PetBabyInstance(int n, NpcTemplate npcTemplate, Player player, ItemInstance itemInstance, int n2, long l) {
        super(n, npcTemplate, player, itemInstance, n2, l);
    }

    public PetBabyInstance(int n, NpcTemplate npcTemplate, Player player, ItemInstance itemInstance) {
        super(n, npcTemplate, player, itemInstance);
    }

    public Skill[] getBuffs() {
        switch (this.getNpcId()) {
            case 16036: {
                return Vr[this.getBuffLevel()];
            }
            case 16034: {
                return Vs[this.getBuffLevel()];
            }
            case 16035: {
                return Vt[this.getBuffLevel()];
            }
        }
        return Skill.EMPTY_ARRAY;
    }

    public Skill onActionTask() {
        try {
            Player player = this.getPlayer();
            if (!(player.isDead() || player.isInvul() || this.isCastingNow())) {
                if (this.getEffectList().getEffectsCountForSkill(5753) > 0) {
                    return null;
                }
                if (this.getEffectList().getEffectsCountForSkill(5771) > 0) {
                    return null;
                }
                boolean bl = PetDataTable.isImprovedBabyPet(this.getNpcId());
                Skill skill = null;
                if (!Config.ALT_PET_HEAL_BATTLE_ONLY || player.isInCombat()) {
                    double d;
                    double d2 = player.getCurrentHpPercents();
                    if (d2 < 90.0 && Rnd.chance((100.0 - d2) / 3.0)) {
                        if (d2 < 33.0) {
                            skill = SkillTable.getInstance().getInfo(bl ? 5590 : 4718, this.getHealLevel());
                        } else if (this.getNpcId() != 16035) {
                            skill = SkillTable.getInstance().getInfo(bl ? 5195 : 4717, this.getHealLevel());
                        }
                    }
                    if (skill == null && this.getNpcId() == 16035 && (d = player.getCurrentMpPercents()) < 66.0 && Rnd.chance((100.0 - d) / 2.0)) {
                        skill = SkillTable.getInstance().getInfo(5200, this.getRechargeLevel());
                    }
                    if (skill != null && skill.checkCondition(this, player, false, !this.isFollowMode(), true)) {
                        this.setTarget(player);
                        this.getAI().Cast(skill, player, false, !this.isFollowMode());
                        return skill;
                    }
                }
                if (!bl || player.isInOfflineMode() || player.getEffectList().getEffectsCountForSkill(5771) > 0) {
                    return null;
                }
                block2: for (Skill skill2 : this.getBuffs()) {
                    if (this.getCurrentMp() < skill2.getMpConsume2()) continue;
                    for (Effect effect : player.getEffectList().getAllEffects()) {
                        if (!this.a(effect, skill2)) continue;
                        continue block2;
                    }
                    if (skill2.checkCondition(this, player, false, !this.isFollowMode(), true)) {
                        this.setTarget(player);
                        this.getAI().Cast(skill2, player, false, !this.isFollowMode());
                        return skill2;
                    }
                    return null;
                }
            }
        }
        catch (Throwable throwable) {
            _log.warn("Pet [#" + this.getNpcId() + "] a buff task error has occurred: " + throwable);
            _log.error("", throwable);
        }
        return null;
    }

    private boolean a(Effect effect, Skill skill) {
        if (effect == null || !effect.isInUse() || !EffectList.checkStackType(effect.getTemplate(), skill.getEffectTemplates()[0])) {
            return false;
        }
        if (effect.getStackOrder() < skill.getEffectTemplates()[0]._stackOrder) {
            return false;
        }
        if (effect.getTimeLeft() > 10) {
            return true;
        }
        if (effect.getNext() != null) {
            return this.a(effect.getNext(), skill);
        }
        return false;
    }

    public synchronized void stopBuffTask() {
        if (this.UQ != null) {
            this.UQ.cancel(false);
            this.UQ = null;
        }
    }

    public synchronized void startBuffTask() {
        if (this.UQ != null) {
            this.stopBuffTask();
        }
        if (this.UQ == null && !this.isDead()) {
            this.UQ = ThreadPoolManager.getInstance().schedule(new ActionTask(), 5000L);
        }
    }

    public boolean isBuffEnabled() {
        return this.UR;
    }

    public void triggerBuff() {
        this.UR = !this.UR;
    }

    @Override
    protected void onDeath(Creature creature) {
        this.stopBuffTask();
        super.onDeath(creature);
    }

    @Override
    public void doRevive() {
        super.doRevive();
        this.startBuffTask();
    }

    @Override
    public void unSummon() {
        this.stopBuffTask();
        super.unSummon();
    }

    public int getHealLevel() {
        return Math.min(Math.max((this.getLevel() - this.getMinLevel()) / ((80 - this.getMinLevel()) / 12), 1), 12);
    }

    public int getRechargeLevel() {
        return Math.min(Math.max((this.getLevel() - this.getMinLevel()) / ((80 - this.getMinLevel()) / 8), 1), 8);
    }

    public int getBuffLevel() {
        return Math.min(Math.max((this.getLevel() - 55) / 5, 0), 3);
    }

    @Override
    public int getSoulshotConsumeCount() {
        return 1;
    }

    @Override
    public int getSpiritshotConsumeCount() {
        return 1;
    }

    class ActionTask
    extends RunnableImpl {
        ActionTask() {
        }

        @Override
        public void runImpl() throws Exception {
            Skill skill = PetBabyInstance.this.onActionTask();
            PetBabyInstance.this.UQ = ThreadPoolManager.getInstance().schedule(new ActionTask(), skill == null ? 1000L : (long)(skill.getHitTime() * 333 / Math.max(PetBabyInstance.this.getMAtkSpd(), 1) - 100));
        }
    }
}


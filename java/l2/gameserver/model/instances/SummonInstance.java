/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.concurrent.Future;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.BaseStats;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SetSummonRemainTime;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.npc.NpcTemplate;

public class SummonInstance
extends Summon {
    public final int CYCLE = 5000;
    private int VU;
    private double VV = 0.0;
    private int VW;
    private int VX;
    private int VY;
    private Future<?> VZ;
    private int Wa;
    private int Wb;
    private int Wc;

    public SummonInstance(int n, NpcTemplate npcTemplate, Player player, int n2, int n3, int n4, int n5, Skill skill) {
        super(n, npcTemplate, player);
        this.setName(npcTemplate.name);
        if (Config.ALT_SET_TITLE_OWNER_TO_SUMMON) {
            this.setTitle(player.getName());
        }
        this.Wb = this.Wc = n2;
        this.VW = n3;
        this.VX = n4;
        this.Wa = this.VY = n5;
        this.VU = skill.getDisplayId();
        this.VZ = ThreadPoolManager.getInstance().schedule(new Lifetime(), 5000L);
    }

    public HardReference<SummonInstance> getRef() {
        return (HardReference<SummonInstance>)(HardReference<?>)super.getRef();
    }

    @Override
    public final int getLevel() {
        return this.getTemplate() != null ? this.getTemplate().level : 0;
    }

    @Override
    public int getPAtk(Creature creature) {
        double d = BaseStats.STR.calcBonus(this) * (double)(this.getTemplate().level + 89);
        return (int)this.calcStat(Stats.POWER_ATTACK, (double)this.getTemplate().basePAtk * d / 100.0, creature, null);
    }

    @Override
    public int getPDef(Creature creature) {
        double d = this.getTemplate().level + 89;
        return (int)this.calcStat(Stats.POWER_DEFENCE, (double)this.getTemplate().basePDef * d / 100.0, creature, null);
    }

    @Override
    public int getMAtk(Creature creature, Skill skill) {
        double d = BaseStats.INT.calcBonus(this);
        double d2 = this.getTemplate().level + 89;
        double d3 = d2 * d2 * d * d / 10000.0;
        return (int)this.calcStat(Stats.MAGIC_ATTACK, (double)this.getTemplate().baseMAtk * d3, creature, skill);
    }

    @Override
    public int getMDef(Creature creature, Skill skill) {
        double d = BaseStats.MEN.calcBonus(this) * (double)(this.getTemplate().level + 89);
        return (int)this.calcStat(Stats.MAGIC_DEFENCE, (double)this.getTemplate().baseMDef * d / 100.0, creature, skill);
    }

    @Override
    public int getSummonType() {
        return 1;
    }

    @Override
    public int getCurrentFed() {
        return this.Wb;
    }

    @Override
    public int getMaxFed() {
        return this.Wc;
    }

    public void setExpPenalty(double d) {
        this.VV = d;
    }

    @Override
    public double getExpPenalty() {
        return this.VV;
    }

    @Override
    protected void onDeath(Creature creature) {
        super.onDeath(creature);
        this.saveEffects();
        if (this.VZ != null) {
            this.VZ.cancel(false);
            this.VZ = null;
        }
    }

    public int getItemConsumeIdInTime() {
        return this.VW;
    }

    public int getItemConsumeCountInTime() {
        return this.VX;
    }

    public int getItemConsumeDelay() {
        return this.VY;
    }

    protected synchronized void stopDisappear() {
        if (this.VZ != null) {
            this.VZ.cancel(false);
            this.VZ = null;
        }
    }

    @Override
    public void unSummon() {
        this.stopDisappear();
        super.unSummon();
    }

    @Override
    public void displayGiveDamageMessage(Creature creature, int n, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        if (bl) {
            player.sendPacket((IStaticPacket)SystemMsg.SUMMONED_MONSTERS_CRITICAL_HIT);
        }
        if (bl2) {
            player.sendPacket((IStaticPacket)new SystemMessage(43));
        } else if (!creature.isInvul()) {
            player.sendPacket((IStaticPacket)new SystemMessage(1026).addNumber(n));
        }
    }

    @Override
    public void displayReceiveDamageMessage(Creature creature, int n) {
        Player player = this.getPlayer();
        player.sendPacket((IStaticPacket)new SystemMessage(1027).addName(creature).addNumber((long)n));
    }

    @Override
    public int getEffectIdentifier() {
        return this.VU;
    }

    @Override
    public boolean isSummon() {
        return true;
    }

    @Override
    public long getWearedMask() {
        return WeaponTemplate.WeaponType.SWORD.mask();
    }

    class Lifetime
    extends RunnableImpl {
        Lifetime() {
        }

        @Override
        public void runImpl() throws Exception {
            Player player = SummonInstance.this.getPlayer();
            if (player == null) {
                SummonInstance.this.VZ = null;
                SummonInstance.this.unSummon();
                return;
            }
            int n = SummonInstance.this.isInCombat() ? 5000 : 1250;
            SummonInstance.this.Wb = SummonInstance.this.Wb - n;
            if (SummonInstance.this.Wb <= 0) {
                player.sendPacket((IStaticPacket)Msg.SERVITOR_DISAPPEASR_BECAUSE_THE_SUMMONING_TIME_IS_OVER);
                SummonInstance.this.VZ = null;
                SummonInstance.this.unSummon();
                return;
            }
            SummonInstance.this.Wa = SummonInstance.this.Wa - n;
            if (SummonInstance.this.VW > 0 && SummonInstance.this.VX > 0 && SummonInstance.this.Wa <= 0) {
                if (player.getInventory().destroyItemByItemId(SummonInstance.this.getItemConsumeIdInTime(), SummonInstance.this.getItemConsumeCountInTime())) {
                    SummonInstance.this.Wa = SummonInstance.this.VY;
                    player.sendPacket((IStaticPacket)new SystemMessage(1029).addItemName(SummonInstance.this.getItemConsumeIdInTime()));
                } else {
                    player.sendPacket((IStaticPacket)Msg.SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITORS_STAY_THE_SERVITOR_WILL_DISAPPEAR);
                    SummonInstance.this.unSummon();
                    return;
                }
            }
            player.sendPacket((IStaticPacket)new SetSummonRemainTime(SummonInstance.this));
            SummonInstance.this.VZ = ThreadPoolManager.getInstance().schedule(this, 5000L);
        }
    }
}


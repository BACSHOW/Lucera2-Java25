/*
 * Decompiled with CFR 0.152.
 */
package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.GameTimeController;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.ai.Fighter;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.listener.game.OnDayNightChangeListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;

public class ZakenNightly
extends Fighter {
    private static final int[] dn = new int[]{29023, 29024, 29026, 29027};
    private static final Location[] cfr_renamed_0 = new Location[]{new Location(55272, 219112, -3496), new Location(56296, 218072, -3496), new Location(54232, 218072, -3496), new Location(54248, 220136, -3496), new Location(56296, 220136, -3496), new Location(55272, 219112, -3224), new Location(56296, 218072, -3224), new Location(54232, 218072, -3224), new Location(54248, 220136, -3224), new Location(56296, 220136, -3224), new Location(55272, 219112, -2952), new Location(56296, 218072, -2952), new Location(54232, 218072, -2952), new Location(54248, 220136, -2952), new Location(56296, 220136, -2952)};
    private static final long dp = 15000L;
    private static final int dq = 2;
    private static final long dr = 90000L;
    private static final long ds = 240000L;
    private static final long dt = 30000L;
    private long du = 0L;
    private List<NpcInstance> dv = new ArrayList<NpcInstance>();
    private long dw = 0L;
    private HardReference<Player> dx = HardReferences.emptyRef();
    private long dy = 0L;

    public ZakenNightly(NpcInstance npcInstance) {
        super(npcInstance);
        this.MAX_PURSUE_RANGE = 0x3FFFFFFF;
        GameTimeController.getInstance().addListener(new s_zaken_regen());
    }

    private void n() {
        this.dx = HardReferences.emptyRef();
        List<Creature> list = this.getActor().getAggroList().getHateList(900);
        Collections.shuffle(list);
        for (Creature creature : list) {
            if (!creature.isPlayer() || creature.isDead() || !GeoEngine.canSeeTarget(this.getActor(), creature, false)) continue;
            this.dx = creature.getPlayer().getRef();
        }
    }

    @Override
    protected void onEvtFinishCasting(Skill skill, Creature creature) {
        Player player = this.dx.get();
        if (skill != null) {
            if (player != null && skill.getId() == 4216) {
                player.teleToLocation(cfr_renamed_0[Rnd.get(cfr_renamed_0.length)]);
                this.getActor().getAggroList().remove(player, true);
                this.dx = HardReferences.emptyRef();
            } else if (skill.getId() == 4222) {
                this.getActor().teleToLocation(cfr_renamed_0[Rnd.get(cfr_renamed_0.length)]);
                this.getActor().getAggroList().clear(true);
                for (int i = 0; i < 10; ++i) {
                    this.getActor().getReflection().addSpawnWithoutRespawn(dn[Rnd.get(dn.length)], this.getActor().getLoc(), 500);
                }
            }
        }
        super.onEvtFinishCasting(skill, creature);
    }

    @Override
    protected void thinkAttack() {
        if (this.du == 0L) {
            this.du = System.currentTimeMillis() + 30000L;
        }
        if (this.dw == 0L) {
            this.dw = System.currentTimeMillis() + 90000L;
        }
        if (this.dy == 0L) {
            this.dy = System.currentTimeMillis() + 150000L;
        }
        if (this.du + 15000L < System.currentTimeMillis()) {
            int n = 0;
            for (NpcInstance npcInstance : this.dv) {
                if (npcInstance.isDead()) continue;
                ++n;
            }
            this.du = System.currentTimeMillis();
            if (n < 30) {
                for (int i = 0; i < 2; ++i) {
                    NpcInstance npcInstance;
                    npcInstance = this.getActor().getReflection().addSpawnWithoutRespawn(dn[Rnd.get(dn.length)], this.getActor().getLoc(), 350);
                    npcInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this.getActor().getAI().getAttackTarget(), 5000);
                    this.dv.add(npcInstance);
                }
            }
        }
        if (Config.ZAKEN_USE_TELEPORT) {
            if (this.dw + 90000L < System.currentTimeMillis()) {
                this.n();
                Player player = this.dx.get();
                if (player != null) {
                    this.getActor().doCast(SkillTable.getInstance().getInfo(4216, 1), player, false);
                }
                this.dw = System.currentTimeMillis();
            }
            if (this.dy + 240000L < System.currentTimeMillis()) {
                this.getActor().doCast(SkillTable.getInstance().getInfo(4222, 1), this.getActor(), false);
                this.dy = System.currentTimeMillis();
            }
        }
        super.thinkAttack();
    }

    @Override
    protected void onEvtDead(Creature creature) {
        this.getActor().broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, this.getActor().getObjectId(), this.getActor().getLoc()));
        super.onEvtDead(creature);
    }

    @Override
    protected void teleportHome() {
    }

    @Override
    protected void returnHome() {
    }

    private class s_zaken_regen
    implements OnDayNightChangeListener {
        private final Skill dz = SkillTable.getInstance().getInfo(4227, 1);

        private s_zaken_regen() {
            if (GameTimeController.getInstance().isNowNight()) {
                this.onNight();
            } else {
                this.onDay();
            }
        }

        @Override
        public void onDay() {
            ZakenNightly.this.getActor().getEffectList().stopEffect(this.dz);
        }

        @Override
        public void onNight() {
            this.dz.getEffects(ZakenNightly.this.getActor(), ZakenNightly.this.getActor(), false, false);
        }
    }
}


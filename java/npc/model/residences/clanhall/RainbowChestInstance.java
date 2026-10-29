/*
 * Decompiled with CFR 0.152.
 */
package npc.model.residences.clanhall;

import l2.commons.util.Rnd;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class RainbowChestInstance
extends MonsterInstance {
    private static final int ayM = 8035;
    private static final int ayN = 8036;
    private static final int ayO = 8037;
    private static final int ayP = 8038;
    private static final int ayQ = 8039;
    private static final int ayR = 8040;
    private static final int ayS = 8041;
    private static final int ayT = 8042;
    private static final int ayU = 8043;
    private static final int ayV = 8045;
    private static final int ayW = 8046;
    private static final int ayX = 8047;
    private static final int ayY = 8048;
    private static final int ayZ = 8049;
    private static final int aza = 8050;
    private static final int azb = 8051;
    private static final int azc = 8052;
    private static final int azd = 8053;
    private static final int aze = 8054;
    private static final int azf = 8055;

    public RainbowChestInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void reduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7) {
        if (creature == null || !creature.isPlayer() || creature.getActiveWeaponInstance() != null || skill != null || bl6) {
            return;
        }
        super.reduceCurrentHp((double)this.getMaxHp() * 0.2, creature, skill, bl, bl2, bl3, bl4, bl5, bl6, bl7);
    }

    @Override
    public void onDeath(Creature creature) {
        super.onDeath(creature);
        Player player = creature.getPlayer();
        if (player == null) {
            return;
        }
        int n = 1 + Rnd.get(2);
        for (int i = 0; i < n; ++i) {
            int n2 = Rnd.get(100);
            if (n2 <= 5) {
                this.dropItem(player, 8035, 1L);
                continue;
            }
            if (n2 > 5 && n2 <= 10) {
                this.dropItem(player, 8036, 1L);
                continue;
            }
            if (n2 > 10 && n2 <= 15) {
                this.dropItem(player, 8037, 1L);
                continue;
            }
            if (n2 > 15 && n2 <= 20) {
                this.dropItem(player, 8038, 1L);
                continue;
            }
            if (n2 > 20 && n2 <= 25) {
                this.dropItem(player, 8039, 1L);
                continue;
            }
            if (n2 > 25 && n2 <= 30) {
                this.dropItem(player, 8040, 1L);
                continue;
            }
            if (n2 > 30 && n2 <= 35) {
                this.dropItem(player, 8041, 1L);
                continue;
            }
            if (n2 > 35 && n2 <= 40) {
                this.dropItem(player, 8042, 1L);
                continue;
            }
            if (n2 > 40 && n2 <= 45) {
                this.dropItem(player, 8043, 1L);
                continue;
            }
            if (n2 > 45 && n2 <= 50) {
                this.dropItem(player, 8045, 1L);
                continue;
            }
            if (n2 > 50 && n2 <= 55) {
                this.dropItem(player, 8046, 1L);
                continue;
            }
            if (n2 > 55 && n2 <= 60) {
                this.dropItem(player, 8047, 1L);
                continue;
            }
            if (n2 > 60 && n2 <= 65) {
                this.dropItem(player, 8048, 1L);
                continue;
            }
            if (n2 > 65 && n2 <= 70) {
                this.dropItem(player, 8049, 1L);
                continue;
            }
            if (n2 > 70 && n2 <= 75) {
                this.dropItem(player, 8050, 1L);
                continue;
            }
            if (n2 > 75 && n2 <= 80) {
                this.dropItem(player, 8051, 1L);
                continue;
            }
            if (n2 > 80 && n2 <= 85) {
                this.dropItem(player, 8052, 1L);
                continue;
            }
            if (n2 > 85 && n2 <= 90) {
                this.dropItem(player, 8053, 1L);
                continue;
            }
            if (n2 > 90 && n2 <= 95) {
                this.dropItem(player, 8054, 1L);
                continue;
            }
            if (n2 <= 95) continue;
            this.dropItem(player, 8055, 1L);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.commons.lang.reference.HardReference;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.SpecialMonsterInstance;
import l2.gameserver.templates.npc.NpcTemplate;

public class SquashInstance
extends SpecialMonsterInstance {
    public static final int Young_Squash = 12774;
    public static final int High_Quality_Squash = 12775;
    public static final int Low_Quality_Squash = 12776;
    public static final int Large_Young_Squash = 12777;
    public static final int High_Quality_Large_Squash = 12778;
    public static final int Low_Quality_Large_Squash = 12779;
    public static final int King_Squash = 13016;
    public static final int Emperor_Squash = 13017;
    private HardReference<Player> axZ;

    public SquashInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    public void setSpawner(Player player) {
        this.axZ = player.getRef();
    }

    public Player getSpawner() {
        return this.axZ.get();
    }

    @Override
    public void reduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7) {
        if (creature.getActiveWeaponInstance() == null) {
            return;
        }
        int n = creature.getActiveWeaponInstance().getItemId();
        if ((this.getNpcId() == 12779 || this.getNpcId() == 12778 || this.getNpcId() == 13017) && n != 4202 && n != 5133 && n != 5817 && n != 7058 && n != 8350) {
            return;
        }
        d = 1.0;
        super.reduceCurrentHp(d, creature, skill, bl, bl2, bl3, bl4, bl5, bl6, bl7);
    }

    @Override
    public long getRegenTick() {
        return 0L;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}


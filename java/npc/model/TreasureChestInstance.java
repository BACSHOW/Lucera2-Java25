/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.instances.ChestInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public class TreasureChestInstance
extends ChestInstance {
    private static final int ayk = 4143;

    public TreasureChestInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void tryOpen(Player player, Skill skill) {
        if (this.dM()) {
            this.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 100);
            return;
        }
        double d = this.calcChance(player, skill);
        if (Rnd.chance(d)) {
            this.getAggroList().addDamageHate(player, 10000, 0);
            this.doDie(player);
        } else {
            this.l(player);
        }
        player.sendPacket((IStaticPacket)new PlaySound("ItemSound2.broken_key"));
    }

    public double calcChance(Player player, Skill skill) {
        int n = skill.getId();
        int n2 = skill.getLevel();
        int n3 = 1;
        int n4 = 1;
        if (n == 27) {
            if (n2 == 1) {
                n3 = 98;
            } else if (n2 == 2) {
                n3 = 84;
            } else if (n2 == 3) {
                n3 = 99;
            } else if (n2 == 4) {
                n3 = 84;
            } else if (n2 == 5) {
                n3 = 88;
            } else if (n2 == 6) {
                n3 = 90;
            } else if (n2 == 7) {
                n3 = 89;
            } else if (n2 == 8) {
                n3 = 88;
            } else if (n2 == 9) {
                n3 = 86;
            } else if (n2 == 10) {
                n3 = 90;
            } else if (n2 == 11) {
                n3 = 87;
            } else if (n2 == 12) {
                n3 = 89;
            } else if (n2 == 13) {
                n3 = 89;
            } else if (n2 == 14) {
                n3 = 89;
            } else if (n2 == 15) {
                n3 = 89;
            }
            n4 = n3 - (this.getLevel() - n2 * 4 - 16) * 6;
            if (n4 > n3) {
                n4 = n3;
            }
        } else if (n == 2065) {
            n4 = (int)(60.0 - (double)(this.getLevel() - (n2 - 1) * 10) * 1.5);
            if (n4 > 60) {
                n4 = 60;
            }
        } else if (n == 2229) {
            if (n2 == 1) {
                int n5 = this.getLevel() - 19;
                n4 = n5 <= 0 ? 100 : (int)((2.0E-4 * (double)(n5 * n5) - 0.0264 * (double)n5 + 0.7695) * 100.0);
            } else if (n2 == 2) {
                int n6 = this.getLevel() - 29;
                n4 = n6 <= 0 ? 100 : (int)((3.0E-4 * (double)n6 * (double)n6 - 0.0279 * (double)n6 + 0.7568) * 100.0);
            } else if (n2 == 3) {
                int n7 = this.getLevel() - 39;
                n4 = n7 <= 0 ? 100 : (int)((3.0E-4 * (double)n7 * (double)n7 - 0.0269 * (double)n7 + 0.7334) * 100.0);
            } else if (n2 == 4) {
                int n8 = this.getLevel() - 49;
                n4 = n8 <= 0 ? 100 : (int)((3.0E-4 * (double)n8 * (double)n8 - 0.0284 * (double)n8 + 0.8034) * 100.0);
            } else if (n2 == 5) {
                int n9 = this.getLevel() - 59;
                n4 = n9 <= 0 ? 100 : (int)((5.0E-4 * (double)n9 * (double)n9 - 0.0356 * (double)n9 + 0.9065) * 100.0);
            } else if (n2 == 6) {
                int n10 = this.getLevel() - 69;
                n4 = n10 <= 0 ? 100 : (int)((9.0E-4 * (double)n10 * (double)n10 - 0.0373 * (double)n10 + 0.8572) * 100.0);
            } else if (n2 == 7) {
                int n11 = this.getLevel() - 79;
                n4 = n11 <= 0 ? 100 : (int)((0.0043 * (double)n11 * (double)n11 - 0.0671 * (double)n11 + 0.9593) * 100.0);
            } else if (n2 == 8) {
                n4 = 100;
            }
        }
        return n4;
    }

    private void l(Creature creature) {
        Skill skill = SkillTable.getInstance().getInfo(4143, this.dL());
        if (skill != null) {
            this.doCast(skill, creature, false);
        }
        this.onDecay();
    }

    private int dL() {
        int n = this.getLevel();
        int n2 = 1;
        if (n >= 78) {
            n2 = 10;
        } else if (n >= 72) {
            n2 = 9;
        } else if (n >= 66) {
            n2 = 8;
        } else if (n >= 60) {
            n2 = 7;
        } else if (n >= 54) {
            n2 = 6;
        } else if (n >= 48) {
            n2 = 5;
        } else if (n >= 42) {
            n2 = 4;
        } else if (n >= 36) {
            n2 = 3;
        } else if (n >= 30) {
            n2 = 2;
        }
        return n2;
    }

    private boolean dM() {
        int n = this.getNpcId();
        return n >= 21801 && n <= 21822;
    }

    @Override
    public void onReduceCurrentHp(double d, Creature creature, Skill skill, boolean bl, boolean bl2, boolean bl3) {
        if (!this.dM()) {
            this.l(creature);
        }
        super.onReduceCurrentHp(d, creature, skill, bl, bl2, bl3);
    }
}


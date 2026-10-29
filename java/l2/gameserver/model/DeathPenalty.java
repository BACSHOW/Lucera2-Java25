/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import l2.commons.lang.reference.HardReference;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;

public class DeathPenalty {
    private static final int _skillId = 5076;
    private static final int ES = 1325;
    private static final int ET = 2168;
    private HardReference<Player> e;
    private int _level;
    private boolean EU;

    public DeathPenalty(Player player, int n) {
        this.e = player.getRef();
        this._level = n;
    }

    public Player getPlayer() {
        return this.e.get();
    }

    public int getLevel() {
        if (this._level > 15) {
            this._level = 15;
        }
        if (this._level < 0) {
            this._level = 0;
        }
        return Config.ALLOW_DEATH_PENALTY_C5 ? this._level : 0;
    }

    public int getLevelOnSaveDB() {
        if (this._level > 15) {
            this._level = 15;
        }
        if (this._level < 0) {
            this._level = 0;
        }
        return this._level;
    }

    public void notifyDead(Creature creature) {
        if (!Config.ALLOW_DEATH_PENALTY_C5) {
            return;
        }
        if (this.EU) {
            this.EU = false;
            return;
        }
        if (creature == null || creature.isPlayable()) {
            return;
        }
        Player player = this.getPlayer();
        if (player == null || player.getLevel() <= 9) {
            return;
        }
        int n = player.getKarma() / Config.ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
        if (n < 0) {
            n = 0;
        }
        if (Rnd.chance(Config.ALT_DEATH_PENALTY_C5_CHANCE + n)) {
            this.addLevel();
        }
    }

    public void restore(Player player) {
        Skill skill = player.getKnownSkill(5076);
        if (skill != null) {
            player.removeSkill(skill, true);
        }
        if (!Config.ALLOW_DEATH_PENALTY_C5) {
            return;
        }
        if (this.getLevel() > 0) {
            player.addSkill(SkillTable.getInstance().getInfo(5076, this.getLevel()), false);
            player.sendPacket((IStaticPacket)new SystemMessage(1916).addNumber(this.getLevel()));
        }
        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    public void addLevel() {
        Skill skill;
        Player player = this.getPlayer();
        if (player == null || this.getLevel() >= 15) {
            return;
        }
        if (this.getLevel() != 0 && (skill = player.getKnownSkill(5076)) != null) {
            player.removeSkill(skill, true);
        }
        ++this._level;
        player.addSkill(SkillTable.getInstance().getInfo(5076, this.getLevel()), false);
        player.sendPacket((IStaticPacket)new SystemMessage(1916).addNumber(this.getLevel()));
        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    public void reduceLevel() {
        Player player = this.getPlayer();
        if (player == null || this.getLevel() <= 0) {
            return;
        }
        Skill skill = player.getKnownSkill(5076);
        if (skill != null) {
            player.removeSkill(skill, true);
        }
        --this._level;
        if (this.getLevel() > 0) {
            player.addSkill(SkillTable.getInstance().getInfo(5076, this.getLevel()), false);
            player.sendPacket((IStaticPacket)new SystemMessage(1916).addNumber(this.getLevel()));
        } else {
            player.sendPacket((IStaticPacket)Msg.THE_DEATH_PENALTY_HAS_BEEN_LIFTED);
        }
        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    public void checkCharmOfLuck() {
        Player player = this.getPlayer();
        if (player != null) {
            for (Effect effect : player.getEffectList().getAllEffects()) {
                if (effect.getSkill().getId() != 2168 && effect.getSkill().getId() != 1325) continue;
                this.EU = true;
                return;
            }
        }
        this.EU = false;
    }
}


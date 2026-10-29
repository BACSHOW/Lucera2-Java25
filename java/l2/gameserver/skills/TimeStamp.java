/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.skills;

import l2.gameserver.model.Skill;

public class TimeStamp {
    private final int _id;
    private final int _level;
    private final long cM;
    private final long AI;

    public TimeStamp(int n, long l, long l2) {
        this._id = n;
        this._level = 0;
        this.cM = l2;
        this.AI = l;
    }

    public TimeStamp(Skill skill, long l) {
        this(skill, System.currentTimeMillis() + l, l);
    }

    public TimeStamp(Skill skill, long l, long l2) {
        this._id = skill.getId();
        this._level = skill.getLevel();
        this.cM = l2;
        this.AI = l;
    }

    public long getReuseBasic() {
        if (this.cM == 0L) {
            return this.getReuseCurrent();
        }
        return this.cM;
    }

    public long getReuseCurrent() {
        return Math.max(this.AI - System.currentTimeMillis(), 0L);
    }

    public long getEndTime() {
        return this.AI;
    }

    public boolean hasNotPassed() {
        return System.currentTimeMillis() < this.AI;
    }

    public int getId() {
        return this._id;
    }

    public int getLevel() {
        return this._level;
    }
}


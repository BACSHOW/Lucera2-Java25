/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import l2.gameserver.Config;
import l2.gameserver.model.base.ClassType2;

public final class SkillLearn
implements Comparable<SkillLearn> {
    private final int _id;
    private final int _level;
    private final int p;
    private final int Lr;
    private final int _itemId;
    private final long Ls;
    private final boolean Lt;
    private final ClassType2 Lu;
    private final boolean Lv;

    public SkillLearn(int n, int n2, int n3, int n4, int n5, long l, boolean bl, boolean bl2) {
        this._id = n;
        this._level = n2;
        this.p = n3;
        this.Lr = n4;
        this._itemId = n5;
        this.Ls = l;
        this.Lt = bl;
        this.Lu = ClassType2.None;
        this.Lv = bl2;
    }

    public SkillLearn(int n, int n2, int n3, int n4, int n5, long l, boolean bl, ClassType2 classType2, boolean bl2) {
        this._id = n;
        this._level = n2;
        this.p = n3;
        this.Lr = n4;
        this._itemId = n5;
        this.Ls = l;
        this.Lt = bl;
        this.Lu = classType2;
        this.Lv = bl2;
    }

    public int getId() {
        return this._id;
    }

    public int getLevel() {
        return this._level;
    }

    public int getMinLevel() {
        return this.p;
    }

    public int getCost() {
        return this.Lr;
    }

    public int getItemId() {
        return this._itemId;
    }

    public long getItemCount() {
        return this.Ls;
    }

    public boolean isClicked() {
        return this.Lt;
    }

    public boolean canAutoLearn() {
        if (!Config.AUTO_LEARN_FORGOTTEN_SKILLS && this.isClicked()) {
            return false;
        }
        if (this._id == 1405) {
            return Config.AUTO_LEARN_DIVINE_INSPIRATION;
        }
        return this.Lv;
    }

    @Override
    public int compareTo(SkillLearn skillLearn) {
        if (this.getId() == skillLearn.getId()) {
            return this.getLevel() - skillLearn.getLevel();
        }
        return this.getId() - skillLearn.getId();
    }

    public ClassType2 getClassType2() {
        return this.Lu;
    }
}


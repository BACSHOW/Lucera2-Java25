/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.templates.item.support;

import java.util.Map;
import l2.gameserver.Config;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.support.EnchantChanceType;
import l2.gameserver.templates.item.support.EnchantItem;
import l2.gameserver.templates.item.support.EnchantScrollOnFailAction;
import l2.gameserver.templates.item.support.EnchantTargetType;
import org.apache.commons.lang3.ArrayUtils;
import org.napile.primitive.Containers;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;

public class EnchantScroll
extends EnchantItem {
    private final EnchantScrollOnFailAction auS;
    private final boolean auT;
    private final boolean auU;
    private final int auV;
    private final int auW;
    private final Map<EnchantChanceType, double[]> auX;
    private IntSet auY = Containers.EMPTY_INT_SET;

    public EnchantScroll(int n, int n2, double d, ItemTemplate.Grade grade, int n3, int n4, EnchantTargetType enchantTargetType, EnchantScrollOnFailAction enchantScrollOnFailAction, int n5, boolean bl, boolean bl2, Map<EnchantChanceType, double[]> map) {
        super(n, d, grade, n3, n4, enchantTargetType);
        this.auW = n2;
        this.auV = n5;
        this.auS = enchantScrollOnFailAction;
        this.auT = bl;
        this.auU = bl2;
        this.auX = map;
    }

    public int getIncrement() {
        return this.auW;
    }

    public int getFailResultLevel() {
        return this.auV;
    }

    public EnchantScrollOnFailAction getOnFailAction() {
        return this.auS;
    }

    public void addItemRestrict(int n) {
        if (this.auY.isEmpty()) {
            this.auY = new HashIntSet();
        }
        this.auY.add(n);
    }

    public boolean isHasAbnormalVisualEffect() {
        return this.auU;
    }

    public boolean isInfallible() {
        return this.auT;
    }

    public boolean isUsableWith(ItemInstance itemInstance) {
        if (!this.auY.isEmpty() && !this.auY.contains(itemInstance.getItemId())) {
            return false;
        }
        int n = itemInstance.getEnchantLevel() + this.getIncrement();
        ItemTemplate.Grade grade = itemInstance.getCrystalType();
        if (grade.gradeOrd() != this.getGrade().gradeOrd()) {
            return false;
        }
        if (n < this.getMinLvl() || n > this.getMaxLvl()) {
            return false;
        }
        return this.getTargetType().isUsableOn(itemInstance);
    }

    public double getEnchantChance(ItemInstance itemInstance) {
        int n = itemInstance.getEnchantLevel();
        double[] dArray = null;
        long l = itemInstance.getTemplate().getBodyPart();
        if (32768L == l) {
            dArray = this.auX.get((Object)EnchantChanceType.FULL_ARMOR);
        } else if (l == 8L || l == 6L || l == 48L) {
            dArray = this.auX.get((Object)EnchantChanceType.JEWELRY);
        } else if (l == 128L || l == 16384L) {
            dArray = this.auX.get((Object)EnchantChanceType.WEAPON);
        } else if (ArrayUtils.contains(new long[]{64L, 256L, 512L, 1024L, 2048L, 4096L, 8192L, 1L}, l)) {
            dArray = this.auX.get((Object)EnchantChanceType.ARMOR);
        }
        if (dArray == null && Config.ALT_HAIR_TO_ACC_SLOT && (itemInstance.getTemplate().getBodyPart() == 65536 || itemInstance.getTemplate().getBodyPart() == 262144 || itemInstance.getTemplate().getBodyPart() == 524288)) {
            dArray = this.auX.get((Object)EnchantChanceType.ARMOR);
        }
        if (dArray == null) {
            return 0.0;
        }
        if (n >= dArray.length) {
            n = dArray.length - 1;
        }
        return dArray[n];
    }
}


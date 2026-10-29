/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Creature;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SysMsgContainer;

public class SystemMessage2
extends SysMsgContainer<SystemMessage2> {
    public SystemMessage2(SystemMsg systemMsg) {
        super(systemMsg);
    }

    public static SystemMessage2 obtainItems(int n, long l, int n2) {
        if (n == 57) {
            return (SystemMessage2)new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_ADENA).addLong(l);
        }
        if (l > 1L) {
            return (SystemMessage2)((SystemMessage2)new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S2_S1S).addItemName(n)).addLong(l);
        }
        if (n2 > 0) {
            return (SystemMessage2)((SystemMessage2)new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_A_S1_S2).addInteger(n2)).addItemName(n);
        }
        return (SystemMessage2)new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1).addItemName(n);
    }

    public static SystemMessage2 obtainItems(ItemInstance itemInstance) {
        return SystemMessage2.obtainItems(itemInstance.getItemId(), itemInstance.getCount(), itemInstance.isEquipable() ? itemInstance.getEnchantLevel() : 0);
    }

    public static SystemMessage2 obtainItemsBy(int n, long l, int n2, Creature creature) {
        if (l > 1L) {
            return (SystemMessage2)((SystemMessage2)((SystemMessage2)new SystemMessage2(SystemMsg.C1_HAS_OBTAINED_S3_S2).addName(creature)).addItemName(n)).addLong(l);
        }
        if (n2 > 0) {
            return (SystemMessage2)((SystemMessage2)((SystemMessage2)new SystemMessage2(SystemMsg.C1_HAS_OBTAINED_S2S3).addName(creature)).addInteger(n2)).addItemName(n);
        }
        return (SystemMessage2)((SystemMessage2)new SystemMessage2(SystemMsg.C1_HAS_OBTAINED_S2).addName(creature)).addItemName(n);
    }

    public static SystemMessage2 obtainItemsBy(ItemInstance itemInstance, Creature creature) {
        return SystemMessage2.obtainItemsBy(itemInstance.getItemId(), itemInstance.getCount(), itemInstance.isEquipable() ? itemInstance.getEnchantLevel() : 0, creature);
    }

    public static SystemMessage2 removeItems(int n, long l) {
        if (n == 57) {
            return (SystemMessage2)new SystemMessage2(SystemMsg.S1_ADENA_DISAPPEARED).addLong(l);
        }
        if (l > 1L) {
            return (SystemMessage2)((SystemMessage2)new SystemMessage2(SystemMsg.S2_S1_HAS_DISAPPEARED).addItemName(n)).addLong(l);
        }
        return (SystemMessage2)new SystemMessage2(SystemMsg.S1_HAS_DISAPPEARED).addItemName(n);
    }

    public static SystemMessage2 removeItems(ItemInstance itemInstance) {
        return SystemMessage2.removeItems(itemInstance.getItemId(), itemInstance.getCount());
    }

    @Override
    protected void writeImpl() {
        this.writeC(100);
        this.writeElements();
    }
}


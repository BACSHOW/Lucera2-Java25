/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.tables.SkillTable;

public final class ArmorSet {
    private final int CH;
    private final List<Integer> CI = new ArrayList<Integer>(1);
    private final List<Integer> CJ = new ArrayList<Integer>(1);
    private final List<Integer> CK = new ArrayList<Integer>(1);
    private final List<Integer> CL = new ArrayList<Integer>(1);
    private final List<Integer> CM = new ArrayList<Integer>(1);
    private final List<Integer> CN = new ArrayList<Integer>(1);
    private final List<Integer> CO = new ArrayList<Integer>(1);
    private final List<Skill> _skills = new ArrayList<Skill>(1);
    private final List<Skill> CP = new ArrayList<Skill>(1);
    private final List<Skill> CQ = new ArrayList<Skill>(1);
    private final List<Skill> CR = new ArrayList<Skill>(1);

    public ArmorSet(int n, String[] stringArray, String[] stringArray2, String[] stringArray3, String[] stringArray4, String[] stringArray5, String[] stringArray6, String[] stringArray7, String[] stringArray8, String[] stringArray9, String[] stringArray10, String[] stringArray11) {
        int n2;
        int n3;
        StringTokenizer stringTokenizer;
        this.CH = n;
        if (stringArray != null) {
            for (String string : stringArray) {
                this.CI.add(Integer.parseInt(string));
            }
        }
        if (stringArray2 != null) {
            for (String string : stringArray2) {
                this.CJ.add(Integer.parseInt(string));
            }
        }
        if (stringArray3 != null) {
            for (String string : stringArray3) {
                this.CK.add(Integer.parseInt(string));
            }
        }
        if (stringArray4 != null) {
            for (String string : stringArray4) {
                this.CL.add(Integer.parseInt(string));
            }
        }
        if (stringArray5 != null) {
            for (String string : stringArray5) {
                this.CM.add(Integer.parseInt(string));
            }
        }
        if (stringArray6 != null) {
            for (String string : stringArray6) {
                this.CN.add(Integer.parseInt(string));
            }
        }
        if (stringArray8 != null) {
            for (String string : stringArray8) {
                this.CO.add(Integer.parseInt(string));
            }
        }
        if (stringArray7 != null) {
            for (String string : stringArray7) {
                stringTokenizer = new StringTokenizer(string, "-");
                if (stringTokenizer.hasMoreTokens()) {
                    n3 = Integer.parseInt(stringTokenizer.nextToken());
                    n2 = Integer.parseInt(stringTokenizer.nextToken());
                    this._skills.add(SkillTable.getInstance().getInfo(n3, n2));
                }
                this._skills.add(SkillTable.getInstance().getInfo(3006, 1));
            }
        }
        if (stringArray9 != null) {
            for (String string : stringArray9) {
                stringTokenizer = new StringTokenizer(string, "-");
                if (!stringTokenizer.hasMoreTokens()) continue;
                n3 = Integer.parseInt(stringTokenizer.nextToken());
                n2 = Integer.parseInt(stringTokenizer.nextToken());
                this.CP.add(SkillTable.getInstance().getInfo(n3, n2));
            }
        }
        if (stringArray10 != null) {
            for (String string : stringArray10) {
                stringTokenizer = new StringTokenizer(string, "-");
                if (!stringTokenizer.hasMoreTokens()) continue;
                n3 = Integer.parseInt(stringTokenizer.nextToken());
                n2 = Integer.parseInt(stringTokenizer.nextToken());
                this.CQ.add(SkillTable.getInstance().getInfo(n3, n2));
            }
        }
        if (stringArray11 != null) {
            for (String string : stringArray11) {
                stringTokenizer = new StringTokenizer(string, "-");
                if (!stringTokenizer.hasMoreTokens()) continue;
                n3 = Integer.parseInt(stringTokenizer.nextToken());
                n2 = Integer.parseInt(stringTokenizer.nextToken());
                this.CR.add(SkillTable.getInstance().getInfo(n3, n2));
            }
        }
    }

    public boolean containAll(Player player) {
        PcInventory pcInventory = player.getInventory();
        ItemInstance itemInstance = pcInventory.getPaperdollItem(10);
        ItemInstance itemInstance2 = pcInventory.getPaperdollItem(11);
        ItemInstance itemInstance3 = pcInventory.getPaperdollItem(6);
        ItemInstance itemInstance4 = pcInventory.getPaperdollItem(9);
        ItemInstance itemInstance5 = pcInventory.getPaperdollItem(12);
        ItemInstance itemInstance6 = pcInventory.getPaperdollItem(0);
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        if (itemInstance != null) {
            n2 = itemInstance.getItemId();
        }
        if (itemInstance2 != null) {
            n2 = itemInstance2.getItemId();
        }
        if (itemInstance3 != null) {
            n3 = itemInstance3.getItemId();
        }
        if (itemInstance4 != null) {
            n4 = itemInstance4.getItemId();
        }
        if (itemInstance5 != null) {
            n5 = itemInstance5.getItemId();
        }
        if (itemInstance6 != null) {
            n6 = itemInstance6.getItemId();
        }
        return this.containAll(n, n2, n3, n4, n5, n6);
    }

    public boolean containAll(int n, int n2, int n3, int n4, int n5, int n6) {
        if (this.CI.isEmpty() && !this.CI.contains(n)) {
            return false;
        }
        if (!this.CJ.isEmpty() && !this.CJ.contains(n2)) {
            return false;
        }
        if (!this.CK.isEmpty() && !this.CK.contains(n3)) {
            return false;
        }
        if (!this.CL.isEmpty() && !this.CL.contains(n4)) {
            return false;
        }
        if (!this.CM.isEmpty() && !this.CM.contains(n5)) {
            return false;
        }
        return this.CN.isEmpty() || this.CN.contains(n6);
    }

    public boolean containItem(int n, int n2) {
        switch (n) {
            case 10: {
                return this.CI.contains(n2);
            }
            case 11: {
                return this.CJ.contains(n2);
            }
            case 6: {
                return this.CK.contains(n2);
            }
            case 9: {
                return this.CL.contains(n2);
            }
            case 12: {
                return this.CM.contains(n2);
            }
            case 0: {
                return this.CN.contains(n2);
            }
        }
        return false;
    }

    public int getSetById() {
        return this.CH;
    }

    public List<Integer> getChestItemIds() {
        return this.CI;
    }

    public List<Skill> getSkills() {
        return this._skills;
    }

    public List<Skill> getShieldSkills() {
        return this.CP;
    }

    public List<Skill> getEnchant6skills() {
        return this.CQ;
    }

    public List<Skill> getEnchant10skills() {
        return this.CR;
    }

    public boolean containShield(Player player) {
        PcInventory pcInventory = player.getInventory();
        ItemInstance itemInstance = pcInventory.getPaperdollItem(8);
        return itemInstance != null && this.CO.contains(itemInstance.getItemId());
    }

    public boolean containShield(int n) {
        if (this.CO.isEmpty()) {
            return false;
        }
        return this.CO.contains(n);
    }

    public boolean isEnchanted6(Player player) {
        if (!this.containAll(player)) {
            return false;
        }
        PcInventory pcInventory = player.getInventory();
        ItemInstance itemInstance = pcInventory.getPaperdollItem(10);
        ItemInstance itemInstance2 = pcInventory.getPaperdollItem(11);
        ItemInstance itemInstance3 = pcInventory.getPaperdollItem(6);
        ItemInstance itemInstance4 = pcInventory.getPaperdollItem(9);
        ItemInstance itemInstance5 = pcInventory.getPaperdollItem(12);
        ItemInstance itemInstance6 = pcInventory.getPaperdollItem(0);
        if (!this.CI.isEmpty() && itemInstance.getEnchantLevel() < Config.ARMOR_ENCHANT_6_SKILL) {
            return false;
        }
        if (!this.CJ.isEmpty() && itemInstance2.getEnchantLevel() < Config.ARMOR_ENCHANT_6_SKILL) {
            return false;
        }
        if (!this.CL.isEmpty() && itemInstance4.getEnchantLevel() < Config.ARMOR_ENCHANT_6_SKILL) {
            return false;
        }
        if (!this.CK.isEmpty() && itemInstance3.getEnchantLevel() < Config.ARMOR_ENCHANT_6_SKILL) {
            return false;
        }
        if (!this.CM.isEmpty() && itemInstance5.getEnchantLevel() < Config.ARMOR_ENCHANT_6_SKILL) {
            return false;
        }
        return this.CN.isEmpty() || itemInstance6.getEnchantLevel() >= Config.ARMOR_ENCHANT_6_SKILL;
    }

    public boolean isEnchanted10(Player player) {
        if (!this.containAll(player)) {
            return false;
        }
        PcInventory pcInventory = player.getInventory();
        ItemInstance itemInstance = pcInventory.getPaperdollItem(10);
        ItemInstance itemInstance2 = pcInventory.getPaperdollItem(11);
        ItemInstance itemInstance3 = pcInventory.getPaperdollItem(6);
        ItemInstance itemInstance4 = pcInventory.getPaperdollItem(9);
        ItemInstance itemInstance5 = pcInventory.getPaperdollItem(12);
        ItemInstance itemInstance6 = pcInventory.getPaperdollItem(0);
        if (!this.CI.isEmpty() && itemInstance.getEnchantLevel() < Config.ARMOR_ENCHANT_10_SKILL) {
            return false;
        }
        if (!this.CJ.isEmpty() && itemInstance2.getEnchantLevel() < Config.ARMOR_ENCHANT_10_SKILL) {
            return false;
        }
        if (!this.CL.isEmpty() && itemInstance4.getEnchantLevel() < Config.ARMOR_ENCHANT_10_SKILL) {
            return false;
        }
        if (!this.CK.isEmpty() && itemInstance3.getEnchantLevel() < Config.ARMOR_ENCHANT_10_SKILL) {
            return false;
        }
        if (!this.CM.isEmpty() && itemInstance5.getEnchantLevel() < Config.ARMOR_ENCHANT_10_SKILL) {
            return false;
        }
        return this.CN.isEmpty() || itemInstance6.getEnchantLevel() >= Config.ARMOR_ENCHANT_10_SKILL;
    }
}


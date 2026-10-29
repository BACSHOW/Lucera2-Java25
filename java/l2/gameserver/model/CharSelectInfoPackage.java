/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.Collection;
import l2.gameserver.dao.ItemsDAO;
import l2.gameserver.model.items.ItemInstance;

public class CharSelectInfoPackage {
    private String _name;
    private int CS = 0;
    private int CT = 199546;
    private long _exp = 0L;
    private int _sp = 0;
    private int CU = 0;
    private int CV = 0;
    private int CW = 0;
    private int CX = 0;
    private int CY = 0;
    private long CZ = 0L;
    private int Da = 0;
    private int Db = 0;
    private int Dc = 0;
    private int Dd = 0;
    private int _level = 1;
    private int De = 0;
    private int Df = 0;
    private int Dg = 0;
    private int _maxHp = 0;
    private double _currentHp = 0.0;
    private int _maxMp = 0;
    private double _currentMp = 0.0;
    private ItemInstance[] _paperdoll;
    private int Dh = 0;
    private int _x = 0;
    private int _y = 0;
    private int Di = 0;
    private int Dj = 20000;

    public CharSelectInfoPackage(int n, String string) {
        this.setObjectId(n);
        this._name = string;
        Collection<ItemInstance> collection = ItemsDAO.getInstance().loadItemsByOwnerIdAndLoc(n, ItemInstance.ItemLocation.PAPERDOLL);
        this._paperdoll = new ItemInstance[17];
        for (ItemInstance itemInstance : collection) {
            if (itemInstance.getEquipSlot() >= 17) continue;
            this._paperdoll[itemInstance.getEquipSlot()] = itemInstance;
        }
    }

    public int getObjectId() {
        return this.CS;
    }

    public void setObjectId(int n) {
        this.CS = n;
    }

    public int getCharId() {
        return this.CT;
    }

    public void setCharId(int n) {
        this.CT = n;
    }

    public int getClanId() {
        return this.CU;
    }

    public void setClanId(int n) {
        this.CU = n;
    }

    public int getClassId() {
        return this.CW;
    }

    public int getBaseClassId() {
        return this.CX;
    }

    public void setBaseClassId(int n) {
        this.CX = n;
    }

    public void setClassId(int n) {
        this.CW = n;
    }

    public double getCurrentHp() {
        return this._currentHp;
    }

    public void setCurrentHp(double d) {
        this._currentHp = d;
    }

    public double getCurrentMp() {
        return this._currentMp;
    }

    public void setCurrentMp(double d) {
        this._currentMp = d;
    }

    public int getDeleteTimer() {
        return this.CY;
    }

    public void setDeleteTimer(int n) {
        this.CY = n;
    }

    public long getLastAccess() {
        return this.CZ;
    }

    public void setLastAccess(long l) {
        this.CZ = l;
    }

    public long getExp() {
        return this._exp;
    }

    public void setExp(long l) {
        this._exp = l;
    }

    public int getFace() {
        return this.Da;
    }

    public void setFace(int n) {
        this.Da = n;
    }

    public int getHairColor() {
        return this.Dc;
    }

    public void setHairColor(int n) {
        this.Dc = n;
    }

    public int getHairStyle() {
        return this.Db;
    }

    public void setHairStyle(int n) {
        this.Db = n;
    }

    public int getPaperdollObjectId(int n) {
        ItemInstance itemInstance = this._paperdoll[n];
        if (itemInstance != null) {
            return itemInstance.getObjectId();
        }
        return 0;
    }

    public int getPaperdollAugmentationId(int n) {
        ItemInstance itemInstance = this._paperdoll[n];
        if (itemInstance != null && itemInstance.isAugmented()) {
            return itemInstance.getVariationStat1() & 0xFFFF | itemInstance.getVariationStat2() << 16;
        }
        return 0;
    }

    public int getPaperdollItemId(int n) {
        ItemInstance itemInstance = this._paperdoll[n];
        if (itemInstance != null) {
            return itemInstance.getItemId();
        }
        return 0;
    }

    public int getPaperdollEnchantEffect(int n) {
        ItemInstance itemInstance = this._paperdoll[n];
        if (itemInstance != null) {
            return itemInstance.getEnchantLevel();
        }
        return 0;
    }

    public int getLevel() {
        return this._level;
    }

    public void setLevel(int n) {
        this._level = n;
    }

    public int getMaxHp() {
        return this._maxHp;
    }

    public void setMaxHp(int n) {
        this._maxHp = n;
    }

    public int getMaxMp() {
        return this._maxMp;
    }

    public void setMaxMp(int n) {
        this._maxMp = n;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String string) {
        this._name = string;
    }

    public int getRace() {
        return this.CV;
    }

    public void setRace(int n) {
        this.CV = n;
    }

    public int getSex() {
        return this.Dd;
    }

    public void setSex(int n) {
        this.Dd = n;
    }

    public int getSp() {
        return this._sp;
    }

    public void setSp(int n) {
        this._sp = n;
    }

    public int getKarma() {
        return this.De;
    }

    public void setKarma(int n) {
        this.De = n;
    }

    public int getAccessLevel() {
        return this.Dh;
    }

    public void setAccessLevel(int n) {
        this.Dh = n;
    }

    public int getX() {
        return this._x;
    }

    public void setX(int n) {
        this._x = n;
    }

    public int getY() {
        return this._y;
    }

    public void setY(int n) {
        this._y = n;
    }

    public int getZ() {
        return this.Di;
    }

    public void setZ(int n) {
        this.Di = n;
    }

    public int getPk() {
        return this.Df;
    }

    public void setPk(int n) {
        this.Df = n;
    }

    public int getPvP() {
        return this.Dg;
    }

    public void setPvP(int n) {
        this.Dg = n;
    }

    public int getVitalityPoints() {
        return this.Dj;
    }

    public void setVitalityPoints(int n) {
        this.Dj = n;
    }
}


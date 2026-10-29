/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.manor.CropProcure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Manor {
    private static final Logger _log = LoggerFactory.getLogger(Manor.class);
    private static Manor Gd;
    private static Map<Integer, SeedData> Ge;

    public Manor() {
        Ge = new ConcurrentHashMap<Integer, SeedData>();
        this.bn();
    }

    public static Manor getInstance() {
        if (Gd == null) {
            Gd = new Manor();
        }
        return Gd;
    }

    public List<Integer> getAllCrops() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (SeedData seedData : Ge.values()) {
            if (arrayList.contains(seedData.getCrop()) || seedData.getCrop() == 0 || arrayList.contains(seedData.getCrop())) continue;
            arrayList.add(seedData.getCrop());
        }
        return arrayList;
    }

    public Map<Integer, SeedData> getAllSeeds() {
        return Ge;
    }

    public int getSeedBasicPrice(int n) {
        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
        if (itemTemplate != null) {
            return itemTemplate.getReferencePrice();
        }
        return 0;
    }

    public int getSeedBasicPriceByCrop(int n) {
        for (SeedData seedData : Ge.values()) {
            if (seedData.getCrop() != n) continue;
            return this.getSeedBasicPrice(seedData.getId());
        }
        return 0;
    }

    public int getCropBasicPrice(int n) {
        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
        if (itemTemplate != null) {
            return itemTemplate.getReferencePrice();
        }
        return 0;
    }

    public int getMatureCrop(int n) {
        for (SeedData seedData : Ge.values()) {
            if (seedData.getCrop() != n) continue;
            return seedData.getMature();
        }
        return 0;
    }

    public long getSeedBuyPrice(int n) {
        long l = this.getSeedBasicPrice(n) / 10;
        return l >= 0L ? l : 1L;
    }

    public int getSeedMinLevel(int n) {
        SeedData seedData = Ge.get(n);
        if (seedData != null) {
            return seedData.getLevel() - 5;
        }
        return -1;
    }

    public int getSeedMaxLevel(int n) {
        SeedData seedData = Ge.get(n);
        if (seedData != null) {
            return seedData.getLevel() + 5;
        }
        return -1;
    }

    public int getSeedLevelByCrop(int n) {
        for (SeedData seedData : Ge.values()) {
            if (seedData.getCrop() != n) continue;
            return seedData.getLevel();
        }
        return 0;
    }

    public int getSeedLevel(int n) {
        SeedData seedData = Ge.get(n);
        if (seedData != null) {
            return seedData.getLevel();
        }
        return -1;
    }

    public boolean isAlternative(int n) {
        for (SeedData seedData : Ge.values()) {
            if (seedData.getId() != n) continue;
            return seedData.isAlternative();
        }
        return false;
    }

    public int getCropType(int n) {
        SeedData seedData = Ge.get(n);
        if (seedData != null) {
            return seedData.getCrop();
        }
        return -1;
    }

    public synchronized int getRewardItem(int n, int n2) {
        for (SeedData seedData : Ge.values()) {
            if (seedData.getCrop() != n) continue;
            return seedData.getReward(n2);
        }
        return -1;
    }

    public synchronized long getRewardAmountPerCrop(int n, int n2, int n3) {
        CropProcure cropProcure = ResidenceHolder.getInstance().getResidence(Castle.class, n).getCropProcure(0).get(n2);
        for (SeedData seedData : Ge.values()) {
            if (seedData.getCrop() != n2) continue;
            return cropProcure.getPrice() / (long)this.getCropBasicPrice(seedData.getReward(n3));
        }
        return -1L;
    }

    public synchronized int getRewardItemBySeed(int n, int n2) {
        SeedData seedData = Ge.get(n);
        if (seedData != null) {
            return seedData.getReward(n2);
        }
        return 0;
    }

    public List<SeedData> getCropsForCastle(int n) {
        ArrayList<SeedData> arrayList = new ArrayList<SeedData>(20);
        for (SeedData seedData : Ge.values()) {
            if (seedData.getManorId() != n) continue;
            arrayList.add(seedData);
        }
        return arrayList;
    }

    public List<SeedData> getSeedsForCastle(int n) {
        ArrayList<SeedData> arrayList = new ArrayList<SeedData>(20);
        for (SeedData seedData : Ge.values()) {
            if (seedData.getManorId() != n) continue;
            arrayList.add(seedData);
        }
        return arrayList;
    }

    public int getCastleIdForSeed(int n) {
        SeedData seedData = Ge.get(n);
        if (seedData != null) {
            return seedData.getManorId();
        }
        return 0;
    }

    public long getSeedSaleLimit(int n) {
        SeedData seedData = Ge.get(n);
        if (seedData != null) {
            return seedData.getSeedLimit();
        }
        return 0L;
    }

    public long getCropPuchaseLimit(int n) {
        for (SeedData seedData : Ge.values()) {
            if (seedData.getCrop() != n) continue;
            return seedData.getCropLimit();
        }
        return 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void bn() {
        BufferedReader bufferedReader = null;
        try {
            File file = new File(Config.DATAPACK_ROOT, "data/seeds.csv");
            bufferedReader = new LineNumberReader(new BufferedReader(new FileReader(file)));
            String string = null;
            while ((string = ((LineNumberReader)bufferedReader).readLine()) != null) {
                if (string.trim().length() == 0 || string.startsWith("#")) continue;
                SeedData seedData = this.r(string);
                Ge.put(seedData.getId(), seedData);
            }
            _log.info("ManorManager: Loaded " + Ge.size() + " seeds");
        }
        catch (FileNotFoundException fileNotFoundException) {
            _log.info("seeds.csv is missing in data folder");
        }
        catch (Exception exception) {
            _log.error("Error while loading seeds!", exception);
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
            catch (Exception exception) {}
        }
    }

    private SeedData r(String string) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, ";");
        int n = Integer.parseInt(stringTokenizer.nextToken());
        int n2 = Integer.parseInt(stringTokenizer.nextToken());
        int n3 = Integer.parseInt(stringTokenizer.nextToken());
        int n4 = Integer.parseInt(stringTokenizer.nextToken());
        int n5 = Integer.parseInt(stringTokenizer.nextToken());
        long l = Math.round((double)Integer.parseInt(stringTokenizer.nextToken()) * Config.RATE_MANOR);
        long l2 = Math.round((double)Integer.parseInt(stringTokenizer.nextToken()) * Config.RATE_MANOR);
        int n6 = Integer.parseInt(stringTokenizer.nextToken());
        int n7 = Integer.parseInt(stringTokenizer.nextToken());
        int n8 = Integer.parseInt(stringTokenizer.nextToken());
        return new SeedData(n5, n2, n4).setData(n3, n6, n7, n, n8, l, l2);
    }

    public class SeedData {
        private int _id;
        private int _level;
        private int Gf;
        private int Gg;
        private int _type1;
        private int _type2;
        private int Gh;
        private int Gi;
        private long Gj;
        private long Gk;

        public SeedData(int n, int n2, int n3) {
            this._level = n;
            this.Gf = n2;
            this.Gg = n3;
        }

        public SeedData setData(int n, int n2, int n3, int n4, int n5, long l, long l2) {
            this._id = n;
            this._type1 = n2;
            this._type2 = n3;
            this.Gh = n4;
            this.Gi = n5;
            this.Gj = l;
            this.Gk = l2;
            return this;
        }

        public int getManorId() {
            return this.Gh;
        }

        public int getId() {
            return this._id;
        }

        public int getCrop() {
            return this.Gf;
        }

        public int getMature() {
            return this.Gg;
        }

        public int getReward(int n) {
            return n == 1 ? this._type1 : this._type2;
        }

        public int getLevel() {
            return this._level;
        }

        public boolean isAlternative() {
            return this.Gi == 1;
        }

        public long getSeedLimit() {
            return this.Gj;
        }

        public long getCropLimit() {
            return this.Gk;
        }
    }
}


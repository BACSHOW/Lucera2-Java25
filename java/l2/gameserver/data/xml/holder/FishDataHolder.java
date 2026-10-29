/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.gameserver.templates.item.support.FishGroup;
import l2.gameserver.templates.item.support.FishTemplate;
import l2.gameserver.templates.item.support.LureTemplate;
import l2.gameserver.templates.item.support.LureType;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

public class FishDataHolder
extends AbstractHolder {
    private static final FishDataHolder vY = new FishDataHolder();
    private List<FishTemplate> vZ = new ArrayList<FishTemplate>();
    private IntObjectMap<LureTemplate> wa = new HashIntObjectMap<LureTemplate>();
    private IntObjectMap<Map<LureType, Map<FishGroup, Integer>>> wb = new HashIntObjectMap<Map<LureType, Map<FishGroup, Integer>>>();

    public static FishDataHolder getInstance() {
        return vY;
    }

    public void addFish(FishTemplate fishTemplate) {
        this.vZ.add(fishTemplate);
    }

    public void addLure(LureTemplate lureTemplate) {
        this.wa.put(lureTemplate.getItemId(), lureTemplate);
    }

    public void addDistribution(int n, LureType lureType, Map<FishGroup, Integer> map) {
        Map<LureType, Map<FishGroup, Integer>> map2 = this.wb.get(n);
        if (map2 == null) {
            map2 = new HashMap<LureType, Map<FishGroup, Integer>>();
            this.wb.put(n, map2);
        }
        map2.put(lureType, map);
    }

    @Override
    public void log() {
        this.info("load " + this.vZ.size() + " fish(es).");
        this.info("load " + this.wa.size() + " lure(s).");
        this.info("load " + this.wb.size() + " distribution(s).");
    }

    @Override
    @Deprecated
    public int size() {
        return 0;
    }

    @Override
    public void clear() {
        this.vZ.clear();
        this.wa.clear();
        this.wb.clear();
    }
}


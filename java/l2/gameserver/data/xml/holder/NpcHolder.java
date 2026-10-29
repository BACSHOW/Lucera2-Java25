/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import gnu.trove.TIntObjectIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.AbstractHolder;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.templates.npc.NpcTemplate;

public final class NpcHolder
extends AbstractHolder {
    private static final NpcHolder ww = new NpcHolder();
    private TIntObjectHashMap<NpcTemplate> wx = new TIntObjectHashMap(20000);
    private TIntObjectHashMap<List<NpcTemplate>> wy;
    private NpcTemplate[] wz;
    private Map<String, NpcTemplate> wA;

    public static NpcHolder getInstance() {
        return ww;
    }

    NpcHolder() {
    }

    public void addTemplate(NpcTemplate npcTemplate) {
        this.wx.put(npcTemplate.npcId, npcTemplate);
    }

    public NpcTemplate getTemplate(int n) {
        NpcTemplate npcTemplate = ArrayUtils.valid(this.wz, n);
        if (npcTemplate == null) {
            this.warn("Not defined npc id : " + n + ", or out of range!", new Exception());
            return null;
        }
        return this.wz[n];
    }

    public NpcTemplate getTemplateByName(String string) {
        return this.wA.get(string.toLowerCase());
    }

    public List<NpcTemplate> getAllOfLevel(int n) {
        return this.wy.get(n);
    }

    public NpcTemplate[] getAll() {
        return this.wx.getValues(new NpcTemplate[this.wx.size()]);
    }

    private void bk() {
        this.wy = new TIntObjectHashMap();
        this.wA = new HashMap<String, NpcTemplate>();
        int n = 0;
        for (int n2 : this.wx.keys()) {
            if (n2 <= n) continue;
            n = n2;
        }
        this.wz = new NpcTemplate[n + 1];
        Object object = this.wx.iterator();
        while (((TIntObjectIterator)object).hasNext()) {
            NpcTemplate npcTemplate;
            ((TIntObjectIterator)object).advance();
            int n3 = ((TIntObjectIterator)object).key();
            this.wz[n3] = npcTemplate = (NpcTemplate)((TIntObjectIterator)object).value();
            List<NpcTemplate> list = this.wy.get(npcTemplate.level);
            if (list == null) {
                list = new ArrayList<NpcTemplate>();
                this.wy.put(n3, list);
            }
            list.add(npcTemplate);
            this.wA.put(npcTemplate.name.toLowerCase(), npcTemplate);
        }
    }

    @Override
    protected void process() {
        this.bk();
    }

    @Override
    public int size() {
        return this.wx.size();
    }

    @Override
    public void clear() {
        this.wA.clear();
        this.wx.clear();
    }
}


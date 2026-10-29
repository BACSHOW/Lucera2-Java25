/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.stats.Env;
import l2.gameserver.stats.Stats;
import l2.gameserver.stats.funcs.Func;
import l2.gameserver.templates.item.ItemTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatModifier
implements OnPlayerEnterListener,
ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(StatMod.class);
    private static final StatModifier bYa = new StatModifier();
    private static final File bWZ = new File(Config.DATAPACK_ROOT, "data/stats_custom_mod.xml");
    private static Map<Stats, Map<Integer, List<StatMod>>> bYb = new HashMap<Stats, Map<Integer, List<StatMod>>>();

    private List<StatMod> a(Element element, ModCond[] modCondArray) {
        ArrayList<StatMod> arrayList = new ArrayList<StatMod>();
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            StatMod statMod;
            Element element2 = iterator.next();
            if ("mul".equalsIgnoreCase(element2.getName())) {
                statMod = new StatMod(modCondArray, Stats.valueOfXml(element2.attributeValue("stat")));
                statMod.addMul(Double.parseDouble(element2.attributeValue("val")) - 1.0);
                arrayList.add(statMod);
                continue;
            }
            if (!"add".equalsIgnoreCase(element2.getName())) continue;
            statMod = new StatMod(modCondArray, Stats.valueOfXml(element2.attributeValue("stat")));
            statMod.setAdd(Double.parseDouble(element2.attributeValue("val")));
            arrayList.add(statMod);
        }
        return arrayList;
    }

    private List<StatMod> a(Element element, final int n, ModCond[] modCondArray) {
        ArrayList<StatMod> arrayList = new ArrayList<StatMod>();
        modCondArray = ArrayUtils.add(modCondArray, new ModCond(){

            @Override
            public boolean test(Player player, Creature creature) {
                return creature != null && creature.isPlayer() && creature.getPlayer().getActiveClassId() == n;
            }
        });
        arrayList.addAll(this.a(element, modCondArray));
        return arrayList;
    }

    private List<StatMod> b(Element element, ModCond[] modCondArray) {
        ArrayList<StatMod> arrayList = new ArrayList<StatMod>();
        int n = Integer.parseInt(element.attributeValue("itemId"));
        final ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n);
        modCondArray = ArrayUtils.add(modCondArray, new ModCond(){

            private boolean a(PcInventory pcInventory, ItemTemplate itemTemplate2) {
                if (pcInventory == null || itemTemplate2 == null) {
                    return false;
                }
                int n = Inventory.getPaperdollIndex(itemTemplate2.getBodyPart());
                if (n < 0) {
                    return false;
                }
                switch (n) {
                    case 14: {
                        return pcInventory.getPaperdollItemId(7) == itemTemplate2.getItemId();
                    }
                }
                return pcInventory.getPaperdollItemId(n) == itemTemplate2.getItemId();
            }

            @Override
            public boolean test(Player player, Creature creature) {
                if (player == null) {
                    return false;
                }
                return this.a(player.getInventory(), itemTemplate);
            }
        });
        arrayList.addAll(this.a(element, modCondArray));
        return arrayList;
    }

    private List<StatMod> b(Element element, final int n, ModCond[] modCondArray) {
        ArrayList<StatMod> arrayList = new ArrayList<StatMod>();
        modCondArray = ArrayUtils.add(modCondArray, new ModCond(){

            @Override
            public boolean test(Player player, Creature creature) {
                return player != null && player.getActiveClassId() == n;
            }
        });
        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element element2 = iterator.next();
            if ("targetPlayer".equalsIgnoreCase(element2.getName())) {
                int n2 = Integer.parseInt(element2.attributeValue("classId"));
                arrayList.addAll(this.a(element2, n2, modCondArray));
                continue;
            }
            if (!"equipedWith".equalsIgnoreCase(element2.getName())) continue;
            arrayList.addAll(this.b(element2, modCondArray));
        }
        arrayList.addAll(this.a(element, (ModCond[])modCondArray.clone()));
        return arrayList;
    }

    private Pair<Boolean, Map<Stats, Map<Integer, List<StatMod>>>> dV() {
        try {
            SAXReader sAXReader = new SAXReader(false);
            Document document = sAXReader.read(bWZ);
            Element element = document.getRootElement();
            if (!"list".equalsIgnoreCase(element.getName())) {
                throw new RuntimeException();
            }
            if (!Boolean.parseBoolean(element.attributeValue("enabled", "false"))) {
                return Pair.of(Boolean.FALSE, Collections.emptyMap());
            }
            HashMap hashMap = new HashMap();
            Iterator<Element> iterator = element.elementIterator();
            while (iterator.hasNext()) {
                Element element2 = iterator.next();
                if (!"player".equalsIgnoreCase(element2.getName())) continue;
                int n = Integer.parseInt(element2.attributeValue("classId"));
                for (StatMod statMod : this.b(element2, n, new ModCond[0])) {
                    ArrayList<StatMod> arrayList;
                    HashMap<Integer, ArrayList<StatMod>> hashMap2 = (HashMap<Integer, ArrayList<StatMod>>)hashMap.get((Object)statMod.getStat());
                    if (hashMap2 == null) {
                        hashMap2 = new HashMap<Integer, ArrayList<StatMod>>();
                        hashMap.put(statMod.getStat(), hashMap2);
                    }
                    if ((arrayList = (ArrayList<StatMod>)hashMap2.get(n)) == null) {
                        arrayList = new ArrayList<StatMod>();
                        hashMap2.put(n, arrayList);
                    }
                    arrayList.add(statMod);
                }
            }
            return Pair.of(Boolean.TRUE, hashMap);
        }
        catch (Exception exception) {
            sQ.error(exception.getMessage(), exception);
            return Pair.of(Boolean.FALSE, Collections.emptyMap());
        }
    }

    @Override
    public void onLoad() {
        bYb.clear();
        Pair<Boolean, Map<Stats, Map<Integer, List<StatMod>>>> pair = this.dV();
        if (!pair.getLeft().booleanValue()) {
            sQ.info("StatModifier: Disabled.");
            return;
        }
        PlayerListenerList.addGlobal(bYa);
        bYb.putAll(pair.getRight());
        sQ.info("StatModifier: Enabled. Loaded mods for {} stats.", (Object)bYb.size());
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
        PlayerListenerList.removeGlobal(bYa);
        if (!bYb.isEmpty()) {
            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                bYa.aC(player);
            }
        }
    }

    private void aC(Player player) {
        if (player == null) {
            return;
        }
        player.removeStatsOwner(this);
    }

    private void aD(Player player) {
        for (Map.Entry<Stats, Map<Integer, List<StatMod>>> entry : bYb.entrySet()) {
            Stats stats = entry.getKey();
            final Map<Integer, List<StatMod>> map = entry.getValue();
            player.addStatFunc(new Func(stats, 80, this){

                @Override
                public void calc(Env env) {
                    if (env.character == null || !env.character.isPlayer()) {
                        return;
                    }
                    Player player = (Player)env.character;
                    List list = (List)map.get(player.getActiveClassId());
                    if (list == null || list.isEmpty()) {
                        return;
                    }
                    double d = 1.0;
                    double d2 = 0.0;
                    for (Object __raw_statMod_227 : (Iterable<?>)(Object)(list)) {
                       StatMod statMod = (StatMod)__raw_statMod_227;
                        if (!statMod.test(player, env.target)) continue;
                        d += statMod.getMul() - 1.0;
                        d2 += statMod.getAdd();
                    }
                    env.value *= d;
                    env.value += d2;
                }
            });
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (player == null) {
            return;
        }
        if (!bYb.isEmpty()) {
            bYa.aD(player);
        }
    }

    private static class StatMod {
        private final ModCond[] bYc;
        private final Stats _stat;
        private double bYd = 1.0;
        private double bYe = 0.0;

        private StatMod(ModCond[] modCondArray, Stats stats) {
            this.bYc = (ModCond[])modCondArray.clone();
            this._stat = stats;
        }

        public boolean test(Player player, Creature creature) {
            for (ModCond modCond : this.bYc) {
                if (modCond.test(player, creature)) continue;
                return false;
            }
            return true;
        }

        public Stats getStat() {
            return this._stat;
        }

        public double getMul() {
            return this.bYd;
        }

        public void addMul(double d) {
            this.bYd += d;
        }

        public double getAdd() {
            return this.bYe;
        }

        public void setAdd(double d) {
            this.bYe = d;
        }
    }

    private static abstract class ModCond {
        private ModCond() {
        }

        public abstract boolean test(Player var1, Creature var2);
    }
}


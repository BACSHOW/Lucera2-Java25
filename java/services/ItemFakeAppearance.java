/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.listener.inventory.OnDisplayListener;
import l2.gameserver.listener.inventory.OnEquipListener;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.model.items.Inventory;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.item.ItemTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemFakeAppearance
implements OnPlayerEnterListener,
ScriptFile {
    private static final Logger sQ = LoggerFactory.getLogger(ItemFakeAppearance.class);
    private static final ItemFakeAppearance bWY = new ItemFakeAppearance();
    private static final File bWZ = new File(Config.DATAPACK_ROOT, "data/item_fake_appearance.xml");
    private static Map<Integer, Map<Integer, Integer>> bXa = new HashMap<Integer, Map<Integer, Integer>>();

    private Pair<Boolean, Map<Integer, Map<Integer, Integer>>> dV() {
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
                if (!"item".equalsIgnoreCase(element2.getName())) continue;
                int n = Integer.parseInt(element2.attributeValue("itemId"));
                HashMap<Integer, Integer> hashMap2 = new HashMap<Integer, Integer>();
                Iterator<Element> iterator2 = element2.elementIterator();
                while (iterator2.hasNext()) {
                    Element element3 = iterator2.next();
                    if (!"display".equalsIgnoreCase(element3.getName())) continue;
                    int n2 = Integer.parseInt(element3.attributeValue("itemId"));
                    ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n2);
                    int n3 = Inventory.getPaperdollIndex(itemTemplate.getBodyPart());
                    hashMap2.put(n3, itemTemplate.getItemId());
                }
                hashMap.put(n, hashMap2);
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
        Pair<Boolean, Map<Integer, Map<Integer, Integer>>> pair = bWY.dV();
        if (!pair.getLeft().booleanValue()) {
            bXa.clear();
            sQ.info("ItemFakeAppearance: Disabled.");
            return;
        }
        if (pair.getRight().isEmpty()) {
            bXa.clear();
            sQ.info("ItemFakeAppearance: No item(s). Disabled.");
            return;
        }
        bXa = pair.getRight();
        sQ.info("ItemFakeAppearance: Loaded " + bXa.size() + " item(s) appearance.");
        PlayerListenerList.addGlobal(bWY);
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
        PlayerListenerList.removeGlobal(bWY);
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (bXa.isEmpty()) {
            return;
        }
        player.getInventory().addListener(new ItemFakeAppearanceEquipListener());
        for (ItemInstance itemInstance : player.getInventory().getPaperdollItems()) {
            if (itemInstance == null || !bXa.containsKey(itemInstance.getItemId()) || !this.s(player, itemInstance.getItemId())) continue;
            return;
        }
    }

    private boolean s(Player player, int n) {
        Map<Integer, Integer> map = bXa.get(n);
        if (map == null || map.isEmpty()) {
            return false;
        }
        player.getInventory().setOnDisplayListener(new ItemFakeAppearanceDisplayListener(map));
        return true;
    }

    private boolean ay(Player player) {
        if (player.getInventory().getOnDisplayListener() == null) {
            return false;
        }
        player.getInventory().setOnDisplayListener(null);
        return true;
    }

    private static final class ItemFakeAppearanceEquipListener
    implements OnEquipListener {
        private ItemFakeAppearanceEquipListener() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void onEquip(int n, ItemInstance itemInstance, Playable playable) {
            Player player = playable.getPlayer();
            if (itemInstance == null || player == null) {
                return;
            }
            int n2 = itemInstance.getItemId();
            PcInventory pcInventory = player.getInventory();
            Map map = (Map)bXa.get(n2);
            if (map == null || map.isEmpty()) {
                return;
            }
            pcInventory.writeLock();
            LinkedHashMap<Integer, ItemInstance> linkedHashMap = new LinkedHashMap<Integer, ItemInstance>();
            try {
                for (Object __raw_serializable_159 : (Iterable<?>)(Object)(map.keySet())) {
                   Serializable serializable = (Serializable)__raw_serializable_159;
                    ItemInstance itemInstance2 = pcInventory.getPaperdollItem((int)serializable);
                    if (itemInstance2 == null || itemInstance2 == itemInstance) continue;
                    linkedHashMap.put((Integer)serializable, itemInstance2);
                }
                for (Serializable serializable : linkedHashMap.values()) {
                    pcInventory.unEquipItem((ItemInstance)serializable);
                }
                bWY.s(player, n2);
                player.sendUserInfo(true);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            finally {
                for (ItemInstance itemInstance3 : linkedHashMap.values()) {
                    pcInventory.equipItem(itemInstance3);
                }
                pcInventory.writeUnlock();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void onUnequip(int n, ItemInstance itemInstance, Playable playable) {
            Player player = playable.getPlayer();
            if (itemInstance == null || player == null) {
                return;
            }
            int n2 = itemInstance.getItemId();
            PcInventory pcInventory = player.getInventory();
            Map map = (Map)bXa.get(n2);
            if (map == null || map.isEmpty()) {
                return;
            }
            pcInventory.writeLock();
            LinkedHashMap<Integer, ItemInstance> linkedHashMap = new LinkedHashMap<Integer, ItemInstance>();
            try {
                for (Object __raw_serializable_199 : (Iterable<?>)(Object)(map.keySet())) {
                   Serializable serializable = (Serializable)__raw_serializable_199;
                    ItemInstance itemInstance2 = pcInventory.getPaperdollItem((int)serializable);
                    if (itemInstance2 == null || itemInstance2 == itemInstance) continue;
                    linkedHashMap.put((Integer)serializable, itemInstance2);
                }
                for (Serializable serializable : linkedHashMap.values()) {
                    pcInventory.unEquipItem((ItemInstance)serializable);
                }
                bWY.ay(playable.getPlayer());
                player.sendUserInfo(true);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            finally {
                for (ItemInstance itemInstance3 : linkedHashMap.values()) {
                    pcInventory.equipItem(itemInstance3);
                }
                pcInventory.writeUnlock();
            }
        }
    }

    private static class ItemFakeAppearanceDisplayListener
    implements OnDisplayListener {
        private final Map<Integer, Integer> bXb;

        private ItemFakeAppearanceDisplayListener(Map<Integer, Integer> map) {
            this.bXb = map;
        }

        @Override
        public Integer onDisplay(int n, ItemInstance itemInstance, Playable playable) {
            return this.bXb.get(n);
        }
    }
}


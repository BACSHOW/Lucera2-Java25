/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import gnu.trove.TIntObjectHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.MultiSellEntry;
import l2.gameserver.model.base.MultiSellIngredient;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MultiSellList;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.XMLUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MultiSellHolder {
    private static final Logger _log = LoggerFactory.getLogger(MultiSellHolder.class);
    private static MultiSellHolder wn = new MultiSellHolder();
    private static final String wo = "production";
    private static final String wp = "ingredient";
    private TIntObjectHashMap<MultiSellListContainer> wq = new TIntObjectHashMap();

    public static MultiSellHolder getInstance() {
        return wn;
    }

    public MultiSellListContainer getList(int n) {
        return this.wq.get(n);
    }

    public MultiSellHolder() {
        this.bn();
    }

    public void reload() {
        this.bn();
    }

    private void bn() {
        this.wq.clear();
        this.parse();
    }

    private void a(String string, List<File> list) {
        File[] fileArray;
        File file = new File(Config.DATAPACK_ROOT, "data/" + string);
        if (!file.exists()) {
            _log.info("Dir " + file.getAbsolutePath() + " not exists");
            return;
        }
        for (File file2 : fileArray = file.listFiles()) {
            if (file2.getName().endsWith(".xml")) {
                list.add(file2);
                continue;
            }
            if (!file2.isDirectory() || file2.getName().equals(".svn")) continue;
            this.a(string + "/" + file2.getName(), list);
        }
    }

    public void addMultiSellListContainer(int n, MultiSellListContainer multiSellListContainer) {
        if (this.wq.containsKey(n)) {
            _log.warn("MultiSell redefined: " + n);
        }
        multiSellListContainer.setListId(n);
        this.wq.put(n, multiSellListContainer);
    }

    public MultiSellListContainer remove(String string) {
        return this.remove(new File(string));
    }

    public MultiSellListContainer remove(File file) {
        return this.remove(Integer.parseInt(file.getName().replaceAll(".xml", "")));
    }

    public MultiSellListContainer remove(int n) {
        return this.wq.remove(n);
    }

    public void parseFile(File file) {
        int n = 0;
        try {
            n = Integer.parseInt(file.getName().replaceAll(".xml", ""));
        }
        catch (Exception exception) {
            _log.error("Error loading file " + file, exception);
            return;
        }
        Document document = null;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setIgnoringComments(true);
            document = documentBuilderFactory.newDocumentBuilder().parse(file);
        }
        catch (Exception exception) {
            _log.error("Error loading file " + file, exception);
            return;
        }
        try {
            this.addMultiSellListContainer(n, this.parseDocument(document, n));
        }
        catch (Exception exception) {
            _log.error("Error in file " + file, exception);
        }
    }

    private void parse() {
        ArrayList<File> arrayList = new ArrayList<File>();
        this.a("multisell", arrayList);
        for (File file : arrayList) {
            this.parseFile(file);
        }
    }

    protected MultiSellListContainer parseDocument(Document document, int n) {
        MultiSellListContainer multiSellListContainer = new MultiSellListContainer();
        int n2 = 1;
        for (Node node = document.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (!"list".equalsIgnoreCase(node.getNodeName())) continue;
            for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                if ("item".equalsIgnoreCase(node2.getNodeName())) {
                    MultiSellEntry multiSellEntry = this.parseEntry(node2, n);
                    if (multiSellEntry == null) continue;
                    multiSellEntry.setEntryId(n2++);
                    multiSellListContainer.addEntry(multiSellEntry);
                    continue;
                }
                if (!"config".equalsIgnoreCase(node2.getNodeName())) continue;
                multiSellListContainer.setShowAll(XMLUtil.getAttributeBooleanValue(node2, "showall", true));
                multiSellListContainer.setNoTax(XMLUtil.getAttributeBooleanValue(node2, "notax", false));
                multiSellListContainer.setKeepEnchant(XMLUtil.getAttributeBooleanValue(node2, "keepenchanted", false));
                multiSellListContainer.setNoKey(XMLUtil.getAttributeBooleanValue(node2, "nokey", false));
            }
        }
        return multiSellListContainer;
    }

    protected MultiSellEntry parseEntry(Node node, int n) {
        Object object;
        MultiSellEntry multiSellEntry = new MultiSellEntry();
        for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
            ItemTemplate itemTemplate;
            long l;
            int n2;
            if (wp.equalsIgnoreCase(node2.getNodeName())) {
                n2 = Integer.parseInt(node2.getAttributes().getNamedItem("id").getNodeValue());
                l = Long.parseLong(node2.getAttributes().getNamedItem("count").getNodeValue());
                object = new MultiSellIngredient(n2, l);
                if (node2.getAttributes().getNamedItem("enchant") != null) {
                    ((MultiSellIngredient)object).setItemEnchant(Integer.parseInt(node2.getAttributes().getNamedItem("enchant").getNodeValue()));
                }
                if (node2.getAttributes().getNamedItem("mantainIngredient") != null) {
                    ((MultiSellIngredient)object).setMantainIngredient(Boolean.parseBoolean(node2.getAttributes().getNamedItem("mantainIngredient").getNodeValue()));
                }
                if (node2.getAttributes().getNamedItem("fireAttr") != null) {
                    ((MultiSellIngredient)object).getItemAttributes().setFire(Integer.parseInt(node2.getAttributes().getNamedItem("fireAttr").getNodeValue()));
                }
                if (node2.getAttributes().getNamedItem("waterAttr") != null) {
                    ((MultiSellIngredient)object).getItemAttributes().setWater(Integer.parseInt(node2.getAttributes().getNamedItem("waterAttr").getNodeValue()));
                }
                if (node2.getAttributes().getNamedItem("earthAttr") != null) {
                    ((MultiSellIngredient)object).getItemAttributes().setEarth(Integer.parseInt(node2.getAttributes().getNamedItem("earthAttr").getNodeValue()));
                }
                if (node2.getAttributes().getNamedItem("windAttr") != null) {
                    ((MultiSellIngredient)object).getItemAttributes().setWind(Integer.parseInt(node2.getAttributes().getNamedItem("windAttr").getNodeValue()));
                }
                if (node2.getAttributes().getNamedItem("holyAttr") != null) {
                    ((MultiSellIngredient)object).getItemAttributes().setHoly(Integer.parseInt(node2.getAttributes().getNamedItem("holyAttr").getNodeValue()));
                }
                if (node2.getAttributes().getNamedItem("unholyAttr") != null) {
                    ((MultiSellIngredient)object).getItemAttributes().setUnholy(Integer.parseInt(node2.getAttributes().getNamedItem("unholyAttr").getNodeValue()));
                }
                multiSellEntry.addIngredient((MultiSellIngredient)object);
                continue;
            }
            if (!wo.equalsIgnoreCase(node2.getNodeName())) continue;
            n2 = Integer.parseInt(node2.getAttributes().getNamedItem("id").getNodeValue());
            l = Long.parseLong(node2.getAttributes().getNamedItem("count").getNodeValue());
            object = new MultiSellIngredient(n2, l);
            if (node2.getAttributes().getNamedItem("enchant") != null) {
                ((MultiSellIngredient)object).setItemEnchant(Integer.parseInt(node2.getAttributes().getNamedItem("enchant").getNodeValue()));
            }
            if (node2.getAttributes().getNamedItem("fireAttr") != null) {
                ((MultiSellIngredient)object).getItemAttributes().setFire(Integer.parseInt(node2.getAttributes().getNamedItem("fireAttr").getNodeValue()));
            }
            if (node2.getAttributes().getNamedItem("waterAttr") != null) {
                ((MultiSellIngredient)object).getItemAttributes().setWater(Integer.parseInt(node2.getAttributes().getNamedItem("waterAttr").getNodeValue()));
            }
            if (node2.getAttributes().getNamedItem("earthAttr") != null) {
                ((MultiSellIngredient)object).getItemAttributes().setEarth(Integer.parseInt(node2.getAttributes().getNamedItem("earthAttr").getNodeValue()));
            }
            if (node2.getAttributes().getNamedItem("windAttr") != null) {
                ((MultiSellIngredient)object).getItemAttributes().setWind(Integer.parseInt(node2.getAttributes().getNamedItem("windAttr").getNodeValue()));
            }
            if (node2.getAttributes().getNamedItem("holyAttr") != null) {
                ((MultiSellIngredient)object).getItemAttributes().setHoly(Integer.parseInt(node2.getAttributes().getNamedItem("holyAttr").getNodeValue()));
            }
            if (node2.getAttributes().getNamedItem("unholyAttr") != null) {
                ((MultiSellIngredient)object).getItemAttributes().setUnholy(Integer.parseInt(node2.getAttributes().getNamedItem("unholyAttr").getNodeValue()));
            }
            if (!Config.ALT_ALLOW_SHADOW_WEAPONS && n2 > 0 && (itemTemplate = ItemHolder.getInstance().getTemplate(n2)) != null && itemTemplate.isShadowItem() && itemTemplate.isWeapon() && !Config.ALT_ALLOW_SHADOW_WEAPONS) {
                return null;
            }
            multiSellEntry.addProduct((MultiSellIngredient)object);
        }
        if (multiSellEntry.getIngredients().isEmpty() || multiSellEntry.getProduction().isEmpty()) {
            _log.warn("MultiSell [" + n + "] is empty!");
            return null;
        }
        if (multiSellEntry.getIngredients().size() == 1 && multiSellEntry.getIngredients().get(0).getItemId() == 57) {
            long l = 0L;
            for (MultiSellIngredient multiSellIngredient : multiSellEntry.getProduction()) {
                object = ItemHolder.getInstance().getTemplate(multiSellIngredient.getItemId());
                if (object == null) {
                    _log.warn("MultiSell [" + n + "] Production [" + multiSellEntry.getProduction().get(0).getItemId() + "] not found!");
                    return null;
                }
                l = SafeMath.addAndCheck(l, SafeMath.mulAndCheck(multiSellEntry.getProduction().get(0).getItemCount(), (long)((ItemTemplate)object).getReferencePrice()));
            }
            if (l > multiSellEntry.getIngredients().get(0).getItemCount() && Config.ALT_MULTISELL_DEBUG) {
                _log.warn("MultiSell [" + n + "] Production [" + multiSellEntry.getEntryId() + "] [" + multiSellEntry.getProduction().get(0).getItemId() + "] price is lower than referenced | " + l + " > " + multiSellEntry.getIngredients().get(0).getItemCount());
            }
        }
        return multiSellEntry;
    }

    private static long[] i(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        String[] stringArray = string.split(":");
        try {
            long l = Integer.parseInt(stringArray[0]);
            long l2 = stringArray.length > 1 ? Long.parseLong(stringArray[1]) : 1L;
            return new long[]{l, l2};
        }
        catch (Exception exception) {
            _log.error("", exception);
            return null;
        }
    }

    public static MultiSellEntry parseEntryFromStr(String string) {
        long[] lArray;
        if (string == null || string.isEmpty()) {
            return null;
        }
        String[] stringArray = string.split("->");
        if (stringArray.length != 2) {
            return null;
        }
        long[] lArray2 = MultiSellHolder.i(stringArray[0]);
        if (lArray2 == null || (lArray = MultiSellHolder.i(stringArray[1])) == null) {
            return null;
        }
        MultiSellEntry multiSellEntry = new MultiSellEntry();
        multiSellEntry.addIngredient(new MultiSellIngredient((int)lArray2[0], lArray2[1]));
        multiSellEntry.addProduct(new MultiSellIngredient((int)lArray[0], lArray[1]));
        return multiSellEntry;
    }

    public void SeparateAndSend(int n, Player player, double d) {
        for (int n2 : Config.ALT_DISABLED_MULTISELL) {
            if (n2 != n) continue;
            player.sendMessage(new CustomMessage("common.MultisellForbidden", player, new Object[0]));
            return;
        }
        MultiSellListContainer multiSellListContainer = this.getList(n);
        if (multiSellListContainer == null) {
            player.sendMessage(new CustomMessage("common.NoMultisell", player, new Object[0]));
            return;
        }
        this.SeparateAndSend(multiSellListContainer, player, d);
    }

    public void SeparateAndSend(MultiSellListContainer multiSellListContainer, Player player, double d) {
        multiSellListContainer = this.a(multiSellListContainer, player, d);
        MultiSellListContainer multiSellListContainer2 = new MultiSellListContainer();
        int n = 1;
        multiSellListContainer2.setListId(multiSellListContainer.getListId());
        player.setMultisell(multiSellListContainer);
        for (MultiSellEntry multiSellEntry : multiSellListContainer.getEntries()) {
            if (multiSellListContainer2.getEntries().size() == Config.MULTISELL_SIZE) {
                player.sendPacket((IStaticPacket)new MultiSellList(multiSellListContainer2, n, 0));
                ++n;
                multiSellListContainer2 = new MultiSellListContainer();
                multiSellListContainer2.setListId(multiSellListContainer.getListId());
            }
            multiSellListContainer2.addEntry(multiSellEntry);
        }
        if (player.isGM()) {
            Functions.sendDebugMessage(player, "MultiSell: " + multiSellListContainer2.getListId() + ".xml");
        }
        player.sendPacket((IStaticPacket)new MultiSellList(multiSellListContainer2, n, 1));
    }

    private MultiSellListContainer a(MultiSellListContainer multiSellListContainer, Player player, double d) {
        MultiSellListContainer multiSellListContainer2 = new MultiSellListContainer();
        multiSellListContainer2.setListId(multiSellListContainer.getListId());
        boolean bl = multiSellListContainer.isKeepEnchant();
        boolean bl2 = multiSellListContainer.isNoTax();
        boolean bl3 = multiSellListContainer.isShowAll();
        boolean bl4 = multiSellListContainer.isNoKey();
        multiSellListContainer2.setShowAll(bl3);
        multiSellListContainer2.setKeepEnchant(bl);
        multiSellListContainer2.setNoTax(bl2);
        multiSellListContainer2.setNoKey(bl4);
        ItemInstance[] itemInstanceArray = player.getInventory().getItems();
        for (MultiSellEntry multiSellEntry : multiSellListContainer.getEntries()) {
            List<MultiSellIngredient> list;
            MultiSellEntry multiSellEntry2 = multiSellEntry.clone();
            if (!bl2 && d > 0.0) {
                double d2 = 0.0;
                long l = 0L;
                list = new ArrayList<MultiSellIngredient>(multiSellEntry2.getIngredients().size() + 1);
                for (MultiSellIngredient multiSellIngredient : multiSellEntry2.getIngredients()) {
                    ItemTemplate itemTemplate;
                    if (multiSellIngredient.getItemId() == 57) {
                        l += multiSellIngredient.getItemCount();
                        d2 += (double)multiSellIngredient.getItemCount() * d;
                        continue;
                    }
                    list.add(multiSellIngredient);
                    if (multiSellIngredient.getItemId() == -200) {
                        d2 += (double)(multiSellIngredient.getItemCount() / 120L * 1000L) * d * 100.0;
                    }
                    if (multiSellIngredient.getItemId() < 1 || !(itemTemplate = ItemHolder.getInstance().getTemplate(multiSellIngredient.getItemId())).isStackable()) continue;
                    d2 += (double)((long)itemTemplate.getReferencePrice() * multiSellIngredient.getItemCount()) * d;
                }
                if ((l = Math.round((double)l + d2)) > 0L) {
                    list.add(new MultiSellIngredient(57, l));
                }
                multiSellEntry2.setTax(Math.round(d2));
                multiSellEntry2.getIngredients().clear();
                multiSellEntry2.getIngredients().addAll(list);
            } else {
                list = multiSellEntry2.getIngredients();
            }
            if (bl3) {
                multiSellListContainer2.entries.add(multiSellEntry2);
                continue;
            }
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            block2: for (MultiSellIngredient multiSellIngredient : list) {
                ItemTemplate itemTemplate;
                ItemTemplate itemTemplate2 = itemTemplate = multiSellIngredient.getItemId() <= 0 ? null : ItemHolder.getInstance().getTemplate(multiSellIngredient.getItemId());
                if (multiSellIngredient.getItemId() > 0 && !bl4 && !itemTemplate.isEquipment() || multiSellIngredient.getItemId() == 12374) continue;
                if (multiSellIngredient.getItemId() == -200) {
                    if (arrayList.contains(multiSellIngredient.getItemId()) || player.getClan() == null || (long)player.getClan().getReputationScore() < multiSellIngredient.getItemCount()) continue;
                    arrayList.add(multiSellIngredient.getItemId());
                    continue;
                }
                if (multiSellIngredient.getItemId() == -100) {
                    if (arrayList.contains(multiSellIngredient.getItemId()) || (long)player.getPcBangPoints() < multiSellIngredient.getItemCount()) continue;
                    arrayList.add(multiSellIngredient.getItemId());
                    continue;
                }
                for (ItemInstance itemInstance : itemInstanceArray) {
                    if (itemInstance.getItemId() != multiSellIngredient.getItemId() || !itemInstance.canBeExchanged(player) || arrayList.contains(bl ? (long)multiSellIngredient.getItemId() + (long)multiSellIngredient.getItemEnchant() * 100000L : (long)multiSellIngredient.getItemId()) || itemInstance.getEnchantLevel() < multiSellIngredient.getItemEnchant()) continue;
                    if (itemInstance.isStackable() && itemInstance.getCount() < multiSellIngredient.getItemCount()) continue block2;
                    arrayList.add(bl ? multiSellIngredient.getItemId() + multiSellIngredient.getItemEnchant() * 100000 : multiSellIngredient.getItemId());
                    MultiSellEntry multiSellEntry3 = new MultiSellEntry(bl ? multiSellEntry2.getEntryId() + itemInstance.getEnchantLevel() * 100000 : multiSellEntry2.getEntryId());
                    for (MultiSellIngredient multiSellIngredient2 : multiSellEntry2.getProduction()) {
                        if (bl && itemTemplate.canBeEnchanted(true)) {
                            multiSellIngredient2.setItemEnchant(itemInstance.getEnchantLevel());
                            multiSellIngredient2.setItemAttributes(itemInstance.getAttributes().clone());
                        }
                        multiSellEntry3.addProduct(multiSellIngredient2);
                    }
                    for (MultiSellIngredient multiSellIngredient2 : list) {
                        if (bl && multiSellIngredient2.getItemId() > 0 && ItemHolder.getInstance().getTemplate(multiSellIngredient2.getItemId()).canBeEnchanted(true)) {
                            multiSellIngredient2.setItemEnchant(itemInstance.getEnchantLevel());
                            multiSellIngredient2.setItemAttributes(itemInstance.getAttributes().clone());
                        }
                        multiSellEntry3.addIngredient(multiSellIngredient2);
                    }
                    multiSellListContainer2.entries.add(multiSellEntry3);
                    continue block2;
                }
            }
        }
        return multiSellListContainer2;
    }

    public static class MultiSellListContainer {
        private int wr;
        private boolean ws = true;
        private boolean wt = false;
        private boolean wu = false;
        private boolean wv = false;
        private List<MultiSellEntry> entries = new ArrayList<MultiSellEntry>();

        public void setListId(int n) {
            this.wr = n;
        }

        public int getListId() {
            return this.wr;
        }

        public void setShowAll(boolean bl) {
            this.ws = bl;
        }

        public boolean isShowAll() {
            return this.ws;
        }

        public void setNoTax(boolean bl) {
            this.wu = bl;
        }

        public boolean isNoTax() {
            return this.wu;
        }

        public void setNoKey(boolean bl) {
            this.wv = bl;
        }

        public boolean isNoKey() {
            return this.wv;
        }

        public void setKeepEnchant(boolean bl) {
            this.wt = bl;
        }

        public boolean isKeepEnchant() {
            return this.wt;
        }

        public void addEntry(MultiSellEntry multiSellEntry) {
            this.entries.add(multiSellEntry);
        }

        public List<MultiSellEntry> getEntries() {
            return this.entries;
        }

        public boolean isEmpty() {
            return this.entries.isEmpty();
        }
    }
}


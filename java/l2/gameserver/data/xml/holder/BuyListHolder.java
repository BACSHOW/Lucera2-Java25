/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.data.xml.holder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class BuyListHolder {
    private static final Logger _log = LoggerFactory.getLogger(BuyListHolder.class);
    private static BuyListHolder vy;
    private Map<Integer, NpcTradeList> _lists = new HashMap<Integer, NpcTradeList>();

    public static BuyListHolder getInstance() {
        if (vy == null) {
            vy = new BuyListHolder();
        }
        return vy;
    }

    public static void reload() {
        vy = new BuyListHolder();
    }

    private BuyListHolder() {
        try {
            File file = new File(Config.DATAPACK_ROOT, "data/merchant_filelists.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(false);
            documentBuilderFactory.setIgnoringComments(true);
            Document document = documentBuilderFactory.newDocumentBuilder().parse(file);
            int n = 0;
            int n2 = 0;
            for (Node node = document.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (!"list".equalsIgnoreCase(node.getNodeName())) continue;
                for (Node node2 = node.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
                    if (!"file".equalsIgnoreCase(node2.getNodeName())) continue;
                    String string = node2.getAttributes().getNamedItem("name").getNodeValue();
                    File file2 = new File(Config.DATAPACK_ROOT, "data/" + string);
                    DocumentBuilderFactory documentBuilderFactory2 = DocumentBuilderFactory.newInstance();
                    documentBuilderFactory2.setValidating(false);
                    documentBuilderFactory2.setIgnoringComments(true);
                    Document document2 = documentBuilderFactory2.newDocumentBuilder().parse(file2);
                    ++n;
                    for (Node node3 = document2.getFirstChild(); node3 != null; node3 = node3.getNextSibling()) {
                        if (!"list".equalsIgnoreCase(node3.getNodeName())) continue;
                        for (Node node4 = node3.getFirstChild(); node4 != null; node4 = node4.getNextSibling()) {
                            int n3;
                            if (!"tradelist".equalsIgnoreCase(node4.getNodeName())) continue;
                            String[] stringArray = node4.getAttributes().getNamedItem("npc").getNodeValue().split(";");
                            String[] stringArray2 = node4.getAttributes().getNamedItem("shop").getNodeValue().split(";");
                            String[] stringArray3 = new String[]{};
                            boolean bl = false;
                            if (node4.getAttributes().getNamedItem("markup") != null) {
                                stringArray3 = node4.getAttributes().getNamedItem("markup").getNodeValue().split(";");
                                bl = true;
                            }
                            int n4 = stringArray.length;
                            if (!bl) {
                                stringArray3 = new String[n4];
                                for (n3 = 0; n3 < n4; ++n3) {
                                    stringArray3[n3] = "0";
                                }
                            }
                            if (stringArray2.length != n4 || stringArray3.length != n4) {
                                _log.warn("Do not correspond to the size of arrays");
                                continue;
                            }
                            for (n3 = 0; n3 < n4; ++n3) {
                                int n5 = Integer.parseInt(stringArray[n3]);
                                int n6 = Integer.parseInt(stringArray2[n3]);
                                double d = n5 > 0 ? 1.0 + Double.parseDouble(stringArray3[n3]) / 100.0 : 0.0;
                                NpcTradeList npcTradeList = new NpcTradeList(n6);
                                npcTradeList.setNpcId(n5);
                                for (Node node5 = node4.getFirstChild(); node5 != null; node5 = node5.getNextSibling()) {
                                    if (!"item".equalsIgnoreCase(node5.getNodeName())) continue;
                                    int n7 = Integer.parseInt(node5.getAttributes().getNamedItem("id").getNodeValue());
                                    ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(n7);
                                    if (itemTemplate == null) {
                                        _log.warn("Template not found for itemId: " + n7 + " for shop " + n6);
                                        continue;
                                    }
                                    if (!this.checkItem(itemTemplate)) continue;
                                    ++n2;
                                    long l = node5.getAttributes().getNamedItem("price") != null ? Long.parseLong(node5.getAttributes().getNamedItem("price").getNodeValue()) : Math.round((double)itemTemplate.getReferencePrice() * Config.ALT_SHOP_BUY_LIST_MULTIPLIER * d);
                                    TradeItem tradeItem = new TradeItem();
                                    tradeItem.setItemId(n7);
                                    int n8 = node5.getAttributes().getNamedItem("count") != null ? Integer.parseInt(node5.getAttributes().getNamedItem("count").getNodeValue()) : 0;
                                    int n9 = node5.getAttributes().getNamedItem("time") != null ? Integer.parseInt(node5.getAttributes().getNamedItem("time").getNodeValue()) : 0;
                                    tradeItem.setOwnersPrice(l);
                                    tradeItem.setCount(n8);
                                    tradeItem.setCurrentValue(n8);
                                    tradeItem.setNextRechargeTime(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(tradeItem.getRechargeTime()));
                                    tradeItem.setRechargeTime(n9);
                                    npcTradeList.addItem(tradeItem);
                                }
                                this._lists.put(n6, npcTradeList);
                            }
                        }
                    }
                }
            }
            _log.info("TradeController: Loaded " + n + " file(s).");
            _log.info("TradeController: Loaded " + n2 + " Items.");
            _log.info("TradeController: Loaded " + this._lists.size() + " Buylists.");
        }
        catch (Exception exception) {
            _log.warn("TradeController: Buylists could not be initialized.");
            _log.error("", exception);
        }
    }

    public boolean checkItem(ItemTemplate itemTemplate) {
        if (itemTemplate.isEquipment() && !itemTemplate.isForPet() && Config.ALT_SHOP_PRICE_LIMITS.length > 0) {
            for (int i = 0; i < Config.ALT_SHOP_PRICE_LIMITS.length; i += 2) {
                if (itemTemplate.getBodyPart() != Config.ALT_SHOP_PRICE_LIMITS[i]) continue;
                if (itemTemplate.getReferencePrice() <= Config.ALT_SHOP_PRICE_LIMITS[i + 1]) break;
                return false;
            }
        }
        if (Config.ALT_SHOP_UNALLOWED_ITEMS.length > 0) {
            for (int n : Config.ALT_SHOP_UNALLOWED_ITEMS) {
                if (itemTemplate.getItemId() != n) continue;
                return false;
            }
        }
        return true;
    }

    public NpcTradeList getBuyList(int n) {
        return this._lists.get(n);
    }

    public void addToBuyList(int n, NpcTradeList npcTradeList) {
        this._lists.put(n, npcTradeList);
    }

    public static class NpcTradeList {
        private List<TradeItem> vz = new ArrayList<TradeItem>();
        private int _id;
        private int _npcId;

        public NpcTradeList(int n) {
            this._id = n;
        }

        public int getListId() {
            return this._id;
        }

        public void setNpcId(int n) {
            this._npcId = n;
        }

        public int getNpcId() {
            return this._npcId;
        }

        public void addItem(TradeItem tradeItem) {
            this.vz.add(tradeItem);
        }

        public synchronized List<TradeItem> getItems() {
            ArrayList<TradeItem> arrayList = new ArrayList<TradeItem>();
            long l = System.currentTimeMillis();
            for (TradeItem tradeItem : this.vz) {
                if (tradeItem.isCountLimited()) {
                    if (tradeItem.getNextRechargeTime() <= l) {
                        tradeItem.setNextRechargeTime(tradeItem.getNextRechargeTime() + TimeUnit.MINUTES.toMillis(tradeItem.getRechargeTime()));
                        tradeItem.setCurrentValue(tradeItem.getCount());
                    }
                    if (tradeItem.getCurrentValue() == 0L) continue;
                }
                arrayList.add(tradeItem);
            }
            return arrayList;
        }

        public TradeItem getItemByItemId(int n) {
            for (TradeItem tradeItem : this.vz) {
                if (tradeItem.getItemId() != n) continue;
                return tradeItem;
            }
            return null;
        }

        public synchronized void updateItems(List<TradeItem> list) {
            for (TradeItem tradeItem : list) {
                TradeItem tradeItem2 = this.getItemByItemId(tradeItem.getItemId());
                if (!tradeItem2.isCountLimited()) continue;
                tradeItem2.setCurrentValue(Math.max(tradeItem2.getCurrentValue() - tradeItem.getCount(), 0L));
            }
        }
    }
}


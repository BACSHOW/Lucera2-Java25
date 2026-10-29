/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Log;

public class AdminCreateItem
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().UseGMShop) {
            return false;
        }
        switch (commands) {
            case admin_itemcreate: {
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            }
            case admin_ci: 
            case admin_create_item: {
                try {
                    if (stringArray.length < 2) {
                        player.sendMessage("USAGE: create_item id [count]");
                        return false;
                    }
                    int n = Integer.parseInt(stringArray[1]);
                    long l = stringArray.length < 3 ? 1L : Long.parseLong(stringArray[2]);
                    this.a(player, n, l);
                }
                catch (NumberFormatException numberFormatException) {
                    player.sendMessage("USAGE: create_item id [count]");
                }
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            }
            case admin_spreaditem: {
                try {
                    int n = Integer.parseInt(stringArray[1]);
                    int n2 = stringArray.length > 2 ? Integer.parseInt(stringArray[2]) : 1;
                    long l = stringArray.length > 3 ? Long.parseLong(stringArray[3]) : 1L;
                    for (int i = 0; i < n2; ++i) {
                        ItemInstance itemInstance = ItemFunctions.createItem(n);
                        itemInstance.setCount(l);
                        itemInstance.dropMe(player, Location.findPointToStay(player, 100));
                    }
                    break;
                }
                catch (NumberFormatException numberFormatException) {
                    player.sendMessage("Specify a valid number.");
                    break;
                }
                catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
                    player.sendMessage("Can't create this item.");
                    break;
                }
            }
            case admin_create_item_element: {
                try {
                    if (stringArray.length < 4) {
                        player.sendMessage("USAGE: create_item_attribue [id] [element id] [value]");
                        return false;
                    }
                    int n = Integer.parseInt(stringArray[1]);
                    int n3 = Integer.parseInt(stringArray[2]);
                    int n4 = Integer.parseInt(stringArray[3]);
                    if (n3 > 5 || n3 < 0) {
                        player.sendMessage("Improper element Id");
                        return false;
                    }
                    if (n4 < 1 || n4 > 300) {
                        player.sendMessage("Improper element value");
                        return false;
                    }
                    ItemInstance itemInstance = this.a(player, n, 1L);
                    Element element = Element.getElementById(n3);
                    itemInstance.setAttributeElement(element, itemInstance.getAttributeElementValue(element, false) + n4);
                    player.sendPacket((IStaticPacket)new InventoryUpdate().addModifiedItem(itemInstance));
                }
                catch (NumberFormatException numberFormatException) {
                    player.sendMessage("USAGE: create_item id [count]");
                }
                player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("data/html/admin/itemcreation.htm"));
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private ItemInstance a(Player player, int n, long l) {
        ItemInstance itemInstance = ItemFunctions.createItem(n);
        itemInstance.setCount(l);
        Log.LogItem(player, Log.ItemLog.Create, itemInstance);
        player.getInventory().addItem(itemInstance);
        if (!itemInstance.isStackable()) {
            for (long i = 0L; i < l - 1L; ++i) {
                itemInstance = ItemFunctions.createItem(n);
                Log.LogItem(player, Log.ItemLog.Create, itemInstance);
                player.getInventory().addItem(itemInstance);
            }
        }
        player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(n, l, 0));
        return itemInstance;
    }

    private static enum Commands {
        admin_itemcreate,
        admin_create_item,
        admin_ci,
        admin_spreaditem,
        admin_create_item_element;

    }
}


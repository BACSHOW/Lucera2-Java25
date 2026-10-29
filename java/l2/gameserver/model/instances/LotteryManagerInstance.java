/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.text.DateFormat;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.games.LotteryManager;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class LotteryManagerInstance
extends NpcInstance {
    public LotteryManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!LotteryManagerInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.startsWith("Loto")) {
            try {
                int n = Integer.parseInt(string.substring(5));
                this.showLotoWindow(player, n);
            }
            catch (NumberFormatException numberFormatException) {
                Log.debug("L2LotteryManagerInstance: bypass: " + string + "; player: " + player, numberFormatException);
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }

    @Override
    public String getHtmlPath(int n, int n2, Player player) {
        String string = n2 == 0 ? "LotteryManager" : "LotteryManager-" + n2;
        return "lottery/" + string + ".htm";
    }

    public void showLotoWindow(Player player, int n) {
        int n2 = this.getTemplate().npcId;
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        switch (n) {
            case 0: {
                String string = this.getHtmlPath(n2, 1, player);
                npcHtmlMessage.setFile(string);
                break;
            }
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: {
                String string;
                int n3;
                if (!LotteryManager.getInstance().isStarted()) {
                    player.sendPacket((IStaticPacket)Msg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
                    return;
                }
                if (!LotteryManager.getInstance().isSellableTickets()) {
                    player.sendPacket((IStaticPacket)Msg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
                    return;
                }
                String string2 = this.getHtmlPath(n2, 5, player);
                npcHtmlMessage.setFile(string2);
                int n4 = 0;
                boolean bl = false;
                for (n3 = 0; n3 < 5; ++n3) {
                    if (player.getLoto(n3) == n) {
                        player.setLoto(n3, 0);
                        bl = true;
                        continue;
                    }
                    if (player.getLoto(n3) <= 0) continue;
                    ++n4;
                }
                if (n4 < 5 && !bl && n <= 20) {
                    for (n3 = 0; n3 < 5; ++n3) {
                        if (player.getLoto(n3) != 0) continue;
                        player.setLoto(n3, n);
                        break;
                    }
                }
                n4 = 0;
                for (n3 = 0; n3 < 5; ++n3) {
                    if (player.getLoto(n3) <= 0) continue;
                    ++n4;
                    string = String.valueOf(player.getLoto(n3));
                    if (player.getLoto(n3) < 10) {
                        string = "0" + string;
                    }
                    String string3 = "fore=\"L2UI.lottoNum" + string + "\" back=\"L2UI.lottoNum" + string + "a_check\"";
                    String string4 = "fore=\"L2UI.lottoNum" + string + "a_check\" back=\"L2UI.lottoNum" + string + "\"";
                    npcHtmlMessage.replace(string3, string4);
                }
                if (n4 != 5) break;
                String string5 = "";
                string = "";
                if (!player.isLangRus()) {
                    string5 = "0\">Return";
                    string = "22\">The winner selected the numbers above.";
                } else {
                    string5 = "0\">\u041d\u0430\u0437\u0430\u0434";
                    string = "22\">\u0412\u044b\u0438\u0433\u0440\u044b\u0448\u043d\u044b\u0435 \u043d\u043e\u043c\u0435\u0440\u0430 \u0432\u044b\u0431\u0440\u0430\u043d\u043d\u044b\u0435 \u0432\u044b\u0448\u0435.";
                }
                npcHtmlMessage.replace(string5, string);
                break;
            }
            case 22: {
                if (!LotteryManager.getInstance().isStarted()) {
                    player.sendPacket((IStaticPacket)Msg.LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD);
                    return;
                }
                if (!LotteryManager.getInstance().isSellableTickets()) {
                    player.sendPacket((IStaticPacket)Msg.TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE);
                    return;
                }
                int n5 = Config.SERVICES_ALT_LOTTERY_PRICE;
                int n6 = LotteryManager.getInstance().getId();
                int n7 = 0;
                int n8 = 0;
                for (int i = 0; i < 5; ++i) {
                    if (player.getLoto(i) == 0) {
                        return;
                    }
                    if (player.getLoto(i) < 17) {
                        n7 = (int)((double)n7 + Math.pow(2.0, player.getLoto(i) - 1));
                        continue;
                    }
                    n8 = (int)((double)n8 + Math.pow(2.0, player.getLoto(i) - 17));
                }
                if (ItemFunctions.getItemCount(player, Config.SERVICE_LOTTERY_ITEM_ID) < (long)n5) {
                    if (Config.SERVICE_LOTTERY_ITEM_ID == 57) {
                        player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    } else {
                        player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
                    }
                    return;
                }
                ItemFunctions.removeItem(player, Config.SERVICE_LOTTERY_ITEM_ID, n5, true);
                SystemMessage systemMessage = new SystemMessage(371);
                systemMessage.addNumber(n6);
                systemMessage.addItemName(4442);
                player.sendPacket((IStaticPacket)systemMessage);
                ItemInstance itemInstance = ItemFunctions.createItem(4442);
                itemInstance.setBlessed(n6);
                itemInstance.setEnchantLevel(n7);
                itemInstance.setDamaged(n8);
                player.getInventory().addItem(itemInstance);
                String string = this.getHtmlPath(n2, 3, player);
                npcHtmlMessage.setFile(string);
                break;
            }
            case 23: {
                String string = this.getHtmlPath(n2, 3, player);
                npcHtmlMessage.setFile(string);
                break;
            }
            case 24: {
                String string = this.getHtmlPath(n2, 4, player);
                npcHtmlMessage.setFile(string);
                int n9 = LotteryManager.getInstance().getId();
                String string6 = "";
                for (ItemInstance itemInstance : player.getInventory().getItems()) {
                    if (itemInstance == null || itemInstance.getItemId() != 4442 || itemInstance.getBlessed() >= n9) continue;
                    string6 = string6 + "<a action=\"bypass -h npc_%objectId%_Loto " + itemInstance.getObjectId() + "\">" + itemInstance.getBlessed();
                    string6 = string6 + " " + new CustomMessage("LotteryManagerInstance.NpcString.EVENT_NUMBER", player, new Object[0]).toString() + " ";
                    int[] nArray = LotteryManager.getInstance().decodeNumbers(itemInstance.getEnchantLevel(), itemInstance.getDamaged());
                    for (int i = 0; i < 5; ++i) {
                        string6 = string6 + nArray[i] + " ";
                    }
                    int[] nArray2 = LotteryManager.getInstance().checkTicket(itemInstance);
                    if (nArray2[0] > 0) {
                        string6 = string6 + "- ";
                        switch (nArray2[0]) {
                            case 1: {
                                string6 = string6 + new CustomMessage("LotteryManagerInstance.NpcString.FIRST_PRIZE", player, new Object[0]).toString();
                                break;
                            }
                            case 2: {
                                string6 = string6 + new CustomMessage("LotteryManagerInstance.NpcString.SECOND_PRIZE", player, new Object[0]).toString();
                                break;
                            }
                            case 3: {
                                string6 = string6 + new CustomMessage("LotteryManagerInstance.NpcString.THIRD_PRIZE", player, new Object[0]).toString();
                                break;
                            }
                            case 4: {
                                string6 = string6 + new CustomMessage("LotteryManagerInstance.NpcString.FOURTH_PRIZE", player, new Object[0]).toString();
                            }
                        }
                        string6 = string6 + " " + nArray2[1] + "a.";
                    }
                    string6 = string6 + "</a>";
                }
                if (string6.length() == 0) {
                    string6 = string6 + new CustomMessage("LotteryManagerInstance.NpcString.THERE_HAS_BEEN_NO_WINNING_LOTTERY_TICKET", player, new Object[0]).toString();
                }
                npcHtmlMessage.replace("%result%", string6);
                break;
            }
            case 25: {
                String string = this.getHtmlPath(n2, 2, player);
                npcHtmlMessage.setFile(string);
                break;
            }
            default: {
                if (n <= 25) break;
                int n10 = LotteryManager.getInstance().getId();
                ItemInstance itemInstance = player.getInventory().getItemByObjectId(n);
                if (itemInstance == null || itemInstance.getItemId() != 4442 || itemInstance.getBlessed() >= n10) {
                    return;
                }
                int[] nArray = LotteryManager.getInstance().checkTicket(itemInstance);
                if (player.getInventory().destroyItem(itemInstance, 1L)) {
                    player.sendPacket((IStaticPacket)SystemMessage2.removeItems(4442, 1L));
                    int n11 = nArray[1];
                    if (n11 > 0) {
                        ItemFunctions.addItem((Playable)player, Config.SERVICE_LOTTERY_ITEM_ID, (long)n11, true);
                    }
                }
                return;
            }
        }
        npcHtmlMessage.replace("%objectId%", String.valueOf(this.getObjectId()));
        npcHtmlMessage.replace("%race%", "" + LotteryManager.getInstance().getId());
        npcHtmlMessage.replace("%prize_amount%", "" + LotteryManager.getInstance().getPrize());
        npcHtmlMessage.replace("%item_id%", "" + Config.SERVICE_LOTTERY_ITEM_ID);
        npcHtmlMessage.replace("%ticket_price%", "" + Config.SERVICES_LOTTERY_TICKET_PRICE);
        npcHtmlMessage.replace("%prize5%", "" + Config.SERVICES_LOTTERY_5_NUMBER_RATE * 100.0);
        npcHtmlMessage.replace("%prize4%", "" + Config.SERVICES_LOTTERY_4_NUMBER_RATE * 100.0);
        npcHtmlMessage.replace("%prize3%", "" + Config.SERVICES_LOTTERY_3_NUMBER_RATE * 100.0);
        npcHtmlMessage.replace("%prize2%", "" + Config.SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE);
        npcHtmlMessage.replace("%enddate%", "" + DateFormat.getDateInstance().format(LotteryManager.getInstance().getEndDate()));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
        player.sendActionFailed();
    }
}


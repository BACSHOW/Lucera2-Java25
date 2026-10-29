/*
 * Decompiled with CFR 0.152.
 */
package services.petevolve;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.Summon;
import l2.gameserver.model.instances.PetInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.Log;
import l2.gameserver.utils.Util;

public class exchange
extends Functions {
    private static final int bZj = 7583;
    private static final int bZk = 7584;
    private static final int bZl = 7585;
    private static final int bZm = 6648;
    private static final int bZn = 6649;
    private static final int bZo = 6650;

    public void exch_1() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (exchange.getItemCount(player, 7583) >= 1L) {
            exchange.removeItem(player, 7583, 1L);
            exchange.addItem(player, 6648, 1L);
            return;
        }
        this.show("scripts/services/petevolve/exchange_no.htm", player);
    }

    public void exch_2() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (exchange.getItemCount(player, 7584) >= 1L) {
            exchange.removeItem(player, 7584, 1L);
            exchange.addItem(player, 6649, 1L);
            return;
        }
        this.show("scripts/services/petevolve/exchange_no.htm", player);
    }

    public void exch_3() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (exchange.getItemCount(player, 7585) >= 1L) {
            exchange.removeItem(player, 7585, 1L);
            exchange.addItem(player, 6650, 1L);
            return;
        }
        this.show("scripts/services/petevolve/exchange_no.htm", player);
    }

    public void showErasePetName() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_CHANGE_PET_NAME_ENABLED) {
            this.show("\u0421\u0435\u0440\u0432\u0438\u0441 \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d.", player);
            return;
        }
        ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(Config.SERVICES_CHANGE_PET_NAME_ITEM);
        String string = "";
        if (player.isLangRus()) {
            string = string + "<html><body>\u0412\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u043e\u0431\u043d\u0443\u043b\u0438\u0442\u044c \u0438\u043c\u044f \u0443 \u043f\u0435\u0442\u0430, \u0434\u043b\u044f \u0442\u043e\u0433\u043e \u0447\u0442\u043e\u0431\u044b \u043d\u0430\u0437\u043d\u0430\u0447\u0438\u0442\u044c \u043d\u043e\u0432\u043e\u0435. \u041f\u0435\u0442 \u043f\u0440\u0438 \u044d\u0442\u043e\u043c \u0434\u043e\u043b\u0436\u0435\u043d \u0431\u044b\u0442\u044c \u0432\u044b\u0437\u0432\u0430\u043d.";
            string = string + "<br>\u0421\u0442\u043e\u0438\u043c\u043e\u0441\u0442\u044c \u043e\u0431\u043d\u0443\u043b\u0435\u043d\u0438\u044f: " + Util.formatAdena(Config.SERVICES_CHANGE_PET_NAME_PRICE) + " " + itemTemplate.getName();
            string = string + "<br><button width=100 height=15 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h scripts_services.petevolve.exchange:erasePetName\" value=\"\u041e\u0431\u043d\u0443\u043b\u0438\u0442\u044c \u0438\u043c\u044f\">";
        } else {
            string = string + "<html><body>You can reset a pet\u2019s name in order to assign a new one. Pet must be called.";
            string = string + "<br>Reset cost: " + Util.formatAdena(Config.SERVICES_CHANGE_PET_NAME_PRICE) + " " + itemTemplate.getName();
            string = string + "<br><button width=100 height=15 back=\"L2UI_CH3.bigbutton2_down\" fore=\"L2UI_CH3.bigbutton2\" action=\"bypass -h scripts_services.petevolve.exchange:erasePetName\" value=\"Reset name\">";
        }
        string = string + "</body></html>";
        this.show(string, player);
    }

    public void erasePetName() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_CHANGE_PET_NAME_ENABLED) {
            if (player.isLangRus()) {
                this.show("\u0421\u0435\u0440\u0432\u0438\u0441 \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d", player);
            } else {
                this.show("Service disabled", player);
            }
            return;
        }
        Summon summon = player.getPet();
        if (summon == null || !summon.isPet()) {
            if (player.isLangRus()) {
                this.show("\u041f\u0438\u0442\u043e\u043c\u0435\u0446 \u0434\u043e\u043b\u0436\u0435\u043d \u0431\u044b\u0442\u044c \u0432\u044b\u0437\u0432\u0430\u043d.", player);
            } else {
                this.show("Pet must be summoned.", player);
            }
            return;
        }
        if (player.getInventory().destroyItemByItemId(Config.SERVICES_CHANGE_PET_NAME_ITEM, Config.SERVICES_CHANGE_PET_NAME_PRICE)) {
            summon.setName(summon.getTemplate().name);
            summon.broadcastCharInfo();
            PetInstance petInstance = (PetInstance)summon;
            ItemInstance itemInstance = petInstance.getControlItem();
            if (itemInstance != null) {
                itemInstance.setDamaged(1);
                player.sendPacket((IStaticPacket)new InventoryUpdate().addModifiedItem(itemInstance));
                Log.service("erasePetName", player, "Pet name erased for " + Config.SERVICES_CHANGE_PET_NAME_ITEM + " amount " + Config.SERVICES_CHANGE_PET_NAME_PRICE);
            }
            if (player.isLangRus()) {
                this.show("\u0418\u043c\u044f \u0441\u0442\u0435\u0440\u0442\u043e.", player);
            } else {
                this.show("Name Erased.", player);
            }
        } else if (Config.SERVICES_CHANGE_PET_NAME_ITEM == 57) {
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
        } else {
            player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
        }
    }

    public String DialogAppend_30731(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30827(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30828(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30829(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30830(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30831(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_30869(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_31067(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_31265(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_31309(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String DialogAppend_31954(Integer n) {
        return this.getHtmlAppends(n);
    }

    public String getHtmlAppends(Integer n) {
        Player player = this.getSelf();
        if (Config.SERVICES_CHANGE_PET_NAME_ENABLED) {
            return player.isLangRus() ? "<br>[scripts_services.petevolve.exchange:showErasePetName|\u041e\u0431\u043d\u0443\u043b\u0438\u0442\u044c \u0438\u043c\u044f \u0443 \u043f\u0435\u0442\u0430]" : "<br>[scripts_services.petevolve.exchange:showErasePetName|Erase Pet Name]";
        }
        return "";
    }
}


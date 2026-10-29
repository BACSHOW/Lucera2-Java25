/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;

public class AdminEnchant
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (!player.getPlayerAccess().CanEditChar) {
            return false;
        }
        int n = -1;
        switch (commands) {
            case admin_enchant: {
                this.showMainPage(player);
                return true;
            }
            case admin_seteh: {
                n = 6;
                break;
            }
            case admin_setec: {
                n = 10;
                break;
            }
            case admin_seteg: {
                n = 9;
                break;
            }
            case admin_seteb: {
                n = 12;
                break;
            }
            case admin_setel: {
                n = 11;
                break;
            }
            case admin_setew: {
                n = 7;
                break;
            }
            case admin_setes: {
                n = 8;
                break;
            }
            case admin_setle: {
                n = 2;
                break;
            }
            case admin_setre: {
                n = 1;
                break;
            }
            case admin_setlf: {
                n = 5;
                break;
            }
            case admin_setrf: {
                n = 4;
                break;
            }
            case admin_seten: {
                n = 3;
                break;
            }
            case admin_setun: {
                n = 0;
                break;
            }
            case admin_setba: {
                n = 13;
                break;
            }
            case admin_setha: {
                n = 15;
                break;
            }
            case admin_setdha: {
                n = 15;
            }
        }
        if (n == -1 || stringArray.length < 2) {
            this.showMainPage(player);
            return true;
        }
        try {
            int n2 = Integer.parseInt(stringArray[1]);
            if (n2 < 0 || n2 > 65535) {
                player.sendMessage("You must set the enchant level to be between 0-65535.");
            } else {
                this.a(player, n2, n);
            }
        }
        catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
            player.sendMessage("Please specify a new enchant value.");
        }
        catch (NumberFormatException numberFormatException) {
            player.sendMessage("Please specify a valid new enchant value.");
        }
        this.showMainPage(player);
        return true;
    }

    private void a(Player player, int n, int n2) {
        GameObject gameObject = player.getTarget();
        if (gameObject == null) {
            gameObject = player;
        }
        if (!gameObject.isPlayer()) {
            player.sendMessage("Wrong target type.");
            return;
        }
        Player player2 = (Player)gameObject;
        int n3 = 0;
        ItemInstance itemInstance = player2.getInventory().getPaperdollItem(n2);
        if (itemInstance != null) {
            n3 = itemInstance.getEnchantLevel();
            player2.getInventory().unEquipItem(itemInstance);
            itemInstance.setEnchantLevel(n);
            player2.getInventory().equipItem(itemInstance);
            player2.sendPacket((IStaticPacket)new InventoryUpdate().addModifiedItem(itemInstance));
            player2.broadcastCharInfo();
            player.sendMessage("Changed enchantment of " + player2.getName() + "'s " + itemInstance.getName() + " from " + n3 + " to " + n + ".");
            player2.sendMessage("Admin has changed the enchantment of your " + itemInstance.getName() + " from " + n3 + " to " + n + ".");
        }
    }

    public void showMainPage(Player player) {
        GameObject gameObject = player.getTarget();
        if (gameObject == null) {
            gameObject = player;
        }
        Player player2 = player;
        if (gameObject.isPlayer()) {
            player2 = (Player)gameObject;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        StringBuilder stringBuilder = new StringBuilder("<html><body>");
        stringBuilder.append("<center><table width=260><tr><td width=40>");
        stringBuilder.append("<button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\">");
        stringBuilder.append("</td><td width=180>");
        stringBuilder.append("<center>Enchant Equip for player: " + player2.getName() + "</center>");
        stringBuilder.append("</td><td width=40>");
        stringBuilder.append("</td></tr></table></center><br>");
        stringBuilder.append("<center><table width=270><tr><td>");
        stringBuilder.append("<button value=\"Shirt\" action=\"bypass -h admin_setun $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Helmet\" action=\"bypass -h admin_seteh $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Mask\" action=\"bypass -h admin_setha $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Necklace\" action=\"bypass -h admin_seten $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table>");
        stringBuilder.append("</center><center><table width=270><tr><td>");
        stringBuilder.append("<button value=\"Weapon\" action=\"bypass -h admin_setew $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Chest\" action=\"bypass -h admin_setec $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Shield\" action=\"bypass -h admin_setes $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Earring\" action=\"bypass -h admin_setre $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Earring\" action=\"bypass -h admin_setle $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table>");
        stringBuilder.append("</center><center><table width=270><tr><td>");
        stringBuilder.append("<button value=\"Gloves\" action=\"bypass -h admin_seteg $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Leggings\" action=\"bypass -h admin_setel $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Boots\" action=\"bypass -h admin_seteb $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Ring\" action=\"bypass -h admin_setrf $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td>");
        stringBuilder.append("<button value=\"Ring\" action=\"bypass -h admin_setlf $menu_command\" width=50 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table>");
        stringBuilder.append("</center><center><table width=270><tr><td>");
        stringBuilder.append("</center><br>");
        stringBuilder.append("<center>[Enchant 0-65535]</center>");
        stringBuilder.append("<center><edit var=\"menu_command\" width=100 height=15></center><br>");
        stringBuilder.append("</body></html>");
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_seteh,
        admin_setec,
        admin_seteg,
        admin_setel,
        admin_seteb,
        admin_setew,
        admin_setes,
        admin_setle,
        admin_setre,
        admin_setlf,
        admin_setrf,
        admin_seten,
        admin_setun,
        admin_setba,
        admin_setha,
        admin_setdha,
        admin_setlbr,
        admin_setrbr,
        admin_setbelt,
        admin_enchant;

    }
}


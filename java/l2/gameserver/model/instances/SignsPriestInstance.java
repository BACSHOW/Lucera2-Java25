/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import java.util.Calendar;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSigns;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.Functions;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignsPriestInstance
extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(SignsPriestInstance.class);

    public SignsPriestInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    private void a(Player player, int n, String string, boolean bl) {
        String string2 = "seven_signs/";
        string2 = string2 + (bl ? "desc_" + n : "signs_" + n);
        string2 = string2 + (string != null ? "_" + string + ".htm" : ".htm");
        this.showChatWindow(player, string2, new Object[0]);
    }

    private boolean U(Player player) {
        int n;
        Clan clan = player.getClan();
        if (clan == null) {
            return false;
        }
        if (!Config.ALT_GAME_REQUIRE_CLAN_CASTLE && (n = clan.getAllyId()) != 0) {
            Clan[] clanArray;
            for (Clan clan2 : clanArray = ClanTable.getInstance().getClans()) {
                if (clan2.getAllyId() != n || clan2.getCastle() <= 0) continue;
                return true;
            }
        }
        return clan.getCastle() > 0;
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!SignsPriestInstance.canBypassCheck(player, this)) {
            return;
        }
        if ((this.getNpcId() == 31113 || this.getNpcId() == 31126) && SevenSigns.getInstance().getPlayerCabal(player) == 0 && !player.isGM() && Config.ALT_MAMONS_CHECK_SEVEN_SING_STATUS) {
            return;
        }
        super.onBypassFeedback(player, string);
        if (string.startsWith("SevenSignsDesc")) {
            int n = Integer.parseInt(string.substring(15));
            this.a(player, n, null, true);
        } else if (string.startsWith("SevenSigns")) {
            int n = 0;
            int n2 = 0;
            ItemInstance itemInstance = player.getInventory().getItemByItemId(5575);
            long l = itemInstance == null ? 0L : itemInstance.getCount();
            int n3 = Integer.parseInt(string.substring(11, 12).trim());
            if (string.length() > 12) {
                n3 = Integer.parseInt(string.substring(11, 13).trim());
            }
            if (string.length() > 13) {
                try {
                    n = Integer.parseInt(string.substring(14, 15).trim());
                }
                catch (Exception exception) {
                    try {
                        n = Integer.parseInt(string.substring(13, 14).trim());
                    }
                    catch (Exception exception2) {
                        // empty catch block
                    }
                }
            }
            switch (n3) {
                case 2: {
                    if (!player.getInventory().validateCapacity(1L)) {
                        player.sendPacket((IStaticPacket)Msg.YOUR_INVENTORY_IS_FULL);
                        return;
                    }
                    if (500L > player.getAdena()) {
                        player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        return;
                    }
                    player.reduceAdena(500L, true);
                    player.getInventory().addItem(ItemFunctions.createItem(5707));
                    player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(5707, 1L, 0));
                    break;
                }
                case 3: 
                case 8: {
                    n = SevenSigns.getInstance().getPriestCabal(this.getNpcId());
                    this.a(player, n3, SevenSigns.getCabalShortName(n), false);
                    break;
                }
                case 10: {
                    String string2 = "seven_signs/";
                    string2 = SevenSigns.getInstance().isSealValidationPeriod() ? (SevenSigns.getInstance().getSealOwner(2) != 0 ? string2 + this.getNpcId() + "_gnosis.htm" : string2 + this.getNpcId() + ".htm") : (SevenSigns.getInstance().getPlayerCabal(player) == 0 ? string2 + this.dc() + "_priest_tp_no.htm" : string2 + this.getNpcId() + ".htm");
                    this.showChatWindow(player, string2, new Object[0]);
                    break;
                }
                case 4: {
                    int n4 = Integer.parseInt(string.substring(15));
                    int n5 = SevenSigns.getInstance().getPlayerCabal(player);
                    if (n5 != 0) {
                        player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.AlreadyMember", player, new Object[0]).addString(SevenSigns.getCabalName(n)));
                        return;
                    }
                    if (player.getClassId().level() == 0) {
                        player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.YouAreNewbie", player, new Object[0]));
                        break;
                    }
                    if (Config.ALT_GAME_REQUIRE_CASTLE_DAWN) {
                        if (this.U(player)) {
                            if (n == 1) {
                                player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.CastleOwning", player, new Object[0]));
                                return;
                            }
                        } else if (n == 2) {
                            boolean bl = false;
                            if (Functions.getItemCount(player, 6388) > 0L) {
                                Functions.removeItem(player, 6388, 1L);
                                bl = true;
                            } else if (Config.ALT_GAME_ALLOW_ADENA_DAWN && player.getAdena() >= 50000L) {
                                player.reduceAdena(50000L, true);
                                bl = true;
                            }
                            if (!bl) {
                                if (Config.ALT_GAME_ALLOW_ADENA_DAWN) {
                                    player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.CastleOwningCertificate", player, new Object[0]));
                                } else {
                                    player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.CastleOwningCertificate2", player, new Object[0]));
                                }
                                return;
                            }
                        }
                    }
                    SevenSigns.getInstance().setPlayerInfo(player.getObjectId(), n, n4);
                    if (n == 2) {
                        player.sendPacket((IStaticPacket)Msg.YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_LORDS_OF_DAWN);
                    } else {
                        player.sendPacket((IStaticPacket)Msg.YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_REVOLUTIONARIES_OF_DUSK);
                    }
                    switch (n4) {
                        case 1: {
                            player.sendPacket((IStaticPacket)Msg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_AVARICE_DURING_THIS_QUEST_EVENT_PERIOD);
                            break;
                        }
                        case 2: {
                            player.sendPacket((IStaticPacket)Msg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_GNOSIS_DURING_THIS_QUEST_EVENT_PERIOD);
                            break;
                        }
                        case 3: {
                            player.sendPacket((IStaticPacket)Msg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_STRIFE_DURING_THIS_QUEST_EVENT_PERIOD);
                        }
                    }
                    this.a(player, 4, SevenSigns.getCabalShortName(n), false);
                    break;
                }
                case 6: {
                    n2 = Integer.parseInt(string.substring(13));
                    ItemInstance itemInstance2 = player.getInventory().getItemByItemId(6362);
                    long l2 = itemInstance2 == null ? 0L : itemInstance2.getCount();
                    ItemInstance itemInstance3 = player.getInventory().getItemByItemId(6361);
                    long l3 = itemInstance3 == null ? 0L : itemInstance3.getCount();
                    ItemInstance itemInstance4 = player.getInventory().getItemByItemId(6360);
                    long l4 = itemInstance4 == null ? 0L : itemInstance4.getCount();
                    long l5 = SevenSigns.getInstance().getPlayerContribScore(player);
                    boolean bl = false;
                    if (l5 == SevenSigns.MAXIMUM_PLAYER_CONTRIB) {
                        player.sendPacket((IStaticPacket)Msg.CONTRIBUTION_LEVEL_HAS_EXCEEDED_THE_LIMIT_YOU_MAY_NOT_CONTINUE);
                        break;
                    }
                    long l6 = 0L;
                    long l7 = 0L;
                    long l8 = 0L;
                    switch (n2) {
                        case 1: {
                            l8 = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - l5) / 3L;
                            if (l8 <= l4) break;
                            l8 = l4;
                            break;
                        }
                        case 2: {
                            l7 = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - l5) / 5L;
                            if (l7 <= l3) break;
                            l7 = l3;
                            break;
                        }
                        case 3: {
                            l6 = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - l5) / 10L;
                            if (l6 <= l2) break;
                            l6 = l2;
                            break;
                        }
                        case 4: {
                            long l9 = l5;
                            l6 = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - l9) / 10L;
                            if (l6 > l2) {
                                l6 = l2;
                            }
                            if ((l7 = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - (l9 += l6 * 10L)) / 5L) > l3) {
                                l7 = l3;
                            }
                            if ((l8 = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - (l9 += l7 * 5L)) / 3L) <= l4) break;
                            l8 = l4;
                        }
                    }
                    if (l6 > 0L && player.getInventory().destroyItemByItemId(6362, l6)) {
                        bl = true;
                    }
                    if (l7 > 0L && player.getInventory().destroyItemByItemId(6361, l7)) {
                        bl = true;
                    }
                    if (l8 > 0L) {
                        ItemInstance itemInstance5 = player.getInventory().getItemByItemId(6360);
                        if (player.getInventory().destroyItemByItemId(6360, l8)) {
                            bl = true;
                        }
                    }
                    if (!bl) {
                        player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySSType", player, new Object[0]));
                        return;
                    }
                    l5 = SevenSigns.getInstance().addPlayerStoneContrib(player, l8, l7, l6);
                    SystemMessage systemMessage = new SystemMessage(1267);
                    systemMessage.addNumber(l5);
                    player.sendPacket((IStaticPacket)systemMessage);
                    this.a(player, 6, null, false);
                    break;
                }
                case 7: {
                    long l10 = 0L;
                    try {
                        l10 = Long.parseLong(string.substring(13).trim());
                    }
                    catch (NumberFormatException numberFormatException) {
                        player.sendMessage(new CustomMessage("common.IntegerAmount", player, new Object[0]));
                        return;
                    }
                    catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
                        player.sendMessage(new CustomMessage("common.IntegerAmount", player, new Object[0]));
                        return;
                    }
                    if (l < l10 || l10 < 1L) {
                        player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        return;
                    }
                    if (!player.getInventory().destroyItemByItemId(5575, l10)) break;
                    player.addAdena(l10);
                    player.sendPacket((IStaticPacket)SystemMessage2.removeItems(5575, l10));
                    player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(57, l10, 0));
                    break;
                }
                case 9: {
                    int n6 = SevenSigns.getInstance().getPlayerCabal(player);
                    int n7 = SevenSigns.getInstance().getCabalHighestScore();
                    if (!SevenSigns.getInstance().isSealValidationPeriod() || n6 != n7) break;
                    int n8 = SevenSigns.getInstance().getAncientAdenaReward(player, true);
                    if (n8 < 3) {
                        this.a(player, 9, "b", false);
                        return;
                    }
                    itemInstance = ItemFunctions.createItem(5575);
                    itemInstance.setCount(n8);
                    player.getInventory().addItem(itemInstance);
                    player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(5575, n8, 0));
                    this.a(player, 9, "a", false);
                    break;
                }
                case 17: {
                    n2 = Integer.parseInt(string.substring(14));
                    int n9 = 0;
                    long l11 = 0L;
                    int n10 = 0;
                    String string3 = null;
                    if (n2 == 4) {
                        ItemInstance itemInstance6;
                        long l12;
                        ItemInstance itemInstance7;
                        long l13;
                        ItemInstance itemInstance8 = player.getInventory().getItemByItemId(6360);
                        long l14 = itemInstance8 != null ? itemInstance8.getCount() : 0L;
                        long l15 = SevenSigns.calcAncientAdenaReward(l14, l13 = (itemInstance7 = player.getInventory().getItemByItemId(6361)) != null ? itemInstance7.getCount() : 0L, l12 = (itemInstance6 = player.getInventory().getItemByItemId(6362)) != null ? itemInstance6.getCount() : 0L);
                        if (l15 > 0L) {
                            if (itemInstance8 != null) {
                                player.getInventory().destroyItem(itemInstance8, l14);
                                player.sendPacket((IStaticPacket)SystemMessage2.removeItems(6360, l14));
                            }
                            if (itemInstance7 != null) {
                                player.getInventory().destroyItem(itemInstance7, l13);
                                player.sendPacket((IStaticPacket)SystemMessage2.removeItems(6361, l13));
                            }
                            if (itemInstance6 != null) {
                                player.getInventory().destroyItem(itemInstance6, l12);
                                player.sendPacket((IStaticPacket)SystemMessage2.removeItems(6362, l12));
                            }
                            itemInstance = ItemFunctions.createItem(5575);
                            itemInstance.setCount(l15);
                            player.getInventory().addItem(itemInstance);
                            player.sendPacket((IStaticPacket)SystemMessage2.obtainItems(5575, l15, 0));
                            break;
                        }
                        player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySS", player, new Object[0]));
                        break;
                    }
                    switch (n2) {
                        case 1: {
                            string3 = "blue";
                            n9 = 6360;
                            n10 = 3;
                            break;
                        }
                        case 2: {
                            string3 = "green";
                            n9 = 6361;
                            n10 = 5;
                            break;
                        }
                        case 3: {
                            string3 = "red";
                            n9 = 6362;
                            n10 = 10;
                        }
                    }
                    ItemInstance itemInstance9 = player.getInventory().getItemByItemId(n9);
                    if (itemInstance9 != null) {
                        l11 = itemInstance9.getCount();
                    }
                    String string4 = "seven_signs/signs_17.htm";
                    String string5 = HtmCache.getInstance().getNotNull(string4, player);
                    if (string5 != null) {
                        string5 = string5.replaceAll("%stoneColor%", string3);
                        string5 = string5.replaceAll("%stoneValue%", String.valueOf(n10));
                        string5 = string5.replaceAll("%stoneCount%", String.valueOf(l11));
                        string5 = string5.replaceAll("%stoneItemId%", String.valueOf(n9));
                        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                        npcHtmlMessage.setHtml(string5);
                        player.sendPacket((IStaticPacket)npcHtmlMessage);
                        break;
                    }
                    _log.warn("Problem with HTML text seven_signs/signs_17.htm: " + string4);
                    break;
                }
                case 18: {
                    int n11 = Integer.parseInt(string.substring(14, 18));
                    long l16 = 0L;
                    try {
                        l16 = Long.parseLong(string.substring(19).trim());
                    }
                    catch (Exception exception) {
                        player.sendMessage(new CustomMessage("common.IntegerAmount", player, new Object[0]));
                        break;
                    }
                    ItemInstance itemInstance10 = player.getInventory().getItemByItemId(n11);
                    if (itemInstance10 == null) {
                        player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySSType", player, new Object[0]));
                        break;
                    }
                    long l17 = itemInstance10.getCount();
                    long l18 = 0L;
                    if (l16 <= l17 && l16 > 0L) {
                        switch (n11) {
                            case 6360: {
                                l18 = SevenSigns.calcAncientAdenaReward(l16, 0L, 0L);
                                break;
                            }
                            case 6361: {
                                l18 = SevenSigns.calcAncientAdenaReward(0L, l16, 0L);
                                break;
                            }
                            case 6362: {
                                l18 = SevenSigns.calcAncientAdenaReward(0L, 0L, l16);
                            }
                        }
                        if (!player.getInventory().destroyItemByItemId(n11, l16)) break;
                        itemInstance = ItemFunctions.createItem(5575);
                        itemInstance.setCount(l18);
                        player.getInventory().addItem(itemInstance);
                        player.sendPacket(SystemMessage2.removeItems(n11, l16), SystemMessage2.obtainItems(5575, l18, 0));
                        break;
                    }
                    player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2SignsPriestInstance.DontHaveSSAmount", player, new Object[0]));
                    break;
                }
                case 19: {
                    int n12 = Integer.parseInt(string.substring(16));
                    String string6 = SevenSigns.getSealName(n12, true) + "_" + SevenSigns.getCabalShortName(n);
                    this.a(player, n3, string6, false);
                    break;
                }
                case 20: {
                    StringBuilder stringBuilder = new StringBuilder("<html><body><font color=\"LEVEL\">[Seal Status]</font><br>");
                    for (int i = 1; i < 4; ++i) {
                        int n13 = SevenSigns.getInstance().getSealOwner(i);
                        if (n13 != 0) {
                            stringBuilder.append("[" + SevenSigns.getSealName(i, false) + ": " + SevenSigns.getCabalName(n13) + "]<br>");
                            continue;
                        }
                        stringBuilder.append("[" + SevenSigns.getSealName(i, false) + ": Nothingness]<br>");
                    }
                    stringBuilder.append("<a action=\"bypass -h npc_" + this.getObjectId() + "_SevenSigns 3 " + n + "\">Go back.</a></body></html>");
                    NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
                    npcHtmlMessage.setHtml(stringBuilder.toString());
                    player.sendPacket((IStaticPacket)npcHtmlMessage);
                    break;
                }
                case 21: {
                    if (player.getLevel() < 60) {
                        this.a(player, 20, null, false);
                        return;
                    }
                    if (player.getVarInt("bmarketadena", 0) >= 500000) {
                        this.a(player, 21, null, false);
                        return;
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(11, 20);
                    calendar.set(12, 0);
                    calendar.set(13, 0);
                    Calendar calendar2 = Calendar.getInstance();
                    calendar2.set(11, 23);
                    calendar2.set(12, 59);
                    calendar2.set(13, 59);
                    if (System.currentTimeMillis() > calendar.getTimeInMillis() && System.currentTimeMillis() < calendar2.getTimeInMillis()) {
                        this.a(player, 23, null, false);
                        break;
                    }
                    this.a(player, 22, null, false);
                    break;
                }
                default: {
                    this.a(player, n3, null, false);
                }
            }
        }
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        int n2 = this.getTemplate().npcId;
        String string = "seven_signs/";
        int n3 = SevenSigns.getInstance().getSealOwner(1);
        int n4 = SevenSigns.getInstance().getSealOwner(2);
        int n5 = SevenSigns.getInstance().getPlayerCabal(player);
        boolean bl = SevenSigns.getInstance().isSealValidationPeriod();
        int n6 = SevenSigns.getInstance().getCabalHighestScore();
        block0 : switch (n2) {
            case 31078: 
            case 31079: 
            case 31080: 
            case 31081: 
            case 31082: 
            case 31083: 
            case 31084: 
            case 31168: 
            case 31692: 
            case 31694: 
            case 31997: {
                switch (n5) {
                    case 2: {
                        if (bl) {
                            if (n6 == 2) {
                                if (n6 != n4) {
                                    string = string + "dawn_priest_2c.htm";
                                    break block0;
                                }
                                string = string + "dawn_priest_2a.htm";
                                break block0;
                            }
                            string = string + "dawn_priest_2b.htm";
                            break block0;
                        }
                        string = string + "dawn_priest_1b.htm";
                        break block0;
                    }
                    case 1: {
                        if (bl) {
                            string = string + "dawn_priest_3b.htm";
                            break block0;
                        }
                        string = string + "dawn_priest_3a.htm";
                        break block0;
                    }
                }
                if (bl) {
                    if (n6 == 2) {
                        string = string + "dawn_priest_4.htm";
                        break;
                    }
                    string = string + "dawn_priest_2b.htm";
                    break;
                }
                string = string + "dawn_priest_1a.htm";
                break;
            }
            case 31085: 
            case 31086: 
            case 31087: 
            case 31088: 
            case 31089: 
            case 31090: 
            case 31091: 
            case 31169: 
            case 31693: 
            case 31695: 
            case 31998: {
                switch (n5) {
                    case 1: {
                        if (bl) {
                            if (n6 == 1) {
                                if (n6 != n4) {
                                    string = string + "dusk_priest_2c.htm";
                                    break block0;
                                }
                                string = string + "dusk_priest_2a.htm";
                                break block0;
                            }
                            string = string + "dusk_priest_2b.htm";
                            break block0;
                        }
                        string = string + "dusk_priest_1b.htm";
                        break block0;
                    }
                    case 2: {
                        if (bl) {
                            string = string + "dusk_priest_3b.htm";
                            break block0;
                        }
                        string = string + "dusk_priest_3a.htm";
                        break block0;
                    }
                }
                if (bl) {
                    if (n6 == 1) {
                        string = string + "dusk_priest_4.htm";
                        break;
                    }
                    string = string + "dusk_priest_2b.htm";
                    break;
                }
                string = string + "dusk_priest_1a.htm";
                break;
            }
            case 31092: {
                string = string + "blkmrkt_1.htm";
                break;
            }
            case 31113: {
                if (!player.isGM()) {
                    switch (n6) {
                        case 2: {
                            if (n5 == n6 && n5 == n3) break;
                            string = string + "mammmerch_2.htm";
                            return;
                        }
                        case 1: {
                            if (n5 == n6 && n5 == n3) break;
                            string = string + "mammmerch_2.htm";
                            return;
                        }
                    }
                }
                string = string + "mammmerch_1.htm";
                break;
            }
            case 31126: {
                if (!player.isGM()) {
                    switch (n6) {
                        case 2: {
                            if (n5 == n6 && n5 == n4) break;
                            string = string + "mammblack_2.htm";
                            return;
                        }
                        case 1: {
                            if (n5 == n6 && n5 == n4) break;
                            string = string + "mammblack_2.htm";
                            return;
                        }
                    }
                }
                string = string + "mammblack_1.htm";
                break;
            }
            default: {
                string = this.getHtmlPath(n2, n, player);
            }
        }
        player.sendPacket((IStaticPacket)new NpcHtmlMessage(player, this, string, n));
    }

    private String dc() {
        return SevenSigns.getCabalShortName(SevenSigns.getInstance().getPriestCabal(this.getNpcId()));
    }
}


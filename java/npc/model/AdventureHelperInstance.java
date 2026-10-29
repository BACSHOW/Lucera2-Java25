/*
 * Decompiled with CFR 0.152.
 */
package npc.model;

import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MerchantInstance;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.npc.NpcTemplate;

public class AdventureHelperInstance
extends MerchantInstance {
    private static final String axk = "newbiehelper/adventure_helper/adventure_helper_01001.htm";
    private static final String axl = "newbiehelper/adventure_helper/adventure_helper_01002.htm";
    private static final String axm = "newbiehelper/adventure_helper/adventure_helper_01003.htm";
    private static final String axn = "newbiehelper/adventure_helper/adventure_helper_01004.htm";
    private static final String axo = "newbiehelper/adventure_helper/adventure_helper_01011.htm";
    private static final String axp = "newbiehelper/adventure_helper/adventure_helper_01012.htm";
    private static final String axq = "newbiehelper/adventure_helper/adventure_helper_01013.htm";

    public AdventureHelperInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        this.showChatWindow(player, axk, new Object[0]);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!AdventureHelperInstance.canBypassCheck(player, this)) {
            return;
        }
        if (string.startsWith("menu_select ")) {
            String[] stringArray = string.replace("menu_select ", "").split(" ");
            int n = Integer.parseInt(stringArray[0]);
            int n2 = Integer.parseInt(stringArray[1]);
            if (n == -1000) {
                switch (n2) {
                    case 1: {
                        if (player.getLevel() > 5 && player.getLevel() < 20 && player.getClassId().getLevel() == 1) {
                            if (!player.getVarB("coupon1")) {
                                if (!player.isQuestContinuationPossible(true)) {
                                    return;
                                }
                                player.setVar("coupon1", "1", -1L);
                                Functions.addItem(player, 7832, 1L);
                                this.showChatWindow(player, axl, new Object[0]);
                                break;
                            }
                            this.showChatWindow(player, axn, new Object[0]);
                            break;
                        }
                        this.showChatWindow(player, axm, new Object[0]);
                        break;
                    }
                    case 2: {
                        if (player.getClassId().getLevel() > 1 && player.getLevel() < 40) {
                            if (!player.getVarB("coupon2")) {
                                if (!player.isQuestContinuationPossible(true)) {
                                    return;
                                }
                                player.setVar("coupon2", "1", -1L);
                                Functions.addItem(player, 7833, 1L);
                                this.showChatWindow(player, axo, new Object[0]);
                                break;
                            }
                            this.showChatWindow(player, axq, new Object[0]);
                            break;
                        }
                        this.showChatWindow(player, axp, new Object[0]);
                    }
                }
            }
            if (n == -303) {
                switch (n2) {
                    case 528: {
                        if (player.getLevel() > 5 && player.getLevel() < 20 && player.getClassId().getLevel() == 1) {
                            MultiSellHolder.getInstance().SeparateAndSend(528, player, 0.0);
                            break;
                        }
                        this.showChatWindow(player, axm, new Object[0]);
                        break;
                    }
                    case 530: {
                        if (player.getLevel() > 5 && player.getLevel() < 20 && player.getClassId().getLevel() == 1) {
                            MultiSellHolder.getInstance().SeparateAndSend(530, player, 0.0);
                            break;
                        }
                        this.showChatWindow(player, axm, new Object[0]);
                        break;
                    }
                    case 529: {
                        if (player.getClassId().getLevel() > 1 && player.getLevel() < 26) {
                            MultiSellHolder.getInstance().SeparateAndSend(529, player, 0.0);
                            break;
                        }
                        this.showChatWindow(player, axp, new Object[0]);
                        break;
                    }
                    case 531: {
                        if (player.getClassId().getLevel() > 1 && player.getLevel() < 26) {
                            MultiSellHolder.getInstance().SeparateAndSend(531, player, 0.0);
                            break;
                        }
                        this.showChatWindow(player, axp, new Object[0]);
                    }
                }
            }
        } else {
            super.onBypassFeedback(player, string);
        }
    }
}


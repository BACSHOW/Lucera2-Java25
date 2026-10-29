/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.instances;

import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.instancemanager.CoupleManager;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.Couple;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.ItemFunctions;

public class WeddingManagerInstance
extends NpcInstance {
    public WeddingManagerInstance(int n, NpcTemplate npcTemplate) {
        super(n, npcTemplate);
    }

    @Override
    public void showChatWindow(Player player, int n, Object ... objectArray) {
        String string = "wedding/start.htm";
        String string2 = "";
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile(string);
        npcHtmlMessage.replace("%replace%", string2);
        npcHtmlMessage.replace("%npcname%", this.getName());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public void onBypassFeedback(Player player, String string) {
        if (!WeddingManagerInstance.canBypassCheck(player, this)) {
            return;
        }
        String string2 = "wedding/start.htm";
        String string3 = "";
        if (player.getPartnerId() == 0) {
            string2 = "wedding/nopartner.htm";
            this.b(player, string2, string3);
            return;
        }
        Player player2 = GameObjectsStorage.getPlayer(player.getPartnerId());
        if (player2 == null || !player2.isOnline()) {
            string2 = "wedding/notfound.htm";
            this.b(player, string2, string3);
            return;
        }
        if (player.isMaried()) {
            string2 = "wedding/already.htm";
            this.b(player, string2, string3);
            return;
        }
        if (string.startsWith("AcceptWedding")) {
            player.setMaryAccepted(true);
            Couple couple = CoupleManager.getInstance().getCouple(player.getCoupleId());
            couple.marry();
            player.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2WeddingManagerMessage", player, new Object[0]));
            player.setMaried(true);
            player.setMaryRequest(false);
            player2.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2WeddingManagerMessage", player2, new Object[0]));
            player2.setMaried(true);
            player2.setMaryRequest(false);
            player.broadcastPacket(new MagicSkillUse(player, player, 2230, 1, 1, 0L));
            player2.broadcastPacket(new MagicSkillUse(player2, player2, 2230, 1, 1, 0L));
            player.broadcastPacket(new MagicSkillUse(player, player, 2025, 1, 1, 0L));
            player2.broadcastPacket(new MagicSkillUse(player2, player2, 2025, 1, 1, 0L));
            if (Config.WEDDING_GIVE_SALVATION_BOW) {
                ItemFunctions.addItem((Playable)player, 9140, 1L, false);
                ItemFunctions.addItem((Playable)player2, 9140, 1L, false);
            }
            if (Config.WEDDING_USE_COLOR) {
                if (player.getSex() == 1 && player2.getSex() == 0 || player.getSex() == 0 && player2.getSex() == 1) {
                    player.setNameColor(Config.WEDDING_NORMAL_COLOR);
                    player2.setNameColor(Config.WEDDING_NORMAL_COLOR);
                }
                if (player.getSex() == 1 && player2.getSex() == 1) {
                    player.setNameColor(Config.WEDDING_LESBIAN_COLOR);
                    player2.setNameColor(Config.WEDDING_LESBIAN_COLOR);
                }
                if (player.getSex() == 0 && player2.getSex() == 0) {
                    player.setNameColor(Config.WEDDING_GAY_COLOR);
                    player2.setNameColor(Config.WEDDING_GAY_COLOR);
                }
                player.broadcastUserInfo(true);
                player2.broadcastUserInfo(true);
            }
            if (Config.WEDDING_ANNOUNCE) {
                Announcements.getInstance().announceByCustomMessage("l2p.gameserver.model.instances.L2WeddingManagerMessage.announce", new String[]{player.getName(), player2.getName()});
            }
            string2 = "wedding/accepted.htm";
            string3 = player2.getName();
            this.b(player2, string2, string3);
            return;
        }
        if (player.isMaryRequest()) {
            if (Config.WEDDING_FORMALWEAR && !WeddingManagerInstance.X(player)) {
                string2 = "wedding/noformal.htm";
                this.b(player, string2, string3);
                return;
            }
            string2 = "wedding/ask.htm";
            player.setMaryRequest(false);
            player2.setMaryRequest(false);
            string3 = player2.getName();
            this.b(player, string2, string3);
            return;
        }
        if (string.startsWith("AskWedding")) {
            if (Config.WEDDING_FORMALWEAR && !WeddingManagerInstance.X(player)) {
                string2 = "wedding/noformal.htm";
                this.b(player, string2, string3);
                return;
            }
            if (ItemFunctions.getItemCount(player, Config.WEDDING_ITEM_ID_PRICE) < (long)Config.WEDDING_PRICE) {
                player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
            player.setMaryAccepted(true);
            player2.setMaryRequest(true);
            string3 = player2.getName();
            string2 = "wedding/requested.htm";
            ItemFunctions.removeItem(player, Config.WEDDING_ITEM_ID_PRICE, Config.WEDDING_PRICE, true);
            this.b(player, string2, string3);
            return;
        }
        if (string.startsWith("DeclineWedding")) {
            player.setMaryRequest(false);
            player2.setMaryRequest(false);
            player.setMaryAccepted(false);
            player2.setMaryAccepted(false);
            player.sendMessage("You declined");
            player2.sendMessage("Your partner declined");
            string3 = player2.getName();
            string2 = "wedding/declined.htm";
            this.b(player2, string2, string3);
            return;
        }
        if (player.isMaryAccepted()) {
            string2 = "wedding/waitforpartner.htm";
            this.b(player, string2, string3);
            return;
        }
        this.b(player, string2, string3);
    }

    private static boolean X(Player player) {
        return player != null && player.getInventory() != null && player.getInventory().getPaperdollItemId(10) == 6408;
    }

    private void b(Player player, String string, String string2) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(player, this);
        npcHtmlMessage.setFile(string);
        npcHtmlMessage.replace("%replace%", string2);
        npcHtmlMessage.replace("%npcname%", this.getName());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }
}


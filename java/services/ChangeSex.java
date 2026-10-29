/*
 * Decompiled with CFR 0.152.
 */
package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class ChangeSex
extends Functions {
    public void changesex_page() {
        Player player = this.getSelf();
        if (player == null) {
            return;
        }
        if (!Config.SERVICES_CHANGE_SEX_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5).setFile("scripts/services/sex_change.htm");
        npcHtmlMessage.replace("%item_id%", String.valueOf(Config.SERVICES_CHANGE_SEX_ITEM));
        npcHtmlMessage.replace("%item_count%", String.valueOf(Config.SERVICES_CHANGE_SEX_PRICE));
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public void change_sex() {
        Player player = this.getSelf();
        if (player == null || !ChangeSex.CheckPlayerConditions(player)) {
            return;
        }
        if (!Config.SERVICES_CHANGE_SEX_ENABLED) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
            return;
        }
        if (!player.isInPeaceZone() || !player.getReflection().isDefault()) {
            player.sendPacket((IStaticPacket)new NpcHtmlMessage(5).setFile("scripts/services/service_peace_zone.htm"));
            return;
        }
        if (ItemFunctions.getItemCount(player, Config.SERVICES_CHANGE_SEX_ITEM) < (long)Config.SERVICES_CHANGE_SEX_PRICE) {
            if (Config.SERVICES_CHANGE_SEX_ITEM == 57) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            } else {
                player.sendPacket((IStaticPacket)SystemMsg.INCORRECT_ITEM_COUNT);
            }
            return;
        }
        ItemFunctions.removeItem(player, Config.SERVICES_CHANGE_SEX_ITEM, Config.SERVICES_CHANGE_SEX_PRICE, true);
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("UPDATE characters SET sex = ? WHERE obj_Id = ?");
            preparedStatement.setInt(1, player.getSex() == 1 ? 0 : 1);
            preparedStatement.setInt(2, player.getObjectId());
            preparedStatement.executeUpdate();
        }
        catch (SQLException sQLException) {
            try {
                throw new RuntimeException(sQLException);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement);
                throw throwable;
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement);
        player.setHairColor(0);
        player.setHairStyle(0);
        player.setFace(0);
        Log.service("ChangeSex", player, " sex changed to " + (player.getSex() == 1 ? "male" : "female") + " for " + Config.SERVICES_CHANGE_SEX_ITEM + " " + Config.SERVICES_CHANGE_SEX_PRICE);
        player.logout();
    }
}


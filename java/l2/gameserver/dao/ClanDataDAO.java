package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClanDataDAO {
   private static final Logger _log = LoggerFactory.getLogger(ClanDataDAO.class);
   private static final ClanDataDAO uO = new ClanDataDAO();
   public static final String SELECT_CASTLE_OWNER = "SELECT `clan_id` FROM `clan_data` WHERE `hasCastle`=? LIMIT 1";
   public static final String SELECT_CLANHALL_OWNER = "SELECT `clan_id` FROM `clan_data` WHERE `hasHideout`=? LIMIT 1";

   public static ClanDataDAO getInstance() {
      return uO;
   }

   public Clan getOwner(Castle var1) {
      return this.a(var1, "SELECT `clan_id` FROM `clan_data` WHERE `hasCastle`=? LIMIT 1");
   }

   public Clan getOwner(ClanHall var1) {
      return this.a(var1, "SELECT `clan_id` FROM `clan_data` WHERE `hasHideout`=? LIMIT 1");
   }

   private Clan a(Residence var1, String var2) {
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement(var2);
         var4.setInt(1, var1.getId());
         var5 = var4.executeQuery();
         if (var5.next()) {
            return ClanTable.getInstance().getClan(var5.getInt("clan_id"));
         }
      } catch (Exception var10) {
         _log.error("ClanDataDAO.getOwner(Residence, String)", var10);
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }

      return null;
   }
}

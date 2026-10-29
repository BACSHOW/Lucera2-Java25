package achievements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.listener.game.OnCharacterDeleteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AchievementCounter {
   private static final Logger LOG = LoggerFactory.getLogger(AchievementCounter.class);
   private final int V;
   private final int aa;

   public AchievementCounter(int var1, int var2) {
      this.V = var1;
      this.aa = var2;
   }

   public static AchievementCounter makeDBStorableCounter(int var0, int var1) {
      return new AchievementCounter.AchievementCounterDb(var0, var1);
   }

   public int getObjid() {
      return this.V;
   }

   public int getAchId() {
      return this.aa;
   }

   public abstract int getVal();

   public abstract void setVal(int var1);

   public boolean isStorable() {
      return false;
   }

   public abstract void store();

   public int incrementAndGetValue() {
      this.setVal(this.getVal() + 1);
      return this.getVal();
   }

   static {
      CharacterDAO.getInstance().getCharacterDeleteListenerList().add(new AchievementCounter.AchievementCounterOnCharacterDeleteListener());
   }

   private static final class AchievementCounterDb extends AchievementCounter {
      private volatile Integer ab = null;

      public AchievementCounterDb(int var1, int var2) {
         super(var1, var2);
      }

      private int b() {
         Connection var1 = null;
         PreparedStatement var2 = null;
         ResultSet var3 = null;

         try {
            var1 = DatabaseFactory.getInstance().getConnection();
            var2 = var1.prepareStatement("SELECT `value` AS `value` FROM `ex_achievements` WHERE  `objId` = ? AND `achId` = ?");
            var2.setInt(1, this.getObjid());
            var2.setInt(2, this.getAchId());
            var3 = var2.executeQuery();
            if (var3.next()) {
               return var3.getInt("value");
            }
         } catch (SQLException var8) {
            AchievementCounter.LOG.error("Can't load counter for " + this.getObjid() + "(" + this.getAchId() + ")", var8);
         } finally {
            DbUtils.closeQuietly(var1, var2, var3);
         }

         return 0;
      }

      @Override
      public int getVal() {
         if (this.ab == null) {
            this.ab = this.b();
         }

         return this.ab;
      }

      @Override
      public void setVal(int var1) {
         this.ab = var1;
      }

      @Override
      public boolean isStorable() {
         return true;
      }

      @Override
      public void store() {
         Connection var1 = null;
         PreparedStatement var2 = null;

         try {
            var1 = DatabaseFactory.getInstance().getConnection();
            var2 = var1.prepareStatement("REPLACE INTO `ex_achievements` (`objId`, `achId`, `value`) VALUES (?, ?, ?)");
            var2.setInt(1, this.getObjid());
            var2.setInt(2, this.getAchId());
            var2.setInt(3, this.getVal());
            var2.executeUpdate();
         } catch (SQLException var7) {
            AchievementCounter.LOG.error("Can't store counter for " + this.getObjid() + "(" + this.getAchId() + ")");
         } finally {
            DbUtils.closeQuietly(var1, var2);
         }
      }
   }

   private static class AchievementCounterOnCharacterDeleteListener implements OnCharacterDeleteListener {
      private AchievementCounterOnCharacterDeleteListener() {
      }

      @Override
      public void onCharacterDelate(int var1) {
         Connection var2 = null;
         PreparedStatement var3 = null;

         try {
            var2 = DatabaseFactory.getInstance().getConnection();
            var3 = var2.prepareStatement("DELETE FROM `ex_achievements` WHERE `objId` = ?");
            var3.setInt(1, var1);
            var3.executeUpdate();
         } catch (SQLException var8) {
            AchievementCounter.LOG.error("Can't delete counter for " + var1);
         } finally {
            DbUtils.closeQuietly(var2, var3);
         }
      }
   }
}

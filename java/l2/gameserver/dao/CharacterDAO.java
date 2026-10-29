package l2.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import l2.commons.dbutils.DbUtils;
import l2.commons.listener.ListenerList;
import l2.gameserver.GameServer;
import l2.gameserver.data.xml.holder.ClassLevelGainHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.RaidBossSpawnManager;
import l2.gameserver.listener.game.OnCharacterDeleteListener;
import l2.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterDAO {
   private static final Logger LOG = LoggerFactory.getLogger(CharacterDAO.class);
   private static final CharacterDAO uA = new CharacterDAO();
   private static final String uB = "INSERT INTO `characters` (    `account_name`,  `obj_Id`, `char_name`, `face`, `hairStyle`, `hairColor`, `sex`, `karma`,    `pvpkills`, `pkkills`, `clanid`, `createtime`, `deletetime`, `title`, `accesslevel`,  `online`,    `leaveclan`, `deleteclan`, `nochannel`, `pledge_type`, `pledge_rank`, `lvl_joined_academy`, `apprentice` ) VALUES (     ?, ?, ?, ?, ?, ?, ?, ?,     ?, ?, ?, ?, ?, ?, ?, ?,     ?, ?, ?, ?, ?, ?, ? )";
   private static final String uC = "INSERT INTO `character_subclasses` (    `char_obj_id`, `class_id`, `exp`, `sp`, `curHp`, `curMp`, `curCp`,     `maxHp`, `maxMp`, `maxCp`, `level`, `active`, `isBase`, `death_penalty`) VALUES (    ?, ?, ?, ?, ?, ?, ?,     ?, ?, ?, ?, ?, ?, ? )";
   private CharacterDAO.CharacterDeleteListenerList uD = new CharacterDAO.CharacterDeleteListenerList();

   public static CharacterDAO getInstance() {
      return uA;
   }

   public CharacterDAO.CharacterDeleteListenerList getCharacterDeleteListenerList() {
      return this.uD;
   }

   public void deleteCharacterDataByObjId(int var1) {
      if (var1 >= 0) {
         RaidBossSpawnManager.getInstance().deletePoints(var1);

         for (Integer var4 : ItemsDAO.getInstance().loadItemObjectIdsByOwner(var1)) {
            int var5 = var4;
            boolean var6 = false;
            if (!var6) {
               ItemsDAO.getInstance().delete(var5);
            }
         }

         Connection var12 = null;
         PreparedStatement var13 = null;

         try {
            var12 = DatabaseFactory.getInstance().getConnection();
            var13 = var12.prepareStatement("DELETE FROM `characters` WHERE `obj_Id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_subclasses` WHERE `char_obj_id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_blocklist` WHERE `obj_Id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_effects_save` WHERE `object_id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_friends` WHERE `char_id`=? OR `friend_id`=?");
            var13.setInt(1, var1);
            var13.setInt(2, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_group_reuse` WHERE `object_id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_hennas` WHERE `char_obj_id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_macroses` WHERE `char_obj_id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_post_friends` WHERE `object_id`=? OR `post_friend`=?");
            var13.setInt(1, var1);
            var13.setInt(2, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_quests` WHERE `char_id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_recipebook` WHERE `char_id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_recommends` WHERE `objId`=? OR `targetId`=?");
            var13.setInt(1, var1);
            var13.setInt(2, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_shortcuts` WHERE `object_id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_skills` WHERE `char_obj_id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            var13 = var12.prepareStatement("DELETE FROM `character_skills_save` WHERE `char_obj_id`=?");
            var13.setInt(1, var1);
            var13.executeUpdate();
            DbUtils.closeQuietly(var13);
            CharacterVariablesDAO.getInstance().deleteVars0(var12, var1);
            this.getCharacterDeleteListenerList().onCharacterDelete(var1);
         } catch (SQLException var10) {
            LOG.error("Can't delete character", var10);
         } finally {
            DbUtils.closeQuietly(var12);
         }
      }
   }

   public boolean insert(Player var1) {
      Connection var2 = null;
      PreparedStatement var3 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement(
            "INSERT INTO `characters` (    `account_name`,  `obj_Id`, `char_name`, `face`, `hairStyle`, `hairColor`, `sex`, `karma`,    `pvpkills`, `pkkills`, `clanid`, `createtime`, `deletetime`, `title`, `accesslevel`,  `online`,    `leaveclan`, `deleteclan`, `nochannel`, `pledge_type`, `pledge_rank`, `lvl_joined_academy`, `apprentice` ) VALUES (     ?, ?, ?, ?, ?, ?, ?, ?,     ?, ?, ?, ?, ?, ?, ?, ?,     ?, ?, ?, ?, ?, ?, ? )"
         );
         var3.setString(1, var1.getAccountName());
         var3.setInt(2, var1.getObjectId());
         var3.setString(3, var1.getName());
         var3.setInt(4, var1.getFace());
         var3.setInt(5, var1.getHairStyle());
         var3.setInt(6, var1.getHairColor());
         var3.setInt(7, var1.getSex());
         var3.setInt(8, var1.getKarma());
         var3.setInt(9, var1.getPvpKills());
         var3.setInt(10, var1.getPkKills());
         var3.setInt(11, var1.getClanId());
         var3.setLong(12, var1.getCreateTime() / 1000L);
         var3.setInt(13, var1.getDeleteTimer());
         var3.setString(14, var1.getTitle());
         var3.setInt(15, var1.getAccessLevel());
         var3.setInt(16, var1.isOnline() ? 1 : 0);
         var3.setLong(17, var1.getLeaveClanTime() / 1000L);
         var3.setLong(18, var1.getDeleteClanTime() / 1000L);
         var3.setLong(19, var1.getNoChannel() > 0L ? var1.getNoChannel() / 1000L : var1.getNoChannel());
         var3.setInt(20, var1.getPledgeType());
         var3.setInt(21, var1.getPowerGrade());
         var3.setInt(22, var1.getLvlJoinedAcademy());
         var3.setInt(23, var1.getApprentice());
         var3.executeUpdate();
         DbUtils.close(var3);
         var3 = var2.prepareStatement(
            "INSERT INTO `character_subclasses` (    `char_obj_id`, `class_id`, `exp`, `sp`, `curHp`, `curMp`, `curCp`,     `maxHp`, `maxMp`, `maxCp`, `level`, `active`, `isBase`, `death_penalty`) VALUES (    ?, ?, ?, ?, ?, ?, ?,     ?, ?, ?, ?, ?, ?, ? )"
         );
         var3.setInt(1, var1.getObjectId());
         var3.setInt(2, var1.getTemplate().classId.getId());
         var3.setInt(3, 0);
         var3.setInt(4, 0);
         var3.setDouble(5, ClassLevelGainHolder.getInstance().getHp(var1.getClassId(), var1.getLevel()));
         var3.setDouble(6, ClassLevelGainHolder.getInstance().getMp(var1.getClassId(), var1.getLevel()));
         var3.setDouble(7, ClassLevelGainHolder.getInstance().getCp(var1.getClassId(), var1.getLevel()));
         var3.setDouble(8, ClassLevelGainHolder.getInstance().getHp(var1.getClassId(), var1.getLevel()));
         var3.setDouble(9, ClassLevelGainHolder.getInstance().getMp(var1.getClassId(), var1.getLevel()));
         var3.setDouble(10, ClassLevelGainHolder.getInstance().getCp(var1.getClassId(), var1.getLevel()));
         var3.setInt(11, 1);
         var3.setInt(12, 1);
         var3.setInt(13, 1);
         var3.setInt(14, 0);
         var3.executeUpdate();
      } catch (Exception var9) {
         LOG.error("Can't store character", var9);
         return false;
      } finally {
         DbUtils.closeQuietly(var2, var3);
      }

      return true;
   }

   public int getObjectIdByName(String var1) {
      int var2 = 0;
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("SELECT `obj_Id` FROM `characters` WHERE `char_name`=?");
         var4.setString(1, var1);
         var5 = var4.executeQuery();
         if (var5.next()) {
            var2 = var5.getInt(1);
         }
      } catch (Exception var10) {
         LOG.error("Can't get character object id by name" + var10, var10);
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }

      return var2;
   }

   public Map<Integer, String> getAllObjectIdsAndName() {
      HashMap var1 = new HashMap();
      Connection var2 = null;
      Statement var3 = null;
      ResultSet var4 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.createStatement();
         var4 = var3.executeQuery("SELECT `obj_Id`, `char_name` FROM `characters`");
         if (var4.next()) {
            var1.put(var4.getInt("obj_Id"), var4.getString("char_name"));
         }
      } catch (Exception var9) {
         LOG.error("Can't get character object id by name" + var9, var9);
      } finally {
         DbUtils.closeQuietly(var2, var3, var4);
      }

      return var1;
   }

   public String getNameByObjectId(int var1) {
      String var2 = "";
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("SELECT `char_name` FROM `characters` WHERE `obj_Id`=?");
         var4.setInt(1, var1);
         var5 = var4.executeQuery();
         if (var5.next()) {
            var2 = var5.getString(1);
         }
      } catch (Exception var10) {
         LOG.error("Can't get char name by id" + var10, var10);
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }

      return var2;
   }

   public int accountCharNumber(String var1) {
      int var2 = 0;
      Connection var3 = null;
      PreparedStatement var4 = null;
      ResultSet var5 = null;

      try {
         var3 = DatabaseFactory.getInstance().getConnection();
         var4 = var3.prepareStatement("SELECT COUNT(`char_name`) FROM `characters` WHERE `account_name`=?");
         var4.setString(1, var1);
         var5 = var4.executeQuery();
         if (var5.next()) {
            var2 = var5.getInt(1);
         }
      } catch (Exception var10) {
         LOG.error("Can't get amount of the account characters", var10);
      } finally {
         DbUtils.closeQuietly(var3, var4, var5);
      }

      return var2;
   }

   public class CharacterDeleteListenerList extends ListenerList<GameServer> {
      public void onCharacterDelete(int var1) {
         this.forEachListener(OnCharacterDeleteListener.class, var1x -> {
            try {
               var1x.onCharacterDelate(var1);
            } catch (Exception var3) {
               CharacterDAO.LOG.warn("Character delete listener", var3);
            }
         });
      }
   }
}

package l2.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.network.l2.s2c.ExSubPledgeSkillAdd;
import l2.gameserver.tables.SkillTable;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.maps.impl.CTreeIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubUnit {
   private static final Logger _log = LoggerFactory.getLogger(SubUnit.class);
   private IntObjectMap<Skill> _skills = new CTreeIntObjectMap();
   private IntObjectMap<UnitMember> YO = new CHashIntObjectMap();
   private int _type;
   private int YP;
   private UnitMember YQ;
   private int YR;
   private String _name;
   private Clan Ia;

   public SubUnit(Clan var1, int var2, UnitMember var3, String var4) {
      this.Ia = var1;
      this._type = var2;
      this._name = var4;
      this.setLeader(var3, false);
   }

   public SubUnit(Clan var1, int var2, int var3, String var4) {
      this.Ia = var1;
      this._type = var2;
      this.YP = var3;
      this._name = var4;
   }

   public int getType() {
      return this._type;
   }

   public String getName() {
      return this._name;
   }

   public UnitMember getLeader() {
      return this.YQ;
   }

   public boolean isUnitMember(int var1) {
      return this.YO.containsKey(var1);
   }

   public void addUnitMember(UnitMember var1) {
      this.YO.put(var1.getObjectId(), var1);
   }

   public UnitMember getUnitMember(int var1) {
      return var1 == 0 ? null : (UnitMember)this.YO.get(var1);
   }

   public UnitMember getUnitMember(String var1) {
      for (UnitMember var3 : this.getUnitMembers()) {
         if (var3.getName().equalsIgnoreCase(var1)) {
            return var3;
         }
      }

      return null;
   }

   public void removeUnitMember(int var1) {
      UnitMember var2 = (UnitMember)this.YO.remove(var1);
      if (var2 != null) {
         if (var1 == this.getLeaderObjectId()) {
            this.setLeader(null, true);
         }

         if (var2.hasSponsor()) {
            this.Ia.getAnyMember(var2.getSponsor()).setApprentice(0);
         }

         a(var2);
         var2.setPlayerInstance(null, true);
      }
   }

   public void replace(int var1, int var2) {
      SubUnit var3 = this.Ia.getSubUnit(var2);
      if (var3 != null) {
         UnitMember var4 = (UnitMember)this.YO.remove(var1);
         if (var4 != null) {
            var4.setPledgeType(var2);
            var3.addUnitMember(var4);
            if (var4.getPowerGrade() > 5) {
               var4.setPowerGrade(this.Ia.getAffiliationRank(var4.getPledgeType()));
            }
         }
      }
   }

   public int getLeaderObjectId() {
      return this.YQ == null ? 0 : this.YQ.getObjectId();
   }

   public int size() {
      return this.YO.size();
   }

   public Collection<UnitMember> getUnitMembers() {
      return this.YO.values();
   }

   public void updateDbLeader(UnitMember var1) {
      if (this.getType() == 0) {
         if (var1 != this.YQ) {
            this.YR = var1.getObjectId();
         } else {
            this.YR = 0;
         }
      }

      Connection var2 = null;
      PreparedStatement var3 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement("UPDATE `clan_subpledges` SET `leader_id`=? WHERE `clan_id`=? and `type`=?");
         var3.setInt(1, var1.getObjectId());
         var3.setInt(2, this.Ia.getClanId());
         var3.setInt(3, this._type);
         var3.execute();
      } catch (Exception var8) {
         _log.error("Exception: " + var8, var8);
      } finally {
         DbUtils.closeQuietly(var2, var3);
      }
   }

   public void setLeader(UnitMember var1, boolean var2) {
      UnitMember var3 = this.YQ;
      if (var3 != null) {
         var3.setLeaderOf(-128);
      }

      this.YQ = var1;
      this.YP = var1 == null ? 0 : var1.getObjectId();
      if (var1 != null) {
         var1.setLeaderOf(this._type);
      }

      if (var2) {
         this.updateDbLeader(this.YQ);
      }
   }

   public void setName(String var1, boolean var2) {
      this._name = var1;
      if (var2) {
         Connection var3 = null;
         PreparedStatement var4 = null;

         try {
            var3 = DatabaseFactory.getInstance().getConnection();
            var4 = var3.prepareStatement("UPDATE `clan_subpledges` SET `name`=? WHERE `clan_id`=? and `type`=?");
            var4.setString(1, this._name);
            var4.setInt(2, this.Ia.getClanId());
            var4.setInt(3, this._type);
            var4.execute();
         } catch (Exception var9) {
            _log.error("Exception: " + var9, var9);
         } finally {
            DbUtils.closeQuietly(var3, var4);
         }
      }
   }

   public String getLeaderName() {
      return this.YQ == null ? "" : this.YQ.getName();
   }

   public Skill addSkill(Skill var1, boolean var2) {
      Skill var3 = null;
      if (var1 != null) {
         var3 = (Skill)this._skills.put(var1.getId(), var1);
         if (var2) {
            Connection var4 = null;
            PreparedStatement var5 = null;

            try {
               var4 = DatabaseFactory.getInstance().getConnection();
               if (var3 != null) {
                  var5 = var4.prepareStatement("UPDATE `clan_subpledges_skills` SET `skill_level`=? WHERE `skill_id`=? AND `clan_id`=? AND `type`=?");
                  var5.setInt(1, var1.getLevel());
                  var5.setInt(2, var3.getId());
                  var5.setInt(3, this.Ia.getClanId());
                  var5.setInt(4, this._type);
                  var5.execute();
               } else {
                  var5 = var4.prepareStatement("INSERT INTO `clan_subpledges_skills` (`clan_id`,`type`,`skill_id`,`skill_level`) VALUES (?,?,?,?)");
                  var5.setInt(1, this.Ia.getClanId());
                  var5.setInt(2, this._type);
                  var5.setInt(3, var1.getId());
                  var5.setInt(4, var1.getLevel());
                  var5.execute();
               }
            } catch (Exception var10) {
               _log.warn("Exception: " + var10, var10);
            } finally {
               DbUtils.closeQuietly(var4, var5);
            }
         }

         ExSubPledgeSkillAdd var12 = new ExSubPledgeSkillAdd(this._type, var1.getId(), var1.getLevel());

         for (UnitMember var6 : this.Ia) {
            if (var6.isOnline()) {
               Player var7 = var6.getPlayer();
               if (var7 != null) {
                  var7.sendPacket(var12);
                  if (var7.getPledgeType() == this._type) {
                     this.a(var7, var1);
                  }
               }
            }
         }
      }

      return var3;
   }

   public int getNextLeaderObjectId() {
      return this.YR;
   }

   public void addSkillsQuietly(Player var1) {
      for (Skill var3 : this._skills.values()) {
         this.a(var1, var3);
      }
   }

   public void enableSkills(Player var1) {
      for (Skill var3 : this._skills.values()) {
         if (var3.getMinRank() <= var1.getPledgeClass()) {
            var1.removeUnActiveSkill(var3);
         }
      }
   }

   public void disableSkills(Player var1) {
      for (Skill var3 : this._skills.values()) {
         var1.addUnActiveSkill(var3);
      }
   }

   private void a(Player var1, Skill var2) {
      if (var2.getMinRank() <= var1.getPledgeClass()) {
         var1.addSkill(var2, false);
         if (this.Ia.getReputationScore() < 0 || var1.isOlyParticipant()) {
            var1.addUnActiveSkill(var2);
         }
      }
   }

   public Collection<Skill> getSkills() {
      return this._skills.values();
   }

   private static void a(UnitMember var0) {
      Connection var1 = null;
      PreparedStatement var2 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement(
            "UPDATE `characters` SET `clanid`=0, `pledge_type`=?, `pledge_rank`=0, `lvl_joined_academy`=0, `apprentice`=0, `title`='', `leaveclan`=? WHERE `obj_Id`=?"
         );
         var2.setInt(1, -128);
         var2.setLong(2, System.currentTimeMillis() / 1000L);
         var2.setInt(3, var0.getObjectId());
         var2.execute();
      } catch (Exception var7) {
         _log.warn("Exception: " + var7, var7);
      } finally {
         DbUtils.closeQuietly(var1, var2);
      }
   }

   public void restore() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement(
            "SELECT `c`.`char_name` AS `char_name`,`s`.`level` AS `level`,`s`.`class_id` AS `classid`,`c`.`obj_Id` AS `obj_id`,`c`.`title` AS `title`,`c`.`pledge_rank` AS `pledge_rank`,`c`.`apprentice` AS `apprentice`, `c`.`sex` AS `sex` FROM `characters` `c` LEFT JOIN `character_subclasses` `s` ON (`s`.`char_obj_id` = `c`.`obj_Id` AND `s`.`isBase` = '1') WHERE `c`.`clanid`=? AND `c`.`pledge_type`=? ORDER BY `c`.`lastaccess` DESC"
         );
         var2.setInt(1, this.Ia.getClanId());
         var2.setInt(2, this._type);
         var3 = var2.executeQuery();

         while (var3.next()) {
            UnitMember var4 = new UnitMember(
               this.Ia,
               var3.getString("char_name"),
               var3.getString("title"),
               var3.getInt("level"),
               var3.getInt("classid"),
               var3.getInt("obj_Id"),
               this._type,
               var3.getInt("pledge_rank"),
               var3.getInt("apprentice"),
               var3.getInt("sex"),
               -128
            );
            this.addUnitMember(var4);
         }

         if (this._type != -1) {
            SubUnit var11 = this.Ia.getSubUnit(0);
            UnitMember var5 = var11.getUnitMember(this.YP);
            if (var5 != null) {
               this.setLeader(var5, false);
            } else if (this._type == 0) {
               _log.error("Clan " + this._name + " have no leader!");
            }
         }
      } catch (Exception var9) {
         _log.warn("Error while restoring clan members for clan: " + this.Ia.getClanId() + " " + var9, var9);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   public void restoreSkills() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT `skill_id`,`skill_level` FROM `clan_subpledges_skills` WHERE `clan_id`=? AND `type`=?");
         var2.setInt(1, this.Ia.getClanId());
         var2.setInt(2, this._type);
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("skill_id");
            int var5 = var3.getInt("skill_level");
            Skill var6 = SkillTable.getInstance().getInfo(var4, var5);
            this._skills.put(var6.getId(), var6);
         }
      } catch (Exception var10) {
         _log.warn("Exception: " + var10, var10);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   public int getSkillLevel(int var1, int var2) {
      Skill var3 = (Skill)this._skills.get(var1);
      return var3 == null ? var2 : var3.getLevel();
   }

   public int getSkillLevel(int var1) {
      return this.getSkillLevel(var1, -1);
   }
}

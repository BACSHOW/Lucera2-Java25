package l2.gameserver.model.pledge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.collections.JoinedIterator;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.cache.CrestCache;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ResidenceType;
import l2.gameserver.model.items.ClanWarehouse;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PledgeReceiveSubPledgeCreated;
import l2.gameserver.network.l2.s2c.PledgeShowInfoUpdate;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListAll;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListDeleteAll;
import l2.gameserver.network.l2.s2c.PledgeSkillList;
import l2.gameserver.network.l2.s2c.PledgeSkillListAdd;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Log;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CTreeIntObjectMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clan implements Iterable<UnitMember> {
   private static final Logger _log = LoggerFactory.getLogger(Clan.class);
   private final int CU;
   private int Yo;
   private int _level;
   private int Yt;
   private int Yu;
   private int Yv;
   private int Yw;
   private long Ys;
   private long Yx;
   private long Yy;
   private long Yz;
   private long YA;
   private final ClanWarehouse YB;
   private int YC = 0;
   private String YD = null;
   private List<Clan> YE = new CopyOnWriteArrayList<>();
   private List<Clan> YF = new CopyOnWriteArrayList<>();
   protected IntObjectMap<Skill> _skills = new CTreeIntObjectMap();
   protected IntObjectMap<RankPrivs> _privs = new CTreeIntObjectMap();
   protected IntObjectMap<SubUnit> _subUnits = new CTreeIntObjectMap();
   public static long DISBAND_PENALTY = 604800000L;
   public static long DISBAND_TIME = 172800000L;
   private int YG = 0;
   private int YH;
   public static final int CP_NOTHING = 0;
   public static final int CP_CL_INVITE_CLAN = 2;
   public static final int CP_CL_MANAGE_TITLES = 4;
   public static final int CP_CL_WAREHOUSE_SEARCH = 8;
   public static final int CP_CL_MANAGE_RANKS = 16;
   public static final int CP_CL_CLAN_WAR = 32;
   public static final int CP_CL_DISMISS = 64;
   public static final int CP_CL_EDIT_CREST = 128;
   public static final int CP_CL_APPRENTICE = 256;
   public static final int CP_CL_TROOPS_FAME = 512;
   public static final int CP_CH_ENTRY_EXIT = 1024;
   public static final int CP_CH_USE_FUNCTIONS = 2048;
   public static final int CP_CH_AUCTION = 4096;
   public static final int CP_CH_DISMISS = 8192;
   public static final int CP_CH_SET_FUNCTIONS = 16384;
   public static final int CP_CS_ENTRY_EXIT = 32768;
   public static final int CP_CS_MANOR_ADMIN = 65536;
   public static final int CP_CS_MANAGE_SIEGE = 131072;
   public static final int CP_CS_USE_FUNCTIONS = 262144;
   public static final int CP_CS_DISMISS = 524288;
   public static final int CP_CS_TAXES = 1048576;
   public static final int CP_CS_MERCENARIES = 2097152;
   public static final int CP_CS_SET_FUNCTIONS = 4194304;
   public static final int CP_ALL = 8388606;
   public static final int RANK_FIRST = 1;
   public static final int RANK_LAST = 9;
   public static final int SUBUNIT_NONE = -128;
   public static final int SUBUNIT_ACADEMY = -1;
   public static final int SUBUNIT_MAIN_CLAN = 0;
   public static final int SUBUNIT_ROYAL1 = 100;
   public static final int SUBUNIT_ROYAL2 = 200;
   public static final int SUBUNIT_KNIGHT1 = 1001;
   public static final int SUBUNIT_KNIGHT2 = 1002;
   public static final int SUBUNIT_KNIGHT3 = 2001;
   public static final int SUBUNIT_KNIGHT4 = 2002;
   private static final Clan.ClanReputationComparator YI = new Clan.ClanReputationComparator();
   private static final int YJ = 100;

   public Clan(int var1) {
      this.CU = var1;
      this.InitializePrivs();
      this.YB = new ClanWarehouse(this);
      this.YB.restore();
   }

   public int getClanId() {
      return this.CU;
   }

   public int getLeaderId() {
      return this.getLeaderId(0);
   }

   public UnitMember getLeader() {
      return this.getLeader(0);
   }

   public String getLeaderName() {
      return this.getLeaderName(0);
   }

   public String getName() {
      return this.getUnitName(0);
   }

   public UnitMember getAnyMember(int var1) {
      for (SubUnit var3 : this.getAllSubUnits()) {
         UnitMember var4 = var3.getUnitMember(var1);
         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   public UnitMember getAnyMember(String var1) {
      for (SubUnit var3 : this.getAllSubUnits()) {
         UnitMember var4 = var3.getUnitMember(var1);
         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   public int getAllSize() {
      int var1 = 0;

      for (SubUnit var3 : this.getAllSubUnits()) {
         var1 += var3.size();
      }

      return var1;
   }

   public String getUnitName(int var1) {
      return var1 != -128 && this._subUnits.containsKey(var1) ? this.getSubUnit(var1).getName() : "";
   }

   public String getLeaderName(int var1) {
      return var1 != -128 && this._subUnits.containsKey(var1) ? this.getSubUnit(var1).getLeaderName() : "";
   }

   public int getLeaderId(int var1) {
      return var1 != -128 && this._subUnits.containsKey(var1) ? this.getSubUnit(var1).getLeaderObjectId() : 0;
   }

   public UnitMember getLeader(int var1) {
      return var1 != -128 && this._subUnits.containsKey(var1) ? this.getSubUnit(var1).getLeader() : null;
   }

   public void flush() {
      for (UnitMember var2 : this) {
         this.removeClanMember(var2.getObjectId());
      }

      this.YB.writeLock();

      try {
         for (ItemInstance var4 : this.YB.getItems()) {
            this.YB.destroyItem(var4);
         }
      } finally {
         this.YB.writeUnlock();
      }

      if (this.Yt != 0) {
         ResidenceHolder.getInstance().getResidence(Castle.class, this.Yt).changeOwner(null);
      }
   }

   public void removeClanMember(int var1) {
      if (var1 != this.getLeaderId(0)) {
         for (SubUnit var3 : this.getAllSubUnits()) {
            if (var3.isUnitMember(var1)) {
               this.removeClanMember(var3.getType(), var1);
               break;
            }
         }
      }
   }

   public void removeClanMember(int var1, int var2) {
      SubUnit var3 = this.getSubUnit(var1);
      if (var3 != null) {
         var3.removeUnitMember(var2);
      }
   }

   public List<UnitMember> getAllMembers() {
      Collection var1 = this.getAllSubUnits();
      int var2 = 0;

      for (Object __raw_var4_227 : (Iterable<?>)(Object)(var1)) {
         SubUnit var4 = (SubUnit)__raw_var4_227;
         var2 += var4.size();
      }

      ArrayList var6 = new ArrayList(var2);

      for (Object __raw_var5_233 : (Iterable<?>)(Object)(var1)) {
         SubUnit var5 = (SubUnit)__raw_var5_233;
         var6.addAll(var5.getUnitMembers());
      }

      return var6;
   }

   public List<Player> getOnlineMembers(int var1) {
      ArrayList var2 = new ArrayList(this.getAllSize() - 1);

      for (UnitMember var4 : this) {
         if (var4 != null && var4.isOnline() && var4.getObjectId() != var1) {
            var2.add(var4.getPlayer());
         }
      }

      return var2;
   }

   public int getAllyId() {
      return this.Yo;
   }

   public int getLevel() {
      return this._level;
   }

   public int getCastle() {
      return this.Yt;
   }

   public int getHasHideout() {
      return this.Yu;
   }

   public int getResidenceId(ResidenceType var1) {
      switch (var1) {
         case Castle:
            return this.Yt;
         case ClanHall:
            return this.Yu;
         default:
            return 0;
      }
   }

   public void setAllyId(int var1) {
      this.Yo = var1;
   }

   public void setHasCastle(int var1) {
      this.Yt = var1;
   }

   public void setHasHideout(int var1) {
      this.Yu = var1;
   }

   public void setLevel(int var1) {
      this._level = var1;
   }

   public boolean isAnyMember(int var1) {
      for (SubUnit var3 : this.getAllSubUnits()) {
         if (var3.isUnitMember(var1)) {
            return true;
         }
      }

      return false;
   }

   public void updateClanInDB() {
      if (this.getLeaderId() == 0) {
         _log.warn("updateClanInDB with empty LeaderId");
         Thread.dumpStack();
      } else if (this.getClanId() == 0) {
         _log.warn("updateClanInDB with empty ClanId");
         Thread.dumpStack();
      } else {
         Connection var1 = null;
         PreparedStatement var2 = null;

         try {
            var1 = DatabaseFactory.getInstance().getConnection();
            var2 = var1.prepareStatement(
               "UPDATE `clan_data` SET `ally_id`=?,`reputation_score`=?,`expelled_member`=?,`leaved_ally`=?,`dissolved_ally`=?,`clan_level`=?,`warehouse`=?,`disband_end`=?,`disband_penalty`=?,`custom_points`=? WHERE `clan_id`=?"
            );
            var2.setInt(1, this.getAllyId());
            var2.setInt(2, this.getReputationScore());
            var2.setLong(3, this.getExpelledMemberTime() / 1000L);
            var2.setLong(4, this.getLeavedAllyTime() / 1000L);
            var2.setLong(5, this.getDissolvedAllyTime() / 1000L);
            var2.setInt(6, this._level);
            var2.setInt(7, this.getWhBonus());
            var2.setInt(8, (int)(this.getDisbandEndTime() / 1000L));
            var2.setInt(9, (int)(this.getDisbandPenaltyTime() / 1000L));
            var2.setInt(10, this.getCustomPoints());
            var2.setInt(11, this.getClanId());
            var2.execute();
         } catch (Exception var7) {
            _log.warn("error while updating clan '" + this.getClanId() + "' data in db");
            _log.error("", var7);
         } finally {
            DbUtils.closeQuietly(var1, var2);
         }
      }
   }

   public void store() {
      Connection var1 = null;
      PreparedStatement var2 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement(
            "INSERT INTO `clan_data` (`clan_id`,`clan_level`,`hasCastle`,`hasHideout`,`ally_id`,`expelled_member`,`leaved_ally`,`dissolved_ally`) values (?,?,?,?,?,?,?,?)"
         );
         var2.setInt(1, this.CU);
         var2.setInt(2, this._level);
         var2.setInt(3, this.Yt);
         var2.setInt(4, this.Yu);
         var2.setInt(5, this.Yo);
         var2.setLong(6, this.getExpelledMemberTime() / 1000L);
         var2.setLong(7, this.getLeavedAllyTime() / 1000L);
         var2.setLong(8, this.getDissolvedAllyTime() / 1000L);
         var2.execute();
         DbUtils.close(var2);
         SubUnit var3 = (SubUnit)this._subUnits.get(0);
         var2 = var1.prepareStatement("INSERT INTO `clan_subpledges` (`clan_id`, `type`, `leader_id`, `name`) VALUES (?,?,?,?)");
         var2.setInt(1, this.CU);
         var2.setInt(2, var3.getType());
         var2.setInt(3, var3.getLeaderObjectId());
         var2.setString(4, var3.getName());
         var2.execute();
         DbUtils.close(var2);
         var2 = var1.prepareStatement("UPDATE `characters` SET `clanid`=?,`pledge_type`=? WHERE `obj_Id`=?");
         var2.setInt(1, this.getClanId());
         var2.setInt(2, var3.getType());
         var2.setInt(3, this.getLeaderId());
         var2.execute();
      } catch (Exception var7) {
         _log.warn("Exception: " + var7, var7);
      } finally {
         DbUtils.closeQuietly(var1, var2);
      }
   }

   public static Clan restore(int var0) {
      if (var0 == 0) {
         return null;
      }

      Clan var1 = null;
      Connection var2 = null;
      PreparedStatement var3 = null;
      ResultSet var4 = null;

      try {
         var2 = DatabaseFactory.getInstance().getConnection();
         var3 = var2.prepareStatement(
            "SELECT `clan_level`,`hasCastle`,`hasHideout`,`ally_id`,`reputation_score`,`expelled_member`,`leaved_ally`,`dissolved_ally`,`warehouse`,`disband_end`,`disband_penalty`,`custom_points` FROM `clan_data` where `clan_id`=?"
         );
         var3.setInt(1, var0);
         var4 = var3.executeQuery();
         if (!var4.next()) {
            _log.warn("Clan " + var0 + " doesnt exists!");
            return null;
         }

         var1 = new Clan(var0);
         var1.setLevel(var4.getInt("clan_level"));
         var1.setHasCastle(var4.getInt("hasCastle"));
         var1.setHasHideout(var4.getInt("hasHideout"));
         var1.setAllyId(var4.getInt("ally_id"));
         var1.YG = var4.getInt("reputation_score");
         var1.setExpelledMemberTime(var4.getLong("expelled_member") * 1000L);
         var1.setLeavedAllyTime(var4.getLong("leaved_ally") * 1000L);
         var1.setDissolvedAllyTime(var4.getLong("dissolved_ally") * 1000L);
         var1.setWhBonus(var4.getInt("warehouse"));
         var1.setDisbandEndTime(var4.getLong("disband_end") * 1000L);
         var1.setDisbandPenaltyTime(var4.getLong("disband_penalty") * 1000L);
         var1.YH = var4.getInt("custom_points");
      } catch (Exception var9) {
         _log.error("Error while restoring clan!", var9);
      } finally {
         DbUtils.closeQuietly(var2, var3, var4);
      }

      if (var1 == null) {
         _log.warn("Clan " + var0 + " does't exist");
         return null;
      }

      var1.restoreSkills();
      var1.dh();

      for (SubUnit var6 : var1.getAllSubUnits()) {
         var6.restore();
         var6.restoreSkills();
      }

      var1.di();
      var1.setCrestId(CrestCache.getInstance().getPledgeCrestId(var0));
      var1.setCrestLargeId(CrestCache.getInstance().getPledgeCrestLargeId(var0));
      return var1;
   }

   public void broadcastToOnlineMembers(IStaticPacket... var1) {
      for (UnitMember var3 : this) {
         if (var3.isOnline()) {
            var3.getPlayer().sendPacket(var1);
         }
      }
   }

   public void broadcastToOnlineMembers(L2GameServerPacket... var1) {
      for (UnitMember var3 : this) {
         if (var3.isOnline()) {
            var3.getPlayer().sendPacket(var1);
         }
      }
   }

   public void broadcastToOtherOnlineMembers(L2GameServerPacket var1, Player var2) {
      for (UnitMember var4 : this) {
         if (var4.isOnline() && var4.getPlayer() != var2) {
            var4.getPlayer().sendPacket(var1);
         }
      }
   }

   @Override
   public String toString() {
      return this.getName();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Clan var2 = (Clan)var1;
         return var2.getClanId() == this.getClanId();
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getAllyId());
   }

   public void setCrestId(int var1) {
      this.Yv = var1;
   }

   public int getCrestId() {
      return this.Yv;
   }

   public boolean hasCrest() {
      return this.Yv > 0;
   }

   public int getCrestLargeId() {
      return this.Yw;
   }

   public void setCrestLargeId(int var1) {
      this.Yw = var1;
   }

   public boolean hasCrestLarge() {
      return this.Yw > 0;
   }

   public long getAdenaCount() {
      return this.YB.getCountOfAdena();
   }

   public ClanWarehouse getWarehouse() {
      return this.YB;
   }

   public int isAtWar() {
      return this.YE != null && !this.YE.isEmpty() ? 1 : 0;
   }

   public int isAtWarOrUnderAttack() {
      return this.YE.isEmpty() && this.YF.isEmpty() ? 0 : 1;
   }

   public boolean isAtWarWith(int var1) {
      Clan var2 = ClanTable.getInstance().getClan(var1);
      return this.YE != null && !this.YE.isEmpty() && this.YE.contains(var2);
   }

   public boolean isUnderAttackFrom(int var1) {
      Clan var2 = ClanTable.getInstance().getClan(var1);
      return this.YF.contains(var2);
   }

   public void setEnemyClan(Clan var1) {
      this.YE.add(var1);
   }

   public void deleteEnemyClan(Clan var1) {
      this.YE.remove(var1);
   }

   public void setAttackerClan(Clan var1) {
      this.YF.add(var1);
   }

   public void deleteAttackerClan(Clan var1) {
      this.YF.remove(var1);
   }

   public List<Clan> getEnemyClans() {
      return this.YE;
   }

   public int getWarsCount() {
      return this.YE.size();
   }

   public List<Clan> getAttackerClans() {
      return Collections.unmodifiableList(this.YF);
   }

   public void broadcastClanStatus(boolean var1, boolean var2, boolean var3) {
      List var4 = var1 ? this.listAll() : null;
      PledgeShowInfoUpdate var5 = new PledgeShowInfoUpdate(this);

      for (UnitMember var7 : this) {
         if (var7.isOnline()) {
            if (var1) {
               var7.getPlayer().sendPacket(PledgeShowMemberListDeleteAll.STATIC);
               var7.getPlayer().sendPacket(var4);
            }

            var7.getPlayer().sendPacket(var5);
            if (var2) {
               var7.getPlayer().broadcastCharInfo();
            }

            if (var3) {
               var7.getPlayer().broadcastRelationChanged();
            }
         }
      }
   }

   public Alliance getAlliance() {
      return this.Yo == 0 ? null : ClanTable.getInstance().getAlliance(this.Yo);
   }

   public void setExpelledMemberTime(long var1) {
      this.Ys = var1;
   }

   public long getExpelledMemberTime() {
      return this.Ys;
   }

   public void setExpelledMember() {
      this.Ys = System.currentTimeMillis();
      this.updateClanInDB();
   }

   public void setLeavedAllyTime(long var1) {
      this.Yx = var1;
   }

   public long getLeavedAllyTime() {
      return this.Yx;
   }

   public void setLeavedAlly() {
      this.Yx = System.currentTimeMillis();
      this.updateClanInDB();
   }

   public void setDissolvedAllyTime(long var1) {
      this.Yy = var1;
   }

   public long getDissolvedAllyTime() {
      return this.Yy;
   }

   public void setDissolvedAlly() {
      this.Yy = System.currentTimeMillis();
      this.updateClanInDB();
   }

   public boolean canInvite() {
      return System.currentTimeMillis() - this.Ys >= Config.EXPELLED_MEMBER_PENALTY;
   }

   public boolean canJoinAlly() {
      return System.currentTimeMillis() - this.Yx >= Config.LEAVED_ALLY_PENALTY;
   }

   public boolean canCreateAlly() {
      return System.currentTimeMillis() - this.Yy >= Config.DISSOLVED_ALLY_PENALTY;
   }

   public boolean canDisband() {
      return System.currentTimeMillis() > this.Yz;
   }

   public int getRank() {
      Clan[] var1 = ClanTable.getInstance().getClans();
      Arrays.sort(var1, YI);
      byte var2 = 1;

      for (int var3 = 0; var3 < var1.length; var3++) {
         if (var3 == 100) {
            return 0;
         }

         Clan var4 = var1[var3];
         if (var4 == this) {
            return var2 + var3;
         }
      }

      return 0;
   }

   public int getReputationScore() {
      return this.YG;
   }

   private void P(int var1) {
      if (this.YG >= 0 && var1 < 0) {
         this.broadcastToOnlineMembers(Msg.SINCE_THE_CLAN_REPUTATION_SCORE_HAS_DROPPED_TO_0_OR_LOWER_YOUR_CLAN_SKILLS_WILL_BE_DE_ACTIVATED);

         for (UnitMember var5 : this) {
            if (var5.isOnline() && var5.getPlayer() != null) {
               this.disableSkills(var5.getPlayer());
            }
         }
      } else if (this.YG < 0 && var1 >= 0) {
         this.broadcastToOnlineMembers(Msg.THE_CLAN_SKILL_WILL_BE_ACTIVATED_BECAUSE_THE_CLANS_REPUTATION_SCORE_HAS_REACHED_TO_0_OR_HIGHER);

         for (UnitMember var3 : this) {
            if (var3.isOnline() && var3.getPlayer() != null) {
               this.enableSkills(var3.getPlayer());
            }
         }
      }

      if (this.YG != var1) {
         this.YG = var1;
         this.broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
      }

      this.updateClanInDB();
   }

   public int incReputation(int var1, boolean var2, String var3) {
      if (var1 > 0 && this._level < Config.MIN_CLAN_LEVEL_FOR_REPUTATION) {
         return 0;
      }

      if (var2 && Math.abs(var1) <= Config.RATE_CLAN_REP_SCORE_MAX_AFFECTED) {
         var1 = (int)Math.round(var1 * Config.RATE_CLAN_REP_SCORE);
      }

      this.P(this.YG + var1);
      Log.add(this.getName() + "|" + var1 + "|" + this.YG + "|" + var3, "clan_reputation");
      return var1;
   }

   private void restoreSkills() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT `skill_id`,`skill_level` FROM `clan_skills` WHERE `clan_id`=?");
         var2.setInt(1, this.getClanId());
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("skill_id");
            int var5 = var3.getInt("skill_level");
            Skill var6 = SkillTable.getInstance().getInfo(var4, var5);
            this._skills.put(var6.getId(), var6);
         }
      } catch (Exception var10) {
         _log.warn("Could not restore clan skills: " + var10);
         _log.error("", var10);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   public Collection<Skill> getSkills() {
      return this._skills.values();
   }

   public final Skill[] getAllSkills() {
      return this.YG < 0 ? Skill.EMPTY_ARRAY : this._skills.values().toArray(new Skill[this._skills.values().size()]);
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
                  var5 = var4.prepareStatement("UPDATE `clan_skills` SET `skill_level`=? WHERE `skill_id`=? AND `clan_id`=?");
                  var5.setInt(1, var1.getLevel());
                  var5.setInt(2, var3.getId());
                  var5.setInt(3, this.getClanId());
                  var5.execute();
               } else {
                  var5 = var4.prepareStatement("INSERT INTO `clan_skills` (`clan_id`,`skill_id`,`skill_level`) VALUES (?,?,?)");
                  var5.setInt(1, this.getClanId());
                  var5.setInt(2, var1.getId());
                  var5.setInt(3, var1.getLevel());
                  var5.execute();
               }
            } catch (Exception var11) {
               _log.warn("Error could not store char skills: " + var11);
               _log.error("", var11);
            } finally {
               DbUtils.closeQuietly(var4, var5);
            }
         }

         PledgeSkillListAdd var13 = new PledgeSkillListAdd(var1.getId(), var1.getLevel());
         PledgeSkillList var14 = new PledgeSkillList(this);

         for (UnitMember var7 : this) {
            if (var7.isOnline()) {
               Player var8 = var7.getPlayer();
               if (var8 != null) {
                  this.a(var8, var1);
                  var8.sendPacket(var13, var14, new SkillList(var8));
               }
            }
         }
      }

      return var3;
   }

   public void addSkillsQuietly(Player var1) {
      for (Skill var3 : this._skills.values()) {
         this.a(var1, var3);
      }

      SubUnit var4 = this.getSubUnit(var1.getPledgeType());
      if (var4 != null) {
         var4.addSkillsQuietly(var1);
      }
   }

   public void enableSkills(Player var1) {
      if (!var1.isOlyParticipant()) {
         for (Skill var3 : this._skills.values()) {
            if (var3.getMinPledgeClass() <= var1.getPledgeClass()) {
               var1.removeUnActiveSkill(var3);
            }
         }

         SubUnit var4 = this.getSubUnit(var1.getPledgeType());
         if (var4 != null) {
            var4.enableSkills(var1);
         }
      }
   }

   public void disableSkills(Player var1) {
      for (Skill var3 : this._skills.values()) {
         var1.addUnActiveSkill(var3);
      }

      SubUnit var4 = this.getSubUnit(var1.getPledgeType());
      if (var4 != null) {
         var4.disableSkills(var1);
      }
   }

   private void a(Player var1, Skill var2) {
      if (var2.getMinPledgeClass() <= var1.getPledgeClass()) {
         var1.addSkill(var2, false);
         if (this.YG < 0 || var1.isOlyParticipant()) {
            var1.addUnActiveSkill(var2);
         }
      }
   }

   public void removeSkill(int var1) {
      this._skills.remove(var1);
      PledgeSkillListAdd var2 = new PledgeSkillListAdd(var1, 0);

      for (UnitMember var4 : this) {
         Player var5 = var4.getPlayer();
         if (var5 != null && var5.isOnline()) {
            var5.removeSkillById(var1);
            var5.sendPacket(var2, new SkillList(var5));
         }
      }
   }

   public void broadcastSkillListToOnlineMembers() {
      for (UnitMember var2 : this) {
         Player var3 = var2.getPlayer();
         if (var3 != null && var3.isOnline()) {
            var3.sendPacket(new PledgeSkillList(this));
            var3.sendPacket(new SkillList(var3));
         }
      }
   }

   public static boolean isAcademy(int var0) {
      return var0 == -1;
   }

   public static boolean isRoyalGuard(int var0) {
      return var0 == 100 || var0 == 200;
   }

   public static boolean isOrderOfKnights(int var0) {
      return var0 == 1001 || var0 == 1002 || var0 == 2001 || var0 == 2002;
   }

   public int getAffiliationRank(int var1) {
      if (isAcademy(var1)) {
         return 9;
      } else if (isOrderOfKnights(var1)) {
         return 8;
      } else {
         return isRoyalGuard(var1) ? 7 : 6;
      }
   }

   public final SubUnit getSubUnit(int var1) {
      return (SubUnit)this._subUnits.get(var1);
   }

   public final void addSubUnit(SubUnit var1, boolean var2) {
      this._subUnits.put(var1.getType(), var1);
      if (var2) {
         this.broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(var1));
         Connection var3 = null;
         PreparedStatement var4 = null;

         try {
            var3 = DatabaseFactory.getInstance().getConnection();
            var4 = var3.prepareStatement("INSERT INTO `clan_subpledges` (clan_id,type,leader_id,name) VALUES (?,?,?,?)");
            var4.setInt(1, this.getClanId());
            var4.setInt(2, var1.getType());
            var4.setInt(3, var1.getLeaderObjectId());
            var4.setString(4, var1.getName());
            var4.execute();
         } catch (Exception var9) {
            _log.warn("Could not store clan Sub pledges: " + var9);
            _log.error("", var9);
         } finally {
            DbUtils.closeQuietly(var3, var4);
         }
      }
   }

   public int createSubPledge(Player var1, int var2, UnitMember var3, String var4) {
      int var5 = var2;
      var2 = this.getAvailablePledgeTypes(var2);
      if (var2 == -128) {
         if (var5 == -1) {
            var1.sendPacket(Msg.YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY);
         } else {
            var1.sendMessage(new CustomMessage("Clan.CantCreateSubUnit", var1));
         }

         return -128;
      } else {
         switch (var2) {
            case -1:
            default:
               break;
            case 100:
            case 200:
               if (this.getReputationScore() < 5000) {
                  var1.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
                  return -128;
               }

               this.incReputation(-5000, false, "SubunitCreate");
               break;
            case 1001:
            case 1002:
            case 2001:
            case 2002:
               if (this.getReputationScore() < 10000) {
                  var1.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
                  return -128;
               }

               this.incReputation(-10000, false, "SubunitCreate");
         }

         this.addSubUnit(new SubUnit(this, var2, var3, var4), true);
         return var2;
      }
   }

   public int getAvailablePledgeTypes(int var1) {
      if (var1 == 0) {
         return -128;
      }

      if (this._subUnits.get(var1) != null) {
         switch (var1) {
            case -1:
               return -128;
            case 100:
               var1 = this.getAvailablePledgeTypes(200);
               break;
            case 200:
               return -128;
            case 1001:
               var1 = this.getAvailablePledgeTypes(1002);
               break;
            case 1002:
               var1 = this.getAvailablePledgeTypes(2001);
               break;
            case 2001:
               var1 = this.getAvailablePledgeTypes(2002);
               break;
            case 2002:
               return -128;
         }
      }

      return var1;
   }

   private void dh() {
      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT * FROM `clan_subpledges` WHERE `clan_id`=?");
         var2.setInt(1, this.getClanId());
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("type");
            int var5 = var3.getInt("leader_id");
            String var6 = var3.getString("name");
            SubUnit var7 = new SubUnit(this, var4, var5, var6);
            this.addSubUnit(var7, false);
         }
      } catch (Exception var11) {
         _log.warn("Could not restore clan SubPledges: " + var11, var11);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   public int getSubPledgeLimit(int var1) {
      int var2;
      switch (this._level) {
         case 0:
            var2 = Config.LIMIT_CLAN_LEVEL0;
            break;
         case 1:
            var2 = Config.LIMIT_CLAN_LEVEL1;
            break;
         case 2:
            var2 = Config.LIMIT_CLAN_LEVEL2;
            break;
         case 3:
            var2 = Config.LIMIT_CLAN_LEVEL3;
            break;
         default:
            var2 = Config.LIMIT_CLAN_LEVEL_4_AND_HIGH;
      }

      switch (var1) {
         case -1:
            var2 = Config.LIMIT_CLAN_ACADEMY;
            break;
         case 100:
         case 200:
            var2 = Config.LIMIT_CLAN_HIGH_UNITS;
            break;
         case 1001:
         case 1002:
         case 2001:
         case 2002:
            var2 = Config.LIMIT_CLAN_LOW_UNITS;
      }

      return var2;
   }

   public int getUnitMembersSize(int var1) {
      return var1 != -128 && this._subUnits.containsKey(var1) ? this.getSubUnit(var1).size() : 0;
   }

   private void di() {
      if (this._privs == null) {
         this.InitializePrivs();
      }

      Connection var1 = null;
      PreparedStatement var2 = null;
      ResultSet var3 = null;

      try {
         var1 = DatabaseFactory.getInstance().getConnection();
         var2 = var1.prepareStatement("SELECT `privilleges`,`rank` FROM `clan_privs` WHERE `clan_id`=?");
         var2.setInt(1, this.getClanId());
         var3 = var2.executeQuery();

         while (var3.next()) {
            int var4 = var3.getInt("rank");
            int var5 = var3.getInt("privilleges");
            RankPrivs var6 = (RankPrivs)this._privs.get(var4);
            if (var6 != null) {
               var6.setPrivs(var5);
            } else {
               _log.warn("Invalid rank value (" + var4 + "), please check clan_privs table");
            }
         }
      } catch (Exception var10) {
         _log.warn("Could not restore clan privs by rank: " + var10);
         _log.error("", var10);
      } finally {
         DbUtils.closeQuietly(var1, var2, var3);
      }
   }

   public void InitializePrivs() {
      for (int var1 = 1; var1 <= 9; var1++) {
         this._privs.put(var1, new RankPrivs(var1, 0, 0));
      }
   }

   public void updatePrivsForRank(int var1) {
      for (UnitMember var3 : this) {
         if (var3.isOnline() && var3.getPlayer() != null && var3.getPlayer().getPowerGrade() == var1 && !var3.getPlayer().isClanLeader()) {
            var3.getPlayer().sendUserInfo();
         }
      }
   }

   public RankPrivs getRankPrivs(int var1) {
      if (var1 >= 1 && var1 <= 9) {
         if (this._privs.get(var1) == null) {
            _log.warn("Request of rank before init: " + var1);
            Thread.dumpStack();
            this.setRankPrivs(var1, 0);
         }

         return (RankPrivs)this._privs.get(var1);
      } else {
         _log.warn("Requested invalid rank value: " + var1);
         Thread.dumpStack();
         return null;
      }
   }

   public int countMembersByRank(int var1) {
      int var2 = 0;

      for (UnitMember var4 : this) {
         if (var4.getPowerGrade() == var1) {
            var2++;
         }
      }

      return var2;
   }

   public void setRankPrivs(int var1, int var2) {
      if (var1 >= 1 && var1 <= 9) {
         if (this._privs.get(var1) != null) {
            ((RankPrivs)this._privs.get(var1)).setPrivs(var2);
         } else {
            this._privs.put(var1, new RankPrivs(var1, this.countMembersByRank(var1), var2));
         }

         Connection var3 = null;
         PreparedStatement var4 = null;

         try {
            var3 = DatabaseFactory.getInstance().getConnection();
            var4 = var3.prepareStatement("REPLACE INTO `clan_privs`(`clan_id`,`rank`,`privilleges`) VALUES (?,?,?)");
            var4.setInt(1, this.getClanId());
            var4.setInt(2, var1);
            var4.setInt(3, var2);
            var4.execute();
         } catch (Exception var9) {
            _log.warn("Could not store clan privs for rank: " + var9);
            _log.error("", var9);
         } finally {
            DbUtils.closeQuietly(var3, var4);
         }
      } else {
         _log.warn("Requested set of invalid rank value: " + var1);
         Thread.dumpStack();
      }
   }

   public final RankPrivs[] getAllRankPrivs() {
      return this._privs == null ? new RankPrivs[0] : this._privs.values().toArray(new RankPrivs[this._privs.values().size()]);
   }

   public int getWhBonus() {
      return this.YC;
   }

   public void setWhBonus(int var1) {
      this.YC = var1;
   }

   public final Collection<SubUnit> getAllSubUnits() {
      return this._subUnits.values();
   }

   public List<L2GameServerPacket> listAll() {
      ArrayList var1 = new ArrayList(this._subUnits.size());

      for (SubUnit var3 : this.getAllSubUnits()) {
         var1.add(new PledgeShowMemberListAll(this, var3));
      }

      return var1;
   }

   public String getNotice() {
      return this.YD;
   }

   public void setNotice(String var1) {
      this.YD = var1;
   }

   public int getSkillLevel(int var1, int var2) {
      Skill var3 = (Skill)this._skills.get(var1);
      return var3 == null ? var2 : var3.getLevel();
   }

   public int getSkillLevel(int var1) {
      return this.getSkillLevel(var1, -1);
   }

   @Override
   public Iterator<UnitMember> iterator() {
      ArrayList var1 = new ArrayList(this._subUnits.size());

      for (SubUnit var3 : this._subUnits.values()) {
         var1.add(var3.getUnitMembers().iterator());
      }

      return new JoinedIterator<>(var1);
   }

   public boolean isPlacedForDisband() {
      return this.Yz != 0L;
   }

   public void placeForDisband() {
      this.Yz = System.currentTimeMillis() + Config.CLAN_DISBAND_TIME;
      this.updateClanInDB();
   }

   public void unPlaceDisband() {
      this.Yz = 0L;
      this.YA = System.currentTimeMillis() + Config.CLAN_DISBAND_PENALTY;
      this.updateClanInDB();
   }

   public long getDisbandEndTime() {
      return this.Yz;
   }

   public void setDisbandEndTime(long var1) {
      this.Yz = var1;
   }

   public long getDisbandPenaltyTime() {
      return this.YA;
   }

   public void setDisbandPenaltyTime(long var1) {
      this.YA = var1;
   }

   public int getCustomPoints() {
      return this.YH;
   }

   public void setCustomPoints(int var1) {
      this.YH = var1;
      this.updateClanInDB();
   }

   private static class ClanReputationComparator implements Comparator<Clan> {
      private ClanReputationComparator() {
      }

      public int compare(Clan var1, Clan var2) {
         return var1 != null && var2 != null ? var2.getReputationScore() - var1.getReputationScore() : 0;
      }
   }
}

package l2.gameserver.model;

import java.util.ArrayList;
import java.util.Collection;
import l2.commons.util.Rnd;
import l2.gameserver.data.xml.holder.SkillAcquireHolder;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.Earthquake;
import l2.gameserver.network.l2.s2c.ExAutoSoulShot;
import l2.gameserver.network.l2.s2c.ExRedSky;
import l2.gameserver.network.l2.s2c.ShortCutInit;
import l2.gameserver.network.l2.s2c.SkillList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Location;

public class CursedWeapon {
   private final String _name;
   private String ED;
   private final int _itemId;
   private final int EE;
   private final int _skillId;
   private int EF;
   private int EG;
   private int EH;
   private int EI;
   private int EJ;
   private int EK;
   private int EL = 0;
   private int EM = 0;
   private int EN = 0;
   private CursedWeapon.CursedWeaponState EO = CursedWeapon.CursedWeaponState.NONE;
   private Location cH = null;
   private long AI = 0L;
   private ItemInstance EP = null;
   private int EQ = 0;
   private Player _player = null;

   public CursedWeapon(int var1, int var2, String var3) {
      this._name = var3;
      this._itemId = var1;
      this._skillId = var2;
      this.EE = SkillTable.getInstance().getMaxLevel(this._skillId);
   }

   public void initWeapon() {
      this.bY();
      this.setState(CursedWeapon.CursedWeaponState.NONE);
      this.AI = 0L;
      this.EP = null;
      this.EL = 0;
   }

   public void create(NpcInstance var1, Player var2) {
      this.EP = ItemFunctions.createItem(this._itemId);
      if (this.EP != null) {
         this.bY();
         this.setState(CursedWeapon.CursedWeaponState.DROPPED);
         if (this.AI == 0L) {
            this.AI = System.currentTimeMillis() + this.getRndDuration() * 60000;
         }

         if (var1 != null) {
            this.EP.dropToTheGround(var1, Location.findPointToStay(var1, 100));
         } else {
            this.EP.dropToTheGround(var2, Location.findPointToStay(var2, 100));
         }

         this.cH = this.EP.getLoc();
         this.EP.setDropTime(0L);
         ExRedSky var3 = new ExRedSky(10);
         Earthquake var4 = new Earthquake(var2.getLoc(), 30, 12);

         for (Player var6 : GameObjectsStorage.getAllPlayersForIterate()) {
            var6.sendPacket(var3, var4);
         }
      }
   }

   public boolean dropIt(NpcInstance var1, Player var2, Player var3) {
      if (Rnd.chance(this.EG)) {
         return false;
      }

      Player var4 = this.getOnlineOwner();
      if (var4 == null) {
         if (var3 == null) {
            return false;
         }

         var4 = var3;
      }

      ItemInstance var5;
      if ((var5 = var4.getInventory().removeItemByItemId(this._itemId, 1L)) == null) {
         return false;
      }

      var4.setKarma(this.EM, true);
      var4.setPkKills(this.EN);
      var4.setCursedWeaponEquippedId(0);
      this.clearSkills(var4);
      var4.setTransformationName(null);
      var4.validateLocation(0);
      Skill var6 = SkillTable.getInstance().getInfo(this._skillId, var4.getSkillLevel(this._skillId));
      if (var6 != null) {
         for (Skill.AddedSkill var10 : var6.getAddedSkills()) {
            var4.removeSkillById(var10.id);
         }
      }

      var4.removeSkillById(this._skillId);
      var4.abortAttack(true, false);
      this.bY();
      this.setState(CursedWeapon.CursedWeaponState.DROPPED);
      var5.dropToTheGround(var4, Location.findPointToStay(var4, 100));
      this.cH = var5.getLoc();
      var5.setDropTime(0L);
      this.EP = var5;
      var4.sendPacket(new SystemMessage(298).addItemName(var5.getItemId()));
      var4.broadcastUserInfo(true);
      var4.broadcastPacket(new Earthquake(var4.getLoc(), 30, 12));
      return true;
   }

   public void clearSkills(Player var1) {
      if (!var1._transformationSkills.isEmpty()) {
         for (Skill var3 : var1._transformationSkills.values()) {
            if (!var3.isCommon() && !SkillAcquireHolder.getInstance().isSkillPossible(var1, var3) && !var3.isHeroic()) {
               var1.removeSkill(var3);
            }
         }

         var1._transformationSkills.clear();
         var1.sendPacket(new SkillList(var1), new ShortCutInit(var1));

         for (int var5 : var1.getAutoSoulShot()) {
            var1.sendPacket(new ExAutoSoulShot(var5, true));
         }
      }
   }

   public void giveSkill(Player var1) {
      for (Skill var3 : this.M(var1)) {
         var1.addSkill(var3, false);
         var1._transformationSkills.put(var3.getId(), var3);
      }

      var1.sendPacket(new SkillList(var1));
   }

   private Collection<Skill> M(Player var1) {
      int var2 = 1 + this.EL / this.EK;
      if (var2 > this.EE) {
         var2 = this.EE;
      }

      Skill var3 = SkillTable.getInstance().getInfo(this._skillId, var2);
      ArrayList var4 = new ArrayList();
      var4.add(var3);

      for (Skill.AddedSkill var8 : var3.getAddedSkills()) {
         var4.add(SkillTable.getInstance().getInfo(var8.id, var8.level));
      }

      return var4;
   }

   public boolean reActivate() {
      if (this.getTimeLeft() <= 0L) {
         if (this.getPlayerId() != 0) {
            this.setState(CursedWeapon.CursedWeaponState.ACTIVATED);
         }

         return false;
      } else {
         if (this.getPlayerId() == 0) {
            if (this.cH == null || (this.EP = ItemFunctions.createItem(this._itemId)) == null) {
               return false;
            }

            this.EP.dropMe(null, this.cH);
            this.EP.setDropTime(0L);
            this.setState(CursedWeapon.CursedWeaponState.DROPPED);
         } else {
            this.setState(CursedWeapon.CursedWeaponState.ACTIVATED);
         }

         return true;
      }
   }

   public void activate(Player var1, ItemInstance var2) {
      if (this.isDropped() || this.getPlayerId() != var1.getObjectId()) {
         this.setPlayerId(var1.getObjectId());
         this.setPlayerKarma(var1.getKarma());
         this.setPlayerPkKills(var1.getPkKills());
      }

      this.setPlayer(var1);
      this.setState(CursedWeapon.CursedWeaponState.ACTIVATED);
      var1.leaveParty();
      if (var1.isMounted()) {
         var1.setMount(0, 0, 0);
      }

      this.EP = var2;
      var1.getInventory().setPaperdollItem(8, null);
      var1.getInventory().setPaperdollItem(7, null);
      var1.getInventory().setPaperdollItem(7, this.EP);
      var1.sendPacket(new SystemMessage(49).addItemName(this.EP.getItemId()));
      var1.setCursedWeaponEquippedId(this._itemId);
      var1.setTransformationName(this.ED);
      var1.setKarma(9999999, true);
      var1.setPkKills(this.EL);
      if (this.AI == 0L) {
         this.AI = System.currentTimeMillis() + this.getRndDuration() * 60000;
      }

      if (!var1.isOlyParticipant() && var1.isHero() && var1.getBaseClassId() == var1.getActiveClassId()) {
         var1._transformationSkills.put(395, SkillTable.getInstance().getInfo(395, 1));
         var1._transformationSkills.put(396, SkillTable.getInstance().getInfo(396, 1));
         var1._transformationSkills.put(1374, SkillTable.getInstance().getInfo(1374, 1));
         var1._transformationSkills.put(1375, SkillTable.getInstance().getInfo(1375, 1));
         var1._transformationSkills.put(1376, SkillTable.getInstance().getInfo(1376, 1));
      }

      this.giveSkill(var1);
      var1.setCurrentHpMp(var1.getMaxHp(), var1.getMaxMp());
      var1.setCurrentCp(var1.getMaxCp());
      var1.broadcastUserInfo(true);
   }

   public void increaseKills() {
      Player var1 = this.getOnlineOwner();
      if (var1 != null) {
         this.EL++;
         var1.setPkKills(this.EL);
         var1.updateStats();
         if (this.EL % this.EK == 0 && this.EL <= this.EK * (this.EE - 1)) {
            this.giveSkill(var1);
         }

         this.AI = this.AI - this.EJ * 60000;
      }
   }

   public void setDisapearChance(int var1) {
      this.EG = var1;
   }

   public void giveSkillAndUpdateStats() {
      Player var1 = this.getOnlineOwner();
      if (var1 != null) {
         if (this.EL <= this.EK * (this.EE - 1)) {
            this.giveSkill(var1);
         }

         var1.updateStats();
      }
   }

   public void setDropRate(int var1) {
      this.EF = var1;
   }

   public void setDurationMin(int var1) {
      this.EH = var1;
   }

   public void setDurationMax(int var1) {
      this.EI = var1;
   }

   public void setDurationLost(int var1) {
      this.EJ = var1;
   }

   public void setStageKills(int var1) {
      this.EK = var1;
   }

   public void setTransformationName(String var1) {
      this.ED = var1;
   }

   public void setNbKills(int var1) {
      this.EL = var1;
   }

   public void setPlayerId(int var1) {
      this.EQ = var1;
   }

   public void setPlayerKarma(int var1) {
      this.EM = var1;
   }

   public void setPlayerPkKills(int var1) {
      this.EN = var1;
   }

   public void setState(CursedWeapon.CursedWeaponState var1) {
      this.EO = var1;
   }

   public void setEndTime(long var1) {
      this.AI = var1;
   }

   public void setPlayer(Player var1) {
      this._player = var1;
   }

   private void bY() {
      this._player = null;
      this.EQ = 0;
      this.EM = 0;
      this.EN = 0;
   }

   public void setItem(ItemInstance var1) {
      this.EP = var1;
   }

   public void setLoc(Location var1) {
      this.cH = var1;
   }

   public CursedWeapon.CursedWeaponState getState() {
      return this.EO;
   }

   public boolean isActivated() {
      return this.getState() == CursedWeapon.CursedWeaponState.ACTIVATED;
   }

   public boolean isDropped() {
      return this.getState() == CursedWeapon.CursedWeaponState.DROPPED;
   }

   public long getEndTime() {
      return this.AI;
   }

   public String getName() {
      return this._name;
   }

   public int getItemId() {
      return this._itemId;
   }

   public ItemInstance getItem() {
      return this.EP;
   }

   public int getSkillId() {
      return this._skillId;
   }

   public int getDropRate() {
      return this.EF;
   }

   public int getPlayerId() {
      return this.EQ;
   }

   public Player getPlayer() {
      return this._player;
   }

   public int getPlayerKarma() {
      return this.EM;
   }

   public int getPlayerPkKills() {
      return this.EN;
   }

   public int getNbKills() {
      return this.EL;
   }

   public int getStageKills() {
      return this.EK;
   }

   public Location getLoc() {
      return this.cH;
   }

   public int getRndDuration() {
      if (this.EH > this.EI) {
         this.EI = 2 * this.EH;
      }

      return Rnd.get(this.EH, this.EI);
   }

   public boolean isActive() {
      return this.isActivated() || this.isDropped();
   }

   public int getLevel() {
      return Math.min(1 + this.EL / this.EK, this.EE);
   }

   public long getTimeLeft() {
      return this.AI - System.currentTimeMillis();
   }

   public Location getWorldPosition() {
      if (this.isActivated()) {
         Player var1 = this.getOnlineOwner();
         if (var1 != null) {
            return var1.getLoc();
         }
      } else if (this.isDropped() && this.EP != null) {
         return this.EP.getLoc();
      }

      return null;
   }

   public Player getOnlineOwner() {
      Player var1 = this.getPlayer();
      return var1 != null && var1.isOnline() ? var1 : null;
   }

   public enum CursedWeaponState {
      NONE,
      ACTIVATED,
      DROPPED;
   }
}

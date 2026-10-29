package l2.gameserver.model.instances;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Future;
import l2.commons.dbutils.DbUtils;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.cache.Msg;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.idfactory.IdFactory;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.PetData;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.base.BaseStats;
import l2.gameserver.model.base.Experience;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PetInventory;
import l2.gameserver.model.items.attachment.FlagItemAttachment;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.stats.Stats;
import l2.gameserver.tables.PetDataTable;
import l2.gameserver.templates.item.WeaponTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PetInstance extends Summon {
   private static final Logger _log = LoggerFactory.getLogger(PetInstance.class);
   private static final int Vu = 5169;
   private final int Vv;
   private int Vw;
   protected PetData _data;
   private Future<?> Vx;
   protected PetInventory _inventory;
   private int _level;
   private boolean Vy;
   private int Vz;

   public static final PetInstance restore(ItemInstance var0, NpcTemplate var1, Player var2) {
      PetInstance var3 = null;
      Connection var4 = null;
      PreparedStatement var5 = null;
      ResultSet var6 = null;

      try {
         var4 = DatabaseFactory.getInstance().getConnection();
         var5 = var4.prepareStatement("SELECT `objId`, `name`, `level`, `curHp`, `curMp`, `exp`, `sp`, `fed` FROM `pets` WHERE `item_obj_id`=?");
         var5.setInt(1, var0.getObjectId());
         var6 = var5.executeQuery();
         if (!var6.next()) {
            if (!PetDataTable.isBabyPet(var1.getNpcId()) && !PetDataTable.isImprovedBabyPet(var1.getNpcId())) {
               var3 = new PetInstance(IdFactory.getInstance().getNextId(), var1, var2, var0);
            } else {
               var3 = new PetBabyInstance(IdFactory.getInstance().getNextId(), var1, var2, var0);
            }

            return var3;
         }

         if (!PetDataTable.isBabyPet(var1.getNpcId()) && !PetDataTable.isImprovedBabyPet(var1.getNpcId())) {
            var3 = new PetInstance(var6.getInt("objId"), var1, var2, var0, var6.getInt("level"), var6.getLong("exp"));
         } else {
            var3 = new PetBabyInstance(var6.getInt("objId"), var1, var2, var0, var6.getInt("level"), var6.getLong("exp"));
         }

         var3.setRespawned(true);
         String var7 = var6.getString("name");
         var3.setName(var7 != null && !var7.isEmpty() ? var7 : var1.name);
         var3.setCurrentHpMp(var6.getDouble("curHp"), var6.getInt("curMp"), true);
         var3.setCurrentCp(var3.getMaxCp());
         var3.setSp(var6.getInt("sp"));
         var3.setCurrentFed(var6.getInt("fed"));
      } catch (Exception var12) {
         _log.error("Could not restore Pet data from item: " + var0 + "!", var12);
         return null;
      } finally {
         DbUtils.closeQuietly(var4, var5, var6);
      }

      return var3;
   }

   public PetInstance(int var1, NpcTemplate var2, Player var3, ItemInstance var4) {
      this(var1, var2, var3, var4, 0, 0L);
   }

   public PetInstance(int var1, NpcTemplate var2, Player var3, ItemInstance var4, int var5, long var6) {
      super(var1, var2, var3);
      this.Vv = var4.getObjectId();
      this._exp = var6;
      this._level = var4.getEnchantLevel();
      if (this._level <= 0) {
         if (var2.npcId == 12564) {
            this._level = var3.getLevel();
         } else {
            this._level = var2.level;
         }

         this._exp = this.getExpForThisLevel();
      }

      int var8 = PetDataTable.getMinLevel(var2.npcId);
      if (this._level < var8) {
         this._level = var8;
      }

      if (this._exp < this.getExpForThisLevel()) {
         this._exp = this.getExpForThisLevel();
      }

      while (this._exp >= this.getExpForNextLevel() && this._level < Experience.getMaxLevel()) {
         this._level++;
      }

      while (this._exp < this.getExpForThisLevel() && this._level > var8) {
         this._level--;
      }

      this._data = PetDataTable.getInstance().getInfo(var2.npcId, this._level);
      this._inventory = new PetInventory(this);
   }

   @Override
   protected void onSpawn() {
      super.onSpawn();
      this.startFeed(false);
   }

   @Override
   protected void onDespawn() {
      super.onSpawn();
      this.cZ();
   }

   public boolean tryFeedItem(ItemInstance var1) {
      if (var1 == null) {
         return false;
      }

      boolean var2 = PetDataTable.isStrider(this.getNpcId()) && var1.getItemId() == 5169;
      if (this.getFoodId() != var1.getItemId() && !var2) {
         return false;
      }

      int var3 = Math.min(this.getMaxFed(), this.getCurrentFed() + Math.max(this.getMaxFed() * this.getAddFed() * (var2 ? 2 : 1) / 100, 1));
      if (this.getCurrentFed() != var3 && this.getInventory().destroyItem(var1, 1L)) {
         this.getPlayer().sendPacket(new SystemMessage(1527).addItemName(var1.getItemId()));
         this.setCurrentFed(var3);
         this.sendStatusUpdate();
      }

      return true;
   }

   public boolean tryFeed() {
      ItemInstance var1 = this.getInventory().getItemByItemId(this.getFoodId());
      if (var1 == null && PetDataTable.isStrider(this.getNpcId())) {
         var1 = this.getInventory().getItemByItemId(5169);
      }

      return this.tryFeedItem(var1);
   }

   @Override
   public void addExpAndSp(long var1, long var3) {
      Player var5 = this.getPlayer();
      this._exp += var1;
      this._sp = (int)(this._sp + var3);
      if (this._exp > this.getMaxExp()) {
         this._exp = this.getMaxExp();
      }

      if (var1 > 0L || var3 > 0L) {
         var5.sendPacket(new SystemMessage(1014).addNumber(var1));
      }

      int var6 = this._level;

      while (this._exp >= this.getExpForNextLevel() && this._level < Experience.getMaxLevel()) {
         this._level++;
      }

      while (this._exp < this.getExpForThisLevel() && this._level > this.getMinLevel()) {
         this._level--;
      }

      if (var6 < this._level) {
         var5.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2PetInstance.PetLevelUp", var5).addNumber(this._level));
         this.broadcastPacket(new SocialAction(this.getObjectId(), 15));
         this.setCurrentHpMp(this.getMaxHp(), this.getMaxMp());
      }

      if (var6 != this._level) {
         this.updateControlItem();
         this.da();
      }

      if (var1 > 0L || var3 > 0L) {
         this.sendStatusUpdate();
      }
   }

   @Override
   public boolean consumeItem(int var1, long var2) {
      return this.getPlayer().getInventory().destroyItemByItemId(var1, var2);
   }

   private void cX() {
      if (!this.isInZoneBattle()) {
         int var1 = this.getLevel();
         double var2 = -0.07 * var1 + 6.5;
         this.Vz = (int)Math.round((this.getExpForNextLevel() - this.getExpForThisLevel()) * var2 / 100.0);
         this.addExpAndSp(-this.Vz, 0L);
      }
   }

   private void cY() {
      Player var1 = this.getPlayer();
      if (this.getControlItemObjId() != 0) {
         if (var1.getInventory().destroyItemByObjectId(this.getControlItemObjId(), 1L)) {
            Connection var2 = null;
            PreparedStatement var3 = null;

            try {
               var2 = DatabaseFactory.getInstance().getConnection();
               var3 = var2.prepareStatement("DELETE FROM `pets` WHERE `item_obj_id`=?");
               var3.setInt(1, this.getControlItemObjId());
               var3.execute();
            } catch (Exception var8) {
               _log.warn("could not delete pet:" + var8);
            } finally {
               DbUtils.closeQuietly(var2, var3);
            }
         }
      }
   }

   @Override
   protected void onDeath(Creature var1) {
      super.onDeath(var1);
      Player var2 = this.getPlayer();
      var2.sendPacket(Msg.THE_PET_HAS_BEEN_KILLED_IF_YOU_DO_NOT_RESURRECT_IT_WITHIN_24_HOURS_THE_PETS_BODY_WILL_DISAPPEAR_ALONG_WITH_ALL_THE_PETS_ITEMS);
      this.startDecay(86400000L);
      this.cZ();
      this.cX();
   }

   @Override
   public void doPickupItem(GameObject var1) {
      Player var2 = this.getPlayer();
      this.stopMove();
      if (var1.isItem()) {
         ItemInstance var3 = (ItemInstance)var1;
         if (var3.isCursed()) {
            var2.sendPacket(new SystemMessage(56).addItemName(var3.getItemId()));
         } else {
            synchronized (var3) {
               if (!var3.isVisible()) {
                  return;
               }

               if (var3.isHerb()) {
                  Skill[] var12 = var3.getTemplate().getAttachedSkills();
                  if (var12.length > 0) {
                     for (Skill var9 : var12) {
                        this.altUseSkill(var9, this);
                     }
                  }

                  var3.deleteMe();
                  return;
               }

               if (!this.getInventory().validateWeight(var3)) {
                  this.sendPacket(Msg.EXCEEDED_PET_INVENTORYS_WEIGHT_LIMIT);
                  return;
               }

               if (!this.getInventory().validateCapacity(var3)) {
                  this.sendPacket(Msg.DUE_TO_THE_VOLUME_LIMIT_OF_THE_PETS_INVENTORY_NO_MORE_ITEMS_CAN_BE_PLACED_THERE);
                  return;
               }

               if (!var3.getTemplate().getHandler().pickupItem(this, var3)) {
                  return;
               }

               FlagItemAttachment var5 = var3.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment)var3.getAttachment() : null;
               if (var5 != null) {
                  return;
               }

               var3.pickupMe();
            }

            if (var2.getParty() != null && var2.getParty().getLootDistribution() != 0) {
               var2.getParty().distributeItem(var2, var3, null);
            } else {
               Log.LogItem(var2, Log.ItemLog.PetPickup, var3);
               this.getInventory().addItem(var3);
               this.sendChanges();
               this.broadcastPickUpMsg(var3);
               var3.pickupMe();
            }

            this.broadcastPickUpMsg(var3);
         }
      }
   }

   public void doRevive(double var1) {
      this.restoreExp(var1);
      this.doRevive();
   }

   @Override
   public void doRevive() {
      this.stopDecay();
      super.doRevive();
      this.startFeed(false);
      this.setRunning();
   }

   @Override
   public int getAccuracy() {
      return (int)this.calcStat(Stats.ACCURACY_COMBAT, this._data.getAccuracy(), null, null);
   }

   @Override
   public ItemInstance getActiveWeaponInstance() {
      return null;
   }

   @Override
   public WeaponTemplate getActiveWeaponItem() {
      return null;
   }

   public ItemInstance getControlItem() {
      Player var1 = this.getPlayer();
      if (var1 == null) {
         return null;
      }

      int var2 = this.getControlItemObjId();
      return var2 == 0 ? null : var1.getInventory().getItemByObjectId(var2);
   }

   @Override
   public int getControlItemObjId() {
      return this.Vv;
   }

   @Override
   public int getCriticalHit(Creature var1, Skill var2) {
      return (int)this.calcStat(Stats.CRITICAL_BASE, this._data.getCritical(), var1, var2);
   }

   @Override
   public int getCurrentFed() {
      return this.Vw;
   }

   @Override
   public int getEvasionRate(Creature var1) {
      return (int)this.calcStat(Stats.EVASION_RATE, this._data.getEvasion(), var1, null);
   }

   @Override
   public long getExpForNextLevel() {
      return PetDataTable.getInstance().getInfo(this.getNpcId(), this._level + 1).getExp();
   }

   @Override
   public long getExpForThisLevel() {
      return PetDataTable.getInstance().getInfo(this.getNpcId(), this._level).getExp();
   }

   public int getFoodId() {
      return this._data.getFoodId();
   }

   public int getAddFed() {
      return this._data.getAddFed();
   }

   @Override
   public PetInventory getInventory() {
      return this._inventory;
   }

   @Override
   public long getWearedMask() {
      return this._inventory.getWearedMask();
   }

   @Override
   public final int getLevel() {
      return this._level;
   }

   public void setLevel(int var1) {
      this._level = var1;
   }

   @Override
   public double getLevelMod() {
      return (89.0 + this.getLevel()) / 100.0;
   }

   public int getMinLevel() {
      return this._data.getMinLevel();
   }

   public long getMaxExp() {
      return PetDataTable.getInstance().getInfo(this.getNpcId(), Experience.getMaxLevel() + 1).getExp();
   }

   @Override
   public int getMaxFed() {
      return this._data.getFeedMax();
   }

   @Override
   public int getMaxLoad() {
      return (int)this.calcStat(Stats.MAX_LOAD, this._data.getMaxLoad(), null, null);
   }

   @Override
   public int getInventoryLimit() {
      return Config.ALT_PET_INVENTORY_LIMIT;
   }

   @Override
   public int getMaxHp() {
      return (int)this.calcStat(Stats.MAX_HP, this._data.getHP(), null, null);
   }

   @Override
   public int getMaxMp() {
      return (int)this.calcStat(Stats.MAX_MP, this._data.getMP(), null, null);
   }

   @Override
   public int getPAtk(Creature var1) {
      double var2 = BaseStats.STR.calcBonus(this) * this.getLevelMod();
      return (int)this.calcStat(Stats.POWER_ATTACK, this._data.getPAtk() / var2, var1, null);
   }

   @Override
   public int getPDef(Creature var1) {
      double var2 = this.getLevelMod();
      return (int)this.calcStat(Stats.POWER_DEFENCE, this._data.getPDef() / var2, var1, null);
   }

   @Override
   public int getMAtk(Creature var1, Skill var2) {
      double var3 = BaseStats.INT.calcBonus(this);
      double var5 = this.getLevelMod();
      double var7 = var5 * var5 * var3 * var3;
      return (int)this.calcStat(Stats.MAGIC_ATTACK, this._data.getMAtk() / var7, var1, var2);
   }

   @Override
   public int getMDef(Creature var1, Skill var2) {
      double var3 = BaseStats.MEN.calcBonus(this) * this.getLevelMod();
      return (int)this.calcStat(Stats.MAGIC_DEFENCE, this._data.getMDef() / var3, var1, var2);
   }

   @Override
   public int getPAtkSpd() {
      return (int)this.calcStat(Stats.POWER_ATTACK_SPEED, this.calcStat(Stats.ATK_BASE, this._data.getAtkSpeed(), null, null), null, null);
   }

   @Override
   public int getMAtkSpd() {
      return (int)this.calcStat(Stats.MAGIC_ATTACK_SPEED, this._data.getCastSpeed(), null, null);
   }

   @Override
   public int getRunSpeed() {
      return this.getSpeed(this._data.getSpeed());
   }

   @Override
   public int getSoulshotConsumeCount() {
      return PetDataTable.getSoulshots(this.getNpcId());
   }

   @Override
   public int getSpiritshotConsumeCount() {
      return PetDataTable.getSpiritshots(this.getNpcId());
   }

   @Override
   public ItemInstance getSecondaryWeaponInstance() {
      return null;
   }

   @Override
   public WeaponTemplate getSecondaryWeaponItem() {
      return null;
   }

   public int getSkillLevel(int var1) {
      if (this._skills != null && this._skills.get(var1) != null) {
         int var2 = this.getLevel();
         return var2 > 70 ? 7 + (var2 - 70) / 5 : var2 / 10;
      } else {
         return -1;
      }
   }

   @Override
   public int getSummonType() {
      return 2;
   }

   @Override
   public NpcTemplate getTemplate() {
      return (NpcTemplate)this._template;
   }

   @Override
   public boolean isMountable() {
      return this._data.isMountable();
   }

   public boolean isRespawned() {
      return this.Vy;
   }

   public void restoreExp(double var1) {
      if (this.Vz != 0) {
         this.addExpAndSp((long)(this.Vz * var1 / 100.0), 0L);
         this.Vz = 0;
      }
   }

   public void setCurrentFed(int var1) {
      this.Vw = Math.min(this.getMaxFed(), Math.max(0, var1));
   }

   public void setRespawned(boolean var1) {
      this.Vy = var1;
   }

   @Override
   public void setSp(int var1) {
      this._sp = var1;
   }

   public void startFeed(boolean var1) {
      boolean var2 = this.Vx == null;
      this.cZ();
      if (!this.isDead()) {
         int var3 = Math.max(var2 ? 15000 : 1000, 60000 / (var1 ? this._data.getFeedBattle() : this._data.getFeedNormal()));
         this.Vx = ThreadPoolManager.getInstance().schedule(new PetInstance.FeedTask(), var3);
      }
   }

   private void cZ() {
      if (this.Vx != null) {
         this.Vx.cancel(false);
         this.Vx = null;
      }
   }

   public void store() {
      if (this.getControlItemObjId() != 0 && this._exp != 0L) {
         Connection var1 = null;
         PreparedStatement var2 = null;

         try {
            var1 = DatabaseFactory.getInstance().getConnection();
            String var3;
            if (!this.isRespawned()) {
               var3 = "INSERT INTO `pets` (`name`,`level`,`curHp`,`curMp`,`exp`,`sp`,`fed`,`objId`,`item_obj_id`) VALUES (?,?,?,?,?,?,?,?,?)";
            } else {
               var3 = "UPDATE `pets` SET `name`=?,`level`=?,`curHp`=?,`curMp`=?,`exp`=?,`sp`=?,`fed`=?,`objId`=? WHERE `item_obj_id` = ?";
            }

            var2 = var1.prepareStatement(var3);
            var2.setString(1, this.getName().equalsIgnoreCase(this.getTemplate().name) ? "" : this.getName());
            var2.setInt(2, this._level);
            var2.setDouble(3, this.getCurrentHp());
            var2.setDouble(4, this.getCurrentMp());
            var2.setLong(5, this._exp);
            var2.setLong(6, this._sp);
            var2.setInt(7, this.Vw);
            var2.setInt(8, this.getObjectId());
            var2.setInt(9, this.Vv);
            var2.executeUpdate();
         } catch (Exception var7) {
            _log.error("Could not store pet data!", var7);
         } finally {
            DbUtils.closeQuietly(var1, var2);
         }

         this.Vy = true;
      }
   }

   @Override
   protected void onDecay() {
      this.getInventory().store();
      this.cY();
      super.onDecay();
   }

   @Override
   public void unSummon() {
      this.cZ();
      this.getInventory().store();
      this.store();
      super.unSummon();
   }

   public void updateControlItem() {
      ItemInstance var1 = this.getControlItem();
      if (var1 != null) {
         var1.setEnchantLevel(this._level);
         var1.setDamaged(this.isDefaultName() ? 0 : 1);
         Player var2 = this.getPlayer();
         var2.sendPacket(new InventoryUpdate().addModifiedItem(var1));
      }
   }

   private void da() {
      this._data = PetDataTable.getInstance().getInfo(this.getTemplate().npcId, this._level);
   }

   @Override
   public double getExpPenalty() {
      return PetDataTable.getExpPenalty(this.getTemplate().npcId);
   }

   @Override
   public void displayGiveDamageMessage(Creature var1, int var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      Player var7 = this.getPlayer();
      if (var3) {
         var7.sendPacket(SystemMsg.SUMMONED_MONSTERS_CRITICAL_HIT);
      }

      if (var4) {
         var7.sendPacket(new SystemMessage(43));
      } else {
         var7.sendPacket(new SystemMessage(1015).addNumber(var2));
      }
   }

   @Override
   public void displayReceiveDamageMessage(Creature var1, int var2) {
      Player var3 = this.getPlayer();
      if (!this.isDead()) {
         SystemMessage var4 = new SystemMessage(1016);
         if (var1.isNpc()) {
            var4.addNpcName(((NpcInstance)var1).getTemplate().npcId);
         } else {
            var4.addString(var1.getName());
         }

         var4.addNumber((long)var2);
         var3.sendPacket(var4);
      }
   }

   @Override
   public int getFormId() {
      switch (this.getNpcId()) {
         case 16025:
         case 16037:
         case 16041:
         case 16042:
            if (this.getLevel() >= 70) {
               return 3;
            } else if (this.getLevel() >= 65) {
               return 2;
            } else if (this.getLevel() >= 60) {
               return 1;
            }
         default:
            return 0;
      }
   }

   @Override
   public boolean isPet() {
      return true;
   }

   public boolean isDefaultName() {
      return StringUtils.isEmpty(this._name) || this.getName().equalsIgnoreCase(this.getTemplate().name);
   }

   @Override
   public int getEffectIdentifier() {
      return 0;
   }

   class FeedTask extends RunnableImpl {
      @Override
      public void runImpl() throws Exception {
         Player var1 = PetInstance.this.getPlayer();

         while (PetInstance.this.getCurrentFed() <= 0.55 * PetInstance.this.getMaxFed() && PetInstance.this.tryFeed()) {
         }

         if (PetInstance.this.getCurrentFed() <= 0.1 * PetInstance.this.getMaxFed()) {
            var1.sendMessage(new CustomMessage("l2p.gameserver.model.instances.L2PetInstance.UnSummonHungryPet", var1));
            PetInstance.this.unSummon();
         } else {
            PetInstance.this.setCurrentFed(PetInstance.this.getCurrentFed() - 5);
            PetInstance.this.sendStatusUpdate();
            PetInstance.this.startFeed(PetInstance.this.isInCombat());
         }
      }
   }
}

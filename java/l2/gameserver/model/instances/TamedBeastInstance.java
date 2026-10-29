package l2.gameserver.model.instances;

import java.util.concurrent.Future;
import l2.commons.lang.reference.HardReference;
import l2.commons.lang.reference.HardReferences;
import l2.commons.threading.RunnableImpl;
import l2.commons.util.Rnd;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.World;
import l2.gameserver.model.base.SpecialEffectState;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.NpcInfo;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.npc.NpcTemplate;

public final class TamedBeastInstance extends FeedableBeastInstance {
   private static final int Wg = 2000;
   private static final int Wh = 300;
   private static final int MAX_DURATION = 1200000;
   private static final int Wi = 60000;
   private static final int Wj = 20000;
   private static final int Wk = 30000;
   private HardReference<Player> e = HardReferences.emptyRef();
   private int Wl;
   private int Wm = 1200000;
   private Future<?> Wn = null;
   private Future<?> Wo = null;
   private Skill[] _skills = new Skill[0];
   private static final int UW = 5200;
   private static final int UU = 5195;
   private static final Skill[][] Wp = new Skill[6][];

   public TamedBeastInstance(int var1, NpcTemplate var2) {
      super(var1, var2);
      this._hasRandomWalk = false;
      this._hasChatWindow = false;
      this._hasRandomAnimation = false;
      this.setUndying(SpecialEffectState.FALSE);
   }

   @Override
   public boolean isAutoAttackable(Creature var1) {
      return false;
   }

   private void dd() {
      this.Wm += 20000;
      if (this.Wm > 1200000) {
         this.Wm = 1200000;
      }
   }

   public int getRemainingTime() {
      return this.Wm;
   }

   public void setRemainingTime(int var1) {
      this.Wm = var1;
   }

   public int getFoodType() {
      return this.Wl;
   }

   public void setTameType(Player var1) {
      switch (this.getNpcId()) {
         case 16013:
         case 16014:
         case 16015:
         case 16016:
         case 16017:
         case 16018:
         default:
            Skill[] var2 = Wp[Rnd.get(Wp.length)];
            this._skills = (Skill[])var2.clone();
      }
   }

   public void buffOwner() {
      if (!this.isInRange(this.getPlayer(), 300L)) {
         this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this.getPlayer());
      } else {
         int var1 = 0;

         for (Skill var5 : this._skills) {
            if (this.getPlayer().getEffectList().getEffectsCountForSkill(var5.getId()) <= 0) {
               ThreadPoolManager.getInstance().schedule(new TamedBeastInstance.Buff(this, this.getPlayer(), var5), var1);
               var1 = var1 + var5.getHitTime() + 500;
            }
         }
      }
   }

   public void setFoodType(int var1) {
      if (var1 > 0) {
         this.Wl = var1;
         if (this.Wn != null) {
            this.Wn.cancel(false);
         }

         this.Wn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new TamedBeastInstance.CheckDuration(this), 60000L, 60000L);
      }
   }

   @Override
   protected void onDeath(Creature var1) {
      super.onDeath(var1);
      if (this.Wn != null) {
         this.Wn.cancel(false);
         this.Wn = null;
      }

      if (this.Wo != null) {
         this.Wo.cancel(false);
         this.Wo = null;
      }

      Player var2 = this.getPlayer();
      if (var2 != null && var2.getTrainedBeast() == this) {
         var2.setTrainedBeast(null);
      }

      this.Wl = 0;
      this.Wm = 0;
   }

   @Override
   public Player getPlayer() {
      return this.e.get();
   }

   public void setOwner(Player var1) {
      this.e = var1 == null ? HardReferences.emptyRef() : var1.getRef();
      if (var1 != null) {
         if (var1.getTrainedBeast() != null) {
            var1.getTrainedBeast().doDespawn();
         }

         var1.setTrainedBeast(this);

         for (Player var3 : World.getAroundPlayers(this)) {
            var3.sendPacket(new NpcInfo(this, var3));
         }

         this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, var1);
         this.Wo = ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
               TamedBeastInstance.this.buffOwner();
            }
         }, 30000L, 30000L);
      } else {
         this.doDespawn();
      }
   }

   public void despawnWithDelay(int var1) {
      ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
         @Override
         public void runImpl() throws Exception {
            TamedBeastInstance.this.doDespawn();
         }
      }, var1);
   }

   public void doDespawn() {
      this.stopMove();
      if (this.Wn != null) {
         this.Wn.cancel(false);
         this.Wn = null;
      }

      if (this.Wo != null) {
         this.Wo.cancel(false);
         this.Wo = null;
      }

      Player var1 = this.getPlayer();
      if (var1 != null && var1.getTrainedBeast() == this) {
         var1.setTrainedBeast(null);
      }

      this.setTarget(null);
      this.Wl = 0;
      this.Wm = 0;
      this.onDecay();
   }

   static {
      Wp[0] = new Skill[]{
         SkillTable.getInstance().getInfo(5186, 1),
         SkillTable.getInstance().getInfo(5188, 1),
         SkillTable.getInstance().getInfo(5189, 1),
         SkillTable.getInstance().getInfo(5187, 1),
         SkillTable.getInstance().getInfo(5191, 1),
         SkillTable.getInstance().getInfo(5195, 1)
      };
      Wp[1] = new Skill[]{
         SkillTable.getInstance().getInfo(5192, 1),
         SkillTable.getInstance().getInfo(5193, 1),
         SkillTable.getInstance().getInfo(5201, 1),
         SkillTable.getInstance().getInfo(5194, 1),
         SkillTable.getInstance().getInfo(5190, 1),
         SkillTable.getInstance().getInfo(5200, 1)
      };
      Wp[2] = new Skill[]{
         SkillTable.getInstance().getInfo(5186, 1),
         SkillTable.getInstance().getInfo(5187, 1),
         SkillTable.getInstance().getInfo(5188, 1),
         SkillTable.getInstance().getInfo(5189, 1),
         SkillTable.getInstance().getInfo(5191, 1),
         SkillTable.getInstance().getInfo(5195, 1)
      };
      Wp[3] = new Skill[]{
         SkillTable.getInstance().getInfo(5192, 1),
         SkillTable.getInstance().getInfo(5193, 1),
         SkillTable.getInstance().getInfo(5201, 1),
         SkillTable.getInstance().getInfo(5194, 1),
         SkillTable.getInstance().getInfo(5190, 1),
         SkillTable.getInstance().getInfo(5200, 1)
      };
      Wp[4] = new Skill[]{
         SkillTable.getInstance().getInfo(5186, 1),
         SkillTable.getInstance().getInfo(5187, 1),
         SkillTable.getInstance().getInfo(5188, 1),
         SkillTable.getInstance().getInfo(5189, 1),
         SkillTable.getInstance().getInfo(5191, 1),
         SkillTable.getInstance().getInfo(5195, 1)
      };
      Wp[5] = new Skill[]{
         SkillTable.getInstance().getInfo(5192, 1),
         SkillTable.getInstance().getInfo(5193, 1),
         SkillTable.getInstance().getInfo(5201, 1),
         SkillTable.getInstance().getInfo(5194, 1),
         SkillTable.getInstance().getInfo(5190, 1),
         SkillTable.getInstance().getInfo(5200, 1)
      };
   }

   public static class Buff extends RunnableImpl {
      private NpcInstance TK;
      private Player LF;
      private Skill _skill;

      public Buff(NpcInstance var1, Player var2, Skill var3) {
         this.TK = var1;
         this.LF = var2;
         this._skill = var3;
      }

      @Override
      public void runImpl() throws Exception {
         if (this.TK != null) {
            this.TK.doCast(this._skill, this.LF, true);
         }
      }
   }

   private static class CheckDuration extends RunnableImpl {
      private TamedBeastInstance Kb;

      CheckDuration(TamedBeastInstance var1) {
         this.Kb = var1;
      }

      @Override
      public void runImpl() throws Exception {
         Player var1 = this.Kb.getPlayer();
         if (var1 != null && var1.isOnline()) {
            if (this.Kb.getDistance(var1) > 2000.0) {
               this.Kb.doDespawn();
            } else {
               int var2 = this.Kb.getFoodType();
               this.Kb.setRemainingTime(this.Kb.getRemainingTime() - 60000);
               ItemInstance var3 = null;
               int var4 = this.Kb.getItemIdBySkillId(var2);
               if (var4 > 0) {
                  var3 = var1.getInventory().getItemByItemId(var4);
               }

               if (var3 != null && var3.getCount() >= 1L) {
                  this.Kb.dd();
                  var1.getInventory().destroyItem(var3, 1L);
               } else if (this.Kb.getRemainingTime() < 900000) {
                  this.Kb.setRemainingTime(-1);
               }

               if (this.Kb.getRemainingTime() <= 0) {
                  this.Kb.doDespawn();
               }
            }
         } else {
            this.Kb.doDespawn();
         }
      }
   }
}

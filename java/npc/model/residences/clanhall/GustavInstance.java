package npc.model.residences.clanhall;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Playable;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.AggroList.HateInfo;
import l2.gameserver.model.GameObjectTasks.NotifyAITask;
import l2.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SpawnExObject;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.utils.Location;
import npc.model.residences.SiegeGuardInstance;

public class GustavInstance extends SiegeGuardInstance implements _34SiegeGuard {
   private AtomicBoolean ayI = new AtomicBoolean();
   private Future<?> ayo;

   public GustavInstance(int var1, NpcTemplate var2) {
      super(var1, var2);
   }

   @Override
   public void onSpawn() {
      super.onSpawn();
      this.ayI.set(false);
      Functions.npcShoutCustomMessage(
         this,
         "clanhall.siege.GustavInstance.PREPARE_TO_DIE_FOREIGN_INVADERS_I_AM_GUSTAV_THE_ETERNAL_RULER_OF_THIS_FORTRESS_AND_I_HAVE_TAKEN_UP_MY_SWORD_TO_REPEL_THEE"
      );
   }

   @Override
   public void onDeath(Creature var1) {
      if (!this.ayI.get()) {
         this.ayI.set(true);
         this.setCurrentHp(1.0, true);

         for (Creature var3 : World.getAroundCharacters(this)) {
            ThreadPoolManager.getInstance().execute(new NotifyAITask(var3, CtrlEvent.EVT_FORGET_OBJECT, this, null));
         }

         ClanHallSiegeEvent var6 = this.getEvent(ClanHallSiegeEvent.class);
         if (var6 == null) {
            return;
         }

         SpawnExObject var8 = var6.getFirstObject("boss");

         for (int var4 = 0; var4 < 3; var4++) {
            final NpcInstance var5 = var8.getSpawns().get(var4).getFirstSpawned();
            Functions.npcSay(var5, ((_34SiegeGuard)var5).teleChatSay());
            var5.broadcastPacket(new MagicSkillUse(var5, var5, 4235, 1, 10000, 0L));
            this.ayo = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
               @Override
               public void runImpl() throws Exception {
                  Location var1x = Location.findAroundPosition(177134, -18807, -2256, 50, 100, var5.getGeoIndex());
                  var5.teleToLocation(var1x);
                  if (var5 == GustavInstance.this) {
                     var5.reduceCurrentHp(var5.getCurrentHp(), var5, null, false, false, false, false, false, false, false);
                  }
               }
            }, 10000L);
         }
      } else {
         if (this.ayo != null) {
            this.ayo.cancel(false);
            this.ayo = null;
         }

         SiegeEvent var7 = this.getEvent(SiegeEvent.class);
         if (var7 == null) {
            return;
         }

         var7.processStep(this.getMostDamagedClan());
         super.onDeath(var1);
      }
   }

   public Clan getMostDamagedClan() {
      ClanHallSiegeEvent var1 = this.getEvent(ClanHallSiegeEvent.class);
      Player var2 = null;
      HashMap var3 = new HashMap();

      for (HateInfo var5 : this.getAggroList().getPlayableMap().values()) {
         Playable var6 = (Playable)var5.attacker;
         int var7 = var5.damage;
         if (var6.isPet() || var6.isSummon()) {
            var2 = var6.getPlayer();
         } else if (var6.isPlayer()) {
            var2 = (Player)var6;
         }

         if (var2 != null && var1.getSiegeClan("attackers", var2.getClan()) != null) {
            if (!var3.containsKey(var2)) {
               var3.put(var2, var7);
            } else {
               int var8 = (Integer)var3.get(var2) + var7;
               var3.put(var2, var8);
            }
         }
      }

      int var10 = 0;
      Player var11 = null;

      for (Object __raw_var13_120 : (Iterable<?>)(Object)(var3.entrySet())) {
         Entry var13 = (Entry)__raw_var13_120;
         int var14 = (Integer)var13.getValue();
         Player var9 = (Player)var13.getKey();
         if (var14 > var10) {
            var10 = var14;
            var11 = var9;
         }
      }

      return var11 == null ? null : var11.getClan();
   }

   @Override
   public String teleChatSay() {
      return "This is unbelievable! Have I really been defeated? I shall return and take your head!";
   }

   @Override
   public boolean isEffectImmune() {
      return true;
   }
}

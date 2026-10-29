package quests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.data.xml.holder.SoulCrystalHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.SoulCrystal;
import l2.gameserver.templates.npc.AbsorbInfo;
import l2.gameserver.templates.npc.NpcTemplate;

public class _350_EnhanceYourWeapon extends Quest implements ScriptFile {
   private static final int bpX = 30115;
   private static final int bpY = 30194;
   private static final int bpZ = 30856;
   private static final int bqa = 4629;
   private static final int bqb = 4640;
   private static final int bqc = 4651;
   private static final int[] bqd = new int[]{
      4651,
      4652,
      4653,
      4654,
      4655,
      4656,
      4657,
      4658,
      4659,
      4660,
      4661,
      4664,
      4629,
      4630,
      4631,
      4632,
      4633,
      4634,
      4635,
      4636,
      4637,
      4638,
      4639,
      4662,
      4640,
      4641,
      4642,
      4643,
      4644,
      4645,
      4646,
      4647,
      4648,
      4649,
      4650,
      4663
   };

   public _350_EnhanceYourWeapon() {
      super(0);
      this.addStartNpc(30115, 30194, 30856);

      for (NpcTemplate var4 : NpcHolder.getInstance().getAll()) {
         if (var4 != null && !var4.getAbsorbInfo().isEmpty()) {
            this.addKillId(var4.npcId);
         }
      }
   }

   @Override
   public void onLoad() {
   }

   @Override
   public void onReload() {
   }

   @Override
   public void onShutdown() {
   }

   @Override
   public String onEvent(String var1, QuestState var2, NpcInstance var3) {
      int var4 = var3.getNpcId();
      String var5 = var1;
      if (var4 == 30115 || var4 == 30194 || var4 == 30856) {
         if (var1.equalsIgnoreCase("quest_accept")) {
            var2.setCond(1);
            var2.set("enchant_weapon", String.valueOf(1), true);
            var2.setState(2);
            var2.playSound("ItemSound.quest_accept");
            switch (var4) {
               case 30115:
                  var5 = "jurek_q0350_03.htm";
                  break;
               case 30194:
                  var5 = "guyder_q0350_03.htm";
                  break;
               case 30856:
                  var5 = "magister_winonin_q0350_03.htm";
            }
         } else if (var1.equalsIgnoreCase("menu_select?ask=350&reply=1")) {
            switch (var4) {
               case 30115:
                  var5 = "jurek_q0350_05.htm";
                  break;
               case 30194:
                  var5 = "guyder_q0350_05.htm";
                  break;
               case 30856:
                  var5 = "magister_winonin_q0350_05.htm";
            }
         } else if (var1.equalsIgnoreCase("menu_select?ask=350&reply=2")) {
            switch (var4) {
               case 30115:
                  var5 = "jurek_q0350_06.htm";
                  break;
               case 30194:
                  var5 = "guyder_q0350_06.htm";
                  break;
               case 30856:
                  var5 = "magister_winonin_q0350_06.htm";
            }
         } else if (var1.equalsIgnoreCase("menu_select?ask=350&reply=3")) {
            switch (var4) {
               case 30115:
                  var5 = "jurek_q0350_07.htm";
                  break;
               case 30194:
                  var5 = "guyder_q0350_07.htm";
                  break;
               case 30856:
                  var5 = "magister_winonin_q0350_07.htm";
            }
         } else if (var1.equalsIgnoreCase("menu_select?ask=350&reply=4")) {
            var2.giveItems(4629, 1L);
            var2.set("enchant_weapon", String.valueOf(2), true);
            switch (var4) {
               case 30115:
                  var5 = "jurek_q0350_08.htm";
                  break;
               case 30194:
                  var5 = "guyder_q0350_08.htm";
                  break;
               case 30856:
                  var5 = "magister_winonin_q0350_08.htm";
            }
         } else if (var1.equalsIgnoreCase("menu_select?ask=350&reply=5")) {
            var2.giveItems(4640, 1L);
            var2.set("enchant_weapon", String.valueOf(2), true);
            switch (var4) {
               case 30115:
                  var5 = "jurek_q0350_09.htm";
                  break;
               case 30194:
                  var5 = "guyder_q0350_09.htm";
                  break;
               case 30856:
                  var5 = "magister_winonin_q0350_09.htm";
            }
         } else if (var1.equalsIgnoreCase("menu_select?ask=350&reply=6")) {
            var2.giveItems(4651, 1L);
            var2.set("enchant_weapon", String.valueOf(2), true);
            switch (var4) {
               case 30115:
                  var5 = "jurek_q0350_10.htm";
                  break;
               case 30194:
                  var5 = "guyder_q0350_10.htm";
                  break;
               case 30856:
                  var5 = "magister_winonin_q0350_10.htm";
            }
         } else if (var1.equalsIgnoreCase("menu_select?ask=350&reply=7")) {
            int[] var6 = bqd;
            int var7 = var6.length;

            for (int var8 = 0; var8 < var7; var8++) {
               Integer var9 = var6[var8];
               var2.takeItems(var9, 1L);
            }

            switch (var4) {
               case 30115:
                  var5 = "jurek_q0350_14.htm";
                  break;
               case 30194:
                  var5 = "guyder_q0350_14.htm";
                  break;
               case 30856:
                  var5 = "magister_winonin_q0350_14.htm";
            }

            var2.unset("enchant_weapon");
            var2.exitCurrentQuest(true);
         } else if (var1.equalsIgnoreCase("menu_select?ask=350&reply=8")) {
            switch (var4) {
               case 30115:
                  var5 = "jurek_q0350_06a.htm";
                  break;
               case 30194:
                  var5 = "guyder_q0350_06a.htm";
                  break;
               case 30856:
                  var5 = "magister_winonin_q0350_06a.htm";
            }
         }
      }

      return var5;
   }

   @Override
   public String onTalk(NpcInstance var1, QuestState var2) {
      int var3 = var1.getNpcId();
      int var4 = var2.getState();
      int var5 = var2.getInt("enchant_weapon");
      String var6 = "no-quest";
      switch (var4) {
         case 1:
            if (var3 == 30115 || var3 == 30194 || var3 == 30856) {
               if (var2.getPlayer().getLevel() < 40) {
                  switch (var3) {
                     case 30115:
                        var6 = "jurek_q0350_01.htm";
                        break;
                     case 30194:
                        var6 = "guyder_q0350_01.htm";
                        break;
                     case 30856:
                        var6 = "magister_winonin_q0350_01.htm";
                  }

                  var2.exitCurrentQuest(true);
               } else {
                  switch (var3) {
                     case 30115:
                        var6 = "jurek_q0350_02.htm";
                        break;
                     case 30194:
                        var6 = "guyder_q0350_02.htm";
                        break;
                     case 30856:
                        var6 = "magister_winonin_q0350_02.htm";
                  }
               }
            }
         case 2:
            if (var3 == 30115 || var3 == 30194 || var3 == 30856) {
               if (var5 == 1) {
                  switch (var3) {
                     case 30115:
                        var6 = "jurek_q0350_03.htm";
                        break;
                     case 30194:
                        var6 = "guyder_q0350_03.htm";
                        break;
                     case 30856:
                        var6 = "magister_winonin_q0350_03.htm";
                  }
               } else if (var5 > 1) {
                  if (var2.getQuestItemsCount(4661) > 0L
                     || var2.getQuestItemsCount(5579) > 0L
                     || var2.getQuestItemsCount(5582) > 0L
                     || var2.getQuestItemsCount(5914) > 0L
                     || var2.getQuestItemsCount(4639) > 0L
                     || var2.getQuestItemsCount(5577) > 0L
                     || var2.getQuestItemsCount(5580) > 0L
                     || var2.getQuestItemsCount(5908) > 0L
                     || var2.getQuestItemsCount(4650) > 0L
                     || var2.getQuestItemsCount(5578) > 0L
                     || var2.getQuestItemsCount(5581) > 0L
                     || var2.getQuestItemsCount(5911) > 0L) {
                     switch (var3) {
                        case 30115:
                           var6 = "jurek_q0350_11a.htm";
                           break;
                        case 30194:
                           var6 = "guyder_q0350_11a.htm";
                           break;
                        case 30856:
                           var6 = "magister_winonin_q0350_11a.htm";
                     }
                  } else if (var2.getQuestItemsCount(4651) <= 0L
                     && var2.getQuestItemsCount(4652) <= 0L
                     && var2.getQuestItemsCount(4653) <= 0L
                     && var2.getQuestItemsCount(654) <= 0L
                     && var2.getQuestItemsCount(4655) <= 0L
                     && var2.getQuestItemsCount(4656) <= 0L
                     && var2.getQuestItemsCount(4657) <= 0L
                     && var2.getQuestItemsCount(4658) <= 0L
                     && var2.getQuestItemsCount(4659) <= 0L
                     && var2.getQuestItemsCount(4660) <= 0L
                     && var2.getQuestItemsCount(4629) <= 0L
                     && var2.getQuestItemsCount(4630) <= 0L
                     && var2.getQuestItemsCount(4631) <= 0L
                     && var2.getQuestItemsCount(4632) <= 0L
                     && var2.getQuestItemsCount(4633) <= 0L
                     && var2.getQuestItemsCount(4634) <= 0L
                     && var2.getQuestItemsCount(4635) <= 0L
                     && var2.getQuestItemsCount(4636) <= 0L
                     && var2.getQuestItemsCount(4637) <= 0L
                     && var2.getQuestItemsCount(4638) <= 0L
                     && var2.getQuestItemsCount(4640) <= 0L
                     && var2.getQuestItemsCount(4641) <= 0L
                     && var2.getQuestItemsCount(4642) <= 0L
                     && var2.getQuestItemsCount(4643) <= 0L
                     && var2.getQuestItemsCount(4644) <= 0L
                     && var2.getQuestItemsCount(4645) <= 0L
                     && var2.getQuestItemsCount(4646) <= 0L
                     && var2.getQuestItemsCount(4647) <= 0L
                     && var2.getQuestItemsCount(4648) <= 0L
                     && var2.getQuestItemsCount(4649) <= 0L) {
                     var2.takeItems(4662, 1L);
                     var2.takeItems(4663, 1L);
                     var2.takeItems(4664, 1L);
                     switch (var3) {
                        case 30115:
                           var6 = "jurek_q0350_13.htm";
                           break;
                        case 30194:
                           var6 = "guyder_q0350_13.htm";
                           break;
                        case 30856:
                           var6 = "magister_winonin_q0350_13.htm";
                     }
                  } else {
                     switch (var3) {
                        case 30115:
                           var6 = "jurek_q0350_11.htm";
                           break;
                        case 30194:
                           var6 = "guyder_q0350_11.htm";
                           break;
                        case 30856:
                           var6 = "magister_winonin_q0350_11.htm";
                     }
                  }
               }
            }
      }

      return var6;
   }

   @Override
   public String onKill(NpcInstance var1, QuestState var2) {
      Player var3 = var2.getPlayer();
      if (var3 != null && var1.isMonster()) {
         ArrayList var4;
         if (var3.getParty() == null) {
            var4 = new ArrayList(1);
            var4.add(new _350_EnhanceYourWeapon.PlayerResult(var3));
         } else {
            var4 = new ArrayList(var3.getParty().getMemberCount());
            var4.add(new _350_EnhanceYourWeapon.PlayerResult(var3));

            for (Player var6 : var3.getParty().getPartyMembers()) {
               if (var6 != var3 && var6.isInRange(var1.getLoc(), Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                  var4.add(new _350_EnhanceYourWeapon.PlayerResult(var6));
               }
            }
         }

         for (AbsorbInfo var9 : var1.getTemplate().getAbsorbInfo()) {
            this.a(var4, (MonsterInstance)var1, var9);
         }

         for (Object __raw_var10_379 : (Iterable<?>)(Object)(var4)) {
            _350_EnhanceYourWeapon.PlayerResult var10 = (_350_EnhanceYourWeapon.PlayerResult)__raw_var10_379;
            var10.send();
         }

         return null;
      } else {
         return null;
      }
   }

   private void a(List<_350_EnhanceYourWeapon.PlayerResult> var1, MonsterInstance var2, AbsorbInfo var3) {
      int var4 = 0;
      List var5;
      switch (var3.getAbsorbType()) {
         case LAST_HIT:
            var5 = Collections.singletonList(var1.get(0));
            break;
         case PARTY_ALL:
            var5 = var1;
            break;
         case PARTY_RANDOM:
            var4 = var1.size();
            if (var4 == 1) {
               var5 = Collections.singletonList(var1.get(0));
            } else {
               int var19 = Rnd.get(var4);
               var5 = new ArrayList(var19);
               ArrayList var7 = new ArrayList(var1);
               Collections.shuffle(var7);

               for (int var8 = 0; var8 < var19; var8++) {
                  var5.add(var7.get(var8));
               }
            }
            break;
         case PARTY_ONE:
            var4 = var1.size();
            if (var4 == 1) {
               var5 = Collections.singletonList(var1.get(0));
            } else {
               int var6 = Rnd.get(var4);
               var5 = Collections.singletonList(var1.get(var6));
            }
            break;
         default:
            return;
      }

      for (Object __raw_var21_427 : (Iterable<?>)(Object)(var5)) {
         _350_EnhanceYourWeapon.PlayerResult var21 = (_350_EnhanceYourWeapon.PlayerResult)__raw_var21_427;
         if (var21 != null && var21.getMessage() != SystemMsg.THE_SOUL_CRYSTAL_SUCCEEDED_IN_ABSORBING_A_SOUL) {
            Player var22 = var21.getPlayer();
            if ((!var3.isSkill() || var2.isAbsorbed(var22)) && var22.getQuestState(_350_EnhanceYourWeapon.class) != null) {
               boolean var9 = false;
               SoulCrystal var10 = null;
               ItemInstance[] var11 = var22.getInventory().getItems();

               for (ItemInstance var15 : var11) {
                  SoulCrystal var16 = SoulCrystalHolder.getInstance().getCrystal(var15.getItemId());
                  if (var16 != null) {
                     var21.setMessage(SystemMsg.THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_THE_SOUL);
                     if (var10 != null) {
                        var21.setMessage(SystemMsg.THE_SOUL_CRYSTAL_CAUSED_RESONATION_AND_FAILED_AT_ABSORBING_A_SOUL);
                        var9 = true;
                        break;
                     }

                     var10 = var16;
                  }
               }

               if (!var9 && var10 != null) {
                  if (!var3.canAbsorb(var10.getLevel() + 1)) {
                     var21.setMessage(SystemMsg.THE_SOUL_CRYSTAL_IS_REFUSING_TO_ABSORB_THE_SOUL);
                  } else {
                     byte var23 = 0;
                     if (var3.getCursedChance() > 0 && var10.getCursedNextItemId() > 0) {
                        var23 = (byte)(Rnd.chance(var3.getCursedChance()) ? var10.getCursedNextItemId() : 0);
                     }

                     if (var23 == 0) {
                        var23 = (byte)(Rnd.chance(var3.getChance()) ? var10.getNextItemId() : 0);
                     }

                     if (var23 == 0) {
                        var21.setMessage(SystemMsg.THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_THE_SOUL);
                     } else if (var22.consumeItem(var10.getItemId(), 1L)) {
                        var22.getInventory().addItem(var23, 1L);
                        var22.sendPacket(SystemMessage2.obtainItems(var23, 1L, 0));
                        var21.setMessage(SystemMsg.THE_SOUL_CRYSTAL_SUCCEEDED_IN_ABSORBING_A_SOUL);
                     } else {
                        var21.setMessage(SystemMsg.THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_THE_SOUL);
                     }
                  }
               }
            }
         }
      }
   }

   private static class PlayerResult {
      private final Player _player;
      private SystemMsg _message;

      public PlayerResult(Player var1) {
         this._player = var1;
      }

      public Player getPlayer() {
         return this._player;
      }

      public SystemMsg getMessage() {
         return this._message;
      }

      public void setMessage(SystemMsg var1) {
         this._message = var1;
      }

      public void send() {
         if (this._message != null) {
            this._player.sendPacket(this._message);
         }
      }
   }
}

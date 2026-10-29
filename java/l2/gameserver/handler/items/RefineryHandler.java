package l2.gameserver.handler.items;

import java.util.Collection;
import java.util.List;
import l2.commons.util.RandomUtils;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.VariationChanceHolder;
import l2.gameserver.data.xml.holder.VariationGroupHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.ExPutCommissionResultForVariationMake;
import l2.gameserver.network.l2.s2c.ExPutIntensiveResultForVariationMake;
import l2.gameserver.network.l2.s2c.ExPutItemResultForVariationCancel;
import l2.gameserver.network.l2.s2c.ExPutItemResultForVariationMake;
import l2.gameserver.network.l2.s2c.ExShowRefineryInterface;
import l2.gameserver.network.l2.s2c.ExShowVariationCancelWindow;
import l2.gameserver.network.l2.s2c.ExVariationCancelResult;
import l2.gameserver.network.l2.s2c.ExVariationResult;
import l2.gameserver.network.l2.s2c.InventoryUpdate;
import l2.gameserver.network.l2.s2c.ShortCutRegister;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.support.VariationChanceData;
import l2.gameserver.templates.item.support.VariationGroupData;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefineryHandler implements IRefineryHandler {
   private static final RefineryHandler zv = new RefineryHandler();
   protected static final Logger LOG = LoggerFactory.getLogger(RefineryHandler.class);

   public static RefineryHandler getInstance() {
      return zv;
   }

   private RefineryHandler() {
   }

   @Override
   public void onInitRefinery(Player var1) {
      if (!Functions.CheckPlayerConditions(var1)) {
         var1.sendActionFailed();
      } else {
         var1.sendPacket(Msg.SELECT_THE_ITEM_TO_BE_AUGMENTED, ExShowRefineryInterface.STATIC);
      }
   }

   @Override
   public void onPutTargetItem(Player var1, ItemInstance var2) {
      if (!Functions.CheckPlayerConditions(var1)) {
         var1.sendActionFailed();
      } else if (var2.isAugmented()) {
         var1.sendPacket(Msg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.STATIC);
      } else {
         List var3 = VariationGroupHolder.getInstance().getDataForItemId(var2.getItemId());
         if (var3 != null && !var3.isEmpty()) {
            var1.sendPacket(Msg.SELECT_THE_CATALYST_FOR_AUGMENTATION, new ExPutItemResultForVariationMake(var2.getObjectId(), true));
         } else {
            var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
         }
      }
   }

   @Override
   public void onPutMineralItem(Player var1, ItemInstance var2, ItemInstance var3) {
      if (!Functions.CheckPlayerConditions(var1)) {
         var1.sendPacket(ActionFail.STATIC);
      } else if (var2.isAugmented()) {
         var1.sendPacket(Msg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.STATIC);
      } else {
         List var4 = VariationGroupHolder.getInstance().getDataForItemId(var2.getItemId());
         if (var4 != null && !var4.isEmpty()) {
            int var5 = var3.getItemId();
            VariationGroupData var6 = null;
            Pair var7 = VariationChanceHolder.getInstance().getVariationChanceDataForMineral(var5);

            for (Object __raw_var9_80 : (Iterable<?>)(Object)(var4)) {
               VariationGroupData var9 = (VariationGroupData)__raw_var9_80;
               if (var9.getMineralItemId() == var5) {
                  var6 = var9;
                  break;
               }
            }

            if (null == var6 || null == var7) {
               var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
            } else if (var2.getTemplate().isMageItem() && var7.getRight() == null) {
               LOG.warn("No mage variation for item " + var2.getItemId() + " and mineral " + var3.getItemId());
               var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
            } else if (!var2.getTemplate().isMageItem() && var7.getLeft() == null) {
               LOG.warn("No warrior variation for item " + var2.getItemId() + " and mineral " + var3.getItemId());
               var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
            } else if (!var3.getTemplate().testCondition(var1, var3, true)) {
               var1.sendPacket(ActionFail.STATIC);
            } else {
               var1.sendPacket(
                  new ExPutIntensiveResultForVariationMake(var3.getObjectId(), var5, var6.getGemstoneItemId(), var6.getGemstoneItemCnt(), true),
                  new SystemMessage(1959).addNumber(var6.getGemstoneItemCnt()).addItemName(var6.getGemstoneItemId())
               );
            }
         } else {
            var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
         }
      }
   }

   @Override
   public void onPutGemstoneItem(Player var1, ItemInstance var2, ItemInstance var3, ItemInstance var4, long var5) {
      if (!Functions.CheckPlayerConditions(var1)) {
         var1.sendPacket(ActionFail.STATIC);
      } else if (var2.isAugmented()) {
         var1.sendPacket(Msg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.STATIC);
      } else {
         List var7 = VariationGroupHolder.getInstance().getDataForItemId(var2.getItemId());
         if (var7 != null && !var7.isEmpty()) {
            int var8 = var3.getItemId();
            int var9 = var4.getItemId();
            VariationGroupData var10 = null;
            Pair var11 = VariationChanceHolder.getInstance().getVariationChanceDataForMineral(var8);

            for (Object __raw_var13_123 : (Iterable<?>)(Object)(var7)) {
               VariationGroupData var13 = (VariationGroupData)__raw_var13_123;
               if (var13.getMineralItemId() == var8 && var13.getGemstoneItemId() == var9) {
                  var10 = var13;
                  break;
               }
            }

            if (null != var10 && var11 != null) {
               if ((var11.getLeft() == null || ((VariationChanceData)var11.getLeft()).getMineralItemId() == var10.getMineralItemId())
                  && (var11.getRight() == null || ((VariationChanceData)var11.getRight()).getMineralItemId() == var10.getMineralItemId())) {
                  if (var2.getTemplate().isMageItem() && var11.getRight() == null) {
                     LOG.warn("No mage variation for item " + var2.getItemId() + " and mineral " + var3.getItemId());
                     var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
                  } else if (!var2.getTemplate().isMageItem() && var11.getLeft() == null) {
                     LOG.warn("No warrior variation for item " + var2.getItemId() + " and mineral " + var3.getItemId());
                     var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
                  } else if (!var3.getTemplate().testCondition(var1, var3, true)) {
                     var1.sendPacket(ActionFail.STATIC);
                  } else if (var10.getGemstoneItemCnt() <= var5 && var1.getInventory().getCountOf(var9) >= var10.getGemstoneItemCnt()) {
                     var1.sendPacket(
                        new ExPutCommissionResultForVariationMake(var4.getObjectId(), var10.getGemstoneItemCnt()), Msg.PRESS_THE_AUGMENT_BUTTON_TO_BEGIN
                     );
                  } else {
                     var1.sendPacket(Msg.GEMSTONE_QUANTITY_IS_INCORRECT, ActionFail.STATIC);
                  }
               } else {
                  var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
               }
            } else {
               var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
            }
         } else {
            var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
         }
      }
   }

   @Override
   public void onRequestRefine(Player var1, ItemInstance var2, ItemInstance var3, ItemInstance var4, long var5) {
      if (!Functions.CheckPlayerConditions(var1)) {
         var1.sendPacket(ActionFail.STATIC);
      } else if (var2.isAugmented()) {
         var1.sendPacket(Msg.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN, ActionFail.STATIC);
      } else {
         List var7 = VariationGroupHolder.getInstance().getDataForItemId(var2.getItemId());
         if (var7 != null && !var7.isEmpty()) {
            int var8 = var3.getItemId();
            int var9 = var4.getItemId();
            VariationGroupData var10 = null;
            Pair var11 = VariationChanceHolder.getInstance().getVariationChanceDataForMineral(var8);

            for (Object __raw_var13_174 : (Iterable<?>)(Object)(var7)) {
               VariationGroupData var13 = (VariationGroupData)__raw_var13_174;
               if (var13.getMineralItemId() == var8 && var13.getGemstoneItemId() == var9) {
                  var10 = var13;
                  break;
               }
            }

            if (null != var10 && var11 != null) {
               if ((var11.getLeft() == null || ((VariationChanceData)var11.getLeft()).getMineralItemId() == var10.getMineralItemId())
                  && (var11.getRight() == null || ((VariationChanceData)var11.getRight()).getMineralItemId() == var10.getMineralItemId())) {
                  if (var2.getTemplate().isMageItem() && var11.getRight() == null) {
                     LOG.warn("No mage variation for item " + var2.getItemId() + " and mineral " + var3.getItemId());
                     var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
                  } else if (!var2.getTemplate().isMageItem() && var11.getLeft() == null) {
                     LOG.warn("No warrior variation for item " + var2.getItemId() + " and mineral " + var3.getItemId());
                     var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
                  } else if (!var3.getTemplate().testCondition(var1, var3, true)) {
                     var1.sendPacket(ActionFail.STATIC);
                  } else if (var10.getGemstoneItemCnt() <= var5 && var1.getInventory().getCountOf(var9) >= var10.getGemstoneItemCnt()) {
                     Collection var21 = null;
                     Collection var23 = null;
                     if (var2.getTemplate().isMageItem()) {
                        var21 = ((VariationChanceData)var11.getRight()).getVariation1();
                        var23 = ((VariationChanceData)var11.getRight()).getVariation2();
                     } else {
                        var21 = ((VariationChanceData)var11.getLeft()).getVariation1();
                        var23 = ((VariationChanceData)var11.getLeft()).getVariation2();
                     }

                     List<org.apache.commons.lang3.tuple.Pair<Integer, Double>> var14 = (List<org.apache.commons.lang3.tuple.Pair<Integer, Double>>)RandomUtils.pickRandomSortedGroup(var21, 100.0);
                     List<org.apache.commons.lang3.tuple.Pair<Integer, Double>> var15 = (List<org.apache.commons.lang3.tuple.Pair<Integer, Double>>)RandomUtils.pickRandomSortedGroup(var23, 100.0);
                     Integer var16 = var14 != null ? (Integer)RandomUtils.pickRandomSortedGroup(var14, 100.0) : Integer.valueOf(0);
                     Integer var17 = var15 != null ? (Integer)RandomUtils.pickRandomSortedGroup(var15, 100.0) : Integer.valueOf(0);
                     if (var1.getInventory().destroyItem(var4, var10.getGemstoneItemCnt())) {
                        if (var1.getInventory().destroyItem(var3, 1L)) {
                           boolean var18;
                           if (var18 = var2.isEquipped()) {
                              var1.getInventory().unEquipItem(var2);
                           }

                           var2.setVariationStat1(var16);
                           var2.setVariationStat2(var17);
                           if (var18) {
                              var1.getInventory().equipItem(var2);
                           }

                           var1.sendPacket(new InventoryUpdate().addModifiedItem(var2));

                           for (ShortCut var20 : var1.getAllShortCuts()) {
                              if (var20.getId() == var2.getObjectId() && var20.getType() == 1) {
                                 var1.sendPacket(new ShortCutRegister(var1, var20));
                              }
                           }

                           var1.sendChanges();
                           var1.sendPacket(new ExVariationResult(var16, var17, 1));
                        }
                     }
                  } else {
                     var1.sendPacket(Msg.GEMSTONE_QUANTITY_IS_INCORRECT, ActionFail.STATIC);
                  }
               } else {
                  var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
               }
            } else {
               var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
            }
         } else {
            var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
         }
      }
   }

   @Override
   public void onInitRefineryCancel(Player var1) {
      if (Functions.CheckPlayerConditions(var1)) {
         var1.sendPacket(Msg.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION, ExShowVariationCancelWindow.STATIC);
      }
   }

   @Override
   public void onPutTargetCancelItem(Player var1, ItemInstance var2) {
      if (!Functions.CheckPlayerConditions(var1)) {
         var1.sendPacket(ActionFail.STATIC);
      } else {
         List var3 = VariationGroupHolder.getInstance().getDataForItemId(var2.getItemId());
         if (var3 == null || var3.isEmpty()) {
            var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
         } else if (!var2.isAugmented()) {
            var1.sendPacket(Msg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM, ActionFail.STATIC);
         } else {
            VariationGroupData var4 = (VariationGroupData)var3.get(0);
            if (var4 == null) {
               var1.sendPacket(ActionFail.STATIC);
            } else {
               var1.sendPacket(new ExPutItemResultForVariationCancel(var2, var4.getCancelPrice(), true));
            }
         }
      }
   }

   @Override
   public void onRequestCancelRefine(Player var1, ItemInstance var2) {
      if (!Functions.CheckPlayerConditions(var1)) {
         var1.sendPacket(ActionFail.STATIC);
      } else {
         List var3 = VariationGroupHolder.getInstance().getDataForItemId(var2.getItemId());
         if (var3 != null && !var3.isEmpty()) {
            if (!var2.isAugmented()) {
               var1.sendPacket(Msg.AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM, ActionFail.STATIC);
            } else {
               VariationGroupData var4 = (VariationGroupData)var3.get(0);
               if (var4 == null) {
                  var1.sendPacket(ActionFail.STATIC);
               } else {
                  long var5 = var4.getCancelPrice();
                  if (var5 < 0L) {
                     var1.sendPacket(ExVariationCancelResult.FAIL_PACKET);
                  }

                  if (!var1.reduceAdena(var5, true)) {
                     var1.sendPacket(ActionFail.STATIC, Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                  } else {
                     boolean var7;
                     if (var7 = var2.isEquipped()) {
                        var1.getInventory().unEquipItem(var2);
                     }

                     var2.setVariationStat1(0);
                     var2.setVariationStat2(0);
                     if (var7) {
                        var1.getInventory().equipItem(var2);
                     }

                     InventoryUpdate var8 = new InventoryUpdate().addModifiedItem(var2);
                     SystemMessage var9 = new SystemMessage(1965);
                     var9.addItemName(var2.getItemId());
                     var1.sendPacket(new ExVariationCancelResult(1), var8, var9);

                     for (ShortCut var11 : var1.getAllShortCuts()) {
                        if (var11.getId() == var2.getObjectId() && var11.getType() == 1) {
                           var1.sendPacket(new ShortCutRegister(var1, var11));
                        }
                     }

                     var1.sendChanges();
                     var2.save();
                  }
               }
            }
         } else {
            var1.sendPacket(Msg.THIS_IS_NOT_A_SUITABLE_ITEM, ActionFail.STATIC);
         }
      }
   }
}

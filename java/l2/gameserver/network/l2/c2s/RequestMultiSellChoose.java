package l2.gameserver.network.l2.c2s;

import java.util.ArrayList;
import java.util.List;
import l2.commons.math.SafeMath;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.MultiSellEntry;
import l2.gameserver.model.base.MultiSellIngredient;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ItemAttributes;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.items.PcInventory;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class RequestMultiSellChoose extends L2GameClientPacket {
   private int wr;
   private int OB;
   private long aav;

   @Override
   protected void readImpl() {
      this.wr = this.readD();
      this.OB = this.readD();
      this.aav = this.readD();
   }

   @Override
   protected void runImpl() {
      Player var1 = this.getClient().getActiveChar();
      if (var1 != null && this.aav >= 1L) {
         MultiSellHolder.MultiSellListContainer var2 = var1.getMultisell();
         if (var2 == null) {
            var1.sendActionFailed();
            var1.setMultisell(null);
         } else if (var2.getListId() != this.wr) {
            Log.add("Player " + var1.getName() + " trying to change multisell list id, ban this player!", "illegal-actions");
            var1.sendActionFailed();
            var1.setMultisell(null);
         } else if (var1.isActionsDisabled()) {
            var1.sendActionFailed();
         } else if (var1.isInStoreMode()) {
            var1.sendPacket(Msg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
         } else if (var1.isInTrade()) {
            var1.sendActionFailed();
         } else if (var1.isFishing()) {
            var1.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
         } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && var1.getKarma() > 0 && !var1.isGM()) {
            var1.sendActionFailed();
         } else {
            NpcInstance var3 = var1.getLastNpc();
            if (var2.getListId() >= 0 && !var1.isGM() && !NpcInstance.canBypassCheck(var1, var3)) {
               var1.setMultisell(null);
            } else {
               MultiSellEntry var4 = null;

               for (MultiSellEntry var6 : var2.getEntries()) {
                  if (var6.getEntryId() == this.OB) {
                     var4 = var6;
                     break;
                  }
               }

               if (var4 != null) {
                  boolean var38 = var2.isKeepEnchant();
                  boolean var39 = var2.isNoTax();
                  ArrayList var7 = new ArrayList();
                  PcInventory var8 = var1.getInventory();
                  long var9 = 0L;
                  Castle var11 = var3 != null ? var3.getCastle(var1) : null;
                  var8.writeLock();

                  try {
                     long var12 = SafeMath.mulAndCheck(var4.getTax(), this.aav);
                     long var14 = 0L;
                     long var16 = 0L;

                     for (MultiSellIngredient var19 : var4.getProduction()) {
                        if (var19.getItemId() > 0) {
                           ItemTemplate var20 = ItemHolder.getInstance().getTemplate(var19.getItemId());
                           var16 = SafeMath.addAndCheck(var16, SafeMath.mulAndCheck(SafeMath.mulAndCheck(var19.getItemCount(), this.aav), var20.getWeight()));
                           if (var20.isStackable()) {
                              if (var8.getItemByItemId(var19.getItemId()) == null) {
                                 var14++;
                              }
                           } else {
                              var14 = SafeMath.addAndCheck(var14, SafeMath.mulAndCheck(var19.getItemCount(), this.aav));
                           }
                        }
                     }

                     if (!var8.validateWeight(var16)) {
                        var1.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
                        var1.sendActionFailed();
                        return;
                     }

                     if (!var8.validateCapacity(var14)) {
                        var1.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
                        var1.sendActionFailed();
                        return;
                     }

                     if (var4.getIngredients().size() == 0) {
                        var1.sendActionFailed();
                        var1.setMultisell(null);
                        return;
                     }

                     for (MultiSellIngredient var42 : var4.getIngredients()) {
                        int var44 = var42.getItemId();
                        long var21 = var42.getItemCount();
                        int var23 = var42.getItemEnchant();
                        long var24 = !var42.getMantainIngredient() ? SafeMath.mulAndCheck(var21, this.aav) : var21;
                        if (var44 == -200) {
                           if (var1.getClan() == null) {
                              var1.sendPacket(Msg.YOU_ARE_NOT_A_CLAN_MEMBER);
                              return;
                           }

                           if (var1.getClan().getReputationScore() < var24) {
                              var1.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
                              return;
                           }

                           if (var1.getClan().getLeaderId() != var1.getObjectId()) {
                              var1.sendPacket(new SystemMessage(9).addString(var1.getName()));
                              return;
                           }

                           if (!var42.getMantainIngredient()) {
                              var7.add(new RequestMultiSellChoose.ItemData(var44, var24, null));
                           }
                        } else if (var44 == -100) {
                           if (var1.getPcBangPoints() < var24) {
                              var1.sendPacket(Msg.YOU_ARE_SHORT_OF_ACCUMULATED_POINTS);
                              return;
                           }

                           if (!var42.getMantainIngredient()) {
                              var7.add(new RequestMultiSellChoose.ItemData(var44, var24, null));
                           }
                        } else {
                           ItemTemplate var26 = ItemHolder.getInstance().getTemplate(var44);
                           if (!var26.isStackable()) {
                              for (int var27 = 0; var27 < var21 * this.aav; var27++) {
                                 List var28 = var8.getItemsByItemId(var44);
                                 ItemInstance var29 = null;
                                 if (!var38) {
                                    for (Object __raw_var57_160 : (Iterable<?>)(Object)(var28)) {
                                       ItemInstance var57 = (ItemInstance)__raw_var57_160;
                                       if (!var7.contains(new RequestMultiSellChoose.ItemData(var57.getItemId(), var57.getCount(), var57))
                                          && (var29 == null || var57.getEnchantLevel() < var29.getEnchantLevel())
                                          && !var57.isShadowItem()
                                          && !var57.isTemporalItem()
                                          && (!var57.isAugmented() || Config.ALT_ALLOW_TRADE_AUGMENTED)
                                          && ItemFunctions.checkIfCanDiscard(var1, var57)) {
                                          var29 = var57;
                                          if (var29.getEnchantLevel() == 0) {
                                             break;
                                          }
                                       }
                                    }
                                 } else {
                                    for (Object __raw_var31_174 : (Iterable<?>)(Object)(var28)) {
                                       ItemInstance var31 = (ItemInstance)__raw_var31_174;
                                       RequestMultiSellChoose.ItemData var32 = new RequestMultiSellChoose.ItemData(var31.getItemId(), var31.getCount(), var31);
                                       if ((var31.getEnchantLevel() == var23 || !var31.getTemplate().isEquipment())
                                          && !var7.contains(var32)
                                          && var31.canBeExchanged(var1)) {
                                          var29 = var31;
                                          break;
                                       }
                                    }
                                 }

                                 if (var29 == null || this.aav > 1L && var29.isAugmented()) {
                                    var1.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                                    return;
                                 }

                                 if (!var42.getMantainIngredient()) {
                                    var7.add(new RequestMultiSellChoose.ItemData(var29.getItemId(), 1L, var29));
                                 }
                              }
                           } else {
                              if (var44 == 57) {
                                 var9 = SafeMath.addAndCheck(var9, SafeMath.mulAndCheck(var21, this.aav));
                              }

                              ItemInstance var55 = var8.getItemByItemId(var44);
                              if (var55 == null || var55.getCount() < var24) {
                                 var1.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                                 return;
                              }

                              if (!var42.getMantainIngredient()) {
                                 var7.add(new RequestMultiSellChoose.ItemData(var55.getItemId(), var24, var55));
                              }
                           }
                        }

                        if (var1.getAdena() < var9) {
                           var1.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                           return;
                        }
                     }

                     int var41 = 0;
                     ItemAttributes var43 = null;
                     int var45 = 0;
                     int var46 = 0;

                     for (Object __raw_var48_222 : (Iterable<?>)(Object)(var7)) {
                        RequestMultiSellChoose.ItemData var48 = (RequestMultiSellChoose.ItemData)__raw_var48_222;
                        long var50 = var48.getCount();
                        if (var50 > 0L) {
                           if (var48.getId() == -200) {
                              var1.getClan().incReputation((int)(-var50), false, "MultiSell");
                              var1.sendPacket(new SystemMessage(1787).addNumber(var50));
                           } else if (var48.getId() == -100) {
                              var1.reducePcBangPoints((int)var50);
                           } else {
                              ItemInstance var53 = var48.getItem();
                              if (!var8.destroyItem(var48.getItem(), var50)) {
                                 return;
                              }

                              if (var38 && var48.getItem().canBeEnchanted(true)) {
                                 var41 = var48.getItem().getEnchantLevel();
                                 var43 = var48.getItem().getAttributes();
                                 var45 = var48.getItem().getVariationStat1();
                                 var46 = var48.getItem().getVariationStat2();
                              }

                              var1.sendPacket(SystemMessage2.removeItems(var48.getId(), var50));
                              Log.LogItem(var1, Log.ItemLog.MultiSellIngredient, var53, var50, 0L, this.wr);
                           }
                        }
                     }

                     if (var12 > 0L && !var39 && var11 != null) {
                        var1.sendMessage(new CustomMessage("trade.HavePaidTax", var1).addNumber(var12));
                        if (var3 != null && var3.getReflection() == ReflectionManager.DEFAULT) {
                           var11.addToTreasury(var12, true, false);
                        }
                     }

                     for (MultiSellIngredient var49 : var4.getProduction()) {
                        if (var49.getItemId() <= 0) {
                           if (var49.getItemId() == -200) {
                              var1.getClan().incReputation((int)(var49.getItemCount() * this.aav), false, "MultiSell");
                              var1.sendPacket(new SystemMessage(1781).addNumber(var49.getItemCount() * this.aav));
                           } else if (var49.getItemId() == -100) {
                              var1.addPcBangPoints((int)(var49.getItemCount() * this.aav), false);
                           }
                        } else if (ItemHolder.getInstance().getTemplate(var49.getItemId()).isStackable()) {
                           long var52 = SafeMath.mulAndLimit(var49.getItemCount(), this.aav);
                           var8.addItem(var49.getItemId(), var52);
                           var1.sendPacket(SystemMessage2.obtainItems(var49.getItemId(), var52, 0));
                           Log.LogItem(var1, Log.ItemLog.MultiSellProduct, var49.getItemId(), var52, 0L, this.wr);
                        } else {
                           for (int var51 = 0; var51 < this.aav; var51++) {
                              for (int var25 = 0; var25 < var49.getItemCount(); var25++) {
                                 ItemInstance var54 = ItemFunctions.createItem(var49.getItemId());
                                 if (var38) {
                                    if (var54.canBeEnchanted(true)) {
                                       var54.setEnchantLevel(var41);
                                       if (var43 != null) {
                                          var54.setAttributes(var43.clone());
                                       }

                                       if (var45 != 0 || var46 != 0) {
                                          var54.setVariationStat1(var45);
                                          var54.setVariationStat2(var46);
                                       }
                                    }
                                 } else {
                                    var54.setEnchantLevel(var49.getItemEnchant());
                                    var54.setAttributes(var49.getItemAttributes().clone());
                                 }

                                 var8.addItem(var54);
                                 var1.sendPacket(SystemMessage2.obtainItems(var54));
                                 Log.LogItem(var1, Log.ItemLog.MultiSellProduct, var54, var54.getCount(), 0L, this.wr);
                              }
                           }
                        }
                     }
                  } catch (ArithmeticException var36) {
                     this.sendPacket(Msg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
                     return;
                  } finally {
                     var8.writeUnlock();
                  }

                  var1.sendChanges();
                  if (!var2.isShowAll()) {
                     MultiSellHolder.getInstance().SeparateAndSend(var2, var1, var11 == null ? 0.0 : var11.getTaxRate());
                  }
               }
            }
         }
      }
   }

   private class ItemData {
      private final int _id;
      private final long CE;
      private final ItemInstance EP;

      public ItemData(int var2, long var3, ItemInstance var5) {
         this._id = var2;
         this.CE = var3;
         this.EP = var5;
      }

      public int getId() {
         return this._id;
      }

      public long getCount() {
         return this.CE;
      }

      public ItemInstance getItem() {
         return this.EP;
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof RequestMultiSellChoose.ItemData)) {
            return false;
         }

         RequestMultiSellChoose.ItemData var2 = (RequestMultiSellChoose.ItemData)var1;
         return this._id == var2._id && this.CE == var2.CE && this.EP == var2.EP;
      }
   }
}

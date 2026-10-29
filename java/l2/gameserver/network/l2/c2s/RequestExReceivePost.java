package l2.gameserver.network.l2.c2s;

import java.util.Set;
import l2.commons.dao.JdbcEntityState;
import l2.commons.math.SafeMath;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.s2c.ExShowReceivedPostList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;

public class RequestExReceivePost extends L2GameClientPacket {
   private int abM;

   @Override
   protected void readImpl() {
      this.abM = this.readD();
   }

   @Override
   protected void runImpl() {
      Player var1 = this.getClient().getActiveChar();
      if (var1 != null) {
         if (var1.isActionsDisabled()) {
            var1.sendActionFailed();
         } else if (var1.isInStoreMode()) {
            var1.sendPacket(Msg.YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
         } else if (var1.isInTrade()) {
            var1.sendPacket(Msg.YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE);
         } else if (var1.isFishing()) {
            var1.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
         } else if (var1.getEnchantScroll() != null) {
            var1.sendPacket(Msg.YOU_CANNOT_RECEIVE_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
         } else {
            Mail var2 = MailDAO.getInstance().getReceivedMailByMailId(var1.getObjectId(), this.abM);
            if (var2 != null) {
               var1.getInventory().writeLock();

               try {
                  Set var3 = var2.getAttachments();
                  if (var3.size() > 0 && !var1.isInPeaceZone()) {
                     var1.sendPacket(Msg.YOU_CANNOT_RECEIVE_IN_A_NON_PEACE_ZONE_LOCATION);
                     return;
                  }

                  ItemInstance[] var4;
                  synchronized (var3) {
                     if (var2.getAttachments().isEmpty()) {
                        return;
                     }

                     var4 = var2.getAttachments().toArray(new ItemInstance[var3.size()]);
                     int var6 = 0;
                     long var7 = 0L;

                     for (ItemInstance var12 : var4) {
                        var7 = SafeMath.addAndCheck(var7, SafeMath.mulAndCheck(var12.getCount(), var12.getTemplate().getWeight()));
                        if (!var12.getTemplate().isStackable() || var1.getInventory().getItemByItemId(var12.getItemId()) == null) {
                           var6++;
                        }
                     }

                     if (!var1.getInventory().validateWeight(var7)) {
                        this.sendPacket(Msg.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
                        return;
                     }

                     if (!var1.getInventory().validateCapacity(var6)) {
                        this.sendPacket(Msg.YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL);
                        return;
                     }

                     if (var2.getPrice() > 0L) {
                        if (!var1.reduceAdena(var2.getPrice(), true)) {
                           var1.sendPacket(Msg.YOU_CANNOT_RECEIVE_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA);
                           return;
                        }

                        Player var23 = World.getPlayer(var2.getSenderId());
                        if (var23 != null) {
                           var23.addAdena(var2.getPrice(), true);
                           var23.sendPacket(new SystemMessage(3072).addName(var1));
                        } else {
                           int var24 = 1296000 + (int)(System.currentTimeMillis() / 1000L);
                           Mail var25 = var2.reply();
                           var25.setExpireTime(var24);
                           ItemInstance var26 = ItemFunctions.createItem(57);
                           var26.setOwnerId(var25.getReceiverId());
                           var26.setCount(var2.getPrice());
                           var26.setLocation(ItemInstance.ItemLocation.MAIL);
                           var26.save();
                           Log.LogItem(var1, Log.ItemLog.PostSend, var26);
                           var25.addAttachment(var26);
                           var25.save();
                        }
                     }

                     var3.clear();
                  }

                  var2.setJdbcState(JdbcEntityState.UPDATED);
                  var2.update();

                  for (ItemInstance var8 : var4) {
                     var1.sendPacket(new SystemMessage(3073).addItemName(var8.getItemId()).addNumber(var8.getCount()));
                     Log.LogItem(var1, Log.ItemLog.PostRecieve, var8);
                     var1.getInventory().addItem(var8);
                  }

                  var1.sendPacket(Msg.MAIL_SUCCESSFULLY_RECEIVED);
               } catch (ArithmeticException var19) {
               } finally {
                  var1.getInventory().writeUnlock();
               }
            }

            var1.sendPacket(new ExShowReceivedPostList(var1));
         }
      }
   }
}

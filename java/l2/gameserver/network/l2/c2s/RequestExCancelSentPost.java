package l2.gameserver.network.l2.c2s;

import java.util.Set;
import l2.commons.math.SafeMath;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.MailDAO;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.mail.Mail;
import l2.gameserver.network.l2.s2c.ExShowSentPostList;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.utils.Log;

public class RequestExCancelSentPost extends L2GameClientPacket {
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
            var1.sendPacket(Msg.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
         } else if (var1.isInTrade()) {
            var1.sendPacket(Msg.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
         } else if (var1.getEnchantScroll() != null) {
            var1.sendPacket(Msg.YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
         } else if (!var1.isInPeaceZone()) {
            var1.sendPacket(Msg.YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION);
         } else if (var1.isFishing()) {
            var1.sendPacket(Msg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
         } else {
            Mail var2 = MailDAO.getInstance().getSentMailByMailId(var1.getObjectId(), this.abM);
            if (var2 == null) {
               var1.sendPacket(new ExShowSentPostList(var1));
            } else {
               label142:
               if (var2.getAttachments().isEmpty()) {
                  var1.sendActionFailed();
               } else {
                  var1.getInventory().writeLock();

                  try {
                     int var3 = 0;
                     long var4 = 0L;

                     for (ItemInstance var7 : var2.getAttachments()) {
                        var4 = SafeMath.addAndCheck(var4, SafeMath.mulAndCheck(var7.getCount(), var7.getTemplate().getWeight()));
                        if (!var7.getTemplate().isStackable() || var1.getInventory().getItemByItemId(var7.getItemId()) == null) {
                           var3++;
                        }
                     }

                     if (!var1.getInventory().validateWeight(var4)) {
                        this.sendPacket(Msg.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
                        return;
                     }

                     if (var1.getInventory().validateCapacity(var3)) {
                        Set var20 = var2.getAttachments();
                        ItemInstance[] var19;
                        synchronized (var20) {
                           var19 = var2.getAttachments().toArray(new ItemInstance[var20.size()]);
                           var20.clear();
                        }

                        for (ItemInstance var11 : var19) {
                           var1.sendPacket(new SystemMessage(3073).addItemName(var11.getItemId()).addNumber(var11.getCount()));
                           Log.LogItem(var1, Log.ItemLog.PostCancel, var11);
                           var1.getInventory().addItem(var11);
                        }

                        var2.update();
                        MailDAO.getInstance().deleteSentMailByMailId(var1.getObserverMode(), this.abM);
                        MailDAO.getInstance().deleteReceivedMailByMailId(var1.getObserverMode(), this.abM);
                        var2.delete();
                        var1.sendPacket(Msg.MAIL_SUCCESSFULLY_CANCELLED);
                        break label142;
                     }

                     this.sendPacket(Msg.YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL);
                  } catch (ArithmeticException var17) {
                     break label142;
                  } finally {
                     var1.getInventory().writeUnlock();
                  }
               }
            }
         }
      }
   }
}

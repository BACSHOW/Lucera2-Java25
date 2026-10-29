package services;

import java.sql.Connection;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.data.xml.holder.PromoCodeHolder;
import l2.gameserver.data.xml.holder.PromoCodeHolder.ActivateResult;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.PromoCode;
import l2.gameserver.model.promoCode.AddLevelPromoCodeReward;
import l2.gameserver.model.promoCode.ExpPromoCodeReward;
import l2.gameserver.model.promoCode.ItemPromoCodeReward;
import l2.gameserver.model.promoCode.PremiumPromoCodeReward;
import l2.gameserver.model.promoCode.PromoCodeReward;
import l2.gameserver.model.promoCode.SetLevelPromoCodeReward;
import l2.gameserver.model.promoCode.SpPromoCodeReward;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class PromoCodeService extends Functions implements IVoicedCommandHandler, ScriptFile {
   private String[] zF = new String[]{"promo", "promocode"};

   @Override
   public boolean useVoicedCommand(String var1, Player var2, String var3) {
      if (!Config.SERVICE_PROMOCODE_COMMAND_ENABLED) {
         return false;
      }

      if (!this.zF[0].equalsIgnoreCase(var1) && !this.zF[1].equalsIgnoreCase(var1)) {
         return false;
      }

      this.showPromo(var2);
      return true;
   }

   @Override
   public String[] getVoicedCommandList() {
      return !Config.SERVICE_PROMOCODE_COMMAND_ENABLED ? ArrayUtils.EMPTY_STRING_ARRAY : this.zF;
   }

   public void showPromo(Player var1) {
      if (var1 != null) {
         NpcHtmlMessage var2 = new NpcHtmlMessage(var1, null);
         var2.setFile("scripts/services/Promo/code.htm");
         var1.sendPacket(var2);
      }
   }

   public void showPromo() {
      Player var1 = this.getSelf();
      if (var1 != null) {
         NpcHtmlMessage var2 = new NpcHtmlMessage(var1, null);
         var2.setFile("scripts/services/Promo/code.htm");
         var1.sendPacket(var2);
      }
   }

   public void promo() {
      Player var1 = this.getSelf();
      if (var1 != null) {
         NpcHtmlMessage var2 = new NpcHtmlMessage(var1, null);
         var2.setFile("scripts/services/Promo/code_enter.htm");
         var1.sendPacket(var2);
      }
   }

   public void promo(String[] var1) {
      Player var2 = this.getSelf();
      if (var2 != null && var1.length >= 1) {
         if (!Config.SERVICE_PROMOCODE_ENABLED) {
            var2.sendPacket(new NpcHtmlMessage(5).setFile("scripts/services/service_disabled.htm"));
         } else {
            NpcHtmlMessage var3 = new NpcHtmlMessage(var2, null);
            var3.setFile("scripts/services/promo/code_result.htm");
            String var4 = StringUtils.trimToEmpty(var1[0]).toUpperCase();
            if (!var4.isEmpty()) {
               PromoCode var5 = PromoCodeHolder.getInstance().getPromoCode(var4);
               ActivateResult var6 = PromoCodeHolder.getInstance().tryActivate(var2, var5);
               CustomMessage var7;
               switch (var6) {
                  case ALREADY:
                     var7 = new CustomMessage("services.promo.code.already", var2);
                     break;
                  case NOT_FOR_YOU:
                     var7 = new CustomMessage("services.promo.code.not.for.you", var2);
                     break;
                  case OK:
                     var7 = new CustomMessage("services.promo.code.ok", var2);
                     this.a(var4, var2);
                     var2.broadcastPacket(new MagicSkillUse(var2, var2, 6234, 1, 1000, 0L));
                     break;
                  case OUT_OF_DATE:
                     var7 = new CustomMessage("services.promo.code.out.of.date", var2);
                     break;
                  case OUT_OF_LIMIT:
                     var7 = new CustomMessage("services.promo.code.out.of.limit", var2);
                     break;
                  case HWID_LIMITED:
                     var7 = new CustomMessage("services.promo.code.hwid.limit", var2);
                     break;
                  case IP_LIMITED:
                     var7 = new CustomMessage("services.promo.code.ip.limit", var2);
                     break;
                  case MIN_LEVEL_LIMITED:
                     var7 = new CustomMessage("services.promo.minLevel", var2).addNumber(var5.getMinLevel());
                     break;
                  case INVALID_TRY_LATER:
                  default:
                     var7 = new CustomMessage("services.promo.code.invalid.try.later", var2).addString(var4);
               }

               var3 = var3.replace("%result%", String.valueOf(var7));
               if (var6 == ActivateResult.OK) {
                  this.a(var4, var2);
               } else {
                  var2.sendPacket(var3);
               }
            }
         }
      }
   }

   private void a(String var1, Player var2) {
      PromoCode var3 = PromoCodeHolder.getInstance().getPromoCode(var1);
      if (var3 != null) {
         String var4 = HtmCache.getInstance().getNotNull("scripts/services/promo/index.htm", var2);
         String var5 = HtmCache.getInstance().getNotNull("scripts/services/promo/box.htm", var2);
         String var6 = "";

         for (PromoCodeReward var8 : var3.getRewards()) {
            if (var8 instanceof ItemPromoCodeReward) {
               ItemPromoCodeReward var9 = (ItemPromoCodeReward)var8;
               String var10 = var5;
               var10 = var10.replace("%icon%", l2.gameserver.utils.Util.getItemIcon(var9._itemId));
               var10 = var10.replace("%data%", new CustomMessage("promo.box.item", var2).addItemName(var9._itemId).addNumber(var9._itemCount).toString());
               var6 = var6 + var10;
            } else if (var8 instanceof SpPromoCodeReward) {
               SpPromoCodeReward var13 = (SpPromoCodeReward)var8;
               String var20 = var5;
               var20 = var20.replace("%icon%", "icon.Item_CanUse30");
               var20 = var20.replace("%data%", new CustomMessage("promo.box.sp", var2).addString(l2.gameserver.utils.Util.formatAdena(var13._value)).toString());
               var6 = var6 + var20;
            } else if (var8 instanceof ExpPromoCodeReward) {
               ExpPromoCodeReward var14 = (ExpPromoCodeReward)var8;
               String var23 = var5;
               var23 = var23.replace("%icon%", "icon.Item_CanUse31");
               var23 = var23.replace(
                  "%data%", new CustomMessage("promo.box.exp", var2).addString(l2.gameserver.utils.Util.formatAdena(var14._value)).toString()
               );
               var6 = var6 + var23;
            } else if (var8 instanceof SetLevelPromoCodeReward) {
               SetLevelPromoCodeReward var15 = (SetLevelPromoCodeReward)var8;
               String var26 = var5;
               var26 = var26.replace("%icon%", "icon.Item_CanUse38");
               var26 = var26.replace("%data%", new CustomMessage("promo.box.level", var2).addNumber(var15._level).toString());
               var6 = var6 + var26;
            } else if (var8 instanceof AddLevelPromoCodeReward) {
               AddLevelPromoCodeReward var16 = (AddLevelPromoCodeReward)var8;
               String var29 = var5;
               var29 = var29.replace("%icon%", "icon.Item_CanUse35");
               var29 = var29.replace("%data%", new CustomMessage("promo.box.level", var2).addNumber(var16._level).toString());
               var6 = var6 + var29;
            } else if (var8 instanceof PremiumPromoCodeReward) {
               PremiumPromoCodeReward var17 = (PremiumPromoCodeReward)var8;
               String var32 = var5;
               var32 = var32.replace("%icon%", "icon.etc_lump_yellow_i00");
               var32 = var32.replace("%data%", new CustomMessage("promo.box.premium", var2).addNumber(var17._premiumId).toString());
               var6 = var6 + var32;
            }
         }

         var4 = var4.replace("%body%", var6);
         var4 = var4.replace("%code%", var1);
         Functions.show(var4, var2, null);
      }
   }

   private static void e() {
      try (Connection var0 = DatabaseFactory.getInstance().getConnection()) {
         GameServer.getInstance().getDbmsStructureSynchronizer(var0).synchronize(new String[]{"promocode"});
      } catch (Exception var13) {
         throw new RuntimeException(var13);
      }
   }

   @Override
   public void onLoad() {
      if (Config.SERVICE_PROMOCODE_COMMAND_ENABLED) {
         e();
         VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
      }
   }

   @Override
   public void onReload() {
   }

   @Override
   public void onShutdown() {
   }
}

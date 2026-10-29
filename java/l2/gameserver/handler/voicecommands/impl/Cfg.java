package l2.gameserver.handler.voicecommands.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.dao.CharacterVariablesDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.authcomm.AuthServerCommunication;
import l2.gameserver.network.authcomm.gs2as.IGPwdCng;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

public class Cfg implements IVoicedCommandHandler {
   private static final String zJ = "{CALL `lip_RepairePlayer`(?)}";
   private static final Pattern zK = Pattern.compile("^([\\w\\d_-]{4,18})\\s+([\\w\\d_-]{4,16})$");
   private static final long zL = 3600000L;
   private String[] zF = new String[]{"cfg", "menu", "password", "repair"};

   @Override
   public boolean useVoicedCommand(String var1, Player var2, String var3) {
      if (!var1.equalsIgnoreCase(this.zF[0]) && !var1.equalsIgnoreCase(this.zF[1])) {
         if (var1.equalsIgnoreCase(this.zF[2])) {
            String var52 = var2.getVar("LastPwdChng");
            boolean var57 = true;
            if (var52 != null && !var52.isEmpty()) {
               long var60 = Long.parseLong(var52) * 1000L;
               if (var60 + 3600000L > System.currentTimeMillis()) {
                  var2.sendMessage(new CustomMessage("usercommandhandlers.CantChangePasswordSoFast", var2));
                  var57 = false;
               }
            }

            if (var57 && !var3.isEmpty()) {
               Matcher var61 = zK.matcher(var3);
               if (var61.find() && var61.groupCount() == 2) {
                  String var65 = var61.group(1);
                  String var67 = var61.group(2);
                  AuthServerCommunication.getInstance().sendPacket(new IGPwdCng(var2, var65, var67));
                  return true;
               }

               var2.sendMessage(new CustomMessage("usercommandhandlers.PasswordNotMet", var2));
            }

            NpcHtmlMessage var62 = new NpcHtmlMessage(5);
            var62.setFile("command/passchg.htm");
            var2.sendPacket(var62);
         } else if (var1.equalsIgnoreCase(this.zF[3])) {
            NpcHtmlMessage var51 = new NpcHtmlMessage(5);
            var51.setFile("command/repair.htm");
            if (var3.isEmpty()) {
               ArrayList var56 = new ArrayList();
               Connection var59 = null;
               PreparedStatement var64 = null;
               ResultSet var66 = null;

               try {
                  var59 = DatabaseFactory.getInstance().getConnection();
                  var64 = var59.prepareStatement("SELECT `obj_Id`, `char_name` FROM `characters` WHERE `account_name` = ? AND `online` = 0");
                  var64.setString(1, var2.getAccountName());
                  var66 = var64.executeQuery();

                  while (var66.next()) {
                     String var68 = var66.getString("char_name");
                     int var70 = var66.getInt("obj_Id");
                     var56.add(Pair.of(var70, var68));
                  }
               } catch (Exception var45) {
               } finally {
                  DbUtils.closeQuietly(var59, var64, var66);
               }

               StringBuilder var69 = new StringBuilder();

               for (Object __raw_var11_87 : (Iterable<?>)(Object)(var56)) {
                  Pair var11 = (Pair)__raw_var11_87;
                  String var12 = (String)var11.getRight();
                  int var13 = (Integer)var11.getLeft();
                  boolean var14 = false;
                  String var15 = CharacterVariablesDAO.getInstance().getVar(var13, "jailed");
                  if (!StringUtils.isBlank(var15)) {
                     int var16 = var15.indexOf(59);
                     var14 = var16 > 0 && Long.parseLong(var15.substring(0, var16)) - System.currentTimeMillis() > 0L;
                  }

                  if (!var14) {
                     var69.append("<a action=\"bypass -h user_repair " + var13 + "\">" + var12 + "</a><br1>");
                  } else {
                     var69.append("<font color=\"AAAAAA\">" + var12 + "</font><br1>");
                  }
               }

               var51.replace("%repair%", var69.toString());
            } else {
               Connection var55 = null;
               PreparedStatement var58 = null;
               CallableStatement var63 = null;
               ResultSet var8 = null;

               try {
                  int var9 = Integer.parseInt(var3);
                  String var10 = CharacterVariablesDAO.getInstance().getVar(var9, "jailed");
                  if (var10 != null && System.currentTimeMillis() / 1000L < Integer.parseInt(var10) + 60) {
                     var2.sendMessage(new CustomMessage("usercommandhandlers.CharNotFound", var2));
                     return true;
                  }

                  var55 = DatabaseFactory.getInstance().getConnection();

                  try {
                     var58 = var55.prepareStatement("SELECT * FROM `characters` WHERE `account_name` = ? AND `obj_Id` = ? AND `online` = 0");
                     var58.setString(1, var2.getAccountName());
                     var58.setInt(2, var9);
                     var8 = var58.executeQuery();
                     if (!var8.next()) {
                        var2.sendMessage(new CustomMessage("usercommandhandlers.CharNotFound", var2));
                        return true;
                     }
                  } finally {
                     DbUtils.close(var58, var8);
                  }

                  if (World.getPlayer(var9) != null) {
                     var2.sendMessage(new CustomMessage("usercommandhandlers.CharacterOnline", var2));
                     return true;
                  }

                  try {
                     var63 = var55.prepareCall("{CALL `lip_RepairePlayer`(?)}");
                     var63.setInt(1, var9);
                     var63.execute();
                  } finally {
                     DbUtils.close(var63);
                  }

                  var51.replace("%repair%", "Character successfully repaired.");
                  var2.sendMessage(new CustomMessage("usercommandhandlers.CharacterRepaired", var2));
               } catch (Exception var48) {
                  var51.replace("%repair%", "Character reparation failed.");
                  var48.printStackTrace();
               } finally {
                  DbUtils.closeQuietly(var55);
               }
            }

            var2.sendPacket(var51);
         }
      } else {
         if (var3 != null) {
            String[] var4 = var3.split(" ");
            if (var4.length == 2) {
               if (var4[0].equalsIgnoreCase("dli")) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     var2.setVar("DroplistIcons", "1", -1L);
                  } else if (var4[1].equalsIgnoreCase("of")) {
                     var2.unsetVar("DroplistIcons");
                  }
               }

               if (var4[0].equalsIgnoreCase("noe")) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     var2.setVar("NoExp", "1", -1L);
                     var2.sendMessage(new CustomMessage("usercommandhandlers.ExpOn", var2));
                  } else if (var4[1].equalsIgnoreCase("of")) {
                     var2.unsetVar("NoExp");
                     var2.sendMessage(new CustomMessage("usercommandhandlers.ExpOff", var2));
                  }
               }

               if (var4[0].equalsIgnoreCase("notraders")) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     var2.setNotShowTraders(true);
                     var2.setVar("notraders", "true", -1L);
                  } else if (var4[1].equalsIgnoreCase("of")) {
                     var2.setNotShowTraders(false);
                     var2.unsetVar("notraders");
                  }
               }

               if (var4[0].equalsIgnoreCase("buffAnimRange")) {
                  int var5 = 15 * NumberUtils.toInt(var4[1], 0);
                  if (var5 < 0) {
                     var5 = -1;
                  } else if (var5 > 1500) {
                     var5 = 1500;
                  }

                  var2.setBuffAnimRange(var5);
                  var2.setVar("buffAnimRange", String.valueOf(var5), -1L);
               }

               if (var4[0].equalsIgnoreCase("shotsAnim")) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     var2.unsetVar("noShotsAnim");
                     var2.setNoShotsAnim(false);
                  } else if (var4[1].startsWith("of")) {
                     var2.setVar("noShotsAnim", "1", -1L);
                     var2.setNoShotsAnim(true);
                  }
               }

               if (var4[0].equalsIgnoreCase("noShift")) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     var2.setVar("noShift", "1", -1L);
                  } else if (var4[1].startsWith("of")) {
                     var2.unsetVar("noShift");
                  }
               }

               if (var4[0].equalsIgnoreCase("hwidlock")
                  && var2.getNetConnection() != null
                  && var2.getNetConnection().getHwid() != null
                  && !var2.getNetConnection().getHwid().isEmpty()) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     var2.setHWIDLock(var2.getNetConnection().getHwid());
                  } else if (var4[1].startsWith("of")) {
                     var2.setHWIDLock(null);
                  }
               }

               if (var4[0].equalsIgnoreCase("iplock")
                  && var2.getNetConnection() != null
                  && var2.getNetConnection().getIpAddr() != null
                  && !var2.getNetConnection().getIpAddr().isEmpty()) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     var2.setIPLock(var2.getNetConnection().getIpAddr());
                  } else if (var4[1].startsWith("of")) {
                     var2.setIPLock(null);
                  }
               }

               if (var4[0].equalsIgnoreCase("lang")) {
                  if (var4[1].equalsIgnoreCase("en")) {
                     var2.setVar("lang@", "en", -1L);
                  } else if (var4[1].equalsIgnoreCase("ru")) {
                     var2.setVar("lang@", "ru", -1L);
                  }
               }

               if (Config.SERVICES_ENABLE_NO_CARRIER && var4[0].equalsIgnoreCase("noCarrier")) {
                  int var53 = NumberUtils.toInt(var4[1], 0);
                  if (var53 <= 0) {
                     var53 = 0;
                  } else if (var53 > Config.SERVICES_NO_CARRIER_MAX_TIME) {
                     var53 = Config.SERVICES_NO_CARRIER_MAX_TIME;
                  } else if (var53 < Config.SERVICES_NO_CARRIER_MIN_TIME) {
                     var53 = Config.SERVICES_NO_CARRIER_MIN_TIME;
                  }

                  var2.setVar("noCarrier", String.valueOf(var53), -1L);
               }

               if (var4[0].equalsIgnoreCase("translit")) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     var2.setVar("translit", "tl", -1L);
                  } else if (var4[1].equalsIgnoreCase("la")) {
                     var2.setVar("translit", "tc", -1L);
                  } else if (var4[1].equalsIgnoreCase("of")) {
                     var2.unsetVar("translit");
                  }
               }

               if (var4[0].equalsIgnoreCase("blockparty")) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     var2.setVar("blockparty@", "on", -1L);
                  } else if (var4[1].equalsIgnoreCase("of")) {
                     var2.unsetVar("blockparty@");
                  }
               }

               if (var4[0].equalsIgnoreCase("blocktrade")) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     var2.setVar("blocktrade@", "on", -1L);
                  } else if (var4[1].equalsIgnoreCase("of")) {
                     var2.unsetVar("blocktrade@");
                  }
               }

               if (Config.AUTO_LOOT_INDIVIDUAL && var4[0].equalsIgnoreCase("autoloot")) {
                  if (var4[1].equalsIgnoreCase("on")) {
                     if (Config.AUTO_LOOT_ONLY_FOR_PREMIUM && !var2.hasBonus()) {
                        var2.sendMessage(new CustomMessage("usercommandhandlers.AutolootForPAOnly", var2));
                        return false;
                     }

                     var2.setAutoLoot(true);
                     if (Config.AUTO_LOOT_HERBS) {
                        var2.setAutoLootHerbs(true);
                     }

                     var2.setAutoLootAdena(true);
                     var2.sendMessage(new CustomMessage("usercommandhandlers.AutoLootAll", var2));
                  } else if (var4[1].equalsIgnoreCase("ad")) {
                     var2.setAutoLoot(false);
                     var2.setAutoLootHerbs(false);
                     if (Config.AUTO_LOOT_ONLY_FOR_PREMIUM && !var2.hasBonus()) {
                        var2.sendMessage(new CustomMessage("usercommandhandlers.AutolootForPAOnly", var2));
                        return false;
                     }

                     var2.setAutoLootAdena(true);
                     var2.sendMessage(new CustomMessage("usercommandhandlers.AutoLootAdena", var2));
                  } else if (var4[1].equalsIgnoreCase("of")) {
                     var2.setAutoLoot(false);
                     var2.setAutoLootHerbs(false);
                     var2.setAutoLootAdena(false);
                     var2.sendMessage(new CustomMessage("usercommandhandlers.AutoLootOff", var2));
                  }
               }
            }
         }

         NpcHtmlMessage var50 = new NpcHtmlMessage(5);
         var50.setFile("command/cfg.htm");
         var50.replace("%dli%", var2.getVarB("DroplistIcons") ? "On" : "Off");
         var50.replace("%noe%", var2.getVarB("NoExp") ? "On" : "Off");
         var50.replace("%notraders%", var2.getVarB("notraders") ? "On" : "Off");
         var50.replace("%noShift%", var2.getVarB("noShift") ? "On" : "Off");
         var50.replace("%noCarrier%", Config.SERVICES_ENABLE_NO_CARRIER ? (var2.getVarB("noCarrier") ? var2.getVar("noCarrier") : "0") : "N/A");
         if (var2.isAutoLootEnabled()) {
            var50.replace("%autoloot%", "All");
         } else if (var2.isAutoLootAdenaEnabled()) {
            var50.replace("%autoloot%", "Adena");
         } else {
            var50.replace("%autoloot%", "Off");
         }

         if (var2.getLangId() == 0) {
            var50.replace("%lang%", "En");
         } else if (var2.getLangId() == 1) {
            var50.replace("%lang%", "Ru");
         } else {
            var50.replace("%lang%", "Unk");
         }

         if (var2.getHWIDLock() != null
            && var2.getNetConnection() != null
            && var2.getNetConnection().getHwid() != null
            && !var2.getNetConnection().getHwid().isEmpty()) {
            var50.replace("%hwidlock%", "On");
         } else {
            var50.replace("%hwidlock%", "Off");
         }

         if (var2.getIPLock() != null
            && var2.getNetConnection() != null
            && var2.getNetConnection().getIpAddr() != null
            && !var2.getNetConnection().getIpAddr().isEmpty()) {
            var50.replace("%iplock%", "On");
         } else {
            var50.replace("%iplock%", "Off");
         }

         if (var2.getBuffAnimRange() < 0) {
            var50.replace("%buffAnimRange%", "Off");
         } else if (var2.getBuffAnimRange() == 0) {
            if (var2.isLangRus()) {
               var50.replace("%buffAnimRange%", "Свои");
            } else {
               var50.replace("%buffAnimRange%", "Self");
            }
         } else {
            var50.replace("%buffAnimRange%", String.valueOf(var2.getBuffAnimRange() / 15));
         }

         if (var2.isNoShotsAnim()) {
            var50.replace("%shotsAnim%", "Off");
         } else {
            var50.replace("%shotsAnim%", "On");
         }

         String var54 = var2.getVar("blockparty@");
         if (var54 == null) {
            var50.replace("%blockparty%", "Off");
         } else if (var54.equals("on")) {
            var50.replace("%blockparty%", "On");
         }

         String var6 = var2.getVar("blocktrade@");
         if (var6 == null) {
            var50.replace("%blocktrade%", "Off");
         } else if (var6.equals("on")) {
            var50.replace("%blocktrade%", "On");
         }

         String var7 = var2.getVar("translit");
         if (var7 == null) {
            var50.replace("%translit%", "Off");
         } else if (var7.equals("tl")) {
            var50.replace("%translit%", "On");
         } else {
            var50.replace("%translit%", "Lt");
         }

         var2.sendPacket(var50);
      }

      return true;
   }

   @Override
   public String[] getVoicedCommandList() {
      return this.zF;
   }
}

package l2.gameserver.instancemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.model.GWAutoAnnounce;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class AutoAnnounce implements Runnable {
   private static final Logger _log = LoggerFactory.getLogger(AutoAnnounce.class.getName());
   private static AutoAnnounce zX;
   static Map<Integer, GWAutoAnnounce> _lists;

   public static AutoAnnounce getInstance() {
      if (zX == null) {
         zX = new AutoAnnounce();
      }

      return zX;
   }

   public void reload() {
      zX = new AutoAnnounce();
   }

   public AutoAnnounce() {
      _lists = new HashMap<>();
      _log.info("AutoAnnounce: Initializing");
      this.load();
      _log.info("AutoAnnounce: Loaded " + (_lists.size() - 1) + " announce.");
   }

   private void load() {
      try {
         DocumentBuilderFactory var1 = DocumentBuilderFactory.newInstance();
         var1.setValidating(false);
         var1.setIgnoringComments(true);
         File var2 = new File("./config/autoannounce.xml");
         if (!var2.exists()) {
            if (Config.DEBUG) {
               System.out.println("AutoAnnounce: NO FILE");
            }

            return;
         }

         Document var3 = var1.newDocumentBuilder().parse(var2);
         int var4 = 0;
         if (var4 == 0) {
            ArrayList var5 = new ArrayList();
            GWAutoAnnounce var6 = new GWAutoAnnounce(var4);
            byte var7 = 0;
            var5.add("" + var7);
            String var8 = "Own1";
            String var9 = "Own2";
            var5.add(var8 + var9);
            var6.setAnnounce(0, 0, var5);
            _lists.put(var4, var6);
            var4++;
         }

         for (Node var16 = var3.getFirstChild(); var16 != null; var16 = var16.getNextSibling()) {
            if ("list".equalsIgnoreCase(var16.getNodeName())) {
               for (Node var17 = var16.getFirstChild(); var17 != null; var17 = var17.getNextSibling()) {
                  if ("announce".equalsIgnoreCase(var17.getNodeName())) {
                     ArrayList var18 = new ArrayList();
                     NamedNodeMap var19 = var17.getAttributes();
                     int var20 = Integer.parseInt(var19.getNamedItem("delay").getNodeValue());
                     int var10 = Integer.parseInt(var19.getNamedItem("repeat").getNodeValue());

                     boolean var11;
                     try {
                        var11 = Boolean.parseBoolean(var19.getNamedItem("is_screen_message").getNodeValue());
                     } catch (Exception var14) {
                        var11 = false;
                     }

                     GWAutoAnnounce var12 = new GWAutoAnnounce(var4);
                     var12.setScreenAnnounce(var11);

                     for (Node var13 = var17.getFirstChild(); var13 != null; var13 = var13.getNextSibling()) {
                        if ("message".equalsIgnoreCase(var13.getNodeName())) {
                           var18.add(String.valueOf(var13.getAttributes().getNamedItem("text").getNodeValue()));
                        }
                     }

                     var12.setAnnounce(var20, var10, var18);
                     _lists.put(var4, var12);
                     var4++;
                  }
               }
            }
         }

         if (Config.DEBUG) {
            System.out.println("AutoAnnounce: OK");
         }
      } catch (Exception var15) {
         _log.error("AutoAnnounce: Error parsing autoannounce.xml file. " + var15);
      }
   }

   @Override
   public void run() {
      if (_lists.size() > 1) {
         for (int var1 = 1; var1 < _lists.size(); var1++) {
            GWAutoAnnounce var2 = _lists.get(var1);
            if (var2.canAnnounce()) {
               for (String var5 : var2.getMessage()) {
                  if (!var2.isScreenAnnounce()) {
                     Announcements.getInstance().announceToAll(var5);
                  } else {
                     int var6 = 3000 + var5.length() * 100;
                     ExShowScreenMessage var7 = new ExShowScreenMessage(var5, var6, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);

                     for (Player var9 : GameObjectsStorage.getAllPlayersForIterate()) {
                        var9.sendPacket(var7);
                     }
                  }
               }

               _lists.get(var1).updateRepeat();
            }
         }
      }
   }

   public static String getRevision() {
      return _lists.size() == 0 ? "" : _lists.get(0).getMessage().get(0);
   }

   public static String getOwnerName() {
      return _lists.size() == 0 ? "" : _lists.get(0).getMessage().get(1);
   }
}

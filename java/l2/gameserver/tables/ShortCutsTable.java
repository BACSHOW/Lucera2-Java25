package l2.gameserver.tables;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import l2.commons.data.xml.helpers.SimpleDTDEntityResolver;
import l2.gameserver.Config;
import l2.gameserver.model.actor.instances.player.ShortCut;
import l2.gameserver.model.base.ClassId;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ShortCutsTable {
   private static final Logger _log = LoggerFactory.getLogger(ShortCutsTable.class);
   private static ShortCutsTable aqR;
   private Map<ClassId, List<ShortCut>> Nv = a(new File(Config.DATAPACK_ROOT, "data/character_shortcuts.xml"));

   public static ShortCutsTable getInstance() {
      if (aqR == null) {
         aqR = new ShortCutsTable();
      }

      return aqR;
   }

   private ShortCutsTable() {
   }

   public List<ShortCut> getShortCuts(ClassId var1) {
      List var2 = this.Nv.get(var1);
      return var2 == null ? Collections.emptyList() : var2;
   }

   private static Map<ClassId, List<ShortCut>> a(final File var0) {
      HashMap var1 = new HashMap();
      if (!var0.exists()) {
         _log.warn("File " + var0.getAbsolutePath() + " not exists");
         return Collections.emptyMap();
      }

      SAXReader var2 = new SAXReader();
      var2.setValidation(true);
      var2.setErrorHandler(new ErrorHandler() {
         @Override
         public void warning(SAXParseException var1) throws SAXException {
            ShortCutsTable._log.warn("File: " + var0.getName() + ":" + var1.getLineNumber() + " warning: " + var1.getMessage());
         }

         @Override
         public void error(SAXParseException var1) throws SAXException {
            ShortCutsTable._log.error("File: " + var0.getName() + ":" + var1.getLineNumber() + " error: " + var1.getMessage());
         }

         @Override
         public void fatalError(SAXParseException var1) throws SAXException {
            ShortCutsTable._log.error("File: " + var0.getName() + ":" + var1.getLineNumber() + " fatal: " + var1.getMessage());
         }
      });
      var2.setEntityResolver(new SimpleDTDEntityResolver(new File(var0.getParentFile(), FilenameUtils.removeExtension(var0.getName()) + ".dtd")));
      FileInputStream var3 = null;

      try {
         var3 = new FileInputStream(var0);
         Document var4 = var2.read(var3);
         Element var5 = var4.getRootElement();
         Iterator var6 = var5.elementIterator();

         while (var6.hasNext()) {
            Element var7 = (Element)var6.next();
            if ("shortcut".equals(var7.getName())) {
               ClassId var8 = null;
               String var9 = var7.attributeValue("classId");
               if (var9 != null) {
                  var8 = ClassId.valueOf(var9);
               }

               int var10 = Integer.parseInt(var7.attributeValue("slot", "0"));
               int var11 = Integer.parseInt(var7.attributeValue("page", "0"));
               String var12 = var7.attributeValue("type");
               byte var13 = 0;
               if ("ITEM".equalsIgnoreCase(var12) || "TYPE_ITEM".equalsIgnoreCase(var12)) {
                  var13 = 1;
               } else if (!"SKILL".equalsIgnoreCase(var12) && !"TYPE_SKILL".equalsIgnoreCase(var12)) {
                  if (!"ACTION".equalsIgnoreCase(var12) && !"TYPE_ACTION".equalsIgnoreCase(var12)) {
                     throw new RuntimeException("Unknown short cut type");
                  }

                  var13 = 3;
               } else {
                  var13 = 2;
               }

               int var14 = Integer.parseInt(var7.attributeValue("id", "0"));
               int var15 = Integer.parseInt(var7.attributeValue("level", "-1"));
               int var16 = Integer.parseInt(var7.attributeValue("characterType", "1"));
               ShortCut var17 = new ShortCut(var10, var11, var13, var14, var15, var16);
               if (var8 == null) {
                  for (ClassId var21 : ClassId.VALUES) {
                     List var22 = (List)var1.get(var21);
                     if (var22 == null) {
                        var1.put(var21, var22 = new ArrayList());
                     }

                     var22.add(var17);
                  }
               } else {
                  List var18 = (List)var1.get(var8);
                  if (var18 == null) {
                     var1.put(var8, var18 = new ArrayList());
                  }

                  var18.add(var17);
               }
            }
         }
      } catch (Exception var31) {
         _log.warn("Exception: " + var31, var31);
      } finally {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Exception var30) {
            }
         }
      }

      return var1;
   }
}

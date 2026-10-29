package l2.gameserver.data.xml.parser;

import gnu.trove.TIntArrayList;
import java.io.File;
import java.util.Iterator;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.HennaHolder;
import l2.gameserver.templates.Henna;
import org.dom4j.Element;

public final class HennaParser extends AbstractFileParser<HennaHolder> {
   private static final HennaParser xq = new HennaParser();

   public static HennaParser getInstance() {
      return xq;
   }

   protected HennaParser() {
      super(HennaHolder.getInstance());
   }

   @Override
   public File getXMLFile() {
      return new File(Config.DATAPACK_ROOT, "data/hennas.xml");
   }

   @Override
   public String getDTDFileName() {
      return "hennas.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         int var4 = Integer.parseInt(var3.attributeValue("symbol_id"));
         int var5 = Integer.parseInt(var3.attributeValue("dye_id"));
         long var6 = Integer.parseInt(var3.attributeValue("price"));
         long var8 = var3.attributeValue("draw_count") == null ? 10L : Integer.parseInt(var3.attributeValue("draw_count"));
         int var10 = Integer.parseInt(var3.attributeValue("wit"));
         int var11 = Integer.parseInt(var3.attributeValue("str"));
         int var12 = Integer.parseInt(var3.attributeValue("int"));
         int var13 = Integer.parseInt(var3.attributeValue("con"));
         int var14 = Integer.parseInt(var3.attributeValue("dex"));
         int var15 = Integer.parseInt(var3.attributeValue("men"));
         TIntArrayList var16 = new TIntArrayList();
         Iterator var17 = var3.elementIterator("class");

         while (var17.hasNext()) {
            Element var18 = (Element)var17.next();
            var16.add(Integer.parseInt(var18.attributeValue("id")));
         }

         Henna var19 = new Henna(var4, var5, var6, var8, var10, var12, var13, var11, var14, var15, var16);
         this.getHolder().addHenna(var19);
      }
   }
}

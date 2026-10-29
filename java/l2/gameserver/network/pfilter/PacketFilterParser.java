package l2.gameserver.network.pfilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import l2.commons.data.xml.AbstractFileParser;
import l2.gameserver.network.l2.c2s.ValidatePosition;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketFilterParser extends AbstractFileParser<PacketFilterHolder> {
   private static final Logger sQ = LoggerFactory.getLogger(PacketFilterParser.class);
   private static final PacketFilterParser anT = new PacketFilterParser();

   private PacketFilterParser() {
      super(PacketFilterHolder.getInstance());
   }

   public static PacketFilterParser getInstance() {
      return anT;
   }

   @Override
   public File getXMLFile() {
      return new File("config/packetfilter.xml");
   }

   @Override
   public String getDTDFileName() {
      return "packetfilter.dtd";
   }

   private Limit f(Element var1) throws Exception {
      int var2 = Integer.parseInt(var1.attributeValue("count"));
      if (var2 >= 0 && var2 <= 15000) {
         int var3 = Integer.parseInt(var1.attributeValue("perMs"));
         if (var3 >= 10 && var3 <= 250000) {
            ArrayList var4 = new ArrayList();
            Iterator var5 = var1.elementIterator();

            while (var5.hasNext()) {
               Element var6 = (Element)var5.next();
               if ("actionFailed".equalsIgnoreCase(var6.getName())) {
                  var4.add(LimitAction.doActionFail());
               } else if ("kick".equalsIgnoreCase(var6.getName())) {
                  var4.add(LimitAction.doKick());
               } else if ("drop".equalsIgnoreCase(var6.getName())) {
                  var4.add(LimitAction.doDrop());
               } else if ("log".equalsIgnoreCase(var6.getName())) {
                  String var7 = var6.attributeValue("format");
                  var4.add(LimitAction.doLog(Objects.requireNonNull(var7, "\"format\" is null")));
               } else if ("msg".equalsIgnoreCase(var6.getName())) {
                  String var10 = var6.attributeValue("text");
                  var4.add(LimitAction.doMsg(Objects.requireNonNull(var10, "\"text\" is null")));
               }
            }

            boolean var8 = false;

            for (Object __raw_var11_62 : (Iterable<?>)(Object)(var4)) {
               LimitAction var11 = (LimitAction)__raw_var11_62;
               if (var11.dropPacket()) {
                  var8 = true;
               }
            }

            return new Limit(var4, var8, var2, var3);
         } else {
            throw new IllegalArgumentException("Field \"perMs\"=" + var3 + " out of [" + 10 + " - " + 250000 + "] range.");
         }
      } else {
         throw new IllegalArgumentException("Field \"count\"=" + var2 + " out of [" + 0 + " - " + 15000 + "] range.");
      }
   }

   private Limit g(Element var1) throws Exception {
      Limit var2 = null;
      Iterator var3 = var1.elementIterator();

      while (var3.hasNext()) {
         Element var4 = (Element)var3.next();
         if ("limit".equalsIgnoreCase(var4.getName())) {
            if (var2 != null) {
               this._log.warn("PacketFilter: Limit redefined.");
            }

            var2 = this.f(var4);
         }
      }

      return var2;
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator();

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         if ("packetByOpcode".equalsIgnoreCase(var3.getName())) {
            int var10 = Integer.decode(var3.attributeValue("op"));
            if (var10 >= 0 && var10 <= 255) {
               Integer var11 = var3.attributeValue("exOp") != null ? Integer.decode(var3.attributeValue("exOp")) : null;
               if (var11 == null || var11 >= 0 && var11 <= 65535) {
                  Limit var12 = this.g(var3);
                  if (var12 == null) {
                     throw var11 == null
                        ? new IllegalStateException(String.format("Limit for opcode <0x%02x> undefined.", var10))
                        : new IllegalStateException(String.format("Limit for opcodeEx <0x%02x:0x%04x> undefined.", var10, var11));
                  }

                  if (var11 == null) {
                     this.getHolder().addByOpcodeLimit(var10, var12);
                  } else {
                     this.getHolder().addByOpcodeExLimit(var10, var11, var12);
                  }
                  continue;
               }

               throw new IllegalArgumentException("Field \"exOp\"=" + var11 + " out of [0x0000 - 0xffff] range.");
            }

            throw new IllegalArgumentException("Field \"op\"=" + var10 + " out of [0x00 - 0xff] range.");
         } else if ("packetByName".equalsIgnoreCase(var3.getName())) {
            String var4 = var3.attributeValue("name");
            String var5 = ValidatePosition.class.getPackage().getName();
            String var6 = var5 + "." + var4;
            Class var7 = null;

            try {
               var7 = Class.forName(var6);
            } catch (Exception var9) {
               throw new IllegalArgumentException("Field \"name\"=" + var4 + " unprocessable.", var9);
            }

            Limit var8 = this.g(var3);
            if (var8 == null) {
               throw new IllegalStateException("Limit for packet \"" + var4 + "\" undefined.");
            }

            this.getHolder().addByPacketNameLimit(var7, var8);
         }
      }
   }
}

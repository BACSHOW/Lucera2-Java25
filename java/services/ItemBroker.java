package services;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.RecipeHolder;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Recipe;
import l2.gameserver.model.World;
import l2.gameserver.model.base.Element;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.items.ManufactureItem;
import l2.gameserver.model.items.TradeItem;
import l2.gameserver.network.l2.s2c.CharInfo;
import l2.gameserver.network.l2.s2c.PrivateStoreMsgBuy;
import l2.gameserver.network.l2.s2c.PrivateStoreMsgSell;
import l2.gameserver.network.l2.s2c.RadarControl;
import l2.gameserver.network.l2.s2c.RecipeShopMsg;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.ItemTemplate.ItemClass;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

public class ItemBroker extends Functions {
   private static final int bWV = 10;
   private static final int bWW = 9;
   private static Map<Integer, ItemBroker.NpcInfo> bWX = new ConcurrentHashMap<>();
   public int[] RARE_ITEMS = new int[]{
      16205, 16206, 16207, 16208, 16209, 16210, 16211, 16212, 16213, 16214, 16215, 16216, 16217, 16218, 16219, 16220, 16304, 16321, 16338, 16355
   };

   private TreeMap<String, TreeMap<Long, ItemBroker.Item>> ah(int var1) {
      Player var2 = this.getSelf();
      NpcInstance var3 = this.getNpc();
      if (var2 != null && var3 != null) {
         this.updateInfo(var2, var3);
         ItemBroker.NpcInfo var4 = bWX.get(this.getNpc().getObjectId());
         if (var4 == null) {
            return null;
         }

         switch (var1) {
            case 1:
               return var4.bestSellItems;
            case 2:
            case 4:
            default:
               return null;
            case 3:
               return var4.bestBuyItems;
            case 5:
               return var4.bestCraftItems;
         }
      } else {
         return null;
      }
   }

   public String DialogAppend_31732(Integer var1) {
      return this.getHtmlAppends(var1);
   }

   public String DialogAppend_31833(Integer var1) {
      return this.getHtmlAppends(var1);
   }

   public String DialogAppend_31838(Integer var1) {
      return this.getHtmlAppends(var1);
   }

   public String DialogAppend_31829(Integer var1) {
      return this.getHtmlAppends(var1);
   }

   public String DialogAppend_31991(Integer var1) {
      return this.getHtmlAppends(var1);
   }

   public String DialogAppend_31805(Integer var1) {
      return this.getHtmlAppends(var1);
   }

   public String getHtmlAppends(Integer var1) {
      if (!Config.ITEM_BROKER_ITEM_SEARCH) {
         return "";
      }

      StringBuilder var2 = new StringBuilder();
      int var3 = 0;
      String var4 = "";
      String var5 = "";
      switch (var1) {
         case 0:
            if (this.getSelf().isLangRus()) {
               var2.append("<br><font color=\"LEVEL\">Поиск торговцев:</font><br1>");
               var2.append("[npc_%objectId%_Chat 11|<font color=\"FF9900\">Список продаваемых товаров</font>]<br1>");
               var2.append("[npc_%objectId%_Chat 13|<font color=\"FF9900\">Список покупаемых товаров</font>]<br1>");
               var2.append("[npc_%objectId%_Chat 15|<font color=\"FF9900\">Список создаваемых товаров</font>]<br1>");
            } else {
               var2.append("<br><font color=\"LEVEL\">Search for dealers:</font><br1>");
               var2.append("[npc_%objectId%_Chat 11|<font color=\"FF9900\">The list of goods for sale</font>]<br1>");
               var2.append("[npc_%objectId%_Chat 13|<font color=\"FF9900\">The list of goods to buy</font>]<br1>");
               var2.append("[npc_%objectId%_Chat 15|<font color=\"FF9900\">The list of goods to craft</font>]<br1>");
            }
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 12:
         case 14:
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 22:
         case 24:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 32:
         case 34:
         default:
            break;
         case 11:
            var3 = 1;
            var4 = "продаваемых";
            var5 = "sell";
            break;
         case 13:
            var3 = 3;
            var4 = "покупаемых";
            var5 = "buy";
            break;
         case 15:
            var3 = 5;
            var4 = "создаваемых";
            var5 = "craft";
            break;
         case 21:
         case 23:
         case 25:
            var3 = var1 - 20;
            if (this.getSelf().isLangRus()) {
               var2.append("!Список снаряжения:<br>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 1 1 0 0|<font color=\"FF9900\">Оружие</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 2 1 0 0|<font color=\"FF9900\">Броня</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 3 1 0 0|<font color=\"FF9900\">Бижутерия</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 4 1 0 0|<font color=\"FF9900\">Украшения</font>]<br1>");
               var2.append("<br>[npc_%objectId%_Chat ").append(10 + var3).append("|<font color=\"FF9900\">Назад</font>]");
            } else {
               var2.append("!The list of equipment:<br>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 1 1 0 0|<font color=\"FF9900\">Weapons</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 2 1 0 0|<font color=\"FF9900\">Armors</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 3 1 0 0|<font color=\"FF9900\">Jewels</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 4 1 0 0|<font color=\"FF9900\">Accessories</font>]<br1>");
               var2.append("<br>[npc_%objectId%_Chat ").append(10 + var3).append("|<font color=\"FF9900\">Back</font>]");
            }

            return var2.toString();
         case 31:
         case 33:
         case 35:
            var3 = var1 - 30;
            if (this.getSelf().isLangRus()) {
               var2.append("!Список снаряжения, заточенного на +4 и выше:<br>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 1 1 4 0|<font color=\"FF9900\">Оружие</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 2 1 4 0|<font color=\"FF9900\">Броня</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 3 1 4 0|<font color=\"FF9900\">Бижутерия</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 4 1 4 0|<font color=\"FF9900\">Украшения</font>]<br1>");
               var2.append("<br>[npc_%objectId%_Chat ").append(10 + var3).append("|<font color=\"FF9900\">Назад</font>]");
            } else {
               var2.append("!The list of equipment, enchanted to +4 and more:<br>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 1 1 4 0|<font color=\"FF9900\">Weapons+</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 2 1 4 0|<font color=\"FF9900\">Armors+</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 3 1 4 0|<font color=\"FF9900\">Jewels+</font>]<br1>");
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 4 1 4 0|<font color=\"FF9900\">Accessories+</font>]<br1>");
               var2.append("<br>[npc_%objectId%_Chat ").append(10 + var3).append("|<font color=\"FF9900\">Back</font>]");
            }

            return var2.toString();
      }

      if (var3 > 0) {
         if (this.getSelf().isLangRus()) {
            var2.append("!Список ").append(var4).append(" товаров:<br>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 0 1 0 0|<font color=\"FF9900\">Весь список</font>]<br1>");
            var2.append("[npc_%objectId%_Chat ").append(var3 + 20).append("|<font color=\"FF9900\">Снаряжение</font>]<br1>");
            if (var3 == 1) {
               var2.append("[npc_%objectId%_Chat ").append(var3 + 30).append("|<font color=\"FF9900\">Снаряжение +4 и выше</font>]<br1>");
            }

            if (var3 != 5) {
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 0 1 0 1|<font color=\"FF9900\">Редкое снаряжение</font>]<br1>");
            }

            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 5 1 0 0|<font color=\"FF9900\">Расходные материалы</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 6 1 0 0|<font color=\"FF9900\">Ингредиенты</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 7 1 0 0|<font color=\"FF9900\">Ключевые ингредиенты</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 8 1 0 0|<font color=\"FF9900\">Рецепты</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 9 1 0 0|<font color=\"FF9900\">Книги и амулеты</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 10 1 0 0|<font color=\"FF9900\">Предметы для улучшения</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 11 1 0 0|<font color=\"FF9900\">Разное</font>]<br1>");
            if (var3 != 5) {
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 90 1 0 0|<font color=\"FF9900\">Стандартные предметы</font>]<br1>");
            }

            var2.append("<edit var=\"tofind\" width=100><br1>");
            var2.append("[scripts_services.ItemBroker:find ").append(var3).append(" 1 $tofind|<font color=\"FF9900\">Найти</font>]<br1>");
            var2.append("<br>[npc_%objectId%_Chat 0|<font color=\"FF9900\">Назад</font>]");
         } else {
            var2.append("!The list of goods to ").append(var5).append(":<br>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 0 1 0 0|<font color=\"FF9900\">List all</font>]<br1>");
            var2.append("[npc_%objectId%_Chat ").append(var3 + 20).append("|<font color=\"FF9900\">Equipment</font>]<br1>");
            if (var3 == 1) {
               var2.append("[npc_%objectId%_Chat ").append(var3 + 30).append("|<font color=\"FF9900\">Equipment +4 and more</font>]<br1>");
            }

            if (var3 != 5) {
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 0 1 0 1|<font color=\"FF9900\">Rare equipment</font>]<br1>");
            }

            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 5 1 0 0|<font color=\"FF9900\">Consumable</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 6 1 0 0|<font color=\"FF9900\">Matherials</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 7 1 0 0|<font color=\"FF9900\">Key matherials</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 8 1 0 0|<font color=\"FF9900\">Recipies</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 9 1 0 0|<font color=\"FF9900\">Books and amulets</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 10 1 0 0|<font color=\"FF9900\">Enchant items</font>]<br1>");
            var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 11 1 0 0|<font color=\"FF9900\">Other</font>]<br1>");
            if (var3 != 5) {
               var2.append("[scripts_services.ItemBroker:list ").append(var3).append(" 90 1 0 0|<font color=\"FF9900\">Commons</font>]<br1>");
            }

            var2.append("<edit var=\"tofind\" width=100><br1>");
            var2.append("[scripts_services.ItemBroker:find ").append(var3).append(" 1 $tofind|<font color=\"FF9900\">Find</font>]<br1>");
            var2.append("<br>[npc_%objectId%_Chat 0|<font color=\"FF9900\">Back</font>]");
         }
      }

      return var2.toString();
   }

   public void list(String[] var1) {
      Player var2 = this.getSelf();
      NpcInstance var3 = this.getNpc();
      if (var2 != null && var3 != null) {
         if (var1.length != 5) {
            show("Некорректная длина данных", var2, var3);
         } else {
            int var4;
            int var5;
            int var6;
            int var7;
            int var8;
            try {
               var4 = Integer.valueOf(var1[0]);
               var5 = Integer.valueOf(var1[1]);
               var6 = Integer.valueOf(var1[2]);
               var7 = Integer.valueOf(var1[3]);
               var8 = Integer.valueOf(var1[4]);
            } catch (Exception var19) {
               show("Некорректные данные", var2, var3);
               return;
            }

            ItemClass var9 = var5 >= ItemClass.values().length ? null : ItemClass.values()[var5];
            TreeMap var10 = this.ah(var4);
            if (var10 == null) {
               show("Ошибка - предметов такого типа не найдено", var2, var3);
            } else {
               ArrayList var11 = new ArrayList(var10.size() * 10);

               for (Object __raw_var13_291 : (Iterable<?>)(Object)(var10.values())) {
                  TreeMap var13 = (TreeMap)__raw_var13_291;
                  TreeMap var14 = new TreeMap();

                  for (Object __raw_var16_294 : (Iterable<?>)(Object)(var13.entrySet())) {
                     Entry var16 = (Entry)__raw_var16_294;
                     ItemBroker.Item var17 = (ItemBroker.Item)var16.getValue();
                     if (var17 != null && var17.enchant >= var7) {
                        ItemTemplate var18 = var17.item != null ? var17.item.getItem() : ItemHolder.getInstance().getTemplate(var17.itemId);
                        if (var18 != null && (var8 <= 0 || var17.rare) && (var9 == null || var9 == ItemClass.ALL || var18.getItemClass() == var9)) {
                           var14.put(var16.getKey(), var17);
                        }
                     }
                  }

                  if (!var14.isEmpty()) {
                     ItemBroker.Item var27 = var4 == 3 ? (ItemBroker.Item)var14.lastEntry().getValue() : (ItemBroker.Item)var14.firstEntry().getValue();
                     if (var27 != null) {
                        var11.add(var27);
                     }
                  }
               }

               StringBuilder var21 = new StringBuilder(200);
               var21.append("[npc_%objectId%_Chat 1");
               var21.append(var4);
               var21.append("|««]&nbsp;&nbsp;");
               int var22 = var11.size();
               var22 = var22 / 10 + (var22 % 10 > 0 ? 1 : 0);
               var22 = Math.max(1, var22);
               var6 = Math.min(var22, Math.max(1, var6));
               if (var22 > 1) {
                  int var25 = Math.max(1, Math.min(var22 - 9 + 1, var6 - 4));
                  if (var25 > 1) {
                     this.a(var21, var4, var5, 1, var7, var8, "1");
                  }

                  if (var6 > 11) {
                     this.a(var21, var4, var5, var6 - 10, var7, var8, String.valueOf(var6 - 10));
                  }

                  if (var6 > 1) {
                     this.a(var21, var4, var5, var6 - 1, var7, var8, "<");
                  }

                  for (int var28 = 0; var28 < 9 && var25 <= var22; var25++) {
                     if (var25 == var6) {
                        var21.append(var25).append("&nbsp;");
                     } else {
                        this.a(var21, var4, var5, var25, var7, var8, String.valueOf(var25));
                     }

                     var28++;
                  }

                  if (var6 < var22) {
                     this.a(var21, var4, var5, var6 + 1, var7, var8, ">");
                  }

                  if (var6 < var22 - 10) {
                     this.a(var21, var4, var5, var6 + 10, var7, var8, String.valueOf(var6 + 10));
                  }

                  if (var25 <= var22) {
                     this.a(var21, var4, var5, var22, var7, var8, String.valueOf(var22));
                  }
               }

               var21.append("<table width=100%>");
               if (var11.size() > 0) {
                  int var26 = 0;
                  ListIterator var29 = var11.listIterator((var6 - 1) * 10);

                  while (var29.hasNext() && var26 < 10) {
                     ItemBroker.Item var30 = (ItemBroker.Item)var29.next();
                     ItemTemplate var31 = var30.item != null ? var30.item.getItem() : ItemHolder.getInstance().getTemplate(var30.itemId);
                     if (var31 != null) {
                        var21.append("<tr><td>");
                        var21.append(var31.getIcon32());
                        var21.append("</td><td><table width=100%><tr><td>[scripts_services.ItemBroker:listForItem ");
                        var21.append(var4);
                        var21.append(" ");
                        var21.append(var30.itemId);
                        var21.append(" ");
                        var21.append(var7);
                        var21.append(" ");
                        var21.append(var8);
                        var21.append(" ");
                        var21.append(var5);
                        var21.append(" 1 ");
                        var21.append(var6);
                        var21.append("|");
                        var21.append(var30.name);
                        var21.append("</td></tr><tr><td>price: ");
                        var21.append(l2.gameserver.utils.Util.formatAdena(var30.price));
                        if (var31.isStackable()) {
                           var21.append(", count: ").append(l2.gameserver.utils.Util.formatAdena(var30.count));
                        }

                        var21.append("</td></tr></table></td></tr>");
                        var26++;
                     }
                  }
               } else if (this.getSelf().isLangRus()) {
                  var21.append("<tr><td colspan=2>Ничего не найдено.</td></tr>");
               } else {
                  var21.append("<tr><td colspan=2>Nothing found.</td></tr>");
               }

               var21.append("</table><br>&nbsp;");
               show(var21.toString(), var2, var3);
            }
         }
      }
   }

   private void a(StringBuilder var1, int var2, int var3, int var4, int var5, int var6, String var7) {
      var1.append("[scripts_services.ItemBroker:list ");
      var1.append(var2);
      var1.append(" ");
      var1.append(var3);
      var1.append(" ");
      var1.append(var4);
      var1.append(" ");
      var1.append(var5);
      var1.append(" ");
      var1.append(var6);
      var1.append("|");
      var1.append(var7);
      var1.append("]&nbsp;");
   }

   public void listForItem(String[] var1) {
      Player var2 = this.getSelf();
      NpcInstance var3 = this.getNpc();
      if (var2 != null && var3 != null) {
         if (var1.length >= 7 && var1.length <= 12) {
            String[] var11 = null;

            int var4;
            int var5;
            int var6;
            int var7;
            int var8;
            int var9;
            int var10;
            try {
               var4 = Integer.valueOf(var1[0]);
               var5 = Integer.valueOf(var1[1]);
               var6 = Integer.valueOf(var1[2]);
               var7 = Integer.valueOf(var1[3]);
               var8 = Integer.valueOf(var1[4]);
               var9 = Integer.valueOf(var1[5]);
               var10 = Integer.valueOf(var1[6]);
               if (var1.length > 7) {
                  var11 = new String[var1.length - 7];
                  System.arraycopy(var1, 7, var11, 0, var11.length);
               }
            } catch (Exception var23) {
               show("Некорректные данные", var2, var3);
               return;
            }

            ItemTemplate var12 = ItemHolder.getInstance().getTemplate(var5);
            if (var12 == null) {
               show("Ошибка - itemId не определен.", var2, var3);
            } else {
               TreeMap var13 = this.ah(var4);
               if (var13 == null) {
                  show("Ошибка - такой тип предмета отсутствует.", var2, var3);
               } else {
                  TreeMap var14 = (TreeMap)var13.get(var12.getName());
                  if (var14 == null) {
                     show("Ошибка - предметов с таким названием не найдено.", var2, var3);
                  } else {
                     StringBuilder var15 = new StringBuilder(200);
                     if (var11 == null) {
                        this.a(var15, var4, var8, var10, var6, var7, "««");
                     } else {
                        this.a(var15, var4, var10, var11, "««");
                     }

                     var15.append("&nbsp;&nbsp;");
                     NavigableMap var16 = var4 == 3 ? var14.descendingMap() : var14;
                     if (var16 == null) {
                        show("Ошибка - ничего не найдено.", var2, var3);
                     } else {
                        ArrayList var17 = new ArrayList(var16.size());

                        for (Object __raw_var19_478 : (Iterable<?>)(Object)(var16.values())) {
                           ItemBroker.Item var19 = (ItemBroker.Item)__raw_var19_478;
                           if (var19 != null && var19.enchant >= var6 && (var7 <= 0 || var19.rare)) {
                              var17.add(var19);
                           }
                        }

                        int var25 = var17.size();
                        var25 = var25 / 10 + (var25 % 10 > 0 ? 1 : 0);
                        var25 = Math.max(1, var25);
                        var9 = Math.min(var25, Math.max(1, var9));
                        if (var25 > 1) {
                           int var28 = Math.max(1, Math.min(var25 - 9 + 1, var9 - 4));
                           if (var28 > 1) {
                              this.a(var15, var4, var5, var6, var7, var8, 1, var10, var11, "1");
                           }

                           if (var9 > 11) {
                              this.a(var15, var4, var5, var6, var7, var8, var9 - 10, var10, var11, String.valueOf(var9 - 10));
                           }

                           if (var9 > 1) {
                              this.a(var15, var4, var5, var6, var7, var8, var9 - 1, var10, var11, "<");
                           }

                           for (int var20 = 0; var20 < 9 && var28 <= var25; var28++) {
                              if (var28 == var9) {
                                 var15.append(var28).append("&nbsp;");
                              } else {
                                 this.a(var15, var4, var5, var6, var7, var8, var28, var10, var11, String.valueOf(var28));
                              }

                              var20++;
                           }

                           if (var9 < var25) {
                              this.a(var15, var4, var5, var6, var7, var8, var9 + 1, var10, var11, ">");
                           }

                           if (var9 < var25 - 10) {
                              this.a(var15, var4, var5, var6, var7, var8, var9 + 10, var10, var11, String.valueOf(var9 + 10));
                           }

                           if (var28 <= var25) {
                              this.a(var15, var4, var5, var6, var7, var8, var25, var10, var11, String.valueOf(var25));
                           }
                        }

                        var15.append("<table width=100%>");
                        if (var17.size() > 0) {
                           int var29 = 0;
                           ListIterator var30 = var17.listIterator((var9 - 1) * 10);

                           while (var30.hasNext() && var29 < 10) {
                              ItemBroker.Item var21 = (ItemBroker.Item)var30.next();
                              ItemTemplate var22 = var21.item != null ? var21.item.getItem() : ItemHolder.getInstance().getTemplate(var21.itemId);
                              if (var22 != null) {
                                 var15.append("<tr><td>");
                                 var15.append(var22.getIcon32());
                                 var15.append("</td><td><table width=100%><tr><td>[scripts_services.ItemBroker:path ");
                                 var15.append(var4);
                                 var15.append(" ");
                                 var15.append(var21.itemId);
                                 var15.append(" ");
                                 var15.append(var21.itemObjId);
                                 var15.append("|");
                                 var15.append(var21.name);
                                 var15.append("</td></tr><tr><td>price: ");
                                 var15.append(l2.gameserver.utils.Util.formatAdena(var21.price));
                                 if (var22.isStackable()) {
                                    var15.append(", count: ").append(l2.gameserver.utils.Util.formatAdena(var21.count));
                                 }

                                 var15.append(", owner: ").append(var21.merchantName);
                                 var15.append("</td></tr></table></td></tr>");
                                 var29++;
                              }
                           }
                        } else if (this.getSelf().isLangRus()) {
                           var15.append("<tr><td colspan=2>Ничего не найдено.</td></tr>");
                        } else {
                           var15.append("<tr><td colspan=2>Nothing found.</td></tr>");
                        }

                        var15.append("</table><br>&nbsp;");
                        show(var15.toString(), var2, var3);
                     }
                  }
               }
            }
         } else {
            show("Некорректная длина данных", var2, var3);
         }
      }
   }

   private void a(StringBuilder var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, String[] var9, String var10) {
      var1.append("[scripts_services.ItemBroker:listForItem ");
      var1.append(var2);
      var1.append(" ");
      var1.append(var3);
      var1.append(" ");
      var1.append(var4);
      var1.append(" ");
      var1.append(var5);
      var1.append(" ");
      var1.append(var6);
      var1.append(" ");
      var1.append(var7);
      var1.append(" ");
      var1.append(var8);
      if (var9 != null) {
         for (int var11 = 0; var11 < var9.length; var11++) {
            var1.append(" ");
            var1.append(var9[var11]);
         }
      }

      var1.append("|");
      var1.append(var10);
      var1.append("]&nbsp;");
   }

   public void path(String[] var1) {
      Player var2 = this.getSelf();
      NpcInstance var3 = this.getNpc();
      if (var2 != null && var3 != null) {
         if (var1.length != 3) {
            show("Некорректная длина данных", var2, var3);
         } else {
            int var4;
            int var5;
            int var6;
            try {
               var4 = Integer.valueOf(var1[0]);
               var5 = Integer.valueOf(var1[1]);
               var6 = Integer.valueOf(var1[2]);
            } catch (Exception var15) {
               show("Некорректные данные", var2, var3);
               return;
            }

            ItemTemplate var7 = ItemHolder.getInstance().getTemplate(var5);
            if (var7 == null) {
               show("Ошибка - itemId не определен.", var2, var3);
            } else {
               TreeMap var8 = this.ah(var4);
               if (var8 == null) {
                  show("Ошибка - предметов такого типа не найдено.", var2, var3);
               } else {
                  TreeMap var9 = (TreeMap)var8.get(var7.getName());
                  if (var9 == null) {
                     show("Ошибка - предметов с таким именем не найдено.", var2, var3);
                  } else {
                     ItemBroker.Item var10 = null;

                     for (Object __raw_var12_633 : (Iterable<?>)(Object)(var9.values())) {
                        ItemBroker.Item var12 = (ItemBroker.Item)__raw_var12_633;
                        if (var12.itemObjId == var6) {
                           var10 = var12;
                           break;
                        }
                     }

                     if (var10 == null) {
                        show("Ошибка - предмет не найден.", var2, var3);
                     } else {
                        boolean var16 = false;
                        Player var17 = GameObjectsStorage.getAsPlayer(var10.merchantStoredId);
                        if (var17 == null) {
                           show("Торговец не найден, возможно он вышел из игры.", var2, var3);
                        } else {
                           switch (var4) {
                              case 1:
                                 if (var17.getSellList() != null) {
                                    for (TradeItem var20 : var17.getSellList()) {
                                       if (var20.getItemId() == var10.itemId && var20.getOwnersPrice() == var10.price) {
                                          var16 = true;
                                          break;
                                       }
                                    }
                                 }
                              case 2:
                              case 4:
                              default:
                                 break;
                              case 3:
                                 if (var17.getBuyList() != null) {
                                    for (TradeItem var14 : var17.getBuyList()) {
                                       if (var14.getItemId() == var10.itemId && var14.getOwnersPrice() == var10.price) {
                                          var16 = true;
                                          break;
                                       }
                                    }
                                 }
                                 break;
                              case 5:
                                 var16 = true;
                           }

                           if (!var16) {
                              if (var2.isLangRus()) {
                                 show("Внимание, цена или предмет изменились, будьте осторожны !", var2, var3);
                              } else {
                                 show("Caution, price or item was changed, please be careful !", var2, var3);
                              }
                           }

                           RadarControl var19 = new RadarControl(0, 1, var10.player);
                           var2.sendPacket(var19);
                           if (var2.getVarB("notraders")) {
                              var2.sendPacket(new CharInfo(var17));
                              if (var17.getPrivateStoreType() == 3) {
                                 var2.sendPacket(new PrivateStoreMsgBuy(var17));
                              } else if (var17.getPrivateStoreType() == 1 || var17.getPrivateStoreType() == 8) {
                                 var2.sendPacket(new PrivateStoreMsgSell(var17));
                              } else if (var17.getPrivateStoreType() == 5) {
                                 var2.sendPacket(new RecipeShopMsg(var17));
                              }
                           }

                           var2.setTarget(var17);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void updateInfo(Player var1, NpcInstance var2) {
      ItemBroker.NpcInfo var3 = bWX.get(var2.getObjectId());
      if (var3 == null || var3.lastUpdate < System.currentTimeMillis() - Config.ITEM_BROKER_UPDATE_TIME) {
         var3 = new ItemBroker.NpcInfo();
         var3.lastUpdate = System.currentTimeMillis();
         var3.bestBuyItems = new TreeMap<>();
         var3.bestSellItems = new TreeMap<>();
         var3.bestCraftItems = new TreeMap<>();
         int var4 = 0;

         for (Player var6 : World.getAroundPlayers(var2, 4000, 400)) {
            int var7 = var6.getPrivateStoreType();
            if (var7 == 1 || var7 == 3 || var7 == 5) {
               TreeMap var8 = null;
               Object var9 = null;
               switch (var7) {
                  case 1:
                     var8 = var3.bestSellItems;

                     for (TradeItem var31 : var6.getSellList()) {
                        ItemTemplate var33 = var31.getItem();
                        if (var33 != null) {
                           TreeMap var35 = (TreeMap)var8.get(var33.getName());
                           if (var35 == null) {
                              var35 = new TreeMap();
                              var8.put(var33.getName(), var35);
                           }

                           ItemBroker.Item var37 = new ItemBroker.Item(
                              var31.getItemId(),
                              var7,
                              var31.getOwnersPrice(),
                              var31.getCount(),
                              var31.getEnchantLevel(),
                              var33.getName(),
                              var6.getStoredId(),
                              var6.getName(),
                              var6.getLoc(),
                              var31.getObjectId(),
                              var31
                           );
                           long var39 = var37.price * 100L;

                           while (var39 < var37.price * 100L + 100L && var35.containsKey(var39)) {
                              var39++;
                           }

                           var35.put(var39, var37);
                        }
                     }
                  case 2:
                  case 4:
                  default:
                     break;
                  case 3:
                     var8 = var3.bestBuyItems;

                     for (TradeItem var30 : var6.getBuyList()) {
                        ItemTemplate var32 = var30.getItem();
                        if (var32 != null) {
                           TreeMap var34 = (TreeMap)var8.get(var32.getName());
                           if (var34 == null) {
                              var34 = new TreeMap();
                              var8.put(var32.getName(), var34);
                           }

                           ItemBroker.Item var36 = new ItemBroker.Item(
                              var30.getItemId(),
                              var7,
                              var30.getOwnersPrice(),
                              var30.getCount(),
                              var30.getEnchantLevel(),
                              var32.getName(),
                              var6.getStoredId(),
                              var6.getName(),
                              var6.getLoc(),
                              var4++,
                              var30
                           );
                           long var38 = var36.price * 100L;

                           while (var38 < var36.price * 100L + 100L && var34.containsKey(var38)) {
                              var38++;
                           }

                           var34.put(var38, var36);
                        }
                     }
                     break;
                  case 5:
                     var8 = var3.bestCraftItems;
                     List var10 = var6.getCreateList();
                     if (var10 != null) {
                        for (Object __raw_var12_800 : (Iterable<?>)(Object)(var10)) {
                           ManufactureItem var12 = (ManufactureItem)__raw_var12_800;
                           int var13 = var12.getRecipeId();
                           Recipe var14 = RecipeHolder.getInstance().getRecipeById(var13);
                           if (var14 != null) {
                              for (Pair var16 : var14.getProducts()) {
                                 ItemTemplate var17 = (ItemTemplate)var16.getKey();
                                 if (var17 != null) {
                                    TreeMap var18 = (TreeMap)var8.get(var17.getName());
                                    if (var18 == null) {
                                       var18 = new TreeMap();
                                       var8.put(var17.getName(), var18);
                                    }

                                    ItemBroker.Item var19 = new ItemBroker.Item(
                                       ((ItemTemplate)var16.getKey()).getItemId(),
                                       var7,
                                       var12.getCost(),
                                       (Long)((Pair)var16.getValue()).getLeft(),
                                       0,
                                       var17.getName(),
                                       var6.getStoredId(),
                                       var6.getName(),
                                       var6.getLoc(),
                                       var4++,
                                       null
                                    );
                                    long var20 = var19.price * 100L;

                                    while (var20 < var19.price * 100L + 100L && var18.containsKey(var20)) {
                                       var20++;
                                    }

                                    var18.put(var20, var19);
                                 }
                              }
                           }
                        }
                     }
               }
            }
         }

         bWX.put(var2.getObjectId(), var3);
      }
   }

   public void find(String[] var1) {
      Player var2 = this.getSelf();
      NpcInstance var3 = this.getNpc();
      if (var2 != null && var3 != null) {
         if (var1.length >= 3 && var1.length <= 7) {
            int var6 = 0;
            String[] var7 = null;

            int var4;
            int var5;
            try {
               var4 = Integer.valueOf(var1[0]);
               var5 = Integer.valueOf(var1[1]);
               var7 = new String[var1.length - 2];

               for (int var9 = 0; var9 < var7.length; var9++) {
                  String var8 = var1[var9 + 2].trim().toLowerCase();
                  var7[var9] = var8;
                  if (var8.length() > 1 && var8.startsWith("+")) {
                     var6 = Integer.valueOf(var8.substring(1));
                  }
               }
            } catch (Exception var19) {
               show("Некорректные данные", var2, var3);
               return;
            }

            TreeMap var22 = this.ah(var4);
            if (var22 == null) {
               show("Ошибка - предметов с таким типом не найдено.", var2, var3);
            } else {
               ArrayList var23 = new ArrayList();

               label163:
               for (Object __raw_var14_880 : (Iterable<?>)(Object)(var22.entrySet())) {
                  Entry var14 = (Entry)__raw_var14_880;
                  for (int var15 = 0; var15 < var7.length; var15++) {
                     String var10 = var7[var15];
                     if (!var10.startsWith("+") && ((String)var14.getKey()).toLowerCase().indexOf(var10) == -1) {
                        continue label163;
                     }
                  }

                  TreeMap var11 = (TreeMap)var14.getValue();
                  ItemBroker.Item var12 = null;

                  for (Object __raw_var16_891 : (Iterable<?>)(Object)(var11.values())) {
                     ItemBroker.Item var16 = (ItemBroker.Item)__raw_var16_891;
                     if (var16 != null && var16.enchant >= var6) {
                        var12 = var16;
                        break;
                     }
                  }

                  if (var12 != null) {
                     var23.add(var12);
                  }
               }

               StringBuilder var25 = new StringBuilder(200);
               var25.append("[npc_%objectId%_Chat 1");
               var25.append(var4);
               var25.append("|««]&nbsp;&nbsp;");
               int var26 = var23.size();
               var26 = var26 / 10 + (var26 % 10 > 0 ? 1 : 0);
               var26 = Math.max(1, var26);
               var5 = Math.min(var26, Math.max(1, var5));
               if (var26 > 1) {
                  int var30 = Math.max(1, Math.min(var26 - 9 + 1, var5 - 4));
                  if (var30 > 1) {
                     this.a(var25, var4, 1, var7, "1");
                  }

                  if (var5 > 11) {
                     this.a(var25, var4, var5 - 10, var7, String.valueOf(var5 - 10));
                  }

                  if (var5 > 1) {
                     this.a(var25, var4, var5 - 1, var7, "<");
                  }

                  for (int var32 = 0; var32 < 9 && var30 <= var26; var30++) {
                     if (var30 == var5) {
                        var25.append(var30).append("&nbsp;");
                     } else {
                        this.a(var25, var4, var30, var7, String.valueOf(var30));
                     }

                     var32++;
                  }

                  if (var5 < var26) {
                     this.a(var25, var4, var5 + 1, var7, ">");
                  }

                  if (var5 < var26 - 10) {
                     this.a(var25, var4, var5 + 10, var7, String.valueOf(var5 + 10));
                  }

                  if (var30 <= var26) {
                     this.a(var25, var4, var26, var7, String.valueOf(var26));
                  }
               }

               var25.append("<table width=100%>");
               if (var23.size() > 0) {
                  int var31 = 0;
                  ListIterator var33 = var23.listIterator((var5 - 1) * 10);

                  while (var33.hasNext() && var31 < 10) {
                     ItemBroker.Item var24 = (ItemBroker.Item)var33.next();
                     ItemTemplate var17 = var24.item != null ? var24.item.getItem() : ItemHolder.getInstance().getTemplate(var24.itemId);
                     if (var17 != null) {
                        var25.append("<tr><td>");
                        var25.append(var17.getIcon32());
                        var25.append("</td><td><table width=100%><tr><td>[scripts_services.ItemBroker:listForItem ");
                        var25.append(var4);
                        var25.append(" ");
                        var25.append(var24.itemId);
                        var25.append(" ");
                        var25.append(var6);
                        var25.append(" 0 0 1 ");
                        var25.append(var5);
                        if (var7 != null) {
                           for (int var18 = 0; var18 < var7.length; var18++) {
                              var25.append(" ");
                              var25.append(var7[var18]);
                           }
                        }

                        var25.append("|");
                        var25.append("<font color=\"LEVEL\">");
                        var25.append(var17.getName());
                        var25.append("</font>]");
                        var25.append("</td></tr>");
                        var25.append("</table></td></tr>");
                        var31++;
                     }
                  }
               } else if (this.getSelf().isLangRus()) {
                  var25.append("<tr><td colspan=2>Ничего не найдено.</td></tr>");
               } else {
                  var25.append("<tr><td colspan=2>Nothing found.</td></tr>");
               }

               var25.append("</table><br>&nbsp;");
               show(var25.toString(), var2, var3);
            }
         } else {
            if (var2.isLangRus()) {
               show("Пожалуйста введите от 1 до 16 символов.<br>[npc_%objectId%_Chat 0|<font color=\"FF9900\">Назад</font>]", var2, var3);
            } else {
               show("Please enter from 1 up to 16 symbols.<br>[npc_%objectId%_Chat 0|<font color=\"FF9900\">Back</font>]", var2, var3);
            }
         }
      }
   }

   private void a(StringBuilder var1, int var2, int var3, String[] var4, String var5) {
      var1.append("[scripts_services.ItemBroker:find ");
      var1.append(var2);
      var1.append(" ");
      var1.append(var3);
      if (var4 != null) {
         for (int var6 = 0; var6 < var4.length; var6++) {
            var1.append(" ");
            var1.append(var4[var6]);
         }
      }

      var1.append("|");
      var1.append(var5);
      var1.append("]&nbsp;");
   }

   public class Item {
      public final int itemId;
      public final int itemObjId;
      public final int type;
      public final long price;
      public final long count;
      public final int enchant;
      public final boolean rare;
      public final long merchantStoredId;
      public final String name;
      public final String merchantName;
      public final Location player;
      public final TradeItem item;

      public Item(int var2, int var3, long var4, long var6, int var8, String var9, long var10, String var12, Location var13, int var14, TradeItem var15) {
         this.itemId = var2;
         this.type = var3;
         this.price = var4;
         this.count = var6;
         this.enchant = var8;
         this.rare = ArrayUtils.contains(ItemBroker.this.RARE_ITEMS, var2);
         StringBuilder var16 = new StringBuilder(70);
         if (var8 > 0) {
            if (this.rare) {
               var16.append("<font color=\"FF0000\">+");
            } else {
               var16.append("<font color=\"7CFC00\">+");
            }

            var16.append(var8);
            var16.append(" ");
         } else if (this.rare) {
            var16.append("<font color=\"0000FF\">Rare ");
         } else {
            var16.append("<font color=\"LEVEL\">");
         }

         var16.append(var9);
         var16.append("</font>]");
         if (var15 != null) {
            if (var15.getAttackElement() != Element.NONE.getId()) {
               var16.append(" &nbsp;<font color=\"7CFC00\">+");
               var16.append(var15.getAttackElementValue());
               switch (var15.getAttackElement()) {
                  case 0:
                     var16.append(" Fire");
                     break;
                  case 1:
                     var16.append(" Water");
                     break;
                  case 2:
                     var16.append(" Wind");
                     break;
                  case 3:
                     var16.append(" Earth");
                     break;
                  case 4:
                     var16.append(" Holy");
                     break;
                  case 5:
                     var16.append(" Unholy");
               }

               var16.append("</font>");
            } else {
               int var17 = var15.getDefenceFire();
               int var18 = var15.getDefenceWater();
               int var19 = var15.getDefenceWind();
               int var20 = var15.getDefenceEarth();
               int var21 = var15.getDefenceHoly();
               int var22 = var15.getDefenceUnholy();
               if (var17 + var18 + var19 + var20 + var21 + var22 > 0) {
                  var16.append("&nbsp;<font color=\"7CFC00\">");
                  if (var17 > 0) {
                     var16.append("+");
                     var16.append(var17);
                     var16.append(" Fire ");
                  }

                  if (var18 > 0) {
                     var16.append("+");
                     var16.append(var18);
                     var16.append(" Water ");
                  }

                  if (var19 > 0) {
                     var16.append("+");
                     var16.append(var19);
                     var16.append(" Wind ");
                  }

                  if (var20 > 0) {
                     var16.append("+");
                     var16.append(var20);
                     var16.append(" Earth ");
                  }

                  if (var21 > 0) {
                     var16.append("+");
                     var16.append(var21);
                     var16.append(" Holy ");
                  }

                  if (var22 > 0) {
                     var16.append("+");
                     var16.append(var22);
                     var16.append(" Unholy ");
                  }

                  var16.append("</font>");
               }
            }
         }

         this.name = var16.toString();
         this.merchantStoredId = var10;
         this.merchantName = var12;
         this.player = var13;
         this.itemObjId = var14;
         this.item = var15;
      }
   }

   public class NpcInfo {
      public long lastUpdate;
      public TreeMap<String, TreeMap<Long, ItemBroker.Item>> bestSellItems;
      public TreeMap<String, TreeMap<Long, ItemBroker.Item>> bestBuyItems;
      public TreeMap<String, TreeMap<Long, ItemBroker.Item>> bestCraftItems;
   }
}

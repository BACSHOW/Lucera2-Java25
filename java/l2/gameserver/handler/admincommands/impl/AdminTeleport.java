package l2.gameserver.handler.admincommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import l2.commons.dbutils.DbUtils;
import l2.commons.lang.ArrayUtils;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.utils.Location;
import l2.gameserver.utils.Util;

public class AdminTeleport implements IAdminCommandHandler {
   @Override
   public boolean useAdminCommand(Enum var1, String[] var2, String var3, Player var4) {
      AdminTeleport.Commands var5 = (AdminTeleport.Commands)var1;
      if (!var4.getPlayerAccess().CanTeleport) {
         return false;
      }

      switch (var5) {
         case admin_show_moves:
            var4.sendPacket(new NpcHtmlMessage(5).setFile("admin/teleports.htm"));
            break;
         case admin_show_moves_other:
            var4.sendPacket(new NpcHtmlMessage(5).setFile("admin/tele/other.htm"));
            break;
         case admin_show_teleport:
            this.I(var4);
            break;
         case admin_teleport_to_character:
            this.a(var4, var4.getTarget());
            break;
         case admin_teleport_to:
         case admin_teleportto:
            if (var2.length < 2) {
               var4.sendMessage("USAGE: //teleportto charName");
               return false;
            }

            String var6 = Util.joinStrings(" ", var2, 1);
            Player var7 = GameObjectsStorage.getPlayer(var6);
            if (var7 == null) {
               var4.sendMessage("Player '" + var6 + "' not found in world");
               return false;
            }

            this.a(var4, var7);
            break;
         case admin_move_to:
         case admin_moveto:
         case admin_teleport:
            if (var2.length < 2) {
               var4.sendMessage("USAGE: //teleport x y z [ref]");
               return false;
            }

            this.a(
               var4,
               var4,
               Util.joinStrings(" ", var2, 1, 3),
               ArrayUtils.valid(var2, 4) != null && !ArrayUtils.valid(var2, 4).isEmpty() ? Integer.parseInt(var2[4]) : 0
            );
            break;
         case admin_walk:
            if (var2.length < 2) {
               var4.sendMessage("USAGE: //walk x y z");
               return false;
            }

            try {
               var4.moveToLocation(Location.parseLoc(Util.joinStrings(" ", var2, 1)), 0, true);
               break;
            } catch (IllegalArgumentException var17) {
               var4.sendMessage("USAGE: //walk x y z");
               return false;
            }
         case admin_gonorth:
         case admin_gosouth:
         case admin_goeast:
         case admin_gowest:
         case admin_goup:
         case admin_godown:
            int var8 = var2.length < 2 ? 150 : Integer.parseInt(var2[1]);
            int var9 = var4.getX();
            int var10 = var4.getY();
            int var11 = var4.getZ();
            if (var5 == AdminTeleport.Commands.admin_goup) {
               var11 += var8;
            } else if (var5 == AdminTeleport.Commands.admin_godown) {
               var11 -= var8;
            } else if (var5 == AdminTeleport.Commands.admin_goeast) {
               var9 += var8;
            } else if (var5 == AdminTeleport.Commands.admin_gowest) {
               var9 -= var8;
            } else if (var5 == AdminTeleport.Commands.admin_gosouth) {
               var10 += var8;
            } else if (var5 == AdminTeleport.Commands.admin_gonorth) {
               var10 -= var8;
            }

            var4.teleToLocation(var9, var10, var11);
            this.H(var4);
            break;
         case admin_tele:
            this.H(var4);
            break;
         case admin_teleto:
         case admin_tele_to:
         case admin_instant_move:
            if (var2.length > 1 && var2[1].equalsIgnoreCase("r")) {
               var4.setTeleMode(2);
            } else if (var2.length > 1 && var2[1].equalsIgnoreCase("end")) {
               var4.setTeleMode(0);
            } else {
               var4.setTeleMode(1);
            }
            break;
         case admin_tonpc:
         case admin_to_npc:
            if (var2.length < 2) {
               var4.sendMessage("USAGE: //tonpc npcId|npcName");
               return false;
            }

            String var12 = Util.joinStrings(" ", var2, 1);

            try {
               NpcInstance var13;
               if ((var13 = GameObjectsStorage.getByNpcId(Integer.parseInt(var12))) != null) {
                  this.a(var4, var13);
                  return true;
               }
            } catch (Exception var16) {
            }

            NpcInstance var31;
            if ((var31 = GameObjectsStorage.getNpc(var12)) != null) {
               this.a(var4, var31);
               return true;
            }

            var4.sendMessage("Npc " + var12 + " not found");
            break;
         case admin_toobject:
            if (var2.length < 2) {
               var4.sendMessage("USAGE: //toobject objectId");
               return false;
            }

            Integer var14 = Integer.parseInt(var2[1]);
            GameObject var15;
            if ((var15 = GameObjectsStorage.findObject(var14)) != null) {
               this.a(var4, var15);
               return true;
            }

            var4.sendMessage("Object " + var14 + " not found");
      }

      if (!var4.getPlayerAccess().CanEditChar) {
         return false;
      }

      switch (var5) {
         case admin_teleport_character:
            if (var2.length < 2) {
               var4.sendMessage("USAGE: //teleport_character x y z");
               return false;
            }

            this.j(var4, Util.joinStrings(" ", var2, 1));
            this.I(var4);
            break;
         case admin_recall:
            if (var2.length < 2) {
               var4.sendMessage("USAGE: //recall charName");
               return false;
            }

            String var18 = Util.joinStrings(" ", var2, 1);
            Player var19 = GameObjectsStorage.getPlayer(var18);
            if (var19 != null) {
               this.a(var4, var19, var4.getLoc(), var4.getReflectionId());
               return true;
            }

            int var20 = CharacterDAO.getInstance().getObjectIdByName(var18);
            if (var20 > 0) {
               this.a(var20, var4.getLoc());
               var4.sendMessage(var18 + " is offline. Offline teleport used...");
            } else {
               var4.sendMessage("->" + var18 + "<- is incorrect.");
            }
            break;
         case admin_sendhome:
            if (var2.length < 2) {
               var4.sendMessage("USAGE: //sendhome");
               return false;
            }

            this.i(var4, Util.joinStrings(" ", var2, 1));
            break;
         case admin_setref:
            if (var2.length < 2) {
               var4.sendMessage("Usage: //setref <reflection>");
               return false;
            }

            int var22 = Integer.parseInt(var2[1]);
            if (var22 != 0 && ReflectionManager.getInstance().get(var22) == null) {
               var4.sendMessage("Reflection <" + var22 + "> not found.");
               return false;
            }

            GameObject var25 = var4;
            GameObject var28 = var4.getTarget();
            if (var28 != null) {
               var25 = var28;
            }

            var25.setReflection(var22);
            var25.decayMe();
            var25.spawnMe();
            break;
         case admin_getref:
            if (var2.length < 2) {
               var4.sendMessage("Usage: //getref <char_name>");
               return false;
            }

            Player var21 = GameObjectsStorage.getPlayer(var2[1]);
            if (var21 == null) {
               var4.sendMessage("Player '" + var2[1] + "' not found in world");
               return false;
            }

            var4.sendMessage("Player '" + var2[1] + "' in reflection: " + var4.getReflectionId() + ", name: " + var4.getReflection().getName());
            break;
         case admin_recall_party:
            if (var2.length < 2) {
               var4.sendMessage("Usage: //recall_party <party_leader_name>");
               return false;
            }

            Player var24 = GameObjectsStorage.getPlayer(var2[1]);
            if (var24 == null) {
               var4.sendMessage("Player '" + var2[1] + "' not found in world");
               return false;
            }

            Party var27 = var24.getParty();
            if (var27 == null) {
               var4.sendMessage("Player '" + var2[1] + "' have no party.");
               return false;
            }

            for (Player var33 : var27.getPartyMembers()) {
               Location var34 = Location.findPointToStay(var4, 128);
               var33.teleToLocation(var34, var4.getReflection());
               var33.sendMessage("Your party have been recalled by Admin.");
            }

            var4.sendMessage("Party recalled.");
            break;
         case admin_escape_party:
            if (var2.length < 2) {
               var4.sendMessage("Usage: //escape_party <party_leader_name>");
               return false;
            }

            Player var23 = GameObjectsStorage.getPlayer(var2[1]);
            if (var23 == null) {
               var4.sendMessage("Player '" + var2[1] + "' not found in world");
               return false;
            }

            Party var26 = var23.getParty();
            if (var26 == null) {
               var4.sendMessage("Player '" + var2[1] + "' have no party.");
               return false;
            }

            for (Player var32 : var26.getPartyMembers()) {
               var32.teleToClosestTown();
            }

            var4.sendMessage("Party moved to near town.");
      }

      if (!var4.getPlayerAccess().CanEditNPC) {
         return false;
      }

      switch (var5) {
         case admin_recall_npc:
            this.J(var4);
         default:
            return true;
      }
   }

   @Override
   public Enum[] getAdminCommandEnum() {
      return AdminTeleport.Commands.values();
   }

   private void H(Player var1) {
      NpcHtmlMessage var2 = new NpcHtmlMessage(5);
      StringBuilder var3 = new StringBuilder("<html><title>Teleport Menu</title>");
      var3.append("<body>");
      var3.append("<br>");
      var3.append("<center><table>");
      var3.append("<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var3.append("<td><button value=\"North\" action=\"bypass -h admin_gonorth\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var3.append("<td><button value=\"Up\" action=\"bypass -h admin_goup\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
      var3.append("<tr><td><button value=\"West\" action=\"bypass -h admin_gowest\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var3.append("<td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var3.append("<td><button value=\"East\" action=\"bypass -h admin_goeast\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
      var3.append("<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var3.append("<td><button value=\"South\" action=\"bypass -h admin_gosouth\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td>");
      var3.append("<td><button value=\"Down\" action=\"bypass -h admin_godown\" width=70 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr>");
      var3.append("</table></center>");
      var3.append("</body></html>");
      var2.setHtml(var3.toString());
      var1.sendPacket(var2);
   }

   private void I(Player var1) {
      GameObject var2 = var1.getTarget();
      Player var3 = null;
      if (var2.isPlayer()) {
         var3 = (Player)var2;
         NpcHtmlMessage var4 = new NpcHtmlMessage(5);
         StringBuilder var5 = new StringBuilder("<html><title>Teleport Character</title>");
         var5.append("<body>");
         var5.append("The character you will teleport is " + var3.getName() + ".");
         var5.append("<br>");
         var5.append("Co-ordinate x");
         var5.append("<edit var=\"char_cord_x\" width=110>");
         var5.append("Co-ordinate y");
         var5.append("<edit var=\"char_cord_y\" width=110>");
         var5.append("Co-ordinate z");
         var5.append("<edit var=\"char_cord_z\" width=110>");
         var5.append(
            "<button value=\"Teleport\" action=\"bypass -h admin_teleport_character $char_cord_x $char_cord_y $char_cord_z\" width=60 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\">"
         );
         var5.append(
            "<button value=\"Teleport near you\" action=\"bypass -h admin_teleport_character "
               + var1.getX()
               + " "
               + var1.getY()
               + " "
               + var1.getZ()
               + "\" width=115 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\">"
         );
         var5.append(
            "<center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui94\"></center>"
         );
         var5.append("</body></html>");
         var4.setHtml(var5.toString());
         var1.sendPacket(var4);
      } else {
         var1.sendPacket(Msg.INVALID_TARGET);
      }
   }

   private void a(Player var1, Player var2, String var3, int var4) {
      try {
         this.a(var1, var2, Location.parseLoc(var3), var4);
      } catch (IllegalArgumentException var6) {
         var1.sendMessage("You must define 3 coordinates required to teleport");
      }
   }

   private void i(Player var1, String var2) {
      Player var3 = GameObjectsStorage.getPlayer(var2);
      if (var3 != null) {
         var3.teleToClosestTown();
         var3.sendMessage("The GM has sent you to the nearest town");
         var1.sendMessage("You have sent " + var2 + " to the nearest town");
      } else {
         var1.sendPacket(Msg.INVALID_TARGET);
      }
   }

   private void a(Player var1, Player var2, Location var3, int var4) {
      if (!var2.equals(var1)) {
         var2.sendMessage("Admin is teleporting you.");
      }

      var2.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
      var2.teleToLocation(var3, var4);
      if (var2.equals(var1)) {
         var1.sendMessage("You have been teleported to " + var3 + ", reflection id: " + var4);
      }
   }

   private void j(Player var1, String var2) {
      GameObject var3 = var1.getTarget();
      if (var3 != null && var3.isPlayer()) {
         if (var3.getObjectId() == var1.getObjectId()) {
            var1.sendMessage("You cannot teleport yourself.");
         } else {
            this.a(var1, (Player)var3, var2, var1.getReflectionId());
         }
      } else {
         var1.sendPacket(Msg.INVALID_TARGET);
      }
   }

   private void a(int var1, Location var2) {
      if (var1 != 0) {
         Connection var3 = null;
         PreparedStatement var4 = null;

         try {
            var3 = DatabaseFactory.getInstance().getConnection();
            var4 = var3.prepareStatement("UPDATE `characters` SET `x`=?,`y`=?,`z`=? WHERE `obj_Id`=? LIMIT 1");
            var4.setInt(1, var2.x);
            var4.setInt(2, var2.y);
            var4.setInt(3, var2.z);
            var4.setInt(4, var1);
            var4.executeUpdate();
         } catch (Exception var9) {
         } finally {
            DbUtils.closeQuietly(var3, var4);
         }
      }
   }

   private void a(Player var1, GameObject var2) {
      if (var2 != null) {
         var1.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
         var1.teleToLocation(var2.getLoc(), var2.getReflectionId());
         var1.sendMessage("You have teleported to " + var2);
      }
   }

   private void J(Player var1) {
      GameObject var2 = var1.getTarget();
      if (var2 != null && var2.isNpc()) {
         var2.setLoc(var1.getLoc());
         ((NpcInstance)var2).broadcastCharInfo();
         var1.sendMessage("You teleported npc " + var2.getName() + " to " + var1.getLoc().toString() + ".");
      } else {
         var1.sendMessage("Target is't npc.");
      }
   }

   private enum Commands {
      admin_show_moves,
      admin_show_moves_other,
      admin_show_teleport,
      admin_teleport_to_character,
      admin_teleportto,
      admin_teleport_to,
      admin_move_to,
      admin_moveto,
      admin_teleport,
      admin_teleport_character,
      admin_recall,
      admin_walk,
      admin_recall_npc,
      admin_gonorth,
      admin_gosouth,
      admin_goeast,
      admin_gowest,
      admin_goup,
      admin_godown,
      admin_tele,
      admin_teleto,
      admin_tele_to,
      admin_instant_move,
      admin_tonpc,
      admin_to_npc,
      admin_toobject,
      admin_setref,
      admin_getref,
      admin_recall_party,
      admin_sendhome,
      admin_escape_party;
   }
}

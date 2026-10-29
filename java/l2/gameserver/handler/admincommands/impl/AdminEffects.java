package l2.gameserver.handler.admincommands.impl;

import java.util.List;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.model.base.InvisibleType;
import l2.gameserver.network.l2.s2c.Earthquake;
import l2.gameserver.network.l2.s2c.ExRedSky;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Util;

public class AdminEffects implements IAdminCommandHandler {
   @Override
   public boolean useAdminCommand(Enum var1, String[] var2, String var3, Player var4) {
      AdminEffects.Commands var5 = (AdminEffects.Commands)var1;
      if (!var4.getPlayerAccess().GodMode) {
         return false;
      }

      AbnormalEffect var7 = AbnormalEffect.NULL;
      GameObject var8 = var4.getTarget();
      switch (var5) {
         case admin_invis:
         case admin_vis:
            if (var4.isInvisible()) {
               var4.setInvisibleType(InvisibleType.NONE);
               var4.stopAbnormalEffect(AbnormalEffect.STEALTH);
               var4.broadcastCharInfo();
               if (var4.getPet() != null) {
                  var4.getPet().broadcastCharInfo();
               }

               var4.setVar("gm_vis", "true", -1L);
            } else {
               var4.setInvisibleType(InvisibleType.NORMAL);
               var4.startAbnormalEffect(AbnormalEffect.STEALTH);
               var4.sendUserInfo(true);
               World.removeObjectFromPlayers(var4);
               var4.unsetVar("gm_vis");
            }
            break;
         case admin_gmspeed:
            int var6;
            if (var2.length < 2) {
               var6 = 0;
            } else {
               try {
                  var6 = Integer.parseInt(var2[1]);
               } catch (Exception var18) {
                  var4.sendMessage("USAGE: //gmspeed value=[0~4]");
                  return false;
               }
            }

            List var9 = var4.getEffectList().getEffectsBySkillId(7029);
            int var10 = var9 == null ? 0 : (var9.isEmpty() ? 0 : ((Effect)var9.get(0)).getSkill().getLevel());
            if (var6 == 0) {
               if (var10 != 0) {
                  var4.doCast(SkillTable.getInstance().getInfo(7029, var10), var4, true);
               }

               var4.unsetVar("gm_gmspeed");
            } else if (var6 >= 1 && var6 <= 4) {
               if (Config.SAVE_GM_EFFECTS) {
                  var4.setVar("gm_gmspeed", String.valueOf(var6), -1L);
               }

               if (var6 != var10) {
                  if (var10 != 0) {
                     var4.doCast(SkillTable.getInstance().getInfo(7029, var10), var4, true);
                  }

                  var4.doCast(SkillTable.getInstance().getInfo(7029, var6), var4, true);
               }
            } else {
               var4.sendMessage("USAGE: //gmspeed value=[0..4]");
            }
            break;
         case admin_invul:
            this.b(var4, var4);
            if (var4.isInvul()) {
               if (Config.SAVE_GM_EFFECTS) {
                  var4.setVar("gm_invul", "true", -1L);
               }
            } else {
               var4.unsetVar("gm_invul");
            }
      }

      if (!var4.isGM()) {
         return false;
      }

      switch (var5) {
         case admin_offline_vis:
            for (Player var32 : GameObjectsStorage.getAllPlayers()) {
               if (var32 != null && var32.isInOfflineMode()) {
                  var32.setInvisibleType(InvisibleType.NONE);
                  var32.decayMe();
                  var32.spawnMe();
               }
            }
            break;
         case admin_offline_invis:
            for (Player var31 : GameObjectsStorage.getAllPlayers()) {
               if (var31 != null && var31.isInOfflineMode()) {
                  var31.setInvisibleType(InvisibleType.NORMAL);
                  var31.decayMe();
               }
            }
            break;
         case admin_earthquake:
            try {
               int var25 = Integer.parseInt(var2[1]);
               int var30 = Integer.parseInt(var2[2]);
               var4.broadcastPacket(new Earthquake(var4.getLoc(), var25, var30));
               break;
            } catch (Exception var17) {
               var4.sendMessage("USAGE: //earthquake intensity duration");
               return false;
            }
         case admin_redsky:
            try {
               int var24 = Integer.parseInt(var2[1]);

               for (Player var33 : GameObjectsStorage.getAllPlayersForIterate()) {
                  var33.broadcastPacket(new ExRedSky(var24));
               }
               break;
            } catch (Exception var20) {
               var4.sendMessage("USAGE: //redsky duration");
               return false;
            }
         case admin_block:
            if (var8 == null || !var8.isCreature()) {
               var4.sendPacket(Msg.INVALID_TARGET);
               return false;
            }

            if (((Creature)var8).isBlocked()) {
               return false;
            }

            ((Creature)var8).abortAttack(true, false);
            ((Creature)var8).abortCast(true, false);
            ((Creature)var8).block();
            var4.sendMessage("Target blocked.");
            ((Creature)var8).sendMessage("You have been paralyzed by a GM " + var4.getName());
            break;
         case admin_unblock:
            if (var8 == null || !var8.isCreature()) {
               var4.sendPacket(Msg.INVALID_TARGET);
               return false;
            }

            if (!((Creature)var8).isBlocked()) {
               return false;
            }

            ((Creature)var8).unblock();
            var4.sendMessage("Target unblocked.");
            ((Creature)var8).sendMessage("You have been unblocked by a GM " + var4.getName());
            break;
         case admin_changename:
            if (var2.length < 2) {
               var4.sendMessage("USAGE: //changename newName");
               return false;
            }

            if (var8 == null) {
               var8 = var4;
            }

            if (!var8.isCreature()) {
               var4.sendPacket(Msg.INVALID_TARGET);
               return false;
            }

            String var23 = ((Creature)var8).getName();
            String var28 = Util.joinStrings(" ", var2, 1);
            ((Creature)var8).setName(var28);
            ((Creature)var8).broadcastCharInfo();
            var4.sendMessage("Changed name from " + var23 + " to " + var28 + ".");
            break;
         case admin_setinvul:
            if (var8 == null || !var8.isPlayer()) {
               var4.sendPacket(Msg.INVALID_TARGET);
               return false;
            }

            this.b(var4, (Player)var8);
            break;
         case admin_getinvul:
            if (var8 != null && var8.isCreature()) {
               var4.sendMessage(
                  "Target " + var8.getName() + "(object ID: " + var8.getObjectId() + ") is " + (!((Creature)var8).isInvul() ? "NOT " : "") + "invul"
               );
            }
            break;
         case admin_social:
            int var22;
            if (var2.length < 2) {
               var22 = Rnd.get(1, 7);
            } else {
               try {
                  var22 = Integer.parseInt(var2[1]);
               } catch (NumberFormatException var16) {
                  var4.sendMessage("USAGE: //social value");
                  return false;
               }
            }

            if (var8 == null || var8 == var4) {
               var4.broadcastPacket(new SocialAction(var4.getObjectId(), var22));
            } else if (var8.isCreature()) {
               ((Creature)var8).broadcastPacket(new SocialAction(var8.getObjectId(), var22));
            }
            break;
         case admin_abnormal:
            try {
               if (var2.length > 1) {
                  var7 = AbnormalEffect.getByName(var2[1]);
               }
            } catch (Exception var19) {
               var4.sendMessage("USAGE: //abnormal name");
               var4.sendMessage("//abnormal - Clears all abnormal effects");
               return false;
            }

            Creature var11 = var8 == null ? var4 : (Creature)var8;
            if (var7 == AbnormalEffect.NULL) {
               var11.startAbnormalEffect(AbnormalEffect.NULL);
               var11.sendMessage("Abnormal effects clearned by admin.");
               if (var11 != var4) {
                  var11.sendMessage("Abnormal effects clearned.");
               }
            } else {
               var11.startAbnormalEffect(var7);
               var11.sendMessage("Admin added abnormal effect: " + var7.getName());
               if (var11 != var4) {
                  var11.sendMessage("Added abnormal effect: " + var7.getName());
               }
            }
            break;
         case admin_transform:
            int var21;
            try {
               var21 = Integer.parseInt(var2[1]);
            } catch (Exception var15) {
               var4.sendMessage("USAGE: //transform transform_id");
               return false;
            }

            var4.setTransformation(var21);
            break;
         case admin_showmovie:
            if (var2.length < 2) {
               var4.sendMessage("USAGE: //showmovie id");
               return false;
            }

            int var12;
            try {
               var12 = Integer.parseInt(var2[1]);
            } catch (NumberFormatException var14) {
               var4.sendMessage("You must specify id");
               return false;
            }

            var4.showQuestMovie(var12);
      }

      return true;
   }

   private void b(Player var1, Player var2) {
      if (var2.isInvul()) {
         var2.setIsInvul(false);
         var2.stopAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
         if (var2.getPet() != null) {
            var2.getPet().setIsInvul(false);
            var2.getPet().stopAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
         }

         var1.sendMessage(var2.getName() + " is now mortal.");
      } else {
         var2.setIsInvul(true);
         var2.startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
         if (var2.getPet() != null) {
            var2.getPet().setIsInvul(true);
            var2.getPet().startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
         }

         var1.sendMessage(var2.getName() + " is now immortal.");
      }
   }

   @Override
   public Enum[] getAdminCommandEnum() {
      return AdminEffects.Commands.values();
   }

   private enum Commands {
      admin_invis,
      admin_vis,
      admin_offline_vis,
      admin_offline_invis,
      admin_earthquake,
      admin_redsky,
      admin_block,
      admin_unblock,
      admin_changename,
      admin_gmspeed,
      admin_invul,
      admin_setinvul,
      admin_getinvul,
      admin_social,
      admin_abnormal,
      admin_transform,
      admin_showmovie;
   }
}

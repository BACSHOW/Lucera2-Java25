package l2.gameserver.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import l2.commons.collections.JoinedIterator;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.instances.NpcFriendInstance;
import l2.gameserver.model.matching.MatchingRoom;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExMPCCClose;
import l2.gameserver.network.l2.s2c.ExMPCCOpen;
import l2.gameserver.network.l2.s2c.ExMPCCPartyInfoUpdate;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class CommandChannel implements PlayerGroup {
   public static final int STRATEGY_GUIDE_ID = 8871;
   public static final int CLAN_IMPERIUM_ID = 391;
   private final List<Party> Dk = new CopyOnWriteArrayList<>();
   private Player Dl;
   private int Dm;
   private Reflection _reflection;
   private MatchingRoom Dn;

   public CommandChannel(Player var1) {
      this.Dl = var1;
      this.Dk.add(var1.getParty());
      this.Dm = var1.getParty().getLevel();
      var1.getParty().setCommandChannel(this);
      this.broadCast(ExMPCCOpen.STATIC);
   }

   public void addParty(Party var1) {
      this.broadCast(new ExMPCCPartyInfoUpdate(var1, 1));
      this.Dk.add(var1);
      this.bV();
      var1.setCommandChannel(this);

      for (Player var3 : var1) {
         var3.sendPacket(ExMPCCOpen.STATIC);
         if (this.Dn != null) {
            this.Dn.broadcastPlayerUpdate(var3);
         }
      }
   }

   public void removeParty(Party var1) {
      this.Dk.remove(var1);
      this.bV();
      var1.setCommandChannel(null);
      var1.broadCast(ExMPCCClose.STATIC);
      Reflection var2 = this.getReflection();
      if (var2 != null) {
         for (Player var4 : var1.getPartyMembers()) {
            var4.teleToLocation(var2.getReturnLoc(), 0);
         }
      }

      if (this.Dk.size() < 2) {
         this.disbandChannel();
      } else {
         for (Player var6 : var1) {
            var6.sendPacket(new ExMPCCPartyInfoUpdate(var1, 0));
            if (this.Dn != null) {
               this.Dn.broadcastPlayerUpdate(var6);
            }
         }
      }
   }

   public void disbandChannel() {
      this.broadCast(Msg.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED);

      for (Party var2 : this.Dk) {
         var2.setCommandChannel(null);
         var2.broadCast(ExMPCCClose.STATIC);
         if (this.isInReflection()) {
            var2.broadCast(new SystemMessage(2106).addNumber(1));
         }
      }

      Reflection var3 = this.getReflection();
      if (var3 != null) {
         var3.startCollapseTimer(60000L);
         this.setReflection(null);
      }

      if (this.Dn != null) {
         this.Dn.disband();
      }

      this.Dk.clear();
      this.Dl = null;
   }

   public int getMemberCount() {
      int var1 = 0;

      for (Party var3 : this.Dk) {
         var1 += var3.getMemberCount();
      }

      return var1;
   }

   @Override
   public void broadCast(IStaticPacket... var1) {
      for (Party var3 : this.Dk) {
         var3.broadCast(var1);
      }
   }

   public void broadcastToChannelPartyLeaders(L2GameServerPacket var1) {
      for (Party var3 : this.Dk) {
         Player var4 = var3.getPartyLeader();
         if (var4 != null) {
            var4.sendPacket(var1);
         }
      }
   }

   public List<Party> getParties() {
      return this.Dk;
   }

   @Deprecated
   public List<Player> getMembers() {
      ArrayList var1 = new ArrayList(this.Dk.size());

      for (Party var3 : this.getParties()) {
         var1.addAll(var3.getPartyMembers());
      }

      return var1;
   }

   @Override
   public Iterator<Player> iterator() {
      ArrayList var1 = new ArrayList(this.Dk.size());

      for (Party var3 : this.getParties()) {
         var1.add(var3.getPartyMembers().iterator());
      }

      return new JoinedIterator<>(var1);
   }

   public int getLevel() {
      return this.Dm;
   }

   public void setChannelLeader(Player var1) {
      this.Dl = var1;
      this.broadCast(new SystemMessage(1589).addString(var1.getName()));
   }

   public Player getChannelLeader() {
      return this.Dl;
   }

   public boolean meetRaidWarCondition(NpcFriendInstance var1) {
      if (!var1.isRaid()) {
         return false;
      }

      int var2 = var1.getNpcId();
      switch (var2) {
         case 29001:
         case 29006:
         case 29014:
         case 29022:
            return this.getMemberCount() > 36;
         case 29019:
            return this.getMemberCount() > 225;
         case 29020:
            return this.getMemberCount() > 56;
         case 29028:
            return this.getMemberCount() > 99;
         default:
            return this.getMemberCount() > 18;
      }
   }

   private void bV() {
      this.Dm = 0;

      for (Party var2 : this.Dk) {
         if (var2.getLevel() > this.Dm) {
            this.Dm = var2.getLevel();
         }
      }
   }

   public boolean isInReflection() {
      return this._reflection != null;
   }

   public void setReflection(Reflection var1) {
      this._reflection = var1;
   }

   public Reflection getReflection() {
      return this._reflection;
   }

   public static boolean checkAuthority(Player var0) {
      if (var0.getClan() != null
         && var0.isInParty()
         && var0.getParty().isLeader(var0)
         && (!Config.CHECK_CLAN_RANK_ON_COMMAND_CHANNEL_CREATE || var0.getPledgeClass() >= 4)) {
         boolean var1 = var0.getSkillLevel(391) > 0;
         boolean var2 = var0.getInventory().getItemByItemId(8871) != null;
         if (!var1 && !var2) {
            var0.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
            return false;
         } else {
            return true;
         }
      } else {
         var0.sendPacket(Msg.YOU_DO_NOT_HAVE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
         return false;
      }
   }

   public MatchingRoom getMatchingRoom() {
      return this.Dn;
   }

   public void setMatchingRoom(MatchingRoom var1) {
      this.Dn = var1;
   }
}

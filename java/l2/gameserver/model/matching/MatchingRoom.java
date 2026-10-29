package l2.gameserver.model.matching;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import l2.gameserver.instancemanager.MatchingRoomManager;
import l2.gameserver.listener.actor.player.OnPlayerPartyInviteListener;
import l2.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.PlayerGroup;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.SysMsgContainer;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public abstract class MatchingRoom implements PlayerGroup {
   public static int PARTY_MATCHING = 0;
   public static int CC_MATCHING = 1;
   public static int WAIT_PLAYER = 0;
   public static int ROOM_MASTER = 1;
   public static int PARTY_MEMBER = 2;
   public static int UNION_LEADER = 3;
   public static int UNION_PARTY = 4;
   public static int WAIT_PARTY = 5;
   public static int WAIT_NORMAL = 6;
   private final int _id;
   private int p;
   private int s;
   private int Yj;
   private int Yk;
   private String Yl;
   private final MatchingRoom.PartyListenerImpl Ym = new MatchingRoom.PartyListenerImpl();
   protected final Player _leader;
   protected Set<Player> _members = new CopyOnWriteArraySet<>();

   public MatchingRoom(Player var1, int var2, int var3, int var4, int var5, String var6) {
      this._leader = var1;
      this._id = MatchingRoomManager.getInstance().addMatchingRoom(this);
      this.p = var2;
      this.s = var3;
      this.Yj = var4;
      this.Yk = var5;
      this.Yl = var6;
      this.a(var1, null);
   }

   public boolean addMember(Player var1) {
      if (this._members.contains(var1)) {
         return true;
      }

      if (var1.getLevel() >= this.getMinLevel() && var1.getLevel() <= this.getMaxLevel() && this.getPlayers().size() < this.getMaxMembersSize()) {
         return this.a(var1, new SystemMessage2(this.enterMessage()).addName(var1));
      }

      var1.sendPacket(this.notValidMessage());
      return false;
   }

   private boolean a(Player var1, L2GameServerPacket var2) {
      if (!this._members.isEmpty()) {
         var1.addListener(this.Ym);
      }

      this._members.add(var1);
      var1.setMatchingRoom(this);

      for (Player var4 : this) {
         if (var4 != var1) {
            var4.sendPacket(var2, this.addMemberPacket(var4, var1));
         }
      }

      MatchingRoomManager.getInstance().removeFromWaitingList(var1);
      var1.sendPacket(this.infoRoomPacket(), this.membersPacket(var1));
      var1.sendChanges();
      return true;
   }

   public void removeMember(Player var1, boolean var2) {
      if (this._members.remove(var1)) {
         var1.removeListener(this.Ym);
         var1.setMatchingRoom(null);
         if (this._members.isEmpty()) {
            this.disband();
         } else {
            L2GameServerPacket var3 = this.infoRoomPacket();
            SystemMsg var4 = this.exitMessage(true, var2);
            SysMsgContainer var5 = var4 != null ? new SystemMessage2(var4).addName(var1) : null;

            for (Player var7 : this) {
               var7.sendPacket(var3, this.removeMemberPacket(var7, var1), var5);
            }
         }

         var1.sendPacket(this.closeRoomPacket(), this.exitMessage(false, var2));
         MatchingRoomManager.getInstance().addToWaitingList(var1);
         var1.sendChanges();
      }
   }

   public void broadcastPlayerUpdate(Player var1) {
      for (Player var3 : this) {
         var3.sendPacket(this.updateMemberPacket(var3, var1));
      }
   }

   public void disband() {
      for (Player var2 : this) {
         var2.removeListener(this.Ym);
         var2.sendPacket(this.closeRoomMessage());
         var2.sendPacket(this.closeRoomPacket());
         var2.setMatchingRoom(null);
         var2.sendChanges();
         MatchingRoomManager.getInstance().addToWaitingList(var2);
      }

      this._members.clear();
      MatchingRoomManager.getInstance().removeMatchingRoom(this);
   }

   public abstract SystemMsg notValidMessage();

   public abstract SystemMsg enterMessage();

   public abstract SystemMsg exitMessage(boolean var1, boolean var2);

   public abstract SystemMsg closeRoomMessage();

   public abstract L2GameServerPacket closeRoomPacket();

   public abstract L2GameServerPacket infoRoomPacket();

   public abstract L2GameServerPacket addMemberPacket(Player var1, Player var2);

   public abstract L2GameServerPacket removeMemberPacket(Player var1, Player var2);

   public abstract L2GameServerPacket updateMemberPacket(Player var1, Player var2);

   public abstract L2GameServerPacket membersPacket(Player var1);

   public abstract int getType();

   public abstract int getMemberType(Player var1);

   @Override
   public void broadCast(IStaticPacket... var1) {
      for (Player var3 : this) {
         var3.sendPacket(var1);
      }
   }

   public int getId() {
      return this._id;
   }

   public int getMinLevel() {
      return this.p;
   }

   public int getMaxLevel() {
      return this.s;
   }

   public String getTopic() {
      return this.Yl;
   }

   public int getMaxMembersSize() {
      return this.Yj;
   }

   public int getLocationId() {
      return MatchingRoomManager.getInstance().getLocation(this._leader);
   }

   public Player getLeader() {
      return this._leader;
   }

   public Collection<Player> getPlayers() {
      return this._members;
   }

   public int getLootType() {
      return this.Yk;
   }

   @Override
   public Iterator<Player> iterator() {
      return this._members.iterator();
   }

   public void setMinLevel(int var1) {
      this.p = var1;
   }

   public void setMaxLevel(int var1) {
      this.s = var1;
   }

   public void setTopic(String var1) {
      this.Yl = var1;
   }

   public void setMaxMemberSize(int var1) {
      this.Yj = var1;
   }

   public void setLootType(int var1) {
      this.Yk = var1;
   }

   private class PartyListenerImpl implements OnPlayerPartyInviteListener, OnPlayerPartyLeaveListener {
      private PartyListenerImpl() {
      }

      @Override
      public void onPartyInvite(Player var1) {
         MatchingRoom.this.broadcastPlayerUpdate(var1);
      }

      @Override
      public void onPartyLeave(Player var1) {
         MatchingRoom.this.broadcastPlayerUpdate(var1);
      }
   }
}

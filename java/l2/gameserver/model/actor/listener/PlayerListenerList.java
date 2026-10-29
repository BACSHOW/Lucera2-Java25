/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.actor.listener;

import l2.gameserver.listener.actor.player.OnAutoSoulShotListener;
import l2.gameserver.listener.actor.player.OnGainExpSpListener;
import l2.gameserver.listener.actor.player.OnItemPickupListener;
import l2.gameserver.listener.actor.player.OnLevelUpListener;
import l2.gameserver.listener.actor.player.OnOlyCompetitionListener;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.listener.actor.player.OnPlayerPartyInviteListener;
import l2.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2.gameserver.listener.actor.player.OnPlayerSayListener;
import l2.gameserver.listener.actor.player.OnPvpPkKillListener;
import l2.gameserver.listener.actor.player.OnQuestStateChangeListener;
import l2.gameserver.listener.actor.player.OnSetClassListener;
import l2.gameserver.listener.actor.player.OnSetLevelListener;
import l2.gameserver.listener.actor.player.OnSetPrivateStoreType;
import l2.gameserver.listener.actor.player.OnTeleportListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.model.entity.oly.Competition;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.quest.QuestState;

public class PlayerListenerList
extends CharListenerList {
    public PlayerListenerList(Player player) {
        super(player);
    }

    @Override
    public Player getActor() {
        return (Player)this.actor;
    }

    public void onEnter() {
        this.forEachListenerWithGlobal(OnPlayerEnterListener.class, onPlayerEnterListener -> onPlayerEnterListener.onPlayerEnter(this.getActor()));
    }

    public void onExit() {
        this.forEachListenerWithGlobal(OnPlayerExitListener.class, onPlayerExitListener -> onPlayerExitListener.onPlayerExit(this.getActor()));
    }

    public void onTeleport(int n, int n2, int n3, Reflection reflection) {
        this.forEachListenerWithGlobal(OnTeleportListener.class, onTeleportListener -> onTeleportListener.onTeleport(this.getActor(), n, n2, n3, reflection));
    }

    public void onQuestStateChange(QuestState questState) {
        this.forEachListenerWithGlobal(OnQuestStateChangeListener.class, onQuestStateChangeListener -> onQuestStateChangeListener.onQuestStateChange(this.getActor(), questState));
    }

    public void onOlyCompetitionCompleted(Competition competition, boolean bl) {
        this.forEachListenerWithGlobal(OnOlyCompetitionListener.class, onOlyCompetitionListener -> onOlyCompetitionListener.onOlyCompetitionCompleted(this.getActor(), competition, bl));
    }

    public void onGainExpSp(long l, long l2) {
        this.forEachListenerWithGlobal(OnGainExpSpListener.class, onGainExpSpListener -> onGainExpSpListener.onGainExpSp(this.getActor(), l, l2));
    }

    public void onPvpPkKill(Player player, boolean bl) {
        this.forEachListenerWithGlobal(OnPvpPkKillListener.class, onPvpPkKillListener -> onPvpPkKillListener.onPvpPkKill(this.getActor(), player, bl));
    }

    public void onItemPickup(ItemInstance itemInstance) {
        this.forEachListenerWithGlobal(OnItemPickupListener.class, onItemPickupListener -> onItemPickupListener.onItemPickup(this.getActor(), itemInstance));
    }

    public void onLevelUp(int n) {
        this.forEachListenerWithGlobal(OnLevelUpListener.class, onLevelUpListener -> onLevelUpListener.onLevelUp(this.getActor(), n));
    }

    public void onSay(int n, String string, String string2) {
        this.forEachListenerWithGlobal(OnPlayerSayListener.class, onPlayerSayListener -> onPlayerSayListener.onSay(this.getActor(), n, string, string2));
    }

    public void onSetPrivateStoreType(int n) {
        this.forEachListenerWithGlobal(OnSetPrivateStoreType.class, onSetPrivateStoreType -> onSetPrivateStoreType.onSetPrivateStoreType(this.getActor(), n));
    }

    public void onAutoSoulShot(int n, boolean bl) {
        this.forEachListenerWithGlobal(OnAutoSoulShotListener.class, onAutoSoulShotListener -> onAutoSoulShotListener.onAutoSoulShot(this.getActor(), n, bl));
    }

    public void onSetLevel(int n) {
        this.forEachListenerWithGlobal(OnSetLevelListener.class, onSetLevelListener -> onSetLevelListener.onSetLevel(this.getActor(), n));
    }

    public void onSetClass(int n) {
        this.forEachListenerWithGlobal(OnSetClassListener.class, onSetClassListener -> onSetClassListener.onSetClass(this.getActor(), n));
    }

    public void onPartyInvite() {
        this.forEachListenerWithGlobal(OnPlayerPartyInviteListener.class, onPlayerPartyInviteListener -> onPlayerPartyInviteListener.onPartyInvite(this.getActor()));
    }

    public void onPartyLeave() {
        this.forEachListenerWithGlobal(OnPlayerPartyLeaveListener.class, onPlayerPartyLeaveListener -> onPlayerPartyLeaveListener.onPartyLeave(this.getActor()));
    }
}


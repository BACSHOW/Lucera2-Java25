/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.voicecommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import l2.commons.dbutils.DbUtils;
import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.instancemanager.CoupleManager;
import l2.gameserver.instancemanager.ReflectionManager;
import l2.gameserver.listener.actor.player.OnAnswerListener;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.Couple;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ConfirmDlg;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.network.l2.s2c.SetupGauge;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.skills.AbnormalEffect;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.utils.Location;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wedding
implements IVoicedCommandHandler {
    private static final Logger _log = LoggerFactory.getLogger(Wedding.class);
    private static String[] zG = new String[]{"divorce", "engage", "gotolove"};

    @Override
    public boolean useVoicedCommand(String string, Player player, String string2) {
        if (!Config.ALLOW_WEDDING) {
            return false;
        }
        if (string.startsWith("engage")) {
            return this.engage(player);
        }
        if (string.startsWith("divorce")) {
            return this.divorce(player);
        }
        if (string.startsWith("gotolove")) {
            return this.goToLove(player);
        }
        return false;
    }

    public boolean divorce(Player player) {
        if (player.getPartnerId() == 0) {
            return false;
        }
        int n = player.getPartnerId();
        long l = 0L;
        if (player.isMaried()) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.Divorced", player, new Object[0]));
            l = Math.abs(player.getAdena() / 100L * (long)Config.WEDDING_DIVORCE_COSTS - 10L);
            player.reduceAdena(l, true);
        } else {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.Disengaged", player, new Object[0]));
        }
        player.setMaried(false);
        player.setPartnerId(0);
        Couple couple = CoupleManager.getInstance().getCouple(player.getCoupleId());
        couple.divorce();
        couple = null;
        Player player2 = GameObjectsStorage.getPlayer(n);
        if (player2 != null) {
            player2.setPartnerId(0);
            if (player2.isMaried()) {
                player2.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerDivorce", player2, new Object[0]));
            } else {
                player2.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerDisengage", player2, new Object[0]));
            }
            player2.setMaried(false);
            if (l > 0L) {
                player2.addAdena(l);
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean engage(Player player) {
        if (player.getTarget() == null) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.NoneTargeted", player, new Object[0]));
            return false;
        }
        if (!player.getTarget().isPlayer()) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.OnlyAnotherPlayer", player, new Object[0]));
            return false;
        }
        if (player.getPartnerId() != 0) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.AlreadyEngaged", player, new Object[0]));
            if (Config.WEDDING_PUNISH_INFIDELITY) {
                player.startAbnormalEffect(AbnormalEffect.BIG_HEAD);
                int n = 1;
                if (player.getLevel() > 40) {
                    n = 2;
                }
                int n2 = player.isMageClass() ? 4361 : 4362;
                Skill skill = SkillTable.getInstance().getInfo(n2, n);
                if (player.getEffectList().getEffectsBySkill(skill) == null) {
                    skill.getEffects(player, player, false, false);
                    player.sendPacket((IStaticPacket)new SystemMessage(110).addSkillName(n2, n));
                }
            }
            return false;
        }
        Player player2 = (Player)player.getTarget();
        if (player2.getObjectId() == player.getObjectId()) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.EngagingYourself", player, new Object[0]));
            return false;
        }
        if (player2.isMaried()) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyMarried", player, new Object[0]));
            return false;
        }
        if (player2.getPartnerId() != 0) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyEngaged", player, new Object[0]));
            return false;
        }
        Pair<Integer, OnAnswerListener> pair = player2.getAskListener(false);
        if (pair != null && pair.getValue() instanceof CoupleAnswerListener) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyAsked", player, new Object[0]));
            return false;
        }
        if (player2.getPartnerId() != 0) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyEngaged", player, new Object[0]));
            return false;
        }
        if (player2.getSex() == player.getSex() && !Config.WEDDING_SAMESEX) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.SameSex", player, new Object[0]));
            return false;
        }
        boolean bl = false;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseFactory.getInstance().getConnection();
            preparedStatement = connection.prepareStatement("SELECT `friend_id` FROM `character_friends` WHERE `char_id`=?");
            preparedStatement.setInt(1, player2.getObjectId());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int n = resultSet.getInt("friend_id");
                if (n != player.getObjectId()) continue;
                bl = true;
                break;
            }
        }
        catch (Exception exception) {
            try {
                _log.error("", exception);
            }
            catch (Throwable throwable) {
                DbUtils.closeQuietly(connection, preparedStatement, resultSet);
                throw throwable;
            }
            DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        }
        DbUtils.closeQuietly(connection, preparedStatement, resultSet);
        if (!bl) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.NotInFriendlist", player, new Object[0]));
            return false;
        }
        ConfirmDlg confirmDlg = (ConfirmDlg)new ConfirmDlg(SystemMsg.S1, 60000).addString("Player " + player.getName() + " asking you to engage. Do you want to start new relationship?");
        player2.ask(confirmDlg, new CoupleAnswerListener(player, player2));
        return true;
    }

    public boolean goToLove(Player player) {
        if (!player.isMaried()) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.YoureNotMarried", player, new Object[0]));
            return false;
        }
        if (player.getPartnerId() == 0) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerNotInDB", player, new Object[0]));
            return false;
        }
        Player player2 = GameObjectsStorage.getPlayer(player.getPartnerId());
        if (player2 == null) {
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerOffline", player, new Object[0]));
            return false;
        }
        if (player2.isOlyParticipant() || player2.isFestivalParticipant() || player.isMovementDisabled() || player.isDead() || player.isMuted(null) || player.isOlyParticipant() || player.isInDuel() || player.isFestivalParticipant() || player2.isInZone(Zone.ZoneType.no_summon)) {
            player.sendMessage(new CustomMessage("common.TryLater", player, new Object[0]));
            return false;
        }
        if (player.isInParty() && player.getParty().isInDimensionalRift() || player2.isInParty() && player2.getParty().isInDimensionalRift()) {
            player.sendMessage(new CustomMessage("common.TryLater", player, new Object[0]));
            return false;
        }
        if (player.getTeleMode() != 0 || player.getReflection() != ReflectionManager.DEFAULT) {
            player.sendMessage(new CustomMessage("common.TryLater", player, new Object[0]));
            return false;
        }
        if (player2.isInZoneBattle() || player2.isInZone(Zone.ZoneType.SIEGE) || player2.isInZone(Zone.ZoneType.no_restart) || player2.isOlyParticipant() || player.isInZoneBattle() || player.isInZone(Zone.ZoneType.SIEGE) || player.isInZone(Zone.ZoneType.no_restart) || player.isOlyParticipant() || player2.getReflection() != ReflectionManager.DEFAULT || player2.isInZone(Zone.ZoneType.no_summon) || player.isInObserverMode() || player2.isInObserverMode() || player2.isInZone(Zone.ZoneType.fun) || player.isInZone(Zone.ZoneType.fun)) {
            player.sendPacket((IStaticPacket)Msg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING);
            return false;
        }
        if (!player.reduceAdena(Config.WEDDING_TELEPORT_PRICE, true)) {
            player.sendPacket((IStaticPacket)Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return false;
        }
        int n = Config.WEDDING_TELEPORT_INTERVAL;
        player.abortAttack(true, true);
        player.abortCast(true, true);
        player.sendActionFailed();
        player.stopMove();
        player.startParalyzed();
        player.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.Teleport", player, new Object[0]).addNumber(n / 60));
        player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        player.broadcastPacket(new MagicSkillUse(player, player, 1050, 1, n, 0L));
        player.sendPacket((IStaticPacket)new SetupGauge(player, 0, n));
        ThreadPoolManager.getInstance().schedule(new EscapeFinalizer(player, player2.getLoc()), (long)n * 1000L);
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return zG;
    }

    public void onLoad() {
        VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
    }

    public void onReload() {
    }

    public void onShutdown() {
    }

    static class EscapeFinalizer
    extends RunnableImpl {
        private Player zT;
        private Location cH;

        EscapeFinalizer(Player player, Location location) {
            this.zT = player;
            this.cH = location;
        }

        @Override
        public void runImpl() throws Exception {
            this.zT.stopParalyzed();
            if (this.zT.isDead()) {
                return;
            }
            this.zT.teleToLocation(this.cH);
        }
    }

    private static class CoupleAnswerListener
    implements OnAnswerListener {
        private HardReference<Player> zR;
        private HardReference<Player> zS;

        public CoupleAnswerListener(Player player, Player player2) {
            this.zR = player.getRef();
            this.zS = player2.getRef();
        }

        @Override
        public void sayYes() {
            Player player;
            Player player2 = this.zR.get();
            if (player2 == null || (player = this.zS.get()) == null) {
                return;
            }
            CoupleManager.getInstance().createCouple(player2, player);
            player2.sendMessage(new CustomMessage("l2p.gameserver.model.L2Player.EngageAnswerYes", player, new Object[0]));
        }

        @Override
        public void sayNo() {
            Player player;
            Player player2 = this.zR.get();
            if (player2 == null || (player = this.zS.get()) == null) {
                return;
            }
            player2.sendMessage(new CustomMessage("l2p.gameserver.model.L2Player.EngageAnswerNo", player, new Object[0]));
        }
    }
}


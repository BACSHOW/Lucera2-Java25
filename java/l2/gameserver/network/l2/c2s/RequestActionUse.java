/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.ai.CtrlIntention;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Summon;
import l2.gameserver.model.instances.DoorInstance;
import l2.gameserver.model.instances.StaticObjectInstance;
import l2.gameserver.model.instances.residences.SiegeFlagInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.AttackRequest;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.PrivateStoreManageListBuy;
import l2.gameserver.network.l2.s2c.PrivateStoreManageListSell;
import l2.gameserver.network.l2.s2c.RecipeShopManageList;
import l2.gameserver.network.l2.s2c.SocialAction;
import l2.gameserver.tables.PetSkillsTable;
import l2.gameserver.tables.SkillTable;
import l2.gameserver.templates.DoorTemplate;
import l2.gameserver.utils.TradeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestActionUse
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestActionUse.class);
    private int aat;
    private boolean aaX;
    private boolean aaY;

    @Override
    protected void readImpl() {
        this.aat = this.readD();
        this.aaX = this.readD() == 1;
        this.aaY = this.readC() == 1;
    }

    @Override
    protected void runImpl() {
        boolean bl;
        GameClient gameClient = (GameClient)this.getClient();
        Player player = gameClient.getActiveChar();
        if (player == null) {
            return;
        }
        Action action = Action.find(this.aat);
        if (action == null) {
            _log.warn("unhandled action type " + this.aat + " by player " + player.getName());
            player.sendActionFailed();
            return;
        }
        boolean bl2 = bl = action.type == 1 || action.type == 2;
        if (!(bl || !player.isOutOfControl() && !player.isActionsDisabled() || player.isFakeDeath() && this.aat == 0)) {
            player.sendActionFailed();
            return;
        }
        if ((player.getTransformation() != 0 || player.isCursedWeaponEquipped()) && action.transform > 0) {
            player.sendActionFailed();
            return;
        }
        if (action.type == 3) {
            if (player.isOutOfControl() || player.getTransformation() != 0 || player.isActionsDisabled() || player.isSitting() || player.getPrivateStoreType() != 0 || player.isProcessingRequest()) {
                player.sendActionFailed();
                return;
            }
            if (player.isFishing()) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
                return;
            }
            player.broadcastPacket(new SocialAction(player.getObjectId(), action.value));
            if (Config.ALT_SOCIAL_ACTION_REUSE) {
                ThreadPoolManager.getInstance().schedule(new SocialTask(player), 2600L);
                player.startParalyzed();
            }
            return;
        }
        GameObject gameObject = player.getTarget();
        Summon summon = player.getPet();
        if (bl) {
            if (summon == null || summon.isOutOfControl()) {
                player.sendActionFailed();
                return;
            }
            if (summon.isDepressed()) {
                player.sendPacket((IStaticPacket)SystemMsg.YOUR_PETSERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
                return;
            }
        }
        if (action.type == 2) {
            if (action.id == 1000 && gameObject != null && !gameObject.isDoor()) {
                player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET, ActionFail.STATIC);
                return;
            }
            if ((action.id == 1039 || action.id == 1040) && (gameObject != null && gameObject.isDoor() && ((DoorInstance)gameObject).getDoorType() != DoorTemplate.DoorType.WALL || gameObject instanceof SiegeFlagInstance)) {
                player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET, ActionFail.STATIC);
                return;
            }
            this.Q(action.value);
            return;
        }
        switch (action.id) {
            case 0: {
                if (player.isMounted()) {
                    player.sendActionFailed();
                    break;
                }
                if (player.isFakeDeath()) {
                    player.breakFakeDeath();
                    player.updateEffectIcons();
                    break;
                }
                if (!player.isSitting()) {
                    if (gameObject != null && gameObject instanceof StaticObjectInstance && ((StaticObjectInstance)gameObject).getType() == 1 && player.getDistance3D(gameObject) <= (double)gameObject.getActingRange()) {
                        player.sitDown((StaticObjectInstance)gameObject);
                        break;
                    }
                    player.sitDown(null);
                    break;
                }
                player.standUp();
                break;
            }
            case 1: {
                if (player.isRunning()) {
                    player.setWalking();
                } else {
                    player.setRunning();
                }
                player.sendUserInfo();
                break;
            }
            case 10: 
            case 61: {
                if (player.getSittingTask()) {
                    player.sendActionFailed();
                    return;
                }
                if (player.isInStoreMode()) {
                    player.setPrivateStoreType(0);
                } else if (!TradeHelper.checksIfCanOpenStore(player, this.aat == 61 ? 8 : 1)) {
                    player.sendActionFailed();
                    return;
                }
                player.sendPacket((IStaticPacket)new PrivateStoreManageListSell(player, this.aat == 61));
                break;
            }
            case 28: {
                if (player.getSittingTask()) {
                    player.sendActionFailed();
                    return;
                }
                if (player.isInStoreMode()) {
                    player.setPrivateStoreType(0);
                } else if (!TradeHelper.checksIfCanOpenStore(player, 3)) {
                    player.sendActionFailed();
                    return;
                }
                player.sendPacket((IStaticPacket)new PrivateStoreManageListBuy(player));
                break;
            }
            case 37: {
                if (player.getSittingTask()) {
                    player.sendActionFailed();
                    return;
                }
                if (player.isInStoreMode()) {
                    player.setPrivateStoreType(0);
                } else if (!TradeHelper.checksIfCanOpenStore(player, 5)) {
                    player.sendActionFailed();
                    return;
                }
                player.sendPacket((IStaticPacket)new RecipeShopManageList(player, true));
                break;
            }
            case 51: {
                if (player.getSittingTask()) {
                    player.sendActionFailed();
                    return;
                }
                if (player.isInStoreMode()) {
                    player.setPrivateStoreType(0);
                } else if (!TradeHelper.checksIfCanOpenStore(player, 5)) {
                    player.sendActionFailed();
                    return;
                }
                player.sendPacket((IStaticPacket)new RecipeShopManageList(player, false));
                break;
            }
            case 96: {
                _log.info("96 Accessed");
                break;
            }
            case 97: {
                _log.info("97 Accessed");
                break;
            }
            case 15: 
            case 21: {
                if (summon == null) break;
                summon.setFollowMode(!summon.isFollowMode());
                break;
            }
            case 16: 
            case 22: {
                if (gameObject == null || !gameObject.isCreature() || summon == gameObject || summon.isDead()) {
                    player.sendActionFailed();
                    return;
                }
                if (summon.getTemplate().getNpcId() == 12564) {
                    return;
                }
                if (!this.aaX && gameObject.isCreature() && !((Creature)gameObject).isAutoAttackable(summon)) {
                    if (gameObject != player && (summon.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW || summon.getFollowTarget() != gameObject)) {
                        summon.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, gameObject);
                    }
                    return;
                }
                if (!gameObject.isAttackable(summon)) {
                    player.sendPacket((IStaticPacket)SystemMsg.INVALID_TARGET);
                    return;
                }
                if (!gameObject.isNpc() && (summon.isInZonePeace() || gameObject.isCreature() && ((Creature)gameObject).isInZonePeace())) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE);
                    return;
                }
                if (player.getLevel() + 20 <= summon.getLevel()) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
                    return;
                }
                long l = System.currentTimeMillis();
                if (l - gameClient.getLastIncomePacketTimeStamp(AttackRequest.class) < (long)Config.ATTACK_PACKET_DELAY) {
                    player.sendActionFailed();
                    return;
                }
                gameClient.setLastIncomePacketTimeStamp(AttackRequest.class, l);
                summon.getAI().Attack(gameObject, this.aaX, this.aaY);
                break;
            }
            case 17: 
            case 23: {
                summon.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                break;
            }
            case 19: {
                if (summon.isDead()) {
                    player.sendPacket(SystemMsg.DEAD_PETS_CANNOT_BE_RETURNED_TO_THEIR_SUMMONING_ITEM, ActionFail.STATIC);
                    return;
                }
                if (summon.isInCombat()) {
                    player.sendPacket(SystemMsg.A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE, ActionFail.STATIC);
                    break;
                }
                if (summon.isPet() && (double)summon.getCurrentFed() < 0.55 * (double)summon.getMaxFed()) {
                    player.sendPacket(SystemMsg.YOU_MAY_NOT_RESTORE_A_HUNGRY_PET, ActionFail.STATIC);
                    break;
                }
                summon.unSummon();
                break;
            }
            case 38: {
                if (player.getTransformation() != 0 || player.isCursedWeaponEquipped()) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                if (summon == null || !summon.isMountable()) {
                    if (!player.isMounted()) break;
                    if (player.isFlying() && !player.checkLandingState()) {
                        player.sendPacket(Msg.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_AT_THIS_LOCATION, ActionFail.STATIC);
                        return;
                    }
                    player.setMount(0, 0, 0);
                    break;
                }
                if (player.isMounted() || player.isInBoat()) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                if (player.isDead()) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                if (summon.isDead()) {
                    player.sendPacket((IStaticPacket)Msg.A_DEAD_PET_CANNOT_BE_RIDDEN);
                    break;
                }
                if (player.isInDuel()) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                if (player.isInCombat() || summon.isInCombat()) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                if (player.isFishing()) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                if (player.isSitting()) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                if (player.isCursedWeaponEquipped()) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                if (player.getActiveWeaponFlagAttachment() != null) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                if (player.isCastingNow()) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                if (player.isParalyzed()) {
                    player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_MOUNT_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
                    break;
                }
                player.setMount(summon.getTemplate().npcId, summon.getObjectId(), summon.getLevel());
                summon.unSummon();
                break;
            }
            case 52: {
                if (summon.isInCombat()) {
                    player.sendPacket((IStaticPacket)SystemMsg.A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE);
                    player.sendActionFailed();
                    break;
                }
                if (Config.ALT_SAVE_SERVITOR_BUFF) {
                    summon.saveEffects();
                }
                summon.unSummon();
                break;
            }
            case 53: 
            case 54: {
                if (gameObject == null || summon == gameObject || summon.isMovementDisabled()) break;
                summon.setFollowMode(false);
                summon.moveToLocation(gameObject.getLoc(), 100, true);
                break;
            }
            case 1001: {
                break;
            }
            default: {
                _log.warn("unhandled action type " + this.aat + " by player " + player.getName());
            }
        }
        player.sendActionFailed();
    }

    private void Q(int n) {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        Summon summon = player.getPet();
        if (summon == null) {
            player.sendActionFailed();
            return;
        }
        int n2 = PetSkillsTable.getInstance().getAvailableLevel(summon, n);
        if (n2 == 0) {
            player.sendActionFailed();
            return;
        }
        Skill skill = SkillTable.getInstance().getInfo(n, n2);
        if (skill == null) {
            player.sendActionFailed();
            return;
        }
        if (player.getLevel() + 20 <= summon.getLevel()) {
            player.sendPacket((IStaticPacket)SystemMsg.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
            return;
        }
        Creature creature = skill.getAimingTarget(summon, player.getTarget());
        if (skill.checkCondition(summon, creature, this.aaX, this.aaY, true)) {
            summon.getAI().Cast(skill, creature, this.aaX, this.aaY);
        } else {
            player.sendActionFailed();
        }
    }

    static class SocialTask
    extends RunnableImpl {
        Player _player;

        SocialTask(Player player) {
            this._player = player;
        }

        @Override
        public void runImpl() throws Exception {
            this._player.stopParalyzed();
        }
    }

    public static enum Action {
        ACTION0(0, 0, 0, 1),
        ACTION1(1, 0, 0, 0),
        ACTION7(7, 0, 0, 1),
        ACTION10(10, 0, 0, 1),
        ACTION28(28, 0, 0, 1),
        ACTION37(37, 0, 0, 1),
        ACTION38(38, 0, 0, 1),
        ACTION51(51, 0, 0, 1),
        ACTION61(61, 0, 0, 1),
        ACTION96(96, 0, 0, 1),
        ACTION97(97, 0, 0, 1),
        ACTION67(67, 0, 0, 1),
        ACTION68(68, 0, 0, 1),
        ACTION69(69, 0, 0, 1),
        ACTION70(70, 0, 0, 1),
        ACTION15(15, 1, 0, 0),
        ACTION16(16, 1, 0, 0),
        ACTION17(17, 1, 0, 0),
        ACTION19(19, 1, 0, 0),
        ACTION21(21, 1, 0, 0),
        ACTION22(22, 1, 0, 0),
        ACTION23(23, 1, 0, 0),
        ACTION52(52, 1, 0, 0),
        ACTION53(53, 1, 0, 0),
        ACTION54(54, 1, 0, 0),
        ACTION32(32, 2, 4230, 0),
        ACTION36(36, 2, 4259, 0),
        ACTION39(39, 2, 4138, 0),
        ACTION41(41, 2, 4230, 0),
        ACTION42(42, 2, 4378, 0),
        ACTION43(43, 2, 4137, 0),
        ACTION44(44, 2, 4139, 0),
        ACTION45(45, 2, 4025, 0),
        ACTION46(46, 2, 4261, 0),
        ACTION47(47, 2, 4260, 0),
        ACTION48(48, 2, 4068, 0),
        ACTION1000(1000, 2, 4079, 0),
        ACTION1003(1003, 2, 4710, 0),
        ACTION1004(1004, 2, 4711, 0),
        ACTION1005(1005, 2, 4712, 0),
        ACTION1006(1006, 2, 4713, 0),
        ACTION1007(1007, 2, 4699, 0),
        ACTION1008(1008, 2, 4700, 0),
        ACTION1009(1009, 2, 4701, 0),
        ACTION1010(1010, 2, 4702, 0),
        ACTION1011(1011, 2, 4703, 0),
        ACTION1012(1012, 2, 4704, 0),
        ACTION1013(1013, 2, 4705, 0),
        ACTION1014(1014, 2, 4706, 0),
        ACTION1015(1015, 2, 4707, 0),
        ACTION1016(1016, 2, 4709, 0),
        ACTION1017(1017, 2, 4708, 0),
        ACTION1031(1031, 2, 5135, 0),
        ACTION1032(1032, 2, 5136, 0),
        ACTION1033(1033, 2, 5137, 0),
        ACTION1034(1034, 2, 5138, 0),
        ACTION1035(1035, 2, 5139, 0),
        ACTION1036(1036, 2, 5142, 0),
        ACTION1037(1037, 2, 5141, 0),
        ACTION1038(1038, 2, 5140, 0),
        ACTION1039(1039, 2, 5110, 0),
        ACTION1040(1040, 2, 5111, 0),
        ACTION1041(1041, 2, 5442, 0),
        ACTION1042(1042, 2, 5444, 0),
        ACTION1043(1043, 2, 5443, 0),
        ACTION1044(1044, 2, 5445, 0),
        ACTION1045(1045, 2, 5584, 0),
        ACTION1046(1046, 2, 5585, 0),
        ACTION1047(1047, 2, 5580, 0),
        ACTION1048(1048, 2, 5581, 0),
        ACTION1049(1049, 2, 5582, 0),
        ACTION1050(1050, 2, 5583, 0),
        ACTION1051(1051, 2, 5638, 0),
        ACTION1052(1052, 2, 5639, 0),
        ACTION1053(1053, 2, 5640, 0),
        ACTION1054(1054, 2, 5643, 0),
        ACTION1055(1055, 2, 5647, 0),
        ACTION1056(1056, 2, 5648, 0),
        ACTION1057(1057, 2, 5646, 0),
        ACTION1058(1058, 2, 5652, 0),
        ACTION1059(1059, 2, 5653, 0),
        ACTION1060(1060, 2, 5654, 0),
        ACTION1061(1061, 2, 5745, 0),
        ACTION1062(1062, 2, 5746, 0),
        ACTION1063(1063, 2, 5747, 0),
        ACTION1064(1064, 2, 5748, 0),
        ACTION1065(1065, 2, 5753, 0),
        ACTION1066(1066, 2, 5749, 0),
        ACTION1067(1067, 2, 5750, 0),
        ACTION1068(1068, 2, 5751, 0),
        ACTION1069(1069, 2, 5752, 0),
        ACTION1070(1070, 2, 5771, 0),
        ACTION1071(1071, 2, 5761, 0),
        ACTION1072(1072, 2, 6046, 0),
        ACTION1073(1073, 2, 6047, 0),
        ACTION1074(1074, 2, 6048, 0),
        ACTION1075(1075, 2, 6049, 0),
        ACTION1076(1076, 2, 6050, 0),
        ACTION1077(1077, 2, 6051, 0),
        ACTION1078(1078, 2, 6052, 0),
        ACTION1079(1079, 2, 6053, 0),
        ACTION1080(1080, 2, 6041, 0),
        ACTION1081(1081, 2, 6042, 0),
        ACTION1082(1082, 2, 6043, 0),
        ACTION1083(1083, 2, 6044, 0),
        ACTION1084(1084, 2, 6054, 0),
        ACTION1086(1086, 2, 6094, 0),
        ACTION1087(1087, 2, 6095, 0),
        ACTION1088(1088, 2, 6096, 0),
        ACTION1089(1089, 2, 6199, 0),
        ACTION1090(1090, 2, 6205, 0),
        ACTION1091(1091, 2, 6206, 0),
        ACTION1092(1092, 2, 6207, 0),
        ACTION1093(1093, 2, 6618, 0),
        ACTION1094(1094, 2, 6681, 0),
        ACTION1095(1095, 2, 6619, 0),
        ACTION1096(1096, 2, 6682, 0),
        ACTION1097(1097, 2, 6683, 0),
        ACTION1098(1098, 2, 6684, 0),
        ACTION5000(5000, 2, 23155, 0),
        ACTION5001(5001, 2, 23167, 0),
        ACTION5002(5002, 2, 23168, 0),
        ACTION5003(5003, 2, 5749, 0),
        ACTION5004(5004, 2, 5750, 0),
        ACTION5005(5005, 2, 5751, 0),
        ACTION5006(5006, 2, 5771, 0),
        ACTION5007(5007, 2, 6046, 0),
        ACTION5008(5008, 2, 6047, 0),
        ACTION5009(5009, 2, 6048, 0),
        ACTION5010(5010, 2, 6049, 0),
        ACTION5011(5011, 2, 6050, 0),
        ACTION5012(5012, 2, 6051, 0),
        ACTION5013(5013, 2, 6052, 0),
        ACTION5014(5014, 2, 6053, 0),
        ACTION5015(5015, 2, 6054, 0),
        ACTION12(12, 3, 2, 2),
        ACTION13(13, 3, 3, 2),
        ACTION14(14, 3, 4, 2),
        ACTION24(24, 3, 6, 2),
        ACTION25(25, 3, 5, 2),
        ACTION26(26, 3, 7, 2),
        ACTION29(29, 3, 8, 2),
        ACTION30(30, 3, 9, 2),
        ACTION31(31, 3, 10, 2),
        ACTION33(33, 3, 11, 2),
        ACTION34(34, 3, 12, 2),
        ACTION35(35, 3, 13, 2),
        ACTION62(62, 3, 14, 2),
        ACTION66(66, 3, 15, 2);

        public int id;
        public int type;
        public int value;
        public int transform;
        public static final Action[] VALUES;

        private Action(int n2, int n3, int n4, int n5) {
            this.id = n2;
            this.type = n3;
            this.value = n4;
            this.transform = n5;
        }

        public static Action find(int n) {
            for (Action action : VALUES) {
                if (action.id != n) continue;
                return action;
            }
            return null;
        }

        static {
            VALUES = Action.values();
        }
    }
}


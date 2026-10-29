/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import l2.commons.net.nio.impl.IClientFactory;
import l2.commons.net.nio.impl.IMMOExecutor;
import l2.commons.net.nio.impl.IPacketHandler;
import l2.commons.net.nio.impl.MMOConnection;
import l2.commons.net.nio.impl.ReceivablePacket;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.network.l2.CGModule;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.Action;
import l2.gameserver.network.l2.c2s.AddTradeItem;
import l2.gameserver.network.l2.c2s.AnswerJoinPartyRoom;
import l2.gameserver.network.l2.c2s.AnswerTradeRequest;
import l2.gameserver.network.l2.c2s.Appearing;
import l2.gameserver.network.l2.c2s.AttackRequest;
import l2.gameserver.network.l2.c2s.AuthLogin;
import l2.gameserver.network.l2.c2s.BypassUserCmd;
import l2.gameserver.network.l2.c2s.CannotMoveAnymore;
import l2.gameserver.network.l2.c2s.CannotMoveAnymoreInVehicle;
import l2.gameserver.network.l2.c2s.CharacterCreate;
import l2.gameserver.network.l2.c2s.CharacterDelete;
import l2.gameserver.network.l2.c2s.CharacterRestore;
import l2.gameserver.network.l2.c2s.CharacterSelected;
import l2.gameserver.network.l2.c2s.ConfirmDlg;
import l2.gameserver.network.l2.c2s.EnterWorld;
import l2.gameserver.network.l2.c2s.FinishRotatingC;
import l2.gameserver.network.l2.c2s.GotoLobby;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.c2s.Logout;
import l2.gameserver.network.l2.c2s.MoveBackwardToLocation;
import l2.gameserver.network.l2.c2s.MoveWithDelta;
import l2.gameserver.network.l2.c2s.NetPing;
import l2.gameserver.network.l2.c2s.NewCharacter;
import l2.gameserver.network.l2.c2s.PetitionVote;
import l2.gameserver.network.l2.c2s.ProtocolVersion;
import l2.gameserver.network.l2.c2s.ReplyGameGuardQuery;
import l2.gameserver.network.l2.c2s.RequestActionUse;
import l2.gameserver.network.l2.c2s.RequestAllyCrest;
import l2.gameserver.network.l2.c2s.RequestAllyInfo;
import l2.gameserver.network.l2.c2s.RequestAnswerJoinAlly;
import l2.gameserver.network.l2.c2s.RequestAnswerJoinParty;
import l2.gameserver.network.l2.c2s.RequestAnswerJoinPledge;
import l2.gameserver.network.l2.c2s.RequestAquireSkill;
import l2.gameserver.network.l2.c2s.RequestAquireSkillInfo;
import l2.gameserver.network.l2.c2s.RequestAskJoinPartyRoom;
import l2.gameserver.network.l2.c2s.RequestAutoSoulShot;
import l2.gameserver.network.l2.c2s.RequestBBSwrite;
import l2.gameserver.network.l2.c2s.RequestBlock;
import l2.gameserver.network.l2.c2s.RequestBuyItem;
import l2.gameserver.network.l2.c2s.RequestBuySeed;
import l2.gameserver.network.l2.c2s.RequestBypassToServer;
import l2.gameserver.network.l2.c2s.RequestBypassToServerInLobby;
import l2.gameserver.network.l2.c2s.RequestCastleSiegeAttackerList;
import l2.gameserver.network.l2.fermata.FermataPacketRegistry;
import l2.gameserver.network.l2.c2s.RequestCastleSiegeDefenderList;
import l2.gameserver.network.l2.c2s.RequestChangeMoveType;
import l2.gameserver.network.l2.c2s.RequestChangePetName;
import l2.gameserver.network.l2.c2s.RequestChangeWaitType;
import l2.gameserver.network.l2.c2s.RequestConfirmCancelItem;
import l2.gameserver.network.l2.c2s.RequestConfirmCastleSiegeWaitingList;
import l2.gameserver.network.l2.c2s.RequestConfirmGemStone;
import l2.gameserver.network.l2.c2s.RequestConfirmRefinerItem;
import l2.gameserver.network.l2.c2s.RequestConfirmTargetItem;
import l2.gameserver.network.l2.c2s.RequestCrystallizeItem;
import l2.gameserver.network.l2.c2s.RequestCursedWeaponList;
import l2.gameserver.network.l2.c2s.RequestCursedWeaponLocation;
import l2.gameserver.network.l2.c2s.RequestDeleteMacro;
import l2.gameserver.network.l2.c2s.RequestDestroyItem;
import l2.gameserver.network.l2.c2s.RequestDismissAlly;
import l2.gameserver.network.l2.c2s.RequestDismissPartyRoom;
import l2.gameserver.network.l2.c2s.RequestDropItem;
import l2.gameserver.network.l2.c2s.RequestDuelAnswerStart;
import l2.gameserver.network.l2.c2s.RequestDuelStart;
import l2.gameserver.network.l2.c2s.RequestDuelSurrender;
import l2.gameserver.network.l2.c2s.RequestEnchantItem;
import l2.gameserver.network.l2.c2s.RequestEx2ndPasswordCheck;
import l2.gameserver.network.l2.c2s.RequestEx2ndPasswordReq;
import l2.gameserver.network.l2.c2s.RequestEx2ndPasswordVerify;
import l2.gameserver.network.l2.c2s.RequestExEnchantSkill;
import l2.gameserver.network.l2.c2s.RequestExEnchantSkillInfo;
import l2.gameserver.network.l2.c2s.RequestExFishRanking;
import l2.gameserver.network.l2.c2s.RequestExMPCCAcceptJoin;
import l2.gameserver.network.l2.c2s.RequestExMPCCAskJoin;
import l2.gameserver.network.l2.c2s.RequestExMPCCShowPartyMembersInfo;
import l2.gameserver.network.l2.c2s.RequestExMagicSkillUseGround;
import l2.gameserver.network.l2.c2s.RequestExOustFromMPCC;
import l2.gameserver.network.l2.c2s.RequestExitPartyMatchingWaitingRoom;
import l2.gameserver.network.l2.c2s.RequestFriendAddReply;
import l2.gameserver.network.l2.c2s.RequestFriendDel;
import l2.gameserver.network.l2.c2s.RequestFriendInvite;
import l2.gameserver.network.l2.c2s.RequestFriendList;
import l2.gameserver.network.l2.c2s.RequestGMCommand;
import l2.gameserver.network.l2.c2s.RequestGetBossRecord;
import l2.gameserver.network.l2.c2s.RequestGetItemFromPet;
import l2.gameserver.network.l2.c2s.RequestGetOffVehicle;
import l2.gameserver.network.l2.c2s.RequestGetOnVehicle;
import l2.gameserver.network.l2.c2s.RequestGiveItemToPet;
import l2.gameserver.network.l2.c2s.RequestGiveNickName;
import l2.gameserver.network.l2.c2s.RequestGmList;
import l2.gameserver.network.l2.c2s.RequestHandOverPartyMaster;
import l2.gameserver.network.l2.c2s.RequestHennaEquip;
import l2.gameserver.network.l2.c2s.RequestHennaItemInfo;
import l2.gameserver.network.l2.c2s.RequestHennaList;
import l2.gameserver.network.l2.c2s.RequestHennaUnequip;
import l2.gameserver.network.l2.c2s.RequestHennaUnequipInfo;
import l2.gameserver.network.l2.c2s.RequestHennaUnequipList;
import l2.gameserver.network.l2.c2s.RequestItemList;
import l2.gameserver.network.l2.c2s.RequestJoinAlly;
import l2.gameserver.network.l2.c2s.RequestJoinCastleSiege;
import l2.gameserver.network.l2.c2s.RequestJoinParty;
import l2.gameserver.network.l2.c2s.RequestJoinPledge;
import l2.gameserver.network.l2.c2s.RequestLinkHtml;
import l2.gameserver.network.l2.c2s.RequestListPartyMatchingWaitingRoom;
import l2.gameserver.network.l2.c2s.RequestMagicSkillUse;
import l2.gameserver.network.l2.c2s.RequestMakeMacro;
import l2.gameserver.network.l2.c2s.RequestManorList;
import l2.gameserver.network.l2.c2s.RequestMoveToLocationInVehicle;
import l2.gameserver.network.l2.c2s.RequestMultiSellChoose;
import l2.gameserver.network.l2.c2s.RequestObserverEnd;
import l2.gameserver.network.l2.c2s.RequestOlympiadMatchList;
import l2.gameserver.network.l2.c2s.RequestOlympiadObserverEnd;
import l2.gameserver.network.l2.c2s.RequestOustAlly;
import l2.gameserver.network.l2.c2s.RequestOustFromPartyRoom;
import l2.gameserver.network.l2.c2s.RequestOustPartyMember;
import l2.gameserver.network.l2.c2s.RequestOustPledgeMember;
import l2.gameserver.network.l2.c2s.RequestPCCafeCouponUse;
import l2.gameserver.network.l2.c2s.RequestPackageSend;
import l2.gameserver.network.l2.c2s.RequestPackageSendableItemList;
import l2.gameserver.network.l2.c2s.RequestPartyMatchConfig;
import l2.gameserver.network.l2.c2s.RequestPartyMatchDetail;
import l2.gameserver.network.l2.c2s.RequestPartyMatchList;
import l2.gameserver.network.l2.c2s.RequestPetGetItem;
import l2.gameserver.network.l2.c2s.RequestPetUseItem;
import l2.gameserver.network.l2.c2s.RequestPetition;
import l2.gameserver.network.l2.c2s.RequestPetitionCancel;
import l2.gameserver.network.l2.c2s.RequestPledgeCrest;
import l2.gameserver.network.l2.c2s.RequestPledgeCrestLarge;
import l2.gameserver.network.l2.c2s.RequestPledgeExtendedInfo;
import l2.gameserver.network.l2.c2s.RequestPledgeInfo;
import l2.gameserver.network.l2.c2s.RequestPledgeMemberInfo;
import l2.gameserver.network.l2.c2s.RequestPledgeMemberList;
import l2.gameserver.network.l2.c2s.RequestPledgeMemberPowerInfo;
import l2.gameserver.network.l2.c2s.RequestPledgePower;
import l2.gameserver.network.l2.c2s.RequestPledgePowerGradeList;
import l2.gameserver.network.l2.c2s.RequestPledgeReorganizeMember;
import l2.gameserver.network.l2.c2s.RequestPledgeSetAcademyMaster;
import l2.gameserver.network.l2.c2s.RequestPledgeSetMemberPowerGrade;
import l2.gameserver.network.l2.c2s.RequestPledgeWarList;
import l2.gameserver.network.l2.c2s.RequestPreviewItem;
import l2.gameserver.network.l2.c2s.RequestPrivateStoreBuy;
import l2.gameserver.network.l2.c2s.RequestPrivateStoreBuySellList;
import l2.gameserver.network.l2.c2s.RequestPrivateStoreManageBuy;
import l2.gameserver.network.l2.c2s.RequestPrivateStoreQuitBuy;
import l2.gameserver.network.l2.c2s.RequestPrivateStoreQuitSell;
import l2.gameserver.network.l2.c2s.RequestPrivateStoreSell;
import l2.gameserver.network.l2.c2s.RequestProcureCropList;
import l2.gameserver.network.l2.c2s.RequestQuestAbort;
import l2.gameserver.network.l2.c2s.RequestQuestList;
import l2.gameserver.network.l2.c2s.RequestRecipeBookOpen;
import l2.gameserver.network.l2.c2s.RequestRecipeItemDelete;
import l2.gameserver.network.l2.c2s.RequestRecipeItemMakeInfo;
import l2.gameserver.network.l2.c2s.RequestRecipeItemMakeSelf;
import l2.gameserver.network.l2.c2s.RequestRecipeShopListSet;
import l2.gameserver.network.l2.c2s.RequestRecipeShopMakeDo;
import l2.gameserver.network.l2.c2s.RequestRecipeShopMakeInfo;
import l2.gameserver.network.l2.c2s.RequestRecipeShopManageQuit;
import l2.gameserver.network.l2.c2s.RequestRecipeShopMessageSet;
import l2.gameserver.network.l2.c2s.RequestRecipeShopSellList;
import l2.gameserver.network.l2.c2s.RequestRefine;
import l2.gameserver.network.l2.c2s.RequestRefineCancel;
import l2.gameserver.network.l2.c2s.RequestReload;
import l2.gameserver.network.l2.c2s.RequestRestart;
import l2.gameserver.network.l2.c2s.RequestRestartPoint;
import l2.gameserver.network.l2.c2s.RequestSSQStatus;
import l2.gameserver.network.l2.c2s.RequestSellItem;
import l2.gameserver.network.l2.c2s.RequestSendL2FriendSay;
import l2.gameserver.network.l2.c2s.RequestSetAllyCrest;
import l2.gameserver.network.l2.c2s.RequestSetCastleSiegeTime;
import l2.gameserver.network.l2.c2s.RequestSetCrop;
import l2.gameserver.network.l2.c2s.RequestSetPledgeCrest;
import l2.gameserver.network.l2.c2s.RequestSetPledgeCrestLarge;
import l2.gameserver.network.l2.c2s.RequestSetSeed;
import l2.gameserver.network.l2.c2s.RequestShortCutDel;
import l2.gameserver.network.l2.c2s.RequestShortCutReg;
import l2.gameserver.network.l2.c2s.RequestShowBoard;
import l2.gameserver.network.l2.c2s.RequestShowMiniMap;
import l2.gameserver.network.l2.c2s.RequestSiegeInfo;
import l2.gameserver.network.l2.c2s.RequestSkillList;
import l2.gameserver.network.l2.c2s.RequestSocialAction;
import l2.gameserver.network.l2.c2s.RequestStartPledgeWar;
import l2.gameserver.network.l2.c2s.RequestStopPledgeWar;
import l2.gameserver.network.l2.c2s.RequestTargetCanceld;
import l2.gameserver.network.l2.c2s.RequestTutorialClientEvent;
import l2.gameserver.network.l2.c2s.RequestTutorialLinkHtml;
import l2.gameserver.network.l2.c2s.RequestTutorialPassCmdToServer;
import l2.gameserver.network.l2.c2s.RequestTutorialQuestionMark;
import l2.gameserver.network.l2.c2s.RequestUnEquipItem;
import l2.gameserver.network.l2.c2s.RequestVoteNew;
import l2.gameserver.network.l2.c2s.RequestWithDrawalParty;
import l2.gameserver.network.l2.c2s.RequestWithdrawAlly;
import l2.gameserver.network.l2.c2s.RequestWithdrawPartyRoom;
import l2.gameserver.network.l2.c2s.RequestWithdrawalPledge;
import l2.gameserver.network.l2.c2s.RequestWriteHeroWords;
import l2.gameserver.network.l2.c2s.Say2C;
import l2.gameserver.network.l2.c2s.SendBypassBuildCmd;
import l2.gameserver.network.l2.c2s.SendWareHouseDepositList;
import l2.gameserver.network.l2.c2s.SendWareHouseWithDrawList;
import l2.gameserver.network.l2.c2s.SetPrivateStoreBuyList;
import l2.gameserver.network.l2.c2s.SetPrivateStoreMsgBuy;
import l2.gameserver.network.l2.c2s.SetPrivateStoreMsgSell;
import l2.gameserver.network.l2.c2s.SetPrivateStoreSellList;
import l2.gameserver.network.l2.c2s.SnoopQuit;
import l2.gameserver.network.l2.c2s.StartRotatingC;
import l2.gameserver.network.l2.c2s.TradeDone;
import l2.gameserver.network.l2.c2s.TradeRequest;
import l2.gameserver.network.l2.c2s.UseItem;
import l2.gameserver.network.l2.c2s.ValidatePosition;
import l2.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GamePacketHandler
implements IClientFactory<GameClient>,
IMMOExecutor<GameClient>,
IPacketHandler<GameClient> {
    private static final Logger _log = LoggerFactory.getLogger(GamePacketHandler.class);

    @Override
    public ReceivablePacket<GameClient> handlePacket(ByteBuffer byteBuffer, GameClient gameClient) {
        int n;
        L2GameClientPacket l2GameClientPacket = null;
        if (CGModule.getInstance().isActive()) {
            if (!CGModule.getInstance().checkPacket(gameClient, byteBuffer)) {
                return null;
            }
            n = byteBuffer.get() & 0xFF;
            l2GameClientPacket = CGModule.getInstance().handlePacket(gameClient, n);
            if (l2GameClientPacket != null) {
                return l2GameClientPacket;
            }
        } else {
            n = byteBuffer.get() & 0xFF;
        }
        if (!gameClient.getPacketFilter().checkPacket(n)) {
            return null;
        }
        try {
            int n2 = 0;
            block1 : switch (gameClient.getState()) {
                case CONNECTED: {
                    switch (n) {
                        case 0: {
                            l2GameClientPacket = new ProtocolVersion();
                            break block1;
                        }
                        case 8: {
                            l2GameClientPacket = new AuthLogin();
                            break block1;
                        }
                        case 203: {
                            l2GameClientPacket = new ReplyGameGuardQuery();
                            break block1;
                        }
                        case 168: {
                            l2GameClientPacket = new NetPing();
                            break block1;
                        }
                        case 253: {
                            l2GameClientPacket = this.handleFermataPacket(byteBuffer, gameClient);
                            break block1;
                        }
                    }
                    gameClient.onUnknownPacket();
                    break;
                }
                case AUTHED: {
                    switch (n) {
                        case 9: {
                            l2GameClientPacket = new Logout();
                            break block1;
                        }
                        case 11: {
                            l2GameClientPacket = new CharacterCreate();
                            break block1;
                        }
                        case 12: {
                            l2GameClientPacket = new CharacterDelete();
                            break block1;
                        }
                        case 13: {
                            l2GameClientPacket = new CharacterSelected();
                            break block1;
                        }
                        case 14: {
                            l2GameClientPacket = new NewCharacter();
                            break block1;
                        }
                        case 33: {
                            l2GameClientPacket = new RequestBypassToServerInLobby();
                            break block1;
                        }
                        case 98: {
                            l2GameClientPacket = new CharacterRestore();
                            break block1;
                        }
                        case 202: {
                            l2GameClientPacket = new ReplyGameGuardQuery();
                            break block1;
                        }
                        case 208: {
                            n2 = byteBuffer.getShort() & 0xFFFF;
                            if (CGModule.getInstance().isActive() && (l2GameClientPacket = CGModule.getInstance().handlePacket(gameClient, n, (Integer)n2)) != null) {
                                return l2GameClientPacket;
                            }
                            if (!gameClient.getPacketFilter().checkPacketEx(n, n2)) {
                                return null;
                            }
                            switch (n2) {
                                case 54: {
                                    l2GameClientPacket = new GotoLobby();
                                    break block1;
                                }
                                case 147: {
                                    l2GameClientPacket = new RequestEx2ndPasswordCheck();
                                    break block1;
                                }
                                case 148: {
                                    l2GameClientPacket = new RequestEx2ndPasswordVerify();
                                    break block1;
                                }
                                case 149: {
                                    l2GameClientPacket = new RequestEx2ndPasswordReq();
                                    break block1;
                                }
                            }
                            gameClient.onUnknownPacket();
                            break block1;
                        }
                        case 168: {
                            l2GameClientPacket = new NetPing();
                            break block1;
                        }
                        case 253: {
                            l2GameClientPacket = this.handleFermataPacket(byteBuffer, gameClient);
                            break block1;
                        }
                    }
                    gameClient.onUnknownPacket();
                    break;
                }
                case IN_GAME: {
                    switch (n) {
                        case 1: {
                            l2GameClientPacket = new MoveBackwardToLocation();
                            break block1;
                        }
                        case 3: {
                            l2GameClientPacket = new EnterWorld();
                            break block1;
                        }
                        case 4: {
                            l2GameClientPacket = new Action();
                            break block1;
                        }
                        case 9: {
                            l2GameClientPacket = new Logout();
                            break block1;
                        }
                        case 10: {
                            l2GameClientPacket = new AttackRequest();
                            break block1;
                        }
                        case 15: {
                            l2GameClientPacket = new RequestItemList();
                            break block1;
                        }
                        case 16: {
                            break block1;
                        }
                        case 17: {
                            l2GameClientPacket = new RequestUnEquipItem();
                            break block1;
                        }
                        case 18: {
                            l2GameClientPacket = new RequestDropItem();
                            break block1;
                        }
                        case 20: {
                            l2GameClientPacket = new UseItem();
                            break block1;
                        }
                        case 21: {
                            l2GameClientPacket = new TradeRequest();
                            break block1;
                        }
                        case 22: {
                            l2GameClientPacket = new AddTradeItem();
                            break block1;
                        }
                        case 23: {
                            l2GameClientPacket = new TradeDone();
                            break block1;
                        }
                        case 27: {
                            l2GameClientPacket = new RequestSocialAction();
                            break block1;
                        }
                        case 28: {
                            l2GameClientPacket = new RequestChangeMoveType();
                            break block1;
                        }
                        case 29: {
                            l2GameClientPacket = new RequestChangeWaitType();
                            break block1;
                        }
                        case 30: {
                            l2GameClientPacket = new RequestSellItem();
                            break block1;
                        }
                        case 31: {
                            l2GameClientPacket = new RequestBuyItem();
                            break block1;
                        }
                        case 32: {
                            l2GameClientPacket = new RequestLinkHtml();
                            break block1;
                        }
                        case 33: {
                            l2GameClientPacket = new RequestBypassToServer();
                            break block1;
                        }
                        case 34: {
                            l2GameClientPacket = new RequestBBSwrite();
                            break block1;
                        }
                        case 36: {
                            l2GameClientPacket = new RequestJoinPledge();
                            break block1;
                        }
                        case 37: {
                            l2GameClientPacket = new RequestAnswerJoinPledge();
                            break block1;
                        }
                        case 38: {
                            l2GameClientPacket = new RequestWithdrawalPledge();
                            break block1;
                        }
                        case 39: {
                            l2GameClientPacket = new RequestOustPledgeMember();
                            break block1;
                        }
                        case 40: {
                            break block1;
                        }
                        case 41: {
                            l2GameClientPacket = new RequestJoinParty();
                            break block1;
                        }
                        case 42: {
                            l2GameClientPacket = new RequestAnswerJoinParty();
                            break block1;
                        }
                        case 43: {
                            l2GameClientPacket = new RequestWithDrawalParty();
                            break block1;
                        }
                        case 44: {
                            l2GameClientPacket = new RequestOustPartyMember();
                            break block1;
                        }
                        case 45: {
                            break block1;
                        }
                        case 47: {
                            l2GameClientPacket = new RequestMagicSkillUse();
                            break block1;
                        }
                        case 48: {
                            l2GameClientPacket = new Appearing();
                            break block1;
                        }
                        case 49: {
                            if (!Config.ALLOW_WAREHOUSE) break block1;
                            l2GameClientPacket = new SendWareHouseDepositList();
                            break block1;
                        }
                        case 50: {
                            l2GameClientPacket = new SendWareHouseWithDrawList();
                            break block1;
                        }
                        case 51: {
                            l2GameClientPacket = new RequestShortCutReg();
                            break block1;
                        }
                        case 53: {
                            l2GameClientPacket = new RequestShortCutDel();
                            break block1;
                        }
                        case 54: {
                            l2GameClientPacket = new CannotMoveAnymore();
                            break block1;
                        }
                        case 55: {
                            l2GameClientPacket = new RequestTargetCanceld();
                            break block1;
                        }
                        case 56: {
                            l2GameClientPacket = new Say2C();
                            break block1;
                        }
                        case 60: {
                            l2GameClientPacket = new RequestPledgeMemberList();
                            break block1;
                        }
                        case 63: {
                            l2GameClientPacket = new RequestSkillList();
                            break block1;
                        }
                        case 65: {
                            l2GameClientPacket = new MoveWithDelta();
                            break block1;
                        }
                        case 66: {
                            l2GameClientPacket = new RequestGetOnVehicle();
                            break block1;
                        }
                        case 67: {
                            l2GameClientPacket = new RequestGetOffVehicle();
                            break block1;
                        }
                        case 68: {
                            l2GameClientPacket = new AnswerTradeRequest();
                            break block1;
                        }
                        case 69: {
                            l2GameClientPacket = new RequestActionUse();
                            break block1;
                        }
                        case 70: {
                            l2GameClientPacket = new RequestRestart();
                            break block1;
                        }
                        case 71: {
                            l2GameClientPacket = new RequestSiegeInfo();
                            break block1;
                        }
                        case 72: {
                            l2GameClientPacket = new ValidatePosition();
                            break block1;
                        }
                        case 73: {
                            break block1;
                        }
                        case 74: {
                            l2GameClientPacket = new StartRotatingC();
                            break block1;
                        }
                        case 75: {
                            l2GameClientPacket = new FinishRotatingC();
                            break block1;
                        }
                        case 77: {
                            l2GameClientPacket = new RequestStartPledgeWar();
                            break block1;
                        }
                        case 79: {
                            l2GameClientPacket = new RequestStopPledgeWar();
                            break block1;
                        }
                        case 83: {
                            l2GameClientPacket = new RequestSetPledgeCrest();
                            break block1;
                        }
                        case 85: {
                            l2GameClientPacket = new RequestGiveNickName();
                            break block1;
                        }
                        case 87: {
                            l2GameClientPacket = new RequestShowBoard();
                            break block1;
                        }
                        case 88: {
                            l2GameClientPacket = new RequestEnchantItem();
                            break block1;
                        }
                        case 89: {
                            l2GameClientPacket = new RequestDestroyItem();
                            break block1;
                        }
                        case 91: {
                            l2GameClientPacket = new SendBypassBuildCmd();
                            break block1;
                        }
                        case 92: {
                            l2GameClientPacket = new RequestMoveToLocationInVehicle();
                            break block1;
                        }
                        case 93: {
                            l2GameClientPacket = new CannotMoveAnymoreInVehicle();
                            break block1;
                        }
                        case 94: {
                            l2GameClientPacket = new RequestFriendInvite();
                            break block1;
                        }
                        case 95: {
                            l2GameClientPacket = new RequestFriendAddReply();
                            break block1;
                        }
                        case 96: {
                            l2GameClientPacket = new RequestFriendList();
                            break block1;
                        }
                        case 97: {
                            l2GameClientPacket = new RequestFriendDel();
                            break block1;
                        }
                        case 99: {
                            l2GameClientPacket = new RequestQuestList();
                            break block1;
                        }
                        case 100: {
                            l2GameClientPacket = new RequestQuestAbort();
                            break block1;
                        }
                        case 102: {
                            l2GameClientPacket = new RequestPledgeInfo();
                            break block1;
                        }
                        case 103: {
                            l2GameClientPacket = new RequestPledgeExtendedInfo();
                            break block1;
                        }
                        case 104: {
                            l2GameClientPacket = new RequestPledgeCrest();
                            break block1;
                        }
                        case 106: {
                            break block1;
                        }
                        case 107: {
                            l2GameClientPacket = new RequestAquireSkillInfo();
                            break block1;
                        }
                        case 108: {
                            l2GameClientPacket = new RequestAquireSkill();
                            break block1;
                        }
                        case 109: {
                            l2GameClientPacket = new RequestRestartPoint();
                            break block1;
                        }
                        case 110: {
                            l2GameClientPacket = new RequestGMCommand();
                            break block1;
                        }
                        case 111: {
                            l2GameClientPacket = new RequestPartyMatchConfig();
                            break block1;
                        }
                        case 112: {
                            l2GameClientPacket = new RequestPartyMatchList();
                            break block1;
                        }
                        case 113: {
                            l2GameClientPacket = new RequestPartyMatchDetail();
                            break block1;
                        }
                        case 114: {
                            l2GameClientPacket = new RequestCrystallizeItem();
                            break block1;
                        }
                        case 115: {
                            l2GameClientPacket = new RequestPrivateStoreSell();
                            break block1;
                        }
                        case 116: {
                            l2GameClientPacket = new SetPrivateStoreSellList();
                            break block1;
                        }
                        case 117: {
                            break block1;
                        }
                        case 118: {
                            l2GameClientPacket = new RequestPrivateStoreQuitSell();
                            break block1;
                        }
                        case 119: {
                            l2GameClientPacket = new SetPrivateStoreMsgSell();
                            break block1;
                        }
                        case 120: {
                            break block1;
                        }
                        case 121: {
                            l2GameClientPacket = new RequestPrivateStoreManageBuy();
                            break block1;
                        }
                        case 122: {
                            break block1;
                        }
                        case 123: {
                            l2GameClientPacket = new RequestTutorialLinkHtml();
                            break block1;
                        }
                        case 124: {
                            l2GameClientPacket = new RequestTutorialPassCmdToServer();
                            break block1;
                        }
                        case 125: {
                            l2GameClientPacket = new RequestTutorialQuestionMark();
                            break block1;
                        }
                        case 126: {
                            l2GameClientPacket = new RequestTutorialClientEvent();
                            break block1;
                        }
                        case 127: {
                            l2GameClientPacket = new RequestPetition();
                            break block1;
                        }
                        case 128: {
                            l2GameClientPacket = new RequestPetitionCancel();
                            break block1;
                        }
                        case 129: {
                            l2GameClientPacket = new RequestGmList();
                            break block1;
                        }
                        case 130: {
                            l2GameClientPacket = new RequestJoinAlly();
                            break block1;
                        }
                        case 131: {
                            l2GameClientPacket = new RequestAnswerJoinAlly();
                            break block1;
                        }
                        case 132: {
                            l2GameClientPacket = new RequestWithdrawAlly();
                            break block1;
                        }
                        case 133: {
                            l2GameClientPacket = new RequestOustAlly();
                            break block1;
                        }
                        case 134: {
                            l2GameClientPacket = new RequestDismissAlly();
                            break block1;
                        }
                        case 135: {
                            l2GameClientPacket = new RequestSetAllyCrest();
                            break block1;
                        }
                        case 136: {
                            l2GameClientPacket = new RequestAllyCrest();
                            break block1;
                        }
                        case 137: {
                            l2GameClientPacket = new RequestChangePetName();
                            break block1;
                        }
                        case 138: {
                            l2GameClientPacket = new RequestPetUseItem();
                            break block1;
                        }
                        case 139: {
                            l2GameClientPacket = new RequestGiveItemToPet();
                            break block1;
                        }
                        case 140: {
                            l2GameClientPacket = new RequestGetItemFromPet();
                            break block1;
                        }
                        case 142: {
                            l2GameClientPacket = new RequestAllyInfo();
                            break block1;
                        }
                        case 143: {
                            l2GameClientPacket = new RequestPetGetItem();
                            break block1;
                        }
                        case 144: {
                            l2GameClientPacket = new RequestPrivateStoreBuy();
                            break block1;
                        }
                        case 145: {
                            l2GameClientPacket = new SetPrivateStoreBuyList();
                            break block1;
                        }
                        case 146: {
                            break block1;
                        }
                        case 147: {
                            l2GameClientPacket = new RequestPrivateStoreQuitBuy();
                            break block1;
                        }
                        case 148: {
                            l2GameClientPacket = new SetPrivateStoreMsgBuy();
                            break block1;
                        }
                        case 149: {
                            break block1;
                        }
                        case 150: {
                            l2GameClientPacket = new RequestPrivateStoreBuySellList();
                            break block1;
                        }
                        case 151: {
                            break block1;
                        }
                        case 152: {
                            break block1;
                        }
                        case 153: {
                            break block1;
                        }
                        case 154: {
                            break block1;
                        }
                        case 155: {
                            break block1;
                        }
                        case 156: {
                            break block1;
                        }
                        case 157: {
                            break block1;
                        }
                        case 158: {
                            l2GameClientPacket = new RequestPackageSendableItemList();
                            break block1;
                        }
                        case 159: {
                            l2GameClientPacket = new RequestPackageSend();
                            break block1;
                        }
                        case 160: {
                            l2GameClientPacket = new RequestBlock();
                            break block1;
                        }
                        case 161: {
                            break block1;
                        }
                        case 162: {
                            l2GameClientPacket = new RequestCastleSiegeAttackerList();
                            break block1;
                        }
                        case 163: {
                            l2GameClientPacket = new RequestCastleSiegeDefenderList();
                            break block1;
                        }
                        case 164: {
                            l2GameClientPacket = new RequestJoinCastleSiege();
                            break block1;
                        }
                        case 165: {
                            l2GameClientPacket = new RequestConfirmCastleSiegeWaitingList();
                            break block1;
                        }
                        case 166: {
                            l2GameClientPacket = new RequestSetCastleSiegeTime();
                            break block1;
                        }
                        case 167: {
                            l2GameClientPacket = new RequestMultiSellChoose();
                            break block1;
                        }
                        case 168: {
                            l2GameClientPacket = new NetPing();
                            break block1;
                        }
                        case 170: {
                            l2GameClientPacket = new BypassUserCmd();
                            break block1;
                        }
                        case 171: {
                            l2GameClientPacket = new SnoopQuit();
                            break block1;
                        }
                        case 172: {
                            l2GameClientPacket = new RequestRecipeBookOpen();
                            break block1;
                        }
                        case 173: {
                            l2GameClientPacket = new RequestRecipeItemDelete();
                            break block1;
                        }
                        case 174: {
                            l2GameClientPacket = new RequestRecipeItemMakeInfo();
                            break block1;
                        }
                        case 175: {
                            l2GameClientPacket = new RequestRecipeItemMakeSelf();
                            break block1;
                        }
                        case 176: {
                            break block1;
                        }
                        case 177: {
                            l2GameClientPacket = new RequestRecipeShopMessageSet();
                            break block1;
                        }
                        case 178: {
                            l2GameClientPacket = new RequestRecipeShopListSet();
                            break block1;
                        }
                        case 179: {
                            l2GameClientPacket = new RequestRecipeShopManageQuit();
                            break block1;
                        }
                        case 180: {
                            l2GameClientPacket = new SnoopQuit();
                            break block1;
                        }
                        case 181: {
                            l2GameClientPacket = new RequestRecipeShopMakeInfo();
                            break block1;
                        }
                        case 182: {
                            l2GameClientPacket = new RequestRecipeShopMakeDo();
                            break block1;
                        }
                        case 183: {
                            l2GameClientPacket = new RequestRecipeShopSellList();
                            break block1;
                        }
                        case 184: {
                            l2GameClientPacket = new RequestObserverEnd();
                            break block1;
                        }
                        case 185: {
                            l2GameClientPacket = new RequestVoteNew();
                            break block1;
                        }
                        case 186: {
                            l2GameClientPacket = new RequestHennaList();
                            break block1;
                        }
                        case 187: {
                            l2GameClientPacket = new RequestHennaItemInfo();
                            break block1;
                        }
                        case 188: {
                            l2GameClientPacket = new RequestHennaEquip();
                            break block1;
                        }
                        case 189: {
                            l2GameClientPacket = new RequestHennaUnequipList();
                            break block1;
                        }
                        case 190: {
                            l2GameClientPacket = new RequestHennaUnequipInfo();
                            break block1;
                        }
                        case 191: {
                            l2GameClientPacket = new RequestHennaUnequip();
                            break block1;
                        }
                        case 192: {
                            l2GameClientPacket = new RequestPledgePower();
                            break block1;
                        }
                        case 193: {
                            l2GameClientPacket = new RequestMakeMacro();
                            break block1;
                        }
                        case 194: {
                            l2GameClientPacket = new RequestDeleteMacro();
                            break block1;
                        }
                        case 195: {
                            l2GameClientPacket = new RequestHennaItemInfo();
                            break block1;
                        }
                        case 196: {
                            l2GameClientPacket = new RequestBuySeed();
                            break block1;
                        }
                        case 197: {
                            l2GameClientPacket = new ConfirmDlg();
                            break block1;
                        }
                        case 198: {
                            l2GameClientPacket = new RequestPreviewItem();
                            break block1;
                        }
                        case 199: {
                            l2GameClientPacket = new RequestSSQStatus();
                            break block1;
                        }
                        case 200: {
                            l2GameClientPacket = new PetitionVote();
                            break block1;
                        }
                        case 202: {
                            l2GameClientPacket = new ReplyGameGuardQuery();
                            break block1;
                        }
                        case 204: {
                            l2GameClientPacket = new RequestSendL2FriendSay();
                            break block1;
                        }
                        case 205: {
                            l2GameClientPacket = new RequestShowMiniMap();
                            break block1;
                        }
                        case 206: {
                            break block1;
                        }
                        case 207: {
                            l2GameClientPacket = new RequestReload();
                            break block1;
                        }
                        case 253: {
                            l2GameClientPacket = this.handleFermataPacket(byteBuffer, gameClient);
                            break block1;
                        }
                        case 208: {
                            if (byteBuffer.remaining() < 2) {
                                Log.network("Client: " + gameClient.toString() + " sent a 0xd0 without the second opcode.");
                                break block1;
                            }
                            n2 = byteBuffer.getShort() & 0xFFFF;
                            if (CGModule.getInstance().isActive() && (l2GameClientPacket = CGModule.getInstance().handlePacket(gameClient, n, (Integer)n2)) != null) {
                                return l2GameClientPacket;
                            }
                            if (!gameClient.getPacketFilter().checkPacketEx(n, n2)) {
                                return null;
                            }
                            switch (n2) {
                                case 1: {
                                    l2GameClientPacket = new RequestOustFromPartyRoom();
                                    break block1;
                                }
                                case 2: {
                                    l2GameClientPacket = new RequestDismissPartyRoom();
                                    break block1;
                                }
                                case 3: {
                                    l2GameClientPacket = new RequestWithdrawPartyRoom();
                                    break block1;
                                }
                                case 4: {
                                    l2GameClientPacket = new RequestHandOverPartyMaster();
                                    break block1;
                                }
                                case 5: {
                                    l2GameClientPacket = new RequestAutoSoulShot();
                                    break block1;
                                }
                                case 6: {
                                    l2GameClientPacket = new RequestExEnchantSkillInfo();
                                    break block1;
                                }
                                case 7: {
                                    l2GameClientPacket = new RequestExEnchantSkill();
                                    break block1;
                                }
                                case 8: {
                                    l2GameClientPacket = new RequestManorList();
                                    break block1;
                                }
                                case 9: {
                                    l2GameClientPacket = new RequestProcureCropList();
                                    break block1;
                                }
                                case 10: {
                                    l2GameClientPacket = new RequestSetSeed();
                                    break block1;
                                }
                                case 11: {
                                    l2GameClientPacket = new RequestSetCrop();
                                    break block1;
                                }
                                case 12: {
                                    l2GameClientPacket = new RequestWriteHeroWords();
                                    break block1;
                                }
                                case 13: {
                                    l2GameClientPacket = new RequestExMPCCAskJoin();
                                    break block1;
                                }
                                case 14: {
                                    l2GameClientPacket = new RequestExMPCCAcceptJoin();
                                    break block1;
                                }
                                case 15: {
                                    l2GameClientPacket = new RequestExOustFromMPCC();
                                    break block1;
                                }
                                case 16: {
                                    l2GameClientPacket = new RequestPledgeCrestLarge();
                                    break block1;
                                }
                                case 17: {
                                    l2GameClientPacket = new RequestSetPledgeCrestLarge();
                                    break block1;
                                }
                                case 18: {
                                    l2GameClientPacket = new RequestOlympiadObserverEnd();
                                    break block1;
                                }
                                case 19: {
                                    l2GameClientPacket = new RequestOlympiadMatchList();
                                    break block1;
                                }
                                case 20: {
                                    l2GameClientPacket = new RequestAskJoinPartyRoom();
                                    break block1;
                                }
                                case 21: {
                                    l2GameClientPacket = new AnswerJoinPartyRoom();
                                    break block1;
                                }
                                case 22: {
                                    l2GameClientPacket = new RequestListPartyMatchingWaitingRoom();
                                    break block1;
                                }
                                case 23: {
                                    l2GameClientPacket = new RequestExitPartyMatchingWaitingRoom();
                                    break block1;
                                }
                                case 24: {
                                    l2GameClientPacket = new RequestGetBossRecord();
                                    break block1;
                                }
                                case 25: {
                                    l2GameClientPacket = new RequestPledgeSetAcademyMaster();
                                    break block1;
                                }
                                case 26: {
                                    l2GameClientPacket = new RequestPledgePowerGradeList();
                                    break block1;
                                }
                                case 27: {
                                    l2GameClientPacket = new RequestPledgeMemberPowerInfo();
                                    break block1;
                                }
                                case 28: {
                                    l2GameClientPacket = new RequestPledgeSetMemberPowerGrade();
                                    break block1;
                                }
                                case 29: {
                                    l2GameClientPacket = new RequestPledgeMemberInfo();
                                    break block1;
                                }
                                case 30: {
                                    l2GameClientPacket = new RequestPledgeWarList();
                                    break block1;
                                }
                                case 31: {
                                    l2GameClientPacket = new RequestExFishRanking();
                                    break block1;
                                }
                                case 32: {
                                    l2GameClientPacket = new RequestPCCafeCouponUse();
                                    break block1;
                                }
                                case 34: {
                                    l2GameClientPacket = new RequestCursedWeaponList();
                                    break block1;
                                }
                                case 35: {
                                    l2GameClientPacket = new RequestCursedWeaponLocation();
                                    break block1;
                                }
                                case 36: {
                                    l2GameClientPacket = new RequestPledgeReorganizeMember();
                                    break block1;
                                }
                                case 38: {
                                    l2GameClientPacket = new RequestExMPCCShowPartyMembersInfo();
                                    break block1;
                                }
                                case 39: {
                                    l2GameClientPacket = new RequestDuelStart();
                                    break block1;
                                }
                                case 40: {
                                    l2GameClientPacket = new RequestDuelAnswerStart();
                                    break block1;
                                }
                                case 41: {
                                    l2GameClientPacket = new RequestConfirmTargetItem();
                                    break block1;
                                }
                                case 42: {
                                    l2GameClientPacket = new RequestConfirmRefinerItem();
                                    break block1;
                                }
                                case 43: {
                                    l2GameClientPacket = new RequestConfirmGemStone();
                                    break block1;
                                }
                                case 44: {
                                    l2GameClientPacket = new RequestRefine();
                                    break block1;
                                }
                                case 45: {
                                    l2GameClientPacket = new RequestConfirmCancelItem();
                                    break block1;
                                }
                                case 46: {
                                    l2GameClientPacket = new RequestRefineCancel();
                                    break block1;
                                }
                                case 47: {
                                    l2GameClientPacket = new RequestExMagicSkillUseGround();
                                    break block1;
                                }
                                case 48: {
                                    l2GameClientPacket = new RequestDuelSurrender();
                                    break block1;
                                }
                            }
                            gameClient.onUnknownPacket();
                            break block1;
                        }
                    }
                    gameClient.onUnknownPacket();
                }
            }
        }
        catch (BufferUnderflowException bufferUnderflowException) {
            gameClient.onPacketReadFail();
        }
        if (l2GameClientPacket != null && !gameClient.getPacketFilter().checkPacket(l2GameClientPacket)) {
            return null;
        }
        return l2GameClientPacket;
    }

    private L2GameClientPacket handleFermataPacket(ByteBuffer byteBuffer, GameClient gameClient) {
        if (byteBuffer.remaining() < 2) {
            return FermataPacketRegistry.unknown();
        }
        int subOpcode = byteBuffer.getShort() & 0xFFFF;
if (subOpcode < FermataPacketRegistry.MIN_SUB_OPCODE || subOpcode > FermataPacketRegistry.MAX_SUB_OPCODE) {
            return FermataPacketRegistry.unknown();
        }
        return FermataPacketRegistry.resolve(subOpcode, byteBuffer, gameClient);
    }

    @Override
    public GameClient create(MMOConnection<GameClient> mMOConnection) {
        return new GameClient(mMOConnection);
    }

    @Override
    public void execute(Runnable runnable) {
        ThreadPoolManager.getInstance().execute(runnable);
    }
}


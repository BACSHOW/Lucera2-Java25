/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.util.HashMap;
import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.handler.bbs.CommunityBoardManager;
import l2.gameserver.handler.bbs.ICommunityBoardHandler;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.instancemanager.BypassManager;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.entity.oly.CompetitionController;
import l2.gameserver.model.entity.oly.HeroController;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.BrowserBypassPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.Scripts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestBypassToServer
extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestBypassToServer.class);
    private BypassManager.DecodedBypass abr = null;
    private String _bypass;

    @Override
    protected void readImpl() {
        this._bypass = this.readS();
    }

    @Override
    protected void runImpl() {
        GameClient gameClient = (GameClient)this.getClient();
        Player player = gameClient.getActiveChar();
        if (player == null) {
            return;
        }
        if (!player.checkAndSetBypassDebounce(this._bypass, (long) Config.REQUEST_BYPASS_DEBOUNCE_TIME_MS, Config.REQUEST_BYPASS_MAX_PACKETS_PER_SECOND)) {
            return;
        }
        try {
            if (this._bypass == null || this._bypass.isEmpty() || (this.abr = gameClient.decodeBypass(this._bypass)) == null) {
                return;
            }
            NpcInstance npcInstance = player.getLastNpc();
            GameObject gameObject = player.getTarget();
            if (npcInstance == null && gameObject != null && gameObject.isNpc()) {
                npcInstance = (NpcInstance)gameObject;
            }
            if (this.abr.bypass.startsWith("admin_")) {
                AdminCommandHandler.getInstance().useAdminCommandHandler(player, this.abr.bypass);
            } else if (this.abr.bypass.equals("come_here") && player.isGM()) {
                RequestBypassToServer.b((GameClient)this.getClient());
            } else if (this.abr.bypass.startsWith("player_help ")) {
                RequestBypassToServer.p(player, this.abr.bypass.substring(12));
            } else if (this.abr.bypass.startsWith("scripts_")) {
                String string = this.abr.bypass.substring(8).trim();
                String[] stringArray = string.split("\\s+");
                String[] stringArray2 = string.substring(stringArray[0].length()).trim().split("\\s+");
                String[] stringArray3 = stringArray[0].split(":");
                if (stringArray3.length != 2) {
                    _log.warn("Bad Script bypass!");
                    return;
                }
                HashMap<String, Object> hashMap = null;
                if (npcInstance != null) {
                    hashMap = new HashMap<String, Object>(1);
                    hashMap.put("npc", npcInstance.getRef());
                }
                if (stringArray.length == 1) {
                    Scripts.getInstance().callScripts(player, stringArray3[0], stringArray3[1], hashMap);
                } else {
                    Scripts.getInstance().callScripts(player, stringArray3[0], stringArray3[1], new Object[]{stringArray2}, hashMap);
                }
            } else if (this.abr.bypass.startsWith("user_")) {
                String string = this.abr.bypass.substring(5).trim();
                String string2 = string.split("\\s+")[0];
                String string3 = string.substring(string2.length()).trim();
                IVoicedCommandHandler iVoicedCommandHandler = VoicedCommandHandler.getInstance().getVoicedCommandHandler(string2);
                if (iVoicedCommandHandler != null) {
                    iVoicedCommandHandler.useVoicedCommand(string2, player, string3);
                } else {
                    _log.warn("Unknown voiced command '" + string2 + "'");
                }
            } else if (this.abr.bypass.startsWith("npc_")) {
                int n = this.abr.bypass.indexOf(95, 5);
                String string = n > 0 ? this.abr.bypass.substring(4, n) : this.abr.bypass.substring(4);
                GameObject gameObject2 = player.getVisibleObject(Integer.parseInt(string));
                if (gameObject2 != null && gameObject2.isNpc() && n > 0 && gameObject2.isInActingRange(player)) {
                    player.setLastNpc((NpcInstance)gameObject2);
                    ((NpcInstance)gameObject2).onBypassFeedback(player, this.abr.bypass.substring(n + 1));
                }
            } else if (this.abr.bypass.startsWith("_olympiad") && this.abr.bypass.contains("?command=move_op_field&field=")) {
                if (!Config.OLY_SPECTATION_ALLOWED) {
                    return;
                }
                int n = -1;
                try {
                    n = Integer.parseInt(this.abr.bypass.substring(38));
                    CompetitionController.getInstance().watchCompetition(player, n);
                }
                catch (Exception exception) {
                    _log.warn("OlyObserver", exception);
                    exception.printStackTrace();
                }
            } else if (this.abr.bypass.startsWith("_diary")) {
                String string = this.abr.bypass.substring(this.abr.bypass.indexOf("?") + 1);
                StringTokenizer stringTokenizer = new StringTokenizer(string, "&");
                int n = Integer.parseInt(stringTokenizer.nextToken().split("=")[1]);
                int n2 = Integer.parseInt(stringTokenizer.nextToken().split("=")[1]);
                HeroController.getInstance().showHeroDiary(player, n, n2);
            } else if (this.abr.bypass.startsWith("_match")) {
                String string = this.abr.bypass.substring(this.abr.bypass.indexOf("?") + 1);
                StringTokenizer stringTokenizer = new StringTokenizer(string, "&");
                int n = Integer.parseInt(stringTokenizer.nextToken().split("=")[1]);
                int n3 = Integer.parseInt(stringTokenizer.nextToken().split("=")[1]);
                HeroController.getInstance().showHistory(player, n, n3);
            } else if (this.abr.bypass.startsWith("manor_menu_select")) {
                GameObject gameObject3 = player.getTarget();
                if (gameObject3 != null && gameObject3.isNpc()) {
                    ((NpcInstance)gameObject3).onBypassFeedback(player, this.abr.bypass);
                }
            } else if (Config.CUSTOM_PATCH_OPTIONS_ENABLE && this.abr.bypass.startsWith("interface?autoloot_on")) {
                player.setAutoLoot(true);
                player.sendMessage(new CustomMessage("usercommandhandlers.AutoLootAll", player, new Object[0]));
            } else if (Config.CUSTOM_PATCH_OPTIONS_ENABLE && this.abr.bypass.startsWith("interface?autoloot_off")) {
                player.setAutoLoot(false);
                player.setAutoLootHerbs(false);
                player.setAutoLootAdena(false);
                player.sendMessage(new CustomMessage("usercommandhandlers.AutoLootOff", player, new Object[0]));
            } else if (Config.CUSTOM_PATCH_OPTIONS_ENABLE && this.abr.bypass.startsWith("interface?exp_on")) {
                player.unsetVar("NoExp");
                player.sendMessage(new CustomMessage("usercommandhandlers.ExpOff", player, new Object[0]));
            } else if (Config.CUSTOM_PATCH_OPTIONS_ENABLE && this.abr.bypass.startsWith("interface?exp_off")) {
                player.setVar("NoExp", "1", -1L);
                player.sendMessage(new CustomMessage("usercommandhandlers.ExpOn", player, new Object[0]));
            } else if (Config.CUSTOM_PATCH_OPTIONS_ENABLE && this.abr.bypass.startsWith("interface?donate_ru")) {
                player.sendPacket((IStaticPacket)new BrowserBypassPacket(Config.CUSTOM_PATCH_DONATE_URL));
            } else if (Config.CUSTOM_PATCH_OPTIONS_ENABLE && this.abr.bypass.startsWith("interface?vk")) {
                player.sendPacket((IStaticPacket)new BrowserBypassPacket(Config.CUSTOM_PATCH_VK_URL));
            } else if (Config.CUSTOM_PATCH_OPTIONS_ENABLE && this.abr.bypass.startsWith("interface?facebook")) {
                player.sendPacket((IStaticPacket)new BrowserBypassPacket(Config.CUSTOM_PATCH_FACEBOOK_URL));
            } else if (Config.CUSTOM_DISPELL_EFFECTS && (this.abr.bypass.startsWith("_dispel") || this.abr.bypass.startsWith("interface?dispell="))) {
                int n = this.abr.bypass.indexOf(58);
                if (n < 0) {
                    return;
                }
                String string = this.abr.bypass.substring(n + 1);
                int n4 = string.indexOf(44);
                if (n4 <= 0) {
                    return;
                }
                String string4 = string.substring(0, n4).trim();
                String string5 = string.substring(n4 + 1).trim();
                if (!StringUtils.isNumeric(string4) || !StringUtils.isNumeric(string5)) {
                    return;
                }
                RequestBypassToServer.c(player, Integer.parseInt(string4), Integer.parseInt(string5));
            } else if (this.abr.bypass.startsWith("multisell ")) {
                MultiSellHolder.getInstance().SeparateAndSend(Integer.parseInt(this.abr.bypass.substring(10)), player, 0.0);
            } else if (this.abr.bypass.startsWith("Quest ")) {
                String string = this.abr.bypass.substring(6).trim();
                int n = string.indexOf(32);
                if (n < 0) {
                    player.processQuestEvent(string, "", npcInstance);
                } else {
                    player.processQuestEvent(string.substring(0, n), string.substring(n).trim(), npcInstance);
                }
            } else if (this.abr.bbs) {
                if (!Config.COMMUNITYBOARD_ENABLED) {
                    player.sendPacket((IStaticPacket)new SystemMessage(938));
                } else {
                    ICommunityBoardHandler iCommunityBoardHandler;
                    if (player.isGM()) {
                        Functions.sendDebugMessage(player, "Community Board Bypass: " + this.abr.bypass);
                    }
                    if ((iCommunityBoardHandler = CommunityBoardManager.getInstance().getCommunityHandler(this.abr.bypass, player)) != null) {
                        iCommunityBoardHandler.onBypassCommand(player, this.abr.bypass);
                    }
                }
            }
        }
        catch (Exception exception) {
            GameObject gameObject;
            String string = "Bad RequestBypassToServer: " + this.abr.bypass;
            GameObject gameObject4 = gameObject = player != null ? player.getTarget() : null;
            if (gameObject != null && gameObject.isNpc()) {
                string = string + " via NPC #" + ((NpcInstance)gameObject).getNpcId();
            }
            _log.error(string, exception);
        }
    }

    private static void b(GameClient gameClient) {
        GameObject gameObject = gameClient.getActiveChar().getTarget();
        if (gameObject != null && gameObject.isNpc()) {
            NpcInstance npcInstance = (NpcInstance)gameObject;
            Player player = gameClient.getActiveChar();
            npcInstance.setTarget(player);
            npcInstance.moveToLocation(player.getLoc(), 0, true);
        }
    }

    private static void p(Player player, String string) {
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);
        npcHtmlMessage.setFile(string);
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    private static void c(Player player, int n, int n2) {
        boolean bl = false;
        if (player.isOlyParticipant() && !Config.CUSTOM_DISPELL_EFFECTS_ON_OLYMPIAD) {
            return;
        }
        for (Effect effect : player.getEffectList().getAllEffects()) {
            if (effect.getDisplayId() != n || effect.getDisplayLevel() != n2) continue;
            if (!effect.isOffensive() && (!effect.getSkill().isMusic() || Config.CUSTOM_DISPELL_MUSIC_EFFECTS) && effect.getSkill().isSelfDispellable() && effect.getSkill().getSkillType() != Skill.SkillType.TRANSFORMATION) {
                effect.exit();
                bl = true;
                continue;
            }
            return;
        }
        if (bl) {
            player.sendPacket((IStaticPacket)new SystemMessage(92).addSkillName(n, n2));
        }
    }
}


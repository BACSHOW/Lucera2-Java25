/*
 * Decompiled with CFR 0.152.
 */
package services;

import l2.commons.lang.reference.HardReference;
import l2.commons.threading.RunnableImpl;
import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.listener.actor.OnKillListener;
import l2.gameserver.model.Creature;
import l2.gameserver.model.EffectList;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.scripts.Scripts;
import l2.gameserver.tables.SkillTable;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotCheckService
extends Functions
implements ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(BotCheckService.class);
    private static final String bWc = "BCSMonKillCnt";
    private static final BotCheckKillListener bWd = new BotCheckKillListener();
    private static final BotCheckVoicedCommandHandler bWe = new BotCheckVoicedCommandHandler();

    private static int aq(Player player) {
        return player.getVarInt(bWc, 0);
    }

    private static void n(Player player, int n) {
        if (n > 0) {
            player.setVar(bWc, n, -1L);
        } else {
            player.unsetVar(bWc);
        }
    }

    private static boolean o(Player player, int n) {
        if (n < Config.CAPTCHA_MONSTER_KILLS_THRESHOLD) {
            return false;
        }
        ThreadPoolManager.getInstance().execute(new BotCheckTask(player));
        return true;
    }

    private static void ar(Player player) {
        Skill skill = SkillTable.getInstance().getInfo(Config.CAPTCHA_PENALTY_SKILL_ID, Config.CAPTCHA_PENALTY_SKILL_LEVEL);
        skill.getEffects(player, player, false, false, 0L, 0.0, false);
        Scripts.getInstance().callScripts(player, "Util", "RequestCapcha", new Object[]{"services.BotCheckService:captchaCheckSucceed", player.getStoredId(), Integer.valueOf(30)});
    }

    public void captchaCheckSucceed() {
        Player player = this.getSelf();
        if (!Config.SERVICE_CAPTCHA_BOT_CHECK || player == null) {
            return;
        }
        BotCheckService.as(player);
    }

    private static void as(Player player) {
        BotCheckService.n(player, 0);
        Skill skill = SkillTable.getInstance().getInfo(Config.CAPTCHA_PENALTY_SKILL_ID, Config.CAPTCHA_PENALTY_SKILL_LEVEL);
        EffectList effectList = player.getEffectList();
        effectList.stopEffect(skill);
    }

    private static void at(Player player) {
        int n = BotCheckService.aq(player);
        if (!BotCheckService.o(player, n)) {
            BotCheckService.n(player, n + 1);
        }
    }

    @Override
    public void onLoad() {
        if (Config.SERVICE_CAPTCHA_BOT_CHECK) {
            VoicedCommandHandler.getInstance().registerVoicedCommandHandler(bWe);
            PlayerListenerList.addGlobal(bWd);
        }
    }

    @Override
    public void onReload() {
        this.onShutdown();
        this.onLoad();
    }

    @Override
    public void onShutdown() {
        PlayerListenerList.removeGlobal(bWd);
    }

    private static class BotCheckTask
    extends RunnableImpl {
        private final HardReference<Player> e;

        private BotCheckTask(Player player) {
            this.e = player.getRef();
        }

        @Override
        public void runImpl() throws Exception {
            Player player = this.e.get();
            if (player == null) {
                return;
            }
            BotCheckService.ar(player);
        }
    }

    private static class BotCheckVoicedCommandHandler
    implements IVoicedCommandHandler {
        private String[] zF = new String[]{"check_captcha"};

        private BotCheckVoicedCommandHandler() {
        }

        @Override
        public boolean useVoicedCommand(String string, Player player, String string2) {
            if (!Config.SERVICE_CAPTCHA_BOT_CHECK) {
                return false;
            }
            if (this.zF[0].equalsIgnoreCase(string)) {
                BotCheckService.o(player, BotCheckService.aq(player));
                return true;
            }
            return false;
        }

        @Override
        public String[] getVoicedCommandList() {
            if (!Config.SERVICE_CAPTCHA_BOT_CHECK) {
                return ArrayUtils.EMPTY_STRING_ARRAY;
            }
            return this.zF;
        }
    }

    private static class BotCheckKillListener
    implements OnKillListener {
        private BotCheckKillListener() {
        }

        @Override
        public void onKill(Creature creature, Creature creature2) {
            AutoFarmContext autoFarmContext = creature.getFarmSystem();
            if (!Config.SERVICE_CAPTCHA_BOT_CHECK || !creature.isPlayer() || !creature2.isMonster() || autoFarmContext != null && autoFarmContext.isAutofarming() || creature.isInAnyZone(Zone.ZoneType.peace_zone, Zone.ZoneType.fun, Zone.ZoneType.epic, Zone.ZoneType.battle_zone, Zone.ZoneType.RESIDENCE)) {
                return;
            }
            BotCheckService.at(creature.getPlayer());
        }

        @Override
        public boolean ignorePetOrSummon() {
            return true;
        }
    }
}

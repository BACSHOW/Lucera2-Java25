package services;

import l2.gameserver.ConfigReloadHooks;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.battlepass.BattlePassConfig;
import services.battlepass.BattlePassKillListener;
import services.battlepass.BattlePassService;
import services.battlepass.BattlePassStore;

/**
 * Battle Pass: bypasses da janela L2PassWnd do cliente e os comandos.
 *
 *   .pass / .battlepass        abre a janela
 *   .passreload                (GM) relê o battlepass.properties
 *   .passpoints [n]            (GM) soma n pontos a si mesmo (padrão 100)
 *   .passreset                 (GM) zera o próprio progresso
 *
 * Bypasses (scripts_services.BattlePass:...):
 *   bpPage PAGINA              manda a página (e marca a janela aberta)
 *   bpClose                    a janela fechou
 *   bpClaim DEGRAU TRILHA PAG  TRILHA 0 = grátis, 1 = premium; DEGRAU 1..N
 *   bpClaimAll PAGINA          pega tudo o que já dá
 *   bpPremium PAGINA           ativa o premium (cobra a moeda configurada)
 */
public class BattlePass extends Functions implements ScriptFile, IVoicedCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(BattlePass.class);
	private static final String[] COMMANDS = { "pass", "battlepass", "passreload", "passpoints", "passreset" };

	private static final BattlePassKillListener KILL = new BattlePassKillListener();
	private static final OnPlayerExitListener EXIT = new OnPlayerExitListener()
	{
		@Override
		public void onPlayerExit(Player player)
		{
			BattlePassService.onExit(player);
		}
	};

	@Override
	public void onLoad()
	{
		BattlePassConfig.load();
		// recarrega com //reload_config (Reload All Configs) e com o "Salvar e recarregar" da janela;
		// a gravacao periodica recomeca com o SaveInterval novo
		ConfigReloadHooks.register("BattlePass", () -> { BattlePassConfig.load(); BattlePassService.startSaveTask(); });
		BattlePassStore.Jdbc.createTable();
		CharListenerList.addGlobal(KILL);
		PlayerListenerList.addGlobal(EXIT);
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		BattlePassService.startSaveTask();
		_log.info("Loaded Service: Battle Pass (" + BattlePassConfig.STEPS.size() + " degraus, " + (BattlePassConfig.ENABLED ? "ligado" : "desligado") + ")");
	}

	@Override
	public void onReload()
	{
		BattlePassConfig.load();
		BattlePassService.startSaveTask();
	}

	@Override
	public void onShutdown()
	{
		BattlePassService.stopSaveTask();
		BattlePassService.saveAll();
		CharListenerList.removeGlobal(KILL);
		PlayerListenerList.removeGlobal(EXIT);
	}

	// ── comandos ──────────────────────────────────────────────────────

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		if (player == null)
			return false;
		if ("pass".equalsIgnoreCase(command) || "battlepass".equalsIgnoreCase(command))
		{
			BattlePassService.open(player);
			return true;
		}
		if (!player.isGM())
			return false;
		if ("passreload".equalsIgnoreCase(command))
		{
			BattlePassConfig.load();
			player.sendMessage("Battle Pass: battlepass.properties relido (" + BattlePassConfig.STEPS.size() + " degraus).");
			return true;
		}
		if ("passpoints".equalsIgnoreCase(command))
		{
			int n = inum(args == null ? new String[0] : args.trim().split("\\s+"), 0, 100);
			int now = BattlePassService.gmAddPoints(player, n);
			player.sendMessage("Battle Pass: " + now + " pontos.");
			return true;
		}
		if ("passreset".equalsIgnoreCase(command))
		{
			BattlePassService.gmReset(player);
			player.sendMessage("Battle Pass: progresso zerado.");
			return true;
		}
		return false;
	}

	private static int inum(String[] a, int i, int def)
	{
		if (a == null || i >= a.length)
			return def;
		try
		{
			return Integer.parseInt(a[i].trim());
		}
		catch (Exception e)
		{
			return def;
		}
	}

	// ── bypasses ──────────────────────────────────────────────────────

	public void bpPage()
	{
		bpPage(new String[0]);
	}

	public void bpPage(String[] a)
	{
		Player p = getSelf();
		if (p != null)
			BattlePassService.page(p, inum(a, 0, 1));
	}

	public void bpClose()
	{
		Player p = getSelf();
		if (p != null)
			BattlePassService.close(p);
	}

	public void bpClose(String[] a)
	{
		bpClose();
	}

	public void bpClaim(String[] a)
	{
		Player p = getSelf();
		if (p != null)
			BattlePassService.claim(p, inum(a, 0, 0), inum(a, 1, 0) == 1, inum(a, 2, 1));
	}

	public void bpClaimAll()
	{
		bpClaimAll(new String[0]);
	}

	public void bpClaimAll(String[] a)
	{
		Player p = getSelf();
		if (p != null)
			BattlePassService.claimAll(p, inum(a, 0, 1));
	}

	public void bpPremium()
	{
		bpPremium(new String[0]);
	}

	public void bpPremium(String[] a)
	{
		Player p = getSelf();
		if (p != null)
			BattlePassService.buyPremium(p, inum(a, 0, 1));
	}
}

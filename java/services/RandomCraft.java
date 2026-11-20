package services;

import l2.gameserver.ConfigReloadHooks;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.listener.actor.player.OnPlayerExitListener;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.PlayerListenerList;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.ServiceLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.randomcraft.RandomCraftConfig;
import services.randomcraft.RandomCraftService;
import services.randomcraft.RandomCraftStore;

/**
 * Random Craft: bypasses das janelas RandomCraftWndTest / RandomCraftChargingWnd
 * e os comandos.
 *
 *   .craft / .randomcraft     abre a janela
 *   .craftprizes / .craftpremios   abre a janela de prêmios e chances
 *   .craftreload              (GM) relê o randomcraft.properties
 *   .craftpoints [n]          (GM) soma n Craft Points a si mesmo (padrão 10)
 *   .craftreset               (GM) zera pontos, experiência e casas
 *
 * Bypasses (scripts_services.RandomCraft:...):
 *   rcState                   manda o estado (sorteia a lista se estiver vazia)
 *   rcRefresh                 atualiza a lista
 *   rcLock N                  trava / destrava a casa N (0..4)
 *   rcCreate                  cria (sorteia entre as 5 casas)
 *   rcPrizeList [pagina] [filtro]  manda o pacote PRIZES (janela nativa)
 *   rcPrizes [pagina] [filtro] versão em HTML (all, weapon, armor, other)
 *   rcList                    itens que dá para extrair
 *   rcPreview id:qtd ...      quanto rende a seleção
 *   rcProcess id:qtd ...      extrai a seleção
 */
public class RandomCraft extends Functions implements ScriptFile, IVoicedCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(RandomCraft.class);
	private static final String[] COMMANDS = { "craft", "randomcraft", "craftreload", "craftpoints", "craftreset", "craftprizes", "craftpremios" };

	private static final OnPlayerExitListener EXIT = new OnPlayerExitListener()
	{
		@Override
		public void onPlayerExit(Player player)
		{
			RandomCraftService.onExit(player);
		}
	};

	@Override
	public void onLoad()
	{
		RandomCraftConfig.load();
		// recarrega com //reload_config (Reload All Configs) e com o "Salvar e recarregar" da janela
		ConfigReloadHooks.register("RandomCraft", () -> { RandomCraftConfig.load(); logSection(); });
		RandomCraftStore.Jdbc.createTable();
		PlayerListenerList.addGlobal(EXIT);
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		logSection();
	}

	/** Bloco "=== RANDOM CRAFT ===" do console (boot e //reload_config). */
	private static void logSection()
	{
		ServiceLog.section("RANDOM CRAFT")
			.add("Random Craft: " + ServiceLog.onOff(RandomCraftConfig.ENABLED) + " | " + RandomCraftConfig.POOL.size() + " recompensas | "
				+ RandomCraftConfig.EXTRACT.size() + " itens de extração")
			.add("Pontos: até " + RandomCraftConfig.MAX_POINTS + " (" + RandomCraftConfig.MAX_EXP + " de exp = 1 ponto) | até "
				+ RandomCraftConfig.MAX_LOCKED_SLOTS + " casas travadas")
			.print();
	}

	@Override
	public void onReload()
	{
		RandomCraftConfig.load();
	}

	@Override
	public void onShutdown()
	{
		PlayerListenerList.removeGlobal(EXIT);
	}

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
		if ("craft".equalsIgnoreCase(command) || "randomcraft".equalsIgnoreCase(command))
		{
			RandomCraftService.open(player);
			return true;
		}
		if ("craftprizes".equalsIgnoreCase(command) || "craftpremios".equalsIgnoreCase(command))
		{
			RandomCraftService.openPrizes(player);
			return true;
		}
		if (!player.isGM())
			return false;
		if ("craftreload".equalsIgnoreCase(command))
		{
			RandomCraftConfig.load();
			player.sendMessage("Random Craft: randomcraft.properties relido (" + RandomCraftConfig.POOL.size() + " recompensas).");
			return true;
		}
		if ("craftpoints".equalsIgnoreCase(command))
		{
			int n = inum(args == null ? new String[0] : args.trim().split("\\s+"), 0, 10);
			player.sendMessage("Random Craft: " + RandomCraftService.gmAddPoints(player, n) + " Craft Points.");
			return true;
		}
		if ("craftreset".equalsIgnoreCase(command))
		{
			RandomCraftService.gmReset(player);
			player.sendMessage("Random Craft: zerado.");
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

	public void rcOpen()
	{
		Player p = getSelf();
		if (p != null)
			RandomCraftService.open(p);
	}

	public void rcState()
	{
		Player p = getSelf();
		if (p != null)
			RandomCraftService.state(p);
	}

	public void rcRefresh()
	{
		Player p = getSelf();
		if (p != null)
			RandomCraftService.refresh(p);
	}

	public void rcLock(String[] a)
	{
		Player p = getSelf();
		if (p != null)
			RandomCraftService.lock(p, inum(a, 0, -1));
	}

	public void rcCreate()
	{
		Player p = getSelf();
		if (p != null)
			RandomCraftService.create(p);
	}

	public void rcPrizeList(String[] a)
	{
		Player p = getSelf();
		if (p != null)
			RandomCraftService.prizeList(p, a);
	}

	public void rcPrizeList()
	{
		rcPrizeList(new String[0]);
	}

	public void rcPrizes(String[] a)
	{
		Player p = getSelf();
		if (p != null)
			RandomCraftService.prizes(p, a);
	}

	public void rcPrizes()
	{
		rcPrizes(new String[0]);
	}

	public void rcList()
	{
		Player p = getSelf();
		if (p != null)
			RandomCraftService.list(p);
	}

	public void rcPreview()
	{
		rcPreview(new String[0]);
	}

	public void rcPreview(String[] a)
	{
		Player p = getSelf();
		if (p != null)
			RandomCraftService.preview(p, a);
	}

	public void rcProcess(String[] a)
	{
		Player p = getSelf();
		if (p != null)
			RandomCraftService.process(p, a);
	}
}

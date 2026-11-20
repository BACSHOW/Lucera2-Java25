package services;

import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;
import l2.gameserver.ConfigReloadHooks;
import l2.gameserver.cache.ItemLinkCache;
import l2.gameserver.cache.ItemLinkCache.Snapshot;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ServiceLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Item no chat (shift+clique no inventário mostra o item com um ícone; clicar no ícone abre o tooltip).
 *
 * Bypasses (scripts_services.ItemLink:...), pedidos do ChatWnd:
 *   hello 1                 a janela pergunta se o sistema está ligado
 *   make OBJECTID PEDIDO    tira a foto do item do inventário e devolve o token
 *   open TOKEN PEDIDO       devolve a foto do token (para o tooltip)
 *
 * Respostas pelo TutorialViewerWnd: "ITEM_LINK|TYPE=...|..." → ChatWnd.ApplyItemLinkPacket.
 * O chat em si continua no Say2C normal (ban, flood, destinatários); lá o nome do link é trocado pelo real
 * (ItemLinkCache.rewriteChat).
 */
public class ItemLink extends Functions implements ScriptFile
{
	private static final Logger _log = LoggerFactory.getLogger(ItemLink.class);
	public static final String FILE = "config/custom/item_link.properties";
	public static final String CHANNEL = "ITEM_LINK";

	private static int maxEntries = 4096;
	private static int lifetimeSeconds = 3600;

	@Override
	public void onLoad()
	{
		load();
		ConfigReloadHooks.register("ItemLink", () -> { load(); logSection(); });
		logSection();
	}

	@Override
	public void onReload()
	{
		load();
	}

	@Override
	public void onShutdown()
	{
		ItemLinkCache.getInstance().clear();
	}

	private static void load()
	{
		boolean on = true;
		try
		{
			ExProperties p = Config.load(FILE);
			on = p.getProperty("ItemLinkEnabled", true);
			maxEntries = Math.max(128, Math.min(65536, p.getProperty("ItemLinkCacheEntries", 4096)));
			lifetimeSeconds = Math.max(60, Math.min(86400, p.getProperty("ItemLinkLifetimeSeconds", 3600)));
		}
		catch (Exception e)
		{
			_log.warn("ItemLink: não foi possível ler " + FILE + " (usando o padrão): " + e.getMessage());
		}
		ItemLinkCache.getInstance().configure(on, maxEntries, lifetimeSeconds * 1000L);
	}

	/** Bloco "=== ITEM NO CHAT ===" do console (boot e //reload_config). */
	private static void logSection()
	{
		ItemLinkCache c = ItemLinkCache.getInstance();
		ServiceLog.section("ITEM NO CHAT")
			.add("Sistema: " + ServiceLog.onOff(c.isEnabled()))
			.add("Links guardados: até " + maxEntries + ", cada um vale " + (lifetimeSeconds / 60) + " min")
			.print();
	}

	// =====================================================================
	// Bypasses
	// =====================================================================

	public void hello(String[] args)
	{
		Player player = getSelf();
		if (player != null && args != null && args.length == 1 && "1".equals(args[0]))
			sendFeatures(player);
	}

	public void make(String[] args)
	{
		Player player = getSelf();
		if (player == null || args == null || args.length != 2)
			return;
		int object = positive(args[0]);
		int request = positive(args[1]);
		if (request < 1 || request > 1000000)
			return;

		ItemLinkCache cache = ItemLinkCache.getInstance();
		if (!cache.isEnabled())
		{
			sendFeatures(player);
			error(player, "CREATE", request, "DISABLED");
			return;
		}
		long now = System.currentTimeMillis();
		if (!cache.allow(player.getObjectId(), true, now))
		{
			error(player, "CREATE", request, "RATE");
			return;
		}

		ItemInstance item = object > 0 ? player.getInventory().getItemByObjectId(object) : null;
		Snapshot snapshot = item != null && item.getOwnerId() == player.getObjectId() ? ItemLinkCache.snapshotOf(item) : null;
		if (snapshot == null)
		{
			error(player, "CREATE", request, "MISSING");
			return;
		}

		String token = cache.put(snapshot, now);
		send(player, "TYPE=CREATE|REQUEST=" + request + "|OBJECT=" + object + "|TOKEN=" + token + "|" + payload(snapshot));
	}

	public void open(String[] args)
	{
		Player player = getSelf();
		if (player == null || args == null || args.length != 2)
			return;
		int request = positive(args[1]);
		if (request < 1 || request > 1000000)
			return;

		ItemLinkCache cache = ItemLinkCache.getInstance();
		if (!cache.isEnabled())
		{
			error(player, "SHOW", request, "DISABLED");
			return;
		}
		long now = System.currentTimeMillis();
		if (!cache.allow(player.getObjectId(), false, now))
		{
			error(player, "SHOW", request, "RATE");
			return;
		}
		Snapshot snapshot = cache.get(args[0], now);
		if (snapshot == null)
		{
			error(player, "SHOW", request, "EXPIRED");
			return;
		}
		send(player, "TYPE=SHOW|REQUEST=" + request + "|TOKEN=" + args[0] + "|" + payload(snapshot));
	}

	// =====================================================================
	// Respostas
	// =====================================================================

	private static void sendFeatures(Player player)
	{
		boolean on = ItemLinkCache.getInstance().isEnabled();
		send(player, "TYPE=FEATURES|ENABLED=" + (on ? 1 : 0) + "|PROTOCOL=4|MAX=" + ItemLinkCache.MAX_PER_MESSAGE + "|");
	}

	private static void error(Player player, String action, int request, String code)
	{
		send(player, "TYPE=ERROR|ACTION=" + action + "|REQUEST=" + Math.max(0, request) + "|CODE=" + code + "|");
	}

	private static void send(Player player, String payload)
	{
		player.sendPacket(new TutorialShowHtml(CHANNEL + "|" + payload));
	}

	/**
	 * ITYPE e SLOT: o tipo do item (0 arma, 1 armadura/escudo, 2 joia, ...) e a parte do corpo, os mesmos valores do
	 * inventário. O cliente não tem esses dois no UIDATA_ITEM; sem eles o tooltip trata armadura como arma e não mostra
	 * "Upper Body / Heavy", "Two Handed" nem P. Def.
	 */
	private static String payload(Snapshot s)
	{
		ItemTemplate t = ItemHolder.getInstance().getTemplate(s.itemId());
		return "ITEM=" + s.itemId() + "|ENCHANT=" + s.enchant() + "|COUNT=" + s.count() + "|AUG1=" + s.augment1()
			+ "|AUG2=" + s.augment2() + "|SHADOW=" + s.shadowSeconds()
			+ "|ITYPE=" + (t != null ? t.getType2ForPackets() : -1) + "|SLOT=" + (t != null ? t.getBodyPart() : 0) + "|";
	}

	private static int positive(String value)
	{
		if (value == null || value.length() > 10)
			return -1;
		try
		{
			int parsed = Integer.parseInt(value);
			return parsed > 0 ? parsed : -1;
		}
		catch (NumberFormatException invalid)
		{
			return -1;
		}
	}
}

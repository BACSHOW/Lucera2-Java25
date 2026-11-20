package services;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2.gameserver.Config;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.s2c.ServerClose;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;

/**
 * Offline Hunting (portado do GiranForge, versao simplificada para o core Lucera).
 *
 * O jogador confirma (token, 30s); o cliente e desanexado do Player SEM parar o farm
 * (Player.detachNetConnectionKeepFarm) e o socket e fechado. O farm continua rodando
 * no servidor. Ao reconectar, a sessao e liberada e o farm segue normalmente.
 * A sessao termina (farm parado + scheduleDelete) quando o farm para por qualquer motivo
 * (tempo acabou, morte, zona proibida, etc.).
 */
public final class OfflineHuntingService
{
	private static final Logger LOG = LoggerFactory.getLogger(OfflineHuntingService.class);
	private static final long CONFIRM_MS = 30000L;
	private static final Map<Integer, Confirmation> PENDING = new ConcurrentHashMap<>();
	private static final Map<Integer, Session> SESSIONS = new ConcurrentHashMap<>();
	private static volatile boolean enabled;
	private static volatile boolean showButton;
	private static ScheduledFuture<?> watcher;

	private OfflineHuntingService()
	{
	}

	public static synchronized void start(Properties p)
	{
		enabled = Boolean.parseBoolean(p.getProperty("OfflineHuntingEnabled", "false"));
		showButton = Boolean.parseBoolean(p.getProperty("InterfaceShowOfflineHunting", "true"));
		if (watcher == null)
			watcher = ThreadPoolManager.getInstance().scheduleAtFixedRate(OfflineHuntingService::watch, 500L, 500L);
	}

	public static synchronized void stop()
	{
		enabled = false;
		PENDING.clear();
		if (watcher != null)
		{
			watcher.cancel(false);
			watcher = null;
		}
		for (Session s : SESSIONS.values())
			finish(s, true);
	}

	public static boolean isEnabled()
	{
		return enabled && AutoFarmPlus.isAutoFarmEnabled() && Config.ALLOW_AUTO_FARM;
	}

	public static boolean isButtonVisible()
	{
		return isEnabled() && showButton;
	}

	public static boolean isOffline(Player player)
	{
		Session s = player == null ? null : SESSIONS.get(player.getObjectId());
		return s != null && s.player == player;
	}

	public static void request(Player player)
	{
		if (player == null)
			return;
		String failure = rejection(player);
		if (failure != null)
		{
			reject(player, failure);
			return;
		}
		Confirmation c = new Confirmation(player.getNetConnection());
		PENDING.put(player.getObjectId(), c);
		player.sendPacket(new TutorialShowHtml("OFFLINE_HUNTING|ACTION=CONFIRM|TOKEN=" + c.token + "|VISIBLE=1|ACTIVE=0|"));
	}

	public static void confirm(Player player, String[] args)
	{
		if (player == null)
			return;
		Confirmation c = PENDING.get(player.getObjectId());
		if (c == null || args == null || args.length != 1 || !c.token.equals(args[0])
				|| c.client != player.getNetConnection() || System.currentTimeMillis() > c.expiresAt
				|| !PENDING.remove(player.getObjectId(), c))
		{
			reject(player, "Offline Hunting: confirmacao expirou. Clique no botao novamente.");
			return;
		}
		String failure = rejection(player);
		if (failure != null)
		{
			reject(player, failure);
			return;
		}

		final GameClient client = c.client;
		final Session session = new Session(player, client);
		synchronized (session)
		{
			if (SESSIONS.putIfAbsent(player.getObjectId(), session) != null)
				return;
			try
			{
				preserveTitle(player);
				player.sendPacket(new TutorialShowHtml("OFFLINE_HUNTING|ACTION=STATE|VISIBLE=1|ACTIVE=1|"));
				client.setActiveChar(null);
				if (!player.detachNetConnectionKeepFarm(client))
					throw new IllegalStateException("Player connection changed during detach");
				client.close(ServerClose.STATIC);
			}
			catch (Exception e)
			{
				SESSIONS.remove(player.getObjectId(), session);
				restoreTitle(session);
				if (restoreClient(session))
					reject(player, "Offline Hunting: nao foi possivel desconectar. Tente novamente.");
				else if (player.getNetConnection() == null)
				{
					stopFarm(player);
					player.scheduleDelete(1000L);
				}
				LOG.error("OfflineHunting: falha ao desanexar " + player.getObjectId(), e);
			}
		}
	}

	/** Chamado pelo EnterListener (inclui reconexao). */
	public static void onPlayerEnter(Player player)
	{
		if (player == null)
			return;
		PENDING.remove(player.getObjectId());
		Session s = SESSIONS.get(player.getObjectId());
		LOG.info("OfflineHunting: enter " + player.getName() + " obj=" + System.identityHashCode(player) + " session=" + (s != null) + " farming=" + player.getFarmSystem().isAutofarming());
		if (s != null && hasReplacementClient(s))
			finish(s, false);
	}

	private static String rejection(Player player)
	{
		if (!isButtonVisible())
			return "Offline Hunting esta desativado no servidor.";
		GameClient client = player.getNetConnection();
		if (!player.isOnline() || player.isLogoutStarted() || player.isInOfflineMode() || client == null
				|| !client.isConnected() || client.getActiveChar() != player
				|| GameObjectsStorage.getPlayer(player.getObjectId()) != player)
			return "Offline Hunting: sessao do personagem nao esta pronta.";
		if (invalidState(player))
			return "Offline Hunting: indisponivel neste modo/local (morto, loja, duelo, olimpiada, evento, instancia).";
		if (player.getPvpFlag() != 0)
			return "Offline Hunting: indisponivel com PvP flag ativa.";
		AutoFarmContext farm = player.getFarmSystem();
		if (farm == null || !farm.isAutofarming())
			return "Offline Hunting: ative o AutoFarm primeiro.";
		return null;
	}

	private static boolean invalidState(Player player)
	{
		if (!isEnabled() || !player.isOnline() || player.isLogoutStarted() || player.isDead() || player.isAlikeDead()
				|| player.isTeleporting() || player.isInStoreMode() || player.isInDuel() || player.isOlyParticipant()
				|| player.isInObserverMode() || player.isCursedWeaponEquipped()
				|| player.getReflectionId() != 0)
			return true;
		if (Config.AUTO_FARM_FOR_PREMIUM && !player.hasBonus())
			return true;
		for (Zone z : player.getZones())
			if (Config.AUTO_FARM_LIMIT_ZONE_NAMES.contains(z.getName()))
				return true;
		return false;
	}

	private static boolean hasReplacementClient(Session s)
	{
		GameClient cur = s.player.getNetConnection();
		return cur != null && cur != s.client;
	}

	private static boolean restoreClient(Session s)
	{
		if (!s.client.isConnected())
			return false;
		GameClient cur = s.player.getNetConnection();
		if (cur != s.client && (cur != null || !s.player.reattachNetConnection(s.client)))
			return false;
		s.client.setActiveChar(s.player);
		return true;
	}

	private static void reject(Player player, String msg)
	{
		player.sendMessage(msg);
		AutoFarmPlus.sendAutoFarmOptions(player);
	}

	private static void watch()
	{
		long now = System.currentTimeMillis();
		PENDING.entrySet().removeIf(e -> e.getValue().expiresAt < now);
		for (Session s : SESSIONS.values())
		{
			synchronized (s)
			{
				if (SESSIONS.get(s.player.getObjectId()) != s)
					continue;
				try
				{
					Player p = s.player;
					if (GameObjectsStorage.getPlayer(p.getObjectId()) != p || !p.isOnline() || p.isLogoutStarted())
					{
						SESSIONS.remove(p.getObjectId(), s);
						continue;
					}
					if (hasReplacementClient(s))
					{
						finish(s, false);
						continue;
					}
					if (s.client.isConnected())
					{
						// socket nao fechou: devolve o cliente
						if (now - s.startedAt > 5000L)
						{
							restoreClient(s);
							finish(s, false);
							reject(p, "Offline Hunting: a desconexao nao completou. Tente novamente.");
						}
						continue;
					}
					if (invalidState(p))
					{
						finish(s, true);
						continue;
					}
					if (!p.getFarmSystem().isAutofarming())
					{
						// Counter Attack pode estar religando o farm; da 60s de tolerancia
						if (AutoFarmPlus.ownsCounterResume(p))
						{
							// enquanto ainda ha PvP flag (luta em andamento) o relogio nao corre
							if (s.missingSince == 0L || p.getPvpFlag() != 0)
								s.missingSince = now;
							if (now - s.missingSince < 60000L)
								continue;
						}
						finish(s, true);
						continue;
					}
					s.missingSince = 0L;
					preserveTitle(p);
					if (!s.announced)
					{
						s.announced = true;
						p.broadcastCharInfo();
					}
				}
				catch (Exception e)
				{
					LOG.error("OfflineHunting: encerrando sessao com erro " + s.player.getObjectId(), e);
					try
					{
						finish(s, true);
					}
					catch (Exception ignored)
					{
					}
				}
			}
		}
	}

	private static void finish(Session s, boolean disconnect)
	{
		synchronized (s)
		{
			if (!SESSIONS.remove(s.player.getObjectId(), s))
				return;
			final Player p = s.player;
			LOG.info("OfflineHunting: finish " + p.getName() + " obj=" + System.identityHashCode(p) + " disconnect=" + disconnect + " farming=" + p.getFarmSystem().isAutofarming());
			final boolean reconnected = hasReplacementClient(s) && p.isConnected();
			if (disconnect && !reconnected)
			{
				try
				{
					AutoFarmPlus.clearOfflineCounterState(p);
					stopFarm(p);
				}
				catch (Exception e)
				{
					LOG.error("OfflineHunting: limpeza falhou " + p.getObjectId(), e);
				}
			}
			if (reconnected || !disconnect)
			{
				restoreTitle(s);
				if (p.isConnected())
				{
					AutoFarmPlus.sendAutoFarmOptions(p);
					p.broadcastCharInfo();
					final GameClient returning = p.getNetConnection();
					ThreadPoolManager.getInstance().schedule(() ->
					{
						if (p.getNetConnection() == returning && p.isConnected())
						{
							AutoFarmPlus.sendAutoFarmOptions(p);
							if (p.getFarmSystem() != null && p.getFarmSystem().isAutofarming())
								p.sendMessage("Auto Farming Ativado");
						}
					}, 1500L);
				}
			}
			else if (!hasReplacementClient(s))
			{
				restoreClient(s);
				p.scheduleDelete(1000L);
			}
		}
	}

	private static void stopFarm(Player p)
	{
		if (p.getFarmSystem() != null && p.getFarmSystem().isAutofarming())
			p.getFarmSystem().stopFarmTask(false);
	}

	private static void preserveTitle(Player p)
	{
		p.setDisconnectedTitle(p.getTitle());
		p.setDisconnectedTitleColor(p.getTitleColor());
	}

	private static void restoreTitle(Session s)
	{
		s.player.setDisconnectedTitle(s.previousTitle);
		s.player.setDisconnectedTitleColor(s.previousColor);
	}

	private static final class Confirmation
	{
		final String token = UUID.randomUUID().toString().replace("-", "");
		final GameClient client;
		final long expiresAt = System.currentTimeMillis() + CONFIRM_MS;

		Confirmation(GameClient client)
		{
			this.client = client;
		}
	}

	private static final class Session
	{
		final Player player;
		final GameClient client;
		final String previousTitle;
		final int previousColor;
		final long startedAt = System.currentTimeMillis();
		boolean announced;
		long missingSince;

		Session(Player player, GameClient client)
		{
			this.player = player;
			this.client = client;
			previousTitle = player.getDisconnectedTitle();
			previousColor = player.getDisconnectedTitleColor();
		}
	}
}
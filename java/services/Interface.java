package services;

import l2.gameserver.network.l2.s2c.*;
import services.TeleporterInterface.TeleLocation2;
import services.TeleporterInterface.TeleportLocationData2;
import services.auctionhouse.AuctionBid;
import services.auctionhouse.AuctionHolder;
import services.auctionhouse.AuctionHouseService;
import services.auctionhouse.AuctionPriceStats;
import services.auctionhouse.AuctionTableCommunity;
import services.auctionhouse.AuctionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2.gameserver.Announcements;
import l2.gameserver.Config;
import l2.gameserver.GameServer;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.MultiSellHolder;
import l2.gameserver.handler.admincommands.AdminCommandHandler;
import l2.gameserver.handler.items.IItemHandler;
import l2.gameserver.handler.items.RefineryHandler;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.listener.game.OnStartListener;
import l2.gameserver.model.Effect;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.model.Skill.SkillType;
import l2.gameserver.model.actor.instances.player.AutoFarmContext;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage.ScreenMessageAlign;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.utils.ItemFunctions;
import l2.gameserver.utils.Log;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Interface extends Functions implements ScriptFile, OnStartListener {
	private static final Logger _log = LoggerFactory.getLogger(Interface.class);
	public final int[] ALLOWED_MULTISELLS;
	/** Tempo minimo entre teleportes pela interface, em segundos (0 = sem limite). */
	private static final int TELEPORT_COOLDOWN_SECONDS = 10;
	private static final Map<Integer, Long> TELEPORT_LAST_USE = new ConcurrentHashMap<>();

	public Interface() {
		this.ALLOWED_MULTISELLS = AllowedConfigs.ALLOWED_MULTISELLS;
	}

	public void onLoad() 
	{
		GameServer.getInstance().addListener(this);
		TeleportLocationData2.getInstance().reload();
		AllowedConfigs.init();
		AdminCommandHandler.getInstance().registerAdminCommandHandler(new AdminReloadTeleports());
		AuctionTableCommunity.getInstance();
		AuctionPriceStats.ensureLoaded();             // histórico de preço do leilão
		AuctionHouseService.registerLoginNotice();    // "vendido enquanto estava offline"
		AutoFarmPlus.start();                         // Counter Attack, Respect Hunt, tipos de farm, Keep Location
		_log.info("Loaded Service: Interface Commands L2ACDC");
	}

	public void onReload() 
	{
		AutoFarmPlus.stop();
		AutoFarmPlus.start();
	}

	public void onShutdown() 
	{
		AutoFarmPlus.stop();
	}

	// ---- Autofarm: ponte usada pela interface (bypass scripts_services.Interface:<metodo>) ----
	public void syncAutoFarmOptions() 
	{
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			AutoFarmPlus.sendAutoFarmOptions(activeChar);
		}
	}

	public void toggleKeepLocation() 
	{
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			AutoFarmPlus.toggleKeepLocation(activeChar);
		}
	}

	public void openAutoSkill() 
	{
		Player activeChar = this.getSelf();
		if (activeChar != null && AutoFarmPlus.isAutoSkillEnabled()) {
			activeChar.sendPacket(new TutorialShowHtml("AUTOSKILL_UI:TOGGLE"));
		}
	}

	public void syncFarmType() 
	{
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			AutoFarmPlus.syncFarmType(activeChar);
		}
	}

	public void nextFarmType() 
	{
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			AutoFarmPlus.nextFarmType(activeChar);
		}
	}

	public void toggleCounterAttack() 
	{
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			AutoFarmPlus.toggleCounterAttack(activeChar);
		}
	}

	public void toggleRespectHunt() 
	{
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			AutoFarmPlus.toggleRespectHunt(activeChar);
		}
	}

	public void autoFarm(String[] args)
	{
		Player activeChar = this.getSelf();
		if (activeChar == null || args == null || args.length == 0)
			return;
		if ("offlineHunting".equalsIgnoreCase(args[0]))
			OfflineHuntingService.request(activeChar);
		else if ("confirmOfflineHunting".equalsIgnoreCase(args[0]))
			OfflineHuntingService.confirm(activeChar, java.util.Arrays.copyOfRange(args, 1, args.length));
	}

	public void autoFarm() 
	{

		Player activeChar = this.getSelf();
		AutoFarmContext autoFarmContext = activeChar.getFarmSystem();
		if (autoFarmContext == null) 
		{
			activeChar.sendMessage("Nao tem AutoFarm");
		} 
		else 
		{
			if (autoFarmContext.isAutofarming()) 
			{
				autoFarmContext.stopFarmTask(false);
				activeChar.sendMessage("Auto Farming Desativado");
			} 
			else if (autoFarmContext.isActiveAutofarm()) 
			{

				autoFarmContext.startFarmTask();
				// so avisa "Ativado" se ligou mesmo (limite, zona proibida, sem summon... nao ligam)
				if (autoFarmContext.isAutofarming())
					activeChar.sendMessage("Auto Farming Ativado");
				AutoFarmPlus.sendAutoFarmOptions(activeChar);   // a animacao do botao segue o estado real
			} 
			else 
			{
				activeChar.sendMessage("Autofarm indisponivel para seu personagem");
				AutoFarmPlus.sendAutoFarmOptions(activeChar);
			}
		}
	}

	public void bossStatus() {
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			IVoicedCommandHandler command = VoicedCommandHandler.getInstance().getVoicedCommandHandler("bstatus");
			if (command != null) {
				command.useVoicedCommand("bstatus", activeChar, "");
			}
		}
	}

	/** Ultimo pedido de autoShot "garantir ON/OFF" por jogador: objectId -> {shotId, estado, horario}. */
	private static final Map<Integer, long[]> AUTO_SHOT_LAST = new ConcurrentHashMap<>();
	private static final long AUTO_SHOT_REPEAT_MS = 1000L;

	public void autoShot(String[] shotId)
	{
		Player activeChar = this.getSelf();
		if (activeChar == null || shotId == null || shotId.length == 0) {
			return;
		}

		int _shotId;
		try {
			_shotId = Integer.parseInt(shotId[0].trim());
		} catch (Exception e) {
			_log.warn("Invalid autoShot item id.");
			return;
		}

		// -1 = toggle (bypass antigo), 0 = garantir OFF, 1 = garantir ON
		int requestedState = -1;
		for (int i = 1; i < shotId.length; i++) {
			String arg = shotId[i];
			if (arg == null) {
				continue;
			}
			arg = arg.trim();
			try {
				if (arg.startsWith("bEnable=")) {
					requestedState = Integer.parseInt(arg.substring("bEnable=".length()));
				} else if (arg.equals("0") || arg.equals("1")) {
					requestedState = Integer.parseInt(arg);
				}
			} catch (NumberFormatException ignored) {
			}
		}

		if (activeChar.getPrivateStoreType() != 0 || activeChar.isDead()) {
			return;
		}

		// O mesmo "garantir ON/OFF" repetido em menos de 1 s nao faz nada. A interface reenvia o pedido quando
		// recebe o ExAutoSoulShot; respondendo de novo, os dois ficavam num ciclo (dezenas de bypass por segundo,
		// barrados pelo packetfilter).
		if (requestedState != -1) {
			long now = System.currentTimeMillis();
			long[] last = AUTO_SHOT_LAST.get(activeChar.getObjectId());
			if (last != null && last[0] == _shotId && last[1] == requestedState && now - last[2] < AUTO_SHOT_REPEAT_MS) {
				return;
			}
			AUTO_SHOT_LAST.put(activeChar.getObjectId(), new long[] { _shotId, requestedState, now });
		}

		boolean isActive = activeChar.getAutoSoulShot().contains(_shotId);
		ItemInstance item = activeChar.getInventory().getItemByItemId(_shotId);

		if (requestedState == 0) {
			// ja desligado: a interface ja sabe, nao responde (evita o ciclo de pedido/resposta)
			if (isActive) {
				activeChar.removeAutoSoulShot(_shotId);
				activeChar.sendPacket(new ExAutoSoulShot(_shotId, false));
				if (item != null) {
					activeChar.sendPacket(new SystemMessage(1434).addString(item.getName()));
				}
			}
			return;
		}

		if (item == null || !item.getTemplate().isShotItem()) {
			return;
		}

		if (!item.getTemplate().testCondition(activeChar, item, false)) {
			String msg = "Player " + activeChar.getName() + " trying illegal item use, ban this player!";
			Log.add(msg, "illegal-actions");
			_log.warn(msg);
			return;
		}

		boolean turnOn = requestedState == 1 || (requestedState == -1 && !isActive);
		if (turnOn) {
			if (!isActive) {
				activeChar.addAutoSoulShot(_shotId);
				activeChar.sendPacket(new ExAutoSoulShot(_shotId, true));
				activeChar.sendPacket(new SystemMessage(1433).addString(item.getName()));
			}
			// ja ligado: nao reenvia o ExAutoSoulShot (a interface responderia com outro pedido)
			// sempre tenta carregar: se a arma ja esta carregada o handler apenas retorna
			IItemHandler handler = item.getTemplate().getHandler();
			if (handler != null) {
				handler.useItem(activeChar, item, false);
			}
		} else {
			activeChar.removeAutoSoulShot(_shotId);
			activeChar.sendPacket(new ExAutoSoulShot(_shotId, false));
			activeChar.sendPacket(new SystemMessage(1434).addString(item.getName()));
		}
	}

	public void GkGo() {
	}

	public void GkGo(String[] teleportId) {
		Player activeChar = this.getSelf();
		if (activeChar == null || teleportId == null || teleportId.length == 0) {
			return;
		}
		String _teleportId = teleportId[0].substring(1);
		if (_teleportId.isEmpty()) {
			return;
		}
		TeleLocation2 teleLocation = TeleportLocationData2.getInstance().getTeleportLocation(_teleportId);
		if (teleLocation == null) {
			activeChar.sendMessage("La ubicacion de teletransporte no existe.");
			return;
		}
		if (!checkallowed(activeChar)) {
			return;
		}
		if (teleLocation.isNoble() && !activeChar.isNoble()) {
			activeChar.sendMessage("Solo Los Nobles Pueden Ir a Esta Zone");
			return;
		}

		// Limite de tempo entre teleportes (GM nao tem limite).
		long now = System.currentTimeMillis();
		if (TELEPORT_COOLDOWN_SECONDS > 0 && !activeChar.isGM()) {
			Long last = TELEPORT_LAST_USE.get(activeChar.getObjectId());
			long waitMs = last == null ? 0L : last + TELEPORT_COOLDOWN_SECONDS * 1000L - now;
			if (waitMs > 0L) {
				activeChar.sendMessage("Aguarde " + ((waitMs + 999L) / 1000L) + " segundo(s) para teleportar novamente.");
				return;
			}
		}

		int price = activeChar.getLevel() >= 40 ? teleLocation.getPrice() : 0;
		if (price > 0) {
			if (activeChar.getAdena() < price) {
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
			// removeItem com aviso: mostra "X adena desapareceu" no chat, como lojas e Gatekeepers
			if (removeItem(activeChar, 57, price) < price) {
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}

		TELEPORT_LAST_USE.put(activeChar.getObjectId(), now);
		MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, teleLocation.getSkillEffectId(), 1, 1, 0L);
		activeChar.broadcastPacket(new L2GameServerPacket[]{MSU});
		activeChar.teleToLocation(teleLocation.getX(), teleLocation.getY(), teleLocation.getZ());
	}

	public void augmentOpen() {
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			activeChar.setRefineryHandler(RefineryHandler.getInstance());
			RefineryHandler.getInstance().onInitRefinery(activeChar);
		}
	}

	public void openHtml(String[] html) {
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			String _html = html[0];
			// So paginas comuns: nada de subir pasta (..), caminho absoluto ou paginas de GM (admin/).
			String path = _html.replace('\\', '/').toLowerCase();
			if (path.contains("..") || path.startsWith("/") || path.contains(":") || path.startsWith("admin/") || path.contains("/admin/")) {
				_log.warn("Interface: " + activeChar.getName() + " tentou abrir o html " + _html + " pelo bypass.");
				return;
			}
			NpcHtmlMessage msg = new NpcHtmlMessage(activeChar, null);
			String char_name = activeChar.getName();
			msg.setFile(_html);
			msg.replace("%charname%", char_name);
			activeChar.sendPacket(msg);
		}
	}

	public void autoFarmInfo() {
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			String comando = "autofarm";
			IVoicedCommandHandler command = VoicedCommandHandler.getInstance().getVoicedCommandHandler(comando);
			if (command != null) {
				command.useVoicedCommand(comando, activeChar, "");
			} else {
				_log.warn("Unknown voiced command '" + comando + "'");
			}
		}
	}

	public void radiusAutoFarm(String[] type) {
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			String param = type[0];
			AutoFarmContext farmSystem = activeChar.getFarmSystem();
			if (farmSystem == null) {
				activeChar.sendMessage("No Tienes AutoFarm");
			} else {
				if (param.equalsIgnoreCase("inc_radius")) {
					activeChar.setVar("farmDistance", Math.min(Config.SEARCH_DISTANCE, farmSystem.getFarmRadius() + 200), -1L);
					farmSystem.setRadiusValue(Math.min(Config.SEARCH_DISTANCE, farmSystem.getFarmRadius() + 200));
					activeChar.sendPacket(
							new ExShowScreenMessage("Auto Farm Range: " + farmSystem.getFarmRadius(), 3000, 0, ScreenMessageAlign.TOP_CENTER, false, 1, -1, true)
							);
					activeChar.sendMessage("Auto Farm Range: " + farmSystem.getFarmRadius());
				} else if (param.equalsIgnoreCase("dec_radius")) {
					activeChar.setVar("farmDistance", Math.max(200, farmSystem.getFarmRadius() - 200), -1L);
					farmSystem.setRadiusValue(Math.max(200, farmSystem.getFarmRadius() - 200));
					activeChar.sendPacket(
							new ExShowScreenMessage("Auto Farm Range: " + farmSystem.getFarmRadius(), 3000, 0, ScreenMessageAlign.TOP_CENTER, false, 1, -1, true)
							);
					activeChar.sendMessage("Auto Farm Range: " + farmSystem.getFarmRadius());
				}
			}
		}
	}

	public void interfaceDonate() {
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			String comando = "donate";
			NpcHtmlMessage msg = new NpcHtmlMessage(activeChar, null);
			msg.setFile("mods/htmls/" + comando + ".html");
			activeChar.sendPacket(msg);
		}
	}

	public void dispellEffect(String[] skillId) {
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			int _skillId = Integer.parseInt(skillId[0]);

			try {
				dispelEffect(activeChar, _skillId, -1);
			} catch (NumberFormatException e) {
				System.out.println("Comando de dispel incorrecto.");
			}
		}
	}

	public void openMultisell(String[] multisellId) {
		Player activeChar = this.getSelf();
		if (activeChar != null) {
			int multiSell = Integer.parseInt(multisellId[0]);
			if (multiSell != -1 && ArrayUtils.contains(this.ALLOWED_MULTISELLS, multiSell)) {
				MultiSellHolder.getInstance().SeparateAndSend(multiSell, activeChar, 0.0);
			} else {
				System.out
				.println(
						"Player "
								+ activeChar.getName()
								+ " tentando abrir a multisell "
								+ multiSell
								+ " via bypass que nao esta na lista de multisells permitidas."
						);
			}
		}
	}

	public static boolean checkallowed(Player activeChar) {
		String msg = null;
		if (activeChar.isSitting()) {
			msg = "No Puedes Usar El Teleport Sentado";
		} else if (activeChar.isDead()) {
			msg = "No Puedes Usar El Teleport Muerto";
		} else if (activeChar.getPvpFlag() > 0) {
			msg = "Estas En Modo Flag No Puedes Teleport";
		} else if (activeChar.getKarma() > 0) {
			msg = "Estas En Modo Pk No Puedes Teleport";
		} else if (activeChar.isInCombat()) {
			msg = "No Puedes Usar El Teleport En Combate";
		} else if (activeChar.isInDuel()) {
			msg = "No Puedes Usar El Teleport En Duelos";
		} else if (activeChar.isOlyParticipant()) {
			msg = "No Puedes Usar El Teleport En Olympiadas";
		} else if (activeChar.getVarB("jailed")) {
			msg = "No Puedes Usar El Teleport En Jail";
		}

		if (msg != null) {
			activeChar.sendMessage(msg);
		}

		return msg == null;
	}

	private static void dispelEffect(Player player, int skillId, int skillLevel) 
	{
		boolean removed = false;
		if (!player.isOlyParticipant() || Config.CUSTOM_DISPELL_EFFECTS_ON_OLYMPIAD) {
			for (Effect effect : player.getEffectList().getAllEffects()) {
				if (effect.getDisplayId() == skillId && (effect.getDisplayLevel() == skillLevel || skillLevel == -1)) 
				{
					if (effect.isOffensive()
							|| effect.getSkill().isMusic() && !Config.CUSTOM_DISPELL_MUSIC_EFFECTS
							|| !effect.getSkill().isSelfDispellable()
							|| effect.getSkill().getSkillType() == SkillType.TRANSFORMATION) {
						return;
					}

					effect.exit();
					removed = true;
				}
			}

			if (removed) 
			{
				player.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(skillId, 1));
			}
		}
	}

	// ================================================================
	// AUCTION HOUSE
	// ================================================================

	// Linhas por página da lista da aba 0 = linhas visíveis da AuctionListCtrl
	// (383 px de altura / 38 = 10 com a janela de 900x638)
	private static final int ITEMS_PER_PAGE = 10;

	// Última lista pedida por cada jogador (página, categoria, busca, grade,
	// ordem). Depois de um lance / compra o servidor manda a lista de novo
	// sozinho: um pedido do cliente colado na resposta pode se perder.
	private static final Map<Integer, String[]> AUC_LAST_LIST = new ConcurrentHashMap<>();

	// ── showAuctionList ─────────────────────────────────────────────
	// bypass: scripts_services.Interface:showAuctionList PAGE CAT SEARCH GRADE ORDER
	public void showAuctionList(String[] params)
	{
		Player player = this.getSelf();
		if (player == null) return;
		if (params == null) params = new String[0];

		AUC_LAST_LIST.put(player.getObjectId(), params.clone());
		sendAuctionList(player, params);
	}

	// A lista que o jogador estava vendo, de novo (depois de lance / compra)
	private static void resendAuctionList(Player player)
	{
		String[] params = AUC_LAST_LIST.get(player.getObjectId());
		if (params != null)
			sendAuctionList(player, params);
	}

	private static void sendAuctionList(Player player, String[] params)
	{
		int    page   = params.length > 0 ? safeInt(params[0], 1) : 1;
		String cat    = params.length > 1 ? params[1] : "ALL";
		String search = params.length > 2 ? params[2] : "";
		String grade  = params.length > 3 ? params[3] : "ALL";
		String order  = params.length > 4 ? params[4] : "TIME";

		if ("*null*".equals(search)) search = "";
		search = search.replace('+', ' ').trim();   // o cliente manda espaço como "+"

		List<AuctionHolder> list = filterAndSort(player, cat, search, grade, order);

		int totalPages = Math.max(1, (int) Math.ceil((double) list.size() / ITEMS_PER_PAGE));
		page = Math.max(1, Math.min(page, totalPages));

		int from = (page - 1) * ITEMS_PER_PAGE;
		int to   = Math.min(from + ITEMS_PER_PAGE, list.size());

		// lances superados: "My Bids (N)" na árvore e aviso na barra
		AuctionHouseService.sendMyBids(player);

		// totalPages: o cliente usa para Anterior/Próxima; itens = total da
		// busca (0 → o cliente mostra "Nenhum item encontrado...")
		player.sendMessage("AUC_PAGE:" + page + "|" + totalPages + "|" + list.size());
		player.sendMessage("AUC_SLOTS_START:");

		for (int i = from; i < to; i++)
			player.sendMessage("AUC_ROW_" + (i - from) + ":" + buildRowData(list.get(i), player));
	}

	// ── buildRowData ────────────────────────────────────────────────
	// classId|enchant|preço|tempo|itemType|bodyPart|auctionId|segundos|média|vendas|moeda
	//   |meuLance|últimaVenda|qtdÚltima|segundosDesdeÚltima|quantidade
	//   |tipo|parcial|preçoUnidade|qtdAnunciada|lances
	//   tipo 0 = leilão, 1 = preço fixo; parcial 1 = aceita compra parcial;
	//   lances = quantos lances o leilão já teve (0 no preço fixo);
	//   quantidade = o que resta, qtdAnunciada = a do começo (vendidas = diferença)
	//   média = preço médio das últimas vendas deste item (mesmo enchant e moeda),
	//   por unidade; vendas 0 = sem histórico (tooltip)
	//   meuLance = 0 sem lance seu, 1 você tem o maior lance, 2 seu lance foi superado
	//   últimaVenda = valor total da última venda (0 = nunca vendido), da
	//   quantidade qtdÚltima, há segundosDesdeÚltima segundos
	// Usado pelas duas listas (aba 0 e "meus itens à venda" da aba 1).
	// Nome, ícone e grade o cliente tira do classId (GetItemInfo).
	// ItemType e bodyPart (SlotBitType) o GetItemInfo NÃO preenche:
	// sem eles o tooltip trata tudo como arma ("Bow /", Atk.Spd em armadura).
	private static String buildRowData(AuctionHolder a, Player viewer)
	{
		ItemTemplate t = ItemHolder.getInstance().getTemplate(a.getItemId());
		long price     = a.getCurrentBid() > 0 ? a.getCurrentBid() : a.getStartingPrice();
		int  bodyPart  = (t != null) ? t.getBodyPart() : 0;
		int  itemType  = AuctionHouseService.itemType(t);

		// média por unidade (o cliente mostra "cada" em stack)
		long[] avg     = AuctionPriceStats.average(a.getItemId(), a.getEnchant(), a.getCostId(), 1L);
		long[] last    = AuctionPriceStats.lastSale(a.getItemId(), a.getEnchant(), a.getCostId());
		int    myBid   = AuctionHouseService.bidState(a, viewer != null ? viewer.getObjectId() : 0);

		return a.getItemId()
				+ "|" + a.getEnchant()
				+ "|" + fmtNum(price)
				+ "|" + a.getTimeRemainingFormatted()
				+ "|" + itemType
				+ "|" + bodyPart
				+ "|" + a.getAuctionId()
				+ "|" + (a.getTimeRemaining() / 1000L)    // segundos: cor do tempo na lista
				+ "|" + avg[0]
						+ "|" + avg[1]
								+ "|" + a.getCostId()
								+ "|" + myBid
								+ "|" + last[0]
										+ "|" + last[1]
												+ "|" + last[2]
														+ "|" + a.getCount()           // quantidade: " (1,110)" no tooltip
														+ "|" + (a.isFixedPrice() ? 1 : 0)
														+ "|" + (a.isPartial() ? 1 : 0)
														+ "|" + a.getUnitPrice()
														+ "|" + a.getOriginalCount()
														+ "|" + AuctionTableCommunity.getInstance().getBids(a.getAuctionId()).size();
	}

	// ── auctionPriceInfo ────────────────────────────────────────────
	// bypass: scripts_services.Interface:auctionPriceInfo ITEMID ENCHANT MOEDA QTD
	// Aba de criação: média das últimas vendas do item escolhido, na moeda
	// escolhida, para a quantidade escolhida (o cliente sugere o preço inicial).
	// AUC_PRICE:itemId|enchant|moeda|qtd|média|vendas|últimaVenda|qtdÚltima|segundosDesdeÚltima
	//   (vendas 0 = sem histórico; últimaVenda 0 = nunca vendido)
	public void auctionPriceInfo(String[] params)
	{
		Player player = this.getSelf();
		if (player == null || params == null || params.length < 4) return;

		int  itemId  = aucInt(params[0], 0);
		int  enchant = aucInt(params[1], 0);
		int  costId  = aucInt(params[2], 0);
		long count   = aucLong(params[3], 1L);
		if (itemId <= 0 || costId <= 0) return;
		if (count < 1) count = 1;

		long[] avg  = AuctionPriceStats.average(itemId, enchant, costId, count);
		long[] last = AuctionPriceStats.lastSale(itemId, enchant, costId);
		player.sendMessage("AUC_PRICE:" + itemId + "|" + enchant + "|" + costId + "|" + count
				+ "|" + avg[0] + "|" + avg[1] + "|" + last[0] + "|" + last[1] + "|" + last[2]);
	}

	// ================================================================
	// ABA 0 — DAR LANCE / COMPRAR JÁ
	// ================================================================

	// ── auctionDetail ───────────────────────────────────────────────
	// bypass: scripts_services.Interface:auctionDetail AUCTIONID [1]
	// A janela "Dar lance" e a confirmação do "Comprar já" usam estes dados
	// (sempre frescos do servidor, não os da linha da lista).
	// "1" no fim = a janela "Dar lance" está aberta neste leilão: o jogador
	// passa a receber as mudanças dele em tempo real (até auctionUnwatch).
	public void auctionDetail(String[] params)
	{
		Player player = this.getSelf();
		if (player == null) return;

		int     auctionId = (params != null && params.length > 0) ? aucInt(params[0], -1) : -1;
		boolean watch     = params != null && params.length > 1 && "1".equals(params[1]);

		boolean ok = sendAuctionDetail(player, auctionId);
		if (!ok)
			resendAuctionList(player);   // o leilão acabou: sai da lista

		if (watch)
		{
			// O cliente não abre janela para o próprio anúncio nem para o preço
			// fixo de 1 unidade: esses não ficam observados. Ficam o leilão
			// (janela de lance), a venda parcial (a janela de compra vê o
			// "restam" mudar) e o lote fechado (a janela fica sabendo do fim).
			AuctionHolder a = AuctionTableCommunity.getInstance().getAuction(auctionId);
			if (ok && a != null && a.getSellerId() != player.getObjectId()
					&& (!AuctionHouseService.isBuyNowOnly(a) || a.isPartial() || a.getCount() > 1))
				AuctionHouseService.watch(player, auctionId);
			else
				AuctionHouseService.unwatch(player);
		}
	}

	// ── auctionUnwatch ──────────────────────────────────────────────
	// bypass: scripts_services.Interface:auctionUnwatch
	// A janela "Dar lance" fechou: para de mandar atualizações.
	public void auctionUnwatch()
	{
		Player player = this.getSelf();
		if (player == null) return;

		AuctionHouseService.unwatch(player);
	}

	// AUC_DETAIL:... (formato no AuctionHouseService.detailMessage)
	// Retorna false se o leilão não está mais aberto (mandou AUC_DETAIL_FAIL).
	private static boolean sendAuctionDetail(Player player, int auctionId)
	{
		AuctionHolder a = AuctionTableCommunity.getInstance().getAuction(auctionId);

		if (a == null || !a.isActive() || a.getTimeRemaining() <= 0)
		{
			player.sendMessage("AUC_DETAIL_FAIL:" + auctionId + "|Este leilão já foi encerrado.");
			return false;
		}

		player.sendMessage(AuctionHouseService.detailMessage(player, a));
		return true;
	}

	// ── auctionBid ──────────────────────────────────────────────────
	// bypass: scripts_services.Interface:auctionBid AUCTIONID VALOR
	public void auctionBid(String[] params)
	{
		Player player = this.getSelf();
		if (player == null) return;

		if (params == null || params.length < 2)
		{
			player.sendMessage("AUC_BID:FAIL|0|Dados incompletos.");
			return;
		}

		int  auctionId = aucInt(params[0], -1);
		long amount    = aucLong(params[1], -1L);

		AuctionHouseService.Result r;
		try
		{
			r = AuctionHouseService.placeBid(player, auctionId, amount);
		}
		catch (Exception e)
		{
			_log.warn("AuctionHouse: erro no lance de " + player.getName() + " no leilão " + auctionId + ": " + e);
			player.sendMessage("AUC_BID:FAIL|" + auctionId + "|Erro ao registrar o lance. Tente de novo.");
			return;
		}

		if (r.ok)
		{
			// o cliente fecha a janela de lance depois do OK
			AuctionHouseService.unwatch(player);
			player.sendMessage("AUC_BID:OK|" + auctionId + "|" + amount + "|" + r.extendedMinutes);
			resendAuctionList(player);   // linha verde e "My Bids (N)" já com o lance novo
			return;
		}

		// Valores novos primeiro (o mínimo pode ter subido), depois o motivo
		boolean open = sendAuctionDetail(player, auctionId);
		player.sendMessage("AUC_BID:FAIL|" + auctionId + "|" + r.message);
		if (!open)
			resendAuctionList(player);   // o leilão acabou: sai da lista
	}

	// ── auctionBuyNow ───────────────────────────────────────────────
	// bypass: scripts_services.Interface:auctionBuyNow AUCTIONID PREÇO
	// PREÇO = o que o jogador viu na confirmação; se mudou, não compra.
	public void auctionBuyNow(String[] params)
	{
		Player player = this.getSelf();
		if (player == null) return;

		if (params == null || params.length < 1)
		{
			player.sendMessage("AUC_BUY:FAIL|0|Dados incompletos.");
			return;
		}

		int  auctionId = aucInt(params[0], -1);
		long expected  = params.length > 1 ? aucLong(params[1], 0L) : 0L;

		AuctionHouseService.Result r;
		try
		{
			r = AuctionHouseService.buyNow(player, auctionId, expected);
		}
		catch (Exception e)
		{
			_log.warn("AuctionHouse: erro na compra de " + player.getName() + " no leilão " + auctionId + ": " + e);
			player.sendMessage("AUC_BUY:FAIL|" + auctionId + "|Erro ao concluir a compra. Avise um GM.");
			return;
		}

		if (r.ok)
			player.sendMessage("AUC_BUY:OK|" + auctionId);
		else
			player.sendMessage("AUC_BUY:FAIL|" + auctionId + "|" + r.message);
		resendAuctionList(player);   // o item comprado (ou que acabou) sai da lista
	}

	// ── auctionBuyQty ───────────────────────────────────────────────
	// bypass: scripts_services.Interface:auctionBuyQty AUCTIONID QTD PREÇO_UNIDADE
	// Compra parcial: só num anúncio de preço fixo que aceita compra parcial.
	// PREÇO_UNIDADE = o que o jogador viu; se mudou, não compra (0 = não confere).
	// Resposta: AUC_BUY:OK|id|qtd   ou   AUC_DETAIL atualizado + AUC_BUY:FAIL|id|motivo
	public void auctionBuyQty(String[] params)
	{
		Player player = this.getSelf();
		if (player == null) return;

		if (params == null || params.length < 2)
		{
			player.sendMessage("AUC_BUY:FAIL|0|Dados incompletos.");
			return;
		}

		int  auctionId = aucInt(params[0], -1);
		long qty       = aucLong(params[1], 0L);
		long unit      = params.length > 2 ? aucLong(params[2], 0L) : 0L;

		AuctionHouseService.Result r;
		try
		{
			r = AuctionHouseService.buyPartial(player, auctionId, qty, unit);
		}
		catch (Exception e)
		{
			_log.warn("AuctionHouse: erro na compra parcial de " + player.getName() + " no anúncio " + auctionId + ": " + e);
			player.sendMessage("AUC_BUY:FAIL|" + auctionId + "|Erro ao concluir a compra. Avise um GM.");
			return;
		}

		if (r.ok)
		{
			// o cliente fecha a janela de quantidade depois do OK
			AuctionHouseService.unwatch(player);
			player.sendMessage("AUC_BUY:OK|" + auctionId + "|" + qty);
		}
		else
		{
			// valores novos primeiro (pode ter sobrado menos), depois o motivo
			sendAuctionDetail(player, auctionId);
			player.sendMessage("AUC_BUY:FAIL|" + auctionId + "|" + r.message);
		}
		resendAuctionList(player);   // "restam" novo na linha (ou o anúncio esgotado sai)
	}

	// ── filterAndSort ───────────────────────────────────────────────
	private static List<AuctionHolder> filterAndSort(Player player,
			String cat, String search, String grade, String order)
	{
		String sl = search.toLowerCase();
		AuctionTableCommunity table = AuctionTableCommunity.getInstance();
		List<AuctionHolder> r = new ArrayList<>();

		for (AuctionHolder a : table.getAuctions().values())
		{
			if (!a.isActive()) continue;

			if ("MY_BIDS".equalsIgnoreCase(cat))
			{
				boolean hasBid = false;
				// cópia: um lance novo pode entrar na lista enquanto lemos
				for (AuctionBid bid : table.getBids(a.getAuctionId()).toArray(new AuctionBid[0]))
					if (bid != null && bid.getBidderId() == player.getObjectId()) { hasBid = true; break; }
				if (!hasBid) continue;
			}
			else if ("MY_SALES".equalsIgnoreCase(cat))
			{
				if (a.getSellerId() != player.getObjectId()) continue;
			}
			else
			{
				if (a.getSellerId() == player.getObjectId()) continue;

				if (!"ALL".equalsIgnoreCase(cat))
				{
					ItemTemplate t = ItemHolder.getInstance().getTemplate(a.getItemId());
					if (t == null || !matchCategory(t, cat)) continue;
				}
			}

			if (!sl.isEmpty())
			{
				ItemTemplate t = ItemHolder.getInstance().getTemplate(a.getItemId());
				if (t == null) continue;
				String fullName = t.getName()
						+ (t.getAdditionalName() != null ? " " + t.getAdditionalName() : "");
				if (!fullName.toLowerCase().contains(sl)) continue;
			}

			if (!"ALL".equalsIgnoreCase(grade))
			{
				ItemTemplate t = ItemHolder.getInstance().getTemplate(a.getItemId());
				if (t == null) continue;
				ItemTemplate.Grade g = t.getCrystalType();
				boolean isNone = (g == null || g == ItemTemplate.Grade.NONE);
				if (grade.equalsIgnoreCase("NG"))
				{
					if (!isNone) continue;
				}
				else
				{
					try
					{
						if (g != ItemTemplate.Grade.valueOf(grade.toUpperCase())) continue;
					}
					catch (IllegalArgumentException e) { continue; }
				}
			}

			r.add(a);
		}

		// Preço: pelo preço de 1 unidade, para stacks e itens únicos ficarem
		// na ordem certa juntos (para item único é o mesmo valor). As chaves
		// são lidas antes de ordenar: um lance ou compra no meio da ordenação
		// não pode mudar o valor (o sort do Java reclama se a ordem mudar).
		Map<AuctionHolder, Long> key = new java.util.IdentityHashMap<>();
		switch (order == null ? "TIME" : order.toUpperCase())
		{
		case "PRICE_ASC":
			for (AuctionHolder a : r) key.put(a, a.getUnitPrice());
			r.sort(Comparator.comparingLong(key::get));
			break;
		case "PRICE_DESC":
			for (AuctionHolder a : r) key.put(a, a.getUnitPrice());
			r.sort((a, b) -> Long.compare(key.get(b), key.get(a)));   // maior para menor
			break;
		case "ENCHANT":
			r.sort((a, b) -> Integer.compare(b.getEnchant(), a.getEnchant()));
			break;
		case "TIME":
		default:
			for (AuctionHolder a : r) key.put(a, a.getTimeRemaining());
			r.sort(Comparator.comparingLong(key::get));
			break;
		}

		return r;
	}

	// ── matchCategory ───────────────────────────────────────────────
	private static boolean matchCategory(ItemTemplate t, String cat)
	{
		if (t == null || cat == null) return false;
		return t.searchItemByType(cat);
	}

	// 1234567 → "1,234,567" (vírgula, igual aos campos do cliente)
	private static String fmtNum(long v)
	{
		return String.format(java.util.Locale.US, "%,d", v);
	}

	// ── Helpers de parse seguro ─────────────────────────────────────
	private static int safeInt(String s, int def)
	{
		try { return Integer.parseInt(s.trim()); }
		catch (Exception e) { return def; }
	}

	// Durações oferecidas no combo (em horas). O antigo aceitava até 72h
	// e oferecia também 12h; para voltar com ela use { 12, 24, 48, 72 }
	// (o cliente mostra "12h" sozinho). O cliente monta o combo a partir
	// desta lista e o servidor só aceita valores dela.
	private static final int[] AUC_DURATIONS_HOURS = { 24, 48, 72 };

	// Cadeado da criação (id do leilão + gravação)
	private static final Object AUC_CREATE_LOCK = new Object();

	// ── auctionCreateOpen ───────────────────────────────────────────
	// bypass: scripts_services.Interface:auctionCreateOpen
	// Chamado quando o jogador abre a aba de criação.
	public void auctionCreateOpen()
	{
		Player player = this.getSelf();
		if (player == null) return;

		// Indo para a aba 1 a janela de lance fecha: para de mandar
		// atualizações aqui mesmo (o cliente não manda um auctionUnwatch
		// separado, que chegaria colado neste pedido)
		AuctionHouseService.unwatch(player);

		sendAuctionCreateData(player, 1);
	}

	// Envia configuração + inventário elegível + lista "meus itens" na
	// página pedida (1 ao abrir a aba e depois de criar um leilão)
	private static void sendAuctionCreateData(Player player, int myPage)
	{
		// AUC_CFG:durações|moedas|taxaItem,taxaQtd|taxaVenda|incrementoMínimo%|compraParcial
		StringBuilder cfg = new StringBuilder("AUC_CFG:");

		for (int i = 0; i < AUC_DURATIONS_HOURS.length; i++)
		{
			if (i > 0) cfg.append(',');
			cfg.append(AUC_DURATIONS_HOURS[i]);
		}

		cfg.append('|');

		for (int i = 0; i < Config.MARKETPLACE_CURRENCIES.length; i++)
		{
			if (i > 0) cfg.append(',');
			cfg.append(Config.MARKETPLACE_CURRENCIES[i]);
		}

		// ...|incrementoMínimo%|compraParcial (1 = o banco tem as colunas)
		cfg.append('|')
		.append(Config.MARKETPLACE_FEE[0])
		.append(',')
		.append(Config.MARKETPLACE_FEE[1])
		.append('|')
		.append(AuctionHouseService.taxBasisPoints())    // taxa sobre a venda: 500 = 5%
		.append('|')
		.append(Math.max(0, Config.AUCTION_MIN_BID_PERCENT))   // compra imediata >= inicial + isto
		.append('|')
		.append(AuctionTableCommunity.getInstance().supportsPartial() ? 1 : 0);

		player.sendMessage(cfg.toString());

		// Inventário elegível
		List<ItemInstance> eligible = getAuctionableItems(player);

		player.sendMessage("AUC_INV_START:");

		// O objectId: o cliente pega o item completo (ícone, enchant,
		// quantidade, ItemType/SlotBitType, augment) do próprio inventário,
		// igual ao ItemEnchantWnd faz. Nada de reconstruir pelo itemId.
		// Depois do "|": histórico de preço do item em cada moeda que tem
		// vendas (tooltip do inventário na aba de criação).
		// AUC_INV_ITEM:objectId[|moeda,médiaUnidade,vendas,últimaVenda,qtdÚltima,segundos;...]
		for (ItemInstance item : eligible)
			player.sendMessage("AUC_INV_ITEM:" + item.getObjectId() + invPriceStats(item));

		player.sendMessage("AUC_INV_END:" + eligible.size());

		// Itens que o personagem já tem à venda
		sendMyAuctions(player, myPage);
	}

	// "|moeda,médiaUnidade,vendas,últimaVenda,qtdÚltima,segundos;..." só das
	// moedas em que o item (mesmo enchant) já foi vendido; "" = nunca vendido
	private static String invPriceStats(ItemInstance item)
	{
		StringBuilder sb = new StringBuilder();
		for (int costId : Config.MARKETPLACE_CURRENCIES)
		{
			long[] avg = AuctionPriceStats.average(item.getItemId(), item.getEnchantLevel(), costId, 1L);
			if (avg[1] <= 0)
				continue;
			long[] last = AuctionPriceStats.lastSale(item.getItemId(), item.getEnchantLevel(), costId);
			sb.append(sb.length() == 0 ? "|" : ";")
			.append(costId).append(',').append(avg[0]).append(',').append(avg[1])
			.append(',').append(last[0]).append(',').append(last[1]).append(',').append(last[2]);
		}
		return sb.toString();
	}

	// Lista da aba 1: leilões ATIVOS do próprio personagem, os que acabam
	// primeiro no topo, paginada (MyAuctionsPerPage). Mesmo filtro da
	// categoria "My Sales" da aba 0.
	// AUC_MY_START:total|máximo|página|páginas  (máximo 0 = sem limite)
	private static void sendMyAuctions(Player player, int page)
	{
		List<AuctionHolder> mine = filterAndSort(player, "MY_SALES", "", "ALL", "TIME");

		int per        = Math.max(1, Config.AUCTION_MY_PER_PAGE);
		int maxItems   = Math.max(0, Config.AUCTION_MAX_PER_PLAYER);
		int totalPages = Math.max(1, (mine.size() + per - 1) / per);
		page = Math.max(1, Math.min(page, totalPages));

		int from = (page - 1) * per;
		int to   = Math.min(from + per, mine.size());

		player.sendMessage("AUC_MY_START:" + mine.size() + "|" + maxItems
				+ "|" + page + "|" + totalPages);

		for (int i = from; i < to; i++)
			player.sendMessage("AUC_MY_ROW_" + (i - from) + ":" + buildRowData(mine.get(i), player));

		player.sendMessage("AUC_MY_END:" + (to - from));
	}

	// Leilões ATIVOS do personagem (é o que conta para o limite)
	private static int aucCountActive(Player player)
	{
		int n = 0;
		for (AuctionHolder a : AuctionTableCommunity.getInstance().getAuctions().values())
		{
			if (a != null && a.isActive() && a.getSellerId() == player.getObjectId())
				n++;
		}
		return n;
	}

	// ── auctionMyList ───────────────────────────────────────────────
	// bypass: scripts_services.Interface:auctionMyList PAGE
	// Botões < > da lista "meus itens à venda".
	public void auctionMyList(String[] params)
	{
		Player player = this.getSelf();
		if (player == null) return;

		int page = (params != null && params.length > 0) ? aucInt(params[0], 1) : 1;
		sendMyAuctions(player, page);
	}

	// ── auctionCancel ───────────────────────────────────────────────
	// bypass: scripts_services.Interface:auctionCancel AUCTIONID PAGE
	// Mesmas regras do bp_cancelAuction antigo: só o vendedor, só leilão
	// ativo. AuctionTableCommunity.cancelAuction devolve o item ao vendedor
	// e o lance atual ao licitante, marca CANCELLED e tira do mapa.
	public void auctionCancel(String[] params)
	{
		Player player = this.getSelf();
		if (player == null) return;

		int auctionId = (params != null && params.length > 0) ? aucInt(params[0], -1) : -1;
		int page      = (params != null && params.length > 1) ? aucInt(params[1], 1) : 1;

		AuctionTableCommunity table = AuctionTableCommunity.getInstance();
		AuctionHolder auction = table.getAuction(auctionId);

		if (auction == null)
		{
			failCancel(player, "Leilão não encontrado (já pode ter terminado).");
			sendMyAuctions(player, page);
			return;
		}

		if (auction.getSellerId() != player.getObjectId())
		{
			_log.warn("AuctionHouse: " + player.getName() + " tentou cancelar o leilão "
					+ auctionId + " de outro jogador.");
			failCancel(player, "Este leilão não é seu.");
			return;
		}

		// Dois cliques seguidos não podem cancelar (e devolver) duas vezes.
		// A tarefa de expiração do AuctionTableCommunity deve usar o mesmo
		// cadeado (veja o trecho do AuctionTableCommunity.java).
		synchronized (auction)
		{
			// getTimeRemaining() <= 0: o tempo acabou e a tarefa que encerra
			// os leilões ainda não passou — cancelar agora desfaria a venda.
			if (!auction.isActive() || table.getAuction(auctionId) == null || auction.getTimeRemaining() <= 0)
			{
				failCancel(player, "Este leilão já foi encerrado.");
				sendMyAuctions(player, page);
				return;
			}

			table.cancelAuction(auction);
		}

		_log.info("AuctionHouse: " + player.getName() + " cancelou o leilão id=" + auctionId
				+ " itemId=" + auction.getItemId() + " +" + auction.getEnchant() + " x" + auction.getCount()
				+ (auction.getSoldCount() > 0 ? " (vendidas " + auction.getSoldCount() + " de " + auction.getOriginalCount() + ")" : ""));

		// venda parcial: as vendidas continuam vendidas, só o resto volta
		if (auction.getSoldCount() > 0)
			player.sendMessage("Anúncio cancelado: vendidas " + aucFmt(auction.getSoldCount()) + " de "
					+ aucFmt(auction.getOriginalCount()) + ". As " + aucFmt(auction.getCount())
					+ " restantes voltaram para você.");

		player.sendMessage("AUC_CANCEL:OK");

		// quem estava com este leilão aberto na janela "Dar lance"
		AuctionHouseService.notifyEnded(auction, "O vendedor cancelou este leilão.", player.getObjectId());

		// O item voltou para o inventário: atualiza a grade e a lista
		sendAuctionCreateData(player, page);
	}

	private static void failCancel(Player player, String reason)
	{
		player.sendMessage("AUC_CANCEL:FAIL|" + reason);
	}

	// Mesmo filtro do showCreateAuctionUnified antigo (+ não equipado)
	private static List<ItemInstance> getAuctionableItems(Player player)
	{
		List<ItemInstance> eligible = new ArrayList<>();

		for (ItemInstance item : player.getInventory().getItems())
		{
			if (item == null) continue;
			if (item.isEquipped()) continue;
			if (!isAuctionable(player, item)) continue;
			eligible.add(item);
		}

		eligible.sort((a, b) ->
		{
			boolean ae = a.isEquipable();
			boolean be = b.isEquipable();
			if (ae != be) return ae ? -1 : 1;
			return a.getName().compareToIgnoreCase(b.getName());
		});

		return eligible;
	}

	private static boolean isAuctionable(Player player, ItemInstance item)
	{
		if (item.getItemId() == Config.TICKET_MARKE_PLACE) return false;
		if (!item.canBeTraded(player)) return false;
		return AuctionUtils.checkItem(item);
	}

	private static void failCreate(Player player, String reason)
	{
		player.sendMessage("AUC_CREATE:FAIL|" + reason);
	}

	// ── createAuction ───────────────────────────────────────────────
	// bypass: scripts_services.Interface:createAuction OBJ QTD INICIAL COMPRA HORAS MOEDA [TIPO PARCIAL]
	//
	//   Sem TIPO (cliente antigo): INICIAL e COMPRA são o total do lote, como
	//   sempre foi (preço fixo = COMPRA igual ao INICIAL).
	//
	//   Com TIPO: INICIAL e COMPRA são POR UNIDADE; o servidor multiplica pela
	//   quantidade (o total precisa caber no teto de sempre).
	//     TIPO 0 = leilão: lances, o lote vai sempre inteiro. COMPRA 0 = sem
	//              compra imediata (o maior lance no fim leva); senão a compra
	//              imediata precisa ser pelo menos o inicial + o incremento
	//              mínimo de lance (MinBidIncreasePercent).
	//     TIPO 1 = preço fixo: sem lances, COMPRA é ignorada.
	//              PARCIAL 1 = aceita compra parcial (só empilhável, QTD > 1).
	//     Compra parcial nunca vale em leilão.
	public void createAuction(String[] params)
	{
		Player player = this.getSelf();
		if (player == null) return;

		if (params == null || params.length < 6)
		{
			failCreate(player, "Dados incompletos.");
			return;
		}

		int  objectId      = aucInt(params[0], 0);
		long amount        = aucLong(params[1], 0L);
		long startingPrice = aucLong(params[2], -1L);
		long buyNowPrice   = aucLong(params[3], -1L);
		int  durationHours = aucInt(params[4], 0);
		int  costId        = aucInt(params[5], 0);

		boolean unitPrices = params.length >= 8;                       // cliente novo: preço por unidade
		int     saleMode   = unitPrices ? aucInt(params[6], -1) : -1;  // 0 leilão, 1 preço fixo
		boolean partial    = unitPrices && "1".equals(params[7].trim());

		// ── 0. Limite de leilões ativos por personagem ───────────────
		int maxItems = Config.AUCTION_MAX_PER_PLAYER;
		if (maxItems > 0 && aucCountActive(player) >= maxItems)
		{
			failCreate(player, "Você já tem " + maxItems + " itens à venda, o máximo permitido.");
			return;
		}

		// ── 1. Item ──────────────────────────────────────────────────
		ItemInstance item = player.getInventory().getItemByObjectId(objectId);

		if (item == null)
		{
			failCreate(player, "Item não encontrado no inventário.");
			return;
		}

		if (item.isEquipped())
		{
			failCreate(player, "O item não pode estar equipado.");
			return;
		}

		if (!isAuctionable(player, item))
		{
			failCreate(player, "Este item não pode ser leiloado.");
			return;
		}

		// ── 2. Quantidade ────────────────────────────────────────────
		if (amount <= 0 || amount > Integer.MAX_VALUE)
		{
			failCreate(player, "Quantidade inválida.");
			return;
		}

		if (!item.isStackable() && amount != 1)
		{
			failCreate(player, "Quantidade inválida para este item.");
			return;
		}

		if (item.getCount() < amount)
		{
			failCreate(player, "Você não tem quantidade suficiente deste item.");
			return;
		}

		// ── 3. Preços ────────────────────────────────────────────────
		// Preço inicial 0 + compra imediata 0 seria um leilão "de graça":
		// o mínimo é 1 (compra imediata 0 continua = desativada)
		if (startingPrice < 1)
		{
			failCreate(player, saleMode == 1 ? "O preço deve ser pelo menos 1."
					: "O preço inicial deve ser pelo menos 1.");
			return;
		}

		// (preço fixo do cliente novo ignora COMPRA: não precisa conferir)
		if (buyNowPrice < 0 && !(unitPrices && saleMode == 1))
		{
			failCreate(player, "O preço de compra imediata deve ser maior ou igual a 0.");
			return;
		}

		if (unitPrices)
		{
			// ── 3b. Tipo de venda e preço por unidade (cliente novo) ──
			if (saleMode != 0 && saleMode != 1)
			{
				failCreate(player, "Tipo de venda inválido.");
				return;
			}

			long unitStart = startingPrice;
			long unitBuy   = (saleMode == 1) ? unitStart : buyNowPrice;   // preço fixo: compra = preço

			// Teto: o total (unidade x quantidade) segue o limite de sempre
			if (unitStart > Integer.MAX_VALUE / amount || unitBuy > Integer.MAX_VALUE / amount)
			{
				failCreate(player, "O total passa do valor máximo (" + aucFmt(Integer.MAX_VALUE) + ").");
				return;
			}
			startingPrice = unitStart * amount;
			buyNowPrice   = unitBuy * amount;

			if (saleMode == 0)
			{
				if (partial)
				{
					failCreate(player, "Compra parcial só existe no preço fixo. No leilão o lote vai inteiro.");
					return;
				}

				// Compra imediata: 0 (sem limite) ou pelo menos o inicial + o
				// incremento mínimo (senão o primeiro lance já a alcançaria)
				long minBuy = AuctionHouseService.minBuyNow(startingPrice);
				if (buyNowPrice > 0 && buyNowPrice < minBuy)
				{
					long minUnit = (minBuy + amount - 1) / amount;
					failCreate(player, "A compra imediata deve ser pelo menos "
							+ (amount > 1 ? aucFmt(minUnit) + " cada." : aucFmt(minBuy) + "."));
					return;
				}
			}
			else if (partial)
			{
				if (!item.isStackable() || amount < 2)
				{
					failCreate(player, "Compra parcial só para itens empilháveis com mais de 1 unidade.");
					return;
				}
				if (!AuctionTableCommunity.getInstance().supportsPartial())
				{
					failCreate(player, "Compra parcial indisponível: o banco do leilão precisa ser atualizado.");
					return;
				}
			}
		}
		else
		{
			// ── 3b. Cliente antigo: INICIAL e COMPRA já são o total ──
			// Teto: o código de lance/compra/devolução faz (int) nos preços
			if (startingPrice > Integer.MAX_VALUE || buyNowPrice > Integer.MAX_VALUE)
			{
				failCreate(player, "Valor máximo: " + aucFmt(Integer.MAX_VALUE) + ".");
				return;
			}

			if (buyNowPrice > 0 && buyNowPrice < startingPrice)
			{
				failCreate(player, "A compra imediata deve ser maior ou igual ao preço inicial.");
				return;
			}
		}

		// ── 4. Duração ───────────────────────────────────────────────
		if (!ArrayUtils.contains(AUC_DURATIONS_HOURS, durationHours))
		{
			failCreate(player, "Duração inválida.");
			return;
		}

		// ── 5. Moeda ─────────────────────────────────────────────────
		if (!ArrayUtils.contains(Config.MARKETPLACE_CURRENCIES, costId))
		{
			failCreate(player, "Moeda inválida.");
			return;
		}

		// ── 6. Taxa ──────────────────────────────────────────────────
		int feeId     = Config.MARKETPLACE_FEE[0];
		int feeAmount = Config.MARKETPLACE_FEE[1];

		if (feeAmount > 0 && ItemFunctions.getItemCount(player, feeId) < feeAmount)
		{
			ItemTemplate feeTpl = ItemHolder.getInstance().getTemplate(feeId);
			String feeName = feeTpl != null ? feeTpl.getName() : ("item " + feeId);
			failCreate(player, "Você não tem " + aucFmt(feeAmount) + " " + feeName + " para pagar a taxa.");
			return;
		}

		// Guarda os dados do item ANTES de remover do inventário
		int itemId  = item.getItemId();
		int enchant = item.getEnchantLevel();

		// Item leiloado = item da taxa (ex.: vender Adena com taxa em Adena):
		// precisa ter a quantidade do leilão + a taxa
		if (feeAmount > 0 && itemId == feeId && item.getCount() < amount + feeAmount)
		{
			failCreate(player, "Quantidade insuficiente para o leilão mais a taxa.");
			return;
		}

		// ── 7. Cobra a taxa primeiro ─────────────────────────────────
		if (feeAmount > 0)
		{
			long removedFee = ItemFunctions.removeItem(player, feeId, feeAmount, true);

			if (removedFee < feeAmount)
			{
				if (removedFee > 0)
					ItemFunctions.addItem(player, feeId, removedFee, true);

				failCreate(player, "Não foi possível cobrar a taxa.");
				return;
			}
		}

		// ── 8. Remove o item (a mesma instância que o jogador escolheu)
		ItemInstance inventoryItem = player.getInventory().getItemByObjectId(objectId);
		boolean removed = false;

		if (inventoryItem != null && inventoryItem.getCount() >= amount)
		{
			if (inventoryItem.isStackable())
				removed = player.getInventory().destroyItem(inventoryItem, amount);
			else
				removed = player.getInventory().destroyItem(inventoryItem, 1L);
		}

		if (!removed)
		{
			// devolve a taxa (moeda não tem enchant, devolução é exata)
			if (feeAmount > 0)
				ItemFunctions.addItem(player, feeId, feeAmount, true);

			_log.warn("AuctionHouse: falha ao remover item. player=" + player.getName()
			+ " objectId=" + objectId + " itemId=" + itemId + " qtd=" + amount);

			failCreate(player, "Não foi possível remover o item do inventário. A taxa foi devolvida.");
			return;
		}

		// ── 9. Cria e salva o leilão ─────────────────────────────────
		Date startTime = new Date();
		Date endTime   = new Date(System.currentTimeMillis() + durationHours * 60L * 60L * 1000L);

		// getNextAuctionId é "MAX(auctionid) + 1" no banco: dois leilões criados
		// no mesmo instante pegariam o mesmo id. Com o cadeado, o segundo só
		// pega o id depois que o primeiro já foi gravado.
		AuctionTableCommunity table = AuctionTableCommunity.getInstance();
		AuctionHolder auction;
		synchronized (AUC_CREATE_LOCK)
		{
			int newId = table.getNextAuctionId();
			while (table.getAuction(newId) != null)   // gravação anterior falhou no banco
				newId++;

			auction = new AuctionHolder(
					newId,
					player.getObjectId(),
					itemId,
					(int) amount,
					enchant,
					costId,
					feeAmount,
					startingPrice,
					buyNowPrice,
					startTime,
					endTime
					);
			auction.setAllowPartial(partial);   // só chega aqui em preço fixo empilhável

			table.addAuction(auction);
		}

		_log.info("AuctionHouse: " + player.getName()
		+ " criou " + (auction.isFixedPrice() ? "venda a preço fixo" : "leilão")
		+ (partial ? " (compra parcial)" : "")
		+ " id=" + auction.getAuctionId()
		+ " itemId=" + itemId + " +" + enchant + " x" + amount
		+ " inicial=" + startingPrice + " compra=" + buyNowPrice
		+ " moeda=" + costId + " dur=" + durationHours + "h");

		// id do leilão novo: o cliente acha a linha dele na lista e dá o destaque
		player.sendMessage("AUC_CREATE:OK|" + auction.getAuctionId());

		// ── 10. Anúncio (igual ao antigo) ────────────────────────────
		try
		{
			String currencyName = AuctionUtils.getCurrencyNameById(costId);
			String itemName     = AuctionUtils.formatItemName(itemId, enchant);

			String qtyText = amount > 1 ? aucFmt(amount) + " " : "";
			String announcement;
			if (auction.isFixedPrice() && partial)
				announcement = player.getName() + " colocou " + qtyText + itemName + " à venda por "
						+ aucFmt(auction.getUnitPrice()) + " " + currencyName + " cada (compre a quantidade que quiser).";
			else if (auction.isFixedPrice())
				announcement = player.getName() + " colocou " + qtyText + itemName + " à venda por "
						+ aucFmt(startingPrice) + " " + currencyName + (amount > 1 ? " (o lote inteiro)" : "") + ".";
			else
				announcement = player.getName() + " adicionou " + qtyText + itemName + " no leilão. "
						+ "Lance inicial: " + aucFmt(startingPrice) + " " + currencyName
						+ (buyNowPrice > 0 ? " | Compra imediata: " + aucFmt(buyNowPrice) + " " + currencyName : "");

			Announcements.getInstance().announceToAll(announcement);
		}
		catch (Exception e)
		{
			_log.warn("AuctionHouse: erro ao anunciar leilão: " + e.getMessage());
		}

		// ── 11. Atualiza a grade do cliente (o item saiu do inventário)
		// e abre a lista "meus itens" na página onde o leilão novo ficou
		sendAuctionCreateData(player, aucMyPageOf(player, auction.getAuctionId()));
	}

	// Página da lista "meus itens à venda" (mesma ordem do sendMyAuctions)
	// em que está o leilão; 1 se não achar
	private static int aucMyPageOf(Player player, int auctionId)
	{
		List<AuctionHolder> mine = filterAndSort(player, "MY_SALES", "", "ALL", "TIME");
		int per = Math.max(1, Config.AUCTION_MY_PER_PAGE);

		for (int i = 0; i < mine.size(); i++)
		{
			if (mine.get(i).getAuctionId() == auctionId)
				return i / per + 1;
		}
		return 1;
	}

	// Helpers próprios deste bloco (nomes únicos para não colidir com
	// safeInt/safeLong/fmtNum que já existam no Interface.java)
	private static int aucInt(String s, int def)
	{
		try { return Integer.parseInt(s.trim()); }
		catch (Exception e) { return def; }
	}

	private static long aucLong(String s, long def)
	{
		try { return Long.parseLong(s.trim()); }
		catch (Exception e) { return def; }
	}

	private static String aucFmt(long v)
	{
		return String.format(java.util.Locale.US, "%,d", v);
	}

	public void onStart() 
	{
		ThreadPoolManager.getInstance()
		.scheduleAtFixedRate(
				() -> {
					for (Player player : GameObjectsStorage.getAllPlayers()) {
						try {
							if (player != null
									&& !player.isInOfflineMode()
									&& player.isOnline()
									&& player.getTarget() != null
									&& player.getTarget() instanceof Player) {
								Player target = (Player)player.getTarget();
								updateStatus(target, player);
							}
						} catch (Exception e) {
							_log.warn(e.getMessage());
						}
					}
				},
				500L,
				500L
				);
	}

	private static void updateStatus(Player _target, Player player) {
		StatusUpdate su = new StatusUpdate(_target);
		su.addAttribute(9, (int)_target.getCurrentHp());
		su.addAttribute(33, (int)_target.getCurrentCp());
		su.addAttribute(10, _target.getMaxHp());
		su.addAttribute(34, _target.getMaxCp());
		player.sendPacket(su);
	}
}
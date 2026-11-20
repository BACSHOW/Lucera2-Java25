package services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestEventType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;
import java.text.Normalizer;
import java.util.Properties;
import java.util.Collections;
import l2.gameserver.handler.ShiftClickHook;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import java.util.concurrent.ConcurrentHashMap;

import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.data.xml.holder.NpcHolder;
import l2.gameserver.data.xml.holder.SpawnHolder;
import l2.gameserver.templates.spawn.SpawnTemplate;
import l2.gameserver.templates.spawn.SpawnNpcInfo;
import l2.gameserver.utils.Location;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.npc.NpcTemplate;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.actor.listener.CharListenerList;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.reward.RewardData;
import l2.gameserver.model.reward.RewardGroup;
import l2.gameserver.model.reward.RewardList;
import l2.gameserver.model.reward.RewardType;
import l2.gameserver.network.l2.s2c.TutorialShowHtml;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Read-only monster information, transported by the Interlude TutorialShowHtml transport.
 * Chances and amounts are explicitly BASE values: no rates, premium, level penalty,
 * event rewards or conditions for harvesting/manor. No entities are spawned here.
 */
public class MobInformer extends Functions implements ScriptFile, IVoicedCommandHandler {
	private static final ShiftClickHook.Handler SHIFT_HANDLER = MobInformer::handleShiftClick;
	private static final Logger LOG = LoggerFactory.getLogger(MobInformer.class);
	public static final String BYPASS = "scripts_services.MobInformer:show";
	public static final String SEARCH_BYPASS = "scripts_services.MobInformer:search";
	public static final String DROPS_BYPASS = "scripts_services.MobInformer:drops";
	public static final String CATALOG_BYPASS = "scripts_services.MobInformer:catalog";
	public static final String DETAIL_BYPASS = "scripts_services.MobInformer:detail";
	private static final Map<Integer, Long> LAST_REQUEST = new ConcurrentHashMap<>();
	private static final int MAX_REWARDS = 2048;
	private static final int CHUNK_ROWS = 12;
	static final int PAGE_SIZE = 32;
	static final int NPC_PAGE_SIZE = 64;
	private static final Map<String, Long> LAST_CATALOG_REQUEST = new ConcurrentHashMap<>();
	private static volatile Catalog catalog;
	private static volatile Properties questTitles;
	static final int MAX_QUESTS = 2048;
	private static volatile Map<Integer, Location> spawnLocations;
	private static final EnterListener ENTER_LISTENER = new EnterListener();
	private static boolean started;
	private static volatile boolean enabled = true;
	private static volatile boolean preview3D = true;
	private static volatile boolean animation = true;
	private static volatile boolean shiftClick = true;
	static final int MAX_CATALOG_NPCS = 65535;
	/** Global cap on simultaneous full-catalog streams; extra requests are retried by the client. */
	static final int MAX_ACTIVE_CATALOG_STREAMS = 64;
	private static final Map<Integer, CatalogStream> ACTIVE_CATALOGS = new ConcurrentHashMap<>();

	@Override public void onLoad() 
	{
		loadSettings();
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		// Exact read operations only; no spawning, target changes or item actions.
		synchronized (Config.class) 
		{
			List<String> allowed = new ArrayList<>();
			for (String bypass : new String[] { BYPASS, SEARCH_BYPASS, DROPS_BYPASS, CATALOG_BYPASS, DETAIL_BYPASS }) if (!allowed.contains(bypass)) allowed.add(bypass);
		}
		if (!started) 
		{
			CharListenerList.addGlobal(ENTER_LISTENER);
			ShiftClickHook.set(SHIFT_HANDLER);
			started = true;
		}
		// Build the shared catalog in the background so the first player does not wait for it.
		ThreadPoolManager.getInstance().schedule(() -> {
			try { if (enabled) getCatalog(); }
			catch (RuntimeException failure) { LOG.warn("MobInformer catalog warm-up failed", failure); }
		}, 5000);
		// Spawn locations and quest titles are also lazy; warm them later, once spawns have been loaded.
		ThreadPoolManager.getInstance().schedule(() -> {
			try { if (enabled) { getSpawnLocations(); questTitle(0, null); } }
			catch (RuntimeException failure) { LOG.warn("MobInformer detail warm-up failed", failure); }
		}, 30000);
		// Keep the installed extension quiet during startup.
	}
	@Override public String[] getVoicedCommandList() { return new String[] { "monsterdata", "mobdata" }; }
	@Override public boolean useVoicedCommand(String command, Player player, String args) {
		if (player == null || !enabled) return false;
		player.sendPacket(new TutorialShowHtml(featurePacket()));
		player.sendPacket(new TutorialShowHtml("MOB_INFO|TYPE=OPEN_BROWSER"));
		return true;
	}

	@Override public void onReload() 
	{ 
		catalog = null; spawnLocations = null; 
		questTitles = null; QUEST_ROWS_CACHE.clear(); 
		onLoad(); 
	}

	@Override public void onShutdown() 
	{
		if (started) { CharListenerList.removeGlobal(ENTER_LISTENER); ShiftClickHook.clear(SHIFT_HANDLER); }
		started = false;
		LAST_REQUEST.clear();
		LAST_CATALOG_REQUEST.clear(); ACTIVE_CATALOGS.clear(); catalog = null; spawnLocations = null; questTitles = null; QUEST_ROWS_CACHE.clear();
	}

	private static void loadSettings() {
		// Configuracao do Monster Data: so config/custom/monsterdata.properties (sem o arquivo, tudo ligado).
		Properties properties = new Properties();
		File localSettings = new File("config/custom/monsterdata.properties");
		if (localSettings.isFile()) try (FileInputStream input = new FileInputStream(localSettings)) { properties.load(input); }
		catch (IOException failure) { LOG.warn("MobInformer: failed to read Monster Data settings", failure); }
		applySettings(properties);
	}
	static void applySettings(Properties properties) {
		enabled = Boolean.parseBoolean(properties.getProperty("InterfaceShowMobInformer", "true"));
		preview3D = Boolean.parseBoolean(properties.getProperty("MobInformer3DPreview", "true"));
		animation = Boolean.parseBoolean(properties.getProperty("MobInformerAnimation", "true"));
		shiftClick = Boolean.parseBoolean(properties.getProperty("MobInformerShiftClick", "true"));
	}
	static String featurePacket() {
		return "MOB_INFO|TYPE=FEATURES|VISIBLE=" + (enabled ? 1 : 0) + "|PREVIEW=" + (preview3D ? 1 : 0)
				+ "|ANIMATION=" + (animation ? 1 : 0) + "|BROWSER=1|QUESTS=1|";
	}
	private static final class EnterListener implements OnPlayerEnterListener {
		@Override public void onPlayerEnter(Player player) {
			ThreadPoolManager.getInstance().schedule(() -> {
				if (player != null && player.isOnline() && !player.isInOfflineMode()) send(player, featurePacket());
			}, 600);
		}
	}

	static String[] openingCatalogArgs(String[] args) {
		if(args==null || (args.length!=2 && args.length!=3 && args.length!=4))throw new IllegalArgumentException("opening arguments");
		int objectID=Integer.parseInt(args[0]),request=Integer.parseInt(args[1]);
		if(objectID<=0 || request<=0 || request>1000000)throw new IllegalArgumentException("opening request");
		if(args.length==2)return null;
		int browserRequest=Integer.parseInt(args[2]);
		if(browserRequest<=0 || browserRequest>1000000)throw new IllegalArgumentException("catalog request");
		if (args.length == 4 && !"-1".equals(args[3])) throw new IllegalArgumentException("catalog mode");
		return new String[]{String.valueOf(browserRequest),args.length==4?"-1":"0","0","-"};
	}
	public void show(String[] args) {
		String[] browserArgs;
		try { browserArgs=openingCatalogArgs(args); } catch(IllegalArgumentException invalid){return;}
		// The initial list and target data share one client bypass; two immediate
		// client requests can lose the second request in the Classic GUI channel.
		showTarget(new String[]{args[0],args[1]});
		if(browserArgs!=null)catalog(browserArgs);
	}
	private void showTarget(String[] args) {
		Player player = self.get();
		if (player == null || args == null || args.length != 2) return;
		int objectID;
		int requestID;
		try {
			objectID = Integer.parseInt(args[0]);
			requestID = Integer.parseInt(args[1]);
		} catch (NumberFormatException invalid) { return; }
		if (objectID <= 0 || requestID <= 0 || requestID > 1000000) return;
		if (!enabled) {
			send(player, featurePacket());
			error(player, requestID, "Mob Informer desativado na config da interface.");
			return;
		}
		long now = System.currentTimeMillis();
		Long previous = LAST_REQUEST.put(player.getObjectId(), now);
		if (LAST_REQUEST.size() > 4096) LAST_REQUEST.entrySet().removeIf(e -> now - e.getValue() > 60000);
		if (previous != null && now - previous < 500) {
			error(player, requestID, "Aguarde um instante e abra novamente.");
			return;
		}
		GameObject target = player.getTarget();
		if (!(target instanceof NpcInstance) || target.getObjectId() != objectID) {
			error(player, requestID, "Selecione um NPC para consultar.");
			return;
		}
		NpcInstance mob = (NpcInstance) target;
		if (player.getVisibleObject(objectID) != mob) {
			error(player, requestID, "O monstro nao esta mais visivel.");
			return;
		}
		try {
			List<Row> rows = rewards(mob);
			if (rows.size() > MAX_REWARDS) {
				error(player, requestID, "Lista de recompensas muito grande.");
				return;
			}
			List<QuestRow> quests = questRows(mob.getTemplate());
			int displayID = mob.getDisplayId() > 0 ? mob.getDisplayId() : mob.getNpcId();
			String kind = mob.isBoss() ? "Epic Boss" : mob.isRaid() ? "Raid Boss" : mob.isMonster() ? "Monster" : "NPC";
			send(player, "MOB_INFO|TYPE=BEGIN|REQ=" + requestID + "|OBJECT=" + objectID
					+ "|NPC=" + mob.getNpcId() + "|DISPLAY=" + displayID + "|NAME=" + safe(mob.getName())
					+ "|KIND=" + kind + "|LEVEL=" + mob.getLevel() + "|HP=" + mob.getMaxHp()
					+ "|MP=" + mob.getMaxMp() + "|PATK=" + mob.getPAtk(null) + "|PDEF=" + mob.getPDef(null)
					+ "|MATK=" + mob.getMAtk(null, null) + "|MDEF=" + mob.getMDef(null, null)
					+ "|EXP=" + mob.getExpReward() + "|SP=" + mob.getSpReward()
					+ "|X=" + mob.getX() + "|Y=" + mob.getY() + "|Z=" + mob.getZ()
					+ "|SOURCE=LIVE|HASSITE=1|HEIGHT=" + Math.max(1.0, mob.getTemplate().collisionHeight) + "|ROWS=" + rows.size()
					+ "|PREVIEW=" + (preview3D ? 1 : 0) + "|ANIMATION=" + (animation ? 1 : 0) + "|QUESTS=" + quests.size() + "|");
			for (int offset = 0; offset < rows.size(); offset += CHUNK_ROWS) {
				StringBuilder chunk = new StringBuilder("MOB_INFO|TYPE=ITEMS|REQ=").append(requestID)
						.append("|OFFSET=").append(offset).append("|DATA=");
				for (int i = offset; i < Math.min(rows.size(), offset + CHUNK_ROWS); i++) chunk.append(rows.get(i).uiWire());
				send(player, chunk.append('|').toString());
			}
			sendQuestRows(player, requestID, quests);
			send(player, "MOB_INFO|TYPE=END|REQ=" + requestID + "|COUNT=" + rows.size() + "|QUESTCOUNT=" + quests.size() + "|");
		} catch (RuntimeException failure) {
			LOG.warn("MobInformer failed for NPC " + mob.getNpcId(), failure);
			error(player, requestID, "Nao foi possivel carregar este monstro.");
		}
	}

	private static List<Row> rewards(NpcInstance mob) { return rewards(mob.getTemplate()); }
	static List<Row> rewards(NpcTemplate template) {
		List<Row> result = new ArrayList<>();
		for (RewardType type : RewardType.values()) {
			if (type == RewardType.DISABLED) continue;
			RewardList list = template.getRewardList(type);
			if (list == null) continue;
			for (RewardGroup group : list) {
				for (RewardData item : group.getItems()) {
					if (item.getChance() <= 0 || item.getItemId() <= 0 || isHerbDrop(item.getItemId())) continue;
					boolean grouped = type != RewardType.NOT_RATED_NOT_GROUPED && type != RewardType.NOT_RATED_GROUPED;
					double chance = baseChance(item.getChance(), group.getChance(), grouped);
					if (chance <= 0) continue;
					result.add(new Row(type == RewardType.SWEEP, item.getItemId(),
							item.getMinDrop(), item.getMaxDrop(), chance));
					if (result.size() > MAX_REWARDS) return result;
				}
			}
		}
		result.sort(Comparator.comparing((Row row) -> row.spoil).thenComparing(Comparator.comparingDouble((Row row) -> row.chance).reversed())
				.thenComparingInt(row -> row.itemID));
		return result;
	}

	static boolean isHerbDrop(int itemID) {
		ItemTemplate item=ItemHolder.getInstance().getTemplate(itemID);
		// These older herb IDs are POTION/OTHER in this datapack rather than HERB.
		// Do not filter quest materials merely because their names contain "Herb".
		return (item!=null && item.isHerb()) || (itemID>=8154 && itemID<=8157) || itemID==8952 || itemID==8953;
	}
	static String uiField(String text) {return safeField(text).replace(',',' ');}
	static String itemIcon(ItemTemplate item) {
		if(item==null)return "";
		String icon=item.getIcon();
		if(icon==null || icon.length()>128 || !icon.matches("[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)+"))return "";
		return icon;
	}
	// One base roll; matches the reference's grouped/ungrouped probability convention.
	static double baseChance(double itemChance, double groupChance, boolean grouped) {
		if (!Double.isFinite(itemChance) || !Double.isFinite(groupChance)) return 0;
		double chance = itemChance * (grouped ? groupChance / RewardList.MAX_CHANCE : 1.0);
		return Math.max(0, Math.min(RewardList.MAX_CHANCE, chance));
	}

	static String percent(double chance) {
		return BigDecimal.valueOf(chance).divide(BigDecimal.valueOf(10000), 8, RoundingMode.HALF_UP)
				.stripTrailingZeros().toPlainString();
	}
	static String safe(String text) {
		if (text == null) return "Monster";
		String value = text.replace('|', ' ').replace('\r', ' ').replace('\n', ' ').replace('\0', ' ');
		return value.length() > 100 ? value.substring(0, 100) : value;
	}

	public void search(String[] args) { queryCatalog(args, false); }
	public void drops(String[] args) { queryCatalog(args, true); }
	public void catalog(String[] args) {
		Player player = self.get();
		if (player == null || args == null || args.length != 4) return;
		int request, page, mode;
		String query;
		try {
			request = Integer.parseInt(args[0]); page = Integer.parseInt(args[1]); mode = Integer.parseInt(args[2]);
			if (request <= 0 || request > 1000000 || page < -1 || page > 10000 || mode < 0 || mode > 3) return;
			query = "-".equals(args[3]) ? "" : decodeQuery(args[3]);
		} catch (IllegalArgumentException invalid) { return; }
		if (!enabled) { browserError(player, request, "Mob Informer desativado na config."); return; }
		if (!allowRead(player, "B")) { browserError(player, request, "Aguarde um instante e busque novamente."); return; }
		try {
			List<CatalogNPC> found = findNPCs(getCatalog(), mode, query);
			ACTIVE_CATALOGS.remove(player.getObjectId());
			if (page == -1) 
			{
				if (ACTIVE_CATALOGS.size() >= MAX_ACTIVE_CATALOG_STREAMS) 
				{
					// The client retries "Aguarde..." errors on its own.
					browserError(player, request, "Aguarde, servidor ocupado."); return;
				}
				if (found.size() > MAX_CATALOG_NPCS) { browserError(player, request, "Catalogo excede o limite do cliente."); return; }
				CatalogStream stream = new CatalogStream(player, request, mode, found);
				ACTIVE_CATALOGS.put(player.getObjectId(), stream);
				send(player, catalogBegin(request, -1, mode, found.size(), found.size()));
				stream.run();
				return;
			}
			int start = Math.min(found.size(), page * NPC_PAGE_SIZE);
			int end = Math.min(found.size(), start + NPC_PAGE_SIZE);
			send(player, "MOB_CATALOG|TYPE=BEGIN|REQ=" + request + "|PAGE=" + page + "|MODE=" + mode
					+ "|TOTAL=" + found.size() + "|ROWS=" + (end-start) + "|");
			for (int offset = start; offset < end; offset += 8) {
				StringBuilder packet = new StringBuilder("MOB_CATALOG|TYPE=ROWS|REQ=").append(request)
						.append("|OFFSET=").append(offset-start).append("|DATA=");
				for (int i = offset; i < Math.min(end,offset+8); i++) packet.append(found.get(i).wire());
				send(player, packet.append('|').toString());
			}
			send(player, "MOB_CATALOG|TYPE=END|REQ=" + request + "|COUNT=" + (end-start) + "|");
		} catch (RuntimeException failure) {
			LOG.warn("MobInformer NPC catalog failed",failure);
			browserError(player,request,"Nao foi possivel carregar a lista de NPCs.");
		}
	}

	static String catalogBegin(int request, int page, int mode, int total, int rows) 
	{
		return "MOB_CATALOG|TYPE=BEGIN|REQ="+request+"|PAGE="+page+"|MODE="+mode+"|TOTAL="+total+"|ROWS="+rows+"|";
	}

	static final int CATALOG_PACKET_ROWS = 16;
	static final int CATALOG_PACKETS_PER_TICK = 4;

	static String catalogRows(int request, List<CatalogNPC> found, int offset, int end) 
	{
		Catalog shared = catalog;
		if (shared != null && found == shared.npcs && offset % CATALOG_PACKET_ROWS == 0
				&& end == Math.min(found.size(), offset + CATALOG_PACKET_ROWS))
			return "MOB_CATALOG|TYPE=ROWS|REQ=" + request + "|OFFSET=" + offset + "|DATA=" + shared.chunk(offset / CATALOG_PACKET_ROWS) + "|";

		StringBuilder packet = new StringBuilder("MOB_CATALOG|TYPE=ROWS|REQ=").append(request)
				.append("|OFFSET=").append(offset).append("|DATA=");
		for (int i=offset; i<end; i++) packet.append(found.get(i).wire());
		return packet.append('|').toString();
	}
	private static final class CatalogStream implements Runnable {
		final Player player; final int request; final List<CatalogNPC> rows; int offset; long lastBatchAt;
		CatalogStream(Player player, int request, int mode, List<CatalogNPC> rows) {
			this.player=player;this.request=request;this.rows=rows;
		}
		@Override public void run() 
		{
			int id=player.getObjectId();
			if (ACTIVE_CATALOGS.get(id)!=this || !enabled || !player.isOnline() || player.isInOfflineMode())
			{
				ACTIVE_CATALOGS.remove(id,this);return;
			}
			// A detail request has priority: pause the list briefly so its reply is not queued behind catalog packets.
			long now = System.currentTimeMillis();
            Long lastDetail = LAST_CATALOG_REQUEST.get(id + "V");
            // While paused, still let one small batch through every 300 ms so continuous clicking never freezes the list.
            if (lastDetail != null && now - lastDetail < 700 && now - lastBatchAt < 300)
            {
                ThreadPoolManager.getInstance().schedule(this, 100);
                return;
            }
            lastBatchAt = now;
			try {
				// Eight small packets per tick, allowing target info and other traffic through.
				for (int batch=0;batch<CATALOG_PACKETS_PER_TICK && offset<rows.size();batch++) 
				{
					if(ACTIVE_CATALOGS.get(id)!=this)return;
					int end=Math.min(rows.size(),offset+CATALOG_PACKET_ROWS);
					send(player,catalogRows(request,rows,offset,end));offset=end;
				}
				if (offset==rows.size()) {
					if(ACTIVE_CATALOGS.remove(id,this))send(player,"MOB_CATALOG|TYPE=END|REQ="+request+"|COUNT="+rows.size()+"|");
				} else ThreadPoolManager.getInstance().schedule(this,20);
			} catch(RuntimeException failure) {
				ACTIVE_CATALOGS.remove(id,this);browserError(player,request,"Falha no catalogo. Busque novamente.");
				LOG.warn("MobInformer full catalog failed",failure);
			}
		}
	}

	/**
	 * Shift + clique em NPC/monstro (via ShiftClickHook, registrado no onLoad), com InterfaceShowMobInformer e
	 * MobInformerShiftClick ligados:
	 * <ul>
	 * <li>jogador: abre so o Monster Data (a janela original nao abre, mesmo com AllowShiftClick/AltShowDroplist);</li>
	 * <li>GM: abre o Monster Data e tambem a janela original de admin do NPC.</li>
	 * </ul>
	 * Retorna false quando nao trata (config desligada, pet, objeto que nao e NPC, variavel noShift) ou quando o
	 * Action deve seguir tambem para a janela original (GM).
	 */
	private static boolean handleShiftClick(Player player, GameObject object) {
		if (!enabled || !shiftClick || !(object instanceof NpcInstance)
				|| object.isPet() || player.getVarB("noShift") || !((NpcInstance)object).isTargetable()) {
			return false;
		}
		NpcInstance npc=(NpcInstance)object;
		if(player.getTarget()!=npc)npc.onAction(player,false);
		if(player.getTarget()==npc && allowRead(player,"S")) {
			send(player,featurePacket());
			send(player,"MOB_INFO|TYPE=OPEN|OBJECT="+npc.getObjectId()+"|");
		}
		if(player.isGM())return false; // GM: segue tambem para a janela original de admin
		player.sendActionFailed();
		return true;
	}

	private static boolean allowRead(Player player,String operation) 
	{
		long now=System.currentTimeMillis();
		String key=player.getObjectId()+operation;
		Long previous=LAST_CATALOG_REQUEST.get(key);
		// A rejected request must not extend the block, otherwise rapid clicks keep failing.
		if(previous!=null && now-previous<350)
			return false;
		LAST_CATALOG_REQUEST.put(key,now);
		if(LAST_CATALOG_REQUEST.size()>4096)LAST_CATALOG_REQUEST.entrySet().removeIf(e->now-e.getValue()>60000);
		return true;
	}

	public void detail(String[] args) 
	{
		Player player=self.get();
		if(player==null || args==null || args.length!=2)return;
		int request,npcID;
		try {
			request=Integer.parseInt(args[0]);npcID=Integer.parseInt(args[1]);
			if(request<=0 || request>1000000 || npcID<=0 || npcID>10000000)return;
		}catch(NumberFormatException invalid){return;}
		if(!enabled){error(player,request,"Mob Informer desativado na config.");return;}
		if(!allowRead(player,"V")){error(player,request,"Aguarde um instante e selecione novamente.");return;}
		NpcTemplate npc=NpcHolder.getInstance().getTemplate(npcID);
		if(npc==null){error(player,request,"NPC nao cadastrado no servidor.");return;}
		try {
			CatalogNPC cached=getCatalog().byId.get(npcID);
			List<Row> rows=cached!=null?cached.rows:rewards(npc);
			if(rows.size()>MAX_REWARDS){error(player,request,"Lista de recompensas muito grande.");return;}
			List<QuestRow> quests=questRows(npc);
			Location loc=getSpawnLocations().get(npcID);
			send(player,templateBegin(npc,request,rows.size(),loc,preview3D,animation,quests.size()));
			for(int offset=0;offset<rows.size();offset+=CHUNK_ROWS){
				StringBuilder chunk=new StringBuilder("MOB_INFO|TYPE=ITEMS|REQ=").append(request).append("|OFFSET=").append(offset).append("|DATA=");
				for(int i=offset;i<Math.min(rows.size(),offset+CHUNK_ROWS);i++)chunk.append(rows.get(i).uiWire());
				send(player,chunk.append('|').toString());
			}
			sendQuestRows(player,request,quests);
			send(player,"MOB_INFO|TYPE=END|REQ="+request+"|COUNT="+rows.size()+"|QUESTCOUNT="+quests.size()+"|");
		}catch(RuntimeException failure){LOG.warn("MobInformer template detail failed for NPC "+npcID,failure);error(player,request,"Nao foi possivel carregar este NPC.");}
	}
	static String templateBegin(NpcTemplate npc,int request,int rowCount,Location loc,boolean preview,boolean animate) {
		return templateBegin(npc,request,rowCount,loc,preview,animate,0);
	}
	static String templateBegin(NpcTemplate npc,int request,int rowCount,Location loc,boolean preview,boolean animate,int questCount) {
		return "MOB_INFO|TYPE=BEGIN|REQ="+request+"|OBJECT=0|NPC="+npc.npcId+"|DISPLAY="+(npc.displayId>0?npc.displayId:npc.npcId)
				+"|NAME="+safe(npc.name)+"|KIND="+(npc.isRaid?"Raid Boss":npc.isInstanceOf(MonsterInstance.class)?"Monster":"NPC")
				+"|LEVEL="+npc.level+"|HP="+Math.max(0L,Math.round(npc.baseHpMax*npc.rateHp))+"|MP="+Math.max(0L,Math.round(npc.baseMpMax))
				+"|PATK="+npc.basePAtk+"|PDEF="+npc.basePDef+"|MATK="+npc.baseMAtk+"|MDEF="+npc.baseMDef
				+"|EXP="+npc.rewardExp+"|SP="+npc.rewardSp+"|SOURCE=TEMPLATE|HASSITE="+(loc==null?0:1)
				+"|X="+(loc==null?0:loc.x)+"|Y="+(loc==null?0:loc.y)+"|Z="+(loc==null?0:loc.z)
				+"|HEIGHT="+Math.max(1.0,npc.collisionHeight)+"|ROWS="+rowCount+"|PREVIEW="+(preview?1:0)+"|ANIMATION="+(animate?1:0)+"|QUESTS="+questCount+"|";
	}
	private static Map<Integer,Location> getSpawnLocations() {
		Map<Integer,Location> result=spawnLocations;
		if(result==null)synchronized(MobInformer.class){
			result=spawnLocations;
			if(result==null){
				Map<Integer,Location> found=new HashMap<>();
				List<String> groups=new ArrayList<>(SpawnHolder.getInstance().getSpawns().keySet());groups.sort(String::compareTo);
				for(String group:groups)for(SpawnTemplate spawn:SpawnHolder.getInstance().getSpawn(group))
					for(int i=0;i<spawn.getNpcSize();i++){
						SpawnNpcInfo info=spawn.getNpcId(i);
						if(found.containsKey(info.getNpcId()))continue;
						Location loc=info.getSpawnLoc();
						if(loc==null && spawn.getSpawnRangeSize()>0)loc=spawn.getSpawnRange(0).getRandomLoc(0);
						if(loc!=null)found.put(info.getNpcId(),new Location(loc.x,loc.y,loc.z));
					}
				result=Collections.unmodifiableMap(new HashMap<>(found));spawnLocations=result;
			}
		}
		return result;
	}
	static List<CatalogNPC> findNPCs(Catalog data,int mode,String query) 
	{
		if(mode<0 || mode>3)throw new IllegalArgumentException("mode");
		if(mode==3)return findQuestNPCs(data,query);
		// Empty NPC search is the common case: reuse the shared, immutable full list.
		if(mode==0 && query.isEmpty())return data.npcs;
		String key=normalize(query);boolean numeric=query.matches("[0-9]+");
		Set<Integer> items=new HashSet<>();
		if(mode!=0 && !query.isEmpty())for(CatalogItem item:findItems(data.items,query))items.add(item.id);
		List<CatalogNPC> found=new ArrayList<>();
		for(CatalogNPC npc:data.npcs)
		{
			boolean match;
			if(mode==0)match=query.isEmpty() || (numeric?String.valueOf(npc.id).equals(query):npc.searchName.contains(key));
			else match=npc.rows.stream().anyMatch(row->row.spoil==(mode==2) && (query.isEmpty() || items.contains(row.itemID)));
			if(match)found.add(npc);
		}
		return found;
	}
	private static void browserError(Player player,int request,String text){send(player,"MOB_CATALOG|TYPE=ERROR|REQ="+request+"|TEXT="+safe(text)+"|");}
	private void queryCatalog(String[] args, boolean mobs) {
		Player player = self.get();
		if (player == null || args == null || args.length != 3) return;
		String kind = mobs ? "M" : "I";
		int request, page, itemID = 0;
		String query = "";
		try {
			request = Integer.parseInt(args[0]); page = Integer.parseInt(args[1]);
			if (request <= 0 || request > 1000000 || page < 0 || page > 10000) return;
			if (mobs) { itemID = Integer.parseInt(args[2]); if (itemID <= 0) return; }
			else query = decodeQuery(args[2]);
		} catch (IllegalArgumentException invalid) { return; }
		if (!enabled) { catalogError(player, kind, request, "Mob Informer desativado na config."); return; }
		long now = System.currentTimeMillis();
		Long previous = LAST_CATALOG_REQUEST.put(player.getObjectId() + kind, now);
		if (LAST_CATALOG_REQUEST.size() > 4096) LAST_CATALOG_REQUEST.entrySet().removeIf(e -> now - e.getValue() > 60000);
		if (previous != null && now - previous < 350) { catalogError(player, kind, request, "Aguarde um instante e tente novamente."); return; }
		try {
			Catalog data = getCatalog();
			List<String> rows = new ArrayList<>();
			int total;
			if (mobs) {
				List<DropSource> sources = data.sources.getOrDefault(itemID, Collections.emptyList()); total = sources.size();
				int start = pageStart(page, total);
				for (int i = start; i < Math.min(total, start + PAGE_SIZE); i++) rows.add(sources.get(i).wire());
			} else {
				List<CatalogItem> found = findItems(data.items, query); total = found.size();
				int start = pageStart(page, total);
				for (int i = start; i < Math.min(total, start + PAGE_SIZE); i++) rows.add(found.get(i).wire());
			}
			send(player, "MOB_SEARCH|KIND=" + kind + "|TYPE=BEGIN|REQ=" + request + "|PAGE=" + page
					+ "|ITEM=" + itemID + "|TOTAL=" + total + "|ROWS=" + rows.size() + "|");
			// Eight bounded names per packet keep the native TutorialShowHtml channel small.
			for (int offset = 0; offset < rows.size(); offset += 8) {
				StringBuilder packet = new StringBuilder("MOB_SEARCH|KIND=").append(kind).append("|TYPE=ROWS|REQ=")
						.append(request).append("|OFFSET=").append(offset).append("|DATA=");
				for (int i = offset; i < Math.min(rows.size(), offset + 8); i++) packet.append(rows.get(i));
				send(player, packet.append('|').toString());
			}
			send(player, "MOB_SEARCH|KIND=" + kind + "|TYPE=END|REQ=" + request + "|COUNT=" + rows.size() + "|");
		} catch (RuntimeException failure) {
			LOG.warn("MobInformer global item catalog failed", failure);
			catalogError(player, kind, request, "Nao foi possivel consultar o catalogo.");
		}
	}
	static String decodeQuery(String encoded) {
		if (encoded == null || encoded.length() < 4 || encoded.length() > 256 || encoded.length() % 4 != 0) throw new IllegalArgumentException("length");
		StringBuilder value = new StringBuilder();
		for (int i = 0; i < encoded.length(); i += 4) {
			int code = 0;
			for (int j = 0; j < 4; j++) { int digit = Character.digit(encoded.charAt(i + j), 16); if (digit < 0) throw new IllegalArgumentException("hex"); code = code * 16 + digit; }
			if (Character.isISOControl(code) || Character.isSurrogate((char)code)) throw new IllegalArgumentException("character");
			value.append((char)code);
		}
		String query = value.toString().trim();
		if (query.length() < 2 && !query.matches("[1-9]")) throw new IllegalArgumentException("short query");
		return query;
	}
	static String normalize(String text) { return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toUpperCase(Locale.ROOT); }
	static int pageStart(int page, int total) { if (page < 0 || page > 10000) throw new IllegalArgumentException("page"); return Math.min(total, page * PAGE_SIZE); }
	static List<CatalogItem> findItems(List<CatalogItem> items, String query) {
		String key = normalize(query);
		boolean numeric = query.matches("[0-9]+");
		List<CatalogItem> matches = new ArrayList<>();
		for (CatalogItem item : items) if (numeric ? String.valueOf(item.id).equals(query) : item.searchName.contains(key)) matches.add(item);
		return matches;
	}
	private static Catalog getCatalog() {
		Catalog result = catalog;
		if (result == null) synchronized (MobInformer.class) {
			result = catalog;
			if (result == null) {
				result = buildCatalog(ItemHolder.getInstance().getAllTemplates(), NpcHolder.getInstance().getAll());
				catalog = result;
				LOG.info("MobInformer catalog ready: {} items, {} reward item IDs.", result.items.size(), result.sources.size());
			}
		}
		return result;
	}
	static Catalog buildCatalog(ItemTemplate[] items, NpcTemplate[] templates) {
		List<CatalogItem> itemRows = new ArrayList<>(); Map<Integer, List<DropSource>> sources = new HashMap<>();
		List<CatalogNPC> npcRows=new ArrayList<>();
		for (ItemTemplate item : items) if (item != null && item.getItemId() > 0)
			itemRows.add(new CatalogItem(item.getItemId(), item.getName(), item.getAdditionalName()));
		itemRows.sort(Comparator.comparingInt(item -> item.id));
		for (NpcTemplate npc : templates) {
			if(npc==null || npc.npcId<=0)continue;
			List<Row> rows=rewards(npc);
			npcRows.add(new CatalogNPC(npc,rows));
			// Preserve the legacy global-item window's monster-only index.
			if(npc.isInstanceOf(MonsterInstance.class))for (Row reward : rows) sources.computeIfAbsent(reward.itemID, unused -> new ArrayList<>()).add(new DropSource(npc, reward));
		}
		sources.replaceAll((id, rows) -> {
			rows.sort(Comparator.comparingInt((DropSource row) -> row.level).thenComparing(row -> row.name)
					.thenComparingInt(row -> row.npcID).thenComparing(row -> row.reward.spoil)
					.thenComparing(Comparator.comparingDouble((DropSource row) -> row.reward.chance).reversed()));
			return Collections.unmodifiableList(new ArrayList<>(rows));
		});
		npcRows.sort(Comparator.comparingInt((CatalogNPC npc)->npc.level).thenComparing(npc->npc.searchName).thenComparingInt(npc->npc.id));
		return new Catalog(Collections.unmodifiableList(new ArrayList<>(itemRows)), Collections.unmodifiableMap(new HashMap<>(sources)),Collections.unmodifiableList(new ArrayList<>(npcRows)));
	}

	/** Read registered quest listeners only; never invoke quest events or create player states. */
	private static final Map<Integer, List<QuestRow>> QUEST_ROWS_CACHE = new ConcurrentHashMap<>();
	static List<QuestRow> questRows(NpcTemplate npc) 
	{
		if (npc == null) return Collections.emptyList();
		List<QuestRow> cached = QUEST_ROWS_CACHE.get(npc.npcId);
		if (cached == null) { cached = computeQuestRows(npc); QUEST_ROWS_CACHE.put(npc.npcId, cached); }
		return cached;
	}
	private static List<QuestRow> computeQuestRows(NpcTemplate npc) 
	{
		if(npc==null)return Collections.emptyList();
		Map<Integer,QuestRow> found=new TreeMap<>();
		for(QuestEventType event:QuestEventType.values()) 
		{
			Quest[] quests=npc.getEventQuests(event);
			if(quests==null)continue;
			int role=event==QuestEventType.QUEST_START?1:event==QuestEventType.QUEST_TALK?2:
				event==QuestEventType.MOB_KILLED_WITH_QUEST?4:8;
			for(Quest quest:quests) {
				if(quest==null || !quest.isVisible())continue;
				int id=quest.getQuestIntId();
				if(id<=0 || id>1000000)continue;
				QuestRow row=found.get(id);
				if(row==null)found.put(id,new QuestRow(id,questTitle(id,quest.getName()),role));
				else row.roles|=role;
				if(found.size()>MAX_QUESTS)throw new IllegalArgumentException("Too many related quests");
			}
		}
		return Collections.unmodifiableList(new ArrayList<>(found.values()));
	}
	static String questTitle(int id,String script) {
		Properties names=questTitles;
		if(names==null)synchronized(MobInformer.class) {
			names=questTitles;
			if(names==null) {
				names=new Properties();
				File file=new File("data/string/strings_en.properties");
				if(file.isFile())try(InputStreamReader input=new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8)) {
					names.load(input);
				}catch(IOException failure){LOG.warn("MobInformer: cannot read quest titles",failure);}
				questTitles=names;
			}
		}
		String title=names.getProperty("q."+id);
		if(title!=null && !title.trim().isEmpty())return safeField(title);
		title=script==null?"":script.replaceFirst("^_[0-9]+_","").replace('_',' ')
				.replaceAll("([a-z0-9])([A-Z])","$1 $2").replaceAll("([A-Z])([A-Z][a-z])","$1 $2").trim();
		return safeField(title.isEmpty()?"Quest #"+id:title);
	}
	static List<CatalogNPC> findQuestNPCs(Catalog data,String query) {
		String key=normalize(query);boolean numeric=query.matches("[0-9]+");
		List<CatalogNPC> found=new ArrayList<>();
		for(CatalogNPC npc:data.npcs)for(QuestRow quest:questRows(npc.template)) {
			if(query.isEmpty() || (numeric?String.valueOf(quest.id).equals(query):normalize(quest.name).contains(key))) {
				found.add(npc);break;
			}
		}
		return found;
	}
	static String questChunk(int request,List<QuestRow> rows,int offset,int end) {
		StringBuilder packet=new StringBuilder("MOB_INFO|TYPE=QUESTS|REQ=").append(request)
				.append("|OFFSET=").append(offset).append("|DATA=");
		for(int i=offset;i<end;i++)packet.append(rows.get(i).wire());
		return packet.append('|').toString();
	}
	private static void sendQuestRows(Player player,int request,List<QuestRow> rows) {
		for(int offset=0;offset<rows.size();offset+=CHUNK_ROWS)
			send(player,questChunk(request,rows,offset,Math.min(rows.size(),offset+CHUNK_ROWS)));
	}
	static final class QuestRow {
		final int id;final String name;int roles;
		QuestRow(int id,String name,int roles){this.id=id;this.name=name;this.roles=roles;}
		String wire(){return id+"^"+safeField(name)+"^"+roles+";";}
	}

	static String safeField(String text) { return safe(text).replace('^', ' ').replace(';', ' '); }
	private static void catalogError(Player player, String kind, int request, String text) {
		send(player, "MOB_SEARCH|KIND=" + kind + "|TYPE=ERROR|REQ=" + request + "|TEXT=" + safe(text) + "|");
	}
	static final class Catalog 
	{
		final List<CatalogItem> items; final Map<Integer, List<DropSource>> sources; final List<CatalogNPC> npcs;
		final Map<Integer, CatalogNPC> byId;
		private volatile String[] chunks;
        /** DATA field of catalog packet number index for the full NPC list; built once and shared by every player. */
        String chunk(int index) 
        {
            String[] cached = chunks;
            if (cached == null) synchronized (this) 
            {
                cached = chunks;
                if (cached == null) {
                    cached = new String[(npcs.size() + CATALOG_PACKET_ROWS - 1) / CATALOG_PACKET_ROWS];
                    for (int i = 0; i < cached.length; i++) {
                        StringBuilder data = new StringBuilder();
                        for (int row = i * CATALOG_PACKET_ROWS; row < Math.min(npcs.size(), (i + 1) * CATALOG_PACKET_ROWS); row++) data.append(npcs.get(row).wire());
                        cached[i] = data.toString();
                    }
                    chunks = cached;
                }
            }
            return cached[index];
        }
		Catalog(List<CatalogItem> items, Map<Integer, List<DropSource>> sources,List<CatalogNPC> npcs)
		{
			this.items = items; this.sources = sources; this.npcs=npcs;
			Map<Integer, CatalogNPC> index=new HashMap<>();
			for(CatalogNPC npc:npcs)index.put(npc.id,npc);
			this.byId=Collections.unmodifiableMap(index);
		}
	}
	static final class CatalogNPC 
	{
		final int id,displayID,level;final String name,searchName;final List<Row> rows;final NpcTemplate template;
		CatalogNPC(NpcTemplate npc,List<Row> rows){template=npc;id=npc.npcId;displayID=npc.displayId>0?npc.displayId:npc.npcId;level=npc.level;name=npc.name;searchName=normalize(name==null?"NPC #"+id:name);this.rows=Collections.unmodifiableList(new ArrayList<>(rows));}
		private volatile String wireCache;
		String wire()
		{
			String cached=wireCache;
			if(cached==null){cached=id+"^"+displayID+"^"+safeField(name)+"^"+level+";";wireCache=cached;}
			return cached;
		}
	}
	static final class CatalogItem {
		final int id; final String name; final String searchName;
		CatalogItem(int id, String name, String extra) { this.id = id; this.name = name == null ? "Item #" + id : name; this.searchName = normalize(this.name + " " + (extra == null ? "" : extra)); }
		String wire() { return id + "^" + safeField(name) + ";"; }
	}
	static final class DropSource {
		final int npcID, displayID, level; final String name; final Row reward;
		DropSource(NpcTemplate npc, Row reward) { npcID = npc.npcId; displayID = npc.displayId > 0 ? npc.displayId : npc.npcId; level = npc.level; name = npc.name; this.reward = reward; }
		String wire() { return npcID + "^" + displayID + "^" + safeField(name) + "^" + level + "^" + (reward.spoil ? "S" : "D")
				+ "^" + reward.minimum + "^" + reward.maximum + "^" + percent(reward.chance) + ";"; }
	}
	private static void error(Player player, int requestID, String text) {
		send(player, "MOB_INFO|TYPE=ERROR|REQ=" + requestID + "|TEXT=" + safe(text) + "|");
	}
	private static void send(Player player, String payload) { player.sendPacket(new TutorialShowHtml(payload)); }

	static final class Row {
		final boolean spoil;
		final int itemID;
		final long minimum;
		final long maximum;
		final double chance;
		Row(boolean spoil, int itemID, long minimum, long maximum, double chance) {
			this.spoil = spoil; this.itemID = itemID; this.minimum = minimum; this.maximum = maximum; this.chance = chance;
		}
		String wire() { return (spoil ? "S" : "D") + "," + itemID + "," + minimum + "," + maximum + "," + percent(chance) + ";"; }
		String uiWire() {
			ItemTemplate item=ItemHolder.getInstance().getTemplate(itemID);
			return (spoil ? "S" : "D") + "," + itemID + "," + minimum + "," + maximum + "," + percent(chance)
			+ ","+itemIcon(item)+","+uiField(item==null?"Item #"+itemID:item.getName())+";";
		}
	}
}

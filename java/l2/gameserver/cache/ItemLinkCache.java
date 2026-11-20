package l2.gameserver.cache;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;

/**
 * Item no chat (shift+clique): guarda uma "foto" imutável do item e devolve um token aleatório de 24 caracteres.
 * O token vai na mensagem como [#:token]; o servidor põe o nome e quem clica pede a foto pelo token (services.ItemLink).
 *
 * O token não é o objectId: não dá para ver itens de ninguém a partir dele. A foto não muda se o item for vendido,
 * encantado ou destruído depois. Lotada a capacidade, sai a mais antiga; cada foto vale por um tempo (config).
 *
 * Fica no núcleo (e não no services.ItemLink) porque o Say2C usa {@link #rewriteChat} para trocar o nome que o
 * cliente escreveu pelo nome real do item: sem isso, um cliente alterado poderia linkar um item +0 e mostrar
 * "+16 ..." no chat.
 */
public final class ItemLinkCache
{
	/** Foto do item no momento do link. shadowSeconds: -1 = não é item de sombra. */
	public record Snapshot(int itemId, int enchant, long count, int augment1, int augment2, int shadowSeconds)
	{
	}

	private record Entry(Snapshot snapshot, long expires)
	{
	}

	/**
	 * Tamanho do token. Tem que ser 24: a Interface.dll do cliente (ícone na caixa de texto, link no balão sobre a
	 * cabeça) só reconhece token de 24. O ChatWnd usa o mesmo (ITEM_LINK_TOKEN_LEN).
	 */
	public static final int TOKEN_LEN = 24;
	/**
	 * Link no chat. Quem envia manda só [#:token] (ocupa menos que o nome na caixa de texto); o servidor devolve
	 * [#:token:nome] para quem recebe.
	 */
	public static final String LINK_REGEX = "\\[#:([0-9a-f]{24})(?::([^\\]]{0,100}))?\\]";
	/** Qualquer coisa com cara de link, inclusive com o token estragado: vira só o nome, nunca código cru no chat. */
	private static final Pattern ANY_LINK = Pattern.compile("\\[#:([^:\\]]{1,40})(?::([^\\]]{0,100}))?\\]");
	/** Máximo de links por mensagem (o cliente também limita a 3). */
	public static final int MAX_PER_MESSAGE = 3;

	private static final ItemLinkCache INSTANCE = new ItemLinkCache();
	private static final SecureRandom RANDOM = randomGenerator();

	private final LinkedHashMap<String, Entry> entries = new LinkedHashMap<>();
	private final LinkedHashMap<Integer, Long> makers = new LinkedHashMap<>();
	private final LinkedHashMap<Integer, Long> readers = new LinkedHashMap<>();

	private volatile boolean enabled;
	private int capacity = 4096;
	private long ttlMillis = 3600000L;

	private ItemLinkCache()
	{
	}

	public static ItemLinkCache getInstance()
	{
		return INSTANCE;
	}

	private static SecureRandom randomGenerator()
	{
		try
		{
			return SecureRandom.getInstance("DRBG");
		}
		catch (NoSuchAlgorithmException unavailable)
		{
			return new SecureRandom();
		}
	}

	/** Chamado pelo services.ItemLink no boot e no //reload_config. */
	public synchronized void configure(boolean on, int maxEntries, long ttl)
	{
		enabled = on;
		capacity = Math.max(128, Math.min(65536, maxEntries));
		ttlMillis = Math.max(60000L, Math.min(86400000L, ttl));
		if (!on)
			clear();
		while (entries.size() > capacity)
			removeOldest(entries);
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public synchronized int size()
	{
		return entries.size();
	}

	public synchronized void clear()
	{
		entries.clear();
		makers.clear();
		readers.clear();
	}

	public static boolean validToken(String token)
	{
		if (token == null || token.length() != TOKEN_LEN)
			return false;
		for (int i = 0; i < TOKEN_LEN; i++)
		{
			char c = token.charAt(i);
			if (!(c >= '0' && c <= '9' || c >= 'a' && c <= 'f'))
				return false;
		}
		return true;
	}

	/** Anti-spam por jogador: criar um link a cada 300 ms, abrir a cada 100 ms. */
	public synchronized boolean allow(int playerId, boolean make, long now)
	{
		if (playerId <= 0)
			return false;
		Map<Integer, Long> requests = make ? makers : readers;
		Long last = requests.get(playerId);
		if (last != null && now - last < (make ? 300L : 100L))
			return false;
		requests.remove(playerId);
		requests.put(playerId, now);
		if (requests.size() > 8192)
			removeOldest(requests);
		return true;
	}

	/** Foto do item (null se o item não serve). */
	public static Snapshot snapshotOf(ItemInstance item)
	{
		if (item == null || item.getItemId() <= 0 || item.getCount() <= 0 || item.getEnchantLevel() < 0)
			return null;
		int shadow = item.isShadowItem() ? Math.max(0, item.getDuration()) : -1;
		return new Snapshot(item.getItemId(), Math.min(65535, item.getEnchantLevel()), item.getCount(),
			item.getVariationStat1(), item.getVariationStat2(), shadow);
	}

	public synchronized String put(Snapshot snapshot, long now)
	{
		Iterator<Entry> purge = entries.values().iterator();
		while (purge.hasNext())
		{
			if (purge.next().expires > now)
				break;
			purge.remove();
		}
		while (entries.size() >= capacity)
			removeOldest(entries);

		// Dígito e letra (a-f) alternados: "3b9e2a...". Nunca há dois dígitos ou duas letras seguidos, então o filtro
		// de palavras do cliente (que troca números/palavras proibidas por "-_-") não acha nada para trocar no token.
		// 12 dígitos x 12 letras de 6 ≈ 72 bits de acaso. O Say2C pula o link no translit.
		char[] chars = new char[TOKEN_LEN];
		String token;
		do
		{
			for (int at = 0; at < chars.length; at++)
				chars[at] = at % 2 == 0 ? (char) ('0' + RANDOM.nextInt(10)) : (char) ('a' + RANDOM.nextInt(6));
			token = new String(chars);
		}
		while (entries.containsKey(token));

		entries.put(token, new Entry(snapshot, now + ttlMillis));
		return token;
	}

	public synchronized Snapshot get(String token, long now)
	{
		if (!validToken(token))
			return null;
		Entry entry = entries.get(token);
		if (entry == null)
			return null;
		if (entry.expires <= now)
		{
			entries.remove(token);
			return null;
		}
		return entry.snapshot;
	}

	/**
	 * Link pronto para um texto que o servidor manda (ex.: anúncio do leilão): "[#:token:nome]", que o ChatWnd mostra com
	 * o ícone. Com o sistema desligado (ou item inválido) devolve só o nome.
	 */
	public String linkText(int itemId, int enchant, long count, String fallback)
	{
		if (!enabled || itemId <= 0 || count <= 0)
			return fallback;
		Snapshot s = new Snapshot(itemId, Math.max(0, Math.min(65535, enchant)), count, 0, 0, -1);
		return "[#:" + put(s, System.currentTimeMillis()) + ":" + caption(s) + "]";
	}

	/**
	 * Mensagem do chat: em cada [#:token:nome] o nome passa a ser o do item real da foto. Token inexistente ou vencido
	 * vira só o texto (sem link); a partir do 4º link também.
	 */
	public String rewriteChat(String text)
	{
		if (!enabled || text == null || text.indexOf("[#:") < 0)
			return text;

		long now = System.currentTimeMillis();
		Matcher m = ANY_LINK.matcher(text);
		StringBuilder out = new StringBuilder(text.length() + 16);
		int links = 0;
		while (m.find())
		{
			Snapshot s = links < MAX_PER_MESSAGE && validToken(m.group(1)) ? get(m.group(1), now) : null;
			String replacement;
			if (s != null)
			{
				replacement = "[#:" + m.group(1) + ":" + caption(s) + "]";
				links++;
			}
			else
				replacement = "(" + (m.group(2) != null && !m.group(2).isBlank() ? clean(m.group(2)) : "item") + ")";
			m.appendReplacement(out, Matcher.quoteReplacement(replacement));
		}
		m.appendTail(out);
		return out.toString();
	}

	/** "+12 Augmented Saint Spear Haste" — o mesmo texto que o ChatWnd monta (ItemLinkWnd.Caption). */
	public static String caption(Snapshot s)
	{
		ItemTemplate t = ItemHolder.getInstance().getTemplate(s.itemId());
		String name = t != null && t.getName() != null ? t.getName() : "Item " + s.itemId();
		if (s.augment1() != 0 || s.augment2() != 0)
			name = "Augmented " + name;
		if (s.enchant() > 0)
			name = "+" + s.enchant() + " " + name;
		String add = t != null ? t.getAdditionalName() : null;
		if (add != null && !add.trim().isEmpty())
			name = name + " " + add.trim();
		return clean(name);
	}

	/** Sem colchetes nem controle; até 90 caracteres. */
	private static String clean(String text)
	{
		StringBuilder sb = new StringBuilder(Math.min(90, text.length()));
		for (int i = 0; i < text.length() && sb.length() < 90; i++)
		{
			char c = text.charAt(i);
			if (c == '[')
				c = '(';
			else if (c == ']')
				c = ')';
			else if (c < 32)
				c = ' ';
			sb.append(c);
		}
		return sb.toString();
	}

	private static <K> void removeOldest(Map<K, ?> map)
	{
		Iterator<K> it = map.keySet().iterator();
		if (it.hasNext())
		{
			it.next();
			it.remove();
		}
	}
}

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
 * Item no chat (shift+clique): guarda uma "foto" imutável do item e devolve um token aleatório de 24 dígitos.
 * O token vai na mensagem como [#:token:nome]; quem clica pede a foto pelo token (services.ItemLink).
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

	/** [#:token:nome] — o mesmo formato que o ChatWnd monta. */
	private static final Pattern LINK = Pattern.compile("\\[#:([0-9a-f]{24}):([^\\]]{0,100})\\]");
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
		if (token == null || token.length() != 24)
			return false;
		for (int i = 0; i < 24; i++)
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

		// só dígitos: passa intacto pelo translit do chat. 24 dígitos ≈ 80 bits de acaso.
		char[] digits = new char[24];
		byte[] entropy = new byte[32];
		String token;
		do
		{
			int at = 0;
			while (at < digits.length)
			{
				RANDOM.nextBytes(entropy);
				for (byte b : entropy)
				{
					int value = b & 255;
					if (value < 250)
						digits[at++] = (char) ('0' + value % 10);
					if (at == digits.length)
						break;
				}
			}
			token = new String(digits);
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
	 * Mensagem do chat: em cada [#:token:nome] o nome passa a ser o do item real da foto. Token inexistente ou vencido
	 * vira só o texto (sem link); a partir do 4º link também.
	 */
	public String rewriteChat(String text)
	{
		if (!enabled || text == null || text.indexOf("[#:") < 0)
			return text;

		long now = System.currentTimeMillis();
		Matcher m = LINK.matcher(text);
		StringBuilder out = new StringBuilder(text.length() + 16);
		int links = 0;
		while (m.find())
		{
			Snapshot s = links < MAX_PER_MESSAGE ? get(m.group(1), now) : null;
			String replacement;
			if (s != null)
			{
				replacement = "[#:" + m.group(1) + ":" + caption(s) + "]";
				links++;
			}
			else
				replacement = "(" + clean(m.group(2)) + ")";
			m.appendReplacement(out, Matcher.quoteReplacement(replacement));
		}
		m.appendTail(out);
		return out.toString();
	}

	/** "+12 Augmented Draconic Bow" — o mesmo texto que o ChatWnd monta (ItemLinkWnd.Caption). */
	public static String caption(Snapshot s)
	{
		ItemTemplate t = ItemHolder.getInstance().getTemplate(s.itemId());
		String name = t != null && t.getName() != null ? t.getName() : "Item " + s.itemId();
		if (s.augment1() != 0 || s.augment2() != 0)
			name = "Augmented " + name;
		if (s.enchant() > 0)
			name = "+" + s.enchant() + " " + name;
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

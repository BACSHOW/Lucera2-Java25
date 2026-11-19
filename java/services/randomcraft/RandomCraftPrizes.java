package services.randomcraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import services.randomcraft.RandomCraftConfig.Reward;

/**
 * Janela "Prêmios do Random Craft": monta o HTML (NpcHtmlMessage) com tudo que
 * está no pool do randomcraft.properties e a chance de cada prêmio sair numa casa.
 *
 * chance por casa = peso da entrada / soma dos pesos de todas as entradas.
 * (Na lista de 5 casas o mesmo item não repete, então a chance real de aparecer
 * em alguma casa é um pouco maior; a coluna mostra a chance por casa.)
 *
 * Sem dependência do servidor: o nome e o ícone dos itens vêm de um Resolver.
 */
public final class RandomCraftPrizes
{
	public static final int PAGE_SIZE = 8;
	public static final String BYPASS = "bypass -h scripts_services.RandomCraft:rcPrizes ";

	public static final int KIND_OTHER = 0;
	public static final int KIND_WEAPON = 1;
	public static final int KIND_ARMOR = 2;   // armaduras e bijuterias

	public static final String[] FILTERS = { "all", "weapon", "armor", "other" };
	private static final String[] FILTER_NAMES = { "Todos", "Armas", "Armaduras/Joias", "Outros" };

	private static final String[] GRADE_COLOR = { "B0B0B0", "7CC576", "5DA9E8", "C57FE8", "F2B84B" };
	private static final String[] GRADE_NAME = { "Comum", "Incomum", "Raro", "Épico", "Lendário" };

	public static final class Info
	{
		public final String name;
		public final String icon;
		public final int kind;

		public Info(String name, String icon, int kind)
		{
			this.name = name;
			this.icon = icon;
			this.kind = kind;
		}
	}

	public interface Resolver
	{
		/** null se o item não existe */
		Info info(int itemId);
	}

	private RandomCraftPrizes()
	{
	}

	public static String normFilter(String f)
	{
		if (f != null)
			for (String s : FILTERS)
				if (s.equalsIgnoreCase(f.trim()))
					return s;
		return "all";
	}

	private static int kindOf(String filter)
	{
		if ("weapon".equals(filter))
			return KIND_WEAPON;
		if ("armor".equals(filter))
			return KIND_ARMOR;
		if ("other".equals(filter))
			return KIND_OTHER;
		return -1;
	}

	/** Número de páginas para um filtro (mínimo 1). */
	public static int pages(List<Reward> pool, Resolver res, String filter)
	{
		int n = filtered(pool, res, filter).size();
		return Math.max(1, (n + PAGE_SIZE - 1) / PAGE_SIZE);
	}

	private static List<Reward> filtered(List<Reward> pool, Resolver res, String filter)
	{
		int want = kindOf(normFilter(filter));
		List<Reward> out = new ArrayList<>();
		for (Reward r : pool)
		{
			Info i = res.info(r.id);
			if (i == null)
				continue;
			if (want >= 0 && i.kind != want)
				continue;
			out.add(r);
		}
		// mais comum primeiro; empate mantém a ordem do arquivo (sort estável)
		Collections.sort(out, new Comparator<Reward>()
		{
			@Override
			public int compare(Reward a, Reward b)
			{
				return Integer.compare(b.weight, a.weight);
			}
		});
		return out;
	}

	public static String html(List<Reward> pool, Resolver res, int page, String filter)
	{
		filter = normFilter(filter);
		long total = 0;
		for (Reward r : pool)
			total += r.weight;
		List<Reward> rows = filtered(pool, res, filter);
		int pages = Math.max(1, (rows.size() + PAGE_SIZE - 1) / PAGE_SIZE);
		if (page < 1)
			page = 1;
		if (page > pages)
			page = pages;

		StringBuilder b = new StringBuilder(4096);
		b.append("<html><body><center>");
		b.append("<font color=\"LEVEL\">Random Craft - Premios</font><br1>");
		b.append("<font color=\"B0B0B0\">").append(pool.size()).append(" premios no sorteio</font><br>");

		// filtros
		b.append("<table><tr>");
		for (int k = 0; k < FILTERS.length; k++)
		{
			b.append("<td>");
			if (FILTERS[k].equals(filter))
				b.append("<font color=\"LEVEL\">").append(FILTER_NAMES[k]).append("</font>");
			else
				b.append("<a action=\"").append(BYPASS).append("1 ").append(FILTERS[k]).append("\">").append(FILTER_NAMES[k]).append("</a>");
			b.append("</td>");
		}
		b.append("</tr></table><br>");

		if (rows.isEmpty())
			b.append("<font color=\"B0B0B0\">Nenhum premio nesta categoria.</font><br>");
		else
		{
			b.append("<table width=270>");
			int from = (page - 1) * PAGE_SIZE;
			int to = Math.min(rows.size(), from + PAGE_SIZE);
			for (int k = from; k < to; k++)
			{
				Reward r = rows.get(k);
				Info i = res.info(r.id);
				int g = Math.max(0, Math.min(GRADE_COLOR.length - 1, r.grade));
				b.append("<tr><td width=36><img src=\"").append(esc(i.icon)).append("\" width=32 height=32></td>");
				b.append("<td width=170><font color=\"").append(GRADE_COLOR[g]).append("\">");
				if (r.enchant > 0)
					b.append('+').append(r.enchant).append(' ');
				b.append(esc(i.name)).append("</font>");
				if (r.count > 1)
					b.append(" <font color=\"B0B0B0\">x").append(num(r.count)).append("</font>");
				b.append("<br1><font color=\"808080\">").append(GRADE_NAME[g]).append("</font></td>");
				b.append("<td width=64 align=right><font color=\"LEVEL\">").append(pct(r.weight, total)).append("</font></td></tr>");
			}
			b.append("</table><br>");
		}

		// paginação
		b.append("<table><tr><td width=70>");
		if (page > 1)
			b.append("<a action=\"").append(BYPASS).append(page - 1).append(' ').append(filter).append("\">&lt;&lt; Anterior</a>");
		b.append("</td><td width=100 align=center>Pagina ").append(page).append('/').append(pages).append("</td><td width=70 align=right>");
		if (page < pages)
			b.append("<a action=\"").append(BYPASS).append(page + 1).append(' ').append(filter).append("\">Proxima &gt;&gt;</a>");
		b.append("</td></tr></table><br>");
		b.append("<font color=\"808080\">Chance por casa da lista (5 casas). Itens iguais nao repetem na mesma lista.</font>");
		b.append("</center></body></html>");
		return b.toString();
	}

	/** Entradas por página da janela nativa (grade 8 x 8). */
	public static final int UI_PAGE_SIZE = 64;

	/**
	 * Pacote PRIZES da janela nativa:
	 *   filtro|pagina|paginas|total|n|id,qtd,enc,grau,chance|...
	 * chance = chance por casa em milésimos de porcento (3,250% = 3250).
	 */
	public static String packet(List<Reward> pool, Resolver res, int page, String filter)
	{
		filter = normFilter(filter);
		long total = 0;
		for (Reward r : pool)
			total += r.weight;
		List<Reward> rows = filtered(pool, res, filter);
		int pages = Math.max(1, (rows.size() + UI_PAGE_SIZE - 1) / UI_PAGE_SIZE);
		if (page < 1)
			page = 1;
		if (page > pages)
			page = pages;
		int from = (page - 1) * UI_PAGE_SIZE;
		int to = Math.min(rows.size(), from + UI_PAGE_SIZE);
		StringBuilder b = new StringBuilder(2048);
		b.append(filter).append('|').append(page).append('|').append(pages).append('|').append(rows.size()).append('|').append(to - from);
		for (int k = from; k < to; k++)
		{
			Reward r = rows.get(k);
			long chance = total <= 0 ? 0 : Math.round(r.weight * 100000.0 / total);
			b.append('|').append(r.id).append(',').append(r.count).append(',').append(r.enchant).append(',').append(r.grade).append(',').append(chance);
		}
		return b.toString();
	}

	static String pct(long weight, long total)
	{
		if (total <= 0)
			return "0%";
		double p = weight * 100.0 / total;
		String s;
		if (p >= 10)
			s = String.format(java.util.Locale.US, "%.1f", p);
		else if (p >= 1)
			s = String.format(java.util.Locale.US, "%.2f", p);
		else
			s = String.format(java.util.Locale.US, "%.3f", p);
		return s.replace('.', ',') + "%";
	}

	static String num(long n)
	{
		String s = Long.toString(n);
		StringBuilder b = new StringBuilder();
		int c = 0;
		for (int k = s.length() - 1; k >= 0; k--)
		{
			b.append(s.charAt(k));
			if (++c % 3 == 0 && k > 0)
				b.append('.');
		}
		return b.reverse().toString();
	}

	static String esc(String s)
	{
		if (s == null)
			return "";
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}
}

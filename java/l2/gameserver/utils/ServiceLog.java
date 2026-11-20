package l2.gameserver.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bloco de log de um sistema da interface (Auction House, Correio, Random Craft...), para o console do boot e do
 * //reload_config ficar organizado:
 *
 * <pre>
 * === AUCTION HOUSE ================================
 *   Moedas aceitas: Adena (57), Gold Bar (3470)
 *   Anúncios ativos: 3 (até 10 por personagem)
 * ==================================================
 * </pre>
 *
 * Junta as linhas e imprime tudo de uma vez, sem misturar com o log de outra thread no meio.
 */
public final class ServiceLog
{
	private static final Logger LOG = LoggerFactory.getLogger(ServiceLog.class);
	private static final int WIDTH = 50;
	private static final Object PRINT_LOCK = new Object();

	private final String title;
	private final List<String[]> lines = new ArrayList<>();

	private ServiceLog(String title)
	{
		this.title = title;
	}

	public static ServiceLog section(String title)
	{
		return new ServiceLog(title);
	}

	/** Linha normal. */
	public ServiceLog add(String text)
	{
		lines.add(new String[] { "I", text });
		return this;
	}

	/** Linha de aviso (sai como WARN, dentro do bloco). */
	public ServiceLog warn(String text)
	{
		lines.add(new String[] { "W", text });
		return this;
	}

	public void print()
	{
		StringBuilder head = new StringBuilder("=== ").append(title).append(' ');
		while (head.length() < WIDTH)
			head.append('=');
		String foot = "=".repeat(WIDTH);

		synchronized (PRINT_LOCK)
		{
			LOG.info(head.toString());
			for (String[] l : lines)
			{
				if ("W".equals(l[0]))
					LOG.warn("  " + l[1]);
				else
					LOG.info("  " + l[1]);
			}
			LOG.info(foot);
		}
	}

	/** "ligado" / "desligado". */
	public static String onOff(boolean on)
	{
		return on ? "ligado" : "desligado";
	}
}

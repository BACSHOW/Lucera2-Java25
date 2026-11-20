package services;

import java.util.ArrayList;
import java.util.List;

import l2.gameserver.ConfigReloadHooks;
import l2.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.utils.ServiceLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.mail.MailConfig;
import services.mail.MailService;
import services.mail.MailText;

/**
 * Correio: bypasses da janela MailWnd do cliente e os comandos.
 *
 *   .correio / .mail      abre a janela (o cliente recebe o pacote MAIL / OPEN)
 *   .correioreload        (GM) relê o mail.properties
 *   .correiotest [item quantidade enchant]
 *                         (GM) manda a si mesmo um correio do sistema
 *
 * Bypasses (scripts_services.MailBox:...), texto livre como token "~...":
 *   mailBox ABA PAGINA
 *   mailRead ABA ID
 *   mailReceive ID
 *   mailPay ID VALOR
 *   mailReturn ID
 *   mailCancel ID
 *   mailDelete ABA PAGINA ID [ID...]
 *   mailWrite 0
 *   mailSend TIPO COBRANCA ADENA N OBJ1 QTD1 ... ~PARA ~ASSUNTO ~TEXTO
 *   mailContactDel ~NOME
 */
public class MailBox extends Functions implements ScriptFile, IVoicedCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(MailBox.class);
	private static final String[] COMMANDS = { "correio", "mail", "correioreload", "correiotest" };

	@Override
	public void onLoad()
	{
		MailConfig.load();
		// recarrega com //reload_config (Reload All Configs) e com o "Salvar e recarregar" da janela
		ConfigReloadHooks.register("Mail", () -> { MailConfig.load(); logSection(); });
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		MailService.registerLoginNotice();
		logSection();
	}

	/** Bloco "=== MAIL ===" do console (boot e //reload_config). */
	private static void logSection()
	{
		ServiceLog.section("MAIL")
			.add("Correio: " + ServiceLog.onOff(MailConfig.ALLOW_MAIL) + " | nível mínimo " + MailConfig.MIN_LEVEL
				+ (MailConfig.PEACE_ZONE ? " | só em zona de paz" : ""))
			.add("Taxa: " + MailConfig.FEE_BASE + " Adena + " + MailConfig.FEE_PER_ITEM + " por anexo | até "
				+ MailConfig.MAX_ATTACHMENTS + " anexos | caixa até " + MailConfig.INBOX_LIMIT + " cartas")
			.add("Validade: " + MailConfig.EXPIRE_HOURS + " h (cobrança na entrega " + MailConfig.COD_EXPIRE_HOURS + " h, sistema "
				+ MailConfig.SYSTEM_EXPIRE_HOURS + " h) | entrega do leilão pelo correio: " + ServiceLog.onOff(MailConfig.AUCTION_DELIVERY))
			.print();
	}

	@Override
	public void onReload()
	{
		MailConfig.load();
	}

	@Override
	public void onShutdown()
	{
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
		if ("correio".equalsIgnoreCase(command) || "mail".equalsIgnoreCase(command))
		{
			MailService.ui(player, "OPEN", "");
			return true;
		}
		if (!player.isGM())
			return false;
		if ("correioreload".equalsIgnoreCase(command))
		{
			MailConfig.load();
			player.sendMessage("Correio: mail.properties relido.");
			return true;
		}
		if ("correiotest".equalsIgnoreCase(command))
		{
			String[] a = args == null ? new String[0] : args.trim().split("\\s+");
			long itemId = num(a, 0, 57L);
			long count = num(a, 1, 1000L);
			long enchant = num(a, 2, 0L);
			List<long[]> items = new ArrayList<>();
			if (itemId > 0 && count > 0)
				items.add(new long[] { itemId, count, enchant });
			boolean ok = MailService.sendSystemMail(player.getObjectId(), "Sistema", "Teste do correio",
				"Correio de teste.\nSe você está lendo isto, o correio funciona.", items, true);
			player.sendMessage(ok ? "Correio: teste enviado." : "Correio: o teste falhou (veja o log do servidor).");
			return true;
		}
		return false;
	}

	private static long num(String[] a, int i, long def)
	{
		if (a == null || i >= a.length)
			return def;
		try
		{
			return Long.parseLong(a[i].replace(",", "").trim());
		}
		catch (Exception e)
		{
			return def;
		}
	}

	private static int inum(String[] a, int i, int def)
	{
		long v = num(a, i, def);
		return v > Integer.MAX_VALUE || v < Integer.MIN_VALUE ? def : (int) v;
	}

	// ── bypasses ──────────────────────────────────────────────────────

	public void mailBox()
	{
		mailBox(new String[0]);
	}

	public void mailBox(String[] a)
	{
		Player p = getSelf();
		if (p == null)
			return;
		MailService.sendBox(p, inum(a, 0, 0), inum(a, 1, 1));
	}

	public void mailRead(String[] a)
	{
		Player p = getSelf();
		if (p == null)
			return;
		MailService.read(p, inum(a, 0, 0), inum(a, 1, 0));
	}

	public void mailReceive(String[] a)
	{
		Player p = getSelf();
		if (p == null)
			return;
		MailService.receive(p, inum(a, 0, 0), false, 0L);
	}

	public void mailPay(String[] a)
	{
		Player p = getSelf();
		if (p == null)
			return;
		MailService.receive(p, inum(a, 0, 0), true, num(a, 1, -1L));
	}

	public void mailReturn(String[] a)
	{
		Player p = getSelf();
		if (p == null)
			return;
		MailService.giveBack(p, inum(a, 0, 0));
	}

	public void mailCancel(String[] a)
	{
		Player p = getSelf();
		if (p == null)
			return;
		MailService.cancel(p, inum(a, 0, 0));
	}

	public void mailDelete(String[] a)
	{
		Player p = getSelf();
		if (p == null || a == null || a.length < 3)
			return;
		int n = Math.min(a.length - 2, 20);
		int[] ids = new int[n];
		for (int i = 0; i < n; i++)
			ids[i] = inum(a, i + 2, 0);
		MailService.delete(p, inum(a, 0, 0), inum(a, 1, 1), ids);
	}

	public void mailWrite()
	{
		mailWrite(new String[0]);
	}

	public void mailWrite(String[] a)
	{
		Player p = getSelf();
		if (p == null)
			return;
		MailService.writeOpen(p);
	}

	public void mailSend(String[] a)
	{
		Player p = getSelf();
		if (p == null || a == null)
			return;
		// TIPO COBRANCA ADENA N (OBJ QTD)xN ~PARA ~ASSUNTO ~TEXTO
		if (a.length < 7)
		{
			MailService.ui(p, "FAIL", "send|0|" + MailText.out("Envio incompleto. Tente de novo."));
			return;
		}
		int type = inum(a, 0, 0);
		long price = num(a, 1, 0L);
		long adena = num(a, 2, 0L);
		int n = inum(a, 3, 0);
		if (n < 0 || n > 8 || a.length != 4 + n * 2 + 3)
		{
			MailService.ui(p, "FAIL", "send|0|" + MailText.out("Envio incompleto. Tente de novo."));
			return;
		}
		int[] objIds = new int[n];
		long[] counts = new long[n];
		for (int i = 0; i < n; i++)
		{
			objIds[i] = inum(a, 4 + i * 2, 0);
			counts[i] = num(a, 5 + i * 2, 0L);
		}
		int t = 4 + n * 2;
		String to = MailText.decodeArg(a[t]);
		String topic = MailText.decodeArg(a[t + 1]);
		String body = MailText.decodeArg(a[t + 2]);
		MailService.send(p, type, price, adena, objIds, counts, to, topic, body);
	}

	public void mailContactDel(String[] a)
	{
		Player p = getSelf();
		if (p == null || a == null || a.length < 1)
			return;
		MailService.removeContact(p, MailText.decodeArg(a[0]));
	}
}

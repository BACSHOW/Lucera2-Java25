package services.mail;

import l2.commons.configuration.ExProperties;
import l2.gameserver.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuração do correio: game/config/mail.properties.
 *
 * Fica nos scripts (não no Config.java do núcleo): recarrega junto com os
 * scripts ou com o comando de GM ".correioreload". Arquivo ausente = todos os
 * valores padrão abaixo. O liga/desliga geral continua sendo o AllowMail do
 * server.properties (Config.ALLOW_MAIL).
 */
public final class MailConfig
{
	private static final Logger _log = LoggerFactory.getLogger(MailConfig.class);

	public static final String FILE = "config/mail.properties";

	/** Ativar Mail */
	public static boolean ALLOW_MAIL = true;

	/** Postagem: FEE_BASE + FEE_PER_ITEM por anexo (a Adena enviada conta como 1 anexo). */
	public static long FEE_BASE = 100L;
	public static long FEE_PER_ITEM = 1000L;

	/** Itens por correio (a Adena vai no campo próprio e não ocupa slot). */
	public static int MAX_ATTACHMENTS = 8;

	/** Tamanho do assunto e do texto (caracteres). */
	public static int MAX_TOPIC = 64;
	public static int MAX_BODY = 500;

	/** Correios recebidos guardados por personagem; acima disso o envio é recusado. */
	public static int INBOX_LIMIT = 240;

	/** Prazos em horas: correio normal, cobrança, devolvido e do sistema (leilão). */
	public static int EXPIRE_HOURS = 360;
	public static int COD_EXPIRE_HOURS = 12;
	public static int RETURNED_EXPIRE_HOURS = 360;
	public static int SYSTEM_EXPIRE_HOURS = 720;

	/** Segundos até o correio aparecer para o destinatário (0 = na hora). */
	public static int DELIVERY_DELAY = 30;

	/** true = todo o correio só em zona de paz. */
	public static boolean PEACE_ZONE = false;
	/** true = o que mexe com itens (anexos, Adena, cobrança, retirar, cancelar, devolver) só em zona de paz. */
	public static boolean PEACE_ZONE_ITEMS = true;

	/** Segundos entre dois envios do mesmo personagem. */
	public static int SEND_COOLDOWN = 5;

	/** Nível mínimo para enviar (GM não tem limite). 1 = sem limite. */
	public static int MIN_LEVEL = 1;

	/** Contatos guardados por personagem (o destinatário de cada envio entra sozinho). */
	public static int MAX_CONTACTS = 100;

	/** Valor máximo da cobrança e da Adena enviada (o cliente trabalha com int). */
	public static long MAX_AMOUNT = Integer.MAX_VALUE;

	/** Leilão entrega pelo correio (false = como antes: direto no inventário / DelayedItems). */
	public static boolean AUCTION_DELIVERY = false;
	public static String AUCTION_SENDER = "Casa de Leilões";

	/** Linhas por página na caixa (as 8 da janela). */
	public static int PAGE_SIZE = 8;

	private MailConfig()
	{
	}

	public static void load()
	{
		try
		{
			ExProperties p = Config.load(FILE);
			ALLOW_MAIL = p.getProperty("AllowMail", true);
			FEE_BASE = Math.max(0L, p.getProperty("MailFeeBase", 100L));
			FEE_PER_ITEM = Math.max(0L, p.getProperty("MailFeePerItem", 1000L));
			MAX_ATTACHMENTS = clamp(p.getProperty("MailMaxAttachments", 8), 1, 8);
			MAX_TOPIC = clamp(p.getProperty("MailMaxTopic", 64), 8, 80);   // coluna tinytext (255 bytes)
			MAX_BODY = clamp(p.getProperty("MailMaxBody", 500), 0, 1000);
			INBOX_LIMIT = Math.max(1, p.getProperty("MailInboxLimit", 240));
			EXPIRE_HOURS = Math.max(1, p.getProperty("MailExpireHours", 360));
			COD_EXPIRE_HOURS = Math.max(1, p.getProperty("MailCodExpireHours", 12));
			RETURNED_EXPIRE_HOURS = Math.max(1, p.getProperty("MailReturnedExpireHours", 360));
			SYSTEM_EXPIRE_HOURS = Math.max(1, p.getProperty("MailSystemExpireHours", 720));
			DELIVERY_DELAY = Math.max(0, p.getProperty("MailDeliveryDelay", 30));
			PEACE_ZONE = p.getProperty("MailPeaceZone", false);
			PEACE_ZONE_ITEMS = p.getProperty("MailPeaceZoneItems", true);
			SEND_COOLDOWN = Math.max(0, p.getProperty("MailSendCooldown", 5));
			MIN_LEVEL = Math.max(1, p.getProperty("MailMinLevel", 1));
			MAX_CONTACTS = clamp(p.getProperty("MailMaxContacts", 100), 0, 500);
			AUCTION_DELIVERY = p.getProperty("MailAuctionDelivery", false);
			AUCTION_SENDER = p.getProperty("MailAuctionSender", "Casa de Leilões").trim();
			if (AUCTION_SENDER.isEmpty())
				AUCTION_SENDER = "Casa de Leilões";
		}
		catch (Exception e)
		{
			_log.warn("Mail: erro lendo " + FILE + " (ficam os valores padrão): " + e.getMessage());
		}
	}

	private static int clamp(int v, int lo, int hi)
	{
		return v < lo ? lo : (v > hi ? hi : v);
	}
}

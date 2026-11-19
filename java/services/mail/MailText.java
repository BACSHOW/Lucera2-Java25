package services.mail;

/**
 * Texto livre do correio (nome, assunto, corpo) no protocolo.
 *
 * Cliente -> servidor (bypass): os argumentos são separados por espaço, então
 * cada texto vai como um token só, começando com "~" (para o vazio também ser
 * um token): espaço vira "+", e "%", "+", "|" e a quebra de linha viram
 * %25, %2B, %7C e %0A. Qualquer %XX é aceito na volta.
 *
 * Servidor -> cliente (pacote InterfaceData): os campos são separados por "|", então
 * "%", "|", "," e a quebra de linha viram %25, %7C, %2C e %0A.
 */
public final class MailText
{
	private MailText()
	{
	}

	/** Token do bypass ("~Oi+Ragnar%2C+tudo%7Cbem") -> texto. Sem o "~" = vazio. */
	public static String decodeArg(String token)
	{
		if (token == null || token.isEmpty() || token.charAt(0) != '~')
			return "";
		String s = token.substring(1);
		StringBuilder sb = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c == '+')
			{
				sb.append(' ');
				continue;
			}
			if (c == '%' && i + 2 < s.length())
			{
				int hi = hex(s.charAt(i + 1));
				int lo = hex(s.charAt(i + 2));
				if (hi >= 0 && lo >= 0)
				{
					sb.append((char) (hi * 16 + lo));
					i += 2;
					continue;
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	private static int hex(char c)
	{
		if (c >= '0' && c <= '9') return c - '0';
		if (c >= 'a' && c <= 'f') return c - 'a' + 10;
		if (c >= 'A' && c <= 'F') return c - 'A' + 10;
		return -1;
	}

	/** Campo do protocolo (servidor -> cliente). */
	public static String out(String s)
	{
		if (s == null || s.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder(s.length() + 8);
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			switch (c)
			{
				case '%': sb.append("%25"); break;
				case '|': sb.append("%7C"); break;
				case ',': sb.append("%2C"); break;
				case '\n': sb.append("%0A"); break;
				case '\r': break;
				default: sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Uma linha (nome, assunto): sem quebras nem caracteres de controle,
	 * espaços repetidos viram um, sem espaço nas pontas, cortada em max.
	 */
	public static String line(String s, int max)
	{
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder(s.length());
		boolean space = false;
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c < 32 || c == 127)
				c = ' ';
			if (c == ' ')
			{
				if (space || sb.length() == 0)
					continue;
				space = true;
			}
			else
				space = false;
			sb.append(c);
		}
		String r = sb.toString().trim();
		return r.length() > max ? r.substring(0, max).trim() : r;
	}

	/**
	 * Corpo: mantém as quebras de linha (no máximo 2 seguidas), tira os outros
	 * caracteres de controle e os espaços do fim, corta em max.
	 */
	public static String body(String s, int max)
	{
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder(s.length());
		int nl = 0;
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c == '\r')
				continue;
			if (c == '\n')
			{
				if (++nl > 2)
					continue;
				sb.append('\n');
				continue;
			}
			if (c < 32 || c == 127)
				c = ' ';
			nl = 0;
			sb.append(c);
		}
		String r = sb.toString();
		int end = r.length();
		while (end > 0 && Character.isWhitespace(r.charAt(end - 1)))
			end--;
		int start = 0;
		while (start < end && r.charAt(start) == '\n')
			start++;
		r = r.substring(start, end);
		return r.length() > max ? r.substring(0, max) : r;
	}

	/** Nome de personagem válido para procurar: letras e números, 1 a 35. */
	public static boolean validName(String s)
	{
		if (s == null || s.isEmpty() || s.length() > 35)
			return false;
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != '_' && c != '-')
				return false;
		}
		return true;
	}
}

package l2.gameserver.network.l2.s2c;

import l2.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dados para uma janela custom da interface, sem passar pelo chat.
 *
 * Dois transportes (InterfaceDataTransport no server.properties):
 *
 *   tutorial (padrao) = TutorialShowHtml (0xA0), o mesmo caminho do AutoHunting e do Monster Data.
 *       Texto: "IFD|canal|tipo|dados". O TutorialViewerWnd ve o prefixo "IFD|", nao abre a janela
 *       de tutorial e repassa (tipo, dados) para a janela do canal: ReceiveInterfaceData(tipo, dados).
 *
 *   board = formato do ShowBoard (0x6E): o cliente dispara EV_ShowBoardPacket com
 *       Command1 = canal, Command2 = tipo, Address = dados (o jeito antigo).
 *
 * As janelas aceitam os dois, entao trocar o transporte nao exige mudar o .uc delas.
 * Campos dos dados separados por "|".
 */
public class InterfaceData extends L2GameServerPacket
{
	private static final Logger _log = LoggerFactory.getLogger(InterfaceData.class);

	/** O mesmo teto do ShowBoard (acima disso o cliente pode fechar). */
	public static final int MAX_DATA = 8000;
	/** Prefixo que o TutorialViewerWnd reconhece. */
	public static final String TUTORIAL_PREFIX = "IFD|";

	private final String _channel;
	private final String _type;
	private final String _data;

	public InterfaceData(String channel, String type, String data)
	{
		_channel = channel == null ? "" : channel;
		_type = type == null ? "" : type;
		String d = data == null ? "" : data;
		if (d.length() > MAX_DATA)
		{
			_log.warn("InterfaceData " + _channel + ":" + _type + " com " + d.length() + " caracteres; cortado em " + MAX_DATA + ".");
			d = d.substring(0, MAX_DATA);
		}
		_data = d;
	}

	public String getChannel()
	{
		return _channel;
	}

	public String getDataType()
	{
		return _type;
	}

	public String getData()
	{
		return _data;
	}

	@Override
	protected final void writeImpl()
	{
		if (Config.INTERFACE_DATA_TUTORIAL)
		{
			writeC(0xA0);   // TutorialShowHtml
			writeS(TUTORIAL_PREFIX + _channel + "|" + _type + "|" + _data);
			return;
		}
		writeC(0x6E);       // ShowBoard
		writeC(1);          // OK
		writeS(_channel);   // Command1
		writeS(_type);      // Command2
		for (int i = 3; i <= 8; i++)
			writeS("");     // Command3 ... Command8
		writeS(_data);      // Address
	}

	@Override
	public String toString()
	{
		return "InterfaceData[" + _channel + ":" + _type + "]";
	}
}

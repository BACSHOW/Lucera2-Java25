package services.battlepass;

/**
 * Progresso de um personagem na temporada. Tudo que muda passa por
 * synchronized(this).
 */
public final class BattlePassData
{
	public final int objId;
	/** Chave da temporada (dia em que ela começou, contado desde 1970). */
	public int season;
	public int points;
	public boolean premium;
	/** Bit (degrau - 1) ligado = recompensa já retirada. */
	public long freeClaimed;
	public long premClaimed;

	/** Cada mudança soma 1; a gravação guarda até qual versão já foi. */
	int version;
	int savedVersion;

	public BattlePassData(int objId)
	{
		this.objId = objId;
	}

	void changed()
	{
		version++;
	}

	public boolean freeTaken(int step)
	{
		return (freeClaimed & (1L << (step - 1))) != 0;
	}

	public boolean premTaken(int step)
	{
		return (premClaimed & (1L << (step - 1))) != 0;
	}
}

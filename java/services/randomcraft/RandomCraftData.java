package services.randomcraft;

/** Estado do Random Craft de um personagem. Tudo que muda passa por synchronized(this). */
public final class RandomCraftData
{
	/** Uma casa da janela. id 0 = vazia. */
	public static final class Slot
	{
		public int id;
		public long count;
		public int grade;
		public int enchant;
		public boolean locked;
		/** Quantas vezes esta casa foi travada desde a última criação. */
		public int lockCount;

		void clear()
		{
			id = 0;
			count = 0;
			grade = 0;
			enchant = 0;
			locked = false;
			lockCount = 0;
		}
	}

	public final int objId;
	/** true só para personagem novo (sem linha no banco): ganha a primeira lista de graça */
	public boolean fresh;
	public int points;
	public long exp;
	public final Slot[] slots = new Slot[RandomCraftConfig.SLOTS];

	public RandomCraftData(int objId)
	{
		this.objId = objId;
		for (int i = 0; i < slots.length; i++)
			slots[i] = new Slot();
	}

	public boolean isEmpty()
	{
		for (Slot s : slots)
			if (s.id > 0)
				return false;
		return true;
	}

	public void clearSlots()
	{
		for (Slot s : slots)
			s.clear();
	}

	/** "id:count:grade:ench:locked:lockCount;..." */
	public String encodeSlots()
	{
		StringBuilder b = new StringBuilder();
		for (Slot s : slots)
		{
			if (b.length() > 0)
				b.append(';');
			b.append(s.id).append(':').append(s.count).append(':').append(s.grade).append(':').append(s.enchant).append(':').append(s.locked ? 1 : 0).append(':').append(s.lockCount);
		}
		return b.toString();
	}

	public void decodeSlots(String v)
	{
		clearSlots();
		if (v == null || v.isEmpty())
			return;
		String[] a = v.split(";");
		for (int i = 0; i < a.length && i < slots.length; i++)
		{
			try
			{
				String[] f = a[i].split(":");
				Slot s = slots[i];
				s.id = Integer.parseInt(f[0]);
				s.count = Long.parseLong(f[1]);
				s.grade = Integer.parseInt(f[2]);
				s.enchant = Integer.parseInt(f[3]);
				s.locked = "1".equals(f[4]);
				s.lockCount = Integer.parseInt(f[5]);
			}
			catch (Exception e)
			{
				slots[i].clear();
			}
		}
	}
}

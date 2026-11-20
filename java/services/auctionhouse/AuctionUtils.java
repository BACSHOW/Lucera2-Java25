package services.auctionhouse;

import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.templates.item.ItemTemplate;

/** Nomes de item/moeda e a regra de quais itens podem ir a leilão. */
public final class AuctionUtils
{
	private AuctionUtils()
	{
	}

	/** Nome da moeda pelo id ("Item 57" se o id não existir). */
	public static String getCurrencyNameById(int currencyId)
	{
		ItemTemplate t = ItemHolder.getInstance().getTemplate(currencyId);
		return t != null ? t.getName() : "Item " + currencyId;
	}

	/** "Draconic Bow - Focus +12" (nome adicional e enchant só quando existem). */
	public static String formatItemName(int itemId, int enchant)
	{
		ItemTemplate t = itemId > 0 ? ItemHolder.getInstance().getTemplate(itemId) : null;
		if (t == null)
			return "Item Desconhecido";

		StringBuilder sb = new StringBuilder(t.getName() != null ? t.getName() : "Item Desconhecido");
		String add = t.getAdditionalName();
		if (add != null && !add.trim().isEmpty())
			sb.append(" - ").append(add.trim());
		if (enchant > 0)
			sb.append(" +").append(enchant);
		return sb.toString();
	}

	/** Item pode ir a leilão: arma/armadura/joia só nos grades configurados, e sempre tradeable. */
	public static boolean checkItem(ItemInstance item)
	{
		if (item == null)
			return false;

		if (Config.ENABLE_GRADE_ITEMS_MARKETPLACE && isGradeItem(item)
				&& !Config.GRADE_ITEMS_MARKETPLACE.contains(item.getCrystalType())
				&& !Config.ITEMS_NO_RULES_MARKETPLACE.contains(item.getItemId()))
			return false;

		return item.getTemplate().isTradeable();
	}

	private static boolean isGradeItem(ItemInstance item)
	{
		ItemTemplate template = item.getTemplate();
		if (template.isWeapon() || template.isArmor())
			return true;

		switch (template.getBodyPart())
		{
			case ItemTemplate.SLOT_R_EAR:
			case ItemTemplate.SLOT_L_EAR:
			case ItemTemplate.SLOT_NECK:
			case ItemTemplate.SLOT_R_FINGER:
			case ItemTemplate.SLOT_L_FINGER:
				return true;
			default:
				return false;
		}
	}
}

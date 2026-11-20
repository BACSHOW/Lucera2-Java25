package services.auctionhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.templates.item.ItemTemplate;
import l2.gameserver.templates.item.ItemTemplate.Grade;

public class AuctionUtils
{
	private static final Logger log = LoggerFactory.getLogger(AuctionUtils.class);
	private static final int MAX_NAME_LENGTH = 25;

	/**
	 * Obtém nome da moeda pelo ID
	 * @param currencyId
	 * @return
	 */
	public static String getCurrencyNameById(int currencyId)
	{
		ItemTemplate currencyItem = ItemHolder.getInstance().getTemplate(currencyId);
		if (currencyItem != null)
		{
			return currencyItem.getName();
		}
		return "Item " + currencyId;
	}

	public static String getFullName(ItemTemplate item)
	{
		if (item == null)
			return "";

		String name = item.getName();
		String additionalName = item.getAdditionalName();

		if (name == null)
			name = "";

		if (additionalName == null)
			additionalName = "";

		name = name.trim();
		additionalName = additionalName.trim();

		/*
		 * Trunca somente o nome principal.
		 * O additionalName permanece sempre completo.
		 */
		if (name.length() > MAX_NAME_LENGTH)
		{
			name = name.substring(0, MAX_NAME_LENGTH - 3) + "...";
		}

		StringBuilder result = new StringBuilder();

		/*
		 * Nome principal.
		 */
		result.append(name);

		/*
		 * Nome adicional.
		 */
		if (!additionalName.isEmpty())
		{
			result.append(" - ")
					.append(additionalName);
		}

		return result.toString();
	}

	public static String getEnchantColor(int enchant)
	{
		if (enchant >= 20)
			return "F28034"; // Laranja

		if (enchant >= 16)
			return "CC66FF"; // Roxo

		if (enchant >= 13)
			return "3399FF"; // Azul

		if (enchant >= 10)
			return "00FF00"; // Verde

		return "3399FF"; // Azul normal
	}

	// ===== GET GRADE ICON =====
	public static String getGradeIcon(int itemId)
	{
		Grade grade = ItemHolder.getInstance().getTemplate(itemId).getCrystalType();

		switch (grade)
		{
			default:
			case NONE:
				return "";
			case D:
				return "<img src=symbol.Icon.grade_d width=16 height=16>";
			case C:
				return "<img src=symbol.Icon.grade_c width=16 height=16>";
			case B:
				return "<img src=symbol.Icon.grade_b width=16 height=16>";
			case A:
				return "<img src=symbol.Icon.grade_a width=16 height=16>";
			case S:
				return "<img src=symbol.Icon.grade_s width=16 height=16>";
		}
	}

	/* ✅ FUNÇÃO PARA TRUNCAR NOMES LONGOS */
	public static String truncateName(String name, int maxLength)
	{
		if (name == null || name.length() <= maxLength)
			return name;

		/* Remove o +X do final se existir */
		String enchant = "";
		if (name.contains(" +"))
		{
			int enchantIndex = name.lastIndexOf(" +");
			enchant = name.substring(enchantIndex);
			name = name.substring(0, enchantIndex);
		}

		/* Trunca o nome */
		if (name.length() > maxLength - 3)
		{
			name = name.substring(0, maxLength - 3) + "...";
		}

		/* Adiciona o enchant de volta */
		return name + enchant;
	}

	// ✅ MÉTODO AUXILIAR: FORMATAR NOME DO ITEM COM ENCHANT
	public static String formatItemName(int itemId, int enchant)
	{
		if (itemId <= 0)
		{
			log.warn("Item ID inválido: " + itemId);
			return "Item Desconhecido";
		}

		try
		{
			String itemName = ItemHolder.getInstance().getTemplate(itemId).getName();
			String AdditionalName = ItemHolder.getInstance().getTemplate(itemId).getAdditionalName();

			if (itemName == null)
			{
				itemName = "Item Desconhecido";
			}

			String itemnamefull = itemName + "-" + AdditionalName;

			if (enchant > 0)
			{
				return itemnamefull + " +" + enchant;
			}

			return itemnamefull;
		}
		catch (Exception e)
		{
			log.warn("Erro ao formatar item: " + e.getMessage());
			return "Item Desconhecido";
		}
	}

	/**
	 * Obtém ícone da moeda pelo ID
	 * @param currencyId
	 * @return
	 */
	public static String getCurrencyIcon(int currencyId)
	{
		ItemTemplate currencyItem = ItemHolder.getInstance().getTemplate(currencyId);
		String icon = ItemHolder.getInstance().getTemplate(currencyItem.getItemId()).getIcon();
		if (currencyItem != null)
		{
			return "<img src=\"" + icon + "\" width=16 height=16>";
		}
		else
		{
			return "<img src=icon.adena width=16 height=16>";
		}
	}

	// ============================================================
	// Regras de item para leilao (antes em MarketplaceCBBypasses.checkItem)
	// ============================================================

	/** Item pode ir a leilao: arma/armadura/joia so nos grades configurados, e sempre tradeable. */
	public static boolean checkItem(l2.gameserver.model.items.ItemInstance item)
	{
		if (item == null)
			return false;

		if (l2.gameserver.Config.ENABLE_GRADE_ITEMS_MARKETPLACE && isGradeItem(item)
				&& !l2.gameserver.Config.GRADE_ITEMS_MARKETPLACE.contains(item.getCrystalType())
				&& !l2.gameserver.Config.ITEMS_NO_RULES_MARKETPLACE.contains(item.getItemId()))
			return false;

		return item.getTemplate().isTradeable();
	}

	private static boolean isGradeItem(l2.gameserver.model.items.ItemInstance item)
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
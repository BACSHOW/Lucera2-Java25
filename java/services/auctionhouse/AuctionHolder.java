package services.auctionhouse;

import java.util.Date;

/** Um anúncio do leilão. Mudanças de estado acontecem dentro de synchronized (this). */
public class AuctionHolder
{
	private int auctionId;
	private int sellerId;
	private int itemId;
	private int count;
	private int enchant;
	private int costId;
	private int costCount;
	
	// Campos do sistema de leilão
	private long startingPrice;
	private long currentBid;
	private long buyNowPrice;
	private int bidderId;
	private Date startTime;
	private volatile Date endTime;
	private volatile AuctionStatus status;

	// Venda parcial (etapa 17). O modo do anúncio não tem coluna própria:
	//   preço fixo = compra imediata igual ao preço inicial (isFixedPrice);
	//   leilão     = o resto (lances; o lote vai sempre inteiro).
	// Em preço fixo, preço inicial = compra imediata = preço da unidade x o que
	// resta, então o preço da unidade é sempre a divisão exata (getUnitPrice).
	private boolean allowPartial;   // só preço fixo + empilhável + mais de 1 unidade
	private int originalCount;      // quantidade anunciada; count = o que resta

	public enum AuctionStatus
	{
	    ACTIVE,
	    PENDING,
	    ENDED,
	    CANCELLED
	}
	
	public AuctionHolder(int auctionId, int sellerId, int itemId, int count, int enchant, 
		int costId, int costCount, long startingPrice, long buyNowPrice,
		Date startTime, Date endTime)
	{
		this.auctionId = auctionId;
		this.sellerId = sellerId;
		this.itemId = itemId;
		this.count = count;
		this.enchant = enchant;
		this.costId = costId;
		this.costCount = costCount;
		this.startingPrice = startingPrice;
		this.currentBid = 0;
		this.buyNowPrice = buyNowPrice;
		this.bidderId = 0;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = AuctionStatus.ACTIVE;
		this.originalCount = count;
	}
	
	// Getters e Setters
	public int getAuctionId() { return auctionId; }
	public int getSellerId() { return sellerId; }
	public int getOwnerId() { return sellerId; }
	public int getItemId() { return itemId; }
	public int getCount() { return count; }
	public int getEnchant() { return enchant; }
	public int getCostId() { return costId; }
	public int getCostCount() { return costCount; }
	
	public long getStartingPrice() { return startingPrice; }
	public void setStartingPrice(long startingPrice) { this.startingPrice = startingPrice; }
	
	public long getCurrentBid() { return currentBid; }
	public void setCurrentBid(long currentBid) { this.currentBid = currentBid; }
	
	public long getBuyNowPrice() { return buyNowPrice; }
	public void setBuyNowPrice(long buyNowPrice) { this.buyNowPrice = buyNowPrice; }
	
	public int getBidderId() { return bidderId; }
	public void setBidderId(int bidderId) { this.bidderId = bidderId; }
	
	public Date getStartTime() { return startTime; }
	
	public Date getEndTime() { return endTime; }
	public void setEndTime(Date endTime) { this.endTime = endTime; }
	
	public AuctionStatus getStatus() { return status; }
	public void setStatus(AuctionStatus status) { this.status = status; }
	
	// ===== MÉTODOS UTILITÁRIOS =====
	public boolean isActive() { return status == AuctionStatus.ACTIVE; }
	
	public boolean isEnded() { return status == AuctionStatus.ENDED; }
	public boolean isCancelled() { return status == AuctionStatus.CANCELLED; }
	
	public long getTimeRemaining()
	{
		if (endTime == null) return 0;
		long remaining = endTime.getTime() - System.currentTimeMillis();
		return Math.max(0, remaining);
	}
	
	public String getTimeRemainingFormatted()
	{
		long millis = getTimeRemaining();
		if (millis <= 0) 
			return "Encerrado";
		
		long seconds = millis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		
		if (days > 0) return days + "d " + (hours % 24) + "h";
		if (hours > 0) return hours + "h " + (minutes % 60) + "m";
		return minutes + "m " + (seconds % 60) + "s";
	}
	
	public boolean hasBuyNow() { return buyNowPrice > 0; }

	// ===== VENDA PARCIAL =====

	/** Quantidade que ainda está à venda (compra parcial diminui). */
	public void setCount(int count) { this.count = count; }

	public boolean isAllowPartial() { return allowPartial; }
	public void setAllowPartial(boolean allowPartial) { this.allowPartial = allowPartial; }

	/** Quantidade anunciada no começo (0 no banco antigo = a quantidade atual). */
	public int getOriginalCount() { return Math.max(originalCount, count); }
	public void setOriginalCount(int originalCount) { this.originalCount = originalCount; }

	/** Unidades já vendidas por compra parcial. */
	public int getSoldCount() { return Math.max(0, getOriginalCount() - count); }

	/** Preço fixo: sem lances, quem paga leva (compra imediata = preço inicial). */
	public boolean isFixedPrice() { return buyNowPrice > 0 && buyNowPrice == startingPrice; }

	/** Aceita comprar só uma parte agora (preço fixo, marcado na criação, resta mais de 0). */
	public boolean isPartial() { return allowPartial && isFixedPrice() && count > 0; }

	/**
	 * Preço de 1 unidade. Na venda parcial é a divisão exata (o total é sempre
	 * unidade x quantidade); nos outros é só para mostrar, arredondado.
	 */
	public long getUnitPrice()
	{
		long total = currentBid > 0 ? currentBid : startingPrice;
		if (count <= 1)
			return total;
		if (allowPartial && isFixedPrice())
			return total / count;
		return (total + count / 2) / count;
	}
}

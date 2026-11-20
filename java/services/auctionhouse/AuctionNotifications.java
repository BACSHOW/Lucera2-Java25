package services.auctionhouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;

public class AuctionNotifications
{
	private static final AuctionNotifications INSTANCE = new AuctionNotifications();

	private static final ConcurrentHashMap<Integer, Integer> outbidNotifications = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(AuctionNotifications.class);

    public static AuctionNotifications getInstance()
    {
        return INSTANCE;
    }
    
    // ===== NOTIFICAR LANCE SUPERADO =====
    public static void notifyOutbid(Player player, AuctionHolder auction)
    {
        // ✅ VERIFICAÇÃO DE NULL - CRÍTICO!
        if (player == null || !player.isOnline())
        {
            log.warn("Player é NULL ou offline em notifyOutbid()");
            return;
        }
        
        if (auction == null)
        {
            log.warn("AuctionHolder é NULL em notifyOutbid()");
            return;
        }
        
        // ✅ VERIFICAR PROPRIEDADES DO AUCTION
        if (auction.getItemId() <= 0)
        {
            log.warn("Item ID inválido em auction: " + auction.getAuctionId());
            return;
        }
        
        if (auction.getCostId() <= 0)
        {
            log.warn("Cost ID inválido em auction: " + auction.getAuctionId());
            return;
        }
        
        // ✅ OBTER INFORMAÇÕES COM VERIFICAÇÃO DE NULL
        String fullItemName = formatItemName(auction.getItemId(), auction.getEnchant());
        String currencyName = AuctionUtils.getCurrencyNameById(auction.getCostId());
        long currentBid = auction.getCurrentBid();
        long previousBid = getPreviousBid(auction);
        
        // ✅ VERIFICAR SE NOME DA MOEDA NÃO É NULL
        if (currencyName == null || currencyName.isEmpty())
        {
            currencyName = "Adena"; // Fallback
        }
        
        // ✅ MENSAGEM DETALHADA
        player.sendMessage("Seu lance foi superado!");
        player.sendMessage("Item: " + fullItemName);
        player.sendMessage("Lance atual: " + String.valueOf(currentBid) + " " + currencyName);
        player.sendMessage("Seu lance anterior: " + String.valueOf(previousBid) + " " + currencyName);
        
        // ✅ ExShowScreenMessage
        try
        {
            player.sendPacket(new ExShowScreenMessage(
                "LANCE SUPERADO!\n\n" +
                "Item: " + fullItemName + "\n" +
                "Lance atual: " + String.valueOf(currentBid) + " " + currencyName, 
                8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false));
        }
        catch (Exception e)
        {
            log.warn("Erro ao enviar ExShowScreenMessage: " + e.getMessage());
        }
        
        // ✅ SALVAR NOTIFICAÇÃO
        outbidNotifications.put(player.getObjectId(), auction.getAuctionId());
    }
    
    public static void notifyWonAuction(Player player, AuctionHolder auction)
    {
    	if (player == null || !player.isOnline())
    		return;

    	if (auction == null)
    	{
    		log.warn("AuctionHolder NULL em notifyWonAuction()");
    		return;
    	}

    	String fullItemName = formatItemName(
    			auction.getItemId(),
    			auction.getEnchant()
    			);

    	String currencyName =
    			AuctionUtils.getCurrencyNameById(auction.getCostId());

    	if (currencyName == null || currencyName.isEmpty())
    		currencyName = "Adena";

    	long finalPrice = auction.getCurrentBid();

    	player.sendMessage("Parabéns! Você venceu o leilão!");
    	player.sendMessage("Item: " + fullItemName);
    	player.sendMessage(
    			"Preço final: " + finalPrice + " " + currencyName
    			);

    	try
    	{
    		player.sendPacket(new ExShowScreenMessage(
    				"VITÓRIA!\n\n" +
    						"Você adquiriu: " + fullItemName + "\n" +
    						"Preço: " + finalPrice + " " + currencyName,
    						10000,
    						ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER,
    						true
    				));
    	}
    	catch (Exception e)
    	{
    		log.warn(
    				"Erro ao enviar notificação de vitória: "
    						+ e.getMessage()
    				);
    	}

    	// Limpar notificação pendente
    	if (outbidNotifications != null)
    	{
    		outbidNotifications.remove(player.getObjectId());
    	}
    }
    
    // ✅ NOTIFICAÇÃO DE LEILÃO TERMINANDO EM BREVE
    public void notifyAuctionEndingSoon(Player player, AuctionHolder auction)
    {
        if (player == null || !player.isOnline())
            return;
        
        if (auction.getBidderId() != player.getObjectId())
            return;
        
        String fullItemName = formatItemName(auction.getItemId(), auction.getEnchant());
        String currencyName = AuctionUtils.getCurrencyNameById(auction.getCostId());
        long currentBid = auction.getCurrentBid();
        String timeRemaining = auction.getTimeRemainingFormatted();
        
        player.sendMessage("⚠️ LEILAO TERMINANDO EM BREVE!");
        player.sendMessage("Item: " + fullItemName);
        player.sendMessage("Seu lance: " + String.valueOf(currentBid) + " " + currencyName);
        player.sendMessage("Tempo restante: " + timeRemaining);
    }
    
    // ✅ NOTIFICAÇÃO DE LEILÃO CRIADO
    public static void notifyAuctionCreated(Player player, AuctionHolder auction)
    {
        if (player == null || !player.isOnline())
            return;
        
        String fullItemName = formatItemName(auction.getItemId(), auction.getEnchant());
        String currencyName = AuctionUtils.getCurrencyNameById(auction.getCostId());
        long startingPrice = auction.getStartingPrice();
        long buyNowPrice = auction.getBuyNowPrice();
        String timeRemaining = auction.getTimeRemainingFormatted();
        
        player.sendMessage("✅ LEILAO CRIADO COM SUCESSO!");
        player.sendMessage("Item: " + fullItemName);
        player.sendMessage("Lance inicial: " + String.valueOf(startingPrice) + " " + currencyName);
        if (buyNowPrice > 0)
            player.sendMessage("Compra imediata: " + String.valueOf(buyNowPrice) + " " + currencyName);
        player.sendMessage("Tempo restante: " + timeRemaining);
        
        player.sendPacket(new ExShowScreenMessage(
            "LEILAO CRIADO COM SUCESSO!\n\n" +
            "Item: " + fullItemName + "\n" +
            "Termina em: " + timeRemaining, 
            5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
    }
    
    // ✅ NOTIFICAÇÃO DE LEILÃO CANCELADO (UNIFICADO COM ENUM)
    public static void notifyAuctionCancelled(Player player, AuctionHolder auction, CancelType type)
    {
        if (player == null || !player.isOnline())
            return;
        
        String fullItemName = formatItemName(auction.getItemId(), auction.getEnchant());
        String currencyName = AuctionUtils.getCurrencyNameById(auction.getCostId());
        long currentBid = auction.getCurrentBid();
        
        switch (type)
        {
            case SELLER:
                // ✅ NOTIFICAÇÃO PARA O VENDEDOR
                player.sendMessage("❌ LEILAO CANCELADO!");
                player.sendMessage("Item: " + fullItemName);
                player.sendMessage("O item foi devolvido " + AuctionDelivery.where() + ".");
                
                if (currentBid > 0)
                {
                    player.sendMessage("⚠️ Lance do licitante foi devolvido: " + 
                        String.valueOf(currentBid) + " " + currencyName);
                }
                
                player.sendPacket(new ExShowScreenMessage(
                    "LEILAO CANCELADO!\n\n" +
                    "Item: " + fullItemName + "\n" +
                    "Item devolvido " + AuctionDelivery.where(), 
                    5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                break;
                
            case BIDDER:
                // ✅ NOTIFICAÇÃO PARA O LICITANTE
                player.sendMessage("⚠️ LEILAO CANCELADO!");
                player.sendMessage("Item: " + fullItemName);
                player.sendMessage("✅ Seu lance foi devolvido: " + 
                    String.valueOf(currentBid) + " " + currencyName);
                player.sendMessage("O item foi devolvido ao vendedor.");
                
                player.sendPacket(new ExShowScreenMessage(
                    "LEILAO CANCELADO!\n\n" +
                    "Item: " + fullItemName + "\n" +
                    "Seu lance foi devolvido: " + String.valueOf(currentBid) + " " + currencyName, 
                    5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                break;
        }
    }
    
    // ✅ NOTIFICAÇÃO DE VENDEDOR RECEBEU PAGAMENTO
    public static void notifySellerPaid(Player player, AuctionHolder auction)
    {
        if (player == null || !player.isOnline())
            return;
        
        String fullItemName = formatItemName(auction.getItemId(), auction.getEnchant());
        String currencyName = AuctionUtils.getCurrencyNameById(auction.getCostId());
        long finalPrice = auction.getCurrentBid();
        // mesma conta do pagamento (AuctionHouseService.taxFor, mesma pasta)
        long tax = AuctionHouseService.taxFor(finalPrice);
        long sellerAmount = finalPrice - tax;

        player.sendMessage("💰 SEU ITEM FOI VENDIDO!");
        player.sendMessage("Item: " + fullItemName);
        player.sendMessage("Preco final: " + AuctionHouseService.fmt(finalPrice) + " " + currencyName);
        player.sendMessage("Taxa: " + AuctionHouseService.fmt(tax) + " " + currencyName);
        player.sendMessage("Valor liquido: " + AuctionHouseService.fmt(sellerAmount) + " " + currencyName);

        player.sendPacket(new ExShowScreenMessage(
            "ITEM VENDIDO!\n\n" +
            "Item: " + fullItemName + "\n" +
            "Preco final: " + AuctionHouseService.fmt(finalPrice) + " " + currencyName,
            5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
    }
    
    // ✅ NOTIFICAÇÃO DE ITEM DEVOLVIDO (SEM LANCE)
    public static void notifyItemReturned(Player player, AuctionHolder auction)
    {
        if (player == null || !player.isOnline())
            return;
        
        String fullItemName = formatItemName(auction.getItemId(), auction.getEnchant());
        
        player.sendMessage("⚠️ LEILAO SEM LANCE!");
        player.sendMessage("Item: " + fullItemName);
        player.sendMessage("O item foi devolvido " + AuctionDelivery.where() + ".");
        
        player.sendPacket(new ExShowScreenMessage(
            "ITEM DEVOLVIDO!\n\n" +
            "Item: " + fullItemName + "\n", 
            5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
    }
    
    // ✅ ENUM PARA TIPO DE NOTIFICAÇÃO DE CANCELAMENTO
    public enum CancelType
    {
        SELLER,  // Vendedor cancela
        BIDDER   // Licitante recebe reembolso
    }
    
    // ✅ MÉTODO AUXILIAR: FORMATAR NOME DO ITEM COM ENCHANT
    private static String formatItemName(int itemId, int enchant)
    {
        if (itemId <= 0)
        {
            log.warn("Item ID inválido: " + itemId);
            return "Item Desconhecido";
        }
        
        try
        {
            String itemName = ItemHolder.getInstance().getTemplate(itemId).getName();
            
            if (itemName == null)
            {
                itemName = "Item Desconhecido";
            }
            
            if (enchant > 0)
            {
                return itemName + " +" + enchant;
            }
            
            return itemName;
        }
        catch (Exception e)
        {
            log.warn("Erro ao formatar item: " + e.getMessage());
            return "Item Desconhecido";
        }
    }
    
    // ✅ MÉTODO AUXILIAR: OBTER ANTERIOR LANCE
    private static long getPreviousBid(AuctionHolder auction)
    {
        try
        {
            // cópia: ordenar a lista compartilhada mexeria na ordem para todo mundo
            List<AuctionBid> bids = new ArrayList<>(AuctionTableCommunity.getInstance().getBids(auction.getAuctionId()));
            
            if (bids.isEmpty())
            {
                return auction.getStartingPrice();
            }
            
            // Ordenar por tempo (mais recente primeiro)
            bids.sort((a, b) -> b.getBidTime().compareTo(a.getBidTime()));
            
            // Pegar o último lance do jogador atual
            int playerId = auction.getBidderId();
            for (AuctionBid bid : bids)
            {
                if (bid.getBidderId() == playerId)
                {
                    return bid.getBidAmount();
                }
            }
            
            return auction.getStartingPrice();
        }
        catch (Exception e)
        {
            log.warn("Erro ao obter lance anterior: " + e.getMessage());
            return auction.getStartingPrice();
        }
    }
    
    // ✅ VERIFICAR NOTIFICAÇÃO PENDENTE
    public boolean hasPendingNotification(int playerId)
    {
        return outbidNotifications.containsKey(playerId);
    }
    
    // ✅ OBTER ID DO LEILÃO PENDENTE
    public int getPendingAuctionId(int playerId)
    {
        return outbidNotifications.getOrDefault(playerId, 0);
    }
    
    // ✅ REMOVER NOTIFICAÇÃO
    public void removeNotification(int playerId)
    {
        outbidNotifications.remove(playerId);
    }
}
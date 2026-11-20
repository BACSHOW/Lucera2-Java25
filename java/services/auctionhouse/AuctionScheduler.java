package services.auctionhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2.gameserver.Announcements;
import l2.gameserver.ThreadPoolManager;
import l2.gameserver.data.xml.holder.ItemHolder;


public class AuctionScheduler
{
    private static AuctionScheduler _instance;
    private static Logger log = LoggerFactory.getLogger(AuctionScheduler.class);
    
    public static AuctionScheduler getInstance()
    {
        if (_instance == null)
            _instance = new AuctionScheduler();
        return _instance;
    }
    
    public AuctionScheduler()
    {
        startAnnouncementTask();
    }
    
    private static void startAnnouncementTask()
    {
        // ✅ APENAS ANÚNCIOS - NÃO VERIFICA EXPIRAÇÃO
    	ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
        {
            announceEndingAuctions();
        }, 300000, 300000);

    }
    
    private static void announceEndingAuctions()
    {
        for (AuctionHolder auction : AuctionTableCommunity.getInstance().getAuctions().values())
        {
            if (auction.isActive() && auction.getTimeRemaining() > 0 && auction.getTimeRemaining() <= 3600000)
            {
                String itemName = ItemHolder.getInstance().getTemplate(auction.getItemId()).getName();
                //Broadcast.gameAnnounceAuctionShop("Leilão de " + itemName + " termina em 1 hora!");
                Announcements.getInstance().announceByCustomMessage("Leilão de " + itemName + " termina em 1 hora!", null);
            }
        }
    }
}
package services.auctionhouse;

import java.util.Date;

public class AuctionBid
{
    private int id;
    private int auctionId;
    private int bidderId;
    private long bidAmount;
    private Date bidTime;
    private boolean cancelled;
    
    public AuctionBid(int auctionId, int bidderId, long bidAmount, Date bidTime)
    {
        this.id = 0;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
        this.cancelled = false;
    }
    
    public AuctionBid(int id, int auctionId, int bidderId, long bidAmount, Date bidTime, boolean cancelled)
    {
        this.id = id;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
        this.cancelled = cancelled;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getAuctionId() { return auctionId; }
    public int getBidderId() { return bidderId; }
    public long getBidAmount() { return bidAmount; }
    public Date getBidTime() { return bidTime; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
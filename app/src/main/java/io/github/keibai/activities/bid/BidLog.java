package io.github.keibai.activities.bid;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * BidLog class
 */

public class BidLog {

    public static final String BID_MESSAGE = "BID_MESSAGE";
    public static final String WON_MESSAGE = "WON_MESSAGE";

    private double amount;
    private Timestamp createdAt;
    private int auctionId;
    private int ownerId;

    public BidLog(double amount, Timestamp createdAt, int auctionId, int ownerId) {
        this.amount = amount;
        this.createdAt = createdAt;
        this.auctionId = auctionId;
        this.ownerId = ownerId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public double getAmount() {
        return amount;
    }

    public int getAuctionId() {
        return auctionId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getBidMessage(String auctionName) {
        //TODO: ownerId + " bidded " + amount + " in " + auctionId;
        return "You bidded " + amount + " in Auction " + auctionName;
    }
}

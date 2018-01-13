package io.github.keibai.activities.auction;

import java.sql.Timestamp;

/**
 * Transaction class
 */

public class Transaction {

    public static final String BID_MESSAGE = "BID_MESSAGE";
    public static final String WON_MESSAGE = "WON_MESSAGE";

    private String user;
    private float money;
    private Timestamp createdAt;
    private String auctionName;

    public Transaction(String user, float money, Timestamp timestamp) {
        this.user = user;
        this.money = money;
        this.createdAt = timestamp;
        this.auctionName = "current auction";
    }

    public Transaction(String user, float money, Timestamp timestamp,
                       String auctionName) {
        this.user = user;
        this.money = money;
        this.createdAt = timestamp;
        this.auctionName = auctionName;
    }

    public String getUser() {
        return user;
    }

    public float getMoney() {
        return money;
    }

    public Timestamp getCreatedAt() { return createdAt; }

    public String getBidMessage() {
        return "User " + user + " bidded €" + money;
    }
}

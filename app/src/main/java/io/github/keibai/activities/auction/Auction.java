package io.github.keibai.activities.auction;

/**
 * Auction class
 */

public class Auction {

    private long id;
    private String name;
    private String owner;
    private int auctionImageId;

    public Auction(long id, String name, String owner, int auctionImageId) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.auctionImageId = auctionImageId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public int getAuctionImageId() {
        return auctionImageId;
    }
}

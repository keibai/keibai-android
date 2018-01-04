package io.github.keibai.http;

import io.github.keibai.BuildConfig;

public class HttpUrl {

    private static final String baseUrl = BuildConfig.API_URL;

    private static final String newUser = "/users/new";
    private static final String userWhoami = "/users/whoami";
    private static final String userByIdUrl = "/users/search?id=";
    private static final String userAuthenticateUrl = "/users/authenticate";
    private static final String userDeauthenticateUrl = "/users/deauthenticate";
    private static final String userUpdateCreditUrl = "/users/update/credit";

    private static final String newEvent = "/events/new";
    private static final String eventByIdUrl = "/events/search?id=";
    private static final String eventListUrl = "/events/list";
    private static final String eventUpdateStatusUrl = "/events/update/status";

    private static final String newAuction = "/auctions/new";
    private static final String auctionByIdUrl = "/auctions/search?id=";
    private static final String auctionListByEventId = "/auctions/list?eventid=";
    private static final String auctionListByWinnerId = "/auctions/winnerlist?winnerid=";
    private static final String auctionAcceptUrl = "/auctions/accept?id=";
    public static final String auctionDeleteUrl = "/auctions/delete?id=";

    private static final String newGood = "/goods/new";
    private static final String goodByIdUrl = "/goods/search?id=";

    private static final String newBid = "/bids/new";
    private static final String bidByIdUrl = "/bids/search?id=";
    private static final String bidListByOwnerId = "/bids/list?ownerid=";

    private static final String webSocket = "/ws";

    // User
    public static String newUserUrl() {
        return baseUrl + newUser;
    }

    public static String userWhoami() {
        return baseUrl + userWhoami;
    }

    public static String getUserByIdUrl(int userId) {
        return baseUrl + userByIdUrl + userId;
    }

    public static String getUserAuthenticateUrl() {
        return baseUrl + userAuthenticateUrl;
    }

    public static String getUserDeauthenticateUrl() {
        return baseUrl + userDeauthenticateUrl;
    }

    public static String userUpdateCreditUrl() { return baseUrl + userUpdateCreditUrl; }

    // Event
    public static String newEventUrl() {
        return baseUrl + newEvent;
    }

    public static String getEventByIdUrl(int eventId) {
        return baseUrl + eventByIdUrl + eventId;
    }

    public static String getEventListUrl() {
        return baseUrl + eventListUrl;
    }

    public static String eventUpdateStatusUrl() {
        return baseUrl + eventUpdateStatusUrl;
    }

    // Auction
    public static String newAuctionUrl() {
        return baseUrl + newAuction;
    }

    public static String getAuctionByIdUrl(int auctionId) {
        return baseUrl + auctionByIdUrl + auctionId;
    }

    public static String getAuctionListByEventId(int eventId) {
        return baseUrl + auctionListByEventId + eventId;
    }

    public static String auctionAcceptUrl(int id) {
        return baseUrl+ auctionAcceptUrl + id;
    }

    public static String auctionDeleteUrl(int id) {
        return baseUrl + auctionDeleteUrl + id;
    }

    // Good
    public static String newGoodUrl() {
        return baseUrl + newGood;
    }

    public static String getGoodByIdUrl(int goodId) {
        return baseUrl + goodByIdUrl + goodId;
    }

    // Bid
    public static String newBidUrl() {
        return baseUrl + newBid;
    }

    public static String getBidByIdUrl(int bidId) {
        return baseUrl + bidByIdUrl + bidId;
    }

    public static String getBidListByOwnerId(int ownerId) {
        return baseUrl + bidListByOwnerId + ownerId;
    }

    public static String getAuctionListByWinnerId(int winnerId) {
        return baseUrl + auctionListByWinnerId + winnerId;
    }

    // WebSocket
    public static String webSocket() {
        return baseUrl + webSocket;
    }
}

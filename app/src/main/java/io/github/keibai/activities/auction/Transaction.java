package io.github.keibai.activities.auction;

import java.util.Calendar;

/**
 * Transaction class
 */

public class Transaction {

    public static final String BID_MESSAGE = "BID_MESSAGE";
    public static final String WON_MESSAGE = "WON_MESSAGE";

    private String user;
    private float money;
    private Calendar calendar;
    private String auctionName;
    private String messageType;

    public Transaction(String user, float money, Calendar date, String messageType) {
        this.user = user;
        this.money = money;
        this.calendar = date;
        this.auctionName = "current auction";
        this.messageType = messageType;
    }

    public Transaction(String user, float money, Calendar date,
                       String auctionName, String messageType) {
        this.user = user;
        this.money = money;
        this.calendar = date;
        this.auctionName = auctionName;
        this.messageType = messageType;
    }

    public String getUser() {
        return user;
    }

    public float getMoney() {
        return money;
    }

    public String getDate() {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return day + "/" + month + "/" + year;
    }

    public String getTime() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int month = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return hour + ":" + month + ":" + second;
    }

    public String getBidMessage() {
        return user + " bidded " + money + " in " + auctionName;
    }

    public String getWonMessage() {
        return "You have won " + auctionName + " bidding " + money;
    }

    public String getMessage() {
        if (messageType.equals(WON_MESSAGE)) {
            return getWonMessage();
        }
        if (messageType.equals(BID_MESSAGE)) {
            return getBidMessage();
        }
        return "";
    }
}

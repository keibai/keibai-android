package io.github.keibai.auction;

import java.util.Calendar;

/**
 * Transaction class
 */

public class Transaction {

    private String user;
    private float money;
    private Calendar calendar;

    public Transaction(String user, float money, Calendar date) {
        this.user = user;
        this.money = money;
        this.calendar = date;
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
}

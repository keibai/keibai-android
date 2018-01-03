package io.github.keibai.activities.bid;

import java.util.Comparator;

public class BidLogAmountComparable implements Comparator<BidLog> {
    @Override
    public int compare(BidLog a, BidLog b) {
        if (a.getAmount() > b.getAmount()) {
            return -1;
        } else if (a.getAmount() == b.getAmount()) {
            return 0;
        } else {
            return 1;
        }
    }
}

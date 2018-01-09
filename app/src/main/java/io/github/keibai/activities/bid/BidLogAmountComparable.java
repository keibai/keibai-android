package io.github.keibai.activities.bid;

import java.util.Comparator;

public class BidLogAmountComparable implements Comparator<BidLog> {
    @Override
    public int compare(BidLog a, BidLog b) {
        return Double.compare(a.getAmount(), b.getAmount());
    }
}

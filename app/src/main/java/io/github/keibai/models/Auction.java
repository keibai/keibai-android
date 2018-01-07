package io.github.keibai.models;

import java.sql.Timestamp;
import java.util.Objects;

public class Auction extends ModelAbstract {

    public static final String PENDING = "PENDING";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String FINISHED = "FINISHED";

    public static final String[] AUCTION_STATUSES = {PENDING, ACCEPTED, IN_PROGRESS, FINISHED};

    public String name;
    public double startingPrice;
    public Timestamp startTime;
    public Timestamp endingTime;
    public int eventId;
    public int ownerId;
    public String status;
    public int winnerId;
    public String combinatorialWinners;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Auction auction = (Auction) o;

        if (Double.compare(auction.startingPrice, startingPrice) != 0) return false;
        if (eventId != auction.eventId) return false;
        if (ownerId != auction.ownerId) return false;
        if (winnerId != auction.winnerId) return false;
        if (!name.equals(auction.name)) return false;
        if (startTime != null ? !startTime.equals(auction.startTime) : auction.startTime != null) return false;
        if (endingTime != null ? !endingTime.equals(auction.endingTime) : auction.endingTime != null) return false;
        if (!status.equals(auction.status)) return false;
        return combinatorialWinners != null ? combinatorialWinners.equals(auction.combinatorialWinners) : auction.combinatorialWinners == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        temp = Double.doubleToLongBits(startingPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endingTime != null ? endingTime.hashCode() : 0);
        result = 31 * result + eventId;
        result = 31 * result + ownerId;
        result = 31 * result + status.hashCode();
        result = 31 * result + winnerId;
        result = 31 * result + (combinatorialWinners != null ? combinatorialWinners.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Auction{" +
                "name='" + name + '\'' +
                ", startingPrice=" + startingPrice +
                ", startTime=" + startTime +
                ", endingTime=" + endingTime +
                ", eventId=" + eventId +
                ", ownerId=" + ownerId +
                ", status='" + status + '\'' +
                ", winnerId=" + winnerId +
                ", combinatorialWinners='" + combinatorialWinners + '\'' +
                ", id=" + id +
                '}';
    }
}

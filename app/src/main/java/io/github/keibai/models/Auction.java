package io.github.keibai.models;

import java.sql.Timestamp;
import java.util.Objects;

public class Auction extends ModelAbstract {

    public static final String OPENED = "OPENED";
    public static final String CLOSED = "CLOSED";
    public static final String IN_PROGRESS = "IN_PROGRESS";

    public static final String[] AUCTION_STATUSES = {OPENED, CLOSED, IN_PROGRESS};

    public static final String PENDING = "PENDING";
    public static final String ACCEPTED = "ACCEPTED";
    public static final String DENIED = "DENIED";
    public static final String[] AUCTION_VALID_STATES = {PENDING, ACCEPTED, DENIED};

    public String name;
    public double startingPrice;
    public Timestamp startTime;
    public String valid;
    public int eventId;
    public int ownerId;
    public String status;
    public int winnerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Auction auction = (Auction) o;

        if (id != auction.id) return false;
        if (Double.compare(auction.startingPrice, startingPrice) != 0) return false;
        if (!Objects.equals(valid, auction.valid)) return false;
        if (eventId != auction.eventId) return false;
        if (ownerId != auction.ownerId) return false;
        if (winnerId != auction.winnerId) return false;
        if (!name.equals(auction.name)) return false;
        if (startTime != null ? !startTime.equals(auction.startTime) : auction.startTime != null) return false;
        return status.equals(auction.status);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        temp = Double.doubleToLongBits(startingPrice);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + valid.hashCode();
        result = 31 * result + eventId;
        result = 31 * result + ownerId;
        result = 31 * result + status.hashCode();
        result = 31 * result + winnerId;
        return result;
    }

    @Override
    public String toString() {
        return "Auction{" +
                "name='" + name + '\'' +
                ", startingPrice=" + startingPrice +
                ", startTime=" + startTime +
                ", valid=" + valid +
                ", eventId=" + eventId +
                ", ownerId=" + ownerId +
                ", status='" + status + '\'' +
                ", winnerId=" + winnerId +
                ", id=" + id +
                '}';
    }

    public Timestamp getStartTime() {
        return this.startTime;
    }


}

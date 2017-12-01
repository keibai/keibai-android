package io.github.keibai.models;

import java.sql.Timestamp;

public class Event extends ModelAbstract {

    public static final String[] AUCTION_TYPES = {"English", "Dutch", "Combinatorial"};

    public String name;
    public int auctionTime;
    public String location;
    public Timestamp createdAt;
    public Timestamp updatedAt;
    public String auctionType;
    public String category;
    public int ownerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        if (id != event.id) return false;
        if (auctionTime != event.auctionTime) return false;
        if (ownerId != event.ownerId) return false;
        if (!name.equals(event.name)) return false;
        if (!location.equals(event.location)) return false;
        if (createdAt != null ? !createdAt.equals(event.createdAt) : event.createdAt != null) return false;
        if (updatedAt != null ? !updatedAt.equals(event.updatedAt) : event.updatedAt != null) return false;
        if (!auctionType.equals(event.auctionType)) return false;
        return category != null ? category.equals(event.category) : event.category == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + auctionTime;
        result = 31 * result + location.hashCode();
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + auctionType.hashCode();
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + ownerId;
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", auctionTime=" + auctionTime +
                ", location='" + location + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", auctionType='" + auctionType + '\'' +
                ", category='" + category + '\'' +
                ", ownerId=" + ownerId +
                ", id=" + id +
                '}';
    }
}



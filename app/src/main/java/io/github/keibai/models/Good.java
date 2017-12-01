package io.github.keibai.models;

public class Good extends ModelAbstract {

    public String name;
    public String image;
    public int auctionId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Good)) return false;

        Good good = (Good) o;

        if (id != good.id) return false;
        if (auctionId != good.auctionId) return false;
        if (!name.equals(good.name)) return false;
        return image != null ? image.equals(good.image) : good.image == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + auctionId;
        return result;
    }
}

package io.github.keibai.auction;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.keibai.R;

/**
 * Auction adapter class
 */

public class AuctionAdapter extends ArrayAdapter {

    public AuctionAdapter(@NonNull Context context, @NonNull List<Auction> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.auction_list_item,
                    parent, false);
        }

        Auction currentAuction = (Auction) getItem(position);

        TextView nameTextView = listItemView.findViewById(R.id.auction_name);
        nameTextView.setText(currentAuction.getName());

        TextView ownerTextView = listItemView.findViewById(R.id.auction_owner);
        ownerTextView.setText(currentAuction.getOwner());

        ImageView imageView = listItemView.findViewById(R.id.auction_img);
        imageView.setImageResource(currentAuction.getAuctionImageId());

        return listItemView;
    }
}

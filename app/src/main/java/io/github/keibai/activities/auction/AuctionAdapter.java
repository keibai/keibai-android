package io.github.keibai.activities.auction;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.keibai.R;
import io.github.keibai.models.Auction;

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

        Resources res = parent.getResources();

        Auction currentAuction = (Auction) getItem(position);
        System.out.println(currentAuction);
        TextView nameTextView = listItemView.findViewById(R.id.auction_name);
        nameTextView.setText(currentAuction.name);

        TextView startingPriceTextView = listItemView.findViewById(R.id.auction_starting_price);
        String startingPrice = String.format(res.getString(R.string.starting_price_placeholder), currentAuction.startingPrice);
        startingPriceTextView.setText(startingPrice);

        ImageView imageView = listItemView.findViewById(R.id.auction_img);
        imageView.setImageResource(R.drawable.ic_dori);

        TextView isValidStatusTextView = listItemView.findViewById(R.id.text_auction_is_valid_status);
        isValidStatusTextView.setText(currentAuction.status);

        return listItemView;
    }
}

package io.github.keibai.activities.auction;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.event.DetailEventActivity;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Event;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

/**
 * Auction adapter class
 */

public class AuctionAdapter extends ArrayAdapter {

    private Context context;
    private Event event;

    public AuctionAdapter(@NonNull Context context, @NonNull List<Auction> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    public void setEvent(Event event) {
        this.event = event;
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

        final Auction currentAuction = (Auction) getItem(position);
        System.out.println(currentAuction);
        TextView nameTextView = listItemView.findViewById(R.id.auction_name);
        nameTextView.setText(currentAuction.name);

        TextView startingPriceTextView = listItemView.findViewById(R.id.auction_starting_price);
        String startingPrice = String.format(res.getString(R.string.starting_price_placeholder), currentAuction.startingPrice);
        startingPriceTextView.setText(startingPrice);

        ImageView imageView = listItemView.findViewById(R.id.auction_img);
        imageView.setImageResource(R.drawable.ic_dori);

        final TextView isValidStatusTextView = listItemView.findViewById(R.id.text_auction_is_valid_status);
        isValidStatusTextView.setText(currentAuction.status);

        final Button acceptButton = listItemView.findViewById(R.id.button_accept_auction);
        final Button denyButton = listItemView.findViewById(R.id.button_deny_auction);

        if (!Objects.equals(currentAuction.status, Auction.PENDING) ||
                event == null || event.ownerId != SaveSharedPreference.getUserId(getContext())) {
            acceptButton.setVisibility(View.INVISIBLE);
            denyButton.setVisibility(View.INVISIBLE);
        } else {
            acceptButton.setVisibility(View.VISIBLE);
            denyButton.setVisibility(View.VISIBLE);
        }

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auction updateAuction = new Auction();
                updateAuction.id = currentAuction.id;
                updateAuction.status = Auction.ACCEPTED;

                new Http(getContext()).post(HttpUrl.auctionUpdateStatusUrl(), updateAuction, new HttpCallback<Auction>(Auction.class) {
                    @Override
                    public void onError(Error error) throws IOException {
                        ((DetailEventActivity) context).runOnUiThread(new RunnableToast(getContext(), error.toString()));
                    }

                    @Override
                    public void onSuccess(final Auction response) throws IOException {
                        ((DetailEventActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                acceptButton.setVisibility(View.INVISIBLE);
                                denyButton.setVisibility(View.INVISIBLE);
                                isValidStatusTextView.setText(response.status);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        ((DetailEventActivity) context).runOnUiThread(new RunnableToast(getContext(), e.toString()));
                    }
                });
            }
        });

        return listItemView;
    }
}

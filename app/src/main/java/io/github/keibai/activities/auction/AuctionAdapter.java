package io.github.keibai.activities.auction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
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
import io.github.keibai.models.meta.Msg;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

/**
 * Auction adapter class
 */

public class AuctionAdapter extends ArrayAdapter {

    private Context context;
    private Event event;
    private List<Auction> auctionList;

    public AuctionAdapter(@NonNull Context context, @NonNull List<Auction> objects) {
        super(context, 0, objects);
        this.context = context;
        this.auctionList = objects;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.auction_list_item,
                    parent, false);
        }

        Collections.sort(this.auctionList, new Comparator<Auction>() {
            @Override
            public int compare(Auction auction1, Auction auction2) {
                if (auction1.status.equals(auction2.status)) {
                    return 0;
                }
                if (auction1.status.equals(Auction.PENDING)) {
                    return -1;
                }
                return 1;
            }
        });

        final Resources res = parent.getResources();

        final Auction currentAuction = (Auction) getItem(position);
        TextView nameTextView = listItemView.findViewById(R.id.auction_name);
        nameTextView.setText(currentAuction.name);

        TextView startingPriceTextView = listItemView.findViewById(R.id.auction_starting_price);
        String startingPrice = String.format(res.getString(R.string.starting_price_placeholder), currentAuction.startingPrice);
        startingPriceTextView.setText(startingPrice);

        final TextView isValidStatusTextView = listItemView.findViewById(R.id.text_auction_is_valid_status);
        isValidStatusTextView.setText(currentAuction.status);

        ImageView imageView = listItemView.findViewById(R.id.auction_img);
        if (currentAuction.status.equals(Auction.PENDING)) imageView.setImageResource(R.drawable.ic_yellow);
        else if (currentAuction.status.equals(Auction.ACCEPTED)) imageView.setImageResource(R.drawable.ic_green);
        else if (currentAuction.status.equals(Auction.IN_PROGRESS)) imageView.setImageResource(R.drawable.ic_red);
        else if (currentAuction.status.equals(Auction.FINISHED)) imageView.setImageResource(R.drawable.ic_grey);

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
                                auctionList.get(position).status = response.status;
                                notifyDataSetChanged();
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

        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb=new AlertDialog.Builder(context);
                adb.setTitle(res.getString(R.string.auction_delete));
                adb.setMessage(String.format(res.getString(R.string.auction_delete_placeholder),
                        currentAuction.name));
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAuction(currentAuction, position);
                    }
                });
                adb.show();
            }
        });

        return listItemView;
    }

    private void deleteAuction(Auction currentAuction, final int position) {
        new Http(getContext()).post(HttpUrl.auctionDeleteUrl(currentAuction.id), new Auction(),
                new HttpCallback<Msg>(Msg.class) {
                    @Override
                    public void onError(Error error) throws IOException {
                        ((DetailEventActivity) context).runOnUiThread(new RunnableToast(getContext(), error.toString()));
                    }

                    @Override
                    public void onSuccess(final Msg response) throws IOException {
                        ((DetailEventActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                auctionList.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        ((DetailEventActivity) context).runOnUiThread(new RunnableToast(getContext(), e.toString()));
                    }
                });
    }
}

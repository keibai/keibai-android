package io.github.keibai.activities.bid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import io.github.keibai.R;
import io.github.keibai.activities.MainActivity;
import io.github.keibai.activities.activity.ActivityBidFragment;
import io.github.keibai.activities.activity.ActivityFragment;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Auction;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

/**
 * BidLog adapter
 */

public class BidAdapter extends ArrayAdapter {

    private Context context;
    private int auctionId;
    private BidLog currentTransaction;
    private View listItemView;

    public BidAdapter(@NonNull Context context, @NonNull List<BidLog> objects) {
        super(context, 0, objects);

        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_list_item,
                    parent, false);
        }

        currentTransaction = (BidLog) getItem(position);

        TextView dateTextView = listItemView.findViewById(R.id.transaction_date);
        long now = System.currentTimeMillis();
        CharSequence friendlyTimestamp = DateUtils.getRelativeTimeSpanString(
                currentTransaction.getCreatedAt().getTime(), now, DateUtils.DAY_IN_MILLIS);
        dateTextView.setText(friendlyTimestamp);

        /*
        TextView timeTextView = listItemView.findViewById(R.id.transaction_time);
        timeTextView.setText(currentTransaction.getTime());
        */

        auctionId = currentTransaction.getAuctionId();
        fetchAuctionName();

        return listItemView;
    }

    private void render(String name)
    {
        TextView text = listItemView.findViewById(R.id.transaction_text);
        text.setText(currentTransaction.getBidMessage(name));
    }

    private void fetchAuctionName() {
        new Http(getContext()).get(HttpUrl.getAuctionByIdUrl(auctionId),
                new HttpCallback<Auction>(Auction.class) {
                    @Override
                    public void onError(Error error) throws IOException {
                        ((MainActivity) context).runOnUiThread(new RunnableToast(getContext(), error.toString()));
                    }

                    @Override
                    public void onSuccess(final Auction response) throws IOException
                  {      ((MainActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               render(response.name);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        ((MainActivity) context).runOnUiThread(new RunnableToast(getContext(), e.toString()));
                    }
                });
    }
}

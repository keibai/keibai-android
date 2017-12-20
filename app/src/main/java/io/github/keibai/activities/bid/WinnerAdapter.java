package io.github.keibai.activities.bid;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.keibai.R;
import io.github.keibai.models.Auction;

/**
 * Auction adapter
 */

public class WinnerAdapter extends ArrayAdapter {

    public WinnerAdapter(@NonNull Context context, @NonNull List<Auction> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_list_item,
                    parent, false);
        }

        Auction currentTransaction = (Auction) getItem(position);

        TextView dateTextView = listItemView.findViewById(R.id.transaction_date);
        long now = System.currentTimeMillis();
        CharSequence friendlyTimestamp = DateUtils.getRelativeTimeSpanString(
                currentTransaction.startTime.getTime(), now, DateUtils.DAY_IN_MILLIS);
        dateTextView.setText(friendlyTimestamp);

        /*
        TextView timeTextView = listItemView.findViewById(R.id.transaction_time);
        timeTextView.setText(currentTransaction.getTime());
        */
        Resources res = parent.getResources();

        String winningmessage = String.format(res.getString(R.string.winmessage), currentTransaction.name);

        TextView text = listItemView.findViewById(R.id.transaction_text);
        text.setText(winningmessage);

        return listItemView;
    }

}

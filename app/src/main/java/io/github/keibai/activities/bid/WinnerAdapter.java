package io.github.keibai.activities.bid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import io.github.keibai.R;
import io.github.keibai.models.Auction;

/**
 * Auction adapter
 */

public class WinnerAdapter extends ArrayAdapter {

    private Integer rowcount;
    private LinearLayout linearLayout;

    public WinnerAdapter(@NonNull Context context, @NonNull List<Auction> objects) {
        super(context, 0, objects);

        this.rowcount = 0;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_list_item,
                    parent, false);
            linearLayout = (LinearLayout) listItemView.findViewById(R.id.layout);
        }

        Auction currentTransaction = (Auction) getItem(position);

        TextView dateTextView = listItemView.findViewById(R.id.transaction_date);
        long now = System.currentTimeMillis();
        CharSequence friendlyTimestamp = DateUtils.getRelativeTimeSpanString(
                currentTransaction.startTime.getTime(), now, DateUtils.DAY_IN_MILLIS);
        dateTextView.setText(friendlyTimestamp);

        Resources res = parent.getResources();

        String winningmessage = String.format(res.getString(R.string.winmessage), currentTransaction.name);

        TextView text = listItemView.findViewById(R.id.transaction_text);
        text.setText(winningmessage);

        if (rowcount % 2 == 0) linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        else linearLayout.setBackgroundColor(Color.parseColor("#F4F4F4"));

        ++rowcount;

        return listItemView;
    }
}

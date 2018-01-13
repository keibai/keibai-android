package io.github.keibai.activities.auction;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.github.keibai.R;

/**
 * Transaction adapter
 */

public class TransactionAdapter extends ArrayAdapter {

    private Integer rowcount;
    private LinearLayout linearLayout;

    public TransactionAdapter(@NonNull Context context, @NonNull List<Transaction> objects) {
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
            linearLayout = listItemView.findViewById(R.id.layout);
        }

        Transaction currentTransaction = (Transaction) getItem(position);

        TextView dateTextView = listItemView.findViewById(R.id.transaction_date);
        long now = System.currentTimeMillis();
        CharSequence friendlyTimestamp = DateUtils.getRelativeTimeSpanString(
                currentTransaction.getCreatedAt().getTime(), now, DateUtils.DAY_IN_MILLIS);
        dateTextView.setText(friendlyTimestamp);

        TextView timeTextView = listItemView.findViewById(R.id.transaction_time);
        timeTextView.setText(currentTransaction.getCreatedAt().toString().split("\\s")[1].split("\\.")[0]);

        TextView text = listItemView.findViewById(R.id.transaction_text);
        text.setText(currentTransaction.getBidMessage());

        if (rowcount % 2 == 0) linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        else linearLayout.setBackgroundColor(Color.parseColor("#F4F4F4"));

        ++rowcount;

        return listItemView;
    }
}

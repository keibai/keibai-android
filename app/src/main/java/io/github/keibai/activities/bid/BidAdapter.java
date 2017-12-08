package io.github.keibai.activities.bid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.keibai.R;

/**
 * BidLog adapter
 */

public class BidAdapter extends ArrayAdapter {

    public BidAdapter(@NonNull Context context, @NonNull List<BidLog> objects) {
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

        BidLog currentTransaction = (BidLog) getItem(position);

        TextView dateTextView = listItemView.findViewById(R.id.transaction_date);
        dateTextView.setText(currentTransaction.getCreatedAt().toString());

        /*
        TextView timeTextView = listItemView.findViewById(R.id.transaction_time);
        timeTextView.setText(currentTransaction.getTime());
        */

        TextView text = listItemView.findViewById(R.id.transaction_text);
        text.setText(currentTransaction.getBidMessage());

        return listItemView;
    }
}

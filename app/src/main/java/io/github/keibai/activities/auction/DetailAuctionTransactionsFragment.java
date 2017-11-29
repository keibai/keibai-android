package io.github.keibai.activities.auction;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.keibai.R;

public class DetailAuctionTransactionsFragment extends Fragment{

    public DetailAuctionTransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_auction_transactions,
                container, false);

        ListView listView = view.findViewById(R.id.auction_transaction_list);

        // TODO: Change in next sprint, events will be retrieved using the API
        List<Transaction> transactions = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        transactions.add(new Transaction("Gerard", 10.31f, calendar, Transaction.BID_MESSAGE));
        transactions.add(new Transaction("Eduard", 10.45f, calendar, Transaction.BID_MESSAGE));
        transactions.add(new Transaction("Mark", 12.31f, calendar, Transaction.BID_MESSAGE));
        transactions.add(new Transaction("Mirza", 15.31f, calendar, Transaction.BID_MESSAGE));

        TransactionAdapter transactionAdapter = new TransactionAdapter(getContext(), transactions);
        listView.setAdapter(transactionAdapter);

        return view;
    }

}
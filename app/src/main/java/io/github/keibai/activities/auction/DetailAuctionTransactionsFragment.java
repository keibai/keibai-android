package io.github.keibai.activities.auction;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.github.keibai.R;
import io.github.keibai.http.WebSocketBodyCallback;
import io.github.keibai.http.WebSocketConnection;
import io.github.keibai.models.Bid;
import io.github.keibai.models.meta.BodyWS;

public class DetailAuctionTransactionsFragment extends Fragment{

    private WebSocketConnection wsConnection;

    private List<Transaction> transactions;
    private TransactionAdapter transactionAdapter;

    public DetailAuctionTransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        wsConnection = ((DetailAuctionActivity) getActivity()).getWsConnection();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wsSubscribeToNewBid();
    }

    private void wsSubscribeToNewBid() {
        wsConnection.on(DetailAuctionBidFragment.TYPE_AUCTION_BIDDED, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, final BodyWS body) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bid bodyBid = new Gson().fromJson(body.json, Bid.class);
                        Transaction transaction = new Transaction(String.valueOf(bodyBid.ownerId),
                                (float) bodyBid.amount, bodyBid.createdAt);
                        transactions.add(0, transaction);
                        transactionAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_auction_transactions,
                container, false);

        ListView listView = view.findViewById(R.id.auction_transaction_list);

        /*
        Websocket returns me a bid, I have to map all bid information into a transaction class.
        Transaction class:
            Transaction user = Bid ownerId
            Transaction money = Bid amount
            Transaction createdAt = Bid createdAt;
            Transaction auctionName = Bid auctionId;
        after that, I add the transaction to a List, and follows the rest of the function:
        */
        transactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(getContext(), transactions);
        listView.setAdapter(transactionAdapter);

        return view;
    }

}
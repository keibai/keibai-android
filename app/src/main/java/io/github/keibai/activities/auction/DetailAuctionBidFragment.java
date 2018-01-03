package io.github.keibai.activities.auction;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Event;

public class DetailAuctionBidFragment extends Fragment{

    private View view;

    private Auction auction;
    private Event event;

    private Button startAuctionButton;
    private TextView remainingAuctionTimeText;
    private TextView highestBidText;
    private EditText editTextBid;
    private SeekBar seekBarBid;
    private Button bidButton;

    public DetailAuctionBidFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_auction_bid, container, false);

        auction = SaveSharedPreference.getCurrentAuction(getContext());
        event = SaveSharedPreference.getCurrentEvent(getContext());

        startAuctionButton = view.findViewById(R.id.start_auction_button);
        remainingAuctionTimeText = view.findViewById(R.id.remaining_auction_time_text);
        highestBidText = view.findViewById(R.id.highest_bid_text);
        editTextBid = view.findViewById(R.id.edit_text_bid);
        seekBarBid = view.findViewById(R.id.seek_bar_bid);
        bidButton = view.findViewById(R.id.bid_button);

        setHighestBidText((float) auction.startingPrice);
        setRemainingAuctionTimeText(event.auctionTime);

        return view;
    }

    private void setHighestBidText(float bid) {
        final Resources res = getResources();
        String text = String.format(res.getString(R.string.money_placeholder), bid);
        highestBidText.setText(text);
    }

    private void setRemainingAuctionTimeText(int seconds) {
        final Resources res = getResources();
        String text = String.format(res.getString(R.string.remaining_auction_time_placeholder), seconds);
        remainingAuctionTimeText.setText(text);
    }

}
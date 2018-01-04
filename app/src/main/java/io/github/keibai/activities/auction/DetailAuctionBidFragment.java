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

import com.google.gson.Gson;

import java.io.IOException;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.http.WebSocket;
import io.github.keibai.http.WebSocketCallback;
import io.github.keibai.http.WebSocketConnection;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Bid;
import io.github.keibai.models.Event;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.BodyWS;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class DetailAuctionBidFragment extends Fragment{

    private static final float STEP = 0.5f;

    private View view;

    private Auction auction;
    private Event event;
    private User user;

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

        demoPlay();
    }

    public void demoPlay() {
        System.out.println("Started demo play.");

        WebSocket ws = new WebSocket(getContext());
        WebSocketConnection wsConnection = ws.connect(HttpUrl.webSocket(), new WebSocketCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, final BodyWS body) {
                try {
                    System.out.println(body.type);
                    System.out.println(body.nonce);
                    System.out.println(body.json);
                    Bid newBid = new Gson().fromJson(body.json, Bid.class);
                    System.out.println("Got " + newBid);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        // 1. Subscribe to auction.
        Auction auction = new Auction();
        auction.id = 13;
        System.out.println("Subscribing to " + auction);
        BodyWS bodySubscription = new BodyWS();
        bodySubscription.type = "AuctionSubscribe";
        bodySubscription.nonce = "1";
        bodySubscription.json = new Gson().toJson(auction);
        wsConnection.send(bodySubscription);

        // 2. Send a sample bid.
        Bid sampleBid = new Bid();
        sampleBid.auctionId = auction.id;
        sampleBid.amount = 1.1;
        System.out.println("Bidding " + sampleBid);
        BodyWS bodySampleBid = new BodyWS();
        bodySampleBid.type = "AuctionBid";
        bodySampleBid.nonce = "1";
        bodySampleBid.json = new Gson().toJson(sampleBid);
        wsConnection.send(bodySampleBid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_auction_bid, container, false);

        auction = SaveSharedPreference.getCurrentAuction(getContext());
        event = SaveSharedPreference.getCurrentEvent(getContext());
        retrieveUser();

        startAuctionButton = view.findViewById(R.id.start_auction_button);
        remainingAuctionTimeText = view.findViewById(R.id.remaining_auction_time_text);
        highestBidText = view.findViewById(R.id.highest_bid_text);
        editTextBid = view.findViewById(R.id.edit_text_bid);
        seekBarBid = view.findViewById(R.id.seek_bar_bid);
        bidButton = view.findViewById(R.id.bid_button);

        setHighestBidText((float) auction.startingPrice);
        setRemainingAuctionTimeText(event.auctionTime);

        seekBarBid.setOnSeekBarChangeListener(seekBarChangeListener);

        return view;
    }

    public void renderSeekBarBid() {
        seekBarBid.setMax((int) ((user.credit - auction.startingPrice) / STEP));
        editTextBid.setText(String.format("%.2f", auction.startingPrice + (seekBarBid.getProgress() * STEP)));
    }

    private void retrieveUser() {
        new Http(getContext()).get(HttpUrl.userWhoami(), new HttpCallback<User>(User.class) {

            @Override
            public void onError(Error error) throws IOException {
                if (getActivity() ==  null) {
                    return;
                }
                getActivity().runOnUiThread(new RunnableToast(getContext(), error.toString()));
            }

            @Override
            public void onSuccess(final User fetchedUser) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        user = fetchedUser;
                        renderSeekBarBid();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() ==  null) {
                    return;
                }
                getActivity().runOnUiThread(new RunnableToast(getContext(), e.toString()));
            }
        });
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

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            editTextBid.setText(String.format("%.2f", auction.startingPrice + (progress * STEP)));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
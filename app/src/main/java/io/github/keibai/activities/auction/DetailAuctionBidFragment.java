package io.github.keibai.activities.auction;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.http.WebSocket;
import io.github.keibai.http.WebSocketBodyCallback;
import io.github.keibai.http.WebSocketConnection;
import io.github.keibai.http.WebSocketConnectionCallback;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Bid;
import io.github.keibai.models.Event;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.BodyWS;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;
import okhttp3.Response;

public class DetailAuctionBidFragment extends Fragment{

    private static final float STEP = 0.5f;

    private View view;
    private Resources res;
    private Http http;

    private Auction auction;
    private Event event;
    private User user;
    private double minBid;

    private Button startAuctionButton;
    private Chronometer auctionTimeChronometer;
    private TextView highestBidText;
    private TextView auctionUserCreditText;
    private EditText editTextBid;
    private TextView bidTextView;
    private SeekBar seekBarBid;
    private Button bidButton;
    private TextView bidInfoText;

    public DetailAuctionBidFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        http = new Http(getContext());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        http.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //demoPlay();
    }

    public void demoPlay() {
        System.out.println("Started demo play.");

        WebSocket ws = new WebSocket(getContext());
        WebSocketConnection wsConnection = ws.connect(HttpUrl.webSocket(), new WebSocketConnectionCallback() {

            @Override
            public void onOpen(WebSocketConnection connection, Response response) {
                System.out.println("WebSocket connected!");
            }

            @Override
            public void onClosed(WebSocketConnection connection, int code, String reason) {
                System.out.println("Socket connection closed.");
            }
        });
        // 0. Subscribe to new bids.
        wsConnection.on("AuctionBidded", new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
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
        // 0. Subscribe to new connections.
        wsConnection.on("AuctionNewConnection", new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
                User user = new Gson().fromJson(body.json, User.class);
                System.out.println("New connection" + user);
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
        wsConnection.send(bodySubscription, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
                System.out.println("Response to AuctionSubscribe" + body);
            }
        });

        // 2. Send a sample bid.
        Bid sampleBid = new Bid();
        sampleBid.auctionId = auction.id;
        sampleBid.amount = 1.1;
        System.out.println("Bidding " + sampleBid);
        BodyWS bodySampleBid = new BodyWS();
        bodySampleBid.type = "AuctionBid";
        bodySampleBid.nonce = "1";
        bodySampleBid.json = new Gson().toJson(sampleBid);
        wsConnection.send(bodySampleBid, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
                System.out.println("Response to AuctionBid" + body);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_auction_bid, container, false);
        res = getResources();

        auction = SaveSharedPreference.getCurrentAuction(getContext());
        event = SaveSharedPreference.getCurrentEvent(getContext());
        minBid = auction.startingPrice + STEP;

        startAuctionButton = view.findViewById(R.id.start_auction_button);
        auctionTimeChronometer = view.findViewById(R.id.auction_time_chronometer);
        highestBidText = view.findViewById(R.id.highest_bid_text);
        auctionUserCreditText = view.findViewById(R.id.auction_user_credit_text);
        editTextBid = view.findViewById(R.id.edit_text_bid);
        bidTextView = view.findViewById(R.id.bid_text_view);
        seekBarBid = view.findViewById(R.id.seek_bar_bid);
        bidButton = view.findViewById(R.id.bid_button);
        bidInfoText = view.findViewById(R.id.bid_info_text);

        setHighestBidText((float) auction.startingPrice);

        if (SaveSharedPreference.getUserId(getContext()) == event.ownerId) {
            // User is the owner. He/she has access to the management part of the UI
            // TODO: Set visible only when there are not auctions in progress and auction is accepted
            startAuctionButton.setVisibility(View.VISIBLE);
            bidInfoText.setText(res.getString(R.string.ready_start_auction));
            auctionUserCreditText.setVisibility(View.INVISIBLE);
            hideBidUi();
            startAuctionButton.setOnClickListener(startAuctionButtonOnClickListener);
        } else {
            // Bidder Ui
            fetchUserInfoAndRenderBidUi();

            seekBarBid.setOnSeekBarChangeListener(seekBarChangeListener);
        }

        return view;
    }

    View.OnClickListener startAuctionButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startAuctionButton.setVisibility(View.GONE);
            auctionTimeChronometer.setVisibility(View.VISIBLE);
            auctionTimeChronometer.start();
        }
    };

    public void renderBidUi() {
        setUserCreditText();

        switch (auction.status) {
            case Auction.PENDING:
                hideBidUi();
                bidInfoText.setText(res.getString(R.string.auction_not_accepted_yet));
                break;
            case Auction.ACCEPTED:
                hideBidUi();
                bidInfoText.setText(res.getString(R.string.auction_not_started_yet));
                break;
            case Auction.FINISHED:
                hideBidUi();
                bidInfoText.setText(res.getString(R.string.auction_finished));
                break;
            case Auction.IN_PROGRESS:
                if (user.credit < minBid + STEP) {
                    disableBidUI();
                } else {
                    seekBarBid.setMax((int) ((user.credit - minBid) / STEP));
                    editTextBid.setText(String.format("%.2f", minBid + (seekBarBid.getProgress() * STEP)));
                }
                break;
        }
    }

    private void fetchUserInfoAndRenderBidUi() {
        http.get(HttpUrl.userWhoami(), new HttpCallback<User>(User.class) {

            @Override
            public void onError(Error error) throws IOException {
                getActivity().runOnUiThread(new RunnableToast(getContext(), error.toString()));
            }

            @Override
            public void onSuccess(final User fetchedUser) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        user = fetchedUser;
                        renderBidUi();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new RunnableToast(getContext(), e.toString()));
            }
        });
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            editTextBid.setText(String.format("%.2f", minBid + (progress * STEP)));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /* Bidding UI utilities */
    private void setHighestBidText(float bid) {
        String text = String.format(res.getString(R.string.money_placeholder), bid);
        highestBidText.setText(text);
    }

    private void setUserCreditText() {
        String text = String.format(res.getString(R.string.auction_user_credit_placeholder), user.credit);
        auctionUserCreditText.setText(text);
    }

    private void disableBidUI() {
        Toast.makeText(getContext(), res.getString(R.string.auction_user_credit_not_enough), Toast.LENGTH_SHORT).show();
        seekBarBid.setProgress(100);
        editTextBid.setText(String.format("%.2f", minBid));
        editTextBid.setEnabled(false);
        seekBarBid.setEnabled(false);
        bidButton.setEnabled(false);
    }

    private void hideBidUi() {
        bidTextView.setVisibility(View.GONE);
        editTextBid.setVisibility(View.GONE);
        seekBarBid.setVisibility(View.GONE);
        bidButton.setVisibility(View.GONE);

        bidInfoText.setVisibility(View.VISIBLE);
    }
}
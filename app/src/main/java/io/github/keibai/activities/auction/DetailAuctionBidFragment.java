package io.github.keibai.activities.auction;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
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

    public static final String TYPE_AUCTION_SUBSCRIBE = "AuctionSubscribe";
    public static final String TYPE_AUCTION_NEW_CONNECTION = "AuctionNewConnection";
    public static final String TYPE_AUCTION_BID = "AuctionBid";
    public static final String TYPE_AUCTION_BIDDED = "AuctionBidded";
    public static final String TYPE_AUCTION_START = "AuctionStart";
    public static final String TYPE_AUCTION_STARTED = "AuctionStarted";
    public static final String TYPE_AUCTION_CLOSE = "AuctionClose";
    public static final String TYPE_AUCTION_CLOSED = "AuctionClosed";

    private static final float STEP = 0.5f;

    private View view;
    private Resources res;
    private Http http;
    private WebSocketConnection wsConnection;

    private Auction auction;
    private Event event;
    private User user;
    private double minBid;
    private SparseArray<User> userMap;

    private Chronometer auctionTimeChronometer;
    private TextView highestBidText;
    private TextView auctionUserCreditText;
    private EditText editTextBid;
    private TextView bidTextView;
    private SeekBar seekBarBid;
    private Button bidButton;
    private TextView bidInfoText;
    private Button startAuctionButton;
    private Button stopAuctionButton;

    public DetailAuctionBidFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        http = new Http(getContext());

        WebSocket ws = new WebSocket(getContext());
        wsConnection = ws.connect(HttpUrl.webSocket(), new WebSocketConnectionCallback() {
            @Override
            public void onOpen(WebSocketConnection connection, Response response) {
                System.out.println("WebSocket connected!");
            }

            @Override
            public void onClosed(WebSocketConnection connection, int code, String reason) {
                System.out.println("Socket connection closed.");
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        http.close();
        wsConnection.close();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auction = SaveSharedPreference.getCurrentAuction(getContext());
        event = SaveSharedPreference.getCurrentEvent(getContext());
        minBid = auction.startingPrice + STEP;
        userMap = new SparseArray<>();

        wsSubscribe();
    }

    private void wsSubscribe() {
        // 1. Subscribe to auction.
        BodyWS bodySubscription = new BodyWS();
        bodySubscription.type = TYPE_AUCTION_SUBSCRIBE;
        bodySubscription.json = new Gson().toJson(auction);
        wsConnection.send(bodySubscription, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
                System.out.println("Response to AuctionSubscribe " + body);
            }
        });

        // 2. Subscribe to new connections.
        wsConnection.on(TYPE_AUCTION_NEW_CONNECTION, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
                User user = new Gson().fromJson(body.json, User.class);
                String msg = "User " + user.name + " connected";
                getActivity().runOnUiThread(new RunnableToast(getContext(), msg));
                // Add user to the internal map in order to change user ID by its name
                userMap.append(user.id, user);
            }
        });

        // 3. Subscribe to new bids. TODO
//        wsConnection.on(TYPE_AUCTION_BIDDED, new WebSocketBodyCallback() {
//            @Override
//            public void onMessage(WebSocketConnection connection, BodyWS body) {
//                try {
//                    Bid newBid = new Gson().fromJson(body.json, Bid.class);
//                    System.out.println(newBid.amount);
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//            }
//        });

        // 4. Subscribe to auction started.
        wsConnection.on(TYPE_AUCTION_STARTED, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
                try {
                    final Auction startedAuction = new Gson().fromJson(body.json, Auction.class);
                    System.out.println(startedAuction);
                    // Start chronometer
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            auction = startedAuction;
                            setChronometerTime();
                            showBidUi();
                            if (user.credit < minBid + STEP) {
                                disableBidUI();
                            }
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        // 5. Subscribe to auction closed. TODO
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_auction_bid, container, false);
        res = getResources();

        auctionTimeChronometer = view.findViewById(R.id.auction_time_chronometer);
        highestBidText = view.findViewById(R.id.highest_bid_text);
        auctionUserCreditText = view.findViewById(R.id.auction_user_credit_text);
        editTextBid = view.findViewById(R.id.edit_text_bid);
        bidTextView = view.findViewById(R.id.bid_text_view);
        seekBarBid = view.findViewById(R.id.seek_bar_bid);
        bidButton = view.findViewById(R.id.bid_button);
        bidInfoText = view.findViewById(R.id.bid_info_text);
        startAuctionButton = view.findViewById(R.id.start_auction_button);
        stopAuctionButton = view.findViewById(R.id.stop_auction_button);

        setHighestBidText((float) auction.startingPrice);

        if (SaveSharedPreference.getUserId(getContext()) == event.ownerId) {
            // User is the owner. He/she has access to the management part of the UI
            startAuctionButton.setOnClickListener(startAuctionButtonOnClickListener);
            stopAuctionButton.setOnClickListener(stopAuctionButtonOnClickListener);
            auctionUserCreditText.setVisibility(View.INVISIBLE);
            hideBidUi();

            switch (auction.status) {
                case Auction.ACCEPTED:
                    startAuctionButton.setVisibility(View.VISIBLE);
                    bidInfoText.setText(res.getString(R.string.ready_start_auction));
                    break;
                case Auction.IN_PROGRESS:
                    setChronometerTime();
                    stopAuctionButton.setVisibility(View.VISIBLE);
                    bidInfoText.setText(res.getString(R.string.ready_stop_auction));
                    break;
                case Auction.FINISHED:
                    // TODO
                    break;
                case Auction.PENDING:
                    // TODO
                    break;
            }
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
            // Send start auction to server
            BodyWS bodySubscription = new BodyWS();
            bodySubscription.type = TYPE_AUCTION_START;
            bodySubscription.json = new Gson().toJson(auction);
            wsConnection.send(bodySubscription);

            startAuctionButton.setVisibility(View.GONE);
            stopAuctionButton.setVisibility(View.VISIBLE);
            bidInfoText.setText(res.getString(R.string.ready_stop_auction));
        }
    };

    View.OnClickListener stopAuctionButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            stopAuctionButton.setVisibility(View.GONE);
            auctionTimeChronometer.stop();
            bidInfoText.setText("");

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
                setChronometerTime();
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

    private void showBidUi() {
        bidTextView.setVisibility(View.VISIBLE);
        editTextBid.setVisibility(View.VISIBLE);
        seekBarBid.setVisibility(View.VISIBLE);
        bidButton.setVisibility(View.VISIBLE);

        bidInfoText.setVisibility(View.GONE);
    }

    private void setChronometerTime() {
        /* https://stackoverflow.com/questions/21561110/how-to-use-timestamp-in-chronometr-android */
        long system = SystemClock.elapsedRealtime();
        long t = auction.startTime.getTime() - System.currentTimeMillis();
        auctionTimeChronometer.setBase((system+t)); // TODO: Check this!
        auctionTimeChronometer.start();
    }
}
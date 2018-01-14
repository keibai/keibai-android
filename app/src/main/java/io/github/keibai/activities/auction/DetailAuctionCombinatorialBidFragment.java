package io.github.keibai.activities.auction;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.gson.BetterGson;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.http.WebSocketBodyCallback;
import io.github.keibai.http.WebSocketConnection;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Bid;
import io.github.keibai.models.Event;
import io.github.keibai.models.Good;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.BodyWS;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class DetailAuctionCombinatorialBidFragment extends Fragment {

    private static final float STEP = 0.5f;

    private View view;
    private Http http;
    private WebSocketConnection wsConnection;
    private Resources res;

    private Auction auction;
    private Event event;
    private User user;
    private List<String> winnerNames;

    private List<Good> availableGoods;
    private List<Good> selectedGoods;

    private Chronometer auctionTimeChronometer;
    private ListView availableGoodsListView;
    private ListView selectedGoodsListView;
    private TextView userCreditText;
    private TextView bidTextView;
    private EditText editTextBid;
    private SeekBar seekBarBid;
    private Button bidButton;
    private TextView infoTextView;
    private Button startAuctionButton;
    private Button stopAuctionButton;

    private Toast currentToast;

    public DetailAuctionCombinatorialBidFragment() {
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

        if (http == null) {
            http = new Http(getContext());
        }

        auction = SaveSharedPreference.getCurrentAuction(getContext());
        event = SaveSharedPreference.getCurrentEvent(getContext());
        user = new User() {{ id = (int) SaveSharedPreference.getUserId(getContext()); }};
        winnerNames = new ArrayList<>();

        wsSubscribe();
    }

    @Override
    public void onStop() {
        super.onStop();

        http.close();
    }

    /* Websockets */
    private void wsSubscribe() {
        wsSubscribeToAuction();
        wsSubscribeToNewConnections();
        wsSubscribeToNewBids();
        wsSubscribeToAuctionStarted();
        wsSubscribeToAuctionClosed();
    }

    private void wsSubscribeToAuction() {
        BodyWS bodySubscription = new BodyWS();
        bodySubscription.type = DetailAuctionBidFragment.TYPE_AUCTION_SUBSCRIBE;
        bodySubscription.json = new BetterGson().newInstance().toJson(auction);
        wsConnection.send(bodySubscription, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
                System.out.println("Response to AuctionSubscribe " + body);
            }
        });
    }

    private void wsSubscribeToNewConnections() {
        wsConnection.on(DetailAuctionBidFragment.TYPE_AUCTION_NEW_CONNECTION, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
                User user = new BetterGson().newInstance().fromJson(body.json, User.class);
                final String msg = "User " + user.name + " connected";
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(msg);
                    }
                });
            }
        });
    }

    private void wsSubscribeToNewBids() {
        wsConnection.on(DetailAuctionBidFragment.TYPE_AUCTION_BIDDED, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, final BodyWS body) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Bid newBid = new BetterGson().newInstance().fromJson(body.json, Bid.class);
                        String msg = String.format(res.getString(R.string.bid_msg_placeholder), String.valueOf(newBid.ownerId), newBid.amount);
                        showToast(msg);
                    }
                });
            }
        });
    }

    private void wsSubscribeToAuctionStarted() {
        wsConnection.on(DetailAuctionBidFragment.TYPE_AUCTION_STARTED, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
                try {
                    final Auction startedAuction = new BetterGson().newInstance().fromJson(body.json, Auction.class);
                    System.out.println(startedAuction);
                    // Start chronometer
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            auction = startedAuction;
                            setChronometerTime();
                            if (user.id != event.ownerId) {
                                showBidUi();
                                if (user.credit < startedAuction.startingPrice + STEP) {
                                    disableBidUi();
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    private void wsSubscribeToAuctionClosed() {
        wsConnection.on(DetailAuctionBidFragment.TYPE_AUCTION_CLOSED, new WebSocketBodyCallback() {
            @Override
            public void onMessage(WebSocketConnection connection, BodyWS body) {
                Auction closedAuction = new BetterGson().newInstance().fromJson(body.json, Auction.class);
                renderCombinatorialWinners(closedAuction.combinatorialWinners);
            }
        });
    }

    private void renderCombinatorialWinners(final String combinatorialWinners) {
        auctionTimeChronometer.stop();
        infoTextView.setVisibility(View.VISIBLE);
        if (combinatorialWinners == null || combinatorialWinners.equals("")) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    infoTextView.setText(res.getString(R.string.auction_finished_without_winners));
                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    infoTextView.setText("Winners IDs: " + combinatorialWinners);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detail_auction_combinatorial_bid, container, false);
        res = getResources();

        auctionTimeChronometer = view.findViewById(R.id.comb_auction_time_chronometer);
        availableGoodsListView = view.findViewById(R.id.comb_available_goods_list);
        selectedGoodsListView = view.findViewById(R.id.comb_selected_goods_list);
        userCreditText = view.findViewById(R.id.comb_auction_user_credit_text);
        bidTextView = view.findViewById(R.id.comb_bid_text_view);
        editTextBid = view.findViewById(R.id.comb_edit_text_bid);
        seekBarBid = view.findViewById(R.id.comb_seek_bar_bid);
        bidButton = view.findViewById(R.id.comb_bid_button);
        infoTextView = view.findViewById(R.id.comb_bid_info_text);
        startAuctionButton = view.findViewById(R.id.comb_start_auction_button);
        stopAuctionButton = view.findViewById(R.id.comb_stop_auction_button);

        fetchAndRenderGoods();
        if (SaveSharedPreference.getUserId(getContext()) == event.ownerId) {
            // User is the owner. He/she has access to the management part of the UI
            startAuctionButton.setOnClickListener(startAuctionButtonOnClickListener);
            stopAuctionButton.setOnClickListener(stopAuctionButtonOnClickListener);
            userCreditText.setVisibility(View.INVISIBLE);
            hideBidUi();

            fetchEventAuctionsAndRenderOwnerUi();
        } else {
            // Bidder Ui
            fetchAndRenderUser();
            seekBarBid.setOnSeekBarChangeListener(seekBarChangeListener);
            bidButton.setOnClickListener(bidButtonOnClickListener);
        }

        return view;
    }

    private void fetchEventAuctionsAndRenderOwnerUi() {
        http.get(HttpUrl.getAuctionListByEventId(event.id), new HttpCallback<Auction[]>(Auction[].class) {
            @Override
            public void onError(final Error error) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(error.toString());
                    }
                });
            }

            @Override
            public void onSuccess(final Auction[] response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Search for auction in progress
                        Auction inProgressAuction = null;
                        for (Auction a: response) {
                            if (a.status.equals(Auction.IN_PROGRESS)) {
                                inProgressAuction = a;
                                break;
                            }
                        }

                        renderOwnerUi(inProgressAuction);
                    }
                });
            }

            @Override
            public void onFailure(Call call, final IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(e.toString());
                    }
                });
            }
        });
    }

    private void renderOwnerUi(Auction inProgressAuction) {
        if (inProgressAuction != null && inProgressAuction.id != auction.id) {
            // Auction in progress is not the current auction
            String text = String.format(res.getString(R.string.in_progress_auction_placeholder), inProgressAuction.name);
            infoTextView.setText(text);
        } else {
            // No auction in progress or auction in progress is the current auction
            switch (auction.status) {
                case Auction.ACCEPTED:
                    startAuctionButton.setVisibility(View.VISIBLE);
                    infoTextView.setText(res.getString(R.string.ready_start_auction));
                    break;
                case Auction.IN_PROGRESS:
                    setChronometerTime();
                    stopAuctionButton.setVisibility(View.VISIBLE);
                    infoTextView.setText(res.getString(R.string.ready_stop_auction));
                    break;
                case Auction.FINISHED:
                    renderCombinatorialWinners(auction.combinatorialWinners);
                    break;
                case Auction.PENDING:
                    infoTextView.setText(res.getString(R.string.auction_should_be_accepted));
                    break;
            }
        }
    }

    private void fetchAndRenderUser() {
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
                        renderUserBidUI();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new RunnableToast(getContext(), e.toString()));
            }
        });

    }

    private void fetchAndRenderGoods() {
        http.get(HttpUrl.getGoodListByAuctionIdUrl(auction.id), new HttpCallback<Good[]>(Good[].class) {
            @Override
            public void onError(Error error) throws IOException {
                getActivity().runOnUiThread(new RunnableToast(getContext(), error.toString()));
            }

            @Override
            public void onSuccess(final Good[] response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        availableGoods = new ArrayList<>(Arrays.asList(response));
                        selectedGoods = new ArrayList<>();
                        renderGoods();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new RunnableToast(getContext(), e.toString()));
            }
        });
    }

    private void renderUserBidUI() {
        setUserCreditText();

        switch (auction.status) {
            case Auction.PENDING:
                hideBidUi();
                infoTextView.setText(res.getString(R.string.auction_not_accepted_yet));
                break;
            case Auction.ACCEPTED:
                hideBidUi();
                infoTextView.setText(res.getString(R.string.auction_not_started_yet));
                break;
            case Auction.FINISHED:
                hideBidUi();
                renderCombinatorialWinners(auction.combinatorialWinners);
                break;
            case Auction.IN_PROGRESS:
                setChronometerTime();
                if (user.credit < auction.startingPrice) {
                    disableBidUi();
                } else {
                    seekBarBid.setMax((int) ((user.credit - auction.startingPrice) / STEP));
                    editTextBid.setText(String.format("%.2f", auction.startingPrice + (seekBarBid.getProgress() * STEP)));
                }
                break;
        }
    }

    private void renderGoods() {
        final GoodAdapter availableGoodsAdapter = new GoodAdapter(getContext(), availableGoods, false);
        final GoodAdapter selectedGoodsAdapter = new GoodAdapter(getContext(), selectedGoods, false);
        availableGoodsListView.setAdapter(availableGoodsAdapter);
        selectedGoodsListView.setAdapter(selectedGoodsAdapter);

        availableGoodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Good goodClicked = (Good) parent.getItemAtPosition(position);
                availableGoods.remove(position);
                selectedGoods.add(goodClicked);
                availableGoodsAdapter.notifyDataSetChanged();
                selectedGoodsAdapter.notifyDataSetChanged();
            }
        });

        selectedGoodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Good goodClicked = (Good) parent.getItemAtPosition(position);
                selectedGoods.remove(position);
                availableGoods.add(goodClicked);
                selectedGoodsAdapter.notifyDataSetChanged();
                availableGoodsAdapter.notifyDataSetChanged();
            }
        });
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

    View.OnClickListener startAuctionButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Send start auction to server
            BodyWS bodyStart = new BodyWS();
            bodyStart.type = DetailAuctionBidFragment.TYPE_AUCTION_START;
            bodyStart.json = new BetterGson().newInstance().toJson(auction);
            wsConnection.send(bodyStart);

            startAuctionButton.setVisibility(View.GONE);
            stopAuctionButton.setVisibility(View.VISIBLE);
            infoTextView.setText(res.getString(R.string.ready_stop_auction));
        }
    };

    View.OnClickListener stopAuctionButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            stopAuctionButton.setVisibility(View.GONE);
            auctionTimeChronometer.stop();
            BodyWS bodyClose = new BodyWS();
            bodyClose.type = DetailAuctionBidFragment.TYPE_AUCTION_CLOSE;
            bodyClose.json = new BetterGson().newInstance().toJson(auction);
            wsConnection.send(bodyClose);
            infoTextView.setText("");
        }
    };

    View.OnClickListener bidButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectedGoods.size() == 0) {
                showToast("You have to select at least one good");
                return;
            }

            List<Bid> bids = new ArrayList<>();

            BodyWS bodyBid = new BodyWS();
            bodyBid.type = DetailAuctionBidFragment.TYPE_AUCTION_BID;

            double amount;
            try {
                amount = Float.parseFloat(editTextBid.getText().toString());
            } catch (NumberFormatException e) {
                showToast("Can not bid " + editTextBid.getText().toString());
                return;
            }

            for (Good good: selectedGoods) {
                Bid bid = new Bid();
                bid.amount = amount;
                bid.auctionId = auction.id;
                bid.ownerId = user.id;
                bid.goodId = good.id;
                bids.add(bid);
            }
            System.out.println(bids);
            bodyBid.json = new BetterGson().newInstance().toJson(bids.toArray(new Bid[bids.size()]));
            wsConnection.send(bodyBid);
            hideBidUi();
            infoTextView.setText("You have already bidded");
        }
    };

    /* Bidding UI utilities */
    private void setUserCreditText() {
        String text = String.format(res.getString(R.string.auction_user_credit_placeholder), user.credit);
        userCreditText.setText(text);
    }

    private void showBidUi() {
        bidTextView.setVisibility(View.VISIBLE);
        editTextBid.setVisibility(View.VISIBLE);
        seekBarBid.setVisibility(View.VISIBLE);
        bidButton.setVisibility(View.VISIBLE);
        selectedGoodsListView.setEnabled(true);
        selectedGoodsListView.setEnabled(true);

        infoTextView.setVisibility(View.GONE);

        setSeekBar();
    }

    private void disableBidUi() {
        Toast.makeText(getContext(), res.getString(R.string.auction_user_credit_not_enough), Toast.LENGTH_SHORT).show();
        seekBarBid.setProgress(100);
        editTextBid.setText(String.format("%.2f", auction.startingPrice));
        editTextBid.setEnabled(false);
        seekBarBid.setEnabled(false);
        bidButton.setEnabled(false);
        selectedGoodsListView.setEnabled(false);
        availableGoodsListView.setEnabled(false);
    }

    private void hideBidUi() {
        bidTextView.setVisibility(View.GONE);
        editTextBid.setVisibility(View.GONE);
        seekBarBid.setVisibility(View.GONE);
        bidButton.setVisibility(View.GONE);
        availableGoodsListView.setEnabled(false);
        selectedGoodsListView.setEnabled(false);

        infoTextView.setVisibility(View.VISIBLE);
    }

    private void showToast(String text) {
        if (currentToast == null) {
            currentToast = Toast.makeText(getContext(), text, Toast.LENGTH_LONG);
        }
        currentToast.setText(text);
        currentToast.setDuration(Toast.LENGTH_LONG);
        currentToast.show();
    }

    // https://stackoverflow.com/questions/526524/android-get-time-of-chronometer-widget
    private void setChronometerTime() {
        long realtime = SystemClock.elapsedRealtime();
        Date auctionTime = auction.startTime;
        Date currentTime = new Date();
        long difference = currentTime.getTime() - auctionTime.getTime();

        auctionTimeChronometer.setBase(realtime - difference);
        auctionTimeChronometer.start();
    }

    private void setSeekBar() {
        seekBarBid.setMax((int) ((user.credit - auction.startingPrice) / STEP));
        seekBarBid.setProgress(0);
        editTextBid.setText(String.format("%.2f", auction.startingPrice + (seekBarBid.getProgress() * STEP)));
    }
}

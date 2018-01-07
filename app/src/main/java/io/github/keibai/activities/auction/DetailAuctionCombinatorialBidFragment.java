package io.github.keibai.activities.auction;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
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
import java.util.List;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Event;
import io.github.keibai.models.Good;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public class DetailAuctionCombinatorialBidFragment extends Fragment {

    private static final float STEP = 0.5f;

    private View view;
    private Http http;
    private Resources res;

    private Auction auction;
    private Event event;
    private User user;

    private List<Good> availableGoods;
    private List<Good> selectedGoods;

    private Chronometer timeChronometer;
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

    public DetailAuctionCombinatorialBidFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detail_auction_combinatorial_bid, container, false);
        res = getResources();

        auction = SaveSharedPreference.getCurrentAuction(getContext());
        event = SaveSharedPreference.getCurrentEvent(getContext());

        timeChronometer = view.findViewById(R.id.comb_auction_time_chronometer);
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
            // TODO: Set visible only when there are not auctions in progress and auction is accepted
            startAuctionButton.setVisibility(View.VISIBLE);
            infoTextView.setText(res.getString(R.string.ready_start_auction));
            userCreditText.setVisibility(View.INVISIBLE);
            hideBidUi();
            startAuctionButton.setOnClickListener(startAuctionButtonOnClickListener);
            stopAuctionButton.setOnClickListener(stopAuctionButtonOnClickListener);
        } else {
            // Bidder Ui
            fetchAndRenderUser();
            seekBarBid.setOnSeekBarChangeListener(seekBarChangeListener);
        }

        return view;
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
                bidTextView.setText(res.getString(R.string.auction_not_accepted_yet));
                break;
            case Auction.ACCEPTED:
                hideBidUi();
                bidTextView.setText(res.getString(R.string.auction_not_started_yet));
                break;
            case Auction.FINISHED:
                hideBidUi();
                bidTextView.setText(res.getString(R.string.auction_finished));
                break;
            case Auction.IN_PROGRESS:
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
            startAuctionButton.setVisibility(View.GONE);
            stopAuctionButton.setVisibility(View.VISIBLE);
            timeChronometer.setVisibility(View.VISIBLE);
            timeChronometer.start();
            infoTextView.setText(res.getString(R.string.ready_stop_auction));
        }
    };

    View.OnClickListener stopAuctionButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            stopAuctionButton.setVisibility(View.GONE);
            timeChronometer.stop();
            infoTextView.setText("");
        }
    };

    /* Bidding UI utilities */
    private void setUserCreditText() {
        String text = String.format(res.getString(R.string.auction_user_credit_placeholder), user.credit);
        userCreditText.setText(text);
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
}

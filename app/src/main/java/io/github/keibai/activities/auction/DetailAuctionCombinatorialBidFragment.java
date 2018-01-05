package io.github.keibai.activities.auction;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
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

    private ListView availableGoodsListView;
    private ListView selectedGoodsListView;
    private TextView userCreditText;
    private TextView bidTextView;
    private EditText editTextBid;
    private SeekBar seekBarBid;
    private Button bidButton;

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

        availableGoodsListView = view.findViewById(R.id.comb_available_goods_list);
        selectedGoodsListView = view.findViewById(R.id.comb_selected_goods_list);
        userCreditText = view.findViewById(R.id.comb_auction_user_credit_text);
        bidTextView = view.findViewById(R.id.comb_bid_text_view);
        editTextBid = view.findViewById(R.id.comb_edit_text_bid);
        seekBarBid = view.findViewById(R.id.comb_seek_bar_bid);
        bidButton = view.findViewById(R.id.comb_bid_button);

        fetchInfoAndRenderBidUI();
        seekBarBid.setOnSeekBarChangeListener(seekBarChangeListener);

        return view;
    }

    private void fetchInfoAndRenderBidUI() {
        // Fetch user
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

        // Fetch auction goods
    }

    private void renderUserBidUI() {
        setUserCreditText();
        // TODO: Auction status checks
        seekBarBid.setMax((int) ((user.credit - auction.startingPrice) / STEP));
        editTextBid.setText(String.format("%.2f", auction.startingPrice + (seekBarBid.getProgress() * STEP)));
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

    private void setUserCreditText() {
        String text = String.format(res.getString(R.string.auction_user_credit_placeholder), user.credit);
        userCreditText.setText(text);
    }
}

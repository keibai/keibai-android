package io.github.keibai.activities.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.MainFragmentAbstract;
import io.github.keibai.activities.bid.BidLog;
import io.github.keibai.activities.bid.BidLogAmountComparable;
import io.github.keibai.activities.credit.CreditActivity;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.Auction;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends MainFragmentAbstract {

    private View view;
    private Http http;

    // Do not delete! Constructor required by Android.
    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        if (http == null) {
            http = new Http(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = super.onCreateView(inflater, container, savedInstanceState);

        Button addButton = view.findViewById(R.id.button_home_add_credit);
        addButton.setOnClickListener(new AddCredit());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchUser();
        fetchAuctionList();
        fetchBidList();
    }

    @Override
    public void onStop() {
        super.onStop();

        http.close();
    }

    public void renderUser(User user) {
        TextView credit = view.findViewById(R.id.text_home_credit);

        credit.setText(String.format("%.2f", user.credit));
    }

    private class AddCredit implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), CreditActivity.class);
            startActivity(intent);
        }
    }

    private void fetchUser() {
        http.get(HttpUrl.userWhoami(), new HttpCallback<User>(User.class) {

            @Override
            public void onError(Error error) throws IOException {
                getActivity().runOnUiThread(new RunnableToast(getContext(), error.toString()));
            }

            @Override
            public void onSuccess(final User user) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        renderUser(user);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //getActivity().runOnUiThread(new RunnableToast(getContext(), e.toString()));
            }
        });
    }

    private void fetchAuctionList() {
        int userId = (int) SaveSharedPreference.getUserId(getContext());
        http.get(HttpUrl.getAuctionListByWinnerId(userId), new HttpCallback<Auction[]>(Auction[].class) {
            @Override
            public void onError(Error error) throws IOException {
                getActivity().runOnUiThread(new RunnableToast(getContext(), error.toString()));
            }

            @Override
            public void onSuccess(final Auction[] response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        renderAuction(response);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //getActivity().runOnUiThread(new RunnableToast(getContext(), e.toString()));
            }
        });
    }

    private void renderAuction(Auction[] response) {
        TextView participateActivity = view.findViewById(R.id.auction_won);

        participateActivity.setText(String.valueOf(response.length));
    }

    private void fetchBidList() {
        int userId = (int) SaveSharedPreference.getUserId(getContext());
        http.get(HttpUrl.getBidListByOwnerId(userId), new HttpCallback<BidLog[]>(BidLog[].class) {
            @Override
            public void onError(Error error) throws IOException {
                getActivity().runOnUiThread(new RunnableToast(getContext(), error.toString()));
            }

            @Override
            public void onSuccess(final BidLog[] response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        renderBid(response);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //getActivity().runOnUiThread(new RunnableToast(getContext(), e.toString()));
            }
        });
    }

    private void renderBid(BidLog[] bids) {
        TextView madeBid = view.findViewById(R.id.made_bid);
        madeBid.setText(String.valueOf(bids.length));

        if (bids.length > 0) {
            BidLog maxBid = Collections.max(Arrays.asList(bids), new BidLogAmountComparable());
            TextView maxBidText = view.findViewById(R.id.max_bid);
            maxBidText.setText(String.valueOf(maxBid.getAmount()));
        }
    }
}

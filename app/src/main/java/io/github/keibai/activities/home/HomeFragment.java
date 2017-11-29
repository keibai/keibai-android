package io.github.keibai.activities.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import io.github.keibai.activities.MainFragmentAbstract;
import io.github.keibai.R;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.models.Bid;
import io.github.keibai.models.meta.Error;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends MainFragmentAbstract {


    public HomeFragment() {
        // Constructor required by Android.
        super(R.layout.fragment_home);
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = super.onCreateView(inflater, container, savedInstanceState);

        TextView textViewMoney = view.findViewById(R.id.text_money);
        textViewMoney.setText("100.00");

        Button addButton = view.findViewById(R.id.button_home_add_credit);
        addButton.setOnClickListener(new AddCredit());

        return view;
    }

    private class AddCredit implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                new Http<>().get("https://keibai.herokuapp.com/bids/search?id=1", new HttpCallback<Bid>() {
                    @Override
                    public Class<Bid> model() {
                        return Bid.class;
                    }

                    @Override
                    public void onError(Error error) throws IOException {
                        System.out.println("this is an error");
                        System.out.println(error);
                    }

                    @Override
                    public void onSuccess(Bid response) throws IOException {
                        System.out.println(response);
                    }
                });
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}

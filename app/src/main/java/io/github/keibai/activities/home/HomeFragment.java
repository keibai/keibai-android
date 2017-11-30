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
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import io.github.keibai.models.meta.Msg;
import okhttp3.Call;


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

        Button fooButton = view.findViewById(R.id.button_home_foo);
        fooButton.setOnClickListener(new Foo());

        Button addButton = view.findViewById(R.id.button_home_add_credit);
        addButton.setOnClickListener(new AddCredit());

        Button barButton= view.findViewById(R.id.button_home_bar);
        barButton.setOnClickListener(new Bar());

        return view;
    }

    private class Foo implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            User user = new User();
            user.email = "zurfyx@gmail.com";
            user.password = "1234";

            new Http(getContext()).post("https://keibai.herokuapp.com/users/authenticate", user, new HttpCallback<Msg>() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("HTTP Error");
                    System.out.println(e.toString());
                }

                @Override
                public Class<Msg> model() {
                    return Msg.class;
                }

                @Override
                public void onError(Error error) throws IOException {
                    System.out.println("this is an error");
                    System.out.println(error);
                }

                @Override
                public void onSuccess(Msg response) throws IOException {
                    System.out.println(response);
                }
            });
        }
    }

    private class AddCredit implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            new Http(getContext()).post("https://keibai.herokuapp.com/bids/new", new Bid(), new HttpCallback<Bid>() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("HTTP Error");
                    System.out.println(e.toString());
                }

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
        }
    }

    private class Bar implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            new Http(getContext()).post("https://keibai.herokuapp.com/users/deauthenticate", null, new HttpCallback<Msg>() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("HTTP Error");
                    System.out.println(e.toString());
                }

                @Override
                public Class<Msg> model() {
                    return Msg.class;
                }

                @Override
                public void onError(Error error) throws IOException {
                    System.out.println("this is an error");
                    System.out.println(error);
                }

                @Override
                public void onSuccess(Msg response) throws IOException {
                    System.out.println(response);
                }
            });
        }
    }
}

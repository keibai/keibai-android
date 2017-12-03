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

import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.MainFragmentAbstract;
import io.github.keibai.R;
import io.github.keibai.activities.credit.CreditActivity;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;

import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends MainFragmentAbstract {

    private View view;

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
        view = super.onCreateView(inflater, container, savedInstanceState);

        Button addButton = view.findViewById(R.id.button_home_add_credit);
        addButton.setOnClickListener(new AddCredit());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchUser();
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
        new Http(getContext()).get(HttpUrl.userWhoami(), new HttpCallback<User>(User.class) {

            @Override
            public void onError(Error error) throws IOException {
                if (getActivity() ==  null) {
                    return;
                }
                getActivity().runOnUiThread(new RunnableToast(getContext(), error.toString()));
            }

            @Override
            public void onSuccess(final User user) throws IOException {
                if (getActivity() ==  null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        renderUser(user);
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
}

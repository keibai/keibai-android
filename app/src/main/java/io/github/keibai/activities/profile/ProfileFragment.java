package io.github.keibai.activities.profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.MainFragmentAbstract;
import io.github.keibai.R;
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
public class ProfileFragment extends MainFragmentAbstract {

    private View view;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        fetchUser();
    }

    public void renderUser(User user) {
        TextView name = view.findViewById(R.id.text_profile_name);
        TextView lastName = view.findViewById(R.id.text_profile_last_name);
        TextView email = view.findViewById(R.id.text_profile_email);

        name.setText(user.name);
        lastName.setText(user.lastName);
        email.setText(user.email);

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

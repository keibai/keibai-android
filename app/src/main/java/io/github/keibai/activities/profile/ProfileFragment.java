package io.github.keibai.activities.profile;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import io.github.keibai.R;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import io.github.keibai.utils.Gravatar;
import okhttp3.Call;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends ProfileMenuFragmentAbstract {

    private View view;
    private Http http;

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

        if (http == null) {
            http = new Http(getContext());
        }
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

    @Override
    public void onStop() {
        super.onStop();

        http.close();
    }

    public void renderUser(User user) {
        ImageView avatarView = view.findViewById(R.id.image_profile_avatar);
        TextView name = view.findViewById(R.id.text_profile_name);
        TextView email = view.findViewById(R.id.text_profile_email);
        TextView creation_date = view.findViewById(R.id.creation_date);

        name.setText(user.name + " " + user.lastName);
        email.setText(user.email);


        long now = System.currentTimeMillis();
        CharSequence friendlyTimestamp = DateUtils.getRelativeTimeSpanString(
                user.createdAt.getTime(), now, DateUtils.DAY_IN_MILLIS);
        creation_date.setText(friendlyTimestamp);

        if (user.email != null) {
            String gravatarUrl = new Gravatar(user.email).setSize(R.dimen.profile_gravatar_size).generateUrl();
            Picasso.with(getContext()).load(gravatarUrl).into(avatarView);
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
                getActivity().runOnUiThread(new RunnableToast(getContext(), e.toString()));
            }
        });
    }
}

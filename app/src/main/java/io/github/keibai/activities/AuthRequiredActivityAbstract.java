package io.github.keibai.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.welcome.WelcomeActivity;
import io.github.keibai.http.Http;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.http.HttpUrl;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import io.github.keibai.runnable.RunnableToast;
import okhttp3.Call;

public abstract class AuthRequiredActivityAbstract extends AppCompatActivity {

    /**
     * Check if signed or move the user to the welcome page.
     */
    private Http http;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (http == null) {
            http = new Http(getApplicationContext());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        http.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Quick pre-check. If no user id saved we can assume the user is not authenticated.
        if (SaveSharedPreference.getUserId(getApplicationContext()) == -1) {
            redirectToWelcomePage();
            return;
        }

        // Check signed in status remotely.
        // @See remoteCheck()
        http.get(HttpUrl.userWhoami(), new HttpCallback<User>(User.class) {
            @Override
            public void onError(Error error) throws IOException {
                runOnUiThread(new RunnableToast(getApplicationContext(), error.toString()));
            }

            @Override
            public void onSuccess(User user) throws IOException {
                remoteCheck(user);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new RunnableToast(getApplicationContext(), e.toString()));
            }
        });
    }

    /**
     * User session might have expired. If so, remove the userId from the Shared Storage and move
     * the user to the welcome page.
     * @param user
     */
    public void remoteCheck(User user) {
        if (user.id == 0) {
            SaveSharedPreference.setUserId(getApplicationContext(), -1);
            redirectToWelcomePage();
        }
    }

    public void redirectToWelcomePage() {
        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

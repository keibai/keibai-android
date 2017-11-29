package io.github.keibai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.io.IOException;

import io.github.keibai.activities.welcome.WelcomeActivity;
import io.github.keibai.http.Http;
import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.activity.ActivityFragment;
import io.github.keibai.activities.home.HomeFragment;
import io.github.keibai.activities.profile.ProfileFragment;
import io.github.keibai.http.HttpCallback;
import io.github.keibai.models.Bid;
import io.github.keibai.models.User;
import io.github.keibai.models.meta.Error;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SaveSharedPreference.getUserId(getApplicationContext()) == -1) {
            // Not signed in. Go to welcome screen.
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        // Display main activity
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottombar_main);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Display the first fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame_layout, HomeFragment.newInstance());
        transaction.commit();

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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = HomeFragment.newInstance();
                    break;
                case R.id.navigation_activities:
                    selectedFragment = ActivityFragment.newInstance();
                    break;
                case R.id.navigation_profile:
                    selectedFragment = ProfileFragment.newInstance();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame_layout, selectedFragment);
            transaction.commit();
            return true;
        }
    };
}

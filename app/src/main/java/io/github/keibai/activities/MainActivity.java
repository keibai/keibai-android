package io.github.keibai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import io.github.keibai.activities.welcome.WelcomeActivity;
import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.activities.activity.ActivityFragment;
import io.github.keibai.activities.home.HomeFragment;
import io.github.keibai.activities.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (SaveSharedPreference.getUserId(getApplicationContext()) == -1) {
//            // Not signed in. Go to welcome screen.
//            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            return;
//        }

        // Display main activity
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottombar_main);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Display the first fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame_layout, HomeFragment.newInstance());
        transaction.commit();
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

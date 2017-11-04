package io.github.keibai;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import io.github.keibai.activity.ActivitiesFragment;
import io.github.keibai.event.ActiveEventsActivity;
import io.github.keibai.event.CreateEventActivity;
import io.github.keibai.home.HomeFragment;
import io.github.keibai.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SaveSharedPreference.getUserId(MainActivity.this) != -1) {
            // Display main activity
            setContentView(R.layout.activity_main);

            Toolbar toolbar = findViewById(R.id.toolbar_main);
            setSupportActionBar(toolbar);

            BottomNavigationView navigation = findViewById(R.id.bottombar_main);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            // Display the first fragment
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame_layout, HomeFragment.newInstance());
            transaction.commit();
        } else {
            // Change to welcome screen
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_navbar_main_create:
                Intent intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                startActivity(intent);
                return true;
            case R.id.item_navbar_main_search:
                Intent activeEventsIntent = new Intent(getApplicationContext(),
                        ActiveEventsActivity.class);
                startActivity(activeEventsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                    selectedFragment = ActivitiesFragment.newInstance();
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

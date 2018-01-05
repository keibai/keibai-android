package io.github.keibai.activities.auction;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import io.github.keibai.R;
import io.github.keibai.SaveSharedPreference;
import io.github.keibai.models.Auction;
import io.github.keibai.models.Event;

public class DetailAuctionActivity extends AppCompatActivity {

    private Auction auction;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_auction);

        event = SaveSharedPreference.getCurrentEvent(getApplicationContext());
        auction = SaveSharedPreference.getCurrentAuction(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar_detail_auction);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(auction.name);

        ViewPager viewPager = findViewById(R.id.viewpager_detail_auction);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs_detail_auction);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (event.auctionType.equals(Event.ENGLISH)) {
            adapter.addFragment(new DetailAuctionBidFragment(), getString(R.string.bid));
        } else if (event.auctionType.equals(Event.COMBINATORIAL)) {
            adapter.addFragment(new DetailAuctionCombinatorialBidFragment(), getString(R.string.bid));
        }
        adapter.addFragment(new DetailAuctionTransactionsFragment(), getString(R.string.transactions));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }
}

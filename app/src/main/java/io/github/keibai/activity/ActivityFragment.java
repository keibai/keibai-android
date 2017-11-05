package io.github.keibai.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.keibai.MainFragmentAbstract;
import io.github.keibai.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityFragment extends MainFragmentAbstract {

    public ActivityFragment() {
        // Constructor required by Android.
        super(R.layout.fragment_activity);
    }

    public static ActivityFragment newInstance() {
        ActivityFragment fragment = new ActivityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.viewpager_activity);
        setupViewPager(viewPager);

        TabLayout tabLayout = view.findViewById(R.id.tabs_activity);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ActivityFragment.ViewPagerAdapter adapter = new ActivityFragment.ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new ActivityBidFragment(), getString(R.string.bid));
        adapter.addFragment(new ActivityWonFragment(), getString(R.string.won));
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

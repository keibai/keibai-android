package io.github.keibai.activities;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
public class ActivitiesFragment extends MainFragmentAbstract {

    public ActivitiesFragment() {
        // Constructor required by Android.
        super(R.layout.fragment_activities);
    }

    public static ActivitiesFragment newInstance() {
        ActivitiesFragment fragment = new ActivitiesFragment();
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

        ViewPager viewPager = view.findViewById(R.id.viewpager_activities);
        setupViewPager(viewPager);

        TabLayout tabLayout = view.findViewById(R.id.tabs_activities);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ActivitiesFragment.ViewPagerAdapter adapter = new ActivitiesFragment.ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new ActivitiesBidFragment(), getString(R.string.bid));
        adapter.addFragment(new ActivitiesWonFragment(), getString(R.string.won));
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

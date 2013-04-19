
package com.dympy.endless.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dympy.endless.R;
import com.dympy.endless.home.workspace.Workspace;

public class Launcher extends FragmentActivity {

    private SectionsPagerAdapter workspaceScreenAdapter;
    private ViewPager workspaceScreens;
    private LauncherModel application;

    private static String TAG = "WORKSPACE_DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        application = (LauncherModel) getApplication();

        workspaceScreenAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        PagerTabStrip workspaceScreenTabs = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        workspaceScreenTabs.setTabIndicatorColor(0x3B3B3B);
        workspaceScreenTabs.setDrawFullUnderline(true);

        workspaceScreens = (ViewPager) findViewById(R.id.pager);
        workspaceScreens.setAdapter(workspaceScreenAdapter);
        workspaceScreens.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: Lookup how to add items to the action bar
        getMenuInflater().inflate(R.menu.launcher, menu);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    // TODO: The actual social fragment
                    Log.d(TAG, "Always the Social fragment");
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
                    fragment.setArguments(args);
                    return fragment;
                default:
                    Log.d(TAG, "A screen fragment");
                    return application.getWorkspace(position - 1);

            }
        }

        @Override
        public int getCount() {
            return application.screenCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // TODO: The way this is handled right now is still dirty, I mean,
            // even Skrillex makes cleaner songs..
            switch (position) {
                case 0:
                    return getString(R.string.title_social);
                default:
                    return ((Workspace) application.getWorkspace(position - 1)).getWorkspaceName();
            }
        }
    }

    public static class DummySectionFragment extends Fragment {
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_launcher_dummy, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                dummyTextView.setText("Social Screen placeholder");
            } else {
                dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            }
            return rootView;
        }
    }

}

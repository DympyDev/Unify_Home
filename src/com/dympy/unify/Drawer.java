package com.dympy.unify;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.dympy.unify.model.AppData;
import com.dympy.unify.view.AppAdapter;

import java.util.ArrayList;

public class Drawer extends FragmentActivity {
    private LauncherApplication application;
    private ArrayList<AppGrid> appGrids = new ArrayList<AppGrid>();
    private ViewPager drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        getActionBar().setIcon(R.drawable.ab_unify);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        application = (LauncherApplication) getApplication();

        int totalApps = getResources().getInteger(R.integer.drawer_app_columns) * getResources().getInteger(R.integer.drawer_app_rows);
        int i = 1;
        for (; i <= application.getApps().size(); i++) {
            if (i % totalApps == 0) {
                AppGrid temp = new AppGrid();
                Bundle args = new Bundle();
                args.putInt("from", (i - totalApps));
                args.putInt("to", i);
                temp.setArguments(args);
                appGrids.add(temp);
            }
        }

        AppGrid temp = new AppGrid();
        Bundle args = new Bundle();
        args.putInt("from", (appGrids.size() * totalApps));
        args.putInt("to", (i - 1));
        temp.setArguments(args);
        appGrids.add(temp);

        DrawerPagerAdapter adapter = new DrawerPagerAdapter(getSupportFragmentManager());
        drawer = (ViewPager) findViewById(R.id.drawer_pager);
        drawer.setOffscreenPageLimit(appGrids.size());
        drawer.setAdapter(adapter);
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class DrawerPagerAdapter extends FragmentStatePagerAdapter {

        public DrawerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return appGrids.get(position);
        }

        @Override
        public int getCount() {
            return appGrids.size();
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.zoom_enter, R.animator.zoom_exit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class AppGrid extends Fragment {
        private ArrayList<AppData> apps = new ArrayList<AppData>();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            int from = getArguments().getInt("from");
            int to = getArguments().getInt("to");

            LauncherApplication app = (LauncherApplication) getApplication();
            for (int i = 0; i < app.getApps().size(); i++) {
                if (i >= from && i < to) {
                    apps.add(app.getApps().get(i));
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_appgrid, container, false);

            GridView appGrid = (GridView) rootView.findViewById(R.id.activity_drawer_appgrid);
            appGrid.setAdapter(new AppAdapter(getActivity(), R.layout.list_item_app, apps));
            appGrid.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    startActivity(apps.get(position).getIntent());
                }
            });
            return rootView;
        }
    }

}

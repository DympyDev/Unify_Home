package com.dympy.unify.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dympy.unify.Launcher;

/**
 * Created by Dymion on 14-7-13.
 */
public class WorkspaceAdapter extends FragmentStatePagerAdapter {
    private Launcher instance;

    public WorkspaceAdapter(FragmentManager fm, Launcher launcher) {
        super(fm);
        this.instance = launcher;
    }

    @Override
    public Fragment getItem(int i) {
        return instance.workspaceFragments.get(i);
    }

    @Override
    public int getCount() {
        return instance.workspaceFragments.size();
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return instance.workspaceFragments.get(position).getScreenTitle();
    }
}

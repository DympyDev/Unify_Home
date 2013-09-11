package com.dympy.unify.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.dympy.unify.LauncherApplication;
import com.dympy.unify.R;
import com.dympy.unify.model.Item;
import com.dympy.unify.view.custom.MultiColumnList.MultiColumnListView;
import com.dympy.unify.view.custom.MultiColumnList.internal.PLA_AbsListView;

import java.util.ArrayList;

public class WorkspaceFragment extends Fragment {
    private ArrayList<Item> items = new ArrayList<Item>();
    private String screenTitle = "";
    private int workspaceID = -1;
    private ItemAdapter adapter = null;
    private ListView listView;
    private MultiColumnListView gridView;

    public WorkspaceFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workspaceID = getArguments().getInt("screen_id");
        LauncherApplication app = (LauncherApplication) getActivity().getApplication();
        items = app.getScreen(workspaceID).getItems();
        screenTitle = app.getScreen(workspaceID).getName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("WorkspaceFragment", "onCreateView called.");
        //TODO: Set an empty view with fancy arrows to the buttons and stuff
        gridView = new MultiColumnListView(getActivity());
        gridView.setDivider(null);
        gridView.setVerticalScrollBarEnabled(false);

        listView = new ListView(getActivity());
        listView.setDivider(null);
        listView.setVerticalScrollBarEnabled(false);

        adapter = new ItemAdapter(getActivity(), items.toArray(new Item[items.size()]));
        gridView.setAdapter(adapter);
        listView.setAdapter(adapter);

        if (getActivity().findViewById(R.id.launcher_land) != null) {
            Log.d("WorkspaceFragment", "returning gridView");
            return gridView;
        } else {
            Log.d("WorkspaceFragment", "returning listView");
            return listView;
        }
    }

    public String getScreenTitle() {
        return screenTitle;
    }

    public int getWorkspaceID() {
        return workspaceID;
    }

    public ItemAdapter getAdapter() {
        return adapter;
    }
}
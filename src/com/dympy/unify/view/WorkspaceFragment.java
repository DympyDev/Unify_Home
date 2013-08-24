package com.dympy.unify.view;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.dympy.unify.LauncherApplication;
import com.dympy.unify.R;
import com.dympy.unify.model.Item;
import com.dympy.unify.view.ItemAdapter;

import java.util.ArrayList;

public class WorkspaceFragment extends ListFragment {
    private ArrayList<Item> items = new ArrayList<Item>();
    private String screenTitle = "";
    private int workspaceID = -1;
    private ItemAdapter adapter = null;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setPadding(getResources().getDimensionPixelOffset(R.dimen.workspace_padding), 0,
                getResources().getDimensionPixelOffset(R.dimen.workspace_padding), 0);
        getListView().setDivider(null);
        getListView().setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.item_workspace_divider));
        getListView().setVerticalScrollBarEnabled(false);
        //TODO: Set an empty view with fancy arrows to the buttons and stuff
        if (getListAdapter() != null) {
            setListAdapter(null);
        }
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        View header = new View(getActivity());
        header.setBackground(null);
        header.setLayoutParams(layoutParams);
        getListView().addHeaderView(header);

        View footer = new View(getActivity());
        footer.setBackground(null);
        footer.setLayoutParams(layoutParams);
        getListView().addFooterView(footer);

        adapter = new ItemAdapter(getActivity(), items.toArray(new Item[items.size()]));
        setListAdapter(adapter);
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
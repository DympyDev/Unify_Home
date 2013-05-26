package com.dympy.endless.screen;

import java.util.ArrayList;

import com.dympy.endless.LauncherApplication;
import com.dympy.endless.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class Screen {

    private int screenID;
    private String name;
    private int position;
    private ArrayList<ScreenItem> items;
    private ScreenFragment screenContent;

    public Screen() {
        items = new ArrayList<ScreenItem>();
        screenContent = null;
    }

    public int getScreenID() {
        return screenID;
    }

    public void setScreenID(int screenID) {
        this.screenID = screenID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<ScreenItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ScreenItem> items) {
        this.items = items;
    }

    public void addItem(ScreenItem item) {
        this.items.add(item);
    }

    public void removeItem(ScreenItem item) {
        this.items.remove(item);
    }

    public Fragment getView() {
        if (screenContent == null) {
            screenContent = new ScreenFragment();
            screenContent.setWorkspaceID(getScreenID());
        }
        return screenContent;
    }

    public void updateContent(Screen screen) {
        this.position = screen.getPosition();
        this.name = screen.getName();
        this.items = screen.getItems();
    }

    @SuppressLint("ValidFragment")
    private class ScreenFragment extends Fragment {
        private int workspaceID = 0;
        private LauncherApplication application;

        public ScreenFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            application = (LauncherApplication) getActivity().getApplication();
            View rootView = inflater.inflate(R.layout.fragment_workspace, container, false);

            TextView paddingBottom = new TextView(this.getActivity());
            paddingBottom.setHeight(getResources().getDimensionPixelOffset(R.dimen.workspace_padding_bottom));

            ListView workspaceItems = (ListView) rootView.findViewById(R.id.fragment_workspace_list);

            Screen items = application.getScreen(workspaceID);
            ScreenItemAdapter workspaceAdapter = new ScreenItemAdapter(this.getActivity(), R.layout.list_item_workspace, items.getItems());

            workspaceItems.addFooterView(paddingBottom);

            workspaceItems.setAdapter(workspaceAdapter);
            return rootView;
        }

        public void setWorkspaceID(int id) {
            this.workspaceID = id;
        }

    }

}

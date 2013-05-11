package com.dympy.endless.home.workspace;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.dympy.endless.R;
import com.dympy.endless.home.LauncherModel;

public class Workspace extends Fragment {
	private int workspaceID = 0;
	private LauncherModel application;

	public Workspace() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		application = (LauncherModel) getActivity().getApplication();
		View rootView = inflater.inflate(R.layout.fragment_workspace,
				container, false);

		TextView paddingTop = new TextView(this.getActivity());
		paddingTop.setHeight(getResources().getDimensionPixelOffset(
				R.dimen.workspace_padding_top));
		TextView paddingBottom = new TextView(this.getActivity());
		paddingBottom.setHeight(getResources().getDimensionPixelOffset(
				R.dimen.workspace_padding_bottom));

		ListView workspaceItems = (ListView) rootView
				.findViewById(R.id.fragment_workspace_list);

		WorkspaceScreen items = application.getWorkspace(workspaceID);
		WorkspaceItemAdapter workspaceAdapter = new WorkspaceItemAdapter(
				this.getActivity(), R.layout.list_item_workspace,
				items.getItems());

		workspaceItems.addHeaderView(paddingTop);
		workspaceItems.addFooterView(paddingBottom);

		workspaceItems.setAdapter(workspaceAdapter);
		return rootView;
	}

	public int getWorkspaceID() {
		return workspaceID;
	}

	public void setWorkspaceID(int id) {
		this.workspaceID = id;
	}

	public void refreshContent() {
		ListView workspaceItems = (ListView) getView().findViewById(
				R.id.fragment_workspace_list);

		WorkspaceScreen items = application.getWorkspace(workspaceID);
		WorkspaceItemAdapter workspaceAdapter = new WorkspaceItemAdapter(
				this.getActivity(), R.layout.list_item_workspace,
				items.getItems());

		workspaceItems.setAdapter(workspaceAdapter);
	}

}

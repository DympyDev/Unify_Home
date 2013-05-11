package com.dympy.endless.home.workspace;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

public class WorkspaceScreen {

	private int screenID;
	private String screenName;
	private ArrayList<WorkspaceItem> items;
	private Workspace screenContent;

	public WorkspaceScreen() {
		items = new ArrayList<WorkspaceItem>();
		screenContent = null;
	}

	public int getScreenID() {
		return screenID;
	}

	public void setScreenID(int screenID) {
		this.screenID = screenID;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public ArrayList<WorkspaceItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<WorkspaceItem> items) {
		this.items = items;
	}

	public void addItem(WorkspaceItem item) {
		this.items.add(item);
	}

	public void removeItem(WorkspaceItem item) {
		this.items.remove(item);
	}

	public Fragment getView() {
		if (screenContent == null) {
			screenContent = new Workspace();
			screenContent.setWorkspaceID(getScreenID());
		}
		return screenContent;
	}

	public void refreshContent() {
		if (screenContent != null) {
			screenContent.refreshContent();
		}
	}

}

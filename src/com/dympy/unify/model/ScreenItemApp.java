package com.dympy.unify.model;

public class ScreenItemApp {
	private int itemID;
	private String name;
	private String packageName;
	private String activityName;
	private int position;
	private AppData appData;

	public ScreenItemApp() {

	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public AppData getAppData() {
		return appData;
	}

	public void setAppData(AppData appData) {
		this.appData = appData;
	}
}

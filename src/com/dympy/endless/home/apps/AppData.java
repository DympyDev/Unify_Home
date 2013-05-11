package com.dympy.endless.home.apps;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppData {

	private String appName;
	private String packageName;
	private String activityName;

	private Intent appIntent;
	private Drawable appIcon;

	public AppData() {

	}

	public AppData(String appName, String packageName, Intent appIntent,
			Drawable appIcon) {
		this.appName = appName;
		this.packageName = packageName;
		this.appIntent = appIntent;
		this.appIcon = appIcon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
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

	public Intent getAppIntent() {
		return appIntent;
	}

	public void setAppIntent(Intent appIntent) {
		this.appIntent = appIntent;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
}

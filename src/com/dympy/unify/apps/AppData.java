package com.dympy.unify.apps;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppData {

	private String name;
	private String packageName;
	private String activityName;

	private Intent intent;
	private Drawable icon;

	public AppData() {

	}

	public AppData(String name, String packageName, Intent intent,
			Drawable icon) {
		this.name = name;
		this.packageName = packageName;
		this.intent = intent;
		this.icon = icon;
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

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}

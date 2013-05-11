package com.dympy.endless.home;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.dympy.endless.home.apps.AppData;
import com.dympy.endless.home.workspace.WorkspaceItem;
import com.dympy.endless.home.workspace.WorkspaceScreen;

public class LauncherModel extends Application {

	// private static String TAG = "LAUNCHERMODEL_DEBUG";

	private ArrayList<AppData> appsArray;
	private List<AppWidgetProviderInfo> widgetsArray;
	private ArrayList<WorkspaceScreen> workspaceScreens;
	public AppWidgetManager widgetManager;
	public boolean hasLoadedApps = false;
	private Boolean firstTime = null;
	private DatabaseHandler db;

	@Override
	public void onCreate() {
		initVars();
		populateApps();
		populateWidgets();

		populateWorkspaces();
		// TODO: Add the broadcast receiver for new or removed apps

		super.onCreate();
	}

	/*
	 * Initializing functions
	 */
	private void initVars() {
		appsArray = new ArrayList<AppData>();
		workspaceScreens = new ArrayList<WorkspaceScreen>();
		db = new DatabaseHandler(this);
	}

	/*
	 * Populate functions
	 */
	private void populateApps() {
		final PackageManager pm = getPackageManager();
		List<ResolveInfo> packages = pm.queryIntentActivities(
				new Intent(Intent.ACTION_MAIN, null)
						.addCategory(Intent.CATEGORY_LAUNCHER), 0);
		for (ResolveInfo info : packages) {
			AppData temp = new AppData();
			temp.setAppName(info.loadLabel(pm).toString());
			temp.setAppIcon(info.loadIcon(pm));
			temp.setPackageName(info.activityInfo.applicationInfo.packageName);
			temp.setActivityName(info.activityInfo.name);

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			intent.setComponent(new ComponentName(
					info.activityInfo.applicationInfo.packageName,
					info.activityInfo.name));
			temp.setAppIntent(intent);
			addApp(temp);
		}
		sortApps();
		hasLoadedApps = true;
	}

	private void populateWorkspaces() {
		workspaceScreens = db.getWorkspaces();

		if (isFirstTime()) {
			WorkspaceScreen mainScreen = new WorkspaceScreen();
			mainScreen.setScreenID(0);
			mainScreen.setScreenName("Main");

			WorkspaceItem standard = new WorkspaceItem(WorkspaceItem.Type.APPS);
			standard.setWorkspaceID(0);
			standard.setItemTitle("Standard");
			// TODO: Define default apps
			// Add the contacts app to this workspaceItem:
			AppData contacts = getApp("com.android.contacts", "");
			if (contacts != null) {
				standard.addApp(contacts);
			}
			// Add the email app to this workspaceItem:
			AppData email = getApp("com.google.android.email", "");
			if (email != null) {
				standard.addApp(email);
			}
			// Add the gallery app to this workspaceItem:
			AppData gallery = getApp("com.google.android.gallery3d", "");
			if (gallery != null) {
				standard.addApp(gallery);
			}

			mainScreen.addItem(standard);
			addWorkspaceScreen(mainScreen);
			workspaceScreens = db.getWorkspaces();
		}
	}

	private void populateWidgets() {
		widgetManager = AppWidgetManager.getInstance(this);
		widgetsArray = widgetManager.getInstalledProviders();
		sortWidgets();
	}

	/*
	 * App functions
	 */
	public void addApp(AppData app) {
		appsArray.add(app);
	}

	public ArrayList<AppData> getApps() {
		return appsArray;
	}

	public void sortApps() {
		Collections.sort(appsArray, new AppComporator());
	}

	public AppData getApp(String packageName, String activityName) {
		for (int i = 0; i < appsArray.size(); i++) {
			if (appsArray.get(i).getPackageName().equals(packageName)
					&& appsArray.get(i).getActivityName().equals(activityName)) {
				return appsArray.get(i);
			}
		}
		return null;
	}

	public class AppComporator implements Comparator<AppData> {
		@SuppressLint("DefaultLocale")
		@Override
		public int compare(AppData o1, AppData o2) {
			return (o1.getAppName().toLowerCase()).compareTo(o2.getAppName()
					.toLowerCase());
		}
	}

	/*
	 * Widget functions
	 */

	public List<AppWidgetProviderInfo> getWidgets() {
		return widgetsArray;
	}

	public void sortWidgets() {
		Collections.sort(widgetsArray, new WidgetComporator());
	}

	public class WidgetComporator implements Comparator<AppWidgetProviderInfo> {
		@SuppressLint("DefaultLocale")
		@Override
		public int compare(AppWidgetProviderInfo o1, AppWidgetProviderInfo o2) {
			return (o1.label.toLowerCase()).compareTo(o2.label.toLowerCase());
		}
	}

	/*
	 * WorkspaceItem functions
	 */
	public void addAppToItem(WorkspaceItem item, AppData app) {
		item.addApp(app);
		db.addAppItem(item.getWorkspaceID(), item.getItemTitle(),
				app.getAppName(), app.getPackageName(), app.getActivityName());
	}

	public void removeAppFromItem(WorkspaceItem item, AppData app) {
		item.removeApp(app);
		db.deleteAppItem(item.getWorkspaceID(), item.getItemTitle(),
				app.getPackageName(), app.getAppName());
	}

	/*
	 * WorkspaceScreen functions
	 */
	public WorkspaceScreen getWorkspace(int screenID) {
		return workspaceScreens.get(screenID);
	}

	public int getWorkspaceScreenSize() {
		return workspaceScreens.size();
	}

	public void addItemToWorkspace(String title, WorkspaceItem.Type type,
			int screenID) {
		WorkspaceItem temp = new WorkspaceItem(this);
		temp.setWorkspaceID(screenID - 1);
		temp.setItemTitle(title);
		temp.setItemType(type);
		workspaceScreens.get(screenID - 1).addItem(temp);
		db.addWorkspaceItem(temp);
		workspaceScreens.get(screenID - 1).refreshContent();
	}

	public void removeItemFromScreen(WorkspaceItem item) {
		workspaceScreens.get(item.getWorkspaceID()).removeItem(item);
		db.deleteWorkspaceItem(item);
		workspaceScreens.get(item.getWorkspaceID()).refreshContent();
	}

	public void renameItem(WorkspaceItem item, String newTitle) {
		db.updateWorkspaceItemName(item, newTitle);
	}

	public void addWorkspaceScreen(WorkspaceScreen temp) {
		workspaceScreens.add(temp);
		db.addWorkspaceScreen(temp);
	}

	/*
	 * Other functions
	 */
	private boolean isFirstTime() {
		if (firstTime == null) {
			SharedPreferences mPreferences = this.getSharedPreferences(
					"first_time", Context.MODE_PRIVATE);
			firstTime = mPreferences.getBoolean("firstTime", true);
			if (firstTime) {
				SharedPreferences.Editor editor = mPreferences.edit();
				editor.putBoolean("firstTime", false);
				editor.commit();
			}
		}
		return firstTime;
	}
}

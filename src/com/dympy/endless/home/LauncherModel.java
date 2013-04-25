
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
import android.support.v4.app.Fragment;
import android.util.Log;

import com.dympy.endless.home.apps.AppData;
import com.dympy.endless.home.workspace.Workspace;
import com.dympy.endless.home.workspace.WorkspaceItem;
import com.dympy.endless.home.workspace.WorkspaceScreen;

public class LauncherModel extends Application {

    private static String TAG = "LAUNCHERMODEL_DEBUG";

    private ArrayList<AppData> appsArray;
    private List<AppWidgetProviderInfo> widgetsArray;
    private ArrayList<Fragment> screenArray;
    private ArrayList<WorkspaceScreen> workspaceScreens;
    public AppWidgetManager widgetManager;
    public int screenCount = 1;
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

    private void initVars() {
        appsArray = new ArrayList<AppData>();
        screenArray = new ArrayList<Fragment>();
        workspaceScreens = new ArrayList<WorkspaceScreen>();
        db = new DatabaseHandler(this);
    }

    private void populateApps() {
        final PackageManager pm = getPackageManager();
        List<ResolveInfo> packages = pm
                .queryIntentActivities(
                        new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                        0);
        for (ResolveInfo info : packages) {
            AppData temp = new AppData();
            temp.setAppName(info.loadLabel(pm).toString());
            temp.setAppIcon(info.loadIcon(pm));
            temp.setPackageName(info.activityInfo.applicationInfo.packageName);
            // info.activityInfo.applicationInfo.className;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.setComponent(new ComponentName(info.activityInfo.applicationInfo.packageName,
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
            // Add the contacts app to this workspaceItem:
            AppData contacts = getApp("com.android.contacts");
            if (contacts != null) {
                standard.addApp(contacts);
            }
            // Add the email app to this workspaceItem:
            AppData email = getApp("com.google.android.email");
            if (email != null) {
                standard.addApp(email);
            }
            // Add the gallery app to this workspaceItem:
            AppData gallery = getApp("com.google.android.gallery3d");
            if (gallery != null) {
                standard.addApp(gallery);
            }

            mainScreen.addItem(standard);
            db.addWorkspaceScreen(mainScreen);
            workspaceScreens = db.getWorkspaces();
        }

        for (int i = 0; i < workspaceScreens.size(); i++) {
            Workspace temp = new Workspace();
            temp.setWorkspaceName(workspaceScreens.get(i).getScreenName());
            temp.setWorkspaceID(workspaceScreens.get(i).getScreenID());

            addWorkspace(temp);
        }
    }

    private void populateWidgets() {
        widgetManager = AppWidgetManager.getInstance(this);
        widgetsArray = widgetManager.getInstalledProviders();
        sortWidgets();
    }

    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time",
                    Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;
    }

    /*
     * The Apps stuff
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

    public class AppComporator implements Comparator<AppData> {
        @SuppressLint("DefaultLocale")
        @Override
        public int compare(AppData o1, AppData o2) {
            return (o1.getAppName().toLowerCase()).compareTo(o2.getAppName().toLowerCase());
        }
    }

    public AppData getApp(String packageName) {
        for (int i = 0; i < appsArray.size(); i++) {
            Log.d(TAG, "Search package: '" + packageName + "', found package: '"
                    + appsArray.get(i).getPackageName() + "'");
            if (appsArray.get(i).getPackageName().equals(packageName)) {
                return appsArray.get(i);
            }
        }
        return null;
    }

    /*
     * The widgets stuff
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
     * The workspace stuff
     */
    public void addAppToItem(WorkspaceItem item, AppData app) {
        item.addApp(app);
        db.addAppItem(item.getWorkspaceID(), item.getItemTitle(), app.getPackageName());
    }

    public Fragment getWorkspace(int id) {
        return screenArray.get(id);
    }

    public void addWorkspace(Fragment newWorkspace) {
        screenArray.add(newWorkspace);
        screenCount += 1;
    }

    /*
     * The workspace screen stuff
     */
    public WorkspaceScreen getWorkspaceScreen(int screenID) {
        return workspaceScreens.get(screenID);
    }
}


package com.dympy.endless.home;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.dympy.endless.home.apps.AppData;
import com.dympy.endless.home.workspace.Workspace;
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

    @Override
    public void onCreate() {
        initVars();
        populateApps();
        populateWidgets();

        for (AppWidgetProviderInfo widgetInfo : widgetsArray) {
            Log.d(TAG, "Found widget /w label: " + widgetInfo.label);
        }
        
        populateWorkspaces();
        // TODO: Add the broadcast receiver for new or removed apps

        super.onCreate();
    }

    private void initVars() {
        appsArray = new ArrayList<AppData>();
        screenArray = new ArrayList<Fragment>();
        workspaceScreens = new ArrayList<WorkspaceScreen>();
    }

    private void populateApps() {
        // TODO: Make threaded (I think..)
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm
                .getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                AppData temp = new AppData();
                if (packageInfo.loadLabel(pm).toString() == null) {
                    temp.setAppName((String) pm.getText(
                            packageInfo.packageName, packageInfo.labelRes,
                            packageInfo));
                } else {
                    temp.setAppName(packageInfo.loadLabel(pm).toString());
                }
                temp.setPackageName(packageInfo.packageName);
                temp.setAppIcon(pm.getApplicationIcon(packageInfo));
                temp.setAppIntent(pm
                        .getLaunchIntentForPackage(packageInfo.packageName));
                addApp(temp);
            }
        }
        sortApps();
        hasLoadedApps = true;
    }

    private void populateWidgets() {
        widgetManager = AppWidgetManager.getInstance(this);
        widgetsArray = widgetManager.getInstalledProviders();
        sortWidgets();
    }

    private void populateWorkspaces() {
        DatabaseHandler db = new DatabaseHandler(this);
        workspaceScreens = db.getWorkspaces();

        if (workspaceScreens.size() == 0) {
            db.addWorkspaceScreen(0, "Main");
            workspaceScreens = db.getWorkspaces();
        }

        for (int i = 0; i < workspaceScreens.size(); i++) {
            Workspace temp = new Workspace();
            temp.setWorkspaceName(workspaceScreens.get(i).getScreenName());
            temp.setWorkspaceID(workspaceScreens.get(i).getScreenID());

            addWorkspace(temp);
        }
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
            if (appsArray.get(i).getPackageName() == packageName) {
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

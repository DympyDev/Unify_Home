
package com.dympy.endless.home;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.dympy.endless.home.apps.AppData;
import com.dympy.endless.home.workspace.Workspace;
import com.dympy.endless.home.workspace.WorkspaceItem;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.util.Log;

public class LauncherModel extends Application {

    private static String TAG = "LAUNCHERMODEL_DEBUG";

    private ArrayList<AppData> appsArray;
    private ArrayList<Fragment> screenArray;
    private ArrayList<ArrayList<WorkspaceItem>> workspaceScreens;
    public int screenCount = 1;
    public boolean hasLoadedApps = false;

    @Override
    public void onCreate() {
        Log.d(TAG, "In onCreate");
        initVars();
        populateApps();
        populateWorkspaces();
        // TODO: Add the broadcast receiver new or removed apps

        super.onCreate();
    }

    private void initVars() {
        appsArray = new ArrayList<AppData>();
        screenArray = new ArrayList<Fragment>();
        workspaceScreens = new ArrayList<ArrayList<WorkspaceItem>>();
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

    private void populateWorkspaces() {
        // TODO: Retrieve from database or something
        Workspace mainScreen = new Workspace();
        int mainScreenID = addWorkspaceScreen();
        mainScreen.setWorkspaceName("Main");
        mainScreen.setWorkspaceID(mainScreenID);
        Log.d(TAG, "Created mainScreen with id: " + mainScreenID);

        WorkspaceItem firstItem = new WorkspaceItem(WorkspaceItem.Type.APPS);
        firstItem.setItemTitle("Test 1");
        firstItem.addApp(appsArray.get(0));
        firstItem.addApp(appsArray.get(1));
        firstItem.addApp(appsArray.get(2));
        firstItem.addApp(appsArray.get(3));

        addToWorkspaceScreen(mainScreenID, firstItem);

        WorkspaceItem secondItem = new WorkspaceItem(WorkspaceItem.Type.APPS);
        secondItem.setItemTitle("Test 2");
        secondItem.addApp(appsArray.get(4));
        secondItem.addApp(appsArray.get(5));
        secondItem.addApp(appsArray.get(6));
        secondItem.addApp(appsArray.get(7));

        addToWorkspaceScreen(mainScreenID, secondItem);

        WorkspaceItem thirdItem = new WorkspaceItem(WorkspaceItem.Type.APPS);
        thirdItem.setItemTitle("Test 3");
        thirdItem.addApp(appsArray.get(8));
        thirdItem.addApp(appsArray.get(9));
        thirdItem.addApp(appsArray.get(10));
        thirdItem.addApp(appsArray.get(11));

        addToWorkspaceScreen(mainScreenID, thirdItem);

        WorkspaceItem fourthItem = new WorkspaceItem(WorkspaceItem.Type.APPS);
        fourthItem.setItemTitle("Test 4");
        fourthItem.addApp(appsArray.get(12));
        fourthItem.addApp(appsArray.get(13));
        fourthItem.addApp(appsArray.get(14));
        //fourthItem.addApp(appsArray.get(15));
        fourthItem.addApp(appsArray.get(16));
        fourthItem.addApp(appsArray.get(17));

        addToWorkspaceScreen(mainScreenID, fourthItem);

        addWorkspace(mainScreen);

        Workspace mediaScreen = new Workspace();
        int mediaScreenID = addWorkspaceScreen();
        mediaScreen.setWorkspaceName("Media");
        mediaScreen.setWorkspaceID(mediaScreenID);
        Log.d(TAG, "Created mediaScreen with id: " + mediaScreenID);

        addWorkspace(mediaScreen);
        Log.d(TAG, "Created the screens");
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
     * The workspace stuff
     */
    public ArrayList<Fragment> getWorkspaceArray() {
        return screenArray;
    }

    public Fragment getWorkspace(int id) {
        return screenArray.get(id);
    }

    public void addWorkspace(Fragment newWorkspace) {
        screenArray.add(newWorkspace);
        screenCount += 1;
    }

    public int getWorkspaceSize() {
        return screenArray.size();
    }

    /*
     * The workspace screen stuff
     */
    public ArrayList<WorkspaceItem> getWorkspaceScreen(int screenID) {
        return workspaceScreens.get(screenID);
    }

    public void addToWorkspaceScreen(int screenID, WorkspaceItem item) {
        // TODO: A check to see if the WorkspaceScreen has been instantiated
        // (Possibly not needed)
        workspaceScreens.get(screenID).add(item);
    }

    /**
     * Adds a new WorkspaceScreen
     * 
     * @return the id of the newly created WorkspaceScreen
     */
    public int addWorkspaceScreen() {
        ArrayList<WorkspaceItem> tempScreen = new ArrayList<WorkspaceItem>();
        workspaceScreens.add(tempScreen);
        return workspaceScreens.size() - 1;
    }
}

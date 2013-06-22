package com.dympy.unify;

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

import com.dympy.unify.apps.AppData;
import com.dympy.unify.screen.Screen;
import com.dympy.unify.screen.ScreenItem;
import com.dympy.unify.screen.ScreenItemApp;

public class LauncherApplication extends Application {

    //private static String TAG = "LAUNCHERAPPLICATION";

    private ArrayList<AppData> appsArray;
    private List<AppWidgetProviderInfo> widgetsArray;
    private ArrayList<Screen> screenArray;
    public AppWidgetManager widgetManager;
    public boolean hasLoadedApps = false;
    private Boolean firstTime = null;
    private DatabaseHandler db;

    @Override
    public void onCreate() {
        initVars();
        populateApps();
        populateWidgets();

        populateScreens();
        Collections.sort(screenArray, new ScreenComparator());
        for (int i = 0; i < screenArray.size(); i++) {
            Collections.sort(screenArray.get(i).getItems(), new ScreenItemComparator());
        }
        // TODO: Sort screens and their content

        // TODO: Add the broadcast receiver for new or removed apps

        super.onCreate();
    }

    /*
     * Initializing functions
     */
    private void initVars() {
        appsArray = new ArrayList<AppData>();
        screenArray = new ArrayList<Screen>();
        db = new DatabaseHandler(this);
    }

    /*
     * Populate functions
     */
    private void populateApps() {
        final PackageManager pm = getPackageManager();
        List<ResolveInfo> packages = pm.queryIntentActivities(new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER), 0);
        for (ResolveInfo info : packages) {
            AppData temp = new AppData();
            temp.setName(info.loadLabel(pm).toString());
            temp.setIcon(info.loadIcon(pm));
            temp.setPackageName(info.activityInfo.applicationInfo.packageName);
            temp.setActivityName(info.activityInfo.name);

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.setComponent(new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name));
            temp.setIntent(intent);
            addApp(temp);
        }
        sortApps();
        hasLoadedApps = true;
    }

    private void populateWidgets() {
        widgetManager = AppWidgetManager.getInstance(this);
        widgetsArray = widgetManager != null ? widgetManager.getInstalledProviders() : null;
        sortWidgets();
    }

    private void populateScreens() {
        if (isFirstTime()) {
            Screen mainScreen = new Screen();
            mainScreen.setName("Main");
            mainScreen.setPosition(0);

            addScreen(mainScreen);
        }
        screenArray = db.getScreens();
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
        Collections.sort(appsArray, new AppComparator());
    }

    public AppData getApp(String packageName, String activityName) {
        for (AppData anAppsArray : appsArray) {
            if (anAppsArray.getPackageName().equals(packageName) && anAppsArray.getActivityName().equals(activityName)) {
                return anAppsArray;
            }
        }
        return null;
    }

    public class AppComparator implements Comparator<AppData> {
        @SuppressLint("DefaultLocale")
        @Override
        public int compare(AppData o1, AppData o2) {
            return (o1.getName().toLowerCase()).compareTo(o2.getName().toLowerCase());
        }
    }

	/*
     * Widget functions
	 */

    public List<AppWidgetProviderInfo> getWidgets() {
        return widgetsArray;
    }

    public void sortWidgets() {
        Collections.sort(widgetsArray, new WidgetComparator());
    }

    public class WidgetComparator implements Comparator<AppWidgetProviderInfo> {
        @SuppressLint("DefaultLocale")
        @Override
        public int compare(AppWidgetProviderInfo o1, AppWidgetProviderInfo o2) {
            return (o1.label.toLowerCase()).compareTo(o2.label.toLowerCase());
        }
    }

    /*
     * Screen functions
     */
    public Screen getScreen(int screenID) {
        for (int i = 0; i < screenArray.size(); i++) {
            if (screenArray.get(i).getScreenID() == screenID) return screenArray.get(i);
        }
        return null;
    }

    public Screen getScreenByPosition(int position) {
        return screenArray.get(position);
    }

    public int getScreenArraySize() {
        return screenArray.size();
    }

    public int getScreenItemPosition(int screenID) {
        return screenArray.get(screenID - 1).getItems().size();
    }

    public void addScreen(Screen temp) {
        screenArray.add(temp);
        db.addScreen(temp);
    }

    public void removeScreen(Screen temp) {
        for (int i = 0; i < screenArray.size(); i++) {
            if (screenArray.get(i).getScreenID() == temp.getScreenID()) {
                screenArray.remove(i);
            }
        }
        Collections.sort(screenArray, new ScreenComparator());
        for (int i = 0; i < screenArray.size(); i++) {
            screenArray.get(i).setPosition(i);
            updateScreen(screenArray.get(i));
        }
        db.removeScreen(temp);
    }

    public void updateScreen(Screen temp) {
        for (int i = 0; i < screenArray.size(); i++) {
            if (screenArray.get(i).getScreenID() == temp.getScreenID()) screenArray.get(i).updateContent(temp);
        }
        db.updateScreen(temp);
    }

    public class ScreenComparator implements Comparator<Screen> {
        @Override
        public int compare(Screen screen, Screen screen2) {
            return screen.getPosition() > screen2.getPosition() ? +1 : screen.getPosition() < screen2.getPosition() ? -1 : 0;
        }
    }

	/*
     * ScreenItem functions
	 */

    public void addScreenItem(ScreenItem item) {
        getScreen(item.getScreenID()).addItem(item);
        db.addScreenItem(item);
        //getScreen(item.getScreenID()).refreshContent();
    }

    public void removeScreenItem(ScreenItem item) {
        for (int i = 0; i < getScreen(item.getScreenID()).getItems().size(); i++) {
            if (getScreen(item.getScreenID()).getItems().get(i).getItemID() == item.getItemID()) {
                getScreen(item.getScreenID()).getItems().remove(i);
            }
        }
        Collections.sort(getScreen(item.getScreenID()).getItems(), new ScreenItemComparator());
        for (int i = 0; i < getScreen(item.getScreenID()).getItems().size(); i++) {
            getScreen(item.getScreenID()).getItems().get(i).setPosition(i);
            updateScreenItem(getScreen(item.getScreenID()).getItems().get(i));
        }
        db.removeScreenItem(item);
        //getScreen(item.getScreenID()).refreshContent();
    }

    public void updateScreenItem(ScreenItem item) {
        db.updateScreenItem(item);
    }

    public class ScreenItemComparator implements Comparator<ScreenItem> {
        @Override
        public int compare(ScreenItem screen, ScreenItem screen2) {
            return screen.getPosition() > screen2.getPosition() ? +1 : screen.getPosition() < screen2.getPosition() ? -1 : 0;
        }
    }

	/*
     * ScreenItemApp functions
	 */

    public void addScreenItemApp(ScreenItemApp app) {
        db.addScreenItemApp(app);
    }

    public void removeScreenItemApp(ScreenItemApp app) {
        //TODO: Position updating
        db.removeScreenItemApp(app);
    }

    /*
     * Other functions
     */
    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
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

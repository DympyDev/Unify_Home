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
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.dympy.unify.controller.ArrayHelper;
import com.dympy.unify.controller.DatabaseHandler;
import com.dympy.unify.model.AppData;
import com.dympy.unify.model.Favorite;
import com.dympy.unify.model.Item;
import com.dympy.unify.model.Screen;

public class LauncherApplication extends Application {

    private static String TAG = "LAUNCHERAPPLICATION";

    private ArrayList<AppData> appsArray;
    private List<AppWidgetProviderInfo> widgetsArray;
    public AppWidgetManager widgetManager;
    private Boolean firstTime = null;
    private DatabaseHandler db;

    public static ArrayHelper<Screen> SCREENS;
    public static ArrayHelper<Favorite> FAVORITES;

    @Override
    public void onCreate() {
        initVars();
        populateApps();
        populateWidgets();

        populateScreens();
        populateFavorite();
        // TODO: Add the broadcast receiver for new or removed apps

        super.onCreate();
    }

    /*
     * Initializing functions
     */
    private void initVars() {
        appsArray = new ArrayList<AppData>();
        db = new DatabaseHandler(this);

        SCREENS = new ArrayHelper<Screen>();
        FAVORITES = new ArrayHelper<Favorite>();
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
            Drawable icon = info.activityInfo.loadIcon(pm);
            icon.setBounds(
                    0,
                    0,
                    getResources().getDimensionPixelOffset(
                            R.dimen.icon_size), getResources()
                    .getDimensionPixelOffset(R.dimen.icon_size));
            temp.setIcon(icon);
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

            SCREENS.setDB(db);
            SCREENS.add(mainScreen);
        } else {
            SCREENS = db.getScreens();
            SCREENS.setDB(db);
        }
        SCREENS.sort();
        //TODO: Should I do this in a cleaner way?
        for (int i = 0; i < SCREENS.size(); i++) {
            SCREENS.get(i).getItems().sort();
            SCREENS.get(i).getItems().setDB(db);
            for (int j = 0; j < SCREENS.get(i).getItems().size(); j++) {
                SCREENS.get(i).getItems().get(j).getApps().sort();
                SCREENS.get(i).getItems().get(j).getApps().setDB(db);
            }
        }
    }

    private void populateFavorite() {
        FAVORITES = db.getFavorites();
        FAVORITES.sort();
        FAVORITES.setDB(db);
    }

    public void forceReload() {
        ArrayList<AppData> oldApps = appsArray;
        appsArray.clear();
        populateApps();
        //TODO: loop through both arrays and update content where needed (App Drawer, Databases, ScreenItems)
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
    public void addScreen(Screen screen) {
        SCREENS.add(screen);
        SCREENS.get(SCREENS.size() - 1).getItems().setDB(db);
    }

    public Screen getScreen(int screenID) {
        for (int i = 0; i < SCREENS.size(); i++) {
            if (SCREENS.get(i).getScreenID() == screenID) return SCREENS.get(i);
        }
        return null;
    }

    public int getScreenItemPosition(int screenID) {
        return SCREENS.get(screenID).getItems().size();
    }

    public void removeScreen(Screen temp) {
        for (int i = 0; i < SCREENS.size(); i++) {
            if (SCREENS.get(i).getScreenID() == temp.getScreenID()) {
                SCREENS.remove(i);
            }
        }
        SCREENS.sort();
        for (int i = 0; i < SCREENS.size(); i++) {
            SCREENS.get(i).setPosition(i);
            updateScreen(SCREENS.get(i));
        }
    }

    public void updateScreen(Screen temp) {
        for (int i = 0; i < SCREENS.size(); i++) {
            if (SCREENS.get(i).getScreenID() == temp.getScreenID())
                SCREENS.get(i).updateContent(temp);
        }
        db.updateScreen(temp);
    }

    /*
     * Item functions
     */
    public void addScreenItem(Item item) {
        item.getApps().sort();
        item.getApps().setDB(db);
        getScreen(item.getScreenID()).addItem(item);
    }

    public void removeScreenItem(Item item) {
        for (int i = 0; i < getScreen(item.getScreenID()).getItems().size(); i++) {
            if (getScreen(item.getScreenID()).getItems().get(i).getItemID() == item.getItemID()) {
                getScreen(item.getScreenID()).getItems().remove(i);
            }
        }
        getScreen(item.getScreenID()).getItems().sort();
        for (int i = 0; i < getScreen(item.getScreenID()).getItems().size(); i++) {
            getScreen(item.getScreenID()).getItems().get(i).setPosition(i);
            updateScreenItem(getScreen(item.getScreenID()).getItems().get(i));
        }
    }

    public void updateScreenItem(Item item) {
        db.updateScreenItem(item);
    }

    /*
     * Favorite functions
     */
    public Favorite getFavorite(int pos) {
        Favorite fav = null;
        for (int i = 0; i < FAVORITES.size(); i++) {
            if (FAVORITES.get(i).getFavPos() == pos) {
                fav = FAVORITES.get(i);
            }
        }
        return fav;
    }

    public void setFavorite(Favorite newFav) {
        boolean newPos = true;
        for (int i = 0; i < FAVORITES.size(); i++) {
            if (FAVORITES.get(i).getFavPos() == newFav.getFavPos()) {
                newPos = false;
                FAVORITES.set(i, newFav);
            }
        }
        if (newPos) {
            FAVORITES.add(newFav);
        }
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

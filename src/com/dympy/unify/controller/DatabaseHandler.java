package com.dympy.unify.controller;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dympy.unify.LauncherApplication;
import com.dympy.unify.model.AppData;
import com.dympy.unify.model.Favorite;
import com.dympy.unify.model.Screen;
import com.dympy.unify.model.ScreenItem;
import com.dympy.unify.model.ScreenItemApp;
import com.dympy.unify.model.ScreenItem.Type;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;
    private LauncherApplication app;
    /*
     * The Database Version, following is some kind of changelog:
     * V2:  - Added the favorites table
     *      - Changed a lot of functions to work a bit more nicely
     * V1:  - Initial creation of database
     *      - Created the Screens, ScreenItems and ScreenItemApps tables
     *      - Created almost all CRUD functions
     */
    private static final int DATABASE_VERSION = 2;

    // Database name
    private static final String DATABASE_NAME = "endlessDB.db";

    /**
     * Screen Table, contents are defined in the following order:</br> Screen
     * ID, Screen Name, Screen Position
     */
    private static final String TABLE_SCREEN = "screen";
    /**
     * Screen Item Table, contents are defined in the following order:</br> Item
     * ID, Screen ID, Item Name, Item Type, Item Position
     */
    private static final String TABLE_SCREEN_ITEM = "screenItem";
    /**
     * Screen Item App Table, contents are defined in the following order:</br>
     * Item ID, App Name, App Package Name, App Activity Name, App Position
     */
    private static final String TABLE_SCREEN_ITEM_APP = "screenItemApp";
    /**
     * SFavorite Table, contents are defined in the following order:</br>
     * Favorite Position, App Name, App Package Name, App Activity Name
     */
    private static final String TABLE_FAVORITE = "favorite";

    /**
     * Screen ID, INTEGER PRIMARY KEY
     */
    private static final String COL_SCREEN_ID = "screen_id";
    /**
     * Screen Name, TEXT
     */
    private static final String COL_SCREEN_NAME = "screen_name";
    /**
     * Screen Position, INTEGER
     */
    private static final String COL_SCREEN_POSITION = "screen_pos";

    /**
     * Item ID, INTEGER PRIMARY KEY
     */
    private static final String COL_ITEM_ID = "item_id";
    /**
     * Screen ID, INTEGER
     */
    private static final String COL_ITEM_SCREEN = "screen_id";
    /**
     * Item Name, TEXT
     */
    private static final String COL_ITEM_NAME = "item_name";
    /**
     * Item Type, INTEGER
     */
    private static final String COL_ITEM_TYPE = "item_type";
    /**
     * Item Position, INTEGER
     */
    private static final String COL_ITEM_POSITION = "item_pos";

    /**
     * Item ID, INTEGER
     */
    private static final String COL_APP_ITEM = "item_id";
    /**
     * App Name, TEXT
     */
    private static final String COL_APP_NAME = "app_name";
    /**
     * App Package Name, TEXT
     */
    private static final String COL_APP_PACKAGE = "app_package";
    /**
     * App Activity Name, TEXT
     */
    private static final String COL_APP_ACTIVITY = "app_activity";
    /**
     * App Position INTEGER
     */
    private static final String COL_APP_POSITION = "app_pos";

    /**
     * App Position INTEGER
     */
    private static final String COL_FAVORITE_POSITION = "fav_pos";
    /**
     * App Name, TEXT
     */
    private static final String COL_FAVORITE_NAME = "app_name";
    /**
     * App Package Name, TEXT
     */
    private static final String COL_FAVORITE_PACKAGE = "app_package";
    /**
     * App Activity Name, TEXT
     */
    private static final String COL_FAVORITE_ACTIVITY = "app_activity";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        app = (LauncherApplication) context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Workspace Screen Table
        String CREATE_SCREEN_TABLE = "CREATE TABLE " + TABLE_SCREEN + "("
                + COL_SCREEN_ID + " INTEGER PRIMARY KEY," + COL_SCREEN_NAME
                + " TEXT, " + COL_SCREEN_POSITION + " INTEGER)";
        db.execSQL(CREATE_SCREEN_TABLE);

        // Create the Workspace Item Table
        String CREATE_SCREEN_ITEM_TABLE = "CREATE TABLE " + TABLE_SCREEN_ITEM
                + "(" + COL_ITEM_ID + " INTEGER PRIMARY KEY, "
                + COL_ITEM_SCREEN + " INTEGER, " + COL_ITEM_NAME + " TEXT, "
                + COL_ITEM_TYPE + " INTEGER, " + COL_ITEM_POSITION
                + " INTEGER)";
        db.execSQL(CREATE_SCREEN_ITEM_TABLE);

        // Create the App Item Table
        String CREATE_SCREEN_ITEM_APP_TABLE = "CREATE TABLE "
                + TABLE_SCREEN_ITEM_APP + "(" + COL_APP_ITEM + " INTEGER, "
                + COL_APP_NAME + " TEXT, " + COL_APP_PACKAGE + " TEXT, "
                + COL_APP_ACTIVITY + " TEXT," + COL_APP_POSITION + " INTEGER"
                + ")";
        db.execSQL(CREATE_SCREEN_ITEM_APP_TABLE);

        // Create the App Item Table
        String CREATE_FAVORITE_TABLE = "CREATE TABLE "
                + TABLE_FAVORITE + "(" + COL_FAVORITE_POSITION + " INTEGER, "
                + COL_FAVORITE_NAME + " TEXT, " + COL_FAVORITE_PACKAGE + " TEXT, "
                + COL_FAVORITE_ACTIVITY + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVORITE_TABLE);
    }

    // Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ArrayList<Screen> oldScreens = getScreens(db);
        ArrayList<Favorite> oldFavorites = null;
        if (DATABASE_VERSION > 2) {
            oldFavorites = getFavorites(db);
        }

        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN_ITEM_APP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);

        // Create tables again
        onCreate(db);

        //populate tables again
        for (Screen tempScreen : oldScreens) {
            addScreen(tempScreen, db);
        }

        if (oldFavorites != null) {
            for (Favorite tempFav : oldFavorites) {
                addFavorite(tempFav, db);
            }
        }
    }

    /*
     * The Create functions
     */
    public void addScreen(Screen screen) {
        addScreen(screen, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void addScreen(Screen screen, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_SCREEN_NAME, screen.getName());
        values.put(COL_SCREEN_POSITION, screen.getPosition());

        // Inserting Row
        int screenID = (int) db.insert(TABLE_SCREEN, null, values);
        Log.d("DB", "ScreenID: " + screenID);
        screen.setScreenID(screenID);

        // Creating the table entries for the ScreenItems
        for (ScreenItem item : screen.getItems()) {
            item.setScreenID(screenID);
            addScreenItem(item, db);
        }
    }

    public void addScreenItem(ScreenItem item) {
        addScreenItem(item, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void addScreenItem(ScreenItem item, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_SCREEN, item.getScreenID());
        values.put(COL_ITEM_NAME, item.getName());
        values.put(COL_ITEM_TYPE, (item.getType() == Type.APPS ? 0 : 1));
        values.put(COL_ITEM_POSITION, item.getPosition());

        // Inserting Row
        int itemID = (int) db.insert(TABLE_SCREEN_ITEM, null, values);
        item.setItemID(itemID);

        // Creating the table entries for the ScreenItemApps
        for (ScreenItemApp app : item.getApps()) {
            app.setItemID(itemID);
            addScreenItemApp(app, db);
        }
    }

    public void addScreenItemApp(ScreenItemApp app) {
        addScreenItemApp(app, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void addScreenItemApp(ScreenItemApp app, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_APP_ITEM, app.getItemID());
        values.put(COL_APP_NAME, app.getName());
        values.put(COL_APP_PACKAGE, app.getPackageName());
        values.put(COL_APP_ACTIVITY, app.getActivityName());
        values.put(COL_APP_POSITION, app.getPosition());

        // Inserting Row
        db.insert(TABLE_SCREEN_ITEM_APP, null, values);
    }

    public void addFavorite(Favorite fav) {
        addFavorite(fav, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void addFavorite(Favorite fav, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_FAVORITE_POSITION, fav.getFavPos());
        values.put(COL_FAVORITE_NAME, fav.getContent().getName());
        values.put(COL_FAVORITE_PACKAGE, fav.getContent().getPackageName());
        values.put(COL_FAVORITE_ACTIVITY, fav.getContent().getActivityName());

        // Inserting Row
        db.insert(TABLE_FAVORITE, null, values);
    }

    /*
     * The Return functions
     */
    public ArrayList<Screen> getScreens() {
        return getScreens(this.getWritableDatabase());
    }

    public ArrayList<Screen> getScreens(SQLiteDatabase db) {
        ArrayList<Screen> screenList = new ArrayList<Screen>();
        String selectQuery = "SELECT * FROM " + TABLE_SCREEN;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Screen screen = new Screen();
                screen.setScreenID(cursor.getInt(0));
                screen.setName(cursor.getString(1));
                screen.setPosition(cursor.getInt(2));
                screen.setItems(getScreenItems(screen.getScreenID(), db));

                screenList.add(screen);
            } while (cursor.moveToNext());
        }
        return screenList;
    }

    public ArrayList<ScreenItem> getScreenItems(int screenID) {
        return getScreenItems(screenID, this.getWritableDatabase());
    }

    public ArrayList<ScreenItem> getScreenItems(int screenID, SQLiteDatabase db) {
        ArrayList<ScreenItem> screenItemList = new ArrayList<ScreenItem>();
        String selectQuery = "SELECT * FROM " + TABLE_SCREEN_ITEM + " WHERE "
                + COL_ITEM_SCREEN + "=" + screenID;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ScreenItem screenItem = new ScreenItem(context);
                screenItem.setItemID(cursor.getInt(0));
                screenItem.setScreenID(cursor.getInt(1));
                screenItem.setName(cursor.getString(2));
                screenItem.setType((cursor.getInt(3) == 0 ? Type.APPS
                        : Type.WIDGET));
                screenItem.setPosition(cursor.getInt(4));
                screenItem.setApps(getScreenItemApps(screenItem.getItemID(), db));

                screenItemList.add(screenItem);
            } while (cursor.moveToNext());
        }
        return screenItemList;

    }

    public ArrayList<ScreenItemApp> getScreenItemApps(int itemID) {
        return getScreenItemApps(itemID, this.getWritableDatabase());
    }

    public ArrayList<ScreenItemApp> getScreenItemApps(int itemID, SQLiteDatabase db) {
        ArrayList<ScreenItemApp> itemAppList = new ArrayList<ScreenItemApp>();
        String selectQuery = "SELECT * FROM " + TABLE_SCREEN_ITEM_APP
                + " WHERE " + COL_APP_ITEM + "=" + itemID;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ScreenItemApp itemApp = new ScreenItemApp();
                itemApp.setItemID(cursor.getInt(0));
                itemApp.setName(cursor.getString(1));
                itemApp.setPackageName(cursor.getString(2));
                itemApp.setActivityName(cursor.getString(3));
                itemApp.setPosition(cursor.getInt(4));
                itemApp.setAppData(app.getApp(itemApp.getPackageName(),
                        itemApp.getActivityName()));

                itemAppList.add(itemApp);
            } while (cursor.moveToNext());
        }
        return itemAppList;
    }

    public ArrayList<Favorite> getFavorites() {
        return getFavorites(this.getWritableDatabase());
    }

    public ArrayList<Favorite> getFavorites(SQLiteDatabase db) {
        ArrayList<Favorite> favs = new ArrayList<Favorite>();
        String selectQuery = "SELECT * FROM " + TABLE_FAVORITE;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Favorite tempFav = new Favorite();
                tempFav.setFavPos(cursor.getInt(0));
                AppData tempData = new AppData();
                tempData.setName(cursor.getString(1));
                tempData.setPackageName(cursor.getString(2));
                tempData.setActivityName(cursor.getString(3));
                tempFav.setContent(app.getApp(tempData.getPackageName(),
                        tempData.getActivityName()));
                favs.add(tempFav);
            } while (cursor.moveToNext());
        }
        return favs;
    }

    /*
     * The Remove functions
     */
    public void removeScreen(Screen screen) {
        removeScreen(screen, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void removeScreen(Screen screen, SQLiteDatabase db) {
        db.delete(TABLE_SCREEN, COL_SCREEN_ID + " = ?",
                new String[]{String.valueOf(screen.getScreenID())});
        for (ScreenItem item : screen.getItems()) {
            removeScreenItem(item, db);
        }
    }

    public void removeScreenItem(ScreenItem item) {
        removeScreenItem(item, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void removeScreenItem(ScreenItem item, SQLiteDatabase db) {
        db.delete(TABLE_SCREEN_ITEM, COL_ITEM_ID + " = ?",
                new String[]{String.valueOf(item.getItemID())});
        for (ScreenItemApp app : item.getApps()) {
            removeScreenItemApp(app, db);
        }
    }

    public void removeScreenItemApp(ScreenItemApp app) {
        removeScreenItemApp(app, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void removeScreenItemApp(ScreenItemApp app, SQLiteDatabase db) {
        db.delete(TABLE_SCREEN_ITEM_APP, COL_APP_ITEM + "=" + app.getItemID()
                + " AND " + COL_APP_PACKAGE + "='" + app.getPackageName()
                + "' AND " + COL_APP_ACTIVITY + "='" + app.getActivityName()
                + "'", null);
    }

    public void removeFavorite(Favorite fav) {
        removeFavorite(fav, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void removeFavorite(Favorite fav, SQLiteDatabase db) {
        db.delete(TABLE_FAVORITE, COL_FAVORITE_POSITION + "=" + fav.getFavPos(), null);
    }

    /*
     * The Update functions
     */
    public void updateScreen(Screen screen) {
        updateScreen(screen, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void updateScreen(Screen screen, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_SCREEN_NAME, screen.getName());
        values.put(COL_SCREEN_POSITION, screen.getPosition());

        db.update(TABLE_SCREEN, values, COL_SCREEN_ID + " = ?",
                new String[]{String.valueOf(screen.getScreenID())});
    }

    public void updateScreenItem(ScreenItem item) {
        updateScreenItem(item, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void updateScreenItem(ScreenItem item, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_NAME, item.getName());
        values.put(COL_ITEM_TYPE, (item.getType() == Type.APPS ? 0 : 1));
        values.put(COL_ITEM_POSITION, item.getPosition());

        db.update(TABLE_SCREEN_ITEM, values, COL_ITEM_ID + " = ?",
                new String[]{String.valueOf(item.getItemID())});
    }

    public void updateAppItem(ScreenItemApp app) {
        updateAppItem(app, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void updateAppItem(ScreenItemApp app, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_POSITION, app.getPosition());

        db.update(TABLE_SCREEN_ITEM_APP, values,
                COL_APP_ITEM + "=" + app.getItemID() + " AND "
                        + COL_APP_PACKAGE + "='" + app.getPackageName()
                        + "' AND " + COL_APP_ACTIVITY + "='"
                        + app.getActivityName() + "'", null);
    }

    public void updateFavorite(Favorite fav) {
        updateFavorite(fav, this.getWritableDatabase());
        this.getWritableDatabase().close();
    }

    public void updateFavorite(Favorite fav, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_FAVORITE_NAME, fav.getContent().getName());
        values.put(COL_FAVORITE_PACKAGE, fav.getContent().getPackageName());
        values.put(COL_FAVORITE_ACTIVITY, fav.getContent().getActivityName());

        db.update(TABLE_FAVORITE, values, COL_FAVORITE_POSITION + "=" + fav.getFavPos(), null);
    }
}

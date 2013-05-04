
package com.dympy.endless.home;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dympy.endless.home.workspace.WorkspaceItem;
import com.dympy.endless.home.workspace.WorkspaceScreen;

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context context;
    // All Static variables
    // Database version
    private static final int DATABASE_VERSION = 1;

    // Database name
    private static final String DATABASE_NAME = "endlessDB.db";

    // Table name constants
    private static final String TABLE_WORKSPACE_SCREEN = "workspaceScreen";
    private static final String TABLE_WORKSPACE_ITEM = "workspaceItem";
    private static final String TABLE_APP_ITEM = "appItem";

    // Workspace Screen Table Column names
    private static final String KEY_SCREEN_ID = "screen_id";
    private static final String KEY_SCREEN_NAME = "screen_name";

    // Workspace Item Table Column names
    private static final String KEY_ITEM_WORKSPACE_ID = "screen_id";
    private static final String KEY_ITEM_NAME = "item_name";
    private static final String KEY_ITEM_TYPE = "item_type";

    // App Item Table Column names
    private static final String KEY_APP_ITEM_NAME = "item_name";
    private static final String KEY_APP_ITEM_SCREEN = "screen_id";
    private static final String KEY_APP_NAME = "app_name";
    private static final String KEY_APP_PACKAGE = "app_package";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the Workspace Screen Table
        String CREATE_SCREEN_TABLE = "CREATE TABLE " + TABLE_WORKSPACE_SCREEN + "("
                + KEY_SCREEN_ID + " INTEGER PRIMARY KEY," + KEY_SCREEN_NAME + " TEXT" + ")";
        db.execSQL(CREATE_SCREEN_TABLE);

        // Create the Workspace Item Table
        String CREATE_WORKSPACE_ITEM_TABLE = "CREATE TABLE " + TABLE_WORKSPACE_ITEM + "("
                + KEY_ITEM_NAME + " TEXT," + KEY_ITEM_WORKSPACE_ID + " INTEGER," + KEY_ITEM_TYPE
                + " INTEGER" + ")";
        db.execSQL(CREATE_WORKSPACE_ITEM_TABLE);

        // Create the App Item Table
        String CREATE_APP_ITEM_TABLE = "CREATE TABLE " + TABLE_APP_ITEM + "("
                + KEY_APP_ITEM_NAME + " TEXT," + KEY_APP_ITEM_SCREEN + " TEXT," + KEY_APP_NAME
                + " TEXT," + KEY_APP_PACKAGE
                + " TEXT" + ")";
        db.execSQL(CREATE_APP_ITEM_TABLE);
    }

    // Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKSPACE_SCREEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKSPACE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_ITEM);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    void addWorkspaceScreen(int screenID, String screenName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SCREEN_ID, screenID);
        values.put(KEY_SCREEN_NAME, screenName);

        // Inserting Row
        db.insert(TABLE_WORKSPACE_SCREEN, null, values);
        db.close(); // Closing database connection
    }

    void addWorkspaceScreen(WorkspaceScreen screen) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SCREEN_ID, screen.getScreenID());
        values.put(KEY_SCREEN_NAME, screen.getScreenName());

        // Inserting Row
        db.insert(TABLE_WORKSPACE_SCREEN, null, values);
        db.close(); // Closing database connection

        // Add entries for all workspace items
        for (int i = 0; i < screen.getItems().size(); i++) {
            addWorkspaceItem(screen.getItems().get(i));
        }
    }

    void addWorkspaceItem(WorkspaceItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_NAME, item.getItemTitle());
        values.put(KEY_ITEM_WORKSPACE_ID, item.getWorkspaceID());
        values.put(KEY_ITEM_TYPE, ((item.getItemType() == WorkspaceItem.Type.APPS) ? 0 : 1));

        // Inserting Row
        db.insert(TABLE_WORKSPACE_ITEM, null, values);
        db.close(); // Closing database connection

        // Add entries for all apps
        for (int i = 0; i < item.getApps().size(); i++) {
            addAppItem(item.getWorkspaceID(), item.getItemTitle(), item.getApps().get(i)
                    .getPackageName(), item.getApps().get(i).getAppName());
        }
    }

    void addAppItem(int screenID, String itemName, String packageName, String appName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_APP_ITEM_NAME, itemName);
        values.put(KEY_APP_ITEM_SCREEN, screenID);
        values.put(KEY_APP_NAME, appName);
        values.put(KEY_APP_PACKAGE, packageName);

        // Inserting Row
        db.insert(TABLE_APP_ITEM, null, values);
        db.close();
    }

    /*
     * All the get functions
     */
    public ArrayList<WorkspaceScreen> getWorkspaces() {
        Log.d("DB", "Getting workspaces");
        ArrayList<WorkspaceScreen> workspaces = new ArrayList<WorkspaceScreen>();
        String selectQuery = "SELECT * FROM " + TABLE_WORKSPACE_SCREEN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Log.d("DB", "Adding a WorkspaceScreen");
                WorkspaceScreen temp = new WorkspaceScreen();
                temp.setScreenID(cursor.getInt(0));
                temp.setScreenName(cursor.getString(1));
                temp.setItems(getWorkspaceItems(temp.getScreenID()));

                workspaces.add(temp);
            } while (cursor.moveToNext());
        }
        return workspaces;
    }

    public ArrayList<WorkspaceItem> getWorkspaceItems(int screenID) {
        ArrayList<WorkspaceItem> items = new ArrayList<WorkspaceItem>();

        String selectQuery = "SELECT * FROM " + TABLE_WORKSPACE_ITEM + " WHERE "
                + KEY_ITEM_WORKSPACE_ID + "=" + screenID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                WorkspaceItem temp = new WorkspaceItem(context);
                temp.setItemTitle(cursor.getString(0));
                temp.setWorkspaceID(screenID);
                temp.setItemType((cursor.getInt(2) == 0) ? WorkspaceItem.Type.APPS
                        : WorkspaceItem.Type.WIDGET);
                List<String> tempPackages = getWorkspaceItemApps(screenID, temp.getItemTitle());
                for (int i = 0; i < tempPackages.size(); i++) {
                    temp.addApp(((LauncherModel) context).getApp(tempPackages.get(i)));
                }

                items.add(temp);
            } while (cursor.moveToNext());
        }

        return items;
    }

    // TODO: change from List<String> to AppData (The return thingy)
    public List<String> getWorkspaceItemApps(int screenID, String itemName) {
        List<String> packageList = new ArrayList<String>();

        String selectQuery = "SELECT " + KEY_APP_PACKAGE + " FROM " + TABLE_APP_ITEM + " WHERE "
                + KEY_APP_ITEM_NAME + "='" + itemName + "' AND " + KEY_APP_ITEM_SCREEN + "="
                + screenID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                packageList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return packageList;
    }

    /*
     * All the remove functions
     */
    public void deleteWorkspaceScreen(WorkspaceScreen screen) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORKSPACE_SCREEN, KEY_SCREEN_ID + "=" + screen.getScreenID(), null);
        db.close();
        for (int i = 0; i < screen.getItems().size(); i++) {
            deleteWorkspaceItem(screen.getItems().get(i));
        }
    }

    public void deleteWorkspaceItem(WorkspaceItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORKSPACE_ITEM, KEY_ITEM_NAME + "='" + item.getItemTitle() + "' AND "
                + KEY_ITEM_WORKSPACE_ID + "=" + item.getWorkspaceID(), null);
        db.close();
        for (int i = 0; i < item.getApps().size(); i++) {
            deleteAppItem(item.getWorkspaceID(), item.getItemTitle(), item.getApps().get(i)
                    .getPackageName(), item.getApps().get(i).getAppName());
        }
    }

    public void deleteAppItem(int screenID, String itemName, String packageName, String appName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APP_ITEM, KEY_APP_ITEM_SCREEN + "=" + screenID + " AND "
                + KEY_APP_ITEM_NAME + "='" + itemName + "' AND "
                + KEY_APP_NAME + "='" + appName + "' AND "
                + KEY_APP_PACKAGE + "='" + packageName + "'", null);
        db.close();
    }
}

package com.dympy.endless;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dympy.endless.screen.Screen;
import com.dympy.endless.screen.ScreenItem;
import com.dympy.endless.screen.ScreenItemApp;
import com.dympy.endless.screen.ScreenItem.Type;

public class DatabaseHandler extends SQLiteOpenHelper {

	private Context context;
	private LauncherApplication app;
	// All Static variables
	// Database version
	private static final int DATABASE_VERSION = 1;

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

	/** Screen ID, INTEGER PRIMARY KEY */
	private static final String COL_SCREEN_ID = "screen_id";
	/** Screen Name, TEXT */
	private static final String COL_SCREEN_NAME = "screen_name";
	/** Screen Position, INTEGER */
	private static final String COL_SCREEN_POSITION = "screen_pos";

	/** Item ID, INTEGER PRIMARY KEY */
	private static final String COL_ITEM_ID = "item_id";
	/** Screen ID, INTEGER */
	private static final String COL_ITEM_SCREEN = "screen_id";
	/** Item Name, TEXT */
	private static final String COL_ITEM_NAME = "item_name";
	/** Item Type, INTEGER */
	private static final String COL_ITEM_TYPE = "item_type";
	/** Item Position, INTEGER */
	private static final String COL_ITEM_POSITION = "item_pos";

	/** Item ID, INTEGER */
	private static final String COL_APP_ITEM = "item_id";
	/** App Name, TEXT */
	private static final String COL_APP_NAME = "app_name";
	/** App Package Name, TEXT */
	private static final String COL_APP_PACKAGE = "app_package";
	/** App Activity Name, TEXT */
	private static final String COL_APP_ACTIVITY = "app_activity";
	/** App Position INTEGER */
	private static final String COL_APP_POSITION = "app_pos";

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
	}

	// Upgrading Database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older tables if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN_ITEM);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN_ITEM_APP);

		// Create tables again
		onCreate(db);
	}

	/*
	 * The Create functions
	 */
	public void addScreen(Screen screen) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_SCREEN_NAME, screen.getName());
		values.put(COL_SCREEN_POSITION, screen.getPosition());

		// Inserting Row
		int screenID = (int) db.insert(TABLE_SCREEN, null, values);
		Log.d("DB", "ScreenID: " + screenID);
		screen.setScreenID(screenID);

		db.close();

		// Creating the table entries for the ScreenItems
		for (ScreenItem item : screen.getItems()) {
			item.setScreenID(screenID);
			addScreenItem(item);
		}
	}

	public void addScreenItem(ScreenItem item) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_ITEM_SCREEN, item.getScreenID());
		values.put(COL_ITEM_NAME, item.getName());
		values.put(COL_ITEM_TYPE, (item.getType() == Type.APPS ? 0 : 1));
		values.put(COL_ITEM_POSITION, item.getPosition());

		// Inserting Row
		int itemID = (int) db.insert(TABLE_SCREEN_ITEM, null, values);
		item.setItemID(itemID);

		db.close();

		// Creating the table entries for the ScreenItemApps
		for (ScreenItemApp app : item.getApps()) {
			app.setItemID(itemID);
			addScreenItemApp(app);
		}
	}

	public void addScreenItemApp(ScreenItemApp app) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_APP_ITEM, app.getItemID());
		values.put(COL_APP_NAME, app.getName());
		values.put(COL_APP_PACKAGE, app.getPackageName());
		values.put(COL_APP_ACTIVITY, app.getActivityName());
		values.put(COL_APP_POSITION, app.getPosition());

		// Inserting Row
		db.insert(TABLE_SCREEN_ITEM_APP, null, values);

		db.close();
	}

	/*
	 * The Return functions
	 */
	public ArrayList<Screen> getScreens() {
		ArrayList<Screen> screenList = new ArrayList<Screen>();
		String selectQuery = "SELECT * FROM " + TABLE_SCREEN;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Screen screen = new Screen();
				screen.setScreenID(cursor.getInt(0));
				screen.setName(cursor.getString(1));
				screen.setPosition(cursor.getInt(2));
				screen.setItems(getScreenItems(screen.getScreenID()));

				screenList.add(screen);
			} while (cursor.moveToNext());
		}
		return screenList;
	}

	public ArrayList<ScreenItem> getScreenItems(int screenID) {
		ArrayList<ScreenItem> screenItemList = new ArrayList<ScreenItem>();
		String selectQuery = "SELECT * FROM " + TABLE_SCREEN_ITEM + " WHERE "
				+ COL_ITEM_SCREEN + "=" + screenID;

		SQLiteDatabase db = this.getWritableDatabase();
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
				screenItem.setApps(getScreenItemApps(screenItem.getItemID()));

				screenItemList.add(screenItem);
			} while (cursor.moveToNext());
		}
		return screenItemList;

	}

	public ArrayList<ScreenItemApp> getScreenItemApps(int itemID) {
		ArrayList<ScreenItemApp> itemAppList = new ArrayList<ScreenItemApp>();
		String selectQuery = "SELECT * FROM " + TABLE_SCREEN_ITEM_APP
				+ " WHERE " + COL_APP_ITEM + "=" + itemID;

		SQLiteDatabase db = this.getWritableDatabase();
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

	/*
	 * The Remove functions
	 */
	public void removeScreen(Screen screen) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SCREEN, COL_SCREEN_ID + " = ?",
				new String[] { String.valueOf(screen.getScreenID()) });
		for (ScreenItem item : screen.getItems()) {
			removeScreenItem(item);
		}
		db.close();
	}

	public void removeScreenItem(ScreenItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SCREEN_ITEM, COL_ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getItemID()) });
		for (ScreenItemApp app : item.getApps()) {
			removeScreenItemApp(app);
		}
		db.close();
	}

	public void removeScreenItemApp(ScreenItemApp app) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SCREEN_ITEM_APP, COL_APP_ITEM + "=" + app.getItemID()
				+ " AND " + COL_APP_PACKAGE + "='" + app.getPackageName()
				+ "' AND " + COL_APP_ACTIVITY + "='" + app.getActivityName()
				+ "'", null);
		db.close();
	}

	/*
	 * The Update functions
	 */
	public void updateScreen(Screen screen) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_SCREEN_NAME, screen.getName());
		values.put(COL_SCREEN_POSITION, screen.getPosition());

		db.update(TABLE_SCREEN, values, COL_SCREEN_ID + " = ?",
				new String[] { String.valueOf(screen.getScreenID()) });
		db.close();
	}

	public void updateScreenItem(ScreenItem item) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_ITEM_NAME, item.getName());
		values.put(COL_ITEM_TYPE, (item.getType() == Type.APPS ? 0 : 1));
		values.put(COL_ITEM_POSITION, item.getPosition());

		db.update(TABLE_SCREEN_ITEM, values, COL_ITEM_ID + " = ?",
				new String[] { String.valueOf(item.getItemID()) });
		db.close();
	}

	public void updateAppItem(ScreenItemApp app) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_ITEM_POSITION, app.getPosition());

		db.update(
				TABLE_SCREEN_ITEM_APP,
				values,
				COL_APP_ITEM + "=" + app.getItemID() + " AND "
						+ COL_APP_PACKAGE + "='" + app.getPackageName()
						+ "' AND " + COL_APP_ACTIVITY + "='"
						+ app.getActivityName() + "'", null);
		db.close();
	}
}

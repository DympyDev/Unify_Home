package com.dympy.unify.model;

import java.util.ArrayList;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;

import com.dympy.unify.R;
import com.dympy.unify.controller.ArrayHelper;

public class Item {
    public enum Type {
        WIDGET, APPS, UNDEFINED
    }

    private int itemID;
    private int screenID;
    private String name;
    private Type type;
    private int position;
    private int accent;
    private ArrayHelper<ItemApp> apps;
    private AppWidgetProviderInfo widget;
    private Context context;

    public Item(Context context) {
        this.context = context;
        this.type = Type.UNDEFINED;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getScreenID() {
        return screenID;
    }

    public void setScreenID(int workspaceID) {
        this.screenID = workspaceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        switch (type) {
            case WIDGET:
                // TODO: Not yet implemented
                break;
            case APPS:
                if (this.apps == null) {
                    this.apps = new ArrayHelper<ItemApp>();
                }
                break;
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getAccent() {
        return accent;
    }

    public int getAccentColor() {
        int[] accents = context.getResources().getIntArray(R.array.item_accents);
        return accents[accent];
    }

    public void setAccent(int accent) {
        this.accent = accent;
    }

    public ArrayHelper<ItemApp> getApps() {
        return apps;
    }

    public void setApps(ArrayHelper<ItemApp> apps) {
        this.apps = apps;
    }

    public void addApp(ItemApp app) {
        this.apps.add(app);
    }

    public void removeApp(ItemApp app) {
        this.apps.remove(app);
    }

    public AppWidgetProviderInfo getWidget() {
        return widget;
    }

    public void setWidget(AppWidgetProviderInfo widget) {
        this.widget = widget;
    }

    // TODO: Fix the whole widget part
    public AppWidgetHostView getWidgetView() {
        AppWidgetHost appWidgetHost = new AppWidgetHost(context, 0);
        int appWidgetId = appWidgetHost.allocateAppWidgetId();
        AppWidgetHostView hostView = appWidgetHost.createView(context, appWidgetId, widget);
        hostView.setAppWidget(appWidgetId, widget);
        return hostView;
    }

    public void updateContent(Item item) {
        this.name = item.getName();
        this.position = item.getPosition();
        this.type = item.getType();
        if (item.getType() == Type.APPS) {
            this.apps = item.getApps();
        } else if (item.getType() == Type.WIDGET) {
            this.widget = item.getWidget();
        }
    }
}

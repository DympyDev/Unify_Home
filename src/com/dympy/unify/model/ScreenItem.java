package com.dympy.unify.model;

import java.util.ArrayList;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;

public class ScreenItem {
    public enum Type {
        WIDGET, APPS
    }

    private int itemID;
    private int screenID;
    private String name;
    private Type type;
    private int position;
    private ArrayList<ScreenItemApp> apps;
    private AppWidgetProviderInfo widget;
    private Context context;

    public ScreenItem(Context context) {
        this.context = context;
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
                this.apps = new ArrayList<ScreenItemApp>();
                break;
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<ScreenItemApp> getApps() {
        return apps;
    }

    public void setApps(ArrayList<ScreenItemApp> apps) {
        this.apps = apps;
    }

    public void addApp(ScreenItemApp app) {
        this.apps.add(app);
    }

    public void removeApp(ScreenItemApp app) {
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

    public void updateContent(ScreenItem item) {
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

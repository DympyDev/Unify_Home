
package com.dympy.endless.home.workspace;

import java.util.ArrayList;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;

import com.dympy.endless.home.apps.AppData;

public class WorkspaceItem {
    public enum Type {
        WIDGET, APPS
    }

    private int itemID;
    private Type itemType;
    private String itemTitle;
    private ArrayList<AppData> apps;
    private AppWidgetProviderInfo widget;
    private Context context;

    public WorkspaceItem() {

    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public WorkspaceItem(Type itemType) {
        this.itemType = itemType;
        switch (itemType) {
            case WIDGET:
                // TODO: Not yet implemented
                break;
            case APPS:
                this.apps = new ArrayList<AppData>();
                break;
        }
    }

    public Type getItemType() {
        return itemType;
    }

    public void setItemType(Type itemType) {
        this.itemType = itemType;
        switch (itemType) {
            case WIDGET:
                // TODO: Not yet implemented
                break;
            case APPS:
                this.apps = new ArrayList<AppData>();
                break;
        }
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public ArrayList<AppData> getApps() {
        return apps;
    }

    public void setApps(ArrayList<AppData> apps) {
        this.apps = apps;
    }

    public void addApp(AppData app) {
        this.apps.add(app);
    }

    // TODO: Fix the whole widget part
    public AppWidgetHostView getWidgetView() {
        AppWidgetHost appWidgetHost = new AppWidgetHost(context, 0);
        int appWidgetId = appWidgetHost.allocateAppWidgetId();
        AppWidgetHostView hostView = appWidgetHost.createView(context, appWidgetId, widget);
        hostView.setAppWidget(appWidgetId, widget);
        return hostView;
    }

    public AppWidgetProviderInfo getWidget() {
        return widget;
    }

    public void setWidget(AppWidgetProviderInfo widget, Context context) {
        this.widget = widget;
        this.context = context;
    }

}

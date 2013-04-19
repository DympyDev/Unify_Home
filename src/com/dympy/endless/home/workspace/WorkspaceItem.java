
package com.dympy.endless.home.workspace;

import java.util.ArrayList;

import com.dympy.endless.home.apps.AppData;

public class WorkspaceItem {
    public enum Type {
        WIDGET, APPS
    }

    private Type itemType;
    private String itemTitle;
    private ArrayList<AppData> apps;
    private Object widget;

    public WorkspaceItem() {

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

    public Object getWidget() {
        return widget;
    }

    public void setWidget(Object widget) {
        this.widget = widget;
    }

}

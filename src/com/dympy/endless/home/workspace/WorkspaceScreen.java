
package com.dympy.endless.home.workspace;

import java.util.ArrayList;

public class WorkspaceScreen {

    private int screenID;
    private String screenName;
    private ArrayList<WorkspaceItem> items;

    public WorkspaceScreen() {

    }

    public int getScreenID() {
        return screenID;
    }

    public void setScreenID(int screenID) {
        this.screenID = screenID;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public ArrayList<WorkspaceItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<WorkspaceItem> items) {
        this.items = items;
    }

}

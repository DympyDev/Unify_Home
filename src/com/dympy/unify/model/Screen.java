package com.dympy.unify.model;

import java.util.ArrayList;

public class Screen {

    private int screenID;
    private String name;
    private int position;
    private ArrayList<ScreenItem> items;

    public Screen() {
        items = new ArrayList<ScreenItem>();
    }

    public int getScreenID() {
        return screenID;
    }

    public void setScreenID(int screenID) {
        this.screenID = screenID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<ScreenItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ScreenItem> items) {
        this.items = items;
    }

    public void addItem(ScreenItem item) {
        this.items.add(item);
    }

    public void removeItem(ScreenItem item) {
        this.items.remove(item);
    }

    public void updateContent(Screen screen) {
        this.position = screen.getPosition();
        this.name = screen.getName();
        this.items = screen.getItems();
    }
}

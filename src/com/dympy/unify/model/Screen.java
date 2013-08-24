package com.dympy.unify.model;

import android.util.Log;

import com.dympy.unify.controller.ArrayHelper;

import java.util.ArrayList;

public class Screen {

    private int screenID;
    private String name;
    private int position;
    ArrayHelper<Item> items;

    public Screen() {
        items = new ArrayHelper<Item>();
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

    public ArrayHelper<Item> getItems() {
        return items;
    }

    public void setItems(ArrayHelper<Item> items) {
        this.items = items;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void removeItem(Item item) {
        this.items.remove(item);
    }

    public void updateContent(Screen screen) {
        this.position = screen.getPosition();
        this.name = screen.getName();
        this.items = screen.getItems();
    }
}

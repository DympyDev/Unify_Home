package com.dympy.unify.model;

/**
 * Created by Dymion on 24-6-13.
 */
public class Favorite {
    private int favPos;
    private AppData content;

    public Favorite() {

    }

    public Favorite(int pos, AppData data) {
        this.favPos = pos;
        this.content = data;
    }

    public int getFavPos() {
        return favPos;
    }

    public void setFavPos(int favPos) {
        this.favPos = favPos;
    }

    public AppData getContent() {
        return content;
    }

    public void setContent(AppData content) {
        this.content = content;
    }
}

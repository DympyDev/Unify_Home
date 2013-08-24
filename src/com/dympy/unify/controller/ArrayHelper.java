package com.dympy.unify.controller;

import android.annotation.SuppressLint;
import android.util.Log;

import com.dympy.unify.model.AppData;
import com.dympy.unify.model.Favorite;
import com.dympy.unify.model.Item;
import com.dympy.unify.model.ItemApp;
import com.dympy.unify.model.Screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Dymion on 15-7-13.
 */
public class ArrayHelper<T> extends ArrayList<T> {
    private static String TAG = "ArrayHelper";
    private boolean useDB = false;
    private DatabaseHandler db = null;

    @Override
    public boolean add(T object) {
        if (object == null) {
            return false;
        }
        if (db != null && useDB) {
            if (object instanceof Item) {
                db.addScreenItem((Item) object);
            } else if (object instanceof Screen) {
                db.addScreen((Screen) object);
            } else if (object instanceof ItemApp) {
                db.addScreenItemApp((ItemApp) object);
            } else if (object instanceof Favorite) {
                db.addFavorite((Favorite) object);
            }
        }
        return super.add(object);
    }

    @Override
    public boolean remove(Object object) {
        if (object == null) {
            return false;
        }
        if (db != null && useDB) {
            if (object instanceof Item) {
                db.removeScreenItem((Item) object);
            } else if (object instanceof Screen) {
                db.removeScreen((Screen) object);
            } else if (object instanceof ItemApp) {
                db.removeScreenItemApp((ItemApp) object);
            } else if (object instanceof Favorite) {
                db.removeFavorite((Favorite) object);
            }
        }
        return super.remove(object);
    }

    @Override
    public T remove(int index) {
        Object object = get(index);
        if (db != null && useDB) {
            if (object instanceof Item) {
                db.removeScreenItem((Item) object);
            } else if (object instanceof Screen) {
                db.removeScreen((Screen) object);
            } else if (object instanceof ItemApp) {
                db.removeScreenItemApp((ItemApp) object);
            } else if (object instanceof Favorite) {
                db.removeFavorite((Favorite) object);
            }
        }
        return super.remove(index);
    }

    @Override
    public T set(int index, T object) {
        if (db != null && useDB) {
            if (object instanceof Favorite) {
                db.updateFavorite((Favorite) object);
            }
        }
        return super.set(index, object);
    }

    public boolean shouldUseDB() {
        return useDB;
    }

    public void setDB(DatabaseHandler db) {
        this.db = db;
        this.useDB = true;
    }

    public void removeDB() {
        this.db = null;
        this.useDB = false;
    }

    public void sort() {
        Collections.sort(this, new ArrayComparator());
    }

    public class ArrayComparator implements Comparator<Object> {
        @SuppressLint("DefaultLocale")
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof Item && o2 instanceof Item) {
                return ((Item) o1).getPosition() > ((Item) o2).getPosition() ? +1 : ((Item) o1).getPosition() < ((Item) o2).getPosition() ? -1 : 0;
            } else if (o1 instanceof Screen && o2 instanceof Screen) {
                return ((Screen) o1).getPosition() > ((Screen) o2).getPosition() ? +1 : ((Screen) o1).getPosition() < ((Screen) o2).getPosition() ? -1 : 0;
            } else if (o1 instanceof ItemApp && o2 instanceof ItemApp) {
                return ((ItemApp) o1).getPosition() > ((ItemApp) o2).getPosition() ? +1 : ((ItemApp) o1).getPosition() < ((ItemApp) o2).getPosition() ? -1 : 0;
            } else if (o1 instanceof Favorite && o2 instanceof Favorite) {
                return ((Favorite) o1).getFavPos() - ((Favorite) o2).getFavPos();
            }
            return (o1.toString().toLowerCase()).compareTo(o2.toString().toLowerCase());
        }
    }
}

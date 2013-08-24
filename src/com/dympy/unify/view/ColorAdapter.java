package com.dympy.unify.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dympy.unify.R;
import com.dympy.unify.model.AppData;

import java.util.ArrayList;

public class ColorAdapter extends BaseAdapter {
    Context context;
    int layoutResourceId;
    int[] data = null;
    int selected = 0;

    ArrayList<ColorHolder> colorHolders = new ArrayList<ColorHolder>();

    public ColorAdapter(Context context, int layoutResourceId, int[] data) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    public ColorAdapter(Context context, int layoutResourceId, int[] data, int selected) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.selected = selected;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int i) {
        return data[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ColorHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ColorHolder();
            holder.color = (LinearLayout) row.findViewById(R.id.list_item_color_background);
            holder.cb = (CheckBox) row.findViewById(R.id.list_item_color_check);

            row.setTag(holder);
            colorHolders.add(holder);
        } else {
            holder = (ColorHolder) row.getTag();
        }

        if (position == selected) {
            holder.cb.setChecked(true);
        } else {
            holder.cb.setChecked(false);
        }

        holder.color.setBackgroundColor(data[position]);

        return row;
    }

    public void setSelected(int position) {
        selected = position;
    }

    static class ColorHolder {
        LinearLayout color;
        CheckBox cb;
    }
}

package com.dympy.unify.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dympy.unify.R;
import com.dympy.unify.model.ItemApp;
import com.dympy.unify.model.Screen;
import com.dympy.unify.view.custom.DynamicList.StableArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Dymion on 28-8-13.
 */
public class ScreenRearrangeAdapter extends StableArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Screen> data;

    public ScreenRearrangeAdapter(Context context, int layoutResourceId, ArrayList<Screen> objects) {
        super(context, layoutResourceId, objects);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = objects;
    }

    public class CustomHolder {
        TextView title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CustomHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new CustomHolder();
            if (row != null) {
                holder.title = (TextView) row.findViewById(R.id.item_rearrange_workspace_txt);
                row.setTag(holder);
            }
        } else {
            holder = (CustomHolder) row.getTag();
        }
        Screen tempScreen = data.get(position);
        holder.title.setText(tempScreen.getName());

        return row;
    }
}

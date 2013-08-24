package com.dympy.unify.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dympy.unify.R;
import com.dympy.unify.controller.ArrayHelper;
import com.dympy.unify.model.AppData;
import com.dympy.unify.model.ItemApp;

import java.util.ArrayList;

public class AppAdapter extends BaseAdapter {
    Context context;
    int layoutResourceId;
    AppData[] data;
    ArrayHelper<ItemApp> selectedApps = new ArrayHelper<ItemApp>();

    ArrayList<AppDataHolder> appDataHolders = new ArrayList<AppDataHolder>();

    public AppAdapter(Context context, int layoutResourceId, ArrayList<AppData> data) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data.toArray(new AppData[data.size()]);
    }

    public AppAdapter(Context context, int layoutResourceId, ArrayList<AppData> data, ArrayHelper<ItemApp> selected) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data.toArray(new AppData[data.size()]);
        if (selected == null) {
            this.selectedApps = new ArrayHelper<ItemApp>();
        } else {
            this.selectedApps = selected;
        }
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
        AppDataHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AppDataHolder();
            if (row != null) {
                if (layoutResourceId == R.layout.list_item_app_select) {
                    holder.txtTitle = (TextView) row.findViewById(R.id.txt_app_select_name);
                    holder.cb = (CheckBox) row.findViewById(R.id.txt_app_select_check);
                } else {
                    holder.txtTitle = (TextView) row.findViewById(R.id.txt_appitem_name);
                }
                row.setTag(holder);
            }
            appDataHolders.add(holder);
        } else {
            holder = (AppDataHolder) row.getTag();
        }
        AppData app = data[position];
        holder.txtTitle.setText(app.getName());
        holder.txtTitle.setCompoundDrawables(null, app.getIcon(), null, null);

        if (layoutResourceId == R.layout.list_item_app_select) {
            if (inSelected(app) != -1) {
                holder.cb.setChecked(true);
            } else {
                holder.cb.setChecked(false);
            }
        }

        return row;
    }

    /**
     * Updates the list of selected Apps in the gridview, does not update the gridview itself.
     *
     * @param app, the App we have selected
     * @return true if the App is added to the selected list, false if the App is removed
     */
    public void setSelected(ItemApp app) {
        int position = inSelected(app);
        if (position != -1) {
            selectedApps.remove(position);
        } else {
            selectedApps.add(app);
        }
    }

    public ArrayHelper<ItemApp> getSelectedApps() {
        return this.selectedApps;
    }

    public int inSelected(AppData app) {
        return inSelected(app.getActivityName(), app.getPackageName());
    }

    public int inSelected(ItemApp app) {
        return inSelected(app.getActivityName(), app.getPackageName());
    }

    public int inSelected(String activityName, String packageName) {
        for (int i = 0; i < selectedApps.size(); i++) {
            if (selectedApps.get(i).getActivityName().equals(activityName) && selectedApps.get(i).getPackageName().equals(packageName)) {
                return i;
            }
        }
        return -1;
    }

    static class AppDataHolder {
        TextView txtTitle;
        CheckBox cb;
    }
}

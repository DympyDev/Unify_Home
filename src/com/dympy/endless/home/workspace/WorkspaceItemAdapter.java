
package com.dympy.endless.home.workspace;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.dympy.endless.R;
import com.dympy.endless.home.apps.AppDataAdapter;

public class WorkspaceItemAdapter extends ArrayAdapter<WorkspaceItem> {
    Context context;
    int layoutResourceId;
    ArrayList<WorkspaceItem> data = null;

    public WorkspaceItemAdapter(Context context, int layoutResourceId,
            ArrayList<WorkspaceItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WorkspaceItemHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new WorkspaceItemHolder();
            holder.itemTitle = (TextView) row
                    .findViewById(R.id.item_workspace_txt_title);
            holder.appsGrid = (GridView) row
                    .findViewById(R.id.item_workspace_grid_apps);

            row.setTag(holder);
        } else {
            holder = (WorkspaceItemHolder) row.getTag();
        }

        WorkspaceItem item = data.get(position);
        holder.itemTitle.setText(item.getItemTitle());
        if (item.getItemType() == WorkspaceItem.Type.APPS) {
            AppDataAdapter appsAdapter = new AppDataAdapter(context,
                    R.layout.list_item_app, item.getApps());
            holder.appsGrid.setAdapter(appsAdapter);
        }

        return row;
    }

    static class WorkspaceItemHolder {
        TextView itemTitle;
        GridView appsGrid;
    }
}

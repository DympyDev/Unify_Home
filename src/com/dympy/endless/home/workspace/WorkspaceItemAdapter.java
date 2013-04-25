
package com.dympy.endless.home.workspace;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dympy.endless.R;
import com.dympy.endless.home.LauncherModel;
import com.dympy.endless.home.apps.AppDataAdapter;
import com.dympy.endless.home.ui.CustomGrid;

public class WorkspaceItemAdapter extends ArrayAdapter<WorkspaceItem> {
    Context context;
    LauncherModel app;
    int layoutResourceId;
    ArrayList<WorkspaceItem> data = null;

    public WorkspaceItemAdapter(Context context, int layoutResourceId,
            ArrayList<WorkspaceItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        app = ((LauncherModel) context
                .getApplicationContext());
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
            holder.itemSettings = (ImageButton) row
                    .findViewById(R.id.item_workspace_btn_settings);
            holder.widgetContent = (LinearLayout) row
                    .findViewById(R.id.item_workspace_layout_widget);
            holder.appsGrid = (CustomGrid) row
                    .findViewById(R.id.item_workspace_grid_apps);

            row.setTag(holder);
        } else {
            holder = (WorkspaceItemHolder) row.getTag();
        }

        final WorkspaceItemHolder itemHolder = holder;

        final WorkspaceItem item = data.get(position);
        itemHolder.itemTitle.setText(item.getItemTitle());
        if (item.getItemType() == WorkspaceItem.Type.APPS) {
            AppDataAdapter appsAdapter = new AppDataAdapter(context,
                    R.layout.list_item_app, item.getApps());
            itemHolder.appsGrid.setExpanded(true);
            itemHolder.appsGrid.setAdapter(appsAdapter);
            itemHolder.appsGrid.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    context.startActivity(item.getApps().get(position).getAppIntent());
                }
            });
            itemHolder.appsGrid.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                    Toast.makeText(context, "Delete app from workspace item", Toast.LENGTH_SHORT)
                            .show();
                    return false;
                }
            });
        } else if (item.getItemType() == WorkspaceItem.Type.WIDGET) {
            itemHolder.widgetContent.addView(item.getWidgetView());
        }
        showSettings(itemHolder, item);

        return row;
    }

    private void showSettings(final WorkspaceItemHolder itemHolder, final WorkspaceItem item) {
        itemHolder.itemSettings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog actualDialog;
                AlertDialog.Builder itemSettings = new AlertDialog.Builder(context);
                itemSettings.setTitle("Item Settings");

                String[] settings = {
                        "Add app", "Remove item"
                };
                ListView settingsContent = new ListView(context);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, settings);
                settingsContent.setAdapter(adapter);

                itemSettings.setView(settingsContent);
                itemSettings.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                            }
                        });

                actualDialog = itemSettings.create();
                actualDialog.show();
                settingsContent.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        actualDialog.dismiss();
                        switch (position) {
                            case 0:
                                addAppDialog(itemHolder, item);
                                break;
                            case 1:
                                break;
                        }
                    }
                });
            }
        });
    }

    private void addAppDialog(final WorkspaceItemHolder itemHolder,
            final WorkspaceItem item) {
        final AlertDialog listDialog;
        AlertDialog.Builder listBuilder = new AlertDialog.Builder(context);
        listBuilder.setTitle("Select an app");

        GridView appGrid = new GridView(context);
        appGrid.setNumColumns(3);
        AppDataAdapter gridAdapter = new AppDataAdapter(context,
                R.layout.list_item_app, app.getApps());
        appGrid.setAdapter(gridAdapter);
        listBuilder.setView(appGrid);
        listDialog = listBuilder.create();

        appGrid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
                listDialog.dismiss();
                app.addAppToItem(item, app.getApps().get(position));
                itemHolder.appsGrid.setAdapter(new AppDataAdapter(context,
                        R.layout.list_item_app, item.getApps()));
            }
        });

        listDialog.show();
    }

    static class WorkspaceItemHolder {
        TextView itemTitle;
        ImageButton itemSettings;
        LinearLayout widgetContent;
        CustomGrid appsGrid;
    }
}

package com.dympy.unify.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dympy.unify.Launcher;
import com.dympy.unify.LauncherApplication;
import com.dympy.unify.R;
import com.dympy.unify.model.AppData;
import com.dympy.unify.model.ScreenItem;
import com.dympy.unify.model.ScreenItemApp;
import com.dympy.unify.view.custom.CustomGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Dymion on 2-7-13.
 */
public class ItemAdapter extends ArrayAdapter<ScreenItem> {
    private final static String TAG = "ItemAdapter";
    private final Activity context;
    private LauncherApplication app;
    private final ScreenItem[] items;
    private boolean longPressed = false;

    static class ItemHolder {
        public TextView title;
        public CustomGrid appGrid;
        public ScreenItem instance;
    }

    public ItemAdapter(Activity context, ScreenItem[] objects) {
        super(context, R.layout.list_item_workspace, objects);
        this.context = context;
        this.app = (LauncherApplication) context.getApplication();
        this.items = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ScreenItem item = items[position];
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_item_workspace, null);
            ItemHolder itemHolder = new ItemHolder();
            itemHolder.title = (TextView) rowView.findViewById(R.id.item_workspace_txt_title);
            itemHolder.appGrid = (CustomGrid) rowView.findViewById(R.id.item_workspace_grid_apps);
            itemHolder.instance = item;
            rowView.setTag(itemHolder);
        }

        final ItemHolder holder = (ItemHolder) rowView.getTag();
        holder.title.setText(item.getName());
        holder.title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSettingsDialog(holder);
            }
        });
        if (holder.appGrid != null && item.getApps().size() > 0) {
            Log.d(TAG, "Populating App Grid with " + item.getApps().size() + " items");
            Collections.sort(item.getApps(), new AppComparator());
            ArrayList<AppData> apps = new ArrayList<AppData>();
            for (ScreenItemApp app : item.getApps()) {
                apps.add(app.getAppData());
            }
            holder.appGrid.setExpanded(true);
            holder.appGrid.setAdapter(new AppAdapter(context, R.layout.list_item_app, apps));
            holder.appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    if (longPressed) {
                        longPressed = false;
                    } else {
                        context.startActivity(holder.instance.getApps().get(position).getAppData().getIntent());
                    }
                }
            });
            holder.appGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                    longPressed = true;
                    removeAppDialog(holder, position);
                    return false;
                }
            });
        }
        return rowView;
    }

    public class AppComparator implements Comparator<ScreenItemApp> {
        @Override
        public int compare(ScreenItemApp screen, ScreenItemApp screen2) {
            return screen.getPosition() > screen2.getPosition() ? +1 : screen.getPosition() < screen2.getPosition() ? -1 : 0;
        }
    }


    private void showSettingsDialog(final ItemHolder itemHolder) {
        final AlertDialog actualDialog;
        AlertDialog.Builder itemSettings = new AlertDialog.Builder(context);
        itemSettings.setTitle(context.getString(R.string.dialog_item_settings_title));

        ArrayList<String> settingsArray = new ArrayList<String>();
        if (itemHolder.instance.getType() == ScreenItem.Type.APPS) {
            settingsArray.add(context.getString(R.string.dialog_item_settings_list_add_app));
            settingsArray.add(context.getString(R.string.dialog_item_settings_list_rearrange_content));
        }
        settingsArray.add(context.getString(R.string.dialog_item_settings_list_rename_item));
        settingsArray.add(context.getString(R.string.dialog_item_settings_list_remove_item));

        ListView settingsContent = new ListView(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, settingsArray);
        settingsContent.setAdapter(adapter);

        itemSettings.setView(settingsContent);
        itemSettings.setNegativeButton(context.getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        actualDialog = itemSettings.create();
        actualDialog.show();
        settingsContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                actualDialog.dismiss();
                //TODO: I broke this
                switch (position) {
                    case 0: // Add app
                        addAppDialog(itemHolder);
                        break;
                    case 1:
                        break;
                    case 2: // Rename item
                        renameItemDialog(itemHolder);
                        break;
                    case 3: // Remove item
                        removeItemDialog(itemHolder);
                        break;
                }
            }
        });
    }

    private void removeAppDialog(final ItemHolder itemHolder, final int appPos) {
        AlertDialog.Builder removeApp = new AlertDialog.Builder(context);
        removeApp.setTitle(context.getString(R.string.dialog_item_remove_app_title));
        removeApp.setMessage(context.getString(R.string.dialog_item_remove_app_message));
        removeApp.setNegativeButton(context.getString(R.string.dialog_btn_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        removeApp.setPositiveButton(context.getString(R.string.dialog_btn_yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String appName = itemHolder.instance.getApps().get(appPos).getName();
                app.removeScreenItemApp(itemHolder.instance.getApps().get(appPos));
                itemHolder.instance.removeApp(itemHolder.instance.getApps().get(appPos));
                ((Launcher) context).updatePager();
                Toast.makeText(context, context.getString(R.string.dialog_item_remove_app_toast) + " '" + appName + "'", Toast.LENGTH_SHORT).show();
            }
        });
        removeApp.show();
    }

    private void removeItemDialog(final ItemHolder itemHolder) {
        AlertDialog.Builder removeApp = new AlertDialog.Builder(context);
        removeApp.setTitle(context.getString(R.string.dialog_item_remove_item_title));
        removeApp.setMessage(context.getString(R.string.dialog_item_remove_item_message));
        removeApp.setNegativeButton(context.getString(R.string.dialog_btn_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        removeApp.setPositiveButton(context.getString(R.string.dialog_btn_yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                app.removeScreenItem(itemHolder.instance);
                ((Launcher) context).updatePager();
            }
        });
        removeApp.show();
    }

    private void addAppDialog(final ItemHolder itemHolder) {
        final AlertDialog listDialog;
        AlertDialog.Builder listBuilder = new AlertDialog.Builder(context);
        listBuilder.setTitle(context.getString(R.string.dialog_item_add_app_title));

        GridView appGrid = new GridView(context);
        appGrid.setNumColumns(3);
        AppAdapter gridAdapter = new AppAdapter(context, R.layout.list_item_app, app.getApps());
        appGrid.setAdapter(gridAdapter);
        listBuilder.setView(appGrid);
        listDialog = listBuilder.create();

        appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                listDialog.dismiss();
                AppData addApp = app.getApps().get(position);
                boolean shouldAdd = true;
                for (ScreenItemApp temp : itemHolder.instance.getApps()) {
                    if (addApp.getActivityName().equals(temp.getActivityName()) && addApp.getPackageName().equals(temp.getPackageName())) {
                        shouldAdd = false;
                    }
                }
                if (shouldAdd) {
                    ScreenItemApp temp = new ScreenItemApp();
                    temp.setItemID(itemHolder.instance.getItemID());
                    temp.setName(addApp.getName());
                    temp.setPackageName(addApp.getPackageName());
                    temp.setActivityName(addApp.getActivityName());
                    temp.setPosition(itemHolder.instance.getApps().size());
                    temp.setAppData(addApp);

                    app.addScreenItemApp(temp);
                    itemHolder.instance.addApp(temp);
                    ((Launcher) context).updatePager();
                } else {
                    Toast.makeText(context, context.getString(R.string.dialog_item_add_app_toast), Toast.LENGTH_SHORT).show();
                }
            }
        });

        listDialog.show();
    }

    private void renameItemDialog(final ItemHolder itemHolder) {
        AlertDialog.Builder listDialog = new AlertDialog.Builder(context);
        listDialog.setTitle(context.getString(R.string.dialog_item_rename_item_title));
        listDialog.setMessage(context.getString(R.string.dialog_item_rename_item_message));

        final EditText renameText = new EditText(context);
        renameText.setText(itemHolder.instance.getName());
        listDialog.setView(renameText);
        listDialog.setPositiveButton(context.getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemHolder.instance.setName(renameText.getText().toString());
                itemHolder.title.setText(itemHolder.instance.getName());
                app.updateScreenItem(itemHolder.instance);
            }
        });

        listDialog.setNegativeButton(context.getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Canceled.
            }
        });

        listDialog.show();
    }
}

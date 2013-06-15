package com.dympy.endless.screen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dympy.endless.Launcher;
import com.dympy.endless.LauncherApplication;
import com.dympy.endless.R;
import com.dympy.endless.apps.AppData;
import com.dympy.endless.apps.AppDataAdapter;
import com.dympy.endless.screen.ScreenItem.Type;
import com.dympy.endless.ui.CustomGrid;

public class ScreenItemAdapter extends ArrayAdapter<ScreenItem> {
    private Context context;
    private ArrayList<ScreenItem> data = null;
    private LauncherApplication app;

    private boolean longPressed = false;

    public ScreenItemAdapter(Context context, int layoutResourceId, ArrayList<ScreenItem> data) {
        super(context, layoutResourceId, data);
        Log.d("ScreenItemAdapter", context.getClass().getName());
        this.context = context;
        app = (LauncherApplication) context.getApplicationContext();
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScreenItem instance = data.get(position);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.list_item_workspace, parent, false);
        ScreenItemHolder tempHolder = new ScreenItemHolder();

        tempHolder.instance = instance;
        tempHolder.title = (TextView) row.findViewById(R.id.item_workspace_txt_title);
        tempHolder.appGrid = (CustomGrid) row.findViewById(R.id.item_workspace_grid_apps);

        final ScreenItemHolder itemHolder = tempHolder;

        ImageButton itemViewSettings = (ImageButton) row.findViewById(R.id.item_workspace_btn_settings);
        LinearLayout widgetViewContent = (LinearLayout) row.findViewById(R.id.item_workspace_layout_widget);

        itemHolder.title.setText(itemHolder.instance.getName());

        itemViewSettings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showSettingsDialog(itemHolder);
            }
        });

        if (itemHolder.instance.getType() == Type.APPS) {
            Collections.sort(itemHolder.instance.getApps(), new AppComparator());
            ItemAppAdapter appsAdapter = new ItemAppAdapter(context, R.layout.list_item_app, itemHolder.instance.getApps());
            itemHolder.appGrid.setExpanded(true);
            itemHolder.appGrid.setAdapter(appsAdapter);
            itemHolder.appGrid.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    if (longPressed) {
                        longPressed = false;
                    } else {
                        context.startActivity(itemHolder.instance.getApps().get(position).getAppData().getIntent());
                    }
                }
            });
            itemHolder.appGrid.setOnItemLongClickListener(new OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> parent,
                                               View v, int position, long id) {
                    longPressed = true;
                    removeAppDialog(itemHolder, position);
                    return false;
                }
            });
        } else if (itemHolder.instance.getType() == Type.WIDGET) {
            widgetViewContent.addView(itemHolder.instance.getWidgetView());
        }
        return row;
    }

    public class AppComparator implements Comparator<ScreenItemApp> {
        @Override
        public int compare(ScreenItemApp screen, ScreenItemApp screen2) {
            return screen.getPosition() > screen2.getPosition() ? +1 : screen.getPosition() < screen2.getPosition() ? -1 : 0;
        }
    }

    static class ScreenItemHolder {
        ScreenItem instance;
        TextView title;
        CustomGrid appGrid;
    }

    private void showSettingsDialog(final ScreenItemHolder itemHolder) {
        final AlertDialog actualDialog;
        AlertDialog.Builder itemSettings = new AlertDialog.Builder(context);
        itemSettings.setTitle(context.getString(R.string.dialog_item_settings_title));

        ArrayList<String> settingsArray = new ArrayList<String>();
        if(itemHolder.instance.getType() == Type.APPS){
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
        settingsContent.setOnItemClickListener(new OnItemClickListener() {

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

    private void removeAppDialog(final ScreenItemHolder itemHolder, final int appPos) {
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

    private void removeItemDialog(final ScreenItemHolder itemHolder) {
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

    private void addAppDialog(final ScreenItemHolder itemHolder) {
        final AlertDialog listDialog;
        AlertDialog.Builder listBuilder = new AlertDialog.Builder(context);
        listBuilder.setTitle(context.getString(R.string.dialog_item_add_app_title));

        GridView appGrid = new GridView(context);
        appGrid.setNumColumns(3);
        AppDataAdapter gridAdapter = new AppDataAdapter(context, R.layout.list_item_app, app.getApps());
        appGrid.setAdapter(gridAdapter);
        listBuilder.setView(appGrid);
        listDialog = listBuilder.create();

        appGrid.setOnItemClickListener(new OnItemClickListener() {

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

    private void renameItemDialog(final ScreenItemHolder itemHolder) {
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

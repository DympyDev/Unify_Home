package com.dympy.unify.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;

import com.dympy.unify.Launcher;
import com.dympy.unify.LauncherApplication;
import com.dympy.unify.R;
import com.dympy.unify.model.AppData;
import com.dympy.unify.model.Item;
import com.dympy.unify.model.ItemApp;

import static com.dympy.unify.model.Item.Type.*;

/**
 * Created by Dymion on 18-8-13.
 */
public class ItemDialog extends AlertDialog.Builder {
    private Context context = null;
    private LauncherApplication application = null;
    private Item item = null;
    private boolean isNewItem = false;

    private EditText itemName;
    private RadioGroup itemType;
    private AlertDialog instance = null;

    AppAdapter appGridAdapter = null;

    public ItemDialog(Context context, Item item) {
        super(context);
        this.context = context;
        this.application = (LauncherApplication) ((Activity) context).getApplication();
        this.item = item;
        if (item == null) {
            this.item = new Item(context);
            isNewItem = true;
        }
    }

    public boolean isNewItem() {
        return isNewItem;
    }

    public void setItem(Item item) {
        this.item = item;
        if (item == null) {
            this.item = new Item(context);
            isNewItem = true;
        } else {
            if (itemName != null) {
                itemName.setText(item.getName());
            }
        }
    }

    public Item getItem() {
        int currentScreen = ((Launcher) context).getCurrentScreen();
        if (itemName != null && item.getName() != itemName.getText().toString()) {
            item.setName(itemName.getText().toString());
        }
        if (isNewItem) {
            Log.d("ItemDialog", "currentScreen: " + currentScreen);
            item.setScreenID(application.SCREENS.get(currentScreen).getScreenID());
            Log.d("ItemDialog", "screen_id: " + item.getScreenID());
            item.setPosition(application.getScreenItemPosition(currentScreen));
            Log.d("ItemDialog", "Item position: " + item.getPosition());
        }
        if (itemType != null && item.getType() != UNDEFINED) {
            switch (itemType.getCheckedRadioButtonId()) {
                case R.id.dialog_add_item_app:
                    item.setType(APPS);
                    break;
                case R.id.dialog_add_item_widget:
                    item.setType(WIDGET);
                    break;
            }
        }
        if (itemName != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(itemName.getWindowToken(), 0);
        }
        return item;
    }

    @Override
    public AlertDialog create() {
        if (instance == null) {
            instance = super.create();
        }
        return instance;
    }

    public void initDialog() {
        this.setTitle(context.getString(R.string.dialog_add_item_title));
        LayoutInflater inflater = ((Launcher) context).getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_add_item, null);
        itemName = (EditText) dialogLayout.findViewById(R.id.dialog_add_item_input);
        if (!isNewItem) {
            itemName.setText(item.getName());
        }
        itemType = (RadioGroup) dialogLayout.findViewById(R.id.dialog_add_item_radiogroup);
        if (!isNewItem) {
            itemType.setVisibility(View.GONE);
            dialogLayout.findViewById(R.id.dialog_add_item_typetext).setVisibility(View.GONE);
            Button removeBtn = (Button) dialogLayout.findViewById(R.id.dialog_add_item_remove);
            removeBtn.setVisibility(View.VISIBLE);
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder removeApp = new AlertDialog.Builder(context);
                    removeApp.setTitle(context.getString(R.string.dialog_item_remove_item_title));
                    removeApp.setMessage(context.getString(R.string.dialog_item_remove_item_message));
                    removeApp.setNegativeButton(context.getString(R.string.dialog_btn_no), null);
                    removeApp.setPositiveButton(context.getString(R.string.dialog_btn_yes), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            application.removeScreenItem(item);
                            ((Launcher) context).updatePager();
                            instance.dismiss();
                        }
                    });
                    removeApp.show();
                }
            });
        }

        final ColorAdapter adapter;
        if (isNewItem) {
            adapter = new ColorAdapter(context, R.layout.list_item_color, context.getResources().getIntArray(R.array.item_accents));
        } else {
            adapter = new ColorAdapter(context, R.layout.list_item_color, context.getResources().getIntArray(R.array.item_accents), item.getAccent());
        }
        GridView colorGrid = (GridView) dialogLayout.findViewById(R.id.dialog_add_item_colors);
        colorGrid.setAdapter(adapter);
        colorGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                adapter.setSelected(position);
                adapter.notifyDataSetChanged();
                item.setAccent(position);
            }
        });

        this.setView(dialogLayout);
    }

    public void initAppContent() {
        //TODO: Extract resource
        this.setTitle("Choose Content");
        GridView appGrid = new GridView(context);
        appGrid.setNumColumns(context.getResources().getInteger(R.integer.add_app_columns));
        appGridAdapter = new AppAdapter(context, R.layout.list_item_app_select, application.getApps(), item.getApps());
        appGrid.setAdapter(appGridAdapter);
        appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AppData selectedApp = application.getApps().get(position);

                ItemApp temp = new ItemApp();
                temp.setItemID(item.getItemID());
                temp.setName(selectedApp.getName());
                temp.setPackageName(selectedApp.getPackageName());
                temp.setActivityName(selectedApp.getActivityName());
                temp.setPosition(appGridAdapter.getSelectedApps().size());
                temp.setAppData(selectedApp);

                appGridAdapter.setSelected(temp);
                appGridAdapter.notifyDataSetChanged();
                item.setApps(appGridAdapter.getSelectedApps());
            }
        });

        this.setView(appGrid);
    }

    public void saveItem(Item item) {
        if (isNewItem) {
            application.addScreenItem(item);
            ((Launcher) context).updatePager();
        } else {
            //TODO: Note to self, don't code after midnight, it looks disgusting..
            application.updateScreenItem(item);
            for (int i = 0; i < ((Launcher) context).workspaceFragments.size(); i++) {
                ((Launcher) context).workspaceFragments.get(i);
                if (((Launcher) context).workspaceFragments.get(i).getWorkspaceID() == item.getScreenID()) {
                    ((Launcher) context).workspaceFragments.get(i).getAdapter().notifyDataSetChanged();
                }
            }
        }
    }
}

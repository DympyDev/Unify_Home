package com.dympy.unify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dympy.unify.model.AppData;
import com.dympy.unify.model.Favorite;
import com.dympy.unify.model.Item;
import com.dympy.unify.model.Screen;
import com.dympy.unify.view.AppAdapter;
import com.dympy.unify.view.ItemDialog;
import com.dympy.unify.view.WorkspaceAdapter;
import com.dympy.unify.view.WorkspaceFragment;

import java.util.ArrayList;

public class Launcher extends FragmentActivity implements OnClickListener, View.OnLongClickListener {
    public ArrayList<WorkspaceFragment> workspaceFragments;
    private ViewPager workspacePager;
    private LauncherApplication app;
    private boolean longPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getActionBar().setIcon(R.drawable.ab_unify);

        app = (LauncherApplication) getApplication();
        workspaceFragments = new ArrayList<WorkspaceFragment>();
        for (Screen screen : app.SCREENS) {
            WorkspaceFragment temp = new WorkspaceFragment();
            Bundle args = new Bundle();
            args.putInt("screen_id", screen.getScreenID());
            temp.setArguments(args);
            workspaceFragments.add(temp);
            //TODO:replace 0 with "Main screen"
            if (screen.getPosition() == 0) {
                getActionBar().setTitle(screen.getName());
            }
        }

        final WorkspaceAdapter adapter = new WorkspaceAdapter(getSupportFragmentManager(), this);

        workspacePager = (ViewPager) findViewById(R.id.pager);
        workspacePager.setOffscreenPageLimit(app.SCREENS.size());
        workspacePager.setAdapter(adapter);
        workspacePager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                getActionBar().setTitle(adapter.getPageTitle(i));
            }
        });
        //TODO: go to the "Main screen"
        workspacePager.setCurrentItem(0);
        initButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.launcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_item:
                addItemDialog();
                return true;
            case R.id.action_add_screen:
                addScreenDialog();
                return true;
            case R.id.action_remove_screen:
                removeScreen(app.SCREENS.get(workspacePager.getCurrentItem()));
                return true;
            case R.id.action_rename_screen:
                renameScreen(app.SCREENS.get(workspacePager.getCurrentItem()));
                return true;
            case R.id.action_change_wallpaper:
                Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                startActivity(Intent.createChooser(intent, "Select Wallpaper"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //TODO: This code should be in the home button thing.. Also go to the "Main screen"
        if (workspacePager.getCurrentItem() != 0) {
            workspacePager.setCurrentItem(0);
        }
    }

    public void updatePager() {
        workspaceFragments.clear();
        for (Screen screen : app.SCREENS) {
            WorkspaceFragment temp = new WorkspaceFragment();
            Bundle args = new Bundle();
            args.putInt("screen_id", screen.getScreenID());
            temp.setArguments(args);
            workspaceFragments.add(temp);
        }
        workspacePager.getAdapter().notifyDataSetChanged();
    }

    public int getCurrentScreen() {
        return workspacePager.getCurrentItem();
    }

    private void renameScreen(final Screen screen) {
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle(getString(R.string.dialog_rename_screen_title));
        listDialog.setMessage(getString(R.string.dialog_rename_screen_message));

        final EditText renameText = new EditText(this);
        renameText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        renameText.setText(screen.getName());
        listDialog.setView(renameText);
        listDialog.setNegativeButton(getString(R.string.dialog_btn_cancel), null).
                setPositiveButton(getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        screen.setName(renameText.getText().toString());
                        app.updateScreen(screen);
                        getActionBar().setTitle(screen.getName());
                    }
                });

        listDialog.show();
    }

    private void addScreenDialog() {
        AlertDialog.Builder addScreen = new AlertDialog.Builder(this);
        addScreen.setTitle(getString(R.string.dialog_add_screen_title));
        final EditText screenName = new EditText(this);
        screenName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        screenName.setHint(getString(R.string.dialog_add_screen_edit_hint));
        addScreen.setMessage(getString(R.string.dialog_add_screen_message));
        addScreen.setView(screenName);
        addScreen.setNegativeButton(getString(R.string.dialog_btn_cancel), null)
                .setPositiveButton(getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Screen newScreen = new Screen();
                        newScreen.setName(screenName.getText().toString());
                        newScreen.setPosition(app.SCREENS.size());
                        app.addScreen(newScreen);
                        updatePager();
                    }
                });
        addScreen.show();
    }

    public void addItemDialog() {
        addItemDialog(null, this);
    }

    public void addItemDialog(Item item, final Context ctx) {
        final ItemDialog tempItemDialog = new ItemDialog(ctx, item);
        final ItemDialog tempAppDialog = new ItemDialog(ctx, item);
        tempAppDialog.initAppContent();
        tempAppDialog.setNegativeButton("Back", null)
                .setNeutralButton("Rearrange", null)
                .setPositiveButton(getString(R.string.dialog_btn_ok), null);

        tempItemDialog.initDialog();
        tempItemDialog.setNegativeButton(getString(R.string.dialog_btn_cancel), null)
                .setPositiveButton(getString(R.string.dialog_btn_next), null);
        if (!tempItemDialog.isNewItem()) {
            tempItemDialog.setNeutralButton("Save", null);
        }

        final AlertDialog itemDialog = tempItemDialog.create();
        final AlertDialog appDialog = tempAppDialog.create();
        itemDialog.show();
        itemDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Item tempItem = tempItemDialog.getItem();
                if (tempItem != null && !tempItem.getName().isEmpty()) {
                    tempItemDialog.saveItem(tempItem);
                    itemDialog.dismiss();
                    appDialog.dismiss();
                } else {
                    Toast.makeText(ctx, "Your Workspace Item should have a name", Toast.LENGTH_LONG).show();
                }
            }
        });
        itemDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item tempItem = tempItemDialog.getItem();
                if (tempItem != null && !tempItem.getName().isEmpty()) {
                    tempAppDialog.setItem(tempItem);
                    appDialog.show();
                    itemDialog.hide();
                } else {
                    Toast.makeText(ctx, "Your Workspace Item should have a name", Toast.LENGTH_LONG).show();
                }
            }
        });

        appDialog.show();
        appDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                tempItemDialog.setItem(tempAppDialog.getItem());
                itemDialog.show();
                appDialog.hide();
            }
        });
        appDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "This feature is not yet implemented", Toast.LENGTH_LONG).show();
                //TODO: A third dialog which can be used to rearrange your item
            }
        });
        appDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Item tempItem = tempAppDialog.getItem();
                if (tempItem != null && tempItem.getApps() != null && tempItem.getApps().size() > 0) {
                    tempAppDialog.saveItem(tempItem);
                    appDialog.dismiss();
                    itemDialog.dismiss();
                } else {
                    if (tempItem.getApps() == null || tempItem.getApps().size() == 0) {
                        Toast.makeText(ctx, "You should select an app or two", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ctx, "Something went wrong while saving this item", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        appDialog.hide();
    }

    public void removeScreen(final Screen screen) {
        if (workspaceFragments.size() > 1) {
            AlertDialog.Builder removeScreen = new AlertDialog.Builder(this);
            removeScreen.setTitle(getString(R.string.dialog_remove_screen_title));
            removeScreen.setMessage(getString(R.string.dialog_remove_screen_message));
            removeScreen.setNegativeButton(getString(R.string.dialog_btn_no), null)
                    .setPositiveButton(getString(R.string.dialog_btn_yes), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            app.removeScreen(screen);
                            updatePager();
                        }
                    });
            removeScreen.show();
        } else {
            //TODO: Extract to string file
            Toast.makeText(this, "You can't remove your last screen", Toast.LENGTH_SHORT).show();
        }
    }

    private void setFavoriteDialog(final int pos) {
        final AlertDialog listDialog;
        AlertDialog.Builder listBuilder = new AlertDialog.Builder(this);
        listBuilder.setTitle(getString(R.string.dialog_item_add_app_title));

        GridView appGrid = new GridView(this);
        appGrid.setNumColumns(getResources().getInteger(R.integer.add_app_columns));
        AppAdapter gridAdapter = new AppAdapter(this, R.layout.list_item_app, app.getApps());
        appGrid.setAdapter(gridAdapter);
        listBuilder.setView(appGrid);
        listDialog = listBuilder.create();

        appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                listDialog.dismiss();
                AppData chosenApp = app.getApps().get(position);
                Favorite newFav = new Favorite(pos, chosenApp);
                app.setFavorite(newFav);
                setFavoriteBtns();
            }
        });

        listDialog.show();
    }

    private void initButtons() {
        ImageButton allApps = (ImageButton) findViewById(R.id.buttonbar_btn_drawer);
        ImageButton favorite1 = (ImageButton) findViewById(R.id.buttonbar_btn_fav1);
        ImageButton favorite2 = (ImageButton) findViewById(R.id.buttonbar_btn_fav2);
        ImageButton favorite3 = (ImageButton) findViewById(R.id.buttonbar_btn_fav3);
        ImageButton favorite4 = (ImageButton) findViewById(R.id.buttonbar_btn_fav4);
        ImageButton favorite5 = (ImageButton) findViewById(R.id.buttonbar_btn_fav5);
        ImageButton favorite6 = (ImageButton) findViewById(R.id.buttonbar_btn_fav6);
        ImageButton favorite7 = (ImageButton) findViewById(R.id.buttonbar_btn_fav7);

        allApps.setOnClickListener(this);
        favorite1.setOnClickListener(this);
        favorite1.setOnLongClickListener(this);
        favorite2.setOnClickListener(this);
        favorite2.setOnLongClickListener(this);
        favorite3.setOnClickListener(this);
        favorite3.setOnLongClickListener(this);
        favorite4.setOnClickListener(this);
        favorite4.setOnLongClickListener(this);
        favorite5.setOnClickListener(this);
        favorite5.setOnLongClickListener(this);

        if (favorite6 != null && favorite7 != null) {
            favorite6.setOnClickListener(this);
            favorite6.setOnLongClickListener(this);
            favorite7.setOnClickListener(this);
            favorite7.setOnLongClickListener(this);
        }
        setFavoriteBtns();
    }

    private void setFavoriteBtns() {
        ImageButton favorite1 = (ImageButton) findViewById(R.id.buttonbar_btn_fav1);
        ImageButton favorite2 = (ImageButton) findViewById(R.id.buttonbar_btn_fav2);
        ImageButton favorite3 = (ImageButton) findViewById(R.id.buttonbar_btn_fav3);
        ImageButton favorite4 = (ImageButton) findViewById(R.id.buttonbar_btn_fav4);
        ImageButton favorite5 = (ImageButton) findViewById(R.id.buttonbar_btn_fav5);
        ImageButton favorite6 = (ImageButton) findViewById(R.id.buttonbar_btn_fav6);
        ImageButton favorite7 = (ImageButton) findViewById(R.id.buttonbar_btn_fav7);

        if (app.getFavorite(0) != null) {
            favorite1.setImageDrawable(app.getFavorite(0).getContent().getIcon().getConstantState().newDrawable());
        }
        if (app.getFavorite(1) != null) {
            favorite2.setImageDrawable(app.getFavorite(1).getContent().getIcon().getConstantState().newDrawable());
        }
        if (app.getFavorite(2) != null) {
            favorite3.setImageDrawable(app.getFavorite(2).getContent().getIcon().getConstantState().newDrawable());
        }
        if (app.getFavorite(3) != null) {
            favorite4.setImageDrawable(app.getFavorite(3).getContent().getIcon().getConstantState().newDrawable());
        }
        if (app.getFavorite(4) != null) {
            favorite5.setImageDrawable(app.getFavorite(4).getContent().getIcon().getConstantState().newDrawable());
        }
        if (app.getFavorite(5) != null) {
            favorite6.setImageDrawable(app.getFavorite(5).getContent().getIcon().getConstantState().newDrawable());
        }
        if (app.getFavorite(6) != null) {
            favorite7.setImageDrawable(app.getFavorite(6).getContent().getIcon().getConstantState().newDrawable());
        }
    }

    @Override
    public void onClick(View v) {
        if (longPressed) {
            longPressed = false;
        } else {
            switch (v.getId()) {
                case R.id.buttonbar_btn_fav1:
                    if (app.getFavorite(0) != null) {
                        startActivity(app.getFavorite(0).getContent().getIntent());
                    } else {
                        setFavoriteDialog(0);
                    }
                    break;
                case R.id.buttonbar_btn_fav2:
                    if (app.getFavorite(1) != null) {
                        startActivity(app.getFavorite(1).getContent().getIntent());
                    } else {
                        setFavoriteDialog(1);
                    }
                    break;
                case R.id.buttonbar_btn_fav3:
                    if (app.getFavorite(2) != null) {
                        startActivity(app.getFavorite(2).getContent().getIntent());
                    } else {
                        setFavoriteDialog(2);
                    }
                    break;
                case R.id.buttonbar_btn_fav4:
                    if (app.getFavorite(3) != null) {
                        startActivity(app.getFavorite(3).getContent().getIntent());
                    } else {
                        setFavoriteDialog(3);
                    }
                    break;
                case R.id.buttonbar_btn_fav5:
                    if (app.getFavorite(4) != null) {
                        startActivity(app.getFavorite(4).getContent().getIntent());
                    } else {
                        setFavoriteDialog(4);
                    }
                    break;
                case R.id.buttonbar_btn_fav6:
                    if (app.getFavorite(5) != null) {
                        startActivity(app.getFavorite(5).getContent().getIntent());
                    } else {
                        setFavoriteDialog(5);
                    }
                    break;
                case R.id.buttonbar_btn_fav7:
                    if (app.getFavorite(6) != null) {
                        startActivity(app.getFavorite(6).getContent().getIntent());
                    } else {
                        setFavoriteDialog(6);
                    }
                    break;
                case R.id.buttonbar_btn_drawer:
                    Intent drawer = new Intent(this, Drawer.class);
                    startActivity(drawer);
                    overridePendingTransition(R.animator.zoom_enter, R.animator.zoom_exit);
                    break;
                default:
                    Toast.makeText(this, "Something broke..", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        longPressed = true;
        switch (view.getId()) {
            case R.id.buttonbar_btn_fav1:
                setFavoriteDialog(0);
                return false;
            case R.id.buttonbar_btn_fav2:
                setFavoriteDialog(1);
                return false;
            case R.id.buttonbar_btn_fav3:
                setFavoriteDialog(2);
                return false;
            case R.id.buttonbar_btn_fav4:
                setFavoriteDialog(3);
                return false;
            case R.id.buttonbar_btn_fav5:
                setFavoriteDialog(4);
                return false;
            case R.id.buttonbar_btn_fav6:
                setFavoriteDialog(5);
                return false;
            case R.id.buttonbar_btn_fav7:
                setFavoriteDialog(6);
                return false;
            default:
                return false;
        }
    }
}

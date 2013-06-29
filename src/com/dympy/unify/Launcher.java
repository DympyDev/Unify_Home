package com.dympy.unify;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.dympy.unify.model.AppData;
import com.dympy.unify.model.Favorite;
import com.dympy.unify.model.Screen;
import com.dympy.unify.model.ScreenItem;
import com.dympy.unify.model.ScreenItem.Type;
import com.dympy.unify.model.ActionItem;
import com.dympy.unify.view.AppDataAdapter;
import com.dympy.unify.view.custom.QuickAction;

public class Launcher extends FragmentActivity implements OnClickListener, View.OnLongClickListener {

    private ViewPager screenPager;
    private LauncherApplication app;
    private boolean longPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        app = (LauncherApplication) getApplication();

        ScreenPagerAdapter screensAdapter = new ScreenPagerAdapter(getSupportFragmentManager());

        PagerTabStrip workspaceScreenTabs = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        workspaceScreenTabs.setTabIndicatorColor(0x3B3B3B);
        workspaceScreenTabs.setDrawFullUnderline(true);

        screenPager = (ViewPager) findViewById(R.id.pager);
        screenPager.setAdapter(screensAdapter);
        if (screensAdapter.getCount() > 1) {
            screenPager.setCurrentItem(1);
        }
        initButtons();
    }

    @Override
    public void onBackPressed() {
        //We don't want to exit this screen!
    }

    public void updatePager() {
        screenPager.getAdapter().notifyDataSetChanged();
    }

    private void renameScreen(final Screen screen) {
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setTitle(getString(R.string.dialog_rename_screen_title));
        listDialog.setMessage(getString(R.string.dialog_rename_screen_message));

        final EditText renameText = new EditText(this);
        renameText.setText(screen.getName());
        listDialog.setView(renameText);
        listDialog.setPositiveButton(getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                screen.setName(renameText.getText().toString());
                app.updateScreen(screen);
                updatePager();
            }
        });

        listDialog.setNegativeButton(getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Canceled.
            }
        });

        listDialog.show();
    }

    private void addScreenDialog() {
        AlertDialog.Builder addScreen = new AlertDialog.Builder(this);
        addScreen.setTitle(getString(R.string.dialog_add_screen_title));
        final EditText screenName = new EditText(this);
        screenName.setHint(getString(R.string.dialog_add_screen_edit_hint));
        addScreen.setMessage(getString(R.string.dialog_add_screen_message));
        addScreen.setView(screenName);
        addScreen.setNegativeButton(getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        addScreen.setPositiveButton(getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Screen newScreen = new Screen();
                newScreen.setName(screenName.getText().toString());
                newScreen.setPosition(app.getScreenArraySize());
                app.addScreen(newScreen);
                updatePager();
            }
        });
        addScreen.show();
    }

    private void addItemDialog() {
        AlertDialog.Builder addItem = new AlertDialog.Builder(this);
        addItem.setTitle(getString(R.string.dialog_add_item_title));

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_add_item, null);
        final EditText itemName = (EditText) dialogLayout.findViewById(R.id.dialog_add_item_input);
        final RadioGroup itemType = (RadioGroup) dialogLayout.findViewById(R.id.dialog_add_item_radiogroup);

        addItem.setView(dialogLayout);
        addItem.setNegativeButton(getString(R.string.dialog_btn_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        addItem.setPositiveButton(getString(R.string.dialog_btn_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ScreenItem tempItem = new ScreenItem(app);
                tempItem.setName(itemName.getText().toString());
                tempItem.setScreenID(app.getScreenByPosition(screenPager.getCurrentItem() - 1).getScreenID());
                tempItem.setPosition(app.getScreenItemPosition(screenPager.getCurrentItem()));
                switch (itemType.getCheckedRadioButtonId()) {
                    case R.id.dialog_add_item_app:
                        tempItem.setType(Type.APPS);
                        break;
                    case R.id.dialog_add_item_widget:
                        tempItem.setType(Type.WIDGET);
                        break;
                }
                app.addScreenItem(tempItem);
                updatePager();
            }
        });
        addItem.show();
    }

    public void removeScreen(final Screen screen) {
        AlertDialog.Builder removeScreen = new AlertDialog.Builder(this);
        removeScreen.setTitle(getString(R.string.dialog_remove_screen_title));
        removeScreen.setMessage(getString(R.string.dialog_remove_screen_message));
        removeScreen.setNegativeButton(getString(R.string.dialog_btn_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        removeScreen.setPositiveButton(getString(R.string.dialog_btn_yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                app.removeScreen(screen);
                updatePager();
            }
        });
        removeScreen.show();
    }

    private void setFavoriteDialog(final int pos) {
        final AlertDialog listDialog;
        AlertDialog.Builder listBuilder = new AlertDialog.Builder(this);
        listBuilder.setTitle(getString(R.string.dialog_item_add_app_title));

        GridView appGrid = new GridView(this);
        appGrid.setNumColumns(3);
        AppDataAdapter gridAdapter = new AppDataAdapter(this, R.layout.list_item_app, app.getApps());
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
        //TODO: Add Tablet favorite buttons too
        ImageButton allApps = (ImageButton) findViewById(R.id.buttonbar_btn_drawer);
        ImageButton favorite1 = (ImageButton) findViewById(R.id.buttonbar_btn_fav1);
        ImageButton favorite2 = (ImageButton) findViewById(R.id.buttonbar_btn_fav2);
        ImageButton favorite3 = (ImageButton) findViewById(R.id.buttonbar_btn_fav3);
        ImageButton favorite4 = (ImageButton) findViewById(R.id.buttonbar_btn_fav4);
        ImageButton favorite5 = (ImageButton) findViewById(R.id.buttonbar_btn_fav5);
        ImageButton btnSettings = (ImageButton) findViewById(R.id.buttonbar_btn_settings);

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
        btnSettings.setOnClickListener(this);
        setFavoriteBtns();
    }

    private void setFavoriteBtns(){
        ImageButton favorite1 = (ImageButton) findViewById(R.id.buttonbar_btn_fav1);
        ImageButton favorite2 = (ImageButton) findViewById(R.id.buttonbar_btn_fav2);
        ImageButton favorite3 = (ImageButton) findViewById(R.id.buttonbar_btn_fav3);
        ImageButton favorite4 = (ImageButton) findViewById(R.id.buttonbar_btn_fav4);
        ImageButton favorite5 = (ImageButton) findViewById(R.id.buttonbar_btn_fav5);

        if(app.getFavorite(0) != null){
            favorite1.setImageDrawable(app.getFavorite(0).getContent().getIcon().getConstantState().newDrawable());
        }
        if(app.getFavorite(1) != null){
            favorite2.setImageDrawable(app.getFavorite(1).getContent().getIcon().getConstantState().newDrawable());
        }
        if(app.getFavorite(2) != null){
            favorite3.setImageDrawable(app.getFavorite(2).getContent().getIcon().getConstantState().newDrawable());
        }
        if(app.getFavorite(3) != null){
            favorite4.setImageDrawable(app.getFavorite(3).getContent().getIcon().getConstantState().newDrawable());
        }
        if(app.getFavorite(4) != null){
            favorite5.setImageDrawable(app.getFavorite(4).getContent().getIcon().getConstantState().newDrawable());
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
                case R.id.buttonbar_btn_drawer:
                    Intent drawer = new Intent(this, Drawer.class);
                    startActivity(drawer);
                    overridePendingTransition(R.animator.zoom_enter, R.animator.zoom_exit);
                    break;
                case R.id.buttonbar_btn_settings:
                    QuickAction quickAction = new QuickAction(this);
                    quickAction.addActionItem(new ActionItem(0, this.getString(R.string.action_add_item)));
                    quickAction.addActionItem(new ActionItem(1, this.getString(R.string.action_add_screen)));
                    quickAction.addActionItem(new ActionItem(2, this.getString(R.string.action_remove_screen)));
                    quickAction.addActionItem(new ActionItem(3, this.getString(R.string.action_rename_screen)));
                    quickAction.addActionItem(new ActionItem(4, this.getString(R.string.action_change_wallpaper)));

                    quickAction.show(v, true);
                    quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
                        @Override
                        public void onItemClick(QuickAction source, int pos, int actionId) {
                            switch (actionId) {
                                case 0:
                                    addItemDialog();
                                    break;
                                case 1:
                                    addScreenDialog();
                                    break;
                                case 2:
                                    removeScreen(app.getScreenByPosition(screenPager.getCurrentItem() - 1));
                                    break;
                                case 3:
                                    renameScreen(app.getScreenByPosition(screenPager.getCurrentItem() - 1));
                                    break;
                                case 4:
                                    Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                                    startActivity(Intent.createChooser(intent, "Select Wallpaper"));
                                    break;
                            }
                        }
                    });
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
            default:
                return false;
        }
    }


    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class ScreenPagerAdapter extends FragmentStatePagerAdapter {

        public ScreenPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SocialScreenFragment();
                default:
                    return app.getScreenByPosition(position - 1).getView();

            }
        }

        @Override
        public int getCount() {
            // We do a +1 because of the (now) static "Social" screen
            return app.getScreenArraySize() + 1;
        }


        @Override
        public int getItemPosition(Object object) {
            //TODO: This should have extra checks (See http://stackoverflow.com/a/10852046)
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_social);
                default:
                    return app.getScreenByPosition(position - 1).getName();
            }
        }
    }

    public static class SocialScreenFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //TODO: Check, if the Scl. is installed, if so, show actual social stuff, if not, add download button
            View rootView = inflater.inflate(R.layout.fragment_social, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.fragment_social_txt_placeholder);
            dummyTextView.setText("Social Screen placeholder");
            return rootView;
        }
    }

}

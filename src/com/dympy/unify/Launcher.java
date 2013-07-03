package com.dympy.unify;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dympy.unify.model.AppData;
import com.dympy.unify.model.Favorite;
import com.dympy.unify.model.Screen;
import com.dympy.unify.model.ScreenItem;
import com.dympy.unify.model.ScreenItem.Type;
import com.dympy.unify.view.AppAdapter;
import com.dympy.unify.view.ItemAdapter;

import java.util.ArrayList;

public class Launcher extends FragmentActivity implements OnClickListener, View.OnLongClickListener {
    private ArrayList<ScreenFragment> screenFragments;
    private ViewPager workspacePager;
    private LauncherApplication app;
    private boolean longPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        getActionBar().setIcon(R.drawable.ab_unify);

        app = (LauncherApplication) getApplication();
        screenFragments = new ArrayList<ScreenFragment>();
        for (Screen screen : app.getScreens()) {
            ScreenFragment temp = new ScreenFragment();
            Bundle args = new Bundle();
            args.putInt("screen_id", screen.getScreenID());
            temp.setArguments(args);
            screenFragments.add(temp);
            if(screen.getPosition() == 0){
                getActionBar().setTitle(screen.getName());
            }
        }

        final WorkspaceAdapter adapter = new WorkspaceAdapter(getSupportFragmentManager());

        workspacePager = (ViewPager) findViewById(R.id.pager);
        workspacePager.setOffscreenPageLimit(app.getScreenArraySize());
        workspacePager.setAdapter(adapter);
        workspacePager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                getActionBar().setTitle(adapter.getPageTitle(i));
            }
        });
        initButtons();
    }

    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * TODO: Research the difference between FragmentStatePagerAdapter and FragmentPagerAdapter (http://www.truiton.com/2013/06/android-fragmentpageradapter-vs-fragmentstatepageradapter/)
     */
    public class WorkspaceAdapter extends FragmentStatePagerAdapter {

        public WorkspaceAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return screenFragments.get(position);
        }

        @Override
        public int getCount() {
            return screenFragments.size();
        }


        @Override
        public int getItemPosition(Object object) {
            //TODO: This should have extra checks (See http://stackoverflow.com/a/10852046)
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return screenFragments.get(position).getScreenTitle();
        }
    }

    public class ScreenFragment extends ListFragment {
        private ArrayList<ScreenItem> items = new ArrayList<ScreenItem>();
        private String screenTitle = "";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            int workspaceID = getArguments().getInt("screen_id");
            LauncherApplication app = (LauncherApplication) getApplication();
            items = app.getScreen(workspaceID).getItems();
            screenTitle = app.getScreen(workspaceID).getName();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            Log.d("ScreenFragment", "onActivityCreated called");

            getListView().setPadding(getResources().getDimensionPixelOffset(R.dimen.workspace_padding), 0,
                    getResources().getDimensionPixelOffset(R.dimen.workspace_padding), 0);
            getListView().setDivider(null);
            getListView().setDividerHeight(getResources().getDimensionPixelOffset(R.dimen.item_workspace_divider));
            getListView().setFooterDividersEnabled(true);
            getListView().setVerticalScrollBarEnabled(false);

            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getResources().getDimensionPixelOffset(R.dimen.workspace_padding));
            View footer = new View(getActivity());
            footer.setLayoutParams(layoutParams);
            getListView().addFooterView(footer);

            setListAdapter(new ItemAdapter(getActivity(), items.toArray(new ScreenItem[items.size()])));
        }

        public String getScreenTitle() {
            return screenTitle;
        }
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
                removeScreen(app.getScreenByPosition(workspacePager.getCurrentItem()));
                return true;
            case R.id.action_rename_screen:
                renameScreen(app.getScreenByPosition(workspacePager.getCurrentItem()));
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
        //We don't want to exit this screen!
    }

    public void updatePager() {
        workspacePager.getAdapter().notifyDataSetChanged();
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
                tempItem.setScreenID(app.getScreenByPosition(workspacePager.getCurrentItem()).getScreenID());
                tempItem.setPosition(app.getScreenItemPosition(workspacePager.getCurrentItem()));
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
        //TODO: Add Tablet favorite buttons too
        ImageButton allApps = (ImageButton) findViewById(R.id.buttonbar_btn_drawer);
        ImageButton favorite1 = (ImageButton) findViewById(R.id.buttonbar_btn_fav1);
        ImageButton favorite2 = (ImageButton) findViewById(R.id.buttonbar_btn_fav2);
        ImageButton favorite3 = (ImageButton) findViewById(R.id.buttonbar_btn_fav3);
        ImageButton favorite4 = (ImageButton) findViewById(R.id.buttonbar_btn_fav4);
        ImageButton favorite5 = (ImageButton) findViewById(R.id.buttonbar_btn_fav5);

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
        setFavoriteBtns();
    }

    private void setFavoriteBtns() {
        ImageButton favorite1 = (ImageButton) findViewById(R.id.buttonbar_btn_fav1);
        ImageButton favorite2 = (ImageButton) findViewById(R.id.buttonbar_btn_fav2);
        ImageButton favorite3 = (ImageButton) findViewById(R.id.buttonbar_btn_fav3);
        ImageButton favorite4 = (ImageButton) findViewById(R.id.buttonbar_btn_fav4);
        ImageButton favorite5 = (ImageButton) findViewById(R.id.buttonbar_btn_fav5);

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
}

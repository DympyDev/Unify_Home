package com.dympy.endless;

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
import com.dympy.endless.apps.Drawer;
import com.dympy.endless.screen.Screen;
import com.dympy.endless.screen.ScreenItem;
import com.dympy.endless.screen.ScreenItem.Type;

public class Launcher extends FragmentActivity implements OnClickListener {

    private ViewPager screenPager;
    private LauncherApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        app = (LauncherApplication) getApplication();

        SectionsPagerAdapter screensAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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
                removeScreen(app.getScreenByPosition(screenPager.getCurrentItem() - 1));
                return true;
            case R.id.action_change_wallpaper:
                Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                startActivity(Intent.createChooser(intent, "Select Wallpaper"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updatePager() {
        screenPager.getAdapter().notifyDataSetChanged();
    }

    private void addScreenDialog() {
        AlertDialog.Builder addScreen = new AlertDialog.Builder(this);
        addScreen.setTitle(getString(R.string.dialog_add_screen_title));
        final EditText screenName = new EditText(this);
        screenName.setHint(getString(R.string.dialog_add_screen_edit_hint));
        addScreen.setMessage(getString(R.string.dialog_add_screen_message));
        addScreen.setView(screenName);
        addScreen.setNegativeButton(getString(R.string.dialog_btn_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
        addScreen.setPositiveButton(getString(R.string.dialog_btn_ok),
                new DialogInterface.OnClickListener() {

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
        final EditText itemName = (EditText) dialogLayout
                .findViewById(R.id.dialog_add_item_input);
        final RadioGroup itemType = (RadioGroup) dialogLayout
                .findViewById(R.id.dialog_add_item_radiogroup);

        addItem.setView(dialogLayout);
        addItem.setNegativeButton(getString(R.string.dialog_btn_cancel),
                new DialogInterface.OnClickListener() {
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
                tempItem.setPosition(app.getScreenItemPosition(screenPager
                        .getCurrentItem()));
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
        removeScreen.setNegativeButton(getString(R.string.dialog_btn_no),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
        removeScreen.setPositiveButton(getString(R.string.dialog_btn_yes),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        app.removeScreen(screen);
                        updatePager();
                    }
                });
        removeScreen.show();
    }

    private void initButtons() {
        ImageButton allApps = (ImageButton) findViewById(R.id.all_apps);
        ImageButton hotseat1 = (ImageButton) findViewById(R.id.hotseat_1);
        ImageButton hotseat2 = (ImageButton) findViewById(R.id.hotseat_2);
        ImageButton hotseat3 = (ImageButton) findViewById(R.id.hotseat_3);
        ImageButton hotseat4 = (ImageButton) findViewById(R.id.hotseat_4);
        ImageButton hotseat5 = (ImageButton) findViewById(R.id.hotseat_5);
        ImageButton hotseat6 = (ImageButton) findViewById(R.id.hotseat_6);

        allApps.setOnClickListener(this);
        hotseat1.setOnClickListener(this);
        hotseat2.setOnClickListener(this);
        hotseat3.setOnClickListener(this);
        hotseat4.setOnClickListener(this);
        if (hotseat5 != null && hotseat6 != null) {
            hotseat5.setOnClickListener(this);
            hotseat6.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_apps:
                Intent drawer = new Intent(this, Drawer.class);
                startActivity(drawer);
                overridePendingTransition(R.animator.zoom_enter,
                        R.animator.zoom_exit);
                break;
            default:
                Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT)
                        .show();
                break;
        }
    }

    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    // TODO: The actual social fragment
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER,
                            position + 1);
                    fragment.setArguments(args);
                    return fragment;
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

    public static class DummySectionFragment extends Fragment {
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_launcher_dummy,
                    container, false);
            TextView dummyTextView = (TextView) rootView
                    .findViewById(R.id.section_label);
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                dummyTextView.setText("Social Screen placeholder");
            } else {
                dummyTextView.setText(Integer.toString(getArguments().getInt(
                        ARG_SECTION_NUMBER)));
            }
            return rootView;
        }
    }

}

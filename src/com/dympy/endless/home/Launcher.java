package com.dympy.endless.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dympy.endless.R;
import com.dympy.endless.home.apps.Drawer;
import com.dympy.endless.home.workspace.WorkspaceItem;

public class Launcher extends FragmentActivity implements OnClickListener {

	private SectionsPagerAdapter workspaceScreenAdapter;
	private ViewPager workspaceScreens;
	private LauncherModel application;

	private ImageButton allApps;
	private ImageButton hotseat1;
	private ImageButton hotseat2;
	private ImageButton hotseat3;
	private ImageButton hotseat4;
	private ImageButton hotseat5;
	private ImageButton hotseat6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);

		application = (LauncherModel) getApplication();

		workspaceScreenAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		PagerTabStrip workspaceScreenTabs = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
		workspaceScreenTabs.setTabIndicatorColor(0x3B3B3B);
		workspaceScreenTabs.setDrawFullUnderline(true);

		workspaceScreens = (ViewPager) findViewById(R.id.pager);
		workspaceScreens.setAdapter(workspaceScreenAdapter);
		workspaceScreens.setCurrentItem(1);
		initButtons();
		// TODO: Lookup how to add items to the action bar
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addItemDialog() {
		// TODO: Move strings to strings file
		AlertDialog.Builder addItem = new AlertDialog.Builder(this);
		addItem.setTitle("Add Item");

		LayoutInflater inflater = getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_add_item, null);
		final EditText itemName = (EditText) dialoglayout
				.findViewById(R.id.dialog_add_item_input);
		final RadioGroup itemType = (RadioGroup) dialoglayout
				.findViewById(R.id.dialog_add_item_radiogroup);

		addItem.setView(dialoglayout);
		addItem.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		addItem.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (itemType.getCheckedRadioButtonId()) {
				case R.id.dialog_add_item_app:
					application.addItemToWorkspace(itemName.getText()
							.toString(), WorkspaceItem.Type.APPS,
							workspaceScreens.getCurrentItem());
					break;
				case R.id.dialog_add_item_widget:
					application.addItemToWorkspace(itemName.getText()
							.toString(), WorkspaceItem.Type.WIDGET,
							workspaceScreens.getCurrentItem());
					break;
				}
			}
		});
		addItem.show();
	}

	private void initButtons() {
		allApps = (ImageButton) findViewById(R.id.all_apps);
		hotseat1 = (ImageButton) findViewById(R.id.hotseat_1);
		hotseat2 = (ImageButton) findViewById(R.id.hotseat_2);
		hotseat3 = (ImageButton) findViewById(R.id.hotseat_3);
		hotseat4 = (ImageButton) findViewById(R.id.hotseat_4);
		hotseat5 = (ImageButton) findViewById(R.id.hotseat_5);
		hotseat6 = (ImageButton) findViewById(R.id.hotseat_6);

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
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
				return application.getWorkspace(position - 1).getView();

			}
		}

		@Override
		public int getCount() {
			// We do a +1 because of the (now) static "Social" screen
			return application.getWorkspaceScreenSize() + 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_social);
			default:
				return application.getWorkspace(position - 1).getScreenName();
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

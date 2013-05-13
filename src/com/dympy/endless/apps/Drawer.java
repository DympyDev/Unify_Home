package com.dympy.endless.apps;

import com.dympy.endless.LauncherModel;
import com.dympy.endless.R;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.app.Activity;

public class Drawer extends Activity {
	private LauncherModel application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);
		application = (LauncherModel) getApplication();

		if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		GridView appGrid = (GridView) findViewById(R.id.activity_drawer_appgrid);
		AppDataAdapter adapter = new AppDataAdapter(this,
				R.layout.list_item_app, application.getApps());
		appGrid.setAdapter(adapter);
		appGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				startActivity(application.getApps().get(position)
						.getAppIntent());

			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.animator.zoom_enter, R.animator.zoom_exit);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}

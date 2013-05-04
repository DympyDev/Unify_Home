package com.dympy.endless.home.workspace;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
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
import com.dympy.endless.home.apps.AppData;
import com.dympy.endless.home.apps.AppDataAdapter;
import com.dympy.endless.home.ui.CustomGrid;

public class WorkspaceItem {
	public enum Type {
		WIDGET, APPS
	}

	private int workspaceID;
	private Type itemType;
	private String itemTitle;
	private ArrayList<AppData> apps;
	private AppWidgetProviderInfo widget;
	private Context context;

	private boolean longPressed;
	private WorkspaceItem instance;
	private LauncherModel app;
	private Context viewContext;
	private TextView itemViewTitle;
	private ImageButton itemViewSettings;
	private LinearLayout widgetViewContent;
	private CustomGrid appViewGrid;

	public WorkspaceItem(Context context) {
		this.context = context;
		this.app = (LauncherModel) context;
		this.longPressed = false;
		this.instance = this;
	}

	public int getWorkspaceID() {
		return workspaceID;
	}

	public void setWorkspaceID(int workspaceID) {
		this.workspaceID = workspaceID;
	}

	public WorkspaceItem(Type itemType) {
		this.itemType = itemType;
		switch (itemType) {
		case WIDGET:
			// TODO: Not yet implemented
			break;
		case APPS:
			this.apps = new ArrayList<AppData>();
			break;
		}
	}

	public Type getItemType() {
		return itemType;
	}

	public void setItemType(Type itemType) {
		this.itemType = itemType;
		switch (itemType) {
		case WIDGET:
			// TODO: Not yet implemented
			break;
		case APPS:
			this.apps = new ArrayList<AppData>();
			break;
		}
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public ArrayList<AppData> getApps() {
		return apps;
	}

	public void setApps(ArrayList<AppData> apps) {
		this.apps = apps;
	}

	public void addApp(AppData app) {
		this.apps.add(app);
	}

	public void removeApp(AppData app) {
		this.apps.remove(app);
	}

	// TODO: Fix the whole widget part
	public AppWidgetHostView getWidgetView() {
		AppWidgetHost appWidgetHost = new AppWidgetHost(context, 0);
		int appWidgetId = appWidgetHost.allocateAppWidgetId();
		AppWidgetHostView hostView = appWidgetHost.createView(context,
				appWidgetId, widget);
		hostView.setAppWidget(appWidgetId, widget);
		return hostView;
	}

	public AppWidgetProviderInfo getWidget() {
		return widget;
	}

	public void setWidget(AppWidgetProviderInfo widget) {
		this.widget = widget;
	}

	public View getView(ViewGroup parent, Context context) {
		viewContext = context;
		LayoutInflater inflater = ((Activity) viewContext).getLayoutInflater();
		View row = inflater
				.inflate(R.layout.list_item_workspace, parent, false);
		itemViewTitle = (TextView) row
				.findViewById(R.id.item_workspace_txt_title);
		itemViewSettings = (ImageButton) row
				.findViewById(R.id.item_workspace_btn_settings);
		widgetViewContent = (LinearLayout) row
				.findViewById(R.id.item_workspace_layout_widget);
		appViewGrid = (CustomGrid) row
				.findViewById(R.id.item_workspace_grid_apps);

		itemViewTitle.setText(getItemTitle());
		itemViewSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSettingsDialog();
			}
		});

		if (getItemType() == Type.APPS) {
			AppDataAdapter appsAdapter = new AppDataAdapter(viewContext,
					R.layout.list_item_app, getApps());
			appViewGrid.setExpanded(true);
			appViewGrid.setAdapter(appsAdapter);
			appViewGrid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					if (longPressed) {
						longPressed = false;
					} else {
						viewContext.startActivity(getApps().get(position)
								.getAppIntent());
					}
				}
			});
			appViewGrid
					.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View v, int position, long id) {
							longPressed = true;
							removeAppDialog(position);
							return false;
						}
					});
		} else if (getItemType() == Type.WIDGET) {
			widgetViewContent.addView(getWidgetView());
		}
		return row;
	}

	private void showSettingsDialog() {
		//TODO: Move strings to strings file
		final AlertDialog actualDialog;
		AlertDialog.Builder itemSettings = new AlertDialog.Builder(viewContext);
		itemSettings.setTitle("Item Settings");

		String[] settings = { "Add app", "Remove item" };
		ListView settingsContent = new ListView(viewContext);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(viewContext,
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
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				actualDialog.dismiss();
				switch (position) {
				case 0:
					addAppDialog();
					break;
				case 1:
					break;
				}
			}
		});
	}

	private void removeAppDialog(final int appPos) {
		//TODO: Move strings to strings file
		AlertDialog.Builder removeApp = new AlertDialog.Builder(viewContext);
		removeApp.setTitle("Remove app");
		removeApp
				.setMessage("Are you sure you want to remove this app from the workspace?");
		removeApp.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});
		removeApp.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						String appName = getApps().get(appPos).getAppName();
						app.removeAppFromItem(instance, getApps().get(appPos));
						appViewGrid.setAdapter(new AppDataAdapter(viewContext,
								R.layout.list_item_app, getApps()));
						Toast.makeText(viewContext,
								"Removed '" + appName + "'", Toast.LENGTH_SHORT)
								.show();
					}
				});
		removeApp.show();
	}

	private void addAppDialog() {
		//TODO: Move strings to strings file
		final AlertDialog listDialog;
		AlertDialog.Builder listBuilder = new AlertDialog.Builder(viewContext);
		listBuilder.setTitle("Select an app");

		GridView appGrid = new GridView(viewContext);
		appGrid.setNumColumns(3);
		AppDataAdapter gridAdapter = new AppDataAdapter(viewContext,
				R.layout.list_item_app, app.getApps());
		appGrid.setAdapter(gridAdapter);
		listBuilder.setView(appGrid);
		listDialog = listBuilder.create();

		appGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				listDialog.dismiss();
				app.addAppToItem(instance, app.getApps().get(position));
				appViewGrid.setAdapter(new AppDataAdapter(viewContext,
						R.layout.list_item_app, getApps()));
			}
		});

		listDialog.show();
	}

}

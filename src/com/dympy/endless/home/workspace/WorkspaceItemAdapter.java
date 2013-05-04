package com.dympy.endless.home.workspace;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class WorkspaceItemAdapter extends ArrayAdapter<WorkspaceItem> {
	private Context context;
	private ArrayList<WorkspaceItem> data = null;

	public WorkspaceItemAdapter(Context context, int layoutResourceId,
			ArrayList<WorkspaceItem> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return data.get(position).getView(parent, context);
	}
}

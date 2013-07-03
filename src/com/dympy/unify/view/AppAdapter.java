package com.dympy.unify.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dympy.unify.R;
import com.dympy.unify.model.AppData;

public class AppAdapter extends ArrayAdapter<AppData> {
	Context context;
	int layoutResourceId;
	ArrayList<AppData> data = null;

	public AppAdapter(Context context, int layoutResourceId,
                      ArrayList<AppData> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		AppDataHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new AppDataHolder();
			holder.txtTitle = (TextView) row
					.findViewById(R.id.txt_appitem_name);

			row.setTag(holder);
		} else {
			holder = (AppDataHolder) row.getTag();
		}
		AppData app = data.get(position);
		holder.txtTitle.setText(app.getName());
		holder.txtTitle.setCompoundDrawables(null, app.getIcon(), null, null);

		return row;
	}

	static class AppDataHolder {
		TextView txtTitle;
	}
}

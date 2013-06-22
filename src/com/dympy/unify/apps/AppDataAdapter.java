package com.dympy.unify.apps;

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

public class AppDataAdapter extends ArrayAdapter<AppData> {
	Context context;
	int layoutResourceId;
	ArrayList<AppData> data = null;

	public AppDataAdapter(Context context, int layoutResourceId,
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
		Drawable icon = app.getIcon();
		icon.setBounds(
				0,
				0,
				context.getResources().getDimensionPixelOffset(
						R.dimen.icon_size), context.getResources()
						.getDimensionPixelOffset(R.dimen.icon_size));
		holder.txtTitle.setCompoundDrawables(null, icon, null, null);

		return row;
	}

	static class AppDataHolder {
		TextView txtTitle;
	}
}

package com.dympy.endless.screen;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dympy.endless.R;

public class ItemAppAdapter extends ArrayAdapter<ScreenItemApp> {
	int layoutResourceId;
	private Context context;
	private ArrayList<ScreenItemApp> data = null;

	public ItemAppAdapter(Context context, int layoutResourceId,
			ArrayList<ScreenItemApp> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ItemAppHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new ItemAppHolder();
			holder.txtTitle = (TextView) row
					.findViewById(R.id.txt_appitem_name);

			row.setTag(holder);
		} else {
			holder = (ItemAppHolder) row.getTag();
		}
		ScreenItemApp app = data.get(position);
		holder.txtTitle.setText(app.getName());
		Drawable icon = app.getAppData().getIcon();
		icon.setBounds(
				0,
				0,
				context.getResources().getDimensionPixelOffset(
						R.dimen.icon_size), context.getResources()
						.getDimensionPixelOffset(R.dimen.icon_size));
		holder.txtTitle.setCompoundDrawables(null, icon, null, null);

		return row;
	}

	static class ItemAppHolder {
		TextView txtTitle;
	}
}

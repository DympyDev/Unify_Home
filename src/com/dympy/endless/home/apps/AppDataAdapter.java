
package com.dympy.endless.home.apps;

import java.util.ArrayList;

import com.dympy.endless.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
            holder.imgIcon = (ImageView) row
                    .findViewById(R.id.img_appitem_icon);
            holder.txtTitle = (TextView) row
                    .findViewById(R.id.txt_appitem_name);

            row.setTag(holder);
        } else {
            holder = (AppDataHolder) row.getTag();
        }

        AppData app = data.get(position);
        holder.txtTitle.setText(app.getAppName());
        holder.imgIcon.setImageDrawable(app.getAppIcon());

        return row;
    }

    static class AppDataHolder {
        ImageView imgIcon;
        TextView txtTitle;
    }
}

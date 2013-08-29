package com.dympy.unify.view;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dympy.unify.Launcher;
import com.dympy.unify.R;
import com.dympy.unify.model.AppData;
import com.dympy.unify.model.Item;
import com.dympy.unify.model.ItemApp;
import com.dympy.unify.view.custom.CustomGrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Dymion on 2-7-13.
 */
public class ItemAdapter extends BaseAdapter {
    private final Activity context;
    private final Item[] items;

    ArrayList<ItemHolder> itemHolders = new ArrayList<ItemHolder>();

    static class ItemHolder {
        public View sideLine;
        public View bottomLine;
        public TextView title;
        public CustomGrid appGrid;
        public Item instance;
    }

    public ItemAdapter(Activity context, Item[] objects) {
        this.context = context;
        this.items = objects;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Item getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        Item item = items[position];
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_workspace, null);
            ItemHolder itemHolder = new ItemHolder();
            itemHolder.sideLine = rowView.findViewById(R.id.item_workspace_view_sideline);
            itemHolder.bottomLine = rowView.findViewById(R.id.item_workspace_view_bottomline);
            itemHolder.title = (TextView) rowView.findViewById(R.id.item_workspace_txt_title);
            itemHolder.appGrid = (CustomGrid) rowView.findViewById(R.id.item_workspace_grid_apps);
            itemHolder.instance = item;
            itemHolders.add(itemHolder);
            rowView.setTag(itemHolder);
        }

        final ItemHolder holder = (ItemHolder) rowView.getTag();

        holder.sideLine.setBackgroundColor(item.getAccentColor());
        holder.bottomLine.setBackgroundColor(item.getAccentColor());

        holder.title.setText(item.getName());
        holder.title.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((Launcher) context).addItemDialog(holder.instance, context);
            }
        });
        if (holder.appGrid != null && item.getApps().size() > 0) {
            Collections.sort(item.getApps(), new AppComparator());
            ArrayList<AppData> apps = new ArrayList<AppData>();
            for (ItemApp app : item.getApps()) {
                apps.add(app.getAppData());
            }
            holder.appGrid.setExpanded(true);
            holder.appGrid.setAdapter(new AppAdapter(context, R.layout.item_app, apps));
            holder.appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    Log.d("ItemAdapter", "Number of apps: " + holder.instance.getApps().size());
                    context.startActivity(holder.instance.getApps().get(position).getAppData().getIntent());
                }
            });
        }
        return rowView;
    }

    public class AppComparator implements Comparator<ItemApp> {
        @Override
        public int compare(ItemApp screen, ItemApp screen2) {
            return screen.getPosition() > screen2.getPosition() ? +1 : screen.getPosition() < screen2.getPosition() ? -1 : 0;
        }
    }
}

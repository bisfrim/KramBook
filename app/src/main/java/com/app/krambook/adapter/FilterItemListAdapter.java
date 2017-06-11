/**
 *
 */
package com.app.krambook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.krambook.R;
import com.app.krambook.models.Filter;
import com.app.krambook.other.CheckableRelativeLayout;

import java.util.List;

/**
 * Adapter that allows us to render a list of items
 *
 * @author marvinlabs
 */
public class FilterItemListAdapter extends ArrayAdapter<Filter> {

    private LayoutInflater li;

    /**
     * Constructor from a list of items
     */
    public FilterItemListAdapter(Context context, List<Filter> items) {
        super(context, 0, items);
        li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // The item we want to get the view for
        // --
        final Filter item = getItem(position);

        // Re-use the view if possible
        // --
        ViewHolder holder;
        if (convertView == null) {
            convertView = li.inflate(R.layout.filter_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.holder, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.holder);
        }

        // Set some view properties
        holder.filtertext.setText("" + item.getFiltername());


        // Restore the checked state properly
        final ListView lv = (ListView) parent;
        holder.layout.setChecked(lv.isItemChecked(position));

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        public TextView filtertext;
        public CheckableRelativeLayout layout;

        public ViewHolder(View root) {
            filtertext= (TextView) root.findViewById(R.id.filter_textView);

            layout = (CheckableRelativeLayout) root.findViewById(R.id.layout);
        }
    }
}

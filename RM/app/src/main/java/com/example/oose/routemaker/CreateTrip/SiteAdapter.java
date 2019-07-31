package com.example.oose.routemaker.CreateTrip;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.example.oose.routemaker.Concrete.Site;
import com.example.oose.routemaker.R;

import java.util.List;

/**
 * Adapter for site list.
 */
public class SiteAdapter extends BaseAdapter {

    /** List of sites to connect to Adapter */
    private List<Site> siteList;

    /** The context in which the adapter is used */
    private final Context context;

    /**
     * Constructor.
     * @param context the context in which the adapter is used
     * @param siteList list of sites to connect to adapter
     */
    public SiteAdapter(Context context, List<Site> siteList) {
        this.siteList = siteList;
        this.context = context;
    }

    /** Get the number of elements in the site list */
    @Override
    public int getCount() {
        return siteList.size();
    }

    /** Get the item in the position */
    @Override
    public Object getItem(int position) {
        return siteList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    /**
     * Creates view using the adapter
     * @param position the position of item in the list
     * @param convertView the old view to reuse, if not null
     * @param parent the parent that this view will be attached to
     * @return a view of the position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.site_item, null);
            holder = new ViewHolder();
            holder.siteName = (CheckedTextView) convertView.findViewById(R.id.site_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.siteName.setText(siteList.get(position).getSiteName());
        return convertView;
    }

    /** Inner class view object */
    static class ViewHolder {
        CheckedTextView siteName;
    }
}


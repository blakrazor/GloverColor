package com.achanr.glovercolorapp.ui.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 4/27/16 4:05 PM
 */
public class GCCollectionsListItemViewHolder extends RecyclerView.ViewHolder {

    public Context mContext;
    public TextView txtTitle;
    public TextView txtDesc;

    public GCCollectionsListItemViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        txtTitle = (TextView) itemView.findViewById(R.id.list_item_collections_title);
        txtDesc = (TextView) itemView.findViewById(R.id.list_item_collections_desc);
    }
}

package com.achanr.glovercolorapp.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/11/16 2:18 PM
 */
public class GCSavedSetListItemViewHolder extends RecyclerView.ViewHolder {

    public TextView txtTitle;
    public TextView txtColors;
    public TextView txtMode;
    public RelativeLayout rlSavedSetItem;
    public int position;

    public ImageView shareButton;
    public ImageView editButton;
    public ImageView deleteButton;

    public GCSavedSetListItemViewHolder(View itemView) {
        super(itemView);
        txtTitle = (TextView) itemView.findViewById(R.id.list_item_saved_set_title);
        txtColors = (TextView) itemView.findViewById(R.id.list_item_saved_set_desc_colors);
        txtMode = (TextView) itemView.findViewById(R.id.list_item_saved_set_desc_mode);
        rlSavedSetItem = (RelativeLayout) itemView.findViewById(R.id.list_item_saved_set_layout);
        shareButton = (ImageView) itemView.findViewById(R.id.share_card_action);
        editButton = (ImageView) itemView.findViewById(R.id.edit_card_action);
        deleteButton = (ImageView) itemView.findViewById(R.id.delete_card_action);
    }
}

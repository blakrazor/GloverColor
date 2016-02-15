package com.achanr.glovercolorapp.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.activities.GCSavedSetListActivity;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.utility.GCUtil;

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
    public GCSavedSet mSavedSet;
    public Context mContext;

    public ImageView shareButton;
    public ImageView editButton;
    //public ImageView deleteButton;

    public GCSavedSetListItemViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        txtTitle = (TextView) itemView.findViewById(R.id.list_item_saved_set_title);
        txtColors = (TextView) itemView.findViewById(R.id.list_item_saved_set_desc_colors);
        txtMode = (TextView) itemView.findViewById(R.id.list_item_saved_set_desc_mode);
        rlSavedSetItem = (RelativeLayout) itemView.findViewById(R.id.list_item_saved_set_layout);
        shareButton = (ImageView) itemView.findViewById(R.id.share_card_action);
        editButton = (ImageView) itemView.findViewById(R.id.edit_card_action);
        //deleteButton = (ImageView) itemView.findViewById(R.id.delete_card_action);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GCUtil.showShareDialog(mContext, mSavedSet);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof GCSavedSetListActivity) {
                    ((GCSavedSetListActivity) mContext).onSavedSetListItemClicked(mSavedSet);
                }
            }
        });
    }
}

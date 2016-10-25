package com.achanr.glovercolorapp.ui.views;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.ui.activities.GCSyncConflictActivity;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

/**
 * @author Andrew Chanrasmi on 10/25/16
 */

public class GCSyncConflictsListItemViewHolder extends SwappingHolder {

    public final TextView txtTitle;
    public final TextView txtColors;
    public final TextView txtMode;
    public final TextView txtChipset;
    private final Context mContext;
    private boolean isChecked;

    public GCSyncConflictsListItemViewHolder(Context context, View itemView, final MultiSelector multiSelector) {
        super(itemView, multiSelector);
        mContext = context;
        txtTitle = (TextView) itemView.findViewById(R.id.list_item_sync_conflicts_title);
        txtColors = (TextView) itemView.findViewById(R.id.list_item_sync_conflicts_desc_colors);
        txtMode = (TextView) itemView.findViewById(R.id.list_item_sync_conflicts_desc_mode);
        txtChipset = (TextView) itemView.findViewById(R.id.list_item_sync_conflicts_desc_chipset);

        itemView.findViewById(R.id.linear_layout_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = !isChecked;
                multiSelector.setSelected(GCSyncConflictsListItemViewHolder.this, isChecked);
                int color = isChecked ?
                        GCUtil.fetchAttributeColor(mContext, R.attr.colorPrimaryDark)
                        : GCUtil.fetchAttributeColor(mContext, R.attr.colorAccentLight);
                v.findViewById(R.id.linear_layout_background).setBackgroundColor(color); //ignore this error, it doesn't know what it's talking about
            }
        });
    }
}
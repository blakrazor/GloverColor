package com.achanr.glovercolorapp.ui.views;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.models.GCCollection;
import com.achanr.glovercolorapp.ui.activities.GCCollectionsActivity;

import java.util.HashMap;

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
    public TextView txtSetNum;
    public GCCollection mCollection;
    public CardView mCardView;

    public GCCollectionsListItemViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        txtTitle = (TextView) itemView.findViewById(R.id.list_item_collections_title);
        txtDesc = (TextView) itemView.findViewById(R.id.list_item_collections_desc);
        txtSetNum = (TextView) itemView.findViewById(R.id.list_item_collections_set_number);
        mCardView = (CardView) itemView;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.findViewById(R.id.linear_layout_background).setBackground(mContext.getDrawable(R.drawable.card_view_ripple));
        }

        itemView.findViewById(R.id.linear_layout_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateToEditItem();
            }
        });

        itemView.findViewById(R.id.linear_layout_background).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog();
                return true;
            }
        });
    }

    private void onNavigateToEditItem() {
        HashMap<String, View> transitionViews = new HashMap<String, View>();
        transitionViews.put(mContext.getString(R.string.transition_name_collection_title), txtTitle);
        transitionViews.put(mContext.getString(R.string.transition_name_collection_desc), txtDesc);
        transitionViews.put(mContext.getString(R.string.transition_name_collection_cardview), mCardView);
        ((GCCollectionsActivity) mContext).onEditCollectionListItemClicked(mCollection, transitionViews);
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.delete))
                .setMessage(mContext.getString(R.string.delete_collection))
                .setPositiveButton(mContext.getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((GCCollectionsActivity) mContext).onCollectionDeleted(mCollection);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_delete_black_48dp)
                .show();
    }
}

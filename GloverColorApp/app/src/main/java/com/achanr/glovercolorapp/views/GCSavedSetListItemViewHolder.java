package com.achanr.glovercolorapp.views;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.activities.GCSavedSetListActivity;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.utility.GCUtil;

import java.util.HashMap;

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
    public TextView txtChipset;
    public RelativeLayout rlSavedSetItem;
    public GCSavedSet mSavedSet;
    public Context mContext;
    public PopupMenu mPopupMenu;
    public CardView mCardView;

    public GCSavedSetListItemViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        txtTitle = (TextView) itemView.findViewById(R.id.list_item_saved_set_title);
        txtColors = (TextView) itemView.findViewById(R.id.list_item_saved_set_desc_colors);
        txtMode = (TextView) itemView.findViewById(R.id.list_item_saved_set_desc_mode);
        txtChipset = (TextView) itemView.findViewById(R.id.list_item_saved_set_desc_chipset);
        rlSavedSetItem = (RelativeLayout) itemView.findViewById(R.id.list_item_saved_set_layout);
        mCardView = (CardView) itemView;

        mPopupMenu = new PopupMenu(mContext, itemView.findViewById(R.id.saved_set_more_actions));
        mPopupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Share");
        mPopupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Edit");
        mPopupMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Delete");
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        GCUtil.showShareDialog(mContext, mSavedSet);
                        return true;
                    case 2:
                        if (mContext instanceof GCSavedSetListActivity) {
                            HashMap<String, View> transitionViews = new HashMap<String, View>();
                            transitionViews.put(mContext.getString(R.string.transition_name_saved_set_title), txtTitle);
                            transitionViews.put(mContext.getString(R.string.transition_name_saved_set_chip), txtChipset);
                            transitionViews.put(mContext.getString(R.string.transition_name_saved_set_mode), txtMode);
                            transitionViews.put(mContext.getString(R.string.transition_name_saved_set_colors), txtColors);
                            transitionViews.put(mContext.getString(R.string.transition_name_saved_set_cardview), mCardView);
                            ((GCSavedSetListActivity) mContext).onSavedSetListItemClicked(mSavedSet, transitionViews);
                        }
                        return true;
                    case 3:
                        if (mContext instanceof GCSavedSetListActivity) {
                            new AlertDialog.Builder(mContext)
                                    .setTitle(mContext.getString(R.string.delete))
                                    .setMessage(mContext.getString(R.string.delete_dialog))
                                    .setPositiveButton(mContext.getString(R.string.delete), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((GCSavedSetListActivity) mContext).onSetDeleted(mSavedSet);
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setIcon(R.drawable.ic_delete_black_48dp)
                                    .show();
                            return true;
                        }
                }
                return false;
            }
        });

        itemView.findViewById(R.id.saved_set_more_actions).

                setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           mPopupMenu.show();
                                       }
                                   }

                );
    }
}

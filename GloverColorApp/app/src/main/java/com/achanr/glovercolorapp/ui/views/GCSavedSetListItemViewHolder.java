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
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.activities.GCEditCollectionActivity;
import com.achanr.glovercolorapp.ui.activities.GCSavedSetListActivity;

import java.util.HashMap;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/11/16 2:18 PM
 */
public class GCSavedSetListItemViewHolder extends RecyclerView.ViewHolder {

    public final TextView txtTitle;
    public final TextView txtColors;
    public final TextView txtMode;
    public final TextView txtChipset;
    public GCSavedSet mSavedSet;
    private final Context mContext;
    //public PopupMenu mPopupMenu;
    private final CardView mCardView;

    public GCSavedSetListItemViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        txtTitle = (TextView) itemView.findViewById(R.id.list_item_saved_set_title);
        txtColors = (TextView) itemView.findViewById(R.id.list_item_saved_set_desc_colors);
        txtMode = (TextView) itemView.findViewById(R.id.list_item_saved_set_desc_mode);
        txtChipset = (TextView) itemView.findViewById(R.id.list_item_saved_set_desc_chipset);
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

        itemView.findViewById(R.id.share_card_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GCUtil.showShareDialog(mContext, mSavedSet);
            }
        });

        /*mPopupMenu = new PopupMenu(mContext, itemView.findViewById(R.id.saved_set_more_actions));
        mPopupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Edit");
        mPopupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "Share");
        mPopupMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, "Delete");
        mPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        if (mContext instanceof GCSavedSetListActivity) {
                            onNavigateToEditItem();
                        }
                        return true;
                    case 2:
                        GCUtil.showShareDialog(mContext, mSavedSet);
                        return true;
                    case 3:
                        if (mContext instanceof GCSavedSetListActivity) {
                            showDeleteDialog();
                        }
                        return true;
                }
                return false;
            }
        });

        itemView.findViewById(R.id.saved_set_more_actions).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupMenu.show();
                    }
                }

        );*/


    }

    private void onNavigateToEditItem() {
        HashMap<String, View> transitionViews = new HashMap<>();
        transitionViews.put(mContext.getString(R.string.transition_name_saved_set_title), txtTitle);
        transitionViews.put(mContext.getString(R.string.transition_name_saved_set_chip), txtChipset);
        transitionViews.put(mContext.getString(R.string.transition_name_saved_set_mode), txtMode);
        transitionViews.put(mContext.getString(R.string.transition_name_saved_set_colors), txtColors);
        transitionViews.put(mContext.getString(R.string.transition_name_saved_set_cardview), mCardView);
        if (mContext instanceof GCSavedSetListActivity) {
            ((GCSavedSetListActivity) mContext).onEditSetListItemClicked(mSavedSet, transitionViews);
        } else if (mContext instanceof GCEditCollectionActivity) {
            ((GCEditCollectionActivity) mContext).onEditSetListItemClicked(mSavedSet, transitionViews);
        }
    }

    private void showDeleteDialog() {
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
        } else if (mContext instanceof GCEditCollectionActivity) {
            new AlertDialog.Builder(mContext)
                    .setTitle("Remove Set")
                    .setMessage("Remove this set from the current collection?")
                    .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((GCEditCollectionActivity) mContext).onSetRemoved(mSavedSet);
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
}

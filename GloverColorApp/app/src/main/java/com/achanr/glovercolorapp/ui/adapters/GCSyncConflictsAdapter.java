package com.achanr.glovercolorapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.GCOnlineDatabaseUtil;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.models.GCChip;
import com.achanr.glovercolorapp.models.GCMode;
import com.achanr.glovercolorapp.models.GCOnlineDBSavedSet;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.viewHolders.GCSyncConflictsListItemViewHolder;
import com.bignerdranch.android.multiselector.MultiSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Chanrasmi on 10/25/16
 */

public class GCSyncConflictsAdapter extends RecyclerView.Adapter<GCSyncConflictsListItemViewHolder> {

    private List<GCOnlineDBSavedSet> mOnlineDBSavedSets;
    private final Context mContext;
    private MultiSelector mMultiSelector;

    public GCSyncConflictsAdapter(Context context, List<GCOnlineDBSavedSet> onlineDBSavedSets) {
        mOnlineDBSavedSets = new ArrayList<>(onlineDBSavedSets);
        mContext = context;

        mMultiSelector = new MultiSelector();
        mMultiSelector.setSelectable(true); // enter selection mode
    }

    @Override
    public GCSyncConflictsListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sync_conflicts, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new GCSyncConflictsListItemViewHolder(mContext, v, mMultiSelector);
    }

    @Override
    public void onBindViewHolder(GCSyncConflictsListItemViewHolder holder, final int position) {
        // - get element from your dataset at this mPosition
        // - replace the contents of the view with that element
        GCSavedSet savedSet = GCOnlineDatabaseUtil.convertToSavedSet(mContext, mOnlineDBSavedSets.get(position));
        String title = savedSet.getTitle();
        GCMode mode = savedSet.getMode();
        GCChip chipSet = savedSet.getChipSet();
        SpannableStringBuilder builder = GCUtil.generateMultiColoredString(savedSet);
        holder.txtTitle.setText(title);
        holder.txtColors.setText(builder, TextView.BufferType.SPANNABLE);
        holder.txtMode.setText(GCUtil.convertToCamelcase(mContext, mode.getTitle()));
        if (chipSet.getTitle().equalsIgnoreCase("NONE")) {
            holder.txtChipset.setText("No Preset");
        } else {
            holder.txtChipset.setText(GCUtil.convertToCamelcase(mContext, chipSet.getTitle()));
        }
    }

    @Override
    public int getItemCount() {
        return mOnlineDBSavedSets.size();
    }

    public List<Integer> getSelectedSets() {
        return mMultiSelector.getSelectedPositions();
    }
}

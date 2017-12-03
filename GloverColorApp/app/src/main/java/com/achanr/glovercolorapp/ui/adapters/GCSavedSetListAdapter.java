package com.achanr.glovercolorapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.models.GCChip;
import com.achanr.glovercolorapp.models.GCMode;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.activities.GCEditCollectionActivity;
import com.achanr.glovercolorapp.ui.viewHolders.GCSavedSetListItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCSavedSetListAdapter extends RecyclerView.Adapter<GCSavedSetListItemViewHolder> {

    private ArrayList<GCSavedSet> mSavedSetList;
    private final Context mContext;

    public GCSavedSetListAdapter(Context context, ArrayList<GCSavedSet> savedSetList) {
        mSavedSetList = new ArrayList<>(savedSetList);
        mContext = context;
    }

    public void add(int position, GCSavedSet savedSet) {
        mSavedSetList.add(position, savedSet);
        sortList();
        int newPosition = mSavedSetList.indexOf(savedSet);
        if (newPosition >= 0) {
            notifyItemInserted(newPosition);
            //notifyItemRangeChanged(position, getItemCount());
        }
    }

    public void update(GCSavedSet oldSet, GCSavedSet newSet) {
        if (mContext instanceof GCEditCollectionActivity) {
            int counter = 0;
            for (GCSavedSet savedSet : mSavedSetList) {
                if (savedSet.getId() == oldSet.getId()) {
                    mSavedSetList.set(counter, newSet);
                    notifyItemChanged(counter);
                }
                counter++;
            }
        } else {
            int position = mSavedSetList.indexOf(oldSet);
            mSavedSetList.set(position, newSet);
            notifyItemChanged(position);
        }
    }

    public void remove(GCSavedSet savedSet) {
        int position = mSavedSetList.indexOf(savedSet);
        if (position >= 0) {
            mSavedSetList.remove(position);
            notifyItemRemoved(position);
            //notifyItemRangeChanged(position, getItemCount());
        }
    }

    @Override
    public GCSavedSetListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_saved_set, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new GCSavedSetListItemViewHolder(mContext, v);
    }

    @Override
    public void onBindViewHolder(GCSavedSetListItemViewHolder holder, final int position) {
        // - get element from your dataset at this mPosition
        // - replace the contents of the view with that element
        String title = mSavedSetList.get(position).getTitle();
        GCMode mode = mSavedSetList.get(position).getMode();
        GCChip chipSet = mSavedSetList.get(position).getChipSet();
        SpannableStringBuilder builder = GCUtil.generateMultiColoredString(mSavedSetList.get(position));
        holder.txtTitle.setText(title);
        holder.txtColors.setText(builder, TextView.BufferType.SPANNABLE);
        holder.txtMode.setText(GCUtil.convertToTitleCase(mContext, mode.getTitle()));
        if (chipSet.getTitle().equalsIgnoreCase("NONE")) {
            holder.txtChipset.setText("No Preset");
        } else {
            holder.txtChipset.setText(GCUtil.convertToTitleCase(mContext, chipSet.getTitle()));
        }
        holder.mSavedSet = mSavedSetList.get(position);
    }

    @Override
    public int getItemCount() {
        return mSavedSetList.size();
    }

    public void animateTo(List<GCSavedSet> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<GCSavedSet> newModels) {
        for (int i = mSavedSetList.size() - 1; i >= 0; i--) {
            final GCSavedSet model = mSavedSetList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<GCSavedSet> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final GCSavedSet model = newModels.get(i);
            if (!mSavedSetList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<GCSavedSet> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final GCSavedSet model = newModels.get(toPosition);
            final int fromPosition = mSavedSetList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void removeItem(int position) {
        mSavedSetList.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(int position, GCSavedSet model) {
        mSavedSetList.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final GCSavedSet model = mSavedSetList.remove(fromPosition);
        mSavedSetList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void sortList() {
        mSavedSetList = GCUtil.sortList(mContext, mSavedSetList);
    }
}

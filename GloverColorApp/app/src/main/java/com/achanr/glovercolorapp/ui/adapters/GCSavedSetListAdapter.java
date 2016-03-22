package com.achanr.glovercolorapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.models.GCChip;
import com.achanr.glovercolorapp.models.GCMode;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.ui.views.GCSavedSetListItemViewHolder;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCSavedSetListAdapter extends RecyclerView.Adapter<GCSavedSetListItemViewHolder> {

    private ArrayList<GCSavedSet> mSavedSetList;
    private Context mContext;

    public GCSavedSetListAdapter(Context context, ArrayList<GCSavedSet> savedSetList) {
        mSavedSetList = savedSetList;
        mContext = context;
    }

    public void add(int position, GCSavedSet savedSet) {
        mSavedSetList.add(position, savedSet);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void update(GCSavedSet oldSet, GCSavedSet newSet) {
        int position = mSavedSetList.indexOf(oldSet);
        mSavedSetList.set(position, newSet);
        notifyItemChanged(position);
    }

    public void remove(GCSavedSet savedSet) {
        int position = mSavedSetList.indexOf(savedSet);
        mSavedSetList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public GCSavedSetListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_saved_set, parent, false);
        // set the view's size, margins, paddings and layout parameters
        GCSavedSetListItemViewHolder vh = new GCSavedSetListItemViewHolder(mContext, v);
        return vh;
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
        holder.txtMode.setText(GCUtil.convertToCamelcase(mode.getTitle().toString()));
        if (chipSet.getTitle().equalsIgnoreCase("NONE")) {
            holder.txtChipset.setText("No preset");
        } else {
            holder.txtChipset.setText(GCUtil.convertToCamelcase(chipSet.getTitle().toString()));
        }
        holder.mSavedSet = mSavedSetList.get(position);
    }

    @Override
    public int getItemCount() {
        return mSavedSetList.size();
    }
}

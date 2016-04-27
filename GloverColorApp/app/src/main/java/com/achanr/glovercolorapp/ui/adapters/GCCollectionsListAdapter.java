package com.achanr.glovercolorapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.views.GCCollectionsListItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 4/27/16 4:03 PM
 */
public class GCCollectionsListAdapter extends RecyclerView.Adapter<GCCollectionsListItemViewHolder> {
    private ArrayList<GCSavedSet> mSavedSetList;
    private Context mContext;

    public GCCollectionsListAdapter(Context context, ArrayList<GCSavedSet> savedSetList) {
        mSavedSetList = new ArrayList<>(savedSetList);
        mContext = context;
    }

    @Override
    public GCCollectionsListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_collections, parent, false);
        // set the view's size, margins, paddings and layout parameters
        GCCollectionsListItemViewHolder vh = new GCCollectionsListItemViewHolder(mContext, v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GCCollectionsListItemViewHolder holder, final int position) {
        // - get element from your dataset at this mPosition
        // - replace the contents of the view with that element

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

    public GCSavedSet removeItem(int position) {
        final GCSavedSet model = mSavedSetList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, GCSavedSet model) {
        mSavedSetList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final GCSavedSet model = mSavedSetList.remove(fromPosition);
        mSavedSetList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}

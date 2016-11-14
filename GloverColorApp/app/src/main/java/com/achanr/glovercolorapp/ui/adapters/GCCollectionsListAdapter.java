package com.achanr.glovercolorapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.models.GCCollection;
import com.achanr.glovercolorapp.ui.viewHolders.GCCollectionsListItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 4/27/16 4:03 PM
 */
public class GCCollectionsListAdapter extends RecyclerView.Adapter<GCCollectionsListItemViewHolder> {
    private ArrayList<GCCollection> mCollectionList;
    private final Context mContext;

    public GCCollectionsListAdapter(Context context, ArrayList<GCCollection> collectionList) {
        mCollectionList = new ArrayList<>(collectionList);
        mContext = context;
    }

    public void sortList() {
        mCollectionList = GCUtil.sortCollectionList(mContext, mCollectionList);
    }

    public void add(int position, GCCollection savedCollection) {
        mCollectionList.add(position, savedCollection);
        sortList();
        int newPosition = mCollectionList.indexOf(savedCollection);
        notifyItemInserted(newPosition);
        //notifyItemRangeChanged(position, getItemCount());
    }

    public void update(GCCollection oldCollection, GCCollection newCollection) {
        int position = mCollectionList.indexOf(oldCollection);
        mCollectionList.set(position, newCollection);
        notifyItemChanged(position);
    }

    public void remove(GCCollection savedCollection) {
        int position = mCollectionList.indexOf(savedCollection);
        mCollectionList.remove(position);
        notifyItemRemoved(position);
        //notifyItemRangeChanged(position, getItemCount());
    }

    @Override
    public GCCollectionsListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_collections, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new GCCollectionsListItemViewHolder(mContext, v);
    }

    @Override
    public void onBindViewHolder(GCCollectionsListItemViewHolder holder, final int position) {
        // - get element from your dataset at this mPosition
        // - replace the contents of the view with that element
        String title = mCollectionList.get(position).getTitle();
        String description = mCollectionList.get(position).getDescription();
        holder.txtTitle.setText(title);
        holder.txtDesc.setText(description);
        holder.txtSetNum.setText(Integer.toString(mCollectionList.get(position).getSavedSetList().size()));
        holder.mCollection = mCollectionList.get(position);
    }

    @Override
    public int getItemCount() {
        return mCollectionList.size();
    }

    public void animateTo(List<GCCollection> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<GCCollection> newModels) {
        for (int i = mCollectionList.size() - 1; i >= 0; i--) {
            final GCCollection model = mCollectionList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<GCCollection> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final GCCollection model = newModels.get(i);
            if (!mCollectionList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<GCCollection> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final GCCollection model = newModels.get(toPosition);
            final int fromPosition = mCollectionList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void removeItem(int position) {
        mCollectionList.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(int position, GCCollection model) {
        mCollectionList.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final GCCollection model = mCollectionList.remove(fromPosition);
        mCollectionList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}

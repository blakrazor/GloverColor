package com.achanr.glovercolorapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.models.GCSavedSetDataModel;
import com.achanr.glovercolorapp.utility.EGCModeEnum;
import com.achanr.glovercolorapp.utility.GCUtil;
import com.achanr.glovercolorapp.views.GCSavedSetListItemViewHolder;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCSavedSetListAdapter extends RecyclerView.Adapter<GCSavedSetListItemViewHolder> {

    private ArrayList<GCSavedSetDataModel> mSavedSetList;
    private Context mContext;

    public GCSavedSetListAdapter(Context context, ArrayList<GCSavedSetDataModel> savedSetList) {
        mSavedSetList = savedSetList;
        mContext = context;
    }

    public void add(int position, GCSavedSetDataModel savedSet) {
        mSavedSetList.add(position, savedSet);
        notifyItemInserted(position);

    }

    public void remove(GCSavedSetDataModel savedSet) {
        int position = mSavedSetList.indexOf(savedSet);
        mSavedSetList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public GCSavedSetListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_saved_set, parent, false);
        // set the view's size, margins, paddings and layout parameters
        GCSavedSetListItemViewHolder vh = new GCSavedSetListItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GCSavedSetListItemViewHolder holder, int position) {
        // - get element from your dataset at this mPosition
        // - replace the contents of the view with that element
        String title = mSavedSetList.get(position).getTitle();
        String shortenedColorString = GCUtil.convertColorListToShortenedColorString(mContext, mSavedSetList.get(position).getColors());
        EGCModeEnum mode = mSavedSetList.get(position).getMode();
        holder.txtTitle.setText(title);
        holder.txtColors.setText(shortenedColorString);
        holder.txtMode.setText(mode.toString());
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return mSavedSetList.size();
    }

}

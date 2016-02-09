package com.achanr.glovercolorapp.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.activities.GCSavedSetListActivity;
import com.achanr.glovercolorapp.database.GCSavedSetDatabase;
import com.achanr.glovercolorapp.models.GCColor;
import com.achanr.glovercolorapp.models.GCSavedSet;
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
        GCSavedSetListItemViewHolder vh = new GCSavedSetListItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GCSavedSetListItemViewHolder holder, final int position) {
        // - get element from your dataset at this mPosition
        // - replace the contents of the view with that element
        String title = mSavedSetList.get(position).getTitle();
        String shortenedColorString = GCUtil.convertColorListToShortenedColorString(mSavedSetList.get(position).getColors());
        EGCModeEnum mode = mSavedSetList.get(position).getMode();
        SpannableStringBuilder builder = GCUtil.generateMultiColoredString(shortenedColorString);
        holder.txtTitle.setText(title);
        holder.txtColors.setText(builder, TextView.BufferType.SPANNABLE);
        holder.txtMode.setText(mode.toString());
        holder.position = position;

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShareDialog(position);
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GCSavedSetListActivity) mContext).onSavedSetListItemClicked(position);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSavedSetList.size();
    }

    private String getShareString(int position) {
        String shareString = "";
        String breakCharacter = GCUtil.BREAK_CHARACTER_FOR_SHARING;

        //Get title
        shareString += mSavedSetList.get(position).getTitle();
        shareString += breakCharacter;

        //Get colors
        ArrayList<GCColor> newColorList = mSavedSetList.get(position).getColors();
        shareString += GCUtil.convertColorListToShortenedColorString(newColorList);
        shareString += breakCharacter;

        //Get mode
        shareString += mSavedSetList.get(position).getMode();

        return shareString;
    }

    public void showShareDialog(int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(mContext.getString(R.string.share));
        alert.setMessage(mContext.getString(R.string.share_dialog));
        alert.setIcon(R.drawable.ic_share_black_48dp);

        TextView input = new TextView(mContext);
        input.setTextIsSelectable(true);
        final String shareString = getShareString(position);
        input.setText(shareString);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        alert.setView(input);

        alert.setPositiveButton(mContext.getString(R.string.copy), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", shareString);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(mContext.getString(R.string.share), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareString);
                sendIntent.setType("text/plain");
                mContext.startActivity(Intent.createChooser(sendIntent, "Send to"));
            }
        });
        alert.setNeutralButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void showDeleteDialog(final int position) {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.delete))
                .setMessage(mContext.getString(R.string.delete_dialog))
                .setPositiveButton(mContext.getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        GCSavedSetDatabase savedSetDatabase = new GCSavedSetDatabase(mContext);
                        savedSetDatabase.deleteData(mSavedSetList.get(position));
                        mSavedSetList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
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

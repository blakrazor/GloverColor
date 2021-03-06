package com.achanr.glovercolorapp.ui.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/11/16 2:28 PM
 */
class GCSavedSetListItemClickListener implements RecyclerView.OnItemTouchListener {
    private final OnSavedSetItemClickListener mListener;

    public interface OnSavedSetItemClickListener {
        void onItemClick(View view, int position);
    }

    private final GestureDetector mGestureDetector;

    public GCSavedSetListItemClickListener(Context context, OnSavedSetItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildLayoutPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}

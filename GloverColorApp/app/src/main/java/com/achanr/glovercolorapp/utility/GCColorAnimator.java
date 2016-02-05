package com.achanr.glovercolorapp.utility;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;

/**
 * Copyright (c) 2015 Miami HEAT. All rights reserved
 *
 * @author SapientNitro
 */
public class GCColorAnimator {

    private View mView;
    private ArrayList<EGCColorEnum> mColorList;
    private EGCModeEnum mMode;
    private Handler mHandler;

    public GCColorAnimator(View view, ArrayList<EGCColorEnum> colorList, EGCModeEnum mode) {
        mView = view;
        mColorList = colorList;
        mMode = mode;
        mHandler = new Handler();
    }

    private class ColorSwitcherTask extends AsyncTask<Void, Void, ArrayList<ColorPreviewItem>> {

        @Override
        protected ArrayList<ColorPreviewItem> doInBackground(Void... params) {
            ArrayList<ColorPreviewItem> colorPreviewItems = new ArrayList<>();



            return colorPreviewItems;
        }

        @Override
        protected void onPostExecute(ArrayList<ColorPreviewItem> colorPreviewItems) {
            super.onPostExecute(colorPreviewItems);
        }

        @Override
        protected void onCancelled(ArrayList<ColorPreviewItem> colorPreviewItems) {
            super.onCancelled(colorPreviewItems);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private class ColorPreviewItem {
        public int[] rgbValues;
        public int lightLength;

        public ColorPreviewItem(int lightLength, int[] rgbValues) {
            this.lightLength = lightLength;
            this.rgbValues = rgbValues;
        }
    }

}

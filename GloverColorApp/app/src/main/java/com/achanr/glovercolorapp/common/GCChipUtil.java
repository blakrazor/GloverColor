package com.achanr.glovercolorapp.common;

import android.content.Context;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.application.GloverColorApplication;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCChip;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 3:43 PM
 */
public class GCChipUtil {

    private static ArrayList<GCChip> mChipArrayList;
    private static Context mContext = GloverColorApplication.getContext();

    public static void initChipArrayList() {
//        mChipArrayList = GCDatabaseHelper.CHIP_DATABASE.getAllData();
//        if (mChipArrayList == null || mChipArrayList.isEmpty()) {
            createDefaultChips();
//        }
    }

    private static void createDefaultChips() {
        mChipArrayList = new ArrayList<>();

        String[] allChipsStringArray = mContext.getResources().getStringArray(R.array.all_chips);
        for (String chipItem : allChipsStringArray) {
            String[] colorStringArray = new String[]{};
            String[] modeStringArray = new String[]{};
            if (chipItem.equalsIgnoreCase(mContext.getString(R.string.NO_CHIP))) {
                //Default colors and modes (all colors and modes)
                colorStringArray = mContext.getResources().getStringArray(R.array.default_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.default_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.CHROMA_24))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.chroma24_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.chroma24_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.ENOVA))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.enova_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.enova_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.EZLITE_2))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.ezlite_2_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.ezlite_2_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.FLOW))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.flow_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.flow_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.CHROMA_CTRL))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.chroma_ctrl_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.chroma_ctrl_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.ELEMENT_V2))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.element_v2_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.element_v2_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.MATRIX))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.matrix_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.matrix_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.AURORA))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.aurora_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.aurora_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.FLUX))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.aurora_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.aurora_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.PULSAR))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.aurora_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.pulsar_simplex_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.SIMPLEX))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.aurora_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.pulsar_simplex_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.ARCLITE_INTENSITY))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.arclite_intensity_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.arclite_intensity_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.ARCLITE_SIMPLICITY))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.arclite_intensity_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.arclite_simplicity_modes);
            } else if (chipItem.equalsIgnoreCase(mContext.getString(R.string.ARCLITE_GALAXY))) {
                colorStringArray = mContext.getResources().getStringArray(R.array.arclite_intensity_colors);
                modeStringArray = mContext.getResources().getStringArray(R.array.arclite_galaxy_modes);
            }
            GCChip chip = new GCChip(chipItem,
                    new ArrayList(Arrays.asList(colorStringArray)),
                    new ArrayList(Arrays.asList(modeStringArray)));
            mChipArrayList.add(chip);
        }

        //Clear database and save default values
        GCDatabaseHelper.CHIP_DATABASE.clearTable();
        for (GCChip chip : mChipArrayList) {
            GCDatabaseHelper.CHIP_DATABASE.insertData(chip);
        }
    }

    public static GCChip getChipUsingTitle(String title) {
        GCChip chip = null;
        for (GCChip chipItem : mChipArrayList) {
            if (title.equalsIgnoreCase(chipItem.getTitle())) {
                chip = chipItem;
                break;
            }
        }
        return chip;
    }

    public static ArrayList<String> getAllChipTitles() {
        ArrayList<String> allChipTitles = new ArrayList<>();
        for (GCChip chip : mChipArrayList) {
            allChipTitles.add(chip.getTitle());
        }
        return allChipTitles;
    }
}

package com.achanr.glovercolorapp.common;

import android.content.Context;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCChip;
import com.achanr.glovercolorapp.models.GCOnlineDefaultChip;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 3:43 PM
 */
public class GCChipUtil {

    private static ArrayList<GCChip> mChipArrayList;

    public static void initChipArrayList(Context context) {
        mChipArrayList = GCDatabaseHelper.getInstance(context).CHIP_DATABASE.getAllData();
        if (mChipArrayList == null || mChipArrayList.isEmpty()
                || !mChipArrayList.get(0).getTitle().equalsIgnoreCase(context.getString(R.string.NO_CHIP))) {
            createDefaultChips(context);
        }
    }

    private static void createDefaultChips(Context context) {
        mChipArrayList = new ArrayList<>();

        String[] allChipsStringArray = context.getResources().getStringArray(R.array.all_chips);
        for (String chipItem : allChipsStringArray) {
            String[] colorStringArray = new String[]{};
            String[] modeStringArray = new String[]{};
            if (chipItem.equalsIgnoreCase(context.getString(R.string.NO_CHIP))) {
                //Default colors and modes (all colors and modes)
                colorStringArray = context.getResources().getStringArray(R.array.default_colors);
                modeStringArray = context.getResources().getStringArray(R.array.default_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.CHROMA_24))) {
                colorStringArray = context.getResources().getStringArray(R.array.chroma24_colors);
                modeStringArray = context.getResources().getStringArray(R.array.chroma24_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.ENOVA))) {
                colorStringArray = context.getResources().getStringArray(R.array.enova_colors);
                modeStringArray = context.getResources().getStringArray(R.array.enova_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.EZLITE_2))) {
                colorStringArray = context.getResources().getStringArray(R.array.ezlite_2_colors);
                modeStringArray = context.getResources().getStringArray(R.array.ezlite_2_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.FLOW))) {
                colorStringArray = context.getResources().getStringArray(R.array.flow_colors);
                modeStringArray = context.getResources().getStringArray(R.array.flow_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.CHROMA_CTRL))) {
                colorStringArray = context.getResources().getStringArray(R.array.chroma_ctrl_colors);
                modeStringArray = context.getResources().getStringArray(R.array.chroma_ctrl_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.ELEMENT_V2))) {
                colorStringArray = context.getResources().getStringArray(R.array.element_v2_colors);
                modeStringArray = context.getResources().getStringArray(R.array.element_v2_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.MATRIX))) {
                colorStringArray = context.getResources().getStringArray(R.array.matrix_colors);
                modeStringArray = context.getResources().getStringArray(R.array.matrix_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.AURORA))) {
                colorStringArray = context.getResources().getStringArray(R.array.aurora_colors);
                modeStringArray = context.getResources().getStringArray(R.array.aurora_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.FLUX))) {
                colorStringArray = context.getResources().getStringArray(R.array.aurora_colors);
                modeStringArray = context.getResources().getStringArray(R.array.aurora_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.PULSAR))) {
                colorStringArray = context.getResources().getStringArray(R.array.aurora_colors);
                modeStringArray = context.getResources().getStringArray(R.array.pulsar_simplex_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.SIMPLEX))) {
                colorStringArray = context.getResources().getStringArray(R.array.aurora_colors);
                modeStringArray = context.getResources().getStringArray(R.array.pulsar_simplex_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.ARCLITE_INTENSITY))) {
                colorStringArray = context.getResources().getStringArray(R.array.arclite_intensity_colors);
                modeStringArray = context.getResources().getStringArray(R.array.arclite_intensity_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.ARCLITE_SIMPLICITY))) {
                colorStringArray = context.getResources().getStringArray(R.array.arclite_intensity_colors);
                modeStringArray = context.getResources().getStringArray(R.array.arclite_simplicity_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.ARCLITE_GALAXY))) {
                colorStringArray = context.getResources().getStringArray(R.array.arclite_intensity_colors);
                modeStringArray = context.getResources().getStringArray(R.array.arclite_galaxy_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.ORACLE))) {
                colorStringArray = context.getResources().getStringArray(R.array.oracle_colors);
                modeStringArray = context.getResources().getStringArray(R.array.oracle_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.SP2_REV1))) {
                colorStringArray = context.getResources().getStringArray(R.array.sp2_rev1_colors);
                modeStringArray = context.getResources().getStringArray(R.array.sp2_rev1_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.SP2_REV2))) {
                colorStringArray = context.getResources().getStringArray(R.array.sp2_rev2_colors);
                modeStringArray = context.getResources().getStringArray(R.array.sp2_rev2_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.SP3_REV1))) {
                colorStringArray = context.getResources().getStringArray(R.array.sp3_rev1_colors);
                modeStringArray = context.getResources().getStringArray(R.array.sp3_rev1_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.SP3_REV2))) {
                colorStringArray = context.getResources().getStringArray(R.array.sp3_rev2_colors);
                modeStringArray = context.getResources().getStringArray(R.array.sp3_rev2_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.OG_CHROMA))) {
                colorStringArray = context.getResources().getStringArray(R.array.oracle_colors);
                modeStringArray = context.getResources().getStringArray(R.array.chroma24_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.KINETIC))) {
                colorStringArray = context.getResources().getStringArray(R.array.kinetic_colors);
                modeStringArray = context.getResources().getStringArray(R.array.kinetic_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.AETHER))) {
                colorStringArray = context.getResources().getStringArray(R.array.aether_colors);
                modeStringArray = context.getResources().getStringArray(R.array.aether_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.ELEMENT_V1))) {
                colorStringArray = context.getResources().getStringArray(R.array.element_v1_colors);
                modeStringArray = context.getResources().getStringArray(R.array.element_v1_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.ELEMENT_V2_LOYALTY))) {
                colorStringArray = context.getResources().getStringArray(R.array.element_v2_loyalty_colors);
                modeStringArray = context.getResources().getStringArray(R.array.element_v2_loyalty_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.CHROMA_36))) {
                colorStringArray = context.getResources().getStringArray(R.array.chroma36_colors);
                modeStringArray = context.getResources().getStringArray(R.array.chroma36_modes);
            } else if (chipItem.equalsIgnoreCase(context.getString(R.string.FLOW_V2))) {
                colorStringArray = context.getResources().getStringArray(R.array.flow_v2_colors);
                modeStringArray = context.getResources().getStringArray(R.array.flow_v2_modes);
            }
            GCChip chip = new GCChip(chipItem,
                    new ArrayList<>(Arrays.asList(colorStringArray)),
                    new ArrayList<>(Arrays.asList(modeStringArray)));
            mChipArrayList.add(chip);
        }

        fillDatabase(context, mChipArrayList);
    }

    public static void syncOnlineDefaultChipDatabase(Context context, ArrayList<GCOnlineDefaultChip> onlineChips) {
        mChipArrayList = new ArrayList<>();
        for (GCOnlineDefaultChip onlineChip : onlineChips) {
            GCChip chip = GCChip.convertFromOnlineChip(onlineChip);
            if (chip != null) {
                mChipArrayList.add(chip);
            }
        }

        fillDatabase(context, mChipArrayList);
    }

    private static void fillDatabase(Context context, ArrayList<GCChip> modes) {
        //Clear database and save default values
        GCDatabaseHelper.getInstance(context).CHIP_DATABASE.clearTable();
        for (GCChip chip : modes) {
            GCDatabaseHelper.getInstance(context).CHIP_DATABASE.insertData(chip);
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

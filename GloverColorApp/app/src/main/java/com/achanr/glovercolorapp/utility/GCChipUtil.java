package com.achanr.glovercolorapp.utility;

import com.achanr.glovercolorapp.R;
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

    public static void initChipArrayList() {
        mChipArrayList = GCDatabaseHelper.CHIP_DATABASE.getAllData();
        if (mChipArrayList == null || mChipArrayList.isEmpty()) {
            createDefaultChips();
        }
    }

    private static void createDefaultChips() {
        mChipArrayList = new ArrayList<>();

        String[] defaultColorStringArray = GloverColorApplication.getContext().getResources().getStringArray(R.array.default_colors);
        String[] defaultModeStringArray = GloverColorApplication.getContext().getResources().getStringArray(R.array.default_modes);
        GCChip noneChip = new GCChip("NONE",
                new ArrayList(Arrays.asList(defaultColorStringArray)),
                new ArrayList(Arrays.asList(defaultModeStringArray)));
        mChipArrayList.add(noneChip);

        //String[] defaultColorStringArray = GloverColorApplication.getContext().getResources().getStringArray(R.array.default_colors);
        //String[] defaultModeStringArray = GloverColorApplication.getContext().getResources().getStringArray(R.array.default_modes);
        GCChip chroma24Chip = new GCChip("CHROMA 24",
                new ArrayList(Arrays.asList(defaultColorStringArray)),
                new ArrayList(Arrays.asList(defaultModeStringArray)));
        mChipArrayList.add(chroma24Chip);

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

package com.achanr.glovercolorapp.common;

import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCColor;

import java.util.ArrayList;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 2:30 PM
 */
public class GCColorUtil {

    private static ArrayList<GCColor> mColorArrayList;

    public static void initColorArrayList() {
        mColorArrayList = GCDatabaseHelper.COLOR_DATABASE.getAllData();
        if (mColorArrayList == null || mColorArrayList.isEmpty()) {
            createDefaultColors();
        }
    }

    private static void createDefaultColors() {
        mColorArrayList = new ArrayList<>();

        mColorArrayList.add(new GCColor("NONE", "", new int[]{0, 0, 0}));
        mColorArrayList.add(new GCColor("BLANK", "--", new int[]{0, 0, 0}));
        mColorArrayList.add(new GCColor("WHITE", "Wh", new int[]{212, 238, 255}));
        mColorArrayList.add(new GCColor("RED", "Re", new int[]{255, 0, 12}));
        mColorArrayList.add(new GCColor("ORANGE", "Or", new int[]{255, 85, 0}));
        mColorArrayList.add(new GCColor("BANANA YELLOW", "BY", new int[]{239, 255, 0}));
        mColorArrayList.add(new GCColor("YELLOW", "Ye", new int[]{204, 255, 0}));
        mColorArrayList.add(new GCColor("LIME GREEN", "Li", new int[]{71, 255, 1}));
        mColorArrayList.add(new GCColor("GREEN", "Gr", new int[]{15, 147, 1}));
        mColorArrayList.add(new GCColor("MINT", "Mi", new int[]{136, 244, 220}));
        mColorArrayList.add(new GCColor("TURQUOISE", "Tu", new int[]{0, 198, 255}));
        mColorArrayList.add(new GCColor("LIGHT BLUE", "LB", new int[]{0, 148, 218}));
        mColorArrayList.add(new GCColor("SKY BLUE", "SB", new int[]{1, 143, 253}));
        mColorArrayList.add(new GCColor("BLUE", "Bl", new int[]{1, 66, 254}));
        mColorArrayList.add(new GCColor("PURPLE", "Pu", new int[]{66, 1, 255}));
        mColorArrayList.add(new GCColor("LAVENDAR", "La", new int[]{172, 120, 255}));
        mColorArrayList.add(new GCColor("BLUSH", "Bu", new int[]{201, 95, 199}));
        mColorArrayList.add(new GCColor("LIGHT PINK", "LP", new int[]{255, 95, 199}));
        mColorArrayList.add(new GCColor("HOT PINK", "HP", new int[]{245, 21, 154}));
        mColorArrayList.add(new GCColor("PEACH", "Pe", new int[]{255, 217, 116}));
        mColorArrayList.add(new GCColor("WARM WHITE", "WW", new int[]{243, 228, 195}));
        mColorArrayList.add(new GCColor("SILVER", "Si", new int[]{159, 207, 227}));
        mColorArrayList.add(new GCColor("LUNA", "Lu", new int[]{48, 113, 207}));

        //Clear database and save default values
        GCDatabaseHelper.COLOR_DATABASE.clearTable();
        for (GCColor color : mColorArrayList) {
            GCDatabaseHelper.COLOR_DATABASE.insertData(color);
        }
    }

    public static GCColor getColorUsingTitle(String title) {
        GCColor color = null;
        for (GCColor colorItem : mColorArrayList) {
            if (title.equalsIgnoreCase(colorItem.getTitle())) {
                color = colorItem;
                break;
            }
        }
        return color;
    }

    public static GCColor getColorUsingAbbrev(String abbrev) {
        GCColor color = null;
        for (GCColor colorItem : mColorArrayList) {
            if (abbrev.equalsIgnoreCase(colorItem.getAbbreviation())) {
                color = colorItem;
                break;
            }
        }
        return color;
    }
}


package com.achanr.glovercolorapp.common;

import android.content.Context;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.application.GloverColorApplication;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCColor;

import java.util.ArrayList;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 2:30 PM
 */
public class GCColorUtil {

    private static ArrayList<GCColor> mColorArrayList;
    private static Context mContext = GloverColorApplication.getContext();

    public static void initColorArrayList() {
        mColorArrayList = GCDatabaseHelper.COLOR_DATABASE.getAllData();
        if (mColorArrayList == null || mColorArrayList.isEmpty()) {
            createDefaultColors();
        }
    }

    private static void createDefaultColors() {
        mColorArrayList = new ArrayList<>();

        String[] allColorStringArray = mContext.getResources().getStringArray(R.array.default_colors);
        for (String colorItem : allColorStringArray) {
            String colorAbbrev = "";
            int[] rgbValues = new int[]{};
            if (colorItem.equalsIgnoreCase(mContext.getString(R.string.NONE))) {
                colorAbbrev = "";
                rgbValues = new int[]{0, 0, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BLANK))) {
                colorAbbrev = "--";
                rgbValues = new int[]{0, 0, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.WHITE))) {
                colorAbbrev = "Wh";
                rgbValues = new int[]{212, 238, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.RED))) {
                colorAbbrev = "Re";
                rgbValues = new int[]{255, 0, 12};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.ORANGE))) {
                colorAbbrev = "Or";
                rgbValues = new int[]{255, 85, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BANANA_YELLOW))) {
                colorAbbrev = "BY";
                rgbValues = new int[]{239, 255, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.YELLOW))) {
                colorAbbrev = "Ye";
                rgbValues = new int[]{204, 255, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIME_GREEN))) {
                colorAbbrev = "LG";
                rgbValues = new int[]{71, 255, 1};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.GREEN))) {
                colorAbbrev = "Gr";
                rgbValues = new int[]{15, 147, 1};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.MINT))) {
                colorAbbrev = "Mi";
                rgbValues = new int[]{136, 244, 220};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.TURQUOISE))) {
                colorAbbrev = "Tu";
                rgbValues = new int[]{0, 198, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIGHT_BLUE))) {
                colorAbbrev = "LB";
                rgbValues = new int[]{0, 148, 218};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SKY_BLUE))) {
                colorAbbrev = "SB";
                rgbValues = new int[]{1, 143, 253};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BLUE))) {
                colorAbbrev = "Bl";
                rgbValues = new int[]{1, 66, 254};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.PURPLE))) {
                colorAbbrev = "Pu";
                rgbValues = new int[]{66, 1, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LAVENDAR))) {
                colorAbbrev = "La";
                rgbValues = new int[]{172, 120, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BLUSH))) {
                colorAbbrev = "Bu";
                rgbValues = new int[]{201, 95, 199};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIGHT_PINK))) {
                colorAbbrev = "LP";
                rgbValues = new int[]{255, 95, 199};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.PINK))) {
                colorAbbrev = "Pi";
                rgbValues = new int[]{217, 27, 198};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.HOT_PINK))) {
                colorAbbrev = "HP";
                rgbValues = new int[]{245, 21, 154};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.PEACH))) {
                colorAbbrev = "Pe";
                rgbValues = new int[]{255, 217, 116};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.OFF_WHITE))) {
                colorAbbrev = "OW";
                rgbValues = new int[]{255, 239, 240};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.WARM_WHITE))) {
                colorAbbrev = "WW";
                rgbValues = new int[]{243, 228, 195};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SILVER))) {
                colorAbbrev = "Si";
                rgbValues = new int[]{159, 207, 227};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LUNA))) {
                colorAbbrev = "Lu";
                rgbValues = new int[]{48, 114, 207};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.GOLDEN_HOUR))) {
                colorAbbrev = "GH";
                rgbValues = new int[]{239, 165, 32};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.CHAOS_EMERALD))) {
                colorAbbrev = "CE";
                rgbValues = new int[]{97, 198, 184};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SEAFOAM))) {
                colorAbbrev = "Se";
                rgbValues = new int[]{97, 189, 114};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BUBBLEGUM))) {
                colorAbbrev = "Bb";
                rgbValues = new int[]{238, 77, 157};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.MEW))) {
                colorAbbrev = "Me";
                rgbValues = new int[]{246, 141, 109};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.FROST))) {
                colorAbbrev = "Fr";
                rgbValues = new int[]{68, 174, 226};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SPACE_GHOST))) {
                colorAbbrev = "SG";
                rgbValues = new int[]{169, 208, 91};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.COSMIC_OWL))) {
                colorAbbrev = "CO";
                rgbValues = new int[]{180, 212, 108};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIME))) {
                colorAbbrev = "Li";
                rgbValues = new int[]{162, 204, 63};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.CYAN))) {
                colorAbbrev = "Cy";
                rgbValues = new int[]{70, 156, 213};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LENS_FLARE))) {
                colorAbbrev = "LF";
                rgbValues = new int[]{146, 122, 184};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SNARF))) {
                colorAbbrev = "Sn";
                rgbValues = new int[]{246, 157, 139};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.TOMBSTONE))) {
                colorAbbrev = "To";
                rgbValues = new int[]{109, 196, 161};
            }
            mColorArrayList.add(new GCColor(colorItem, colorAbbrev, rgbValues));
        }

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


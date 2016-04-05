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
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.CUSTOM))) {
                colorAbbrev = "??";
                rgbValues = new int[]{255, 255, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BLANK))) {
                colorAbbrev = "--";
                rgbValues = new int[]{0, 0, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.WHITE))) {
                colorAbbrev = mContext.getString(R.string.Wh);
                rgbValues = new int[]{212, 238, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.RED))) {
                colorAbbrev = mContext.getString(R.string.Re);
                rgbValues = new int[]{255, 0, 12};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.ORANGE))) {
                colorAbbrev = mContext.getString(R.string.Or);
                rgbValues = new int[]{255, 85, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BANANA_YELLOW))) {
                colorAbbrev = mContext.getString(R.string.BY);
                rgbValues = new int[]{239, 255, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.YELLOW))) {
                colorAbbrev = mContext.getString(R.string.Ye);
                rgbValues = new int[]{204, 255, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIME_GREEN))) {
                colorAbbrev = mContext.getString(R.string.LG);
                rgbValues = new int[]{71, 255, 1};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.GREEN))) {
                colorAbbrev = mContext.getString(R.string.Gr);
                rgbValues = new int[]{15, 147, 1};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.MINT))) {
                colorAbbrev = mContext.getString(R.string.Mi);
                rgbValues = new int[]{136, 244, 220};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.TURQUOISE))) {
                colorAbbrev = mContext.getString(R.string.Tu);
                rgbValues = new int[]{0, 198, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIGHT_BLUE))) {
                colorAbbrev = mContext.getString(R.string.LB);
                rgbValues = new int[]{0, 148, 218};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SKY_BLUE))) {
                colorAbbrev = mContext.getString(R.string.SB);
                rgbValues = new int[]{1, 143, 253};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BLUE))) {
                colorAbbrev = mContext.getString(R.string.Bl);
                rgbValues = new int[]{1, 66, 254};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.PURPLE))) {
                colorAbbrev = mContext.getString(R.string.Pu);
                rgbValues = new int[]{66, 1, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LAVENDAR))) {
                colorAbbrev = mContext.getString(R.string.La);
                rgbValues = new int[]{172, 120, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BLUSH))) {
                colorAbbrev = mContext.getString(R.string.Bu);
                rgbValues = new int[]{201, 95, 199};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIGHT_PINK))) {
                colorAbbrev = mContext.getString(R.string.LP);
                rgbValues = new int[]{255, 95, 199};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.PINK))) {
                colorAbbrev = mContext.getString(R.string.Pi);
                rgbValues = new int[]{217, 27, 198};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.HOT_PINK))) {
                colorAbbrev = mContext.getString(R.string.HP);
                rgbValues = new int[]{245, 21, 154};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.PEACH))) {
                colorAbbrev = mContext.getString(R.string.Pe);
                rgbValues = new int[]{255, 217, 116};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.OFF_WHITE))) {
                colorAbbrev = mContext.getString(R.string.OW);
                rgbValues = new int[]{255, 239, 240};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.WARM_WHITE))) {
                colorAbbrev = mContext.getString(R.string.WW);
                rgbValues = new int[]{243, 228, 195};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SILVER))) {
                colorAbbrev = mContext.getString(R.string.Si);
                rgbValues = new int[]{159, 207, 227};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LUNA))) {
                colorAbbrev = mContext.getString(R.string.Lu);
                rgbValues = new int[]{48, 114, 207};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.GOLDEN_HOUR))) {
                colorAbbrev = mContext.getString(R.string.GH);
                rgbValues = new int[]{239, 165, 32};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.CHAOS_EMERALD))) {
                colorAbbrev = mContext.getString(R.string.CE);
                rgbValues = new int[]{97, 198, 184};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SEAFOAM))) {
                colorAbbrev = mContext.getString(R.string.Se);
                rgbValues = new int[]{97, 189, 114};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BUBBLEGUM))) {
                colorAbbrev = mContext.getString(R.string.Bb);
                rgbValues = new int[]{238, 77, 157};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.MEW))) {
                colorAbbrev = mContext.getString(R.string.Me);
                rgbValues = new int[]{246, 141, 109};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.FROST))) {
                colorAbbrev = mContext.getString(R.string.Fr);
                rgbValues = new int[]{68, 174, 226};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SPACE_GHOST))) {
                colorAbbrev = mContext.getString(R.string.SG);
                rgbValues = new int[]{169, 208, 91};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.COSMIC_OWL))) {
                colorAbbrev = mContext.getString(R.string.CO);
                rgbValues = new int[]{180, 212, 108};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIME))) {
                colorAbbrev = mContext.getString(R.string.Li);
                rgbValues = new int[]{162, 204, 63};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.CYAN))) {
                colorAbbrev = mContext.getString(R.string.Cy);
                rgbValues = new int[]{70, 156, 213};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LENS_FLARE))) {
                colorAbbrev = mContext.getString(R.string.LF);
                rgbValues = new int[]{146, 122, 184};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SNARF))) {
                colorAbbrev = mContext.getString(R.string.Sn);
                rgbValues = new int[]{246, 157, 139};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.TOMBSTONE))) {
                colorAbbrev = mContext.getString(R.string.To);
                rgbValues = new int[]{109, 196, 161};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.BLOOD_ORANGE))) {
                colorAbbrev = mContext.getString(R.string.BO);
                rgbValues = new int[]{193, 58, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.GOLD))) {
                colorAbbrev = mContext.getString(R.string.Go);
                rgbValues = new int[]{188, 171, 57};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.COBALT))) {
                colorAbbrev = mContext.getString(R.string.Co);
                rgbValues = new int[]{61, 91, 135};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.PLUM))) {
                colorAbbrev = mContext.getString(R.string.Pl);
                rgbValues = new int[]{93, 14, 105};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SALMON))) {
                colorAbbrev = mContext.getString(R.string.Sa);
                rgbValues = new int[]{209, 78, 48};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SUN))) {
                colorAbbrev = mContext.getString(R.string.Su);
                rgbValues = new int[]{230, 202, 119};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.MOON))) {
                colorAbbrev = mContext.getString(R.string.Mo);
                rgbValues = new int[]{186, 204, 206};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.TANGERINE))) {
                colorAbbrev = mContext.getString(R.string.Ta);
                rgbValues = new int[]{255, 144, 0};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.AQUA_GREEN))) {
                colorAbbrev = mContext.getString(R.string.AG);
                rgbValues = new int[]{57, 253, 57};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.OCEAN))) {
                colorAbbrev = mContext.getString(R.string.Oc);
                rgbValues = new int[]{54, 253, 152};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.WINTER))) {
                colorAbbrev = mContext.getString(R.string.Wi);
                rgbValues = new int[]{87, 241, 251};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.NEBULA))) {
                colorAbbrev = mContext.getString(R.string.Ne);
                rgbValues = new int[]{0, 168, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.DEEP_BLUE))) {
                colorAbbrev = mContext.getString(R.string.DB);
                rgbValues = new int[]{1, 114, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.VIOLET))) {
                colorAbbrev = mContext.getString(R.string.Vi);
                rgbValues = new int[]{150, 0, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.ROSE))) {
                colorAbbrev = mContext.getString(R.string.Ro);
                rgbValues = new int[]{234, 77, 252};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.MAGENTA))) {
                colorAbbrev = mContext.getString(R.string.Ma);
                rgbValues = new int[]{255, 0, 156};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SUNSET))) {
                colorAbbrev = mContext.getString(R.string.Ss);
                rgbValues = new int[]{252, 171, 54};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.SUNKIST))) {
                colorAbbrev = mContext.getString(R.string.Sk);
                rgbValues = new int[]{250, 190, 104};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.WHITE_PURPLE))) {
                colorAbbrev = mContext.getString(R.string.WP);
                rgbValues = new int[]{227, 165, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.DEEP_ORCHID))) {
                colorAbbrev = mContext.getString(R.string.DO);
                rgbValues = new int[]{253, 99, 156};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.WHITE_SILVER))) {
                colorAbbrev = mContext.getString(R.string.WS);
                rgbValues = new int[]{255, 255, 191};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.GOLDEN_YELLOW))) {
                colorAbbrev = mContext.getString(R.string.GY);
                rgbValues = new int[]{255, 199, 11};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.PINK_RASPBERRY))) {
                colorAbbrev = mContext.getString(R.string.PR);
                rgbValues = new int[]{255, 48, 170};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIGHT_LAVENDAR))) {
                colorAbbrev = mContext.getString(R.string.LL);
                rgbValues = new int[]{203, 171, 255};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIGHT_PURPLE))) {
                colorAbbrev = mContext.getString(R.string.LU);
                rgbValues = new int[]{193, 125, 254};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.FUCHSIA))) {
                colorAbbrev = mContext.getString(R.string.Fu);
                rgbValues = new int[]{253, 0, 121};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.WHITE_LAVENDAR))) {
                colorAbbrev = mContext.getString(R.string.WL);
                rgbValues = new int[]{184, 156, 254};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.ORCHID))) {
                colorAbbrev = mContext.getString(R.string.Od);
                rgbValues = new int[]{254, 122, 253};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.FLAMINGO))) {
                colorAbbrev = mContext.getString(R.string.Fl);
                rgbValues = new int[]{233, 87, 136};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.MANGO))) {
                colorAbbrev = mContext.getString(R.string.Ma);
                rgbValues = new int[]{254, 76, 29};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.LIME_YELLOW))) {
                colorAbbrev = mContext.getString(R.string.LY);
                rgbValues = new int[]{225, 254, 5};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.GREEN_ICE))) {
                colorAbbrev = mContext.getString(R.string.GI);
                rgbValues = new int[]{212, 253, 106};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.FLUORESCENT_RED))) {
                colorAbbrev = mContext.getString(R.string.FR);
                rgbValues = new int[]{239, 48, 64};
            } else if (colorItem.equalsIgnoreCase(mContext.getString(R.string.CREAM_SICKLE))) {
                colorAbbrev = mContext.getString(R.string.CS);
                rgbValues = new int[]{247, 106, 53};
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


package com.achanr.glovercolorapp.common;

import android.content.Context;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCColor;
import com.achanr.glovercolorapp.models.GCOnlineColor;

import java.util.ArrayList;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 2:30 PM
 */
public class GCColorUtil {

    private static ArrayList<GCColor> mColorArrayList;

    public static void initColorArrayList(Context context) {
        mColorArrayList = GCDatabaseHelper.getInstance(context).COLOR_DATABASE.getAllData();
        if (mColorArrayList == null || mColorArrayList.isEmpty()) {
            createDefaultColors(context);
        }
    }

    private static void createDefaultColors(Context context) {
        mColorArrayList = new ArrayList<>();

        String[] allColorStringArray = context.getResources().getStringArray(R.array.default_colors);
        for (String colorItem : allColorStringArray) {
            String colorAbbrev = "";
            int[] rgbValues = new int[]{};
            if (colorItem.equalsIgnoreCase(context.getString(R.string.NONE))) {
                colorAbbrev = "";
                rgbValues = new int[]{0, 0, 0};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.CUSTOM))) {
                colorAbbrev = "??";
                rgbValues = new int[]{255, 255, 255};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.BLANK))) {
                colorAbbrev = "--";
                rgbValues = new int[]{0, 0, 0};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.WHITE))) {
                colorAbbrev = context.getString(R.string.Wh);
                rgbValues = new int[]{212, 238, 255};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.RED))) {
                colorAbbrev = context.getString(R.string.Re);
                rgbValues = new int[]{255, 0, 12};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.ORANGE))) {
                colorAbbrev = context.getString(R.string.Or);
                rgbValues = new int[]{255, 85, 0};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.BANANA_YELLOW))) {
                colorAbbrev = context.getString(R.string.BY);
                rgbValues = new int[]{239, 255, 0};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.YELLOW))) {
                colorAbbrev = context.getString(R.string.Ye);
                rgbValues = new int[]{204, 255, 0};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LIME_GREEN))) {
                colorAbbrev = context.getString(R.string.LG);
                rgbValues = new int[]{71, 255, 1};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.GREEN))) {
                colorAbbrev = context.getString(R.string.Gr);
                rgbValues = new int[]{15, 147, 1};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.MINT))) {
                colorAbbrev = context.getString(R.string.Mi);
                rgbValues = new int[]{136, 244, 220};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.TURQUOISE))) {
                colorAbbrev = context.getString(R.string.Tu);
                rgbValues = new int[]{0, 198, 255};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LIGHT_BLUE))) {
                colorAbbrev = context.getString(R.string.LB);
                rgbValues = new int[]{0, 148, 218};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SKY_BLUE))) {
                colorAbbrev = context.getString(R.string.SB);
                rgbValues = new int[]{1, 143, 253};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.BLUE))) {
                colorAbbrev = context.getString(R.string.Bl);
                rgbValues = new int[]{1, 66, 254};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.PURPLE))) {
                colorAbbrev = context.getString(R.string.Pu);
                rgbValues = new int[]{66, 1, 255};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LAVENDAR))) {
                colorAbbrev = context.getString(R.string.La);
                rgbValues = new int[]{172, 120, 255};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.BLUSH))) {
                colorAbbrev = context.getString(R.string.Bu);
                rgbValues = new int[]{201, 95, 199};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LIGHT_PINK))) {
                colorAbbrev = context.getString(R.string.LP);
                rgbValues = new int[]{255, 95, 199};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.PINK))) {
                colorAbbrev = context.getString(R.string.Pi);
                rgbValues = new int[]{217, 27, 198};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.HOT_PINK))) {
                colorAbbrev = context.getString(R.string.HP);
                rgbValues = new int[]{245, 21, 154};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.PEACH))) {
                colorAbbrev = context.getString(R.string.Pe);
                rgbValues = new int[]{255, 217, 116};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.OFF_WHITE))) {
                colorAbbrev = context.getString(R.string.OW);
                rgbValues = new int[]{255, 239, 240};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.WARM_WHITE))) {
                colorAbbrev = context.getString(R.string.WW);
                rgbValues = new int[]{243, 228, 195};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SILVER))) {
                colorAbbrev = context.getString(R.string.Si);
                rgbValues = new int[]{159, 207, 227};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LUNA))) {
                colorAbbrev = context.getString(R.string.Lu);
                rgbValues = new int[]{48, 114, 207};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.GOLDEN_HOUR))) {
                colorAbbrev = context.getString(R.string.GH);
                rgbValues = new int[]{239, 165, 32};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.CHAOS_EMERALD))) {
                colorAbbrev = context.getString(R.string.CE);
                rgbValues = new int[]{97, 198, 184};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SEAFOAM))) {
                colorAbbrev = context.getString(R.string.Se);
                rgbValues = new int[]{97, 189, 114};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.BUBBLEGUM))) {
                colorAbbrev = context.getString(R.string.Bb);
                rgbValues = new int[]{238, 77, 157};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.MEW))) {
                colorAbbrev = context.getString(R.string.Me);
                rgbValues = new int[]{246, 141, 109};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.FROST))) {
                colorAbbrev = context.getString(R.string.Fr);
                rgbValues = new int[]{68, 174, 226};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SPACE_GHOST))) {
                colorAbbrev = context.getString(R.string.SG);
                rgbValues = new int[]{169, 208, 91};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.COSMIC_OWL))) {
                colorAbbrev = context.getString(R.string.CO);
                rgbValues = new int[]{180, 212, 108};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LIME))) {
                colorAbbrev = context.getString(R.string.Li);
                rgbValues = new int[]{162, 204, 63};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.CYAN))) {
                colorAbbrev = context.getString(R.string.Cy);
                rgbValues = new int[]{70, 156, 213};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LENS_FLARE))) {
                colorAbbrev = context.getString(R.string.LF);
                rgbValues = new int[]{146, 122, 184};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SNARF))) {
                colorAbbrev = context.getString(R.string.Sn);
                rgbValues = new int[]{246, 157, 139};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.TOMBSTONE))) {
                colorAbbrev = context.getString(R.string.To);
                rgbValues = new int[]{109, 196, 161};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.BLOOD_ORANGE))) {
                colorAbbrev = context.getString(R.string.BO);
                rgbValues = new int[]{193, 58, 0};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.GOLD))) {
                colorAbbrev = context.getString(R.string.Go);
                rgbValues = new int[]{188, 171, 57};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.COBALT))) {
                colorAbbrev = context.getString(R.string.Co);
                rgbValues = new int[]{61, 91, 135};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.PLUM))) {
                colorAbbrev = context.getString(R.string.Pl);
                rgbValues = new int[]{93, 14, 105};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SALMON))) {
                colorAbbrev = context.getString(R.string.Sa);
                rgbValues = new int[]{209, 78, 48};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SUN))) {
                colorAbbrev = context.getString(R.string.Su);
                rgbValues = new int[]{230, 202, 119};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.MOON))) {
                colorAbbrev = context.getString(R.string.Mo);
                rgbValues = new int[]{186, 204, 206};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.TANGERINE))) {
                colorAbbrev = context.getString(R.string.Ta);
                rgbValues = new int[]{255, 144, 0};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.AQUA_GREEN))) {
                colorAbbrev = context.getString(R.string.AG);
                rgbValues = new int[]{57, 253, 57};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.OCEAN))) {
                colorAbbrev = context.getString(R.string.Oc);
                rgbValues = new int[]{54, 253, 152};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.WINTER))) {
                colorAbbrev = context.getString(R.string.Wi);
                rgbValues = new int[]{87, 241, 251};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.NEBULA))) {
                colorAbbrev = context.getString(R.string.Ne);
                rgbValues = new int[]{0, 168, 255};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.DEEP_BLUE))) {
                colorAbbrev = context.getString(R.string.DB);
                rgbValues = new int[]{1, 114, 255};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.VIOLET))) {
                colorAbbrev = context.getString(R.string.Vi);
                rgbValues = new int[]{150, 0, 255};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.ROSE))) {
                colorAbbrev = context.getString(R.string.Ro);
                rgbValues = new int[]{234, 77, 252};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.MAGENTA))) {
                colorAbbrev = context.getString(R.string.Ma);
                rgbValues = new int[]{255, 0, 156};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SUNSET))) {
                colorAbbrev = context.getString(R.string.Ss);
                rgbValues = new int[]{252, 171, 54};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SUNKIST))) {
                colorAbbrev = context.getString(R.string.Sk);
                rgbValues = new int[]{250, 190, 104};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.WHITE_PURPLE))) {
                colorAbbrev = context.getString(R.string.WP);
                rgbValues = new int[]{227, 165, 255};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.DEEP_ORCHID))) {
                colorAbbrev = context.getString(R.string.DO);
                rgbValues = new int[]{253, 99, 156};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.WHITE_SILVER))) {
                colorAbbrev = context.getString(R.string.WS);
                rgbValues = new int[]{255, 255, 191};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.GOLDEN_YELLOW))) {
                colorAbbrev = context.getString(R.string.GY);
                rgbValues = new int[]{255, 199, 11};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.PINK_RASPBERRY))) {
                colorAbbrev = context.getString(R.string.PR);
                rgbValues = new int[]{255, 48, 170};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LIGHT_LAVENDAR))) {
                colorAbbrev = context.getString(R.string.LL);
                rgbValues = new int[]{203, 171, 255};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LIGHT_PURPLE))) {
                colorAbbrev = context.getString(R.string.LU);
                rgbValues = new int[]{193, 125, 254};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.FUCHSIA))) {
                colorAbbrev = context.getString(R.string.Fu);
                rgbValues = new int[]{253, 0, 121};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.WHITE_LAVENDAR))) {
                colorAbbrev = context.getString(R.string.WL);
                rgbValues = new int[]{184, 156, 254};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.ORCHID))) {
                colorAbbrev = context.getString(R.string.Od);
                rgbValues = new int[]{254, 122, 253};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.FLAMINGO))) {
                colorAbbrev = context.getString(R.string.Fl);
                rgbValues = new int[]{233, 87, 136};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.MANGO))) {
                colorAbbrev = context.getString(R.string.Mg);
                rgbValues = new int[]{254, 76, 29};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LIME_YELLOW))) {
                colorAbbrev = context.getString(R.string.LY);
                rgbValues = new int[]{225, 254, 5};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.GREEN_ICE))) {
                colorAbbrev = context.getString(R.string.GI);
                rgbValues = new int[]{212, 253, 106};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.FLUORESCENT_RED))) {
                colorAbbrev = context.getString(R.string.FR);
                rgbValues = new int[]{239, 48, 64};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.CREAM_SICKLE))) {
                colorAbbrev = context.getString(R.string.CS);
                rgbValues = new int[]{247, 106, 53};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LEMON))) {
                colorAbbrev = context.getString(R.string.Le);
                rgbValues = new int[]{255, 253, 56};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.AURORA_COLOR))) {
                colorAbbrev = context.getString(R.string.Au);
                rgbValues = new int[]{39, 36, 250};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SOLAR_FLARE))) {
                colorAbbrev = context.getString(R.string.SF);
                rgbValues = new int[]{251, 40, 28};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.ACID))) {
                colorAbbrev = context.getString(R.string.Ac);
                rgbValues = new int[]{115, 253, 48};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.COMET))) {
                colorAbbrev = context.getString(R.string.Cm);
                rgbValues = new int[]{139, 220, 252};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.KIWI))) {
                colorAbbrev = context.getString(R.string.Ki);
                rgbValues = new int[]{184, 253, 51};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.NYMPH))) {
                colorAbbrev = context.getString(R.string.Ny);
                rgbValues = new int[]{56, 253, 47};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.AQUA))) {
                colorAbbrev = context.getString(R.string.Aq);
                rgbValues = new int[]{42, 253, 116};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SKY))) {
                colorAbbrev = context.getString(R.string.Sy);
                rgbValues = new int[]{45, 255, 254};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.VIRIDIAN))) {
                colorAbbrev = context.getString(R.string.Vr);
                rgbValues = new int[]{19, 114, 250};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LAKE))) {
                colorAbbrev = context.getString(R.string.Lk);
                rgbValues = new int[]{12, 52, 251};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.DUSK))) {
                colorAbbrev = context.getString(R.string.Du);
                rgbValues = new int[]{110, 37, 251};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.CANDY))) {
                colorAbbrev = context.getString(R.string.Ca);
                rgbValues = new int[]{252, 40, 251};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LOVE))) {
                colorAbbrev = context.getString(R.string.Lo);
                rgbValues = new int[]{252, 14, 47};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.LEAF))) {
                colorAbbrev = context.getString(R.string.Lf);
                rgbValues = new int[]{221, 253, 112};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.DREAM))) {
                colorAbbrev = context.getString(R.string.Dr);
                rgbValues = new int[]{140, 253, 174};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.NEPTUNE))) {
                colorAbbrev = context.getString(R.string.Np);
                rgbValues = new int[]{141, 254, 221};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.STARDUST))) {
                colorAbbrev = context.getString(R.string.SD);
                rgbValues = new int[]{138, 173, 252};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.BLOSSOM))) {
                colorAbbrev = context.getString(R.string.Bs);
                rgbValues = new int[]{253, 139, 219};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.CLOUD))) {
                colorAbbrev = context.getString(R.string.Cl);
                rgbValues = new int[]{253, 136, 170};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.SMURF))) {
                colorAbbrev = context.getString(R.string.Sm);
                rgbValues = new int[]{103, 202, 241};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.RUST))) {
                colorAbbrev = context.getString(R.string.Ru);
                rgbValues = new int[]{241, 183, 170};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.CORAL))) {
                colorAbbrev = context.getString(R.string.Cr);
                rgbValues = new int[]{232, 27, 68};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.TEAL))) {
                colorAbbrev = context.getString(R.string.Te);
                rgbValues = new int[]{106, 196, 160};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.PHANTOM))) {
                colorAbbrev = context.getString(R.string.Ph);
                rgbValues = new int[]{139, 93, 166};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.AMETHYST))) {
                colorAbbrev = context.getString(R.string.Am);
                rgbValues = new int[]{154, 86, 161};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.COTTON_CANDY))) {
                colorAbbrev = context.getString(R.string.CC);
                rgbValues = new int[]{221, 144, 190};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.KREAM))) {
                colorAbbrev = context.getString(R.string.Kr);
                rgbValues = new int[]{212, 235, 249};
            } else if (colorItem.equalsIgnoreCase(context.getString(R.string.BLUE_STEEL))) {
                colorAbbrev = context.getString(R.string.BS);
                rgbValues = new int[]{78, 197, 205};
            }
            mColorArrayList.add(new GCColor(colorItem, colorAbbrev, rgbValues));
        }

        fillDatabase(context, mColorArrayList);
    }

    public static void syncOnlineColorDatabase(Context context, ArrayList<GCOnlineColor> onlineColors) {
        mColorArrayList = new ArrayList<>();
        for (GCOnlineColor onlineColor : onlineColors) {
            GCColor color = GCColor.convertFromOnlineColor(onlineColor);
            if (color != null) {
                mColorArrayList.add(color);
            }
        }
        fillDatabase(context, mColorArrayList);
    }

    private static void fillDatabase(Context context, ArrayList<GCColor> modes) {
        //Clear database and save default values
        GCDatabaseHelper.getInstance(context).COLOR_DATABASE.clearTable();
        for (GCColor color : modes) {
            GCDatabaseHelper.getInstance(context).COLOR_DATABASE.insertData(color);
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

    static GCColor getColorUsingAbbrev(String abbrev) {
        GCColor color = null;
        for (GCColor colorItem : mColorArrayList) {
            if (abbrev.equals(colorItem.getAbbreviation())) {
                color = colorItem;
                break;
            }
        }
        return color;
    }
}


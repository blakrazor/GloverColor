package com.achanr.glovercolorapp.common;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.application.GloverColorApplication;
import com.achanr.glovercolorapp.models.GCColor;
import com.achanr.glovercolorapp.models.GCPowerLevel;
import com.achanr.glovercolorapp.models.GCPoweredColor;
import com.achanr.glovercolorapp.models.GCSavedSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCUtil {

    public static final String BREAK_CHARACTER_FOR_SHARING = ":";
    public static final String THEME_KEY = "theme_key";
    public static final String WAS_REFRESHED = "was_refreshed";

    private static Context context = GloverColorApplication.getContext();

    public static String convertColorListToShortenedColorString(ArrayList<GCPoweredColor> colorList) {
        String shortenedColorString = "";
        for (GCPoweredColor color : colorList) {
            if (!color.getColor().getTitle().equalsIgnoreCase(GCConstants.COLOR_NONE)) {
                shortenedColorString += (color.getColor().getAbbreviation() + color.getPowerLevel().getAbbreviation());
            }
        }
        return shortenedColorString;
    }

    public static ArrayList<GCPoweredColor> convertShortenedColorStringToColorList(String shortenedColorString) {
        ArrayList<GCPoweredColor> colorList = new ArrayList<>();

        List<String> stringParts = getParts(shortenedColorString, 3);

        for (String colorAbbrev : stringParts) {
            String colorString = colorAbbrev.substring(0, 2);
            String powerString = colorAbbrev.substring(2, 3);
            GCColor color = GCColorUtil.getColorUsingAbbrev(colorString);
            GCPowerLevel powerLevel = GCPowerLevelUtil.getPowerLevelUsingAbbrev(powerString);
            colorList.add(new GCPoweredColor(color, powerLevel.getTitle()));
        }

        return colorList;
    }

    private static List<String> getParts(String string, int partitionSize) {
        List<String> parts = new ArrayList<>();
        int len = string.length();
        for (int i = 0; i < len; i += partitionSize) {
            parts.add(string.substring(i, Math.min(len, i + partitionSize)));
        }
        return parts;
    }

    /*public static String randomTitle(ArrayList<GCSavedSet> savedSetList) {
        String newTitle = randomString();
        for (GCSavedSet savedSet : savedSetList) {
            if (savedSet.getTitle().equals(newTitle)) {
                return randomTitle(savedSetList);
            }
        }
        return newTitle;
    }

    public static String randomString() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(5) + 5;
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = "qwertyuiopasdfghjklzxcvbnm".charAt(generator.nextInt("qwertyuiopasdfghjklzxcvbnm".length()));
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public static EGCColorEnum randomColor() {
        List<EGCColorEnum> colorValues = Collections.unmodifiableList(Arrays.asList(EGCColorEnum.values()));
        return colorValues.get((new Random()).nextInt(colorValues.size()));
    }

    public static EGCModeEnum randomMode() {
        List<EGCModeEnum> modeValues = Collections.unmodifiableList(Arrays.asList(EGCModeEnum.values()));
        return modeValues.get((new Random()).nextInt(modeValues.size()));
    }*/

    public static SpannableStringBuilder generateMultiColoredString(GCSavedSet savedSet) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        String shortenedColorString = convertColorListToShortenedColorString(savedSet.getColors());

        List<String> stringParts = getParts(shortenedColorString, 3);
        int index = 0;
        for (String colorAbbrev : stringParts) {
            String colorString = colorAbbrev.substring(0, 2);
            String powerString = colorAbbrev.substring(2, 3);
            GCColor color = GCColorUtil.getColorUsingAbbrev(colorString);
            GCPowerLevel powerLevel = GCPowerLevelUtil.getPowerLevelUsingAbbrev(powerString);
            int[] rgbValues;
            if (color.getTitle().equalsIgnoreCase(GCConstants.COLOR_CUSTOM)) {
                rgbValues = convertRgbToPowerLevel(savedSet.getCustomColors().get(index), powerLevel);
            } else {
                rgbValues = convertRgbToPowerLevel(color.getRGBValues(), powerLevel);
            }

            if (colorString.equalsIgnoreCase("--")) {
                SpannableString spannableString = new SpannableString(colorString);
                spannableString.setSpan(new ForegroundColorSpan(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2])), 0, colorString.length(), 0);
                builder.append(spannableString);
            } else {
                SpannableString spannableString = new SpannableString(colorAbbrev);
                spannableString.setSpan(new ForegroundColorSpan(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2])), 0, 3, 0);
                spannableString.setSpan(new SuperscriptSpan(), colorAbbrev.length() - 1, colorAbbrev.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new RelativeSizeSpan(0.75f), colorAbbrev.length() - 1, colorAbbrev.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.append(spannableString);
            }
            index++;
        }

        return builder;
    }

    public static void refreshActivity(Activity activity) {
        activity.finish();
        Intent intent = new Intent(activity, activity.getClass());
        intent.putExtra(WAS_REFRESHED, true);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, EGCThemeEnum themeEnum) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(THEME_KEY, themeEnum.toString());
        editor.apply();
        refreshActivity(activity);
    }

    public static String getCurrentTheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(THEME_KEY, EGCThemeEnum.DEFAULT_THEME.toString());
    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        String themeString = getCurrentTheme();

        switch (EGCThemeEnum.valueOf(themeString)) {
            default:
            case DEFAULT_THEME:
                activity.setTheme(R.style.DefaultTheme);
                break;
            case BLUE_THEME:
                activity.setTheme(R.style.BlueTheme);
                break;
            case PURPLE_THEME:
                activity.setTheme(R.style.PurpleTheme);
                break;
            case GREEN_THEME:
                activity.setTheme(R.style.GreenTheme);
                break;
        }
    }

    public static int[] convertRgbToPowerLevel(int[] originalRgb, GCPowerLevel powerLevel) {
        int[] newRgbValues = new int[3];

        float[] hsv = new float[3];
        Color.RGBToHSV(originalRgb[0], originalRgb[1], originalRgb[2], hsv);

        hsv[1] = hsv[1] * powerLevel.getValue();

        int outputColor = Color.HSVToColor(hsv);
        newRgbValues[0] = Color.red(outputColor);
        newRgbValues[1] = Color.green(outputColor);
        newRgbValues[2] = Color.blue(outputColor);

        return newRgbValues;
    }

    public static String getShareString(GCSavedSet savedSet) {
        String shareString = "";

        //Get title
        String title = savedSet.getTitle();
        title = title.replace(" ", "_");
        shareString += title;
        shareString += BREAK_CHARACTER_FOR_SHARING;

        //Get chipset
        shareString += savedSet.getChipSet().getTitle();
        shareString += BREAK_CHARACTER_FOR_SHARING;

        //Get colors
        ArrayList<GCPoweredColor> newColorList = savedSet.getColors();
        shareString += convertColorListToShortenedColorString(newColorList);
        shareString += BREAK_CHARACTER_FOR_SHARING;

        //Get mode
        shareString += savedSet.getMode().getTitle();
        shareString += BREAK_CHARACTER_FOR_SHARING;

        //Get custom colors
        shareString += getCustomColorShareString(savedSet);

        return shareString;
    }

    public static void showShareDialog(final Context mContext, GCSavedSet savedSet) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(mContext.getString(R.string.share_code));
        alert.setIcon(R.drawable.ic_share_black_48dp);

        TextView input = new TextView(mContext);
        input.setTextIsSelectable(true);
        final String shareString = getShareString(savedSet);
        input.setText(shareString);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        input.setPadding(64, 32, 64, 32);
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareString + mContext.getString(R.string.share_body_text));
                sendIntent.setType("text/plain");
                mContext.startActivity(Intent.createChooser(sendIntent, "Share gloving set with"));
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

    public static String convertToCamelcase(String inputString) {
        if (inputString.equalsIgnoreCase("DOPs")) {
            return "DOPs";
        }

        if (inputString != null && !inputString.isEmpty()) {
            if (inputString.length() < 2) {
                return inputString.toUpperCase();
            }

            String returnString = "";
            returnString += inputString.substring(0, 1).toUpperCase();
            returnString += inputString.substring(1).toLowerCase();
            return returnString;
        } else {
            return "";
        }
    }

    public static int fetchAttributeColor(Context mContext, int attributeId) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = mContext.obtainStyledAttributes(typedValue.data, new int[]{attributeId});
        int color = a.getColor(0, 0);

        a.recycle();

        return color;
    }

    public static String convertCustomColorArrayToString(ArrayList<int[]> customColorArray) {
        String customColorString = "";
        for (int[] rgbValues : customColorArray) {
            customColorString += rgbValues[0] + "-" + rgbValues[1] + "-" + rgbValues[2];
            customColorString += ",";
        }
        return customColorString;
    }

    public static ArrayList<int[]> convertStringToCustomColorArray(String customColorString) {
        ArrayList<int[]> customColorArray = new ArrayList<>();
        String[] customColorParts = customColorString.split(",");
        for (String customColor : customColorParts) {
            int[] rgbValues = new int[3];
            int firstIndex = customColor.indexOf("-");
            int secondIndex = customColor.indexOf("-", firstIndex + 1);
            rgbValues[0] = Integer.parseInt(customColor.substring(0, firstIndex));
            rgbValues[1] = Integer.parseInt(customColor.substring(firstIndex + 1, secondIndex));
            rgbValues[2] = Integer.parseInt(customColor.substring(secondIndex + 1));
            customColorArray.add(rgbValues);
        }
        return customColorArray;
    }

    public static String getCustomColorShareString(GCSavedSet savedSet) {
        ArrayList<Integer> indexList = new ArrayList<>();
        String customColorString = "";
        int index = 0;
        for (GCPoweredColor poweredColor : savedSet.getColors()) {
            if (poweredColor.getColor().getTitle().equalsIgnoreCase(GCConstants.COLOR_CUSTOM)) {
                indexList.add(index);
            }
            index++;
        }
        for (int i = 0; i < indexList.size(); i++) {
            int[] rgbValues = savedSet.getCustomColors().get(indexList.get(i));
            customColorString += "(" + Integer.toString(indexList.get(i)) + "," +
                    rgbValues[0] + "-" + rgbValues[1] + "-" + rgbValues[2] + ")";
        }
        return customColorString;
    }

    public static ArrayList<int[]> parseCustomColorShareString(String customColorString) {
        ArrayList<int[]> customColorArray = new ArrayList<>();
        for (int i = 0; i < GCConstants.MAX_COLORS; i++) {
            customColorArray.add(new int[]{255, 255, 255});
        }

        String[] customColorParts = customColorString.split("\\(");
        for (String customColor : customColorParts) {
            if (!customColor.isEmpty()) {
                int index = Integer.parseInt(customColor.substring(0, customColor.indexOf(",")));

                int[] rgbValues = new int[3];
                int firstIndex = customColor.indexOf("-");
                int secondIndex = customColor.indexOf("-", firstIndex + 1);
                rgbValues[0] = Integer.parseInt(customColor.substring(customColor.indexOf(",") + 1, firstIndex));
                rgbValues[1] = Integer.parseInt(customColor.substring(firstIndex + 1, secondIndex));
                rgbValues[2] = Integer.parseInt(customColor.substring(secondIndex + 1, customColor.indexOf(")")));

                int[] oldRgbValues = customColorArray.get(index);
                oldRgbValues[0] = rgbValues[0];
                oldRgbValues[1] = rgbValues[1];
                oldRgbValues[2] = rgbValues[2];
            }
        }
        return customColorArray;
    }
}

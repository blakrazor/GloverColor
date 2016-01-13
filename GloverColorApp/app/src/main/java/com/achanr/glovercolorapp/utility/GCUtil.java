package com.achanr.glovercolorapp.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.models.GCSavedSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCUtil {

    public static final String BREAK_CHARACTER_FOR_SHARING = ":";
    public static final String THEME_KEY = "theme_key";

    private static Context context = GloverColorApplication.getContext();

    private static final Map<String, EGCColorEnum> colorAbbrevToEnumHashMap = new HashMap<>();

    static {
        for (final EGCColorEnum s : EnumSet.allOf(EGCColorEnum.class)) {
            colorAbbrevToEnumHashMap.put(s.getColorAbbrev(), s);
        }
    }

    public static String convertColorListToShortenedColorString(ArrayList<EGCColorEnum> colorList) {
        String shortenedColorString = "";
        for (EGCColorEnum color : colorList) {
            shortenedColorString += color.getColorAbbrev();
        }
        return shortenedColorString;
    }

    public static ArrayList<EGCColorEnum> convertShortenedColorStringToColorList(String shortenedColorString) {
        ArrayList<EGCColorEnum> colorList = new ArrayList<>();

        List<String> stringParts = getParts(shortenedColorString, 2);

        for (String colorAbbrev : stringParts) {
            EGCColorEnum colorEnum = colorAbbrevToEnumHashMap.get(colorAbbrev);
            colorList.add(colorEnum);
        }

        return colorList;
    }

    private static List<String> getParts(String string, int partitionSize) {
        List<String> parts = new ArrayList<String>();
        int len = string.length();
        for (int i = 0; i < len; i += partitionSize) {
            parts.add(string.substring(i, Math.min(len, i + partitionSize)));
        }
        return parts;
    }

    public static String randomTitle(ArrayList<GCSavedSet> savedSetList) {
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
    }

    public static SpannableStringBuilder generateMultiColoredString(String shortenedColorString) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        List<String> stringParts = getParts(shortenedColorString, 2);
        for (String colorAbbrev : stringParts) {
            SpannableString spannableString = new SpannableString(colorAbbrev);
            int[] rgbValues = colorAbbrevToEnumHashMap.get(colorAbbrev).getRgbValues();
            spannableString.setSpan(new ForegroundColorSpan(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2])), 0, colorAbbrev.length(), 0);
            builder.append(spannableString);
        }

        return builder;
    }

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, EGCThemeEnum themeEnum) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(THEME_KEY, themeEnum.toString());
        editor.commit();
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String themeString = prefs.getString(THEME_KEY, EGCThemeEnum.BLUE_THEME.toString());

        switch (EGCThemeEnum.valueOf(themeString)) {
            default:
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
}

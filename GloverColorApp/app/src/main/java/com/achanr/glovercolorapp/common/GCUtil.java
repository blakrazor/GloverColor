package com.achanr.glovercolorapp.common;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCCollection;
import com.achanr.glovercolorapp.models.GCColor;
import com.achanr.glovercolorapp.models.GCPowerLevel;
import com.achanr.glovercolorapp.models.GCPoweredColor;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.activities.GCCollectionsActivity;
import com.achanr.glovercolorapp.ui.activities.GCSavedSetListActivity;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCUtil {

    public static final String BREAK_CHARACTER_FOR_SHARING = ":";
    private static final String THEME_KEY = "theme_key";
    public static final String WAS_REFRESHED = "was_refreshed";

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
            try {
                String colorString = colorAbbrev.substring(0, 2);
                String powerString = colorAbbrev.substring(2, 3);
                GCColor color = GCColorUtil.getColorUsingAbbrev(colorString);
                GCPowerLevel powerLevel = GCPowerLevelUtil.getPowerLevelUsingAbbrev(powerString);
                colorList.add(new GCPoweredColor(color, powerLevel.getTitle()));
            } catch (Exception e) {
                return null;
            }
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
                spannableString.setSpan(new ForegroundColorSpan(Color.argb(255, 255, 255, 255)), 0, colorString.length(), 0);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(THEME_KEY, themeEnum.toString());
        editor.apply();
        refreshActivity(activity);
    }

    public static String getCurrentTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(THEME_KEY, EGCThemeEnum.DEFAULT_THEME.toString());
    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        String themeString = getCurrentTheme(activity);

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
            case RED_THEME:
                activity.setTheme(R.style.RedTheme);
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

    private static String getShareString(GCSavedSet savedSet) {
        String shareString = "";

        //Get title
        String title = savedSet.getTitle();
        title = title.replace(" ", "_");
        shareString += title;
        shareString += BREAK_CHARACTER_FOR_SHARING;

        //Get chipset
        String chip = savedSet.getChipSet().getTitle();
        chip = chip.replace(" ", "_");
        shareString += chip;
        shareString += BREAK_CHARACTER_FOR_SHARING;

        //Get colors
        ArrayList<GCPoweredColor> newColorList = savedSet.getColors();
        shareString += convertColorListToShortenedColorString(newColorList);
        shareString += BREAK_CHARACTER_FOR_SHARING;

        //Get mode
        String mode = savedSet.getMode().getTitle();
        mode = mode.replace(" ", "_");
        shareString += mode;
        shareString += BREAK_CHARACTER_FOR_SHARING;

        //Get custom colors
        shareString += getCustomColorShareString(savedSet);

        return shareString;
    }

    public static void showShareDialog(final Context mContext, final GCSavedSet savedSet) {
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
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", shareString);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(mContext.getString(R.string.share), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createShareChooser(mContext, savedSet.getTitle(), mContext.getString(R.string.facebook_share_description), shareString);
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://deeplink.me/glovercolorapp.com/entercode/" + shareString);
//                sendIntent.setType("text/plain");
//                mContext.startActivity(Intent.createChooser(sendIntent, "Share gloving set with"));
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

    private static void createShareChooser(final Context context,
                                           final String title,
                                           final String description,
                                           final String shareString) {
        final Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "https://deeplink.me/glovercolorapp.com/entercode/" + shareString);

        final List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(i, 0);

        List<String> appNames = new ArrayList<>();
        for (ResolveInfo info : activities) {
            appNames.add(info.loadLabel(context.getPackageManager()).toString());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Share gloving set using...");
        builder.setItems(appNames.toArray(new CharSequence[appNames.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ResolveInfo info = activities.get(item);
                if (info.activityInfo.packageName.equals("com.facebook.katana")) {
                    // Facebook was chosen
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("https://deeplink.me/glovercolorapp.com/entercode/" + shareString))
                            .setImageUrl(Uri.parse("http://res.cloudinary.com/glovercolor/image/upload/w_599,h_314,c_fit/v1477433765/glovercolor_icon.png"))
                            .setContentTitle(shareString)
                            .setContentDescription(description)
                            .build();
                    ShareDialog shareDialog = new ShareDialog((Activity) context);
                    shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
                    return;
                }

                // start the selected activity
                i.setPackage(info.activityInfo.packageName);
                context.startActivity(i);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String convertToTitleCase(Context context, String inputString) {
        if (inputString != null && !inputString.isEmpty()) {

            if (inputString.toLowerCase().contains("dop")) {
                int dopIndex = inputString.toLowerCase().indexOf("dop");
                return convertToTitleCase(context, inputString.substring(0, dopIndex)) + "DOP" + convertToTitleCase(context, inputString.substring(dopIndex + 3).toLowerCase());
            }

            if (inputString.equalsIgnoreCase(context.getString(R.string.OG_CHROMA))) {
                return "OG Chroma";
            }

            if (inputString.equalsIgnoreCase(context.getString(R.string.EZLITE_2))) {
                return "ezLite 2.0";
            }

            if (inputString.toLowerCase().startsWith("sp")) {
                return "SP" + convertToTitleCase(context, inputString.substring(2).toLowerCase());
            }


            if (inputString.length() <= 1) {
                return inputString.toLowerCase();
            }

            String[] parts = inputString.split(" ");
            String camelCaseString = "";
            for (String part : parts) {
                camelCaseString = camelCaseString + capitalizeFirstLetter(part) + " ";
            }
            return camelCaseString.trim();
        } else {
            return "";
        }
    }

    private static String capitalizeFirstLetter(String s) {
        if (s.length() > 0) {
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
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

    private static String getCustomColorShareString(GCSavedSet savedSet) {
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
                try {
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
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return customColorArray;
    }

    public static void hideKeyboard(Activity activity, View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static <V extends View> Collection<V> findChildrenByClass(ViewGroup viewGroup, Class<V> clazz) {

        return gatherChildrenByClass(viewGroup, clazz, new ArrayList<V>());
    }

    private static <V extends View> Collection<V> gatherChildrenByClass(ViewGroup viewGroup, Class<V> clazz, Collection<V> childrenFound) {

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            final View child = viewGroup.getChildAt(i);
            if (clazz.isAssignableFrom(child.getClass())) {
                //noinspection unchecked
                childrenFound.add((V) child);
            }
            if (child instanceof ViewGroup) {
                gatherChildrenByClass((ViewGroup) child, clazz, childrenFound);
            }
        }

        return childrenFound;
    }

    public static ArrayList<GCSavedSet> sortList(Context context, ArrayList<GCSavedSet> savedSetArrayList) {
        ArrayList<GCSavedSet> sortedSetList = new ArrayList<>(savedSetArrayList);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int sortTypeInt = prefs.getInt(GCConstants.SORTING_KEY, 0);
        GCSavedSetListActivity.SortEnum sortType = GCSavedSetListActivity.SortEnum.values()[sortTypeInt];

        switch (sortType) {
            case TITLE_DESC:
                Collections.sort(sortedSetList, new Comparator<GCSavedSet>() {
                    @Override
                    public int compare(GCSavedSet lhs, GCSavedSet rhs) {
                        return rhs.getTitle().compareToIgnoreCase(lhs.getTitle());
                    }
                });
                break;
            case TITLE_ASC:
                Collections.sort(sortedSetList, new Comparator<GCSavedSet>() {
                    @Override
                    public int compare(GCSavedSet lhs, GCSavedSet rhs) {
                        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                    }
                });
                break;
            case CHIP_DESC:
                Collections.sort(sortedSetList, new Comparator<GCSavedSet>() {
                    @Override
                    public int compare(GCSavedSet lhs, GCSavedSet rhs) {
                        return rhs.getChipSet().getTitle().compareToIgnoreCase(lhs.getChipSet().getTitle());
                    }
                });
                break;
            case CHIP_ASC:
                Collections.sort(sortedSetList, new Comparator<GCSavedSet>() {
                    @Override
                    public int compare(GCSavedSet lhs, GCSavedSet rhs) {
                        return lhs.getChipSet().getTitle().compareToIgnoreCase(rhs.getChipSet().getTitle());
                    }
                });
                break;
        }
        return sortedSetList;
    }

    public static ArrayList<GCCollection> sortCollectionList(Context context, ArrayList<GCCollection> collectionList) {
        ArrayList<GCCollection> sortedCollectionList = new ArrayList<>(collectionList);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int sortTypeInt = prefs.getInt(GCConstants.COLLECTION_SORTING_KEY, 0);
        GCCollectionsActivity.SortEnum sortType = GCCollectionsActivity.SortEnum.values()[sortTypeInt];

        switch (sortType) {
            case TITLE_DESC:
                Collections.sort(sortedCollectionList, new Comparator<GCCollection>() {
                    @Override
                    public int compare(GCCollection lhs, GCCollection rhs) {
                        return rhs.getTitle().compareToIgnoreCase(lhs.getTitle());
                    }
                });
                break;
            case TITLE_ASC:
                Collections.sort(sortedCollectionList, new Comparator<GCCollection>() {
                    @Override
                    public int compare(GCCollection lhs, GCCollection rhs) {
                        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                    }
                });
                break;
        }
        return sortedCollectionList;
    }

    public static String convertSetListToString(ArrayList<GCSavedSet> setArrayList) {
        String setListString = "";
        for (GCSavedSet set : setArrayList) {
            setListString += set.getId();
            setListString += ",";
        }
        return setListString;
    }

    public static ArrayList<GCSavedSet> convertStringToSetList(Context context, String setListString) {
        ArrayList<GCSavedSet> setArrayList = new ArrayList<>();
        String[] stringParts = setListString.split(",");
        ArrayList<GCSavedSet> allSavedSets = GCDatabaseHelper.getInstance(context).SAVED_SET_DATABASE.getAllData();
        for (String part : stringParts) {
            for (GCSavedSet set : allSavedSets) {
                if (set.getId() == Integer.parseInt(part)) {
                    setArrayList.add(set);
                    break;
                }
            }
        }
        return setArrayList;
    }
}

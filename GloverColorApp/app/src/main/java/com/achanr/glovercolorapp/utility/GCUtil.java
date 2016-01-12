package com.achanr.glovercolorapp.utility;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.models.GCSavedSetDataModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCUtil {

    public static final String BREAK_CHARACTER_FOR_SHARING = ":";

    private static Context context = GloverColorApplication.getContext();

    public static String convertColorListToShortenedColorString(ArrayList<EGCColorEnum> colorList) {
        String shortenedColorString = "";
        for (EGCColorEnum color : colorList) {
            switch (color) {
                case RED:
                    shortenedColorString += context.getString(R.string.color_red_abbrev);
                    break;
                case BLUE:
                    shortenedColorString += context.getString(R.string.color_blue_abbrev);
                    break;
                case GREEN:
                    shortenedColorString += context.getString(R.string.color_green_abbrev);
                    break;
                case YELLOW:
                    shortenedColorString += context.getString(R.string.color_yellow_abbrev);
                    break;
                case ORANGE:
                    shortenedColorString += context.getString(R.string.color_orange_abbrev);
                    break;
                case PURPLE:
                    shortenedColorString += context.getString(R.string.color_purple_abbrev);
                    break;
                case WHITE:
                    shortenedColorString += context.getString(R.string.color_white_abbrev);
                    break;
                case BLANK:
                    shortenedColorString += context.getString(R.string.color_blank_abbrev);
                    break;
            }
        }
        return shortenedColorString;
    }

    public static EGCColorEnum convertColorAbbrevToColorEnum(String colorAbbrev){
        if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_red_abbrev))){
            return EGCColorEnum.RED;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_blue_abbrev))){
            return EGCColorEnum.BLUE;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_green_abbrev))){
            return EGCColorEnum.GREEN;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_yellow_abbrev))){
            return EGCColorEnum.YELLOW;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_orange_abbrev))){
            return EGCColorEnum.ORANGE;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_purple_abbrev))){
            return EGCColorEnum.PURPLE;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_white_abbrev))){
            return EGCColorEnum.WHITE;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_blank_abbrev))){
            return EGCColorEnum.BLANK;
        }
        return EGCColorEnum.BLANK;
    }

    private static int[] getRGBValuesForColorAbbrev(String colorAbbrev){
        int[] rgbValues = new int[3];

        EGCColorEnum colorEnum = convertColorAbbrevToColorEnum(colorAbbrev);
        switch(colorEnum){
            case RED:
                rgbValues[0] = 255;
                rgbValues[1] = 0;
                rgbValues[2] = 0;
                break;
            case BLUE:
                rgbValues[0] = 0;
                rgbValues[1] = 0;
                rgbValues[2] = 255;
                break;
            case GREEN:
                rgbValues[0] = 0;
                rgbValues[1] = 255;
                rgbValues[2] = 0;
                break;
            case YELLOW:
                rgbValues[0] = 255;
                rgbValues[1] = 255;
                rgbValues[2] = 0;
                break;
            case ORANGE:
                rgbValues[0] = 255;
                rgbValues[1] = 165;
                rgbValues[2] = 0;
                break;
            case PURPLE:
                rgbValues[0] = 128;
                rgbValues[1] = 0;
                rgbValues[2] = 128;
                break;
            case WHITE:
                rgbValues[0] = 128;
                rgbValues[1] = 128;
                rgbValues[2] = 128;
                break;
            case BLANK:
                rgbValues[0] = 0;
                rgbValues[1] = 0;
                rgbValues[2] = 0;
                break;
        }

        return rgbValues;
    }

    public static ArrayList<EGCColorEnum>  convertShortenedColorStringToColorList(String shortenedColorString){
        ArrayList<EGCColorEnum> colorList = new ArrayList<>();

        List<String> stringParts = getParts(shortenedColorString, 2);

        for(String colorAbbrev : stringParts){
            EGCColorEnum colorEnum = convertColorAbbrevToColorEnum(colorAbbrev);
            colorList.add(colorEnum);
        }

        return colorList;
    }

    private static List<String> getParts(String string, int partitionSize) {
        List<String> parts = new ArrayList<String>();
        int len = string.length();
        for (int i=0; i<len; i+=partitionSize)
        {
            parts.add(string.substring(i, Math.min(len, i + partitionSize)));
        }
        return parts;
    }

    public static String randomTitle(ArrayList<GCSavedSetDataModel> savedSetList){
        String newTitle = randomString();
        for(GCSavedSetDataModel savedSet: savedSetList){
            if(savedSet.getTitle().equals(newTitle)){
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
        for (int i = 0; i < randomLength; i++){
            tempChar = "qwertyuiopasdfghjklzxcvbnm".charAt(generator.nextInt("qwertyuiopasdfghjklzxcvbnm".length()));
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public static EGCColorEnum randomColor(){
        List<EGCColorEnum> colorValues = Collections.unmodifiableList(Arrays.asList(EGCColorEnum.values()));
        return colorValues.get((new Random()).nextInt(colorValues.size()));
    }

    public static EGCModeEnum randomMode(){
        List<EGCModeEnum> modeValues = Collections.unmodifiableList(Arrays.asList(EGCModeEnum.values()));
        return modeValues.get((new Random()).nextInt(modeValues.size()));
    }

    public static SpannableStringBuilder generateMultiColoredString(String shortenedColorString){
        SpannableStringBuilder builder = new SpannableStringBuilder();

        List<String> stringParts = getParts(shortenedColorString, 2);
        for(String colorAbbrev : stringParts){
            SpannableString spannableString = new SpannableString(colorAbbrev);
            int[] rgbValues = getRGBValuesForColorAbbrev(colorAbbrev);
            spannableString.setSpan(new ForegroundColorSpan(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2])), 0, colorAbbrev.length(), 0);
            builder.append(spannableString);
        }

        return builder;
    }
}

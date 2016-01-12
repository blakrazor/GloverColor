package com.achanr.glovercolorapp.utility;

import android.content.Context;

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
}

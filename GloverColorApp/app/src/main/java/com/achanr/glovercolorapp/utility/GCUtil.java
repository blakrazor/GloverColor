package com.achanr.glovercolorapp.utility;

import android.content.Context;

import com.achanr.glovercolorapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */
public class GCUtil {

    public static String convertColorListToShortenedColorString(Context context, ArrayList<GCColorEnum> colorList) {
        String shortenedColorString = "";
        for (GCColorEnum color : colorList) {
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

    public static ArrayList<GCColorEnum>  convertShortenedColorStringToColorList(Context context, String shortenedColorString){
        ArrayList<GCColorEnum> colorList = new ArrayList<>();

        List<String> stringParts = getParts(shortenedColorString, 2);

        for(String colorAbbrev : stringParts){
            GCColorEnum colorEnum = convertColorAbbrevToColorEnum(context, colorAbbrev);
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

    public static GCColorEnum convertColorAbbrevToColorEnum(Context context, String colorAbbrev){
        if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_red_abbrev))){
            return GCColorEnum.RED;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_blue_abbrev))){
            return GCColorEnum.BLUE;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_green_abbrev))){
            return GCColorEnum.GREEN;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_yellow_abbrev))){
            return GCColorEnum.YELLOW;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_orange_abbrev))){
            return GCColorEnum.ORANGE;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_purple_abbrev))){
            return GCColorEnum.PURPLE;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_white_abbrev))){
            return GCColorEnum.WHITE;
        } else if(colorAbbrev.equalsIgnoreCase(context.getString(R.string.color_blank_abbrev))){
            return GCColorEnum.BLANK;
        }
        return GCColorEnum.BLANK;
    }
}

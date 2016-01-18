package com.achanr.glovercolorapp.utility;

import com.achanr.glovercolorapp.R;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @date 1/8/16 12:32 PM
 */
public enum EGCColorEnum {
    NONE(GloverColorApplication.getContext().getString(R.string.color_none_abbrev), new int[]{0,0,0}),
    BLANK(GloverColorApplication.getContext().getString(R.string.color_blank_abbrev), new int[]{0,0,0}),
    RED(GloverColorApplication.getContext().getString(R.string.color_red_abbrev), new int[]{255,0,0}),
    BLUE(GloverColorApplication.getContext().getString(R.string.color_blue_abbrev), new int[]{0,0,255}),
    GREEN(GloverColorApplication.getContext().getString(R.string.color_green_abbrev), new int[]{0,255,0}),
    YELLOW(GloverColorApplication.getContext().getString(R.string.color_yellow_abbrev), new int[]{255,255,0}),
    ORANGE(GloverColorApplication.getContext().getString(R.string.color_orange_abbrev), new int[]{255,165,0}),
    PURPLE(GloverColorApplication.getContext().getString(R.string.color_purple_abbrev), new int[]{128,0,128}),
    WHITE(GloverColorApplication.getContext().getString(R.string.color_white_abbrev), new int[]{255,255,255});

    EGCColorEnum(String colorAbbrev, int[] rgbValues){
        this.rgbValues = rgbValues;
        this.colorAbbrev = colorAbbrev;
    }

    private int[] rgbValues;
    private String colorAbbrev;

    public int[] getRgbValues(){
        return rgbValues;
    }

    public String getColorAbbrev() {
        return colorAbbrev;
    }
}

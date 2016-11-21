package com.achanr.glovercolorapp.common;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 3/1/16 2:41 PM
 */
public class GCConstants {

    //Power Level Default values
    public static final String POWER_LEVEL_HIGH_TITLE = "HIGH";
    public static final String POWER_LEVEL_HIGH_ABBREV = "H";
    public static final float POWER_LEVEL_HIGH_VALUE = (float) 1.0;
    public static final String POWER_LEVEL_MEDIUM_TITLE = "MEDIUM";
    public static final String POWER_LEVEL_MEDIUM_ABBREV = "M";
    public static final float POWER_LEVEL_MEDIUM_VALUE = (float) 0.7;
    public static final String POWER_LEVEL_LOW_TITLE = "LOW";
    public static final String POWER_LEVEL_LOW_ABBREV = "L";
    public static final float POWER_LEVEL_LOW_VALUE = (float) 0.4;

    //Preferences Constants
    public static final String WAS_POWER_LEVELS_CHANGED_KEY = "was_power_levels_changed_key";
    public static final String DONT_SHOW_CHIP_PRESET_DIALOG_KEY = "dont_show_chip_preset_dialog_key";
    public static final String SORTING_KEY = "SORTING_TYPE_KEY";
    public static final String COLLECTION_SORTING_KEY = "COLLECTION_SORTING_TYPE_KEY";

    //Important color constants
    public static final String COLOR_NONE = "NONE";
    public static final String COLOR_BLANK = "BLANK";
    public static final String COLOR_WHITE = "WHITE";
    public static final String COLOR_CUSTOM = "CUSTOM";

    //Other constants
    public static final String CUSTOM_PREFIX = "??";
    public static final int MAX_COLORS = 16;
    public static final int HALF_COLORS = 8;

    //ShowcaseView Constants
    public static final long LOGIN_LOGOUT_SHOWCASE = 1001;
    public static final long POST_LOGIN_SHOWCASE = 1002;
}

package com.achanr.glovercolorapp.utility;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @date 1/8/16 12:32 PM
 */
public enum EGCColorEnum {
    NONE("", new int[]{0,0,0}),
    BLANK("--", new int[]{0,0,0}),
    WHITE("Wh", new int[]{212,238,255}),
    RED("Re", new int[]{255,0,12}),
    ORANGE("Or", new int[]{255,85,0}),
    BANANA_YELLOW("BY", new int[]{239,255,0}),
    YELLOW("Ye", new int[]{204,255,0}),
    LIME_GREEN("Li", new int[]{71,255,1}),
    GREEN("Gr", new int[]{15,147,1}),
    MINT("Mi", new int[]{136,244,220}),
    TURQUOISE("Tu", new int[]{0,198,255}),
    LIGHT_BLUE("LB", new int[]{0,148,218}),
    SKY_BLUE("SB", new int[]{1,143,253}),
    BLUE("Bl", new int[]{1,66,254}),
    PURPLE("Pu", new int[]{66,1,255}),
    LAVENDAR("La", new int[]{172,120,255}),
    BLUSH("Bu", new int[]{201,95,199}),
    LIGHT_PINK("LP", new int[]{255,95,199}),
    HOT_PINK("HP", new int[]{245,21,154}),
    PEACH("Pe", new int[]{255,217,116}),
    WARM_WHITE("WW", new int[]{243,228,195}),
    SILVER("Si", new int[]{159,207,227}),
    LUNA("Lu", new int[]{48,113,207}),

    ;

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


    @Override
    public String toString() {
        return super.toString().replace("_", " ");
    }
}

package com.achanr.glovercolorapp.utility;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 2/15/16 1:33 PM
 */
public enum EGCChipSet {
    NONE(null, null),
    CHROMA_24(
            new EGCColorEnum[]{
                    EGCColorEnum.NONE,
                    EGCColorEnum.WHITE,
                    EGCColorEnum.BLANK,
                    EGCColorEnum.RED,
                    EGCColorEnum.ORANGE,
                    EGCColorEnum.BANANA_YELLOW,
                    EGCColorEnum.YELLOW,
                    EGCColorEnum.LIME_GREEN,
                    EGCColorEnum.GREEN,
                    EGCColorEnum.MINT,
                    EGCColorEnum.TURQUOISE,
                    EGCColorEnum.LIGHT_BLUE,
                    EGCColorEnum.SKY_BLUE,
                    EGCColorEnum.BLUE,
                    EGCColorEnum.PURPLE,
                    EGCColorEnum.LAVENDAR,
                    EGCColorEnum.BLUSH,
                    EGCColorEnum.LIGHT_PINK,
                    EGCColorEnum.HOT_PINK,
                    EGCColorEnum.PEACH,
                    EGCColorEnum.WARM_WHITE,
                    EGCColorEnum.SILVER,
                    EGCColorEnum.LUNA,
            },
            new EGCModeEnum[]{
                    EGCModeEnum.STROBE,
                    EGCModeEnum.HYPERSTROBE,
                    EGCModeEnum.DOPS,
                    EGCModeEnum.STROBIE,
                    EGCModeEnum.CHROMA
            }),


    ;

    private EGCColorEnum[] mColorEnums;
    private EGCModeEnum[] mModeEnums;

    EGCChipSet(EGCColorEnum[] colorEnums, EGCModeEnum[] modeEnums) {
        mColorEnums = colorEnums;
        mModeEnums = modeEnums;
    }

    public EGCColorEnum[] getColorEnums() {
        return mColorEnums != null ? mColorEnums : EGCColorEnum.values();
    }

    public EGCModeEnum[] getModeEnums() {
        return mModeEnums != null ? mModeEnums : EGCModeEnum.values();
    }
}

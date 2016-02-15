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
                    EGCColorEnum.BLANK,
                    EGCColorEnum.RED,
                    EGCColorEnum.BLUE,
                    EGCColorEnum.GREEN,
            },
            new EGCModeEnum[]{
                    EGCModeEnum.HYPERSTROBE,
                    EGCModeEnum.STROBE,
                    EGCModeEnum.TRACER,
            }),;

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

package com.achanr.glovercolorapp.models;

import com.achanr.glovercolorapp.common.GCConstants;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Andrew Chanrasmi on 12/2/16
 */

public class GCUploadSet implements Serializable {

    private String userUid;
    private String title;
    private String description;
    private String colors;
    private String mode;
    private String chip;
    private String custom_colors;
    private String timestamp;

    public GCUploadSet() {
    }

    private GCUploadSet(String userUid, String title, String description, String colors, String mode, String chip, String custom_colors, String timestamp) {
        this.userUid = userUid;
        this.title = title;
        this.description = description;
        this.colors = colors;
        this.mode = mode;
        this.chip = chip;
        this.custom_colors = custom_colors;
        this.timestamp = timestamp;
    }

    public static GCUploadSet convertToUploadSet(GCOnlineDBSavedSet onlineDBSavedSet, String userUid) {
        SimpleDateFormat format = new SimpleDateFormat(GCConstants.UPLOAD_DATE_FORMAT, Locale.getDefault());
        String currentTime = format.format(Calendar.getInstance().getTime());
        return new GCUploadSet(
                userUid,
                onlineDBSavedSet.getTitle(),
                onlineDBSavedSet.getDescription(),
                onlineDBSavedSet.getColors(),
                onlineDBSavedSet.getMode(),
                onlineDBSavedSet.getChip(),
                onlineDBSavedSet.getCustom_colors(),
                currentTime
        );
    }

    public String getUserUid() {
        return userUid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getColors() {
        return colors;
    }

    public String getMode() {
        return mode;
    }

    public String getChip() {
        return chip;
    }

    public String getCustom_colors() {
        return custom_colors;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

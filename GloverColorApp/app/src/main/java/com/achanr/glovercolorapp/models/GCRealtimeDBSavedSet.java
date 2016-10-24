package com.achanr.glovercolorapp.models;

/**
 * @author Andrew Chanrasmi on 10/24/16
 */

public class GCRealtimeDBSavedSet {

    private int id;
    private String title;
    private String colors;
    private String mode;
    private String chip;
    private String custom_colors;

    public GCRealtimeDBSavedSet() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getChip() {
        return chip;
    }

    public void setChip(String chip) {
        this.chip = chip;
    }

    public String getCustom_colors() {
        return custom_colors;
    }

    public void setCustom_colors(String custom_colors) {
        this.custom_colors = custom_colors;
    }
}

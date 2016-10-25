package com.achanr.glovercolorapp.models;

import java.io.Serializable;

/**
 * @author Andrew Chanrasmi on 10/24/16
 */

public class GCOnlineDBSavedSet implements Serializable {

    private int id;
    private String title;
    private String colors;
    private String mode;
    private String chip;
    private String custom_colors;

    public GCOnlineDBSavedSet() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GCOnlineDBSavedSet that = (GCOnlineDBSavedSet) o;

        if (id != that.id) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (colors != null ? !colors.equals(that.colors) : that.colors != null) return false;
        if (mode != null ? !mode.equals(that.mode) : that.mode != null) return false;
        if (chip != null ? !chip.equals(that.chip) : that.chip != null) return false;
        return custom_colors != null ? custom_colors.equals(that.custom_colors) : that.custom_colors == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (colors != null ? colors.hashCode() : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        result = 31 * result + (chip != null ? chip.hashCode() : 0);
        result = 31 * result + (custom_colors != null ? custom_colors.hashCode() : 0);
        return result;
    }
}

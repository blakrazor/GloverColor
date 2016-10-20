package com.achanr.glovercolorapp.common;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

/**
 * @author Andrew Chanrasmi
 * @created 3/22/16 3:20 PM
 */
public class InputFilterMinMax implements InputFilter {

    private final int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String destString = dest.toString();
            if (dstart == 0 && dend == 3) {
                destString = "";
            }
            int input = Integer.parseInt(destString + source.toString());
            if (isInRange(min, max, input)) {
                return null;
            }
        } catch (NumberFormatException nfe) {
            Log.e(this.getClass().getSimpleName(), nfe.getMessage());
        }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
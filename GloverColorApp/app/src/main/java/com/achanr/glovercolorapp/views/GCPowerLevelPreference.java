package com.achanr.glovercolorapp.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.models.GCPowerLevel;
import com.achanr.glovercolorapp.utility.EGCColorEnum;
import com.achanr.glovercolorapp.utility.GCConstants;
import com.achanr.glovercolorapp.utility.GCPowerLevelUtil;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 3/2/16 12:28 PM
 */
public class GCPowerLevelPreference extends DialogPreference {

    public interface PowerLevelCallback {
        void onPowerLevelChanged();
    }

    private SeekBar mSeekBarHigh;
    private SeekBar mSeekBarMedium;
    private SeekBar mSeekBarLow;

    private RelativeLayout mColorSwatchHigh;
    private RelativeLayout mColorSwatchMedium;
    private RelativeLayout mColorSwatchLow;

    private TextView mTextViewHigh;
    private TextView mTextViewMedium;
    private TextView mTextViewLow;

    private final EGCColorEnum PREFERENCE_COLOR = EGCColorEnum.RED;

    private enum PowerEnum {HIGH, MEDIUM, LOW}

    private boolean wasDefaultClicked = false;
    private PowerLevelCallback mPowerLevelCallback;

    public GCPowerLevelPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        setDialogLayoutResource(R.layout.preference_power_level);
    }

    public void setPowerLevelCallback(PowerLevelCallback powerLevelCallback) {
        mPowerLevelCallback = powerLevelCallback;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setNeutralButton("Default", this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            wasDefaultClicked = true;

            float defaultHighValue = GCConstants.POWER_LEVEL_HIGH_VALUE;
            mSeekBarHigh.setProgress((int) (defaultHighValue * 10));
            configurePowerLevelVariables(PowerEnum.HIGH, Integer.toString((int) (defaultHighValue * 100)), defaultHighValue);

            float defaultMediumValue = GCConstants.POWER_LEVEL_MEDIUM_VALUE;
            mSeekBarMedium.setProgress((int) (defaultMediumValue * 10));
            configurePowerLevelVariables(PowerEnum.MEDIUM, Integer.toString((int) (defaultMediumValue * 100)), defaultMediumValue);

            float defaultLowValue = GCConstants.POWER_LEVEL_LOW_VALUE;
            mSeekBarLow.setProgress((int) (defaultLowValue * 10));
            configurePowerLevelVariables(PowerEnum.LOW, Integer.toString((int) (defaultLowValue * 100)), defaultLowValue);
        } else {
            super.onClick(dialog, which);
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        setupVariables(view);
        setupCurrentPowerLevels();
        setupListeners();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult || wasDefaultClicked) {
            wasDefaultClicked = false;
            GCPowerLevelUtil.updatePowerLevelValue(GCConstants.POWER_LEVEL_HIGH_TITLE, (float) mSeekBarHigh.getProgress() / 10);
            GCPowerLevelUtil.updatePowerLevelValue(GCConstants.POWER_LEVEL_MEDIUM_TITLE, (float) mSeekBarMedium.getProgress() / 10);
            GCPowerLevelUtil.updatePowerLevelValue(GCConstants.POWER_LEVEL_LOW_TITLE, (float) mSeekBarLow.getProgress() / 10);
            mPowerLevelCallback.onPowerLevelChanged();
        }
    }

    private void setupVariables(View view) {
        mSeekBarHigh = (SeekBar) view.findViewById(R.id.seek_bar_high);
        mColorSwatchHigh = (RelativeLayout) view.findViewById(R.id.color_swatch_high);
        mTextViewHigh = (TextView) view.findViewById(R.id.color_swatch_high_textview);

        mSeekBarMedium = (SeekBar) view.findViewById(R.id.seek_bar_medium);
        mColorSwatchMedium = (RelativeLayout) view.findViewById(R.id.color_swatch_medium);
        mTextViewMedium = (TextView) view.findViewById(R.id.color_swatch_medium_textview);

        mSeekBarLow = (SeekBar) view.findViewById(R.id.seek_bar_low);
        mColorSwatchLow = (RelativeLayout) view.findViewById(R.id.color_swatch_low);
        mTextViewLow = (TextView) view.findViewById(R.id.color_swatch_low_textview);
    }

    private void setupCurrentPowerLevels() {
        GCPowerLevel highPower = GCPowerLevelUtil.getPowerLevelUsingTitle(GCConstants.POWER_LEVEL_HIGH_TITLE);
        mSeekBarHigh.setProgress(highPower.convertValueToInt() / 10);
        configurePowerLevelVariables(PowerEnum.HIGH, Integer.toString(highPower.convertValueToInt()), highPower.getValue());

        GCPowerLevel mediumPower = GCPowerLevelUtil.getPowerLevelUsingTitle(GCConstants.POWER_LEVEL_MEDIUM_TITLE);
        mSeekBarMedium.setProgress(mediumPower.convertValueToInt() / 10);
        configurePowerLevelVariables(PowerEnum.MEDIUM, Integer.toString(mediumPower.convertValueToInt()), mediumPower.getValue());

        GCPowerLevel lowPower = GCPowerLevelUtil.getPowerLevelUsingTitle(GCConstants.POWER_LEVEL_LOW_TITLE);
        mSeekBarLow.setProgress(lowPower.convertValueToInt() / 10);
        configurePowerLevelVariables(PowerEnum.LOW, Integer.toString(lowPower.convertValueToInt()), lowPower.getValue());
    }

    private void setupListeners() {
        mSeekBarHigh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= mSeekBarMedium.getProgress()) {
                    mSeekBarMedium.setProgress(progress);
                }

                configurePowerLevelVariables(PowerEnum.HIGH, Integer.toString(progress * 10), (float) progress / 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarMedium.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= mSeekBarLow.getProgress()) {
                    mSeekBarLow.setProgress(progress);
                }

                if (progress >= mSeekBarHigh.getProgress()) {
                    mSeekBarHigh.setProgress(progress);
                }

                configurePowerLevelVariables(PowerEnum.MEDIUM, Integer.toString(progress * 10), (float) progress / 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSeekBarLow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress >= mSeekBarMedium.getProgress()) {
                    mSeekBarMedium.setProgress(progress);
                }

                configurePowerLevelVariables(PowerEnum.LOW, Integer.toString(progress * 10), (float) progress / 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void convertSwatchToProgress(RelativeLayout colorSwatch, float powerValue) {
        int[] originalRgb = PREFERENCE_COLOR.getRgbValues();
        int[] newRgbValues = new int[3];

        float[] hsv = new float[3];
        Color.RGBToHSV(originalRgb[0], originalRgb[1], originalRgb[2], hsv);

        hsv[1] = hsv[1] * powerValue;

        int outputColor = Color.HSVToColor(hsv);
        newRgbValues[0] = Color.red(outputColor);
        newRgbValues[1] = Color.green(outputColor);
        newRgbValues[2] = Color.blue(outputColor);

        colorSwatch.setBackgroundColor(Color.argb(255, newRgbValues[0], newRgbValues[1], newRgbValues[2]));
    }

    private void configurePowerLevelVariables(PowerEnum powerEnum, String powerString, float powerValue) {
        switch (powerEnum) {
            case HIGH:
                mTextViewHigh.setText(powerString);
                convertSwatchToProgress(mColorSwatchHigh, powerValue);
                break;
            case MEDIUM:
                mTextViewMedium.setText(powerString);
                convertSwatchToProgress(mColorSwatchMedium, powerValue);
                break;
            case LOW:
                mTextViewLow.setText(powerString);
                convertSwatchToProgress(mColorSwatchLow, powerValue);
                break;
        }
    }
}

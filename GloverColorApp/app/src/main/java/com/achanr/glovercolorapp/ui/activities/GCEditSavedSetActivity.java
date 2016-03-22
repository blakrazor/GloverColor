package com.achanr.glovercolorapp.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Transition;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.GCChipUtil;
import com.achanr.glovercolorapp.common.GCColorUtil;
import com.achanr.glovercolorapp.common.GCConstants;
import com.achanr.glovercolorapp.common.GCModeUtil;
import com.achanr.glovercolorapp.common.GCPowerLevelUtil;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.common.InputFilterMinMax;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCChip;
import com.achanr.glovercolorapp.models.GCColor;
import com.achanr.glovercolorapp.models.GCMode;
import com.achanr.glovercolorapp.models.GCPowerLevel;
import com.achanr.glovercolorapp.models.GCPoweredColor;
import com.achanr.glovercolorapp.models.GCSavedSet;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 2/15/16 1:26 PM
 */

public class GCEditSavedSetActivity extends GCBaseActivity {

    public Context mContext;
    private GCSavedSet mSavedSet;
    private GCSavedSet mNewSet;
    private EditText mTitleEditText;
    //private TextView mTitleTextView;
    private ArrayList<ColorSpinnerHolder> mColorSpinnerHolders;
    private Spinner mModeSpinner;
    private Spinner mChipSetSpinner;
    private boolean madeChanges = false;
    private GCChip mChipSet;
    private ArrayList<int[]> mCustomColorArrayList;
    private boolean enterFinished = false;
    private boolean isNewSet = false;
    private boolean wasChangeDialogCanceled = false;
    private int spinnerSelectionCount = 0;

    public static final String SAVED_SET_KEY = "saved_set_key";
    public static final String IS_NEW_SET_KEY = "is_new_set_key";

    public static final int MAX_TITLE_LENGTH = 100;

    private interface CustomColorCallback {
        void valueChanged();
    }

    private InputFilter titleFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            if (source instanceof SpannableStringBuilder) {
                SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder) source;
                for (int i = end - 1; i >= start; i--) {
                    char currentChar = source.charAt(i);
                    if (!Character.isLetterOrDigit(currentChar) && !Character.isSpaceChar(currentChar)) {
                        sourceAsSpannableBuilder.delete(i, i + 1);
                    }
                }
                return source;
            } else {
                StringBuilder filteredStringBuilder = new StringBuilder();
                for (int i = start; i < end; i++) {
                    char currentChar = source.charAt(i);
                    if (Character.isLetterOrDigit(currentChar) || Character.isSpaceChar(currentChar)) {
                        filteredStringBuilder.append(currentChar);
                    }
                }
                return filteredStringBuilder.toString();
            }
        }
    };

    private class ColorSpinnerHolder {
        private LinearLayout mColorLayout;
        private Spinner mColorSpinner;
        private RelativeLayout mColorSwatch;
        private TextView mColorSwatchTextView;
        private String mPowerLevel;

        public ColorSpinnerHolder(LinearLayout colorLayout, Spinner colorSpinner, RelativeLayout colorSwatch, TextView colorSwatchTextView) {
            mColorLayout = colorLayout;
            mColorSpinner = colorSpinner;
            mColorSwatch = colorSwatch;
            mColorSwatchTextView = colorSwatchTextView;
            mPowerLevel = GCConstants.POWER_LEVEL_HIGH_TITLE;
        }

        public LinearLayout getColorLayout() {
            return mColorLayout;
        }

        public Spinner getColorSpinner() {
            return mColorSpinner;
        }

        public RelativeLayout getColorSwatch() {
            return mColorSwatch;
        }

        public TextView getColorSwatchTextView() {
            return mColorSwatchTextView;
        }

        public GCPowerLevel getPowerLevel() {
            return GCPowerLevelUtil.getPowerLevelUsingTitle(mPowerLevel);
        }

        public void setPowerLevel(String powerLevel) {
            mPowerLevel = powerLevel;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_saved_set, mFrameLayout);
        mContext = this;
        setupToolbar(getString(R.string.title_edit_set));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            mSavedSet = (GCSavedSet) intent.getSerializableExtra(SAVED_SET_KEY);
            isNewSet = intent.getBooleanExtra(IS_NEW_SET_KEY, false);
            if (isNewSet) {
                setCustomTitle(getString(R.string.title_add_set));
            }
        }

        //mTitleTextView = (TextView) findViewById(R.id.text_view_title);
        mTitleEditText = (EditText) findViewById(R.id.edit_text_title);
        mTitleEditText.setFilters(new InputFilter[]{titleFilter});
        mModeSpinner = (Spinner) findViewById(R.id.mode_spinner);
        mChipSetSpinner = (Spinner) findViewById(R.id.chip_preset_spinner);
        mCustomColorArrayList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            mCustomColorArrayList.add(new int[]{255, 255, 255});
        }

        setupChipSetSpinner();
        setupColorSpinnerHolders();
        retrievePresetColorEnums();

        if (mSavedSet != null) {
            fillExistingData();
        } else {
            fillDefaultData();
        }

        setupColorListeners();
        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkForChanges();
            }
        });
        mTitleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //mTitleTextView.setText(mTitleEditText.getText().toString().trim());
                    //showTitleEditText(false);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        /*mTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTitleEditText(true);
                mTitleEditText.requestFocus();
            }
        });*/

        mModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkForChanges();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkForChanges();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimationListener();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isNewSet) {
            menu.add(0, 2, 2, "Save").setIcon(R.drawable.ic_save_white_48dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        } else {
            if (madeChanges) {
                menu.add(0, 1, 1, "Reset").setIcon(R.drawable.ic_settings_backup_restore_white_48dp)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                menu.add(0, 2, 2, "Save").setIcon(R.drawable.ic_save_white_48dp)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }
        menu.add(0, 3, 3, "Delete").setIcon(R.drawable.ic_delete_white_48dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: //revert changes
                showResetDialog();
                return true;
            case 2: //Save
                if (validateFields()) {
                    if (isNewSet) {
                        showSaveDialog(mContext.getString(R.string.add_new_set), mContext.getString(R.string.add_new_set_dialog));
                    } else {
                        showSaveDialog(mContext.getString(R.string.save_changes), mContext.getString(R.string.save_changes_dialog));
                    }
                }
                return true;
            case 3: //delete
                showDeleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean validateFields() {
        String newTitle = mTitleEditText.getText().toString().trim();
        if (newTitle.isEmpty()) {
            showErrorDialog(mContext.getString(R.string.error_title_empty));
            return false;
        } else if (newTitle.length() > MAX_TITLE_LENGTH) {
            showErrorDialog(String.format(mContext.getString(R.string.error_title_length), MAX_TITLE_LENGTH));
            return false;
        }

        int colorCount = 0;
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            String color = (String) colorSpinnerHolder.getColorSpinner().getSelectedItem();
            if (!color.equalsIgnoreCase(GCConstants.COLOR_BLANK)
                    && !color.equalsIgnoreCase(GCConstants.COLOR_NONE)) {
                colorCount++;
            }
        }
        if (colorCount == 0) {
            showErrorDialog(mContext.getString(R.string.error_no_color));
            return false;
        }

        return true;
    }

    private void setupColorSpinnerHolders() {
        mColorSpinnerHolders = new ArrayList<>();
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) findViewById(R.id.color_1_layout),
                (Spinner) findViewById(R.id.spinner_color_1),
                (RelativeLayout) findViewById(R.id.color_swatch_1),
                (TextView) findViewById(R.id.color_swatch_1_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) findViewById(R.id.color_2_layout),
                (Spinner) findViewById(R.id.spinner_color_2),
                (RelativeLayout) findViewById(R.id.color_swatch_2),
                (TextView) findViewById(R.id.color_swatch_2_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) findViewById(R.id.color_3_layout),
                (Spinner) findViewById(R.id.spinner_color_3),
                (RelativeLayout) findViewById(R.id.color_swatch_3),
                (TextView) findViewById(R.id.color_swatch_3_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) findViewById(R.id.color_4_layout),
                (Spinner) findViewById(R.id.spinner_color_4),
                (RelativeLayout) findViewById(R.id.color_swatch_4),
                (TextView) findViewById(R.id.color_swatch_4_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) findViewById(R.id.color_5_layout),
                (Spinner) findViewById(R.id.spinner_color_5),
                (RelativeLayout) findViewById(R.id.color_swatch_5),
                (TextView) findViewById(R.id.color_swatch_5_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) findViewById(R.id.color_6_layout),
                (Spinner) findViewById(R.id.spinner_color_6),
                (RelativeLayout) findViewById(R.id.color_swatch_6),
                (TextView) findViewById(R.id.color_swatch_6_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) findViewById(R.id.color_7_layout),
                (Spinner) findViewById(R.id.spinner_color_7),
                (RelativeLayout) findViewById(R.id.color_swatch_7),
                (TextView) findViewById(R.id.color_swatch_7_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) findViewById(R.id.color_8_layout),
                (Spinner) findViewById(R.id.spinner_color_8),
                (RelativeLayout) findViewById(R.id.color_swatch_8),
                (TextView) findViewById(R.id.color_swatch_8_textview)));
    }

    private void setupChipSetSpinner() {
        mChipSetSpinner.setAdapter(new ArrayAdapter<>(mContext, R.layout.spinner_color_item, GCChipUtil.getAllChipTitles()));
        if (mSavedSet != null) {
            GCChip chipSet = mSavedSet.getChipSet();
            int chipsetIndex = GCChipUtil.getAllChipTitles().indexOf(chipSet.getTitle());
            mChipSetSpinner.setSelection(chipsetIndex, false);
        } else {
            mChipSetSpinner.setSelection(0, false);
        }
        mChipSet = GCChipUtil.getChipUsingTitle((String) mChipSetSpinner.getSelectedItem());
        mChipSetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (wasChangeDialogCanceled) {
                    wasChangeDialogCanceled = false;
                } else {
                    showChangingChipDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void matchColorSpinnerToSwatch() {
        int index = 0;
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            GCColor colorEnum = GCColorUtil.getColorUsingTitle((String) colorSpinnerHolder.getColorSpinner().getSelectedItem());
            int[] rgbValues;
            if (colorEnum.getTitle().equalsIgnoreCase(GCConstants.COLOR_CUSTOM)) {
                rgbValues = GCUtil.convertRgbToPowerLevel(mCustomColorArrayList.get(index), colorSpinnerHolder.getPowerLevel());
            } else {
                rgbValues = GCUtil.convertRgbToPowerLevel(colorEnum.getRGBValues(), colorSpinnerHolder.getPowerLevel());
            }
            colorSpinnerHolder.getColorSwatch().setBackgroundColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));
            index++;
        }
    }

    private void retrievePresetColorEnums() {
        mChipSet = GCChipUtil.getChipUsingTitle((String) mChipSetSpinner.getSelectedItem());
        fillSpinnersWithEnums(mChipSet.getColors(), mChipSet.getModes());
    }

    private void fillSpinnersWithEnums(final ArrayList<String> colorArray, ArrayList<String> modeArray) {

        mModeSpinner.setAdapter(new ArrayAdapter<>(mContext, R.layout.spinner_color_item, modeArray));

        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            ArrayAdapter<String> customAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_color_item, colorArray) {
                @Override
                public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                    View v = convertView;
                    if (v == null) {
                        Context mContext = this.getContext();
                        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        // Androids orginal spinner view item
                        v = vi.inflate(R.layout.spinner_color_dropdown_item, null);
                    }
                    // The text view of the spinner list view
                    TextView tv = (TextView) v.findViewById(R.id.spinner_color_dropdown_item_title);
                    RelativeLayout dropdownItemBackground = (RelativeLayout) v.findViewById(R.id.spinner_color_dropdown_item_background);
                    if (colorArray.get(position).equalsIgnoreCase(GCConstants.COLOR_CUSTOM)) {
                        tv.setText(GCConstants.COLOR_CUSTOM);
                        tv.setTextColor(Color.BLACK);
                        int[] rgbValues = new int[]{255, 255, 255};
                        dropdownItemBackground.setBackgroundColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));
                    } else {
                        GCColor color = GCColorUtil.getColorUsingTitle(colorArray.get(position));
                        tv.setText(color.getTitle());
                        if (color.getTitle().equalsIgnoreCase(GCConstants.COLOR_BLANK)) {
                            tv.setTextColor(Color.WHITE);
                        } else {
                            tv.setTextColor(Color.BLACK);
                        }

                        if (color.getTitle().equalsIgnoreCase(GCConstants.COLOR_NONE)) {
                            dropdownItemBackground.setBackgroundColor(Color.WHITE);
                        } else {
                            int[] rgbValues = color.getRGBValues();
                            dropdownItemBackground.setBackgroundColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));
                        }
                    }
                    return v;
                }
            };
            colorSpinnerHolder.getColorSpinner().setAdapter(customAdapter);
            colorSpinnerHolder.setPowerLevel(GCConstants.POWER_LEVEL_HIGH_TITLE);
            colorSpinnerHolder.getColorSwatchTextView().setText(GCConstants.POWER_LEVEL_HIGH_ABBREV);
        }
    }

    private void fillExistingData() {
        String title = mSavedSet.getTitle();
        ArrayList<GCPoweredColor> colorList = mSavedSet.getColors();
        GCMode mode = mSavedSet.getMode();
        ArrayList<int[]> customColorArray = mSavedSet.getCustomColors();
        mCustomColorArrayList = new ArrayList<>();
        for (int[] item : customColorArray) {
            mCustomColorArrayList.add(item.clone());
        }

        mTitleEditText.setText(title);
        //mTitleTextView.setText(title);
        //showTitleEditText(false);

        int spinnerIndex = 0;
        ArrayList<String> colors = mChipSet.getColors();
        for (GCPoweredColor color : colorList) {
            int colorIndex = 0;
            for (String colorItem : colors) {
                if (colorItem.equalsIgnoreCase(color.getColor().getTitle())) {
                    ColorSpinnerHolder colorSpinnerHolder = mColorSpinnerHolders.get(spinnerIndex);
                    colorSpinnerHolder.getColorSpinner().setSelection(colorIndex, false);
                    colorSpinnerHolder.setPowerLevel(color.getPowerLevel().getTitle());
                    colorSpinnerHolder.getColorSwatchTextView().setText(color.getPowerLevel().getAbbreviation());
                    colorSpinnerSelectedFunction(colorSpinnerHolder.getColorSpinner().getSelectedView(),
                            colorSpinnerHolder.getColorSpinner(),
                            true);
                    break;
                } else {
                    colorIndex++;
                }
            }
            spinnerIndex++;
        }

        ArrayList<String> modes = mChipSet.getModes();
        int modeIndex = 0;
        for (String modeItem : modes) {
            if (mode.getTitle().equalsIgnoreCase(modeItem)) {
                mModeSpinner.setSelection(modeIndex);
                break;
            } else {
                modeIndex++;
            }
        }
    }

    private void fillDefaultData() {
        mTitleEditText.setText("");
        //showTitleEditText(true);

        ArrayList<String> colors = mChipSet.getColors();
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            int colorIndex = 0;
            for (String colorItem : colors) {
                if (colorItem.equalsIgnoreCase(GCConstants.COLOR_NONE)) {
                    colorSpinnerHolder.getColorSpinner().setSelection(colorIndex);
                    break;
                } else {
                    colorIndex++;
                }
            }
        }

        mModeSpinner.setSelection(0);
    }

    private void setupColorListeners() {
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            colorSpinnerHolder.getColorSpinner().setOnItemSelectedListener(mColorSpinnerSelectedListener);
            colorSpinnerHolder.getColorSwatchTextView().setOnClickListener(mColorSwatchClickListener);
        }
    }


    private AdapterView.OnItemSelectedListener mColorSpinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (spinnerSelectionCount >= 8) {
                colorSpinnerSelectedFunction(view, parent, false);
                checkForChanges();
            } else {
                spinnerSelectionCount++;
                colorSpinnerSelectedFunction(view, parent, true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void colorSpinnerSelectedFunction(View view, View parent, boolean isFirstTime) {
        TextView spinnerTv = ((TextView) view.findViewById(R.id.spinner_color_item_title));
        String colorString = spinnerTv.getText().toString();
        GCColor colorEnum = GCColorUtil.getColorUsingTitle(colorString);
        if (colorEnum.getTitle().equalsIgnoreCase(GCConstants.COLOR_NONE)) {
            ColorSpinnerHolder colorSpinnerHolder = getColorSpinnerHolder(parent);
            hideColorSpinnersAfterPosition(mColorSpinnerHolders.indexOf(colorSpinnerHolder));
        } else if (colorEnum.getTitle().equalsIgnoreCase(GCConstants.COLOR_CUSTOM)) {
            if (isFirstTime) {
                ColorSpinnerHolder colorSpinnerHolder = getColorSpinnerHolder(parent);
                int[] rgbValues = mCustomColorArrayList.get(mColorSpinnerHolders.indexOf(colorSpinnerHolder));
                spinnerTv.setTextColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));
                unhideNextColorSpinner(parent);
                matchColorSpinnerToSwatch();
            } else {
                showCustomColorDialog(spinnerTv, parent);
            }
        } else {
            int[] rgbValues = colorEnum.getRGBValues();
            spinnerTv.setTextColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));
            unhideNextColorSpinner(parent);
            matchColorSpinnerToSwatch();
        }
    }

    private View.OnClickListener mColorSwatchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView colorSwatchTv = (TextView) v;
            String currentLevelString = colorSwatchTv.getText().toString();
            GCPowerLevel currentLevel = GCPowerLevelUtil.getPowerLevelUsingAbbrev(currentLevelString);
            syncSpinnerAndSwatch(colorSwatchTv, currentLevel);
            checkForChanges();
        }
    };

    private void syncSpinnerAndSwatch(TextView colorSwatchTv, GCPowerLevel currentLevel) {
        String newLevel;
        if (currentLevel.getTitle().equalsIgnoreCase(GCConstants.POWER_LEVEL_HIGH_TITLE)) {
            newLevel = GCConstants.POWER_LEVEL_MEDIUM_TITLE;
            colorSwatchTv.setText(GCConstants.POWER_LEVEL_MEDIUM_ABBREV);
        } else if (currentLevel.getTitle().equalsIgnoreCase(GCConstants.POWER_LEVEL_MEDIUM_TITLE)) {
            newLevel = GCConstants.POWER_LEVEL_LOW_TITLE;
            colorSwatchTv.setText(GCConstants.POWER_LEVEL_LOW_ABBREV);
        } else {
            newLevel = GCConstants.POWER_LEVEL_HIGH_TITLE;
            colorSwatchTv.setText(GCConstants.POWER_LEVEL_HIGH_ABBREV);
        }

        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            if (colorSpinnerHolder.getColorSwatchTextView() == colorSwatchTv) {
                colorSpinnerHolder.setPowerLevel(newLevel);
                matchColorSpinnerToSwatch();
                break;
            }
        }
    }

    private ColorSpinnerHolder getColorSpinnerHolder(View view) {
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            if (colorSpinnerHolder.getColorSpinner() == view) {
                return colorSpinnerHolder;
            }
        }
        return null;
    }

    private void hideColorSpinnersAfterPosition(int position) {
        ArrayList<String> colors = mChipSet.getColors();
        for (int i = position + 1; i < mColorSpinnerHolders.size(); i++) {
            int colorIndex = 0;
            for (String colorItem : colors) {
                if (colorItem.equalsIgnoreCase(GCConstants.COLOR_NONE)) {
                    mColorSpinnerHolders.get(i).getColorSpinner().setSelection(colorIndex);
                    break;
                } else {
                    colorIndex++;
                }
            }
            mColorSpinnerHolders.get(i).getColorLayout().setVisibility(View.INVISIBLE);
            mColorSpinnerHolders.get(i).getColorSwatch().setVisibility(View.GONE);
        }

        mColorSpinnerHolders.get(position).getColorSwatch().setVisibility(View.GONE);
    }

    private void unhideNextColorSpinner(View view) {
        int selectedSpinner = 0;
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            if (colorSpinnerHolder.getColorSpinner() == view) {
                break;
            } else {
                selectedSpinner++;
            }
        }

        if (selectedSpinner < mColorSpinnerHolders.size() - 1) {
            mColorSpinnerHolders.get(selectedSpinner + 1).getColorLayout().setVisibility(View.VISIBLE);
        }
        mColorSpinnerHolders.get(selectedSpinner).getColorSwatch().setVisibility(View.VISIBLE);
    }

    private ArrayList<GCPoweredColor> getNewColorList() {
        ArrayList<String> colors = mChipSet.getColors();
        ArrayList<GCPoweredColor> newColorList = new ArrayList<>();
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            int colorPosition = colorSpinnerHolder.getColorSpinner().getSelectedItemPosition();
            GCColor color = GCColorUtil.getColorUsingTitle(colors.get(colorPosition));
            GCPowerLevel power = colorSpinnerHolder.getPowerLevel();
            GCPoweredColor newColor = new GCPoweredColor(color, power.getTitle());
            newColorList.add(newColor);

        }
        return newColorList;
    }

    private void saveSet() {
        String newTitle = mTitleEditText.getText().toString().trim();
        if (validateTitleAgainstDatabase(newTitle) && isNewSet) {
            showErrorDialog(mContext.getString(R.string.error_title_exists));
            return;
        }

        ArrayList<GCPoweredColor> newColorList = getNewColorList();

        ArrayList<String> modes = mChipSet.getModes();
        int modePosition = mModeSpinner.getSelectedItemPosition();
        GCMode newMode = GCModeUtil.getModeUsingTitle(modes.get(modePosition));

        mNewSet = new GCSavedSet();
        mNewSet.setTitle(newTitle);
        mNewSet.setColors(newColorList);
        mNewSet.setMode(newMode);
        mNewSet.setChipSet(mChipSet);
        mNewSet.setCustomColors(mCustomColorArrayList);

        if (isNewSet) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(GCSavedSetListActivity.NEW_SET_KEY, mNewSet);
            finishActivityTransition(RESULT_OK, resultIntent);
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(GCSavedSetListActivity.OLD_SET_KEY, mSavedSet);
            resultIntent.putExtra(GCSavedSetListActivity.NEW_SET_KEY, mNewSet);
            finishActivityTransition(RESULT_OK, resultIntent);
        }
    }

    private void finishActivityTransition(int resultCode, Intent resultIntent) {
        setResult(resultCode, resultIntent);
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            if (isNewSet) {
                addSetAnimation(true);
            } else {
                getWindow().setExitTransition(new Fade());
                supportFinishAfterTransition();
            }
        } else {
            // Implement this feature without material design
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private boolean validateTitleAgainstDatabase(String title) {
        ArrayList<GCSavedSet> savedSetList = GCDatabaseHelper.SAVED_SET_DATABASE.getAllData();
        for (GCSavedSet savedSet : savedSetList) {
            if (savedSet.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }

    private void showSaveDialog(String title, String body) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton(mContext.getString(R.string.save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveSet();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_save_black_48dp)
                .show();
    }

    private void showResetDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.revert_changes))
                .setMessage(mContext.getString(R.string.revert_changes_dialog))
                .setPositiveButton(mContext.getString(R.string.reset), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        wasChangeDialogCanceled = true;
                        spinnerSelectionCount = 0;
                        if (mSavedSet == null) {
                            mChipSetSpinner.setSelection(0, false);
                            mChipSet = GCChipUtil.getChipUsingTitle((String) mChipSetSpinner.getSelectedItem());
                            retrievePresetColorEnums();
                            fillDefaultData();
                        } else {
                            GCChip chipSet = mSavedSet.getChipSet();
                            int chipsetIndex = GCChipUtil.getAllChipTitles().indexOf(chipSet.getTitle());
                            mChipSetSpinner.setSelection(chipsetIndex, false);
                            retrievePresetColorEnums();
                            fillExistingData();
                            setupColorListeners();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_settings_backup_restore_black_48dp)
                .show();
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.delete))
                .setMessage(mContext.getString(R.string.delete_dialog))
                .setPositiveButton(mContext.getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(GCSavedSetListActivity.OLD_SET_KEY, mSavedSet);
                        returnIntent.putExtra(GCSavedSetListActivity.IS_DELETE_KEY, true);
                        finishActivityTransition(RESULT_OK, returnIntent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_delete_black_48dp)
                .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.error))
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_48dp)
                .show();
    }

    public void showChangingChipDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.changing_preset_chip))
                .setMessage(getString(R.string.changing_preset_chip_desc))
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        retrievePresetColorEnums();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        wasChangeDialogCanceled = true;
                        if (mSavedSet != null) {
                            GCChip chipSet = mSavedSet.getChipSet();
                            int chipsetIndex = GCChipUtil.getAllChipTitles().indexOf(chipSet.getTitle());
                            mChipSetSpinner.setSelection(chipsetIndex, false);
                        } else {
                            mChipSetSpinner.setSelection(0, false);
                        }
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_48dp)
                .show();
    }

    public void showLeavingDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.unsaved_changes))
                .setMessage(mContext.getString(R.string.unsaved_changes_dialog))
                .setPositiveButton(mContext.getString(R.string.exit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finishActivityTransition(RESULT_CANCELED, null);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_48dp)
                .show();
    }

    public boolean madeChanges() {
        if (mSavedSet == null) {
            return true;
        }

        if (!mTitleEditText.getText().toString().trim().equals(mSavedSet.getTitle())) {
            return true;
        }

        int index = 0;
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            String colorEnum = (String) colorSpinnerHolder.getColorSpinner().getSelectedItem();
            if (mSavedSet.getColors().size() > index) {
                if (!colorEnum.equalsIgnoreCase(mSavedSet.getColors().get(index).getColor().getTitle())
                        || !colorSpinnerHolder.getPowerLevel().getTitle().equalsIgnoreCase(mSavedSet.getColors().get(index).getPowerLevel().getTitle())) {
                    return true;
                }
            } else if (!colorEnum.equalsIgnoreCase(GCConstants.COLOR_NONE)) {
                return true;
            }
            index++;
        }

        String modeEnum = (String) mModeSpinner.getSelectedItem();
        if (!modeEnum.equalsIgnoreCase(mSavedSet.getMode().getTitle())) {
            return true;
        }

        for (int i = 0; i < mCustomColorArrayList.size(); i++) {
            int[] oldRgbValues = mSavedSet.getCustomColors().get(i);
            int[] newRgbValues = mCustomColorArrayList.get(i);
            if (oldRgbValues[0] != newRgbValues[0] || oldRgbValues[1] != newRgbValues[1] || oldRgbValues[2] != newRgbValues[2]) {
                return true;
            }
        }

        return false;
    }

    private void checkForChanges() {
        if (madeChanges()) {
            if (!madeChanges) {
                madeChanges = true;
                invalidateOptionsMenu();
            }
        } else {
            if (madeChanges) {
                madeChanges = false;
                invalidateOptionsMenu();
            }
        }
    }

    private void showCustomColorDialog(final TextView spinnerTv, final View parent) {
        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialog_custom_color, null);
        final View colorPreview = dialoglayout.findViewById(R.id.custom_color_preview);
        EditText redValueEditText = (EditText) dialoglayout.findViewById(R.id.red_value_edittext);
        redValueEditText.setFilters(new InputFilter[]{new InputFilterMinMax("0", "255")});
        EditText greenValueEditText = (EditText) dialoglayout.findViewById(R.id.green_value_edittext);
        greenValueEditText.setFilters(new InputFilter[]{new InputFilterMinMax("0", "255")});
        EditText blueValueEditText = (EditText) dialoglayout.findViewById(R.id.blue_value_edittext);
        blueValueEditText.setFilters(new InputFilter[]{new InputFilterMinMax("0", "255")});
        final SeekBar redValueSeekBar = (SeekBar) dialoglayout.findViewById(R.id.seek_bar_red);
        final SeekBar greenValueSeekBar = (SeekBar) dialoglayout.findViewById(R.id.seek_bar_green);
        final SeekBar blueValueSeekBar = (SeekBar) dialoglayout.findViewById(R.id.seek_bar_blue);

        CustomColorCallback customColorCallback = new CustomColorCallback() {
            @Override
            public void valueChanged() {
                int redValue = redValueSeekBar.getProgress();
                int greenValue = greenValueSeekBar.getProgress();
                int blueValue = blueValueSeekBar.getProgress();
                colorPreview.setBackgroundColor(Color.argb(255, redValue, greenValue, blueValue));
            }
        };
        connectSeekBarAndEditText(redValueSeekBar, redValueEditText, customColorCallback);
        connectSeekBarAndEditText(blueValueSeekBar, blueValueEditText, customColorCallback);
        connectSeekBarAndEditText(greenValueSeekBar, greenValueEditText, customColorCallback);

        ColorSpinnerHolder colorSpinnerHolder = getColorSpinnerHolder(parent);
        final int position = mColorSpinnerHolders.indexOf(colorSpinnerHolder);
        final int[] oldValues = mCustomColorArrayList.get(position);
        redValueSeekBar.setProgress(oldValues[0]);
        redValueEditText.setText(Integer.toString(oldValues[0]));
        greenValueSeekBar.setProgress(oldValues[1]);
        greenValueEditText.setText(Integer.toString(oldValues[1]));
        blueValueSeekBar.setProgress(oldValues[2]);
        blueValueEditText.setText(Integer.toString(oldValues[2]));

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialoglayout);
        builder.setTitle("Set Custom Color");
        builder.setMessage("Set custom color based on RGB values");
        builder.setPositiveButton("SET COLOR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int redValue = redValueSeekBar.getProgress();
                int greenValue = greenValueSeekBar.getProgress();
                int blueValue = blueValueSeekBar.getProgress();
                int[] rgbValues = new int[]{redValue, greenValue, blueValue};
                oldValues[0] = rgbValues[0];
                oldValues[1] = rgbValues[1];
                oldValues[2] = rgbValues[2];
                spinnerTv.setTextColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));
                unhideNextColorSpinner(parent);
                matchColorSpinnerToSwatch();
                checkForChanges();
                dialog.dismiss();
            }
        });
        builder.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Toast.makeText(mContext, "You must set the custom color to continue.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        builder.setIcon(R.drawable.ic_settings_black_48dp);
        builder.show();
    }

    private void connectSeekBarAndEditText(final SeekBar seekBar, final EditText editText, final CustomColorCallback callback) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editText.setText(Integer.toString(progress));
                    callback.valueChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                if (inputText.isEmpty()) {
                    inputText = Integer.toString(0);
                }
                int progress = Integer.parseInt(inputText);
                seekBar.setProgress(progress);
                callback.valueChanged();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (editText.getText().toString().isEmpty()) {
                        String inputText = Integer.toString(0);
                        editText.setText(inputText);
                        callback.valueChanged();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (madeChanges()) {
            showLeavingDialog();
        } else {
            finishActivityTransition(RESULT_CANCELED, null);
        }
    }

    /*private void showTitleEditText(boolean willShow) {
        if (willShow) {
            mTitleEditText.setVisibility(View.VISIBLE);
            mTitleTextView.setVisibility(View.GONE);
        } else {
            mTitleTextView.setVisibility(View.VISIBLE);
            mTitleEditText.setVisibility(View.GONE);
        }
    }*/

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void fadeBackgroundColor(boolean isReverse) {
        final View parentView = findViewById(R.id.edit_set_layout);
        TypedValue darkColor = new TypedValue();
        TypedValue lightColor = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, darkColor, true);
        getTheme().resolveAttribute(R.attr.background, lightColor, true);
        ValueAnimator colorAnimator;
        if (isReverse) {
            colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), lightColor.data, darkColor.data);
            colorAnimator.setInterpolator(new AccelerateInterpolator());
            colorAnimator.setDuration(500); // milliseconds
        } else {
            colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), darkColor.data, lightColor.data);
            colorAnimator.setInterpolator(new DecelerateInterpolator());
            colorAnimator.setDuration(1000); // milliseconds
        }
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                parentView.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        colorAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void addSetAnimation(final boolean isReverse) {
        // previously invisible view
        final View myView = findViewById(R.id.edit_set_layout);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth();
        int cy = myView.getMeasuredHeight();
        int finalRadius = myView.getHeight() * 2;
        TypedValue startColor = new TypedValue();
        TypedValue endColor = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, startColor, true);
        getTheme().resolveAttribute(R.attr.background, endColor, true);

        Animator anim;
        ValueAnimator colorAnimator;
        if (isReverse) {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, finalRadius, 0);
            colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), endColor.data, startColor.data);
        } else {
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
            myView.setVisibility(View.VISIBLE);
            colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startColor.data, endColor.data);
        }

        colorAnimator.setInterpolator(new LinearInterpolator()); // milliseconds
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                myView.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim, colorAnimator);
        animatorSet.setDuration(1000);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isReverse) {
                    myView.setVisibility(View.INVISIBLE);
                    getWindow().setExitTransition(new Fade());
                    supportFinishAfterTransition();
                }
            }
        });
        // start the animation
        animatorSet.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimationListener() {
        getWindow().getEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                if (enterFinished) {
                    if (!isNewSet) {
                        fadeBackgroundColor(true);
                    }
                    enterFinished = false;
                } else {
                    if (isNewSet) {
                        findViewById(R.id.edit_set_layout).setVisibility(View.INVISIBLE);
                        addSetAnimation(false);
                    } else {
                        fadeBackgroundColor(false);
                    }
                    enterFinished = true;
                }
            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }
}

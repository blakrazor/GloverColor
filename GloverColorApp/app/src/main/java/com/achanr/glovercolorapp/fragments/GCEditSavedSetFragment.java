package com.achanr.glovercolorapp.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.database.GCSavedSetDatabase;
import com.achanr.glovercolorapp.listeners.IGCEditSavedSetFragmentListener;
import com.achanr.glovercolorapp.models.GCColor;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.utility.EGCColorEnum;
import com.achanr.glovercolorapp.utility.EGCModeEnum;
import com.achanr.glovercolorapp.utility.EGCPowerLevelEnum;
import com.achanr.glovercolorapp.utility.GCUtil;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/11/16 3:40 PM
 */
public class GCEditSavedSetFragment extends Fragment {

    private Context mContext;
    private IGCEditSavedSetFragmentListener mListener;
    private GCSavedSet mSavedSet;
    private GCSavedSet mNewSet;
    private EditText mTitleEditText;
    private ArrayList<ColorSpinnerHolder> mColorSpinnerHolders;
    private Spinner mModeSpinner;

    private boolean isNewSet = false;

    private static final String SAVED_SET_KEY = "saved_set_key";
    private static final String NEW_SET_KEY = "new_set_key";

    public static final int MAX_TITLE_LENGTH = 100;

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
        private EGCPowerLevelEnum mPowerLevelEnum;

        public ColorSpinnerHolder(LinearLayout colorLayout, Spinner colorSpinner, RelativeLayout colorSwatch, TextView colorSwatchTextView) {
            mColorLayout = colorLayout;
            mColorSpinner = colorSpinner;
            mColorSwatch = colorSwatch;
            mColorSwatchTextView = colorSwatchTextView;
            mPowerLevelEnum = EGCPowerLevelEnum.HIGH;
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

        public EGCPowerLevelEnum getPowerLevelEnum() {
            return mPowerLevelEnum;
        }

        public void setPowerLevelEnum(EGCPowerLevelEnum powerLevelEnum) {
            mPowerLevelEnum = powerLevelEnum;
        }
    }

    public GCEditSavedSetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static GCEditSavedSetFragment newInstance(GCSavedSet savedSet, boolean isNewSet) {
        GCEditSavedSetFragment fragment = new GCEditSavedSetFragment();
        Bundle args = new Bundle();
        args.putSerializable(SAVED_SET_KEY, savedSet);
        args.putBoolean(NEW_SET_KEY, isNewSet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof IGCEditSavedSetFragmentListener) {
            mListener = (IGCEditSavedSetFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IGCEditSavedSetFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mSavedSet = (GCSavedSet) getArguments().getSerializable(SAVED_SET_KEY);
            isNewSet = getArguments().getBoolean(NEW_SET_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_saved_set, container, false);
        mTitleEditText = (EditText) v.findViewById(R.id.edit_text_title);
        mTitleEditText.setFilters(new InputFilter[]{titleFilter});

        setupColorSpinnerHolders(v);
        fillColorSpinnerWithEnums();

        mModeSpinner = (Spinner) v.findViewById(R.id.edit_text_mode);
        mModeSpinner.setAdapter(new ArrayAdapter<>(mContext, R.layout.spinner_color_item, EGCModeEnum.values()));

        if (mSavedSet != null) {
            fillOutData();
        } else {
            fillDefaultData();
        }
        setupColorListeners();
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isNewSet) {
            menu.add(0, 1, 1, "Share").setIcon(android.R.drawable.ic_menu_share)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        menu.add(0, 2, 2, "Save").setIcon(android.R.drawable.ic_menu_save)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 3, 3, "Reset").setIcon(android.R.drawable.ic_menu_revert)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 4, 4, "Delete").setIcon(android.R.drawable.ic_menu_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: //share
                showShareDialog();
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
            case 3: //revert changes
                showResetDialog();
                return true;
            case 4: //Delete
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
        EGCColorEnum[] colors = EGCColorEnum.values();
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            int colorPosition = colorSpinnerHolder.getColorSpinner().getSelectedItemPosition();
            if (colors[colorPosition] != EGCColorEnum.BLANK
                    && colors[colorPosition] != EGCColorEnum.NONE) {
                colorCount++;
            }
        }
        if (colorCount == 0) {
            showErrorDialog(mContext.getString(R.string.error_no_color));
            return false;
        }

        return true;
    }

    private void setupColorSpinnerHolders(View v) {
        mColorSpinnerHolders = new ArrayList<>();
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) v.findViewById(R.id.color_1_layout),
                (Spinner) v.findViewById(R.id.spinner_color_1),
                (RelativeLayout) v.findViewById(R.id.color_swatch_1),
                (TextView) v.findViewById(R.id.color_swatch_1_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) v.findViewById(R.id.color_2_layout),
                (Spinner) v.findViewById(R.id.spinner_color_2),
                (RelativeLayout) v.findViewById(R.id.color_swatch_2),
                (TextView) v.findViewById(R.id.color_swatch_2_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) v.findViewById(R.id.color_3_layout),
                (Spinner) v.findViewById(R.id.spinner_color_3),
                (RelativeLayout) v.findViewById(R.id.color_swatch_3),
                (TextView) v.findViewById(R.id.color_swatch_3_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) v.findViewById(R.id.color_4_layout),
                (Spinner) v.findViewById(R.id.spinner_color_4),
                (RelativeLayout) v.findViewById(R.id.color_swatch_4),
                (TextView) v.findViewById(R.id.color_swatch_4_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) v.findViewById(R.id.color_5_layout),
                (Spinner) v.findViewById(R.id.spinner_color_5),
                (RelativeLayout) v.findViewById(R.id.color_swatch_5),
                (TextView) v.findViewById(R.id.color_swatch_5_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) v.findViewById(R.id.color_6_layout),
                (Spinner) v.findViewById(R.id.spinner_color_6),
                (RelativeLayout) v.findViewById(R.id.color_swatch_6),
                (TextView) v.findViewById(R.id.color_swatch_6_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) v.findViewById(R.id.color_7_layout),
                (Spinner) v.findViewById(R.id.spinner_color_7),
                (RelativeLayout) v.findViewById(R.id.color_swatch_7),
                (TextView) v.findViewById(R.id.color_swatch_7_textview)));
        mColorSpinnerHolders.add(new ColorSpinnerHolder((LinearLayout) v.findViewById(R.id.color_8_layout),
                (Spinner) v.findViewById(R.id.spinner_color_8),
                (RelativeLayout) v.findViewById(R.id.color_swatch_8),
                (TextView) v.findViewById(R.id.color_swatch_8_textview)));
    }

    private void matchColorSpinnerToSwatch() {
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            EGCColorEnum colorEnum = (EGCColorEnum) colorSpinnerHolder.getColorSpinner().getSelectedItem();
            int[] rgbValues = GCUtil.convertRgbToPowerLevel(colorEnum.getRgbValues(), colorSpinnerHolder.getPowerLevelEnum());
            colorSpinnerHolder.getColorSwatch().setBackgroundColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));
        }
    }

    private void fillColorSpinnerWithEnums() {
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            colorSpinnerHolder.getColorSpinner().setAdapter(new ArrayAdapter<>(mContext, R.layout.spinner_color_item, EGCColorEnum.values()));
        }
    }

    private void fillOutData() {
        String title = mSavedSet.getTitle();
        ArrayList<GCColor> colorList = mSavedSet.getColors();
        EGCModeEnum mode = mSavedSet.getMode();

        mTitleEditText.setText(title);

        int spinnerIndex = 0;
        EGCColorEnum[] colors = EGCColorEnum.values();
        for (GCColor color : colorList) {
            int colorIndex = 0;
            for (EGCColorEnum colorItem : colors) {
                if (colorItem == color.getColorEnum()) {
                    ColorSpinnerHolder colorSpinnerHolder = mColorSpinnerHolders.get(spinnerIndex);
                    colorSpinnerHolder.getColorSpinner().setSelection(colorIndex);
                    colorSpinnerHolder.setPowerLevelEnum(color.getPowerLevelEnum());
                    colorSpinnerHolder.getColorSwatchTextView().setText(color.getPowerLevelEnum().getPowerAbbrev());
                    break;
                } else {
                    colorIndex++;
                }
            }
            spinnerIndex++;
        }

        EGCModeEnum[] modes = EGCModeEnum.values();
        int modeIndex = 0;
        for (EGCModeEnum modeItem : modes) {
            if (mode == modeItem) {
                mModeSpinner.setSelection(modeIndex);
            } else {
                modeIndex++;
            }
        }
    }

    private void fillDefaultData() {
        mTitleEditText.setText("");

        EGCColorEnum[] colors = EGCColorEnum.values();
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            int colorIndex = 0;
            for (EGCColorEnum colorItem : colors) {
                if (colorItem == EGCColorEnum.NONE) {
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
            TextView spinnerTv = ((TextView) view.findViewById(R.id.spinner_color_item_title));
            String colorString = spinnerTv.getText().toString();
            EGCColorEnum colorEnum = EGCColorEnum.valueOf(colorString);
            if (colorEnum == EGCColorEnum.NONE) {
                ColorSpinnerHolder colorSpinnerHolder = getColorSpinnerHolder(parent);
                hideColorSpinnersAfterPosition(mColorSpinnerHolders.indexOf(colorSpinnerHolder));
            } else {
                int[] rgbValues = colorEnum.getRgbValues();
                spinnerTv.setTextColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));
                unhideNextColorSpinner(parent);
                matchColorSpinnerToSwatch();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener mColorSwatchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView colorSwatchTv = (TextView) v;
            String currentLevelString = colorSwatchTv.getText().toString();
            EGCPowerLevelEnum currentLevel = GCUtil.getPowerLevelEnum(currentLevelString);
            syncSpinnerAndSwatch(colorSwatchTv, currentLevel);
        }
    };

    private void syncSpinnerAndSwatch(TextView colorSwatchTv, EGCPowerLevelEnum currentLevel) {
        EGCPowerLevelEnum newLevel = null;
        if (currentLevel == EGCPowerLevelEnum.HIGH) {
            colorSwatchTv.setText("M");
            newLevel = EGCPowerLevelEnum.MEDIUM;
        } else if (currentLevel == EGCPowerLevelEnum.MEDIUM) {
            colorSwatchTv.setText("L");
            newLevel = EGCPowerLevelEnum.LOW;
        } else if (currentLevel == EGCPowerLevelEnum.LOW) {
            colorSwatchTv.setText("H");
            newLevel = EGCPowerLevelEnum.HIGH;
        }

        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            if (colorSpinnerHolder.getColorSwatchTextView() == colorSwatchTv) {
                colorSpinnerHolder.setPowerLevelEnum(newLevel);
                /*EGCColorEnum colorEnum = (EGCColorEnum) colorSpinnerHolder.getColorSpinner().getSelectedItem();
                TextView spinnerTv = (TextView) colorSpinnerHolder.getColorSpinner().findViewById(R.id.spinner_color_item_title);
                int[] rgbValues = GCUtil.convertRgbToPowerLevel(colorEnum.getRgbValues(), colorSpinnerHolder.getPowerLevelEnum());
                spinnerTv.setTextColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));*/
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
        EGCColorEnum[] colors = EGCColorEnum.values();
        for (int i = position + 1; i < mColorSpinnerHolders.size(); i++) {
            int colorIndex = 0;
            for (EGCColorEnum colorItem : colors) {
                if (colorItem == EGCColorEnum.NONE) {
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

    private ArrayList<GCColor> getNewColorList() {
        EGCColorEnum[] colors = EGCColorEnum.values();
        ArrayList<GCColor> newColorList = new ArrayList<>();
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            int colorPosition = colorSpinnerHolder.getColorSpinner().getSelectedItemPosition();
            EGCColorEnum color = colors[colorPosition];
            EGCPowerLevelEnum power = colorSpinnerHolder.getPowerLevelEnum();
            GCColor newColor = new GCColor(color, power);
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

        ArrayList<GCColor> newColorList = getNewColorList();

        EGCModeEnum[] modes = EGCModeEnum.values();
        int modePosition = mModeSpinner.getSelectedItemPosition();
        EGCModeEnum newMode = modes[modePosition];

        mNewSet = new GCSavedSet();
        mNewSet.setTitle(newTitle);
        mNewSet.setColors(newColorList);
        mNewSet.setMode(newMode);

        if (mListener != null) {
            if (isNewSet) {
                mListener.onSetAdded(mNewSet);
            } else {
                mListener.onSetSaved(mSavedSet, mNewSet);
            }
        }
    }

    private boolean validateTitleAgainstDatabase(String title) {
        GCSavedSetDatabase mSavedSetDatabase = new GCSavedSetDatabase(mContext);
        ArrayList<GCSavedSet> savedSetList = mSavedSetDatabase.readData();
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
                .setIcon(android.R.drawable.ic_menu_save)
                .show();
    }

    private void showResetDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.revert_changes))
                .setMessage(mContext.getString(R.string.revert_changes_dialog))
                .setPositiveButton(mContext.getString(R.string.reset), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mSavedSet == null) {
                            fillDefaultData();
                        } else {
                            fillOutData();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_menu_revert)
                .show();
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.delete))
                .setMessage(mContext.getString(R.string.delete_dialog))
                .setPositiveButton(mContext.getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onSetDeleted(mSavedSet, isNewSet);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_menu_delete)
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
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void showLeavingDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.unsaved_changes))
                .setMessage(mContext.getString(R.string.unsaved_changes_dialog))
                .setPositiveButton(mContext.getString(R.string.exit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onLeaveConfirmed();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void showShareDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(mContext.getString(R.string.share));
        alert.setMessage(mContext.getString(R.string.share_dialog));

        TextView input = new TextView(mContext);
        input.setTextIsSelectable(true);
        final String shareString = getShareString();
        input.setText(shareString);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        alert.setView(input);

        alert.setPositiveButton(mContext.getString(R.string.copy), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", shareString);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private String getShareString() {
        String shareString = "";
        String breakCharacter = GCUtil.BREAK_CHARACTER_FOR_SHARING;

        //Get title
        shareString += mTitleEditText.getText().toString().trim();
        shareString += breakCharacter;

        //Get colors
        ArrayList<GCColor> newColorList = getNewColorList();
        shareString += GCUtil.convertColorListToShortenedColorString(newColorList);
        shareString += breakCharacter;

        //Get mode
        EGCModeEnum[] modes = EGCModeEnum.values();
        int modePosition = mModeSpinner.getSelectedItemPosition();
        EGCModeEnum modeEnum = modes[modePosition];
        shareString += modeEnum.toString();

        return shareString;
    }

    public boolean madeChanges() {
        if (mSavedSet == null) {
            return true;
        }

        if (!mTitleEditText.getText().toString().trim().equals(mSavedSet.getTitle())) {
            return true;
        }

        EGCColorEnum[] colors = EGCColorEnum.values();
        int index = 0;
        for (ColorSpinnerHolder colorSpinnerHolder : mColorSpinnerHolders) {
            int colorPosition = colorSpinnerHolder.getColorSpinner().getSelectedItemPosition();
            if (mSavedSet.getColors().size() > index) {
                if (colors[colorPosition] != mSavedSet.getColors().get(index).getColorEnum()
                        || colorSpinnerHolder.getPowerLevelEnum() != mSavedSet.getColors().get(index).getPowerLevelEnum()) {
                    return true;
                }
            } else if (colors[colorPosition] != EGCColorEnum.NONE) {
                return true;
            }
            index++;
        }

        EGCModeEnum[] modes = EGCModeEnum.values();
        int modePosition = mModeSpinner.getSelectedItemPosition();
        if (modes[modePosition] != mSavedSet.getMode()) {
            return true;
        }

        return false;
    }
}

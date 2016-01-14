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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.database.GCSavedSetDatabase;
import com.achanr.glovercolorapp.listeners.IGCEditSavedSetFragmentListener;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.utility.EGCColorEnum;
import com.achanr.glovercolorapp.utility.EGCModeEnum;
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
    private ColorSpinnerHolder mColor1Spinner;
    private ColorSpinnerHolder mColor2Spinner;
    private ColorSpinnerHolder mColor3Spinner;
    private ColorSpinnerHolder mColor4Spinner;
    private ColorSpinnerHolder mColor5Spinner;
    private ColorSpinnerHolder mColor6Spinner;
    private Spinner mModeSpinner;
    private Button mSaveButton;

    private boolean isNewSet = false;

    private static final String SAVED_SET_KEY = "saved_set_key";
    private static final String NEW_SET_KEY = "new_set_key";
    private final int COLOR_SPINNER_SIZE = 6;

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
        private Spinner mColorSpinner;
        private View mColorSwatch;

        public ColorSpinnerHolder(Spinner colorSpinner, View colorSwatch) {
            mColorSpinner = colorSpinner;
            mColorSwatch = colorSwatch;
        }

        public Spinner getColorSpinner() {
            return mColorSpinner;
        }

        public void setColorSpinner(Spinner colorSpinner) {
            mColorSpinner = colorSpinner;
        }

        public View getColorSwatch() {
            return mColorSwatch;
        }

        public void setColorSwatch(View colorSwatch) {
            mColorSwatch = colorSwatch;
        }
    }

    public GCEditSavedSetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SavedSetListFragment.
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
        mModeSpinner = (Spinner) v.findViewById(R.id.edit_text_mode);

        fillSpinnerWithEnums(mColor1Spinner.getColorSpinner(), EGCColorEnum.values());
        fillSpinnerWithEnums(mColor2Spinner.getColorSpinner(), EGCColorEnum.values());
        fillSpinnerWithEnums(mColor3Spinner.getColorSpinner(), EGCColorEnum.values());
        fillSpinnerWithEnums(mColor4Spinner.getColorSpinner(), EGCColorEnum.values());
        fillSpinnerWithEnums(mColor5Spinner.getColorSpinner(), EGCColorEnum.values());
        fillSpinnerWithEnums(mColor6Spinner.getColorSpinner(), EGCColorEnum.values());
        fillSpinnerWithEnums(mModeSpinner, EGCModeEnum.values());

        if (mSavedSet != null) {
            fillOutData();
        } else {
            fillDefaultData();
        }
        setupColorSpinnerSelectionListeners();
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mContext = null;
        mSavedSet = null;
        mTitleEditText = null;
        mColor1Spinner = null;
        mColor2Spinner = null;
        mColor3Spinner = null;
        mColor4Spinner = null;
        mColor5Spinner = null;
        mColor6Spinner = null;
        mModeSpinner = null;
        mSaveButton = null;
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

        int blankCount = 0;
        EGCColorEnum[] colors = EGCColorEnum.values();
        for (int i = 0; i < COLOR_SPINNER_SIZE; i++) {
            int colorPosition = getColorSpinnerHolder(i + 1).getColorSpinner().getSelectedItemPosition();
            if (colors[colorPosition] == EGCColorEnum.BLANK) {
                blankCount++;
            }
        }
        if (blankCount >= 6) {
            showErrorDialog(mContext.getString(R.string.error_no_color));
            return false;
        }

        return true;
    }

    private void setupColorSpinnerHolders(View v) {
        mColor1Spinner = new ColorSpinnerHolder((Spinner) v.findViewById(R.id.spinner_color_1), v.findViewById(R.id.color_swatch_1));
        mColor2Spinner = new ColorSpinnerHolder((Spinner) v.findViewById(R.id.spinner_color_2), v.findViewById(R.id.color_swatch_2));
        mColor3Spinner = new ColorSpinnerHolder((Spinner) v.findViewById(R.id.spinner_color_3), v.findViewById(R.id.color_swatch_3));
        mColor4Spinner = new ColorSpinnerHolder((Spinner) v.findViewById(R.id.spinner_color_4), v.findViewById(R.id.color_swatch_4));
        mColor5Spinner = new ColorSpinnerHolder((Spinner) v.findViewById(R.id.spinner_color_5), v.findViewById(R.id.color_swatch_5));
        mColor6Spinner = new ColorSpinnerHolder((Spinner) v.findViewById(R.id.spinner_color_6), v.findViewById(R.id.color_swatch_6));
    }

    private void matchColorSpinnerToSwatch() {
        for(int i = 0; i < COLOR_SPINNER_SIZE; i++) {
            EGCColorEnum colorEnum = (EGCColorEnum) getColorSpinnerHolder(i + 1).getColorSpinner().getSelectedItem();
            int[] rgbValues = colorEnum.getRgbValues();
            getColorSpinnerHolder(i + 1).getColorSwatch().setBackgroundColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));
        }
    }

    private void fillSpinnerWithEnums(Spinner spinner, Object[] values) {
        spinner.setAdapter(new ArrayAdapter<>(mContext, R.layout.spinner_color_item, values));
    }

    private void fillOutData() {
        String title = mSavedSet.getTitle();
        ArrayList<EGCColorEnum> colorList = mSavedSet.getColors();
        EGCModeEnum mode = mSavedSet.getMode();

        mTitleEditText.setText(title);

        int spinnerIndex = 0;
        EGCColorEnum[] colors = EGCColorEnum.values();
        for (EGCColorEnum color : colorList) {
            spinnerIndex++;
            int colorIndex = 0;
            for (EGCColorEnum colorItem : colors) {
                if (colorItem == color) {
                    getColorSpinnerHolder(spinnerIndex).getColorSpinner().setSelection(colorIndex);
                    break;
                } else {
                    colorIndex++;
                }
            }
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
        for (int i = 0; i < COLOR_SPINNER_SIZE; i++) {
            int colorIndex = 0;
            for (EGCColorEnum colorItem : colors) {
                if (colorItem == EGCColorEnum.BLANK) {
                    getColorSpinnerHolder(i + 1).getColorSpinner().setSelection(colorIndex);
                    break;
                } else {
                    colorIndex++;
                }
            }
        }

        mModeSpinner.setSelection(0);
    }

    private void setupColorSpinnerSelectionListeners() {
        for (int i = 0; i < COLOR_SPINNER_SIZE; i++) {
            getColorSpinnerHolder(i + 1).getColorSpinner().setOnItemSelectedListener(mColorSpinnerSelectedListener);
        }
    }

    private AdapterView.OnItemSelectedListener mColorSpinnerSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            TextView spinnerTv = ((TextView) view.findViewById(R.id.spinner_color_item_title));
            String colorString = spinnerTv.getText().toString();
            EGCColorEnum colorEnum = EGCColorEnum.valueOf(colorString);
            int[] rgbValues = colorEnum.getRgbValues();
            spinnerTv.setTextColor(Color.argb(255, rgbValues[0], rgbValues[1], rgbValues[2]));
            matchColorSpinnerToSwatch();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private ColorSpinnerHolder getColorSpinnerHolder(int position) {
        switch (position) {
            case 1:
                return mColor1Spinner;
            case 2:
                return mColor2Spinner;
            case 3:
                return mColor3Spinner;
            case 4:
                return mColor4Spinner;
            case 5:
                return mColor5Spinner;
            case 6:
                return mColor6Spinner;
            default:
                return null;
        }
    }

    private ArrayList<EGCColorEnum> getNewColorList() {
        EGCColorEnum[] colors = EGCColorEnum.values();
        ArrayList<EGCColorEnum> newColorList = new ArrayList<>();
        for (int i = 0; i < COLOR_SPINNER_SIZE; i++) {
            int colorPosition = getColorSpinnerHolder(i + 1).getColorSpinner().getSelectedItemPosition();
            newColorList.add(colors[colorPosition]);
        }
        return newColorList;
    }

    private void saveSet() {
        String newTitle = mTitleEditText.getText().toString().trim();
        if (validateTitleAgainstDatabase(newTitle) && isNewSet) {
            showErrorDialog(mContext.getString(R.string.error_title_exists));
            return;
        }

        ArrayList<EGCColorEnum> newColorList = getNewColorList();

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
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
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
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
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
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
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
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
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
        ArrayList<EGCColorEnum> newColorList = getNewColorList();
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
        for (int i = 0; i < COLOR_SPINNER_SIZE; i++) {
            int colorPosition = getColorSpinnerHolder(i + 1).getColorSpinner().getSelectedItemPosition();
            if (colors[colorPosition] != mSavedSet.getColors().get(i)) {
                return true;
            }
        }

        EGCModeEnum[] modes = EGCModeEnum.values();
        int modePosition = mModeSpinner.getSelectedItemPosition();
        if (modes[modePosition] != mSavedSet.getMode()) {
            return true;
        }

        return false;
    }
}

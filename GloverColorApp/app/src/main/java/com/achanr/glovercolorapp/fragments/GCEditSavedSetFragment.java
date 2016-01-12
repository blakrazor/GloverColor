package com.achanr.glovercolorapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.listeners.IGCEditSavedSetFragmentListener;
import com.achanr.glovercolorapp.models.GCSavedSetDataModel;
import com.achanr.glovercolorapp.utility.EGCColorEnum;
import com.achanr.glovercolorapp.utility.EGCModeEnum;

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
    private GCSavedSetDataModel mSavedSet;
    private GCSavedSetDataModel mNewSet;
    private EditText mTitleEditText;
    private Spinner mColor1Spinner;
    private Spinner mColor2Spinner;
    private Spinner mColor3Spinner;
    private Spinner mColor4Spinner;
    private Spinner mColor5Spinner;
    private Spinner mColor6Spinner;
    private Spinner mModeSpinner;
    private Button mSaveButton;

    private boolean isNewSet = false;

    private static final String SAVED_SET_KEY = "saved_set_key";
    private static final String NEW_SET_KEY = "new_set_key";
    private int colorSpinnerSize = 6;

    public GCEditSavedSetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SavedSetListFragment.
     */
    public static GCEditSavedSetFragment newInstance(GCSavedSetDataModel savedSet, boolean isNewSet) {
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
            mSavedSet = (GCSavedSetDataModel) getArguments().getSerializable(SAVED_SET_KEY);
            isNewSet = getArguments().getBoolean(NEW_SET_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_saved_set, container, false);
        mTitleEditText = (EditText) v.findViewById(R.id.edit_text_title);
        mColor1Spinner = (Spinner) v.findViewById(R.id.edit_text_color_1);
        mColor2Spinner = (Spinner) v.findViewById(R.id.edit_text_color_2);
        mColor3Spinner = (Spinner) v.findViewById(R.id.edit_text_color_3);
        mColor4Spinner = (Spinner) v.findViewById(R.id.edit_text_color_4);
        mColor5Spinner = (Spinner) v.findViewById(R.id.edit_text_color_5);
        mColor6Spinner = (Spinner) v.findViewById(R.id.edit_text_color_6);
        mModeSpinner = (Spinner) v.findViewById(R.id.edit_text_mode);

        fillSpinnerWithEnums(mColor1Spinner, EGCColorEnum.values());
        fillSpinnerWithEnums(mColor2Spinner, EGCColorEnum.values());
        fillSpinnerWithEnums(mColor3Spinner, EGCColorEnum.values());
        fillSpinnerWithEnums(mColor4Spinner, EGCColorEnum.values());
        fillSpinnerWithEnums(mColor5Spinner, EGCColorEnum.values());
        fillSpinnerWithEnums(mColor6Spinner, EGCColorEnum.values());
        fillSpinnerWithEnums(mModeSpinner, EGCModeEnum.values());

        if (mSavedSet != null) {
            fillOutData();
        } else {
            fillDefaultData();
        }
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
        menu.add(0, 1, 1, "Save").setIcon(android.R.drawable.ic_menu_save)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 2, 2, "Delete").setIcon(android.R.drawable.ic_menu_delete)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: //Save
                if (validateFields()) {
                    if (isNewSet) {
                        showSaveDialog("Add New Set", "Add new set?");
                    } else {
                        showSaveDialog("Save Changes", "Save your changes?");
                    }
                }
                return true;
            case 2: //Delete
                showDeleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean validateFields() {
        String newTitle = mTitleEditText.getText().toString().trim();
        if (newTitle.isEmpty()) {
            showErrorDialog("Title cannot be empty.");
            return false;
        } else if (newTitle.length() > 100) {
            showErrorDialog("Title must be less than 20 characters.");
            return false;
        }

        int blankCount = 0;
        EGCColorEnum[] colors = EGCColorEnum.values();
        ArrayList<EGCColorEnum> newColorList = new ArrayList<>();
        for (int i = 0; i < colorSpinnerSize; i++) {
            int colorPosition = getColorSpinner(i + 1).getSelectedItemPosition();
            if (colors[colorPosition] == EGCColorEnum.BLANK) {
                blankCount++;
            }
        }
        if (blankCount >= 6) {
            showErrorDialog("You must choose at least 1 color.");
            return false;
        }

        return true;
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
                    getColorSpinner(spinnerIndex).setSelection(colorIndex);
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
        EGCColorEnum[] colors = EGCColorEnum.values();
        for (int i = 0; i < colorSpinnerSize; i++) {
            int colorIndex = 0;
            for (EGCColorEnum colorItem : colors) {
                if (colorItem == EGCColorEnum.BLANK) {
                    getColorSpinner(i + 1).setSelection(colorIndex);
                    break;
                } else {
                    colorIndex++;
                }
            }
        }
    }

    private Spinner getColorSpinner(int position) {
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

    private void saveSet() {
        String newTitle = mTitleEditText.getText().toString().trim();

        EGCColorEnum[] colors = EGCColorEnum.values();
        ArrayList<EGCColorEnum> newColorList = new ArrayList<>();
        for (int i = 0; i < colorSpinnerSize; i++) {
            int colorPosition = getColorSpinner(i + 1).getSelectedItemPosition();
            newColorList.add(colors[colorPosition]);
        }

        EGCModeEnum[] modes = EGCModeEnum.values();
        int modePosition = mModeSpinner.getSelectedItemPosition();
        EGCModeEnum newMode = modes[modePosition];

        mNewSet = new GCSavedSetDataModel();
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

    private void showDeleteDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("Delete")
                .setMessage("Delete this set?")
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
                .setTitle("Error")
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
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. Are you sure you want to leave?")
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

    public boolean madeChanges() {
        if (mSavedSet == null) {
            return true;
        }

        if (!mTitleEditText.getText().toString().trim().equals(mSavedSet.getTitle())) {
            return true;
        }

        EGCColorEnum[] colors = EGCColorEnum.values();
        for (int i = 0; i < colorSpinnerSize; i++) {
            int colorPosition = getColorSpinner(i + 1).getSelectedItemPosition();
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

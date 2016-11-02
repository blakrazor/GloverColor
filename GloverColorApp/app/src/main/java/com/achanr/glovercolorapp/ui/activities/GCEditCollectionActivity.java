package com.achanr.glovercolorapp.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Pair;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.CustomItemAnimator;
import com.achanr.glovercolorapp.common.GCOnlineDatabaseUtil;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCCollection;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.adapters.GCSavedSetListAdapter;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 4/29/16 3:22 PM
 */
public class GCEditCollectionActivity extends GCBaseActivity {

    private TextView mTitleEditText;
    private TextView mDescEditText;
    private GCCollection mCollection;
    private Button mAddSetButton;
    private GridRecyclerView mSetsListRecyclerView;
    private GCSavedSetListAdapter mSetsListListAdapter;
    private ArrayList<GCSavedSet> mSetsList;
    private boolean enterFinished = false;
    private boolean isNewSet = false;
    private boolean madeChanges = false;
    private boolean wasChangeDialogCanceled = false;

    public static final String SAVED_COLLECTION_KEY = "SAVED_COLLECTION_KEY";
    public static final String IS_NEW_COLLECTION_KEY = "IS_NEW_COLLECTION_KEY";
    public static final String ADD_SET_KEY = "add_set_key";
    public static final String IS_REMOVE_KEY = "is_delete_key";
    private static final String NEW_SET_KEY = "new_set_key";
    public static final String OLD_SET_KEY = "old_set_key";
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_DESC_LENGTH = 500;
    private static final int COLLECTIONS_ADD_SET_RESULT = 1000;
    private static final int UPDATE_SET_REQUEST_CODE = 1001;

    private final InputFilter titleFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence arg0, int arg1, int arg2, Spanned arg3, int arg4, int arg5) {
            for (int k = arg1; k < arg2; k++) {
                if (!Character.isLetterOrDigit(arg0.charAt(k))
                        && !Character.isSpaceChar(arg0.charAt(k))) {
                    return "";
                }
            }
            return null;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COLLECTIONS_ADD_SET_RESULT) {
            if (resultCode == RESULT_OK) {
                GCSavedSet addedSet = (GCSavedSet) data.getSerializableExtra(ADD_SET_KEY);
                mSetsListListAdapter.add(mSetsList.size(), addedSet);
                mSetsList.add(addedSet);
            }
        } else if (requestCode == UPDATE_SET_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                boolean isRemoved = data.getBooleanExtra(IS_REMOVE_KEY, false);
                if (isRemoved) {
                    GCSavedSet savedSet = (GCSavedSet) data.getSerializableExtra(OLD_SET_KEY);
                    onSetRemoved(savedSet);
                } else {
                    GCSavedSet oldSet = (GCSavedSet) data.getSerializableExtra(OLD_SET_KEY);
                    GCSavedSet newSet = (GCSavedSet) data.getSerializableExtra(NEW_SET_KEY);
                    onSetUpdated(oldSet, newSet);
                }
            }
        }
        checkForChanges();
    }

    @Override
    protected void onStop() {
        super.onStop();
        View view = this.getCurrentFocus();
        if (view != null) {
            GCUtil.hideKeyboard(GCEditCollectionActivity.this, view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_edit_collection, mFrameLayout);
        setupToolbar(getString(R.string.title_edit_collection));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTitleEditText = (EditText) findViewById(R.id.edit_text_title);
        mDescEditText = (EditText) findViewById(R.id.edit_text_description);
        mAddSetButton = (Button) findViewById(R.id.add_set_button);
        mSetsListRecyclerView = (GridRecyclerView) findViewById(R.id.collection_sets_list);
        mTitleEditText.setFilters(new InputFilter[]{titleFilter});

        Intent intent = getIntent();
        if (intent != null) {
            mCollection = (GCCollection) intent.getSerializableExtra(SAVED_COLLECTION_KEY);
            isNewSet = intent.getBooleanExtra(IS_NEW_COLLECTION_KEY, false);
            if (isNewSet) {
                setCustomTitle(getString(R.string.add_collection_title));
            }
        }

        if (mCollection != null) {
            fillExistingData();
        } else {
            fillDefaultData();
        }

        setupSetsList();
        setupListeners();
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
                        showSaveDialog(getString(R.string.add_new_set), getString(R.string.add_new_set_dialog));
                    } else {
                        showSaveDialog(getString(R.string.save_changes), getString(R.string.save_changes_dialog));
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

    @Override
    public void onBackPressed() {
        if (madeChanges()) {
            showLeavingDialog();
        } else {
            finishActivityTransition(RESULT_CANCELED, null);
        }
    }

    private void fillDefaultData() {
        mTitleEditText.setText("");
        mDescEditText.setText("");
        mSetsList = new ArrayList<>();
    }

    private void fillExistingData() {
        String title = mCollection.getTitle();
        String description = mCollection.getDescription();
        mSetsList = new ArrayList<>(mCollection.getSavedSetList());
        mTitleEditText.setText(title);
        mDescEditText.setText(description);
    }

    private void setupSetsList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mSetsListRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        GridLayoutManager mSetsListLayoutManager = new GridLayoutManager(this, 1);
        mSetsListRecyclerView.setLayoutManager(mSetsListLayoutManager);
        mSetsListRecyclerView.setItemAnimator(new CustomItemAnimator());
        mSetsListListAdapter = new GCSavedSetListAdapter(this, mSetsList);
        mSetsListListAdapter.sortList();
        mSetsListRecyclerView.setAdapter(mSetsListListAdapter);
    }

    private void setupListeners() {
        TextWatcher textWatcher = new TextWatcher() {
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
        };

        TextView.OnEditorActionListener onEditActionlistener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    GCUtil.hideKeyboard(GCEditCollectionActivity.this, v);
                    return true;
                }
                return false;
            }
        };

        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    GCUtil.hideKeyboard(GCEditCollectionActivity.this, v);
                }
            }
        };

        mAddSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSavedSetList();
            }
        });

        mTitleEditText.addTextChangedListener(textWatcher);
        mTitleEditText.setOnEditorActionListener(onEditActionlistener);
        mTitleEditText.setOnFocusChangeListener(onFocusChangeListener);
        mDescEditText.addTextChangedListener(textWatcher);
        mDescEditText.setOnEditorActionListener(onEditActionlistener);
        mDescEditText.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void navigateToSavedSetList() {
        setPosition(R.id.nav_saved_color_sets);
        Intent intent = new Intent(this, GCSavedSetListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(GCSavedSetListActivity.FROM_NAVIGATION, GCEditCollectionActivity.class.getName());
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
            @SuppressWarnings("unchecked") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivityForResult(intent, COLLECTIONS_ADD_SET_RESULT, options.toBundle());
        } else {
            // Implement this feature without material design
            startActivityForResult(intent, COLLECTIONS_ADD_SET_RESULT);
        }
    }

    private boolean validateFields() {
        String newTitle = mTitleEditText.getText().toString().trim();
        if (newTitle.isEmpty()) {
            showErrorDialog(getString(R.string.error_title_empty));
            return false;
        } else if (newTitle.length() > MAX_TITLE_LENGTH) {
            showErrorDialog(String.format(getString(R.string.error_title_length), MAX_TITLE_LENGTH));
            return false;
        }

        String newDesc = mDescEditText.getText().toString().trim();
        if (newDesc.length() > MAX_DESC_LENGTH) {
            showErrorDialog(String.format(getString(R.string.error_desc_length), MAX_DESC_LENGTH));
            return false;
        }

        if (mSetsList.size() == 0) {
            showErrorDialog(getString(R.string.add_at_least_1_set));
            return false;
        }

        return true;
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

    private boolean madeChanges() {
        if (mCollection == null) {
            return true;
        }

        if (!mTitleEditText.getText().toString().trim().equals(mCollection.getTitle())) {
            return true;
        }

        if (!mDescEditText.getText().toString().trim().equals(mCollection.getDescription())) {
            return true;
        }

        //noinspection RedundantIfStatement made for readability
        if (mCollection.getSavedSetList().size() != mSetsList.size()) {
            return true;
        }

        return false;
    }

    private void saveCollection() {
        String newTitle = mTitleEditText.getText().toString().trim();
        if (validateTitleAgainstDatabase(newTitle) && isNewSet) {
            showErrorDialog(getString(R.string.error_title_exists));
            return;
        }

        String description = mDescEditText.getText().toString().trim();

        GCCollection newCollection = new GCCollection();
        newCollection.setTitle(newTitle);
        newCollection.setDescription(description);
        newCollection.setSavedSetList(new ArrayList<>(mSetsList));

        if (isNewSet) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(GCCollectionsActivity.NEW_COLLECTION_KEY, newCollection);
            finishActivityTransition(RESULT_OK, resultIntent);
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(GCCollectionsActivity.OLD_COLLECTION_KEY, mCollection);
            resultIntent.putExtra(GCCollectionsActivity.NEW_COLLECTION_KEY, newCollection);
            finishActivityTransition(RESULT_OK, resultIntent);
        }
    }

    private boolean validateTitleAgainstDatabase(String title) {
        ArrayList<GCCollection> collectionList = GCDatabaseHelper.getInstance(this).COLLECTION_DATABASE.getAllData();
        for (GCCollection collection : collectionList) {
            if (collection.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }

    private void showSaveDialog(String title, String body) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(body)
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveCollection();
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

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete))
                .setMessage(getString(R.string.delete_collection))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(GCCollectionsActivity.OLD_COLLECTION_KEY, mCollection);
                        returnIntent.putExtra(GCCollectionsActivity.IS_DELETE_KEY, true);
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
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.error))
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_48dp)
                .show();
    }

    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.revert_changes))
                .setMessage(getString(R.string.revert_changes_dialog))
                .setPositiveButton(getString(R.string.reset), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        wasChangeDialogCanceled = true;
                        if (mCollection == null) {
                            fillDefaultData();
                        } else {
                            fillExistingData();
                        }
                        setupSetsList();
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

    private void showLeavingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.unsaved_changes));
        builder.setMessage(getString(R.string.unsaved_changes_dialog));
        builder.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finishActivityTransition(RESULT_CANCELED, null);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        if (!isNewSet) {
            builder.setNeutralButton("Save and Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveCollection();
                }
            });
        }
        builder.setIcon(R.drawable.ic_warning_black_48dp);
        builder.show();
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

    public void onEditSetListItemClicked(GCSavedSet savedSet, HashMap<String, View> transitionViews) {
        Intent intent = new Intent(this, GCEditSavedSetActivity.class);
        intent.putExtra(GCEditSavedSetActivity.SAVED_SET_KEY, savedSet);
        intent.putExtra(GCEditSavedSetActivity.FROM_NAVIGATION, GCEditCollectionActivity.class.getName());
        startEditSetActivityTransition(intent, transitionViews);
    }

    private void startEditSetActivityTransition(Intent intent, HashMap<String, View> transitionView) {
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            getWindow().setExitTransition(new Fade());
            ArrayList<Pair> pairArrayList = new ArrayList<>();
            for (Map.Entry<String, View> entry : transitionView.entrySet()) {
                String key = entry.getKey();
                View value = entry.getValue();
                pairArrayList.add(Pair.create(value, key));
            }
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    pairArrayList.get(0),
                    pairArrayList.get(1),
                    pairArrayList.get(2),
                    pairArrayList.get(3),
                    pairArrayList.get(4));
            startActivityForResult(intent, UPDATE_SET_REQUEST_CODE, options.toBundle());
        } else {
            // Implement this feature without material design
            startActivityForResult(intent, UPDATE_SET_REQUEST_CODE);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    private void onSetUpdated(GCSavedSet oldSet, GCSavedSet newSet) {
        GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.updateData(oldSet, newSet);
        mSetsListListAdapter.update(oldSet, newSet);
        int counter = 0;
        for (GCSavedSet savedSet : mSetsList) {
            if (savedSet.getId() == oldSet.getId()) {
                mSetsList.set(counter, newSet);
            }
            counter++;
        }
        Toast.makeText(this, getString(R.string.set_updated_message), Toast.LENGTH_SHORT).show();
        GCOnlineDatabaseUtil.updateToOnlineDB(this, newSet);
    }

    public void onSetRemoved(GCSavedSet savedSet) {
        mSetsListListAdapter.remove(savedSet);
        int position = mSetsList.indexOf(savedSet);
        mSetsList.remove(position);
        Toast.makeText(this, R.string.removed_set_from_collection, Toast.LENGTH_SHORT).show();
    }
}

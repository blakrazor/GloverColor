package com.achanr.glovercolorapp.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.transition.Fade;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.GCConstants;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.adapters.GCSavedSetListAdapter;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public class GCSavedSetListActivity extends GCBaseActivity {

    private enum SortEnum {
        TITLE_ASC("Sort by title ascending"),
        TITLE_DESC("Sort by title descending"),
        CHIP_ASC("Sort by chip ascending"),
        CHIP_DESC("Sort by chip descending");

        String description;

        SortEnum(String desc) {
            description = desc;
        }

        public String getDescription() {
            return description;
        }
    }

    private Context mContext;
    private ArrayList<GCSavedSet> mSavedSetList;
    private GridRecyclerView mSavedSetListRecyclerView;
    private GCSavedSetListAdapter mSavedSetListAdapter;
    private GridLayoutManager mSavedSetListLayoutManager;
    private FloatingActionButton mFab;
    private boolean isFromEditing = false;
    private boolean isLeaving = false;
    private boolean isFromEnterCode = false;

    public static final String FROM_NAVIGATION = "from_navigation";
    public static final String NEW_SET_KEY = "new_set_key";
    public static final String OLD_SET_KEY = "old_set_key";
    public static final String IS_DELETE_KEY = "is_delete_key";

    public static final int ADD_NEW_SET_REQUEST_CODE = 1001;
    public static final int UPDATE_SET_REQUEST_CODE = 1002;

    public interface AnimationCompleteListener {
        void onComplete();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isLeaving = false;
        if (requestCode == ADD_NEW_SET_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean isDeleted = data.getBooleanExtra(IS_DELETE_KEY, false);
            if (!isDeleted) {
                GCSavedSet newSet = (GCSavedSet) data.getSerializableExtra(NEW_SET_KEY);
                onSetAdded(newSet);
            }
        } else if (requestCode == UPDATE_SET_REQUEST_CODE) {
            isFromEditing = true;
            if (resultCode == RESULT_OK && data != null) {
                boolean isDeleted = data.getBooleanExtra(IS_DELETE_KEY, false);
                if (isDeleted) {
                    GCSavedSet savedSet = (GCSavedSet) data.getSerializableExtra(OLD_SET_KEY);
                    onSetDeleted(savedSet);
                } else {
                    GCSavedSet oldSet = (GCSavedSet) data.getSerializableExtra(OLD_SET_KEY);
                    GCSavedSet newSet = (GCSavedSet) data.getSerializableExtra(NEW_SET_KEY);
                    onSetUpdated(oldSet, newSet);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_saved_set_list, mFrameLayout);
        mContext = this;
        setupToolbar(getString(R.string.title_your_saved_sets));

        getSavedSetListFromDatabase();

        /*if (mSavedSetList != null && mSavedSetList.size() > 0) {
            findViewById(R.id.icon_background).setVisibility(View.GONE);
        }*/

        mSavedSetListRecyclerView = (GridRecyclerView) findViewById(R.id.saved_set_list_recyclerview);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromEditing = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animateFab(false, new AnimationCompleteListener() {
                        @Override
                        public void onComplete() {
                            animateListView(false, new AnimationCompleteListener() {
                                @Override
                                public void onComplete() {
                                    onAddSetListItemClicked();
                                }
                            });
                        }
                    });
                } else {
                    onAddSetListItemClicked();
                }
            }
        });

        setupSavedSetList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSavedSetListRecyclerView.setVisibility(View.INVISIBLE);
            mFab.setBackground(getDrawable(R.drawable.fab_ripple));
        } else {
            mFab.setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();
        if (intent != null) {
            String fromNavigation = intent.getStringExtra(FROM_NAVIGATION);
            if (fromNavigation != null) {
                if (fromNavigation.equalsIgnoreCase(GCEnterCodeActivity.class.getName())
                        || fromNavigation.equalsIgnoreCase(GCHomeActivity.class.getName())) {
                    isFromEnterCode = true;
                    GCSavedSet newSet = (GCSavedSet) intent.getSerializableExtra(NEW_SET_KEY);
                    Intent newIntent = new Intent(mContext, GCEditSavedSetActivity.class);
                    newIntent.putExtra(GCEditSavedSetActivity.IS_NEW_SET_KEY, true);
                    newIntent.putExtra(GCEditSavedSetActivity.SAVED_SET_KEY, newSet);
                    startActivityForResult(newIntent, ADD_NEW_SET_REQUEST_CODE);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }
            }

            if (intent.getBooleanExtra(GCUtil.WAS_REFRESHED, false)) {
                mFab.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_saved_color_sets);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (prefs.getBoolean(GCConstants.WAS_POWER_LEVELS_CHANGED_KEY, false)) {
            setupSavedSetList();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(GCConstants.WAS_POWER_LEVELS_CHANGED_KEY, false);
            editor.apply();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mSavedSetListRecyclerView.getVisibility() != View.VISIBLE) {
                animateListView(true, new AnimationCompleteListener() {
                    @Override
                    public void onComplete() {
                        animateFab(true, null);
                    }
                });
            } else if (mFab.getVisibility() != View.VISIBLE
                    && !isFromEnterCode) {
                animateFab(true, null);
            } else {
                isFromEnterCode = false;
            }
        } else {
            mFab.setVisibility(View.VISIBLE);
            mSavedSetListRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "Sort").setIcon(R.drawable.ic_sort_white_48dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: //revert changes
                showSortDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortDialog() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int sortTypeInt = prefs.getInt(GCConstants.SORTING_KEY, 0);

        SortEnum[] sortEnums = SortEnum.values();
        String[] sortDescs = new String[sortEnums.length];

        for (int i = 0; i < sortEnums.length; i++) {
            sortDescs[i] = sortEnums[i].getDescription();
            if (i == sortTypeInt) {
                sortDescs[i] += " (current)";
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose sort option");
        builder.setItems(sortDescs, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(GCConstants.SORTING_KEY, item);
                editor.apply();
                sortList(mSavedSetList);
                mSavedSetListAdapter.notifyDataSetChanged();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sortList(ArrayList<GCSavedSet> unsortedList) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        int sortTypeInt = prefs.getInt(GCConstants.SORTING_KEY, 0);
        SortEnum sortType = SortEnum.values()[sortTypeInt];

        switch (sortType) {
            case TITLE_DESC:
                Collections.sort(unsortedList, new Comparator<GCSavedSet>() {
                    @Override
                    public int compare(GCSavedSet lhs, GCSavedSet rhs) {
                        return rhs.getTitle().compareToIgnoreCase(lhs.getTitle());
                    }
                });
                break;
            case TITLE_ASC:
                Collections.sort(unsortedList, new Comparator<GCSavedSet>() {
                    @Override
                    public int compare(GCSavedSet lhs, GCSavedSet rhs) {
                        return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
                    }
                });
                break;
            case CHIP_DESC:
                Collections.sort(unsortedList, new Comparator<GCSavedSet>() {
                    @Override
                    public int compare(GCSavedSet lhs, GCSavedSet rhs) {
                        return rhs.getChipSet().getTitle().compareToIgnoreCase(lhs.getChipSet().getTitle());
                    }
                });
                break;
            case CHIP_ASC:
                Collections.sort(unsortedList, new Comparator<GCSavedSet>() {
                    @Override
                    public int compare(GCSavedSet lhs, GCSavedSet rhs) {
                        return lhs.getChipSet().getTitle().compareToIgnoreCase(rhs.getChipSet().getTitle());
                    }
                });
                break;
        }
    }

    private void getSavedSetListFromDatabase() {
        ArrayList<GCSavedSet> savedSetList = GCDatabaseHelper.SAVED_SET_DATABASE.getAllData();
        if (mSavedSetList == null || mSavedSetList.size() <= 0) {
            mSavedSetList = new ArrayList<>();
        }
        sortList(savedSetList);
        mSavedSetList = savedSetList;
    }

    private void setupSavedSetList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mSavedSetListRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mSavedSetListLayoutManager = new GridLayoutManager(mContext, 1);
        mSavedSetListRecyclerView.setLayoutManager(mSavedSetListLayoutManager);
        mSavedSetListAdapter = new GCSavedSetListAdapter(mContext, mSavedSetList);
        mSavedSetListRecyclerView.setAdapter(mSavedSetListAdapter);
    }

    public void onEditSetListItemClicked(GCSavedSet savedSet, HashMap<String, View> transitionViews) {
        isFromEditing = true;
        isLeaving = true;
        Intent intent = new Intent(mContext, GCEditSavedSetActivity.class);
        intent.putExtra(GCEditSavedSetActivity.SAVED_SET_KEY, savedSet);
        startEditSetActivityTransition(intent, transitionViews);
    }

    public void onAddSetListItemClicked() {
        isLeaving = true;
        Intent intent = new Intent(mContext, GCEditSavedSetActivity.class);
        intent.putExtra(GCEditSavedSetActivity.IS_NEW_SET_KEY, true);
        startAddSetActivityTransition(intent);
    }

    private void startAddSetActivityTransition(Intent intent) {
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            getWindow().setExitTransition(new Fade());
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivityForResult(intent, ADD_NEW_SET_REQUEST_CODE, options.toBundle());
        } else {
            // Implement this feature without material design
            startActivityForResult(intent, ADD_NEW_SET_REQUEST_CODE);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
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
            animateFab(false, null);
            startActivityForResult(intent, UPDATE_SET_REQUEST_CODE, options.toBundle());
        } else {
            // Implement this feature without material design
            startActivityForResult(intent, UPDATE_SET_REQUEST_CODE);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    public void onSetUpdated(GCSavedSet oldSet, GCSavedSet newSet) {
        GCDatabaseHelper.SAVED_SET_DATABASE.updateData(oldSet, newSet);
        getSavedSetListFromDatabase();
        mSavedSetListAdapter.update(oldSet, newSet);
        Toast.makeText(mContext, getString(R.string.set_updated_message), Toast.LENGTH_SHORT).show();
    }

    public void onSetDeleted(GCSavedSet savedSet) {
        GCDatabaseHelper.SAVED_SET_DATABASE.deleteData(savedSet);
        getSavedSetListFromDatabase();
        mSavedSetListAdapter.remove(savedSet);
        Toast.makeText(mContext, getString(R.string.set_deleted_message), Toast.LENGTH_SHORT).show();
    }

    public void onSetAdded(GCSavedSet newSet) {
        GCDatabaseHelper.SAVED_SET_DATABASE.insertData(newSet);
        getSavedSetListFromDatabase();
        mSavedSetListAdapter.add(mSavedSetList.indexOf(newSet), newSet);
        Toast.makeText(mContext, getString(R.string.set_added_message), Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void animateFab(boolean isAppearing, final AnimationCompleteListener animationCompleteListener) {
        // previously visible view
        final View myView = findViewById(R.id.fab);

        // get the center for the clipping circle
        final int cx = myView.getMeasuredWidth() / 2;
        final int cy = myView.getMeasuredHeight() / 2;


        // create the animation (the final radius is zero)
        Animator anim;
        ObjectAnimator animator;
        AnimatorSet animatorSet = new AnimatorSet();
        if (isAppearing) {
            int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
            animator = ObjectAnimator.ofFloat(myView, "rotation", 45f, 0f);
            myView.setVisibility(View.VISIBLE);
        } else {
            int initialRadius = myView.getWidth() / 2;
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);
            animator = ObjectAnimator.ofFloat(myView, "rotation", 0f, 45f);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                    if (animationCompleteListener != null) {
                        animationCompleteListener.onComplete();
                    }
                }
            });
        }

        animatorSet.playTogether(anim, animator);
        // start the animation
        animatorSet.start();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        if (!isLeaving) {
            if (!isFromEditing) {
                if (mSavedSetListRecyclerView.getVisibility() != View.VISIBLE) {
                    animateListView(true, new AnimationCompleteListener() {
                        @Override
                        public void onComplete() {
                            if (mFab.getVisibility() != View.VISIBLE) {
                                animateFab(true, null);
                            }
                        }
                    });
                }
            } else {
                isFromEditing = false;
            }
        } else {
            isLeaving = false;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void animateListView(boolean isEntering, final AnimationCompleteListener animationCompleteListener) {
        if (isEntering) {
            isLeaving = false;
            mSavedSetListRecyclerView.setVisibility(View.VISIBLE);
            mSavedSetListRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mContext, R.anim.grid_layout_in_animation));
            mSavedSetListRecyclerView.setLayoutAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (animationCompleteListener != null) {
                        animationCompleteListener.onComplete();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mSavedSetListRecyclerView.scheduleLayoutAnimation();
        } else {
            isLeaving = true;
            mSavedSetListRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mContext, R.anim.grid_layout_out_animation));
            mSavedSetListRecyclerView.setLayoutAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mSavedSetListRecyclerView.setVisibility(View.INVISIBLE);
                    if (animationCompleteListener != null) {
                        animationCompleteListener.onComplete();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mSavedSetListRecyclerView.scheduleLayoutAnimation();
            mSavedSetListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animateFab(false, new AnimationCompleteListener() {
                    @Override
                    public void onComplete() {
                        animateListView(false, new AnimationCompleteListener() {
                            @Override
                            public void onComplete() {
                                completedAnimationBackPressed();
                            }
                        });
                    }
                });
            } else {
                super.onBackPressed();
            }
        }
    }

    private void completedAnimationBackPressed() {
        super.onBackPressed();
    }
}

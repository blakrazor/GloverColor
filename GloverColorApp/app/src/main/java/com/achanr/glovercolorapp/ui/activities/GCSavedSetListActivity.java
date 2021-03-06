package com.achanr.glovercolorapp.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.SearchView;
import android.transition.Fade;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.CustomItemAnimator;
import com.achanr.glovercolorapp.common.GCConstants;
import com.achanr.glovercolorapp.common.GCOnlineDatabaseUtil;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.adapters.GCSavedSetListAdapter;
import com.achanr.glovercolorapp.ui.viewHolders.GCSavedSetListViewHolder;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public class GCSavedSetListActivity extends GCBaseActivity {

    public enum SortEnum {
        TITLE_ASC("Sort by title ascending"),
        TITLE_DESC("Sort by title descending"),
        CHIP_ASC("Sort by chip ascending"),
        CHIP_DESC("Sort by chip descending");

        final String description;

        SortEnum(String desc) {
            description = desc;
        }

        public String getDescription() {
            return description;
        }
    }

    private ArrayList<GCSavedSet> mSavedSetList;
    private GridRecyclerView mSavedSetListRecyclerView;
    private GCSavedSetListAdapter mSavedSetListAdapter;
    private GridLayoutManager mSavedSetListLayoutManager;
    private FloatingActionButton mFab;
    private String mFromNavigation;
    private boolean isFromEditing = false;
    private boolean isLeaving = false;
    private boolean isFromEnterCode = false;
    private boolean isAnimating = false;

    public static final String FROM_NAVIGATION = "from_navigation";
    public static final String NEW_SET_KEY = "new_set_key";
    public static final String OLD_SET_KEY = "old_set_key";
    public static final String IS_DELETE_KEY = "is_delete_key";

    private static final int ADD_NEW_SET_REQUEST_CODE = 1001;
    private static final int UPDATE_SET_REQUEST_CODE = 1002;

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
        setupToolbar(getString(R.string.title_your_saved_sets));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSavedSetListRecyclerView.setVisibility(View.INVISIBLE);
            mFab.setBackground(getDrawable(R.drawable.fab_ripple));
        } else {
            mFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setupContentLayout() {
        View view = getLayoutInflater().inflate(R.layout.activity_saved_set_list, mFrameLayout);
        GCSavedSetListViewHolder viewHolder = new GCSavedSetListViewHolder(view);
        mSavedSetListRecyclerView = viewHolder.getSavedSetListRecyclerView();
        mFab = viewHolder.getFab();

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_saved_color_sets);

        getSavedSetListFromDatabase();
        setupSavedSetList();

        Intent intent = getIntent();
        if (intent != null) {
            mFromNavigation = intent.getStringExtra(FROM_NAVIGATION);
            if (mFromNavigation != null) {
                if (mFromNavigation.equalsIgnoreCase(GCEnterCodeActivity.class.getName())
                        || mFromNavigation.equalsIgnoreCase(GCHomeActivity.class.getName())) {
                    isFromEnterCode = true;
                    GCSavedSet newSet = (GCSavedSet) intent.getSerializableExtra(NEW_SET_KEY);
                    Intent newIntent = new Intent(this, GCEditSavedSetActivity.class);
                    newIntent.putExtra(GCEditSavedSetActivity.IS_NEW_SET_KEY, true);
                    newIntent.putExtra(GCEditSavedSetActivity.SAVED_SET_KEY, newSet);
                    startActivityForResult(newIntent, ADD_NEW_SET_REQUEST_CODE);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                } else if (mFromNavigation.equalsIgnoreCase(GCEditCollectionActivity.class.getName())) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                    mFab.setVisibility(View.GONE);
                }
                intent.removeExtra(FROM_NAVIGATION);
            }

            if (intent.getBooleanExtra(GCUtil.WAS_REFRESHED, false)) {
                mFab.setVisibility(View.VISIBLE);
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_saved_set_list, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            for (TextView textView : GCUtil.findChildrenByClass(searchView, TextView.class)) {
                textView.setTextColor(Color.WHITE);
            }
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    final List<GCSavedSet> filteredModelList = filter(mSavedSetList, newText);
                    mSavedSetListAdapter.animateTo(filteredModelList);
                    mSavedSetListRecyclerView.scrollToPosition(0);
                    return true;
                }
            });
        }

        MenuItem sortItem = menu.findItem(R.id.menu_sort);
        sortItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSortDialog();
                return true;
            }
        });
        return true;
    }

    private List<GCSavedSet> filter(List<GCSavedSet> models, String query) {
        query = query.toLowerCase();

        final List<GCSavedSet> filteredModelList = new ArrayList<>();
        for (GCSavedSet model : models) {
            final String text = model.getTitle().toLowerCase();
            final String mode = model.getMode().getTitle().toLowerCase();
            final String chip = model.getChipSet().getTitle().toLowerCase();
            final String description = model.getDescription().toLowerCase();
            if (text.contains(query)
                    || mode.contains(query)
                    || chip.contains(query)
                    || description.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void showSortDialog() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
        builder.setTitle(getString(R.string.choose_sort_option));
        builder.setItems(sortDescs, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GCSavedSetListActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(GCConstants.SORTING_KEY, item);
                editor.apply();
                sort();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getSavedSetListFromDatabase() {
        ArrayList<GCSavedSet> savedSetList = GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.getAllData();
        if (mSavedSetList == null || mSavedSetList.size() <= 0) {
            mSavedSetList = new ArrayList<>();
        }
        mSavedSetList = savedSetList;
        mSavedSetList = GCUtil.sortList(this, mSavedSetList);
    }

    private void setupSavedSetList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mSavedSetListRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mSavedSetListLayoutManager = new GridLayoutManager(this, 1);
        mSavedSetListRecyclerView.setLayoutManager(mSavedSetListLayoutManager);
        mSavedSetListRecyclerView.setItemAnimator(new CustomItemAnimator());
        mSavedSetListAdapter = new GCSavedSetListAdapter(this, mSavedSetList);
        mSavedSetListAdapter.sortList();
        mSavedSetListRecyclerView.setAdapter(mSavedSetListAdapter);
    }

    public void onEditSetListItemClicked(GCSavedSet savedSet, HashMap<String, View> transitionViews) {
        isFromEditing = true;
        isLeaving = true;
        if (mFromNavigation != null && mFromNavigation.equalsIgnoreCase(GCEditCollectionActivity.class.getName())) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(GCEditCollectionActivity.ADD_SET_KEY, savedSet);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Intent intent = new Intent(this, GCEditSavedSetActivity.class);
            intent.putExtra(GCEditSavedSetActivity.SAVED_SET_KEY, savedSet);
            startEditSetActivityTransition(intent, transitionViews);
        }
    }

    private void onAddSetListItemClicked() {
        isLeaving = true;
        Intent intent = new Intent(this, GCEditSavedSetActivity.class);
        intent.putExtra(GCEditSavedSetActivity.IS_NEW_SET_KEY, true);
        startAddSetActivityTransition(intent);
    }

    private void startAddSetActivityTransition(Intent intent) {
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            getWindow().setExitTransition(new Fade());
            @SuppressWarnings("unchecked") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
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

    private void onSetUpdated(GCSavedSet oldSet, GCSavedSet newSet) {
        GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.updateData(oldSet, newSet);
        mSavedSetListAdapter.update(oldSet, newSet);
        int position = mSavedSetList.indexOf(oldSet);
        mSavedSetList.set(position, newSet);
        Toast.makeText(this, getString(R.string.set_updated_message), Toast.LENGTH_SHORT).show();
        GCOnlineDatabaseUtil.updateToOnlineDB(this, newSet);
    }

    public void onSetDeleted(GCSavedSet savedSet) {
        int id = GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.getData(savedSet).getId();
        GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.deleteData(savedSet);
        mSavedSetListAdapter.remove(savedSet);
        int position = mSavedSetList.indexOf(savedSet);
        mSavedSetList.remove(position);
        Toast.makeText(this, getString(R.string.set_deleted_message), Toast.LENGTH_SHORT).show();
        GCOnlineDatabaseUtil.deleteFromOnlineDB(this, id);
    }

    private void onSetAdded(GCSavedSet newSet) {
        GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.insertData(newSet);
        mSavedSetListAdapter.add(mSavedSetList.size(), newSet);
        mSavedSetList.add(mSavedSetList.size(), newSet);
        mSavedSetList = GCUtil.sortList(this, mSavedSetList);
        Toast.makeText(this, getString(R.string.set_added_message), Toast.LENGTH_SHORT).show();
        GCOnlineDatabaseUtil.addToOnlineDB(this, GCDatabaseHelper.getInstance(this).SAVED_SET_DATABASE.getData(newSet));
    }

    private void sort() {
        int firstVisible = mSavedSetListLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = mSavedSetListLayoutManager.findLastVisibleItemPosition();
        int itemsChanged = lastVisible - firstVisible + 1; // + 1 because we start count items from 0
        int start = firstVisible - itemsChanged > 0 ? firstVisible - itemsChanged : 0;
        mSavedSetList = GCUtil.sortList(this, mSavedSetList);
        mSavedSetListAdapter.sortList();
        mSavedSetListAdapter.notifyItemRangeChanged(start, itemsChanged + itemsChanged);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void animateFab(final boolean isAppearing, final AnimationCompleteListener animationCompleteListener) {
        mFab.post(new Runnable() {
            @Override
            public void run() {
                if (mFromNavigation != null && mFromNavigation.equalsIgnoreCase(GCEditCollectionActivity.class.getName())) {
                    mFab.setVisibility(View.GONE);
                    if (animationCompleteListener != null) {
                        animationCompleteListener.onComplete();
                    }
                    return;
                }

                // previously visible view
                final View myView = mFab;

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
        });
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
            mSavedSetListRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.grid_layout_in_animation));
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
            mSavedSetListRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.grid_layout_out_animation));
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
        if (isAnimating) {
            return;
        }

        isAnimating = true;
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            isAnimating = false;
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
                isAnimating = false;
                super.onBackPressed();
            }
        }
    }

    private void completedAnimationBackPressed() {
        isAnimating = false;
        super.onBackPressed();
    }

    public void refreshList() {
        getSavedSetListFromDatabase();
        mSavedSetListAdapter = new GCSavedSetListAdapter(this, mSavedSetList);
        mSavedSetListAdapter.sortList();
        mSavedSetListRecyclerView.setAdapter(mSavedSetListAdapter);
    }

    @Override
    protected void updateAfterLogout() {
        super.updateAfterLogout();
        refreshList();
    }
}

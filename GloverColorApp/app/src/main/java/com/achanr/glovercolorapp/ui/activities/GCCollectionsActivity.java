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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
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
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.database.GCDatabaseHelper;
import com.achanr.glovercolorapp.models.GCCollection;
import com.achanr.glovercolorapp.ui.adapters.GCCollectionsListAdapter;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 4/27/16 3:23 PM
 */
public class GCCollectionsActivity extends GCBaseActivity {

    public enum SortEnum {
        TITLE_ASC("Sort by title ascending"),
        TITLE_DESC("Sort by title descending");

        final String description;

        SortEnum(String desc) {
            description = desc;
        }

        public String getDescription() {
            return description;
        }
    }

    private ArrayList<GCCollection> mCollectionsList;
    private GridRecyclerView mCollectionsListRecyclerView;
    private GCCollectionsListAdapter mCollectionsListAdapter;
    private GridLayoutManager mCollectionsListLayoutManager;
    private FloatingActionButton mFab;
    private boolean isFromEditing = false;
    private boolean isLeaving = false;
    private boolean isFromEnterCode = false;
    private boolean isAnimating = false;
    private boolean fromActivityResult = false;

    public static final String OLD_COLLECTION_KEY = "old_collection_key";
    public static final String IS_DELETE_KEY = "is_delete_key";
    public static final String NEW_COLLECTION_KEY = "new_collection_key";
    private static final int ADD_NEW_COLLECTION_REQUEST_CODE = 1000;
    private static final int UPDATE_COLLECTION_REQUEST_CODE = 1001;

    public interface AnimationCompleteListener {
        void onComplete();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fromActivityResult = true;
        isLeaving = false;
        if (requestCode == ADD_NEW_COLLECTION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean isDeleted = data.getBooleanExtra(IS_DELETE_KEY, false);
            if (!isDeleted) {
                GCCollection newCollection = (GCCollection) data.getSerializableExtra(NEW_COLLECTION_KEY);
                onCollectionAdded(newCollection);
            }
        } else if (requestCode == UPDATE_COLLECTION_REQUEST_CODE) {
            isFromEditing = true;
            if (resultCode == RESULT_OK && data != null) {
                boolean isDeleted = data.getBooleanExtra(IS_DELETE_KEY, false);
                if (isDeleted) {
                    GCCollection collection = (GCCollection) data.getSerializableExtra(OLD_COLLECTION_KEY);
                    onCollectionDeleted(collection);
                } else {
                    GCCollection oldCollection = (GCCollection) data.getSerializableExtra(OLD_COLLECTION_KEY);
                    GCCollection newCollection = (GCCollection) data.getSerializableExtra(NEW_COLLECTION_KEY);
                    onCollectionUpdated(oldCollection, newCollection);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_collections, mFrameLayout);
        setupToolbar(getString(R.string.title_collections));

        getCollectionListFromDatabase();

        mCollectionsListRecyclerView = (GridRecyclerView) findViewById(R.id.collections_recyclerview);
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
                                    onAddCollectionClicked();
                                }
                            });
                        }
                    });
                } else {
                    onAddCollectionClicked();
                }
            }
        });

        setupSavedSetList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCollectionsListRecyclerView.setVisibility(View.INVISIBLE);
            mFab.setBackground(getDrawable(R.drawable.fab_ripple));
        } else {
            mFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_collections);

        if (!fromActivityResult) {
            getCollectionListFromDatabase();
            setupSavedSetList();
        } else {
            fromActivityResult = false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mCollectionsListRecyclerView.getVisibility() != View.VISIBLE) {
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
            mCollectionsListRecyclerView.setVisibility(View.VISIBLE);
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
                    final List<GCCollection> filteredModelList = filter(mCollectionsList, newText);
                    mCollectionsListAdapter.animateTo(filteredModelList);
                    mCollectionsListRecyclerView.scrollToPosition(0);
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

    private void getCollectionListFromDatabase() {
        ArrayList<GCCollection> collectionArrayList = GCDatabaseHelper.getInstance(this).COLLECTION_DATABASE.getAllData();
        if (mCollectionsList == null || mCollectionsList.size() <= 0) {
            mCollectionsList = new ArrayList<>();
        }
        mCollectionsList = collectionArrayList;
        mCollectionsList = GCUtil.sortCollectionList(this, mCollectionsList);
    }

    private List<GCCollection> filter(List<GCCollection> models, String query) {
        query = query.toLowerCase();

        final List<GCCollection> filteredModelList = new ArrayList<>();
        for (GCCollection model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void setupSavedSetList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mCollectionsListRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mCollectionsListLayoutManager = new GridLayoutManager(this, 1);
        mCollectionsListRecyclerView.setLayoutManager(mCollectionsListLayoutManager);
        mCollectionsListRecyclerView.setItemAnimator(new CustomItemAnimator());
        mCollectionsListAdapter = new GCCollectionsListAdapter(this, mCollectionsList);
        mCollectionsListRecyclerView.setAdapter(mCollectionsListAdapter);
    }

    public void onEditCollectionListItemClicked(GCCollection collection, HashMap<String, View> transitionViews) {
        isFromEditing = true;
        isLeaving = true;
        Intent intent = new Intent(this, GCEditCollectionActivity.class);
        intent.putExtra(GCEditCollectionActivity.SAVED_COLLECTION_KEY, collection);
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
                    pairArrayList.get(2));
            animateFab(false, null);
            startActivityForResult(intent, UPDATE_COLLECTION_REQUEST_CODE, options.toBundle());
        } else {
            // Implement this feature without material design
            startActivityForResult(intent, UPDATE_COLLECTION_REQUEST_CODE);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    private void onAddCollectionClicked() {
        isLeaving = true;
        Intent intent = new Intent(this, GCEditCollectionActivity.class);
        intent.putExtra(GCEditCollectionActivity.IS_NEW_COLLECTION_KEY, true);
        startAddCollectionActivityTransition(intent);
    }

    private void startAddCollectionActivityTransition(Intent intent) {
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            getWindow().setExitTransition(new Fade());
            @SuppressWarnings("unchecked") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivityForResult(intent, ADD_NEW_COLLECTION_REQUEST_CODE, options.toBundle());
        } else {
            // Implement this feature without material design
            startActivityForResult(intent, ADD_NEW_COLLECTION_REQUEST_CODE);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    private void onCollectionUpdated(GCCollection oldCollection, GCCollection newCollection) {
        GCDatabaseHelper.getInstance(this).COLLECTION_DATABASE.updateData(oldCollection, newCollection);
        mCollectionsListAdapter.update(oldCollection, newCollection);
        int position = mCollectionsList.indexOf(oldCollection);
        mCollectionsList.set(position, newCollection);
        Toast.makeText(this, R.string.collection_updated, Toast.LENGTH_SHORT).show();
    }

    public void onCollectionDeleted(GCCollection savedCollection) {
        GCDatabaseHelper.getInstance(this).COLLECTION_DATABASE.deleteData(savedCollection);
        mCollectionsListAdapter.remove(savedCollection);
        int position = mCollectionsList.indexOf(savedCollection);
        mCollectionsList.remove(position);
        Toast.makeText(this, R.string.collection_deleted, Toast.LENGTH_SHORT).show();
    }

    private void onCollectionAdded(GCCollection newCollection) {
        GCDatabaseHelper.getInstance(this).COLLECTION_DATABASE.insertData(newCollection);
        mCollectionsListAdapter.add(mCollectionsList.size(), newCollection);
        mCollectionsList.add(mCollectionsList.size(), newCollection);
        mCollectionsList = GCUtil.sortCollectionList(this, mCollectionsList);
        Toast.makeText(this, R.string.collection_added, Toast.LENGTH_SHORT).show();
    }

    private void showSortDialog() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int sortTypeInt = prefs.getInt(GCConstants.COLLECTION_SORTING_KEY, 0);

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
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(GCCollectionsActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(GCConstants.COLLECTION_SORTING_KEY, item);
                editor.apply();
                sort();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sort() {
        int firstVisible = mCollectionsListLayoutManager.findFirstVisibleItemPosition();
        int lastVisible = mCollectionsListLayoutManager.findLastVisibleItemPosition();
        int itemsChanged = lastVisible - firstVisible + 1; // + 1 because we start count items from 0
        int start = firstVisible - itemsChanged > 0 ? firstVisible - itemsChanged : 0;
        mCollectionsList = GCUtil.sortCollectionList(this, mCollectionsList);
        mCollectionsListAdapter.sortList();
        mCollectionsListAdapter.notifyItemRangeChanged(start, itemsChanged + itemsChanged);
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
                if (mCollectionsListRecyclerView.getVisibility() != View.VISIBLE) {
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
            mCollectionsListRecyclerView.setVisibility(View.VISIBLE);
            mCollectionsListRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.grid_layout_in_animation));
            mCollectionsListRecyclerView.setLayoutAnimationListener(new Animation.AnimationListener() {
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
            mCollectionsListRecyclerView.scheduleLayoutAnimation();
        } else {
            isLeaving = true;
            mCollectionsListRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.grid_layout_out_animation));
            mCollectionsListRecyclerView.setLayoutAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCollectionsListRecyclerView.setVisibility(View.INVISIBLE);
                    if (animationCompleteListener != null) {
                        animationCompleteListener.onComplete();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mCollectionsListRecyclerView.scheduleLayoutAnimation();
            mCollectionsListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (isAnimating) {
            return;
        }

        isAnimating = true;
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
        getCollectionListFromDatabase();
        mCollectionsListAdapter = new GCCollectionsListAdapter(this, mCollectionsList);
        mCollectionsListRecyclerView.setAdapter(mCollectionsListAdapter);
    }
}

package com.achanr.glovercolorapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Pair;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.adapters.GCSavedSetListAdapter;
import com.achanr.glovercolorapp.database.GCSavedSetDatabase;
import com.achanr.glovercolorapp.models.GCSavedSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public class GCSavedSetListActivity extends GCBaseActivity {

    private Context mContext;
    private GCSavedSetDatabase mSavedSetDatabase;
    private ArrayList<GCSavedSet> mSavedSetList;
    private RecyclerView mSavedSetListRecyclerView;
    private GCSavedSetListAdapter mSavedSetListAdapter;
    private RecyclerView.LayoutManager mSavedSetListLayoutManager;
    private FloatingActionButton mFab;

    public static final String FROM_NAVIGATION = "from_navigation";
    public static final String NEW_SET_KEY = "new_set_key";
    public static final String OLD_SET_KEY = "old_set_key";
    public static final String IS_DELETE_KEY = "is_delete_key";

    public static final int ADD_NEW_SET_REQUEST_CODE = 1001;
    public static final int UPDATE_SET_REQUEST_CODE = 1002;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NEW_SET_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean isDeleted = data.getBooleanExtra(IS_DELETE_KEY, false);
            if (!isDeleted) {
                GCSavedSet newSet = (GCSavedSet) data.getSerializableExtra(NEW_SET_KEY);
                onSetAdded(newSet);
            }
        } else if (requestCode == UPDATE_SET_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
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
        if (findViewById(R.id.fab).getVisibility() == View.INVISIBLE) {
            animFabAppear();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_saved_set_list, mFrameLayout);
        mContext = this;
        setupToolbar(getString(R.string.title_your_saved_sets));

        mSavedSetDatabase = new GCSavedSetDatabase(mContext);
        mSavedSetList = getSavedSetListFromDatabase();

        if (mSavedSetList != null && mSavedSetList.size() > 0) {
            findViewById(R.id.icon_background).setVisibility(View.GONE);
        }

        mSavedSetListRecyclerView = (RecyclerView) findViewById(R.id.saved_set_list_recyclerview);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animFabDisappear(null, null);
                } else {
                    onAddSetListItemClicked();
                }
            }
        });

        setupSavedSetList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimationListener();
        }

        Intent intent = getIntent();
        if (intent != null) {
            String fromNavigation = intent.getStringExtra(FROM_NAVIGATION);
            if (fromNavigation != null) {
                if (fromNavigation.equalsIgnoreCase(GCEnterCodeActivity.class.getName())) {
                    GCSavedSet newSet = (GCSavedSet) intent.getSerializableExtra(NEW_SET_KEY);
                    Intent newIntent = new Intent(mContext, GCEditSavedSetActivity.class);
                    newIntent.putExtra(GCEditSavedSetActivity.IS_NEW_SET_KEY, true);
                    newIntent.putExtra(GCEditSavedSetActivity.SAVED_SET_KEY, newSet);
                    startAddSetActivityTransition(newIntent);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_saved_color_sets);
    }

    private ArrayList<GCSavedSet> getSavedSetListFromDatabase() {
        ArrayList<GCSavedSet> savedSetList = mSavedSetDatabase.readData();
        if (mSavedSetList == null || mSavedSetList.size() <= 0) {
            mSavedSetList = new ArrayList<>();
        }
        return savedSetList;
    }

    private void setupSavedSetList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mSavedSetListRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mSavedSetListLayoutManager = new LinearLayoutManager(mContext);
        mSavedSetListRecyclerView.setLayoutManager(mSavedSetListLayoutManager);
        mSavedSetListAdapter = new GCSavedSetListAdapter(mContext, mSavedSetList);
        mSavedSetListRecyclerView.setAdapter(mSavedSetListAdapter);

        /*mSavedSetListRecyclerView.addOnItemTouchListener(
                new GCSavedSetListItemClickListener(mContext,
                        new GCSavedSetListItemClickListener.OnSavedSetItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (mListener != null) {
                                    //mListener.onSavedSetListItemClicked(position);
                                }
                            }
                        })
        );*/
    }

    public void onSavedSetListItemClicked(GCSavedSet savedSet, HashMap<String, View> transitionViews) {
        Intent intent = new Intent(mContext, GCEditSavedSetActivity.class);
        intent.putExtra(GCEditSavedSetActivity.SAVED_SET_KEY, savedSet);
        startEditSetActivityTransition(intent, transitionViews);
    }

    public void onAddSetListItemClicked() {
        Intent intent = new Intent(mContext, GCEditSavedSetActivity.class);
        intent.putExtra(GCEditSavedSetActivity.IS_NEW_SET_KEY, true);
        startAddSetActivityTransition(intent);
    }

    private void startAddSetActivityTransition(Intent intent) {
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            getWindow().setExitTransition(new Slide());
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
            animFabDisappear(intent, options);
            startActivityForResult(intent, UPDATE_SET_REQUEST_CODE, options.toBundle());
        } else {
            // Implement this feature without material design
            startActivityForResult(intent, UPDATE_SET_REQUEST_CODE);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    public void onSetUpdated(GCSavedSet oldSet, GCSavedSet newSet) {
        mSavedSetDatabase.updateData(oldSet, newSet);
        mSavedSetList = getSavedSetListFromDatabase();
        mSavedSetListAdapter.update(oldSet, newSet);
        Toast.makeText(mContext, getString(R.string.set_updated_message), Toast.LENGTH_SHORT).show();
    }

    public void onSetDeleted(GCSavedSet savedSet) {
        mSavedSetDatabase.deleteData(savedSet);
        mSavedSetList = getSavedSetListFromDatabase();
        mSavedSetListAdapter.remove(savedSet);
        Toast.makeText(mContext, getString(R.string.set_deleted_message), Toast.LENGTH_SHORT).show();
    }

    public void onSetAdded(GCSavedSet newSet) {
        mSavedSetDatabase.insertData(newSet);
        mSavedSetList = getSavedSetListFromDatabase();
        mSavedSetListAdapter.add(mSavedSetList.indexOf(newSet), newSet);
        Toast.makeText(mContext, getString(R.string.set_added_message), Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animFabAppear() {
        // previously invisible view
        final View myView = findViewById(R.id.fab);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(myView, "rotation", 45f, 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim, animator);
        // start the animation
        animatorSet.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animFabDisappear(final Intent intent, final ActivityOptions options) {
        // previously visible view
        final View myView = findViewById(R.id.fab);

        // get the center for the clipping circle
        final int cx = myView.getMeasuredWidth() / 2;
        final int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
                if (intent == null) {
                    onAddSetListItemClicked();
                } else {
                    //startActivityForResult(intent, UPDATE_SET_REQUEST_CODE, options.toBundle());
                }
            }
        });
        ObjectAnimator animator = ObjectAnimator.ofFloat(myView, "rotation", 0f, 45f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim, animator);
        // start the animation
        animatorSet.start();
    }

    private Transition.TransitionListener mTransitionListener;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimationListener() {
        mTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                animFabAppear();
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
        };
        getWindow().getEnterTransition().addListener(mTransitionListener);
    }
}

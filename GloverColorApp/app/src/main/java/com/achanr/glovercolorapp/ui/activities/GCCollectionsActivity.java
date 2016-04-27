package com.achanr.glovercolorapp.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.CustomItemAnimator;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.adapters.GCCollectionsListAdapter;
import com.achanr.glovercolorapp.ui.views.GridRecyclerView;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 4/27/16 3:23 PM
 */
public class GCCollectionsActivity extends GCBaseActivity {

    private Context mContext;
    private ArrayList<GCSavedSet> mCollectionsList;
    private GridRecyclerView mCollectionsListRecyclerView;
    private GCCollectionsListAdapter mCollectionsListAdapter;
    private GridLayoutManager mCollectionsListLayoutManager;
    private FloatingActionButton mFab;
    private boolean isFromEditing = false;
    private boolean isLeaving = false;
    private boolean isFromEnterCode = false;
    private boolean isAnimating = false;

    public interface AnimationCompleteListener {
        void onComplete();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_collections, mFrameLayout);
        mContext = this;
        setupToolbar(getString(R.string.title_collections));

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

                                }
                            });
                        }
                    });
                } else {

                }
            }
        });

        mCollectionsList = new ArrayList<>();
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


        return false;
    }

    private void setupSavedSetList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mCollectionsListRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mCollectionsListLayoutManager = new GridLayoutManager(mContext, 1);
        mCollectionsListRecyclerView.setLayoutManager(mCollectionsListLayoutManager);
        mCollectionsListRecyclerView.setItemAnimator(new CustomItemAnimator());
        mCollectionsListAdapter = new GCCollectionsListAdapter(mContext, mCollectionsList);
        mCollectionsListRecyclerView.setAdapter(mCollectionsListAdapter);
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
            mCollectionsListRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mContext, R.anim.grid_layout_in_animation));
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
            mCollectionsListRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mContext, R.anim.grid_layout_out_animation));
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
        if(isAnimating){
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
}

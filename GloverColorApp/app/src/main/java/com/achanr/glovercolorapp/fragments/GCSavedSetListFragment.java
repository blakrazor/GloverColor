/*
package com.achanr.glovercolorapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.adapters.GCSavedSetListAdapter;
import com.achanr.glovercolorapp.listeners.IGCSavedSetListFragmentListener;
import com.achanr.glovercolorapp.models.GCSavedSet;

import java.util.ArrayList;


public class GCSavedSetListFragment extends Fragment implements View.OnClickListener {

    private ArrayList<GCSavedSet> mSavedSetList;
    private RecyclerView mSavedSetListRecyclerView;
    private GCSavedSetListAdapter mSavedSetListAdapter;
    private RecyclerView.LayoutManager mSavedSetListLayoutManager;
    private FloatingActionButton mFab;

    private Context mContext;

    private IGCSavedSetListFragmentListener mListener;

    public static final String SAVED_SET_LIST_KEY = "saved_set_list_key";

    public GCSavedSetListFragment() {
        // Required empty public constructor
    }

    */
/**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SavedSetListFragment.
     *//*

    public static GCSavedSetListFragment newInstance(ArrayList<GCSavedSet> savedSetList) {
        GCSavedSetListFragment fragment = new GCSavedSetListFragment();
        Bundle args = new Bundle();
        args.putSerializable(SAVED_SET_LIST_KEY, savedSetList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof IGCSavedSetListFragmentListener) {
            mListener = (IGCSavedSetListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IGCSavedSetListFragmentListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mSavedSetList = (ArrayList<GCSavedSet>) savedInstanceState.getSerializable(SAVED_SET_LIST_KEY);
            mSavedSetListAdapter = new GCSavedSetListAdapter(mContext, mSavedSetList, mListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_SET_LIST_KEY, mSavedSetList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (getArguments() != null) {
            mSavedSetList = (ArrayList<GCSavedSet>) getArguments().getSerializable(SAVED_SET_LIST_KEY);
            mSavedSetListAdapter = new GCSavedSetListAdapter(mContext, mSavedSetList, mListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_saved_set_list, container, false);
        mSavedSetListRecyclerView = (RecyclerView) v.findViewById(R.id.saved_set_list_recyclerview);
        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        setupSavedSetList();
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mSavedSetList = null;
        mSavedSetListRecyclerView = null;
        mSavedSetListAdapter = null;
        mSavedSetListLayoutManager = null;
        mFab = null;
        mContext = null;
    }

    private void setupSavedSetList() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mSavedSetListRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mSavedSetListLayoutManager = new LinearLayoutManager(mContext);
        mSavedSetListRecyclerView.setLayoutManager(mSavedSetListLayoutManager);
        mSavedSetListRecyclerView.setAdapter(mSavedSetListAdapter);

        */
/*mSavedSetListRecyclerView.addOnItemTouchListener(
                new GCSavedSetListItemClickListener(mContext,
                        new GCSavedSetListItemClickListener.OnSavedSetItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (mListener != null) {
                                    //mListener.onEditSetListItemClicked(position);
                                }
                            }
                        })
        );*//*

    }

    @Override
    public void onClick(View v) {
        if (v == mFab) {
            addSavedSet();
        }
    }

    private void addSavedSet() {
        if (mListener != null) {
            mListener.onAddSetListItemClicked();
        }
    }

    public void onSetAdded(int position, GCSavedSet savedSet) {
        mSavedSetListAdapter.add(position, savedSet);
    }

    public void onSetUpdated(GCSavedSet oldSet, GCSavedSet newSet) {
        mSavedSetListAdapter.update(oldSet, newSet);
    }

    public void onSetDeleted(GCSavedSet savedSet) {
        mSavedSetListAdapter.remove(savedSet);
    }
}
*/

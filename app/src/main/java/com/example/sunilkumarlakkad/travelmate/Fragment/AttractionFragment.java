package com.example.sunilkumarlakkad.travelmate.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sunilkumarlakkad.travelmate.Adapter.FlipAdapter;
import com.example.sunilkumarlakkad.travelmate.R;

import se.emilsjolander.flipview.FlipView;
import se.emilsjolander.flipview.OverFlipMode;


/**
 * A simple {@link Fragment} subclass.
 */
public class AttractionFragment extends Fragment implements FlipView.OnFlipListener, FlipView.OnOverFlipListener {

    public interface OnAttractionSelectionCallbacks {
        void onAttractionSelected(int position);
    }

    public static FlipAdapter mAdapter;
    OnAttractionSelectionCallbacks mCallback;

    public static AttractionFragment newInstance() {
        return new AttractionFragment();
    }

    public AttractionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mCallback = (OnAttractionSelectionCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_attraction, container, false);

        FlipView mFlipView = (FlipView) rootView.findViewById(R.id.flip_view);
        mAdapter = new FlipAdapter(getActivity());

        mAdapter.setCallback(new FlipAdapter.Callback() {
            @Override
            public void onPageRequested(int page) {
                mCallback.onAttractionSelected(page);
            }
        });
        mFlipView.setAdapter(mAdapter);
        mFlipView.setOnFlipListener(this);
        mFlipView.peakNext(false);
        mFlipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
        mFlipView.setOnOverFlipListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Attractions");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFlippedToPage(FlipView v, int position, long id) {

    }

    @Override
    public void onOverFlip(FlipView v, OverFlipMode mode, boolean overFlippingPrevious, float overFlipDistance, float flipDistancePerPage) {

    }
}

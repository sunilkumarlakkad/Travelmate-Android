package com.example.sunilkumarlakkad.travelmate.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sunilkumarlakkad.travelmate.Activity.HomeActivity;
import com.example.sunilkumarlakkad.travelmate.Adapter.MyYoutubeRecyclerAdapter;
import com.example.sunilkumarlakkad.travelmate.R;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class YoutubeVideoListFragment extends Fragment {

    RecyclerView mRecyclerView;
    public static MyYoutubeRecyclerAdapter myYoutubeRecyclerAdapter;
    OnItemSelectCallbacks mCallbacks;

    public interface OnItemSelectCallbacks {
        void onItemSelected(String videoID);
    }

    public static YoutubeVideoListFragment newInstance() {
        return new YoutubeVideoListFragment();
    }

    public YoutubeVideoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        mCallbacks = (OnItemSelectCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_youtube_video_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.myRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        myYoutubeRecyclerAdapter = new MyYoutubeRecyclerAdapter(getActivity());

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(myYoutubeRecyclerAdapter);
        mRecyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        myYoutubeRecyclerAdapter.setOnItemClickListener(new MyYoutubeRecyclerAdapter.OnItemListener() {
            @Override
            public void onItemClicked(int t) {
                mCallbacks.onItemSelected(HomeActivity.videoItemList.get(t).getId());
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Videos");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

package com.example.sunilkumarlakkad.travelmate.Fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.bowyer.app.fabtransitionlayout.BottomSheetLayout;
import com.example.sunilkumarlakkad.travelmate.Adapter.MyFirebaseRecylerAdapter;
import com.example.sunilkumarlakkad.travelmate.Model.Note;
import com.example.sunilkumarlakkad.travelmate.R;
import com.example.sunilkumarlakkad.travelmate.Utility;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotesListFragment extends Fragment {

    Firebase ref;
    RecyclerView mRecyclerView;
    MyFirebaseRecylerAdapter myFirebaseRecylerAdapter;
    BottomSheetLayout mBottomSheetLayout;

    public static NotesListFragment newInstance() {
        return new NotesListFragment();
    }

    public NotesListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        String strUser = Utility.Username;
        strUser = strUser.substring(0, strUser.lastIndexOf("."));
        String URL = Utility.FIREBASEREF + "notedata/";
        URL = URL + strUser;
        ref = new Firebase(URL);
        View rootView = inflater.inflate(R.layout.fragment_notes_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.myRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());


        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        myFirebaseRecylerAdapter = new MyFirebaseRecylerAdapter(Note.class, R.layout.recyclerview_row, MyFirebaseRecylerAdapter.MovieViewHolder.class, ref);

        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(myFirebaseRecylerAdapter);
        mRecyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));

        SlideInLeftAnimator animator = new SlideInLeftAnimator();
        animator.setInterpolator(new OvershootInterpolator());
        mRecyclerView.setItemAnimator(animator);


        myFirebaseRecylerAdapter.setOnItemClickListener(new MyFirebaseRecylerAdapter.OnItemListener() {
            @Override
            public void onDeleteClicked(View v, int t) {
                Note movieNote = myFirebaseRecylerAdapter.getItem(t);
                ref.child(movieNote.getId()).removeValue();
            }
        });

        final TextView txtTitle = (TextView) rootView.findViewById(R.id.txtNoteTitle);
        final TextView txtDetail = (TextView) rootView.findViewById(R.id.txtNoteDetail);
        final Button btnAdd = (Button) rootView.findViewById(R.id.btnAddNote);

        btnAdd.setEnabled(false);
        final boolean[] isEmpty = {true, true};
        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0)
                    isEmpty[0] = false;
                if (!isEmpty[0] && !isEmpty[1])
                    btnAdd.setEnabled(true);
            }
        });
        txtDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0)
                    isEmpty[1] = false;
                if (!isEmpty[0] && !isEmpty[1])
                    btnAdd.setEnabled(true);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long millis = System.currentTimeMillis();
                String timestamp = String.valueOf(millis);
                Firebase t = ref.child(timestamp);
                Map<String, String> map = new HashMap<>();
                map.put("id", timestamp);
                map.put("title", txtTitle.getText().toString());
                map.put("detail", txtDetail.getText().toString());
                t.setValue(map);
                mBottomSheetLayout.slideOutFab();
                txtTitle.setText("");
                txtDetail.setText("");
                btnAdd.setEnabled(false);
                isEmpty[0] = true;
                isEmpty[1] = true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_addnote);
        mBottomSheetLayout = (BottomSheetLayout) rootView.findViewById(R.id.bottom_sheet);
        mBottomSheetLayout.setFab(fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetLayout.expandFab();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Notes");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

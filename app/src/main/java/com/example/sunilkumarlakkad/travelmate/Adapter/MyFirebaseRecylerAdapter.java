package com.example.sunilkumarlakkad.travelmate.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sunilkumarlakkad.travelmate.Model.Note;
import com.example.sunilkumarlakkad.travelmate.R;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

public class MyFirebaseRecylerAdapter extends FirebaseRecyclerAdapter<Note, MyFirebaseRecylerAdapter.MovieViewHolder> {

    static OnItemListener onItemListener;

    public interface OnItemListener {
        void onDeleteClicked(View v, int t);
    }

    public void setOnItemClickListener(final OnItemListener onItemListener) {
        MyFirebaseRecylerAdapter.onItemListener = onItemListener;
    }

    public MyFirebaseRecylerAdapter(Class<Note> modelClass, int modelLayout,
                                    Class<MovieViewHolder> holder, Query ref) {
        super(modelClass, modelLayout, holder, ref);
    }

    @Override
    protected void populateViewHolder(MovieViewHolder movieViewHolder, Note note, int i) {

        movieViewHolder.title.setText(note.getTitle());
        movieViewHolder.detail.setText(note.getDetail());
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView detail;
        ImageView delete;

        public MovieViewHolder(View v) {
            super(v);
            title = (TextView) itemView.findViewById(R.id.rv_title);
            detail = (TextView) itemView.findViewById(R.id.rv_detail);
            delete = (ImageView) itemView.findViewById(R.id.rv_delete);

            if (delete != null) {
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemListener != null)
                            onItemListener.onDeleteClicked(delete, getAdapterPosition());
                    }
                });
            }
        }
    }

}

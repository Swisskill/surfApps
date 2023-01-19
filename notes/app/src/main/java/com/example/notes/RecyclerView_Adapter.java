package com.example.notes;
//@author Will Brant with help from
// https://www.c-sharpcorner.com/article/recyclerview-in-andriod-with-java/
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;

public class RecyclerView_Adapter extends RecyclerView.Adapter<View_Holder> {

    notes exp;
    private onItemClickListener listener;

    int list;
    Context context;
    private Cursor pCursor;
    public RecyclerView_Adapter(int layout, Application application, Cursor c) {
        this.list = layout;
        this.context = application;
        pCursor = c;
    }


    public interface onItemClickListener {
        void onItemClick(String ID);
    }
    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        View_Holder holder = new View_Holder(v, listener);
        return holder;
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        pCursor.moveToPosition(position);
        holder.note.setText(pCursor.getString(pCursor.getColumnIndex(mySQLiteHelper.KEY_NOTE))); //todo: maybe put this in?

        Log.wtf("ID is ", pCursor.getString(pCursor.getColumnIndex(mySQLiteHelper.KEY_ROWID) ));
        holder.note.setTag(pCursor.getString(pCursor.getColumnIndex(mySQLiteHelper.KEY_ROWID)));
    }
    public void setCursor(Cursor cursor) {
        pCursor = cursor;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return pCursor == null ? 0 : pCursor.getCount();
    }
}
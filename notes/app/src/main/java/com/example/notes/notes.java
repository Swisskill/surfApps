package com.example.notes;

//@author Will Brant with assistance from Jim Ward

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * put in update fields when clicking
 * make sure dollar amounts are valid (no multi decimal)
 */

public class notes extends Fragment {
    public notes(){
    }
    final static String TAG = "Expend Fragment";
    private RecyclerView mRecyclerView;
    String Note = "";
    RecyclerView recyclerView;
    RecyclerView_Adapter adapter;
    CursorViewModel mCursor;
    //----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //----------------------------Initial Setups-------------------------------
        mCursor = new ViewModelProvider(requireActivity()).get(CursorViewModel.class);
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).hide();//get rid of toolbar
        View myView = inflater.inflate(R.layout.fragment_expend, container, false);
        recyclerView = (RecyclerView) myView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerView_Adapter(R.layout.row_layout, getActivity().getApplication(), null);
        //----------------------------Cursor Set-------------------------------
        mCursor.getData().observe(getActivity(), new Observer<Cursor>() {
            @Override
            public void onChanged(Cursor cursor) {
                adapter.setCursor(cursor);
            }
        });

        //----------------------------Touch-------------------------------
        adapter.setOnItemClickListener(new RecyclerView_Adapter.onItemClickListener() {
            @Override
            public void onItemClick(String ID) {
                //Toast.makeText(getContext(), "-wb", Toast.LENGTH_LONG).show();
                Log.wtf("update click ", ID);
                Dialog(Integer.valueOf(ID), 0);
            }
        });
        //---------------------------Set Recycler---------------------------------
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //---------------------------Swipe to Delete---------------------------------
        ItemTouchHelper.SimpleCallback toucher = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction==ItemTouchHelper.RIGHT){

                    String ID = ((View_Holder)viewHolder).note.getTag().toString();
                    Log.d("Swipe id", ID);
                    int item = viewHolder.getAdapterPosition();
                    mCursor.Delete("Notes", ID, null); //they all have the same ID
                    adapter.notifyDataSetChanged();
                }
            }
        };
        ItemTouchHelper toucherHelper = new ItemTouchHelper(toucher);
        toucherHelper.attachToRecyclerView(recyclerView);
        //---------------------------FAB Add---------------------------------
        FloatingActionButton fab = myView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog(0,1);
            }
        });
        return myView;
    }
    //--------------------------------End On Create-------------------------------------------------
    //------------------------------------Dialog----------------------------------------------------
    @SuppressLint("Range")
    void Dialog(int ID, int dial) {
        String dialType;
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        final View textenter = inflater.inflate(R.layout.fragment_my_dialog, null);
        final EditText set_note = textenter.findViewById(R.id.et_note);
        if(dial == 0){
            dialType="Update";
            Cursor pCursor = mCursor.Query(String.valueOf(ID));
            set_note.setText(pCursor.getString(pCursor.getColumnIndex(mySQLiteHelper.KEY_NOTE)));
        }else{dialType="Add";}

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                requireContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog));
        builder.setView(textenter).setTitle(dialType);
        builder.setPositiveButton(dialType, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                checkEmpty(set_note, dial);
                logControl();
                if(dial == 1){
                    mCursor.add(Note);
                } else{
                    mCursor.Update("Notes", dbControl(), String.valueOf(ID), null);
                }
                adapter.notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                logCanceled();
                dialog.cancel();
            }
        });
        builder.show();
    }
    //---------------------------------------------------------------------------------------------
    void checkEmpty(EditText o, int dial) {
        String check = o.getText().toString();
        if (check.isEmpty()){Note = "nothing. . .";}
        else if (dial != 1){
            Note= check;
        }
        else {Note=String.valueOf(LocalDate.now())+":\n"+check;}
    }
    //---------------------------------------------------------------------------------------------
    void logCanceled(){
        Log.d(TAG, "dialog canceled");
    }
    //----------------------------------------------------------------------------------------------
    void logControl() {
        Log.d(TAG, "NOTE is " + Note);
    }
    ContentValues dbControl(){
        ContentValues values = new ContentValues();
        values.put(mySQLiteHelper.KEY_NOTE, Note);
        return values;
    }


}

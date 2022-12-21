package com.example.expenses;

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

/**
 * A simple {@link Fragment} subclass.
 * put in update fields when clicking
 * make sure dollar amounts are valid (no multi decimal)
 */

public class expend extends Fragment {
    public expend(){
    }
    final static String TAG = "Expend Fragment";
    private RecyclerView mRecyclerView;
    String Name = "";
    String Cate = "";
    String Date = "";
    String Amot = "";
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

                    String ID = ((View_Holder)viewHolder).name.getTag().toString();
                    Log.d("Swipe id", ID);
                    int item = viewHolder.getAdapterPosition();
                    mCursor.Delete("Expenses", ID, null); //they all have the same ID
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
        final EditText set_name = textenter.findViewById(R.id.et_name);
        final EditText set_cate = textenter.findViewById(R.id.et_cate);
        final EditText set_date = textenter.findViewById(R.id.et_date);
        final EditText set_amot = textenter.findViewById(R.id.et_amot);
        final EditText set_note = textenter.findViewById(R.id.et_note);
        if(dial == 0){
            dialType="Update";
            Cursor pCursor = mCursor.Query(String.valueOf(ID));
            set_name.setText(pCursor.getString(pCursor.getColumnIndex(mySQLiteHelper.KEY_NAME)));
            set_cate.setText(pCursor.getString(pCursor.getColumnIndex(mySQLiteHelper.KEY_CATE)));
            set_date.setText(pCursor.getString(pCursor.getColumnIndex(mySQLiteHelper.KEY_DATE)));
            set_amot.setText(pCursor.getString(pCursor.getColumnIndex(mySQLiteHelper.KEY_AMOT)));
            set_note.setText(pCursor.getString(pCursor.getColumnIndex(mySQLiteHelper.KEY_NOTE)));
        }else{dialType="Add";}

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                requireContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog));
        builder.setView(textenter).setTitle(dialType);
        builder.setPositiveButton(dialType, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                checkEmpty(set_name,set_cate,set_date,set_amot,set_note);
                logControl();
                if(dial == 1){
                    mCursor.add(Name, Cate, Date, Amot, Note);
                } else{
                    mCursor.Update("Expenses", dbControl(), String.valueOf(ID), null);
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
    void checkEmpty(EditText n,EditText c,EditText d,EditText a,EditText o) {
        //I know this method is hard to look at. It's simple: it takes those edit texts, checks for empty
        //and updates those global variables as a string. Nice and easy.
        String rn, rc, rd, ra, ro; String nd = String.valueOf(LocalDate.now());
        if (n.getText().toString().isEmpty()){Name = "Expense";}else{Name=n.getText().toString();}
        if (c.getText().toString().isEmpty()){Cate = "misc.";}else{Cate=c.getText().toString();}
        if (d.getText().toString().isEmpty()){Date = nd;}else{Date=d.getText().toString();}
        if (a.getText().toString().isEmpty()){Amot = "0";}
        else if (!isFloat(a.getText().toString())){
            Amot= "0";} else{Amot=a.getText().toString();}
        if (o.getText().toString().isEmpty()){Note = "nothing. . .";}else{Note=o.getText().toString();}
    }
    //---------------------------------------------------------------------------------------------
    void logCanceled(){
        Log.d(TAG, "dialog canceled");
    }
    //----------------------------------------------------------------------------------------------
    void logControl() {
        Log.d(TAG, "NAME is " + Name);Log.d(TAG, "CATEGORY is " + Cate);
        Log.d(TAG, "DATE is " + Date);Log.d(TAG, "AMOUNT is " + Amot);
        Log.d(TAG, "NOTE is " + Note);
    }
    ContentValues dbControl(){
        ContentValues values = new ContentValues();
        values.put(mySQLiteHelper.KEY_NAME, Name); // create new data for update
        values.put(mySQLiteHelper.KEY_CATE, Cate);
        values.put(mySQLiteHelper.KEY_DATE, Date);
        values.put(mySQLiteHelper.KEY_AMOT, Amot);
        values.put(mySQLiteHelper.KEY_NOTE, Note);
        return values;
    }
    public boolean isFloat(String amot) {
        float check;
        if(amot == null || amot.equals("")) {return false;}
        try {
            check = Float.parseFloat(amot);
            Log.wtf("Amount:", "is float");
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(),"Please enter in a valid amount ", Toast.LENGTH_LONG).show();
            Log.wtf("Amount:", "not float");

        }
        return false;
    }
    //---------------------------------------------------------------------------------------------


}
//--------------------------------------------------------------------------------------------------

/*
------------------------BIN------------------------------------
Toast.makeText(getContext(), "-wb", Toast.LENGTH_LONG).show();
Name = "Name:     " + fields[0];Cate = "Category: " + fields[1];
                Date = "Date:     " + fields[2];Amot = "Amount:   " + fields[3];
                Note = "Note:     " + fields[4];


 void controlInsert(){
        mydb.open();
        mydb.insertName(Name, Cate, Date, Amot, Note);
        mCursor.setValue(mydb.getAllNames());
        mydb.close();
    }
    void controlDelete(){

    }
        //----------------------------------------------------------------------------------------------
//    void showDialog() {
//        LayoutInflater inflater = LayoutInflater.from(requireContext());
//        final View textenter = inflater.inflate(R.layout.fragment_my_dialog, null);
//        final EditText et_name = textenter.findViewById(R.id.et_name);
//        final EditText et_cate = textenter.findViewById(R.id.et_cate);
//        final EditText et_date = textenter.findViewById(R.id.et_date);
//        final EditText et_amot = textenter.findViewById(R.id.et_amot);
//        final EditText et_note = textenter.findViewById(R.id.et_note);
//        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(requireContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog));
//        builder.setView(textenter).setTitle("Add");
//        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int id) {
//                Log.d("Who knows", String.valueOf(getId()));
//                checkEmpty(et_name,et_cate,et_date,et_amot,et_note);
//                logControl();
//                mCursor.add(Name, Cate, Date, Amot, Note);
//            }
//        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                logCanceled();
//                dialog.cancel();
//            }
//        });
//        builder.show();
//    }
    //----------------------------------------------------------------------------------------------
 */

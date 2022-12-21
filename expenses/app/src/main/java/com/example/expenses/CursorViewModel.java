package com.example.expenses;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;

/**
 *  a ViewModel that access the scoreDatabase.  This could actually replace the ScoreDatabase class,
 *  but in this place, it just calls scoreDatabase class.
 */

public class CursorViewModel extends AndroidViewModel {
    myDatabase db;
    MutableLiveData<Cursor> myCursor = new MutableLiveData<Cursor>();

    public CursorViewModel(@NonNull Application application) {
        super(application);
        db = new myDatabase(application);
        db.open();
        myCursor.setValue(db.getAllNames());
        db.close();
    }

    public MutableLiveData<Cursor> getData() {
        return myCursor;
    }

    // this uses the Convenience method to add something to the database and then update the cursor.
    public void add(String Name, String Cate, String Date, String Amot, String Note) {
        db.open();
        db.insertName(Name, Cate, Date, Amot, Note);
        myCursor.setValue(db.getAllNames());
        db.close();
    }

    // this uses the Convenience method to update something from the database and then update the cursor.
    public int Update(String TableName, ContentValues values, String selection, String[] selectionArgs) {
        db.open();
        int ret = db.Update(TableName, values, selection, selectionArgs);
        myCursor.setValue(db.getAllNames());
        db.close();
        return ret;
    }

    // this uses the Convenience method to delete something in the database and then update the cursor.
    public int Delete(String TableName, String selection, String[] selectionArgs) {
        db.open();
        int ret = db.Delete(TableName, selection, selectionArgs);
        myCursor.setValue(db.getAllNames());
        db.close();
        return ret;
    }
    @SuppressLint("Range")
    public Cursor Query(String selection){
        String[] projection = {mySQLiteHelper.KEY_ROWID, mySQLiteHelper.KEY_NAME, mySQLiteHelper.KEY_CATE,
                mySQLiteHelper.KEY_DATE, mySQLiteHelper.KEY_AMOT, mySQLiteHelper.KEY_NOTE};
        String helpSelect = mySQLiteHelper.KEY_ROWID + "=" + selection;
        db.open();
        Cursor newCursor = db.qbQuery("Expenses", projection, helpSelect, null, null);
        newCursor.moveToPosition(0); //apparently that works
        db.close();
        return newCursor;
    }
}

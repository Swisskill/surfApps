package com.example.notes;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import java.io.IOException;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;

/**
 * This an accessor class that do all the work in the database.  This is the object that the
 * system uses to access/insert/update/etc the database.
 * <p>
 * This provides a number of methods as examples for different things.
 * It also provides the accessor methods for the content provider as well.
 */

public class myDatabase {


    private final SupportSQLiteOpenHelper helper;
    private SupportSQLiteDatabase db;


    //constructor
    public myDatabase(Context ctx) {
        SupportSQLiteOpenHelper.Factory factory = new FrameworkSQLiteOpenHelperFactory();
        SupportSQLiteOpenHelper.Configuration configuration = SupportSQLiteOpenHelper.Configuration.builder(ctx)
                .name(mySQLiteHelper.DATABASE_NAME)
                .callback(new mySQLiteHelper())
                .build();
        helper = factory.create(configuration);

    }

    //---opens the database---
    public void open() throws SQLException {
        db = helper.getWritableDatabase();
    }

    public void close() {
        try {
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //InsertName is wrapper method, so the activity doesn't have to build  ContentValues for the insert.
    public long insertName(String note) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(mySQLiteHelper.KEY_NOTE, note);

        return db.insert(mySQLiteHelper.TABLE_NAME, CONFLICT_FAIL, initialValues);
    }

    //This is the method to actually do an insert using the convenience method.  Note, it uses fail when there is a conflict.
    public long Insert(String TableName, ContentValues values) {
        return db.insert(TableName, CONFLICT_FAIL, values);
    }
    public Cursor getAllNames() {
        //SELECT KEY_NAME, KEY_SCORE FROM DATABASE_TABLE SORTBY KEY_NAME;
        Cursor mCursor = qbQuery(mySQLiteHelper.TABLE_NAME,   //table name
                new String[]{mySQLiteHelper.KEY_ROWID,mySQLiteHelper.KEY_NOTE},  //projection, ie columns.
                null,  //selection,  we want everything.
                null, // String[] selectionArgs,  again, we want everything.
                null//mySQLiteHelper.KEY_NAME// String sortOrder  by name as the sort.
        );
        if (mCursor != null)  //make sure cursor is not empty!
            mCursor.moveToFirst();
        return mCursor;
    }

    //build a function in cursor view model.
    public Cursor qbQuery(String TableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SupportSQLiteQueryBuilder qb = SupportSQLiteQueryBuilder.builder(TableName);
        qb.columns(projection);
        qb.selection(selection, selectionArgs);
        qb.orderBy(sortOrder);
        //using the query builder to manage the actual query at this point.
        return db.query(qb.create());
    }

    public int Update(String TableName, ContentValues values, String selection, String[] selectionArgs) {
         String helpSelect = mySQLiteHelper.KEY_ROWID + "=" + selection;
        return db.update(TableName, CONFLICT_FAIL, values, helpSelect, selectionArgs);
    }

    public int Delete(String TableName, String selection, String[] selectionArgs) {
        String helpSelect = mySQLiteHelper.KEY_ROWID + "=" + selection;
        return db.delete(TableName, helpSelect, selectionArgs);
    }
}
//-------------------------------------------------------------------------------------------------
/*
// This is a wrapper method, so that main code doesn't have ContentValues.
public boolean updateRow(String name, int amot) {
    ContentValues args = new ContentValues();
    args.put(mySQLiteHelper.KEY_AMOT, amot); //originally had score of course, not amot
    //returns true if one or more updates happened, otherwise false.
    return Update(mySQLiteHelper.TABLE_NAME, args, mySQLiteHelper.KEY_NAME + "= \'" + name + "\'", null) > 0;
}
    //remove all entries from the CurrentBoard
    public void emptydb() {
        db.delete(mySQLiteHelper.TABLE_NAME, null, null);
    }

    //returns true if db is open.  Helper method.
    public boolean isOpen() throws SQLException {
        return db.isOpen();
    }
*/

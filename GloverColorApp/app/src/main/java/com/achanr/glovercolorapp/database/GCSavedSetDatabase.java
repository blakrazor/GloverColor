package com.achanr.glovercolorapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.achanr.glovercolorapp.models.GCSavedSetDataModel;
import com.achanr.glovercolorapp.utility.EGCModeEnum;
import com.achanr.glovercolorapp.utility.GCUtil;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/8/16 1:48 PM
 */
public class GCSavedSetDatabase {

    private SQLiteDatabase mSavedSetDatabase;
    private GCSavedSetDbHelper mSavedSetDbHelper;
    private Context mContext;

    public GCSavedSetDatabase(Context context) {
        mSavedSetDbHelper = new GCSavedSetDbHelper(context);
        mSavedSetDatabase = mSavedSetDbHelper.getWritableDatabase();
        mContext = context;
    }

    public long insertData(GCSavedSetDataModel savedSet) {
        // Gets the data repository in write mode
        mSavedSetDatabase = mSavedSetDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GCSavedSetEntry.COLUMN_NAME_TITLE, savedSet.getTitle());
        values.put(GCSavedSetEntry.COLUMN_NAME_COLORS, GCUtil.convertColorListToShortenedColorString(savedSet.getColors()));
        values.put(GCSavedSetEntry.COLUMN_NAME_MODE, savedSet.getMode().toString());

        // Insert the new row, returning the primary key value of the new row
        return mSavedSetDatabase.insert(GCSavedSetEntry.TABLE_NAME, null, values);
    }

    public ArrayList<GCSavedSetDataModel> readData() {
        ArrayList<GCSavedSetDataModel> savedSetList = new ArrayList<>();
        mSavedSetDatabase = mSavedSetDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                GCSavedSetEntry._ID,
                GCSavedSetEntry.COLUMN_NAME_TITLE,
                GCSavedSetEntry.COLUMN_NAME_COLORS,
                GCSavedSetEntry.COLUMN_NAME_MODE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                GCSavedSetEntry.COLUMN_NAME_TITLE + " ASC";

        Cursor mCursor = mSavedSetDatabase.query(
                GCSavedSetEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        if (mCursor != null) {
            mCursor.moveToFirst();
            do {
                GCSavedSetDataModel savedSet = new GCSavedSetDataModel();
                String title = mCursor.getString(mCursor.getColumnIndex(GCSavedSetEntry.COLUMN_NAME_TITLE));
                String shortenedColorString = mCursor.getString(mCursor.getColumnIndex(GCSavedSetEntry.COLUMN_NAME_COLORS));
                String modeString = mCursor.getString(mCursor.getColumnIndex(GCSavedSetEntry.COLUMN_NAME_MODE));
                savedSet.setTitle(title);
                savedSet.setColors(GCUtil.convertShortenedColorStringToColorList(shortenedColorString));
                savedSet.setMode(EGCModeEnum.valueOf(modeString.toUpperCase()));
                savedSetList.add(savedSet);
            } while (mCursor.moveToNext());
        }

        return savedSetList;
    }

    public boolean deleteData(GCSavedSetDataModel savedSet) {
        mSavedSetDatabase = mSavedSetDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = GCSavedSetEntry.COLUMN_NAME_TITLE + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(savedSet.getTitle())};
        // Issue SQL statement.
        return mSavedSetDatabase.delete(GCSavedSetEntry.TABLE_NAME, selection, selectionArgs) > 0;
    }

    public int updateData(GCSavedSetDataModel oldSavedSet, GCSavedSetDataModel newSavedSet) {
        mSavedSetDatabase = mSavedSetDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(GCSavedSetEntry.COLUMN_NAME_TITLE, newSavedSet.getTitle());
        values.put(GCSavedSetEntry.COLUMN_NAME_COLORS, GCUtil.convertColorListToShortenedColorString(newSavedSet.getColors()));
        values.put(GCSavedSetEntry.COLUMN_NAME_MODE, newSavedSet.getMode().toString());

        // Which row to update, based on the ID
        String selection = GCSavedSetEntry.COLUMN_NAME_TITLE + "=?";
        String[] selectionArgs = {String.valueOf(oldSavedSet.getTitle())};

        return mSavedSetDatabase.update(
                GCSavedSetEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
}

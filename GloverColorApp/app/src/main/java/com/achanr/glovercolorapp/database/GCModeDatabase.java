package com.achanr.glovercolorapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.achanr.glovercolorapp.models.GCMode;

import java.util.ArrayList;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 4:21 PM
 */
public class GCModeDatabase extends GCAbstractDatabase {

    static final String TABLE_NAME = "MODE_TBL";

    private static class GCModeEntry implements BaseColumns {
        static final String MODE_TITLE_KEY = "MODE_TITLE";
    }

    GCModeDatabase(GCDatabaseAdapter databaseAdapter) {
        db_adapter = databaseAdapter;
    }

    @Override
    public void createTable(SQLiteDatabase db) {
        synchronized (GCDatabaseAdapter.Lock) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    TABLE_NAME +
                    " (" + GCModeEntry._ID + " INTEGER PRIMARY KEY," +
                    GCModeEntry.MODE_TITLE_KEY + GCDatabaseHelper.TEXT_TYPE +
                    " );");
        }
    }

    public void insertData(GCMode mode) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GCModeEntry.MODE_TITLE_KEY, mode.getTitle());

        db_adapter.insertEntryInDB(TABLE_NAME, values);
    }

    public void clearTable() {
        db_adapter.clearTable(TABLE_NAME);
    }

    public ArrayList<GCMode> getAllData() {
        ArrayList<GCMode> modeArrayList = new ArrayList<>();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                GCModeEntry._ID,
                GCModeEntry.MODE_TITLE_KEY
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                GCModeEntry._ID + " DESC";

        Cursor mCursor = null;
        try {
            mCursor = db_adapter.getEntryFromDB(
                    TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            if (mCursor != null) {
                if (mCursor.moveToFirst()) {
                    do {
                        String title = mCursor.getString(mCursor.getColumnIndex(GCModeEntry.MODE_TITLE_KEY));

                        modeArrayList.add(new GCMode(title));
                    } while (mCursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e(GCModeDatabase.class.getSimpleName(), e.getMessage());
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
            if (db_adapter.isOpen())
                db_adapter.close();
        }

        return modeArrayList;
    }

    public boolean deleteData(GCMode mode) {
        // Define 'where' part of query.
        String selection = GCModeEntry.MODE_TITLE_KEY + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(mode.getTitle())};
        // Issue SQL statement.
        return db_adapter.deleteRow(TABLE_NAME, selection, selectionArgs) > 0;
    }

    public int updateData(GCMode oldMode, GCMode newMode) {

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(GCModeEntry.MODE_TITLE_KEY, newMode.getTitle());

        // Which row to update, based on the ID
        String selection = GCModeEntry.MODE_TITLE_KEY + "=?";
        String[] selectionArgs = {String.valueOf(oldMode.getTitle())};

        return db_adapter.updateEntryInDB(TABLE_NAME, values, selection, selectionArgs);
    }
}

package com.achanr.glovercolorapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.achanr.glovercolorapp.models.GCChip;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Andrew Chanrasmi
 * @created 3/3/16 3:29 PM
 */
public class GCChipDatabase extends GCAbstractDatabase {

    static final String TABLE_NAME = "CHIP_TBL";

    private static class GCChipEntry implements BaseColumns {
        static final String CHIP_TITLE_KEY = "CHIP_TITLE";
        static final String CHIP_COLORS_KEY = "CHIP_COLORS";
        static final String CHIP_MODES_KEY = "CHIP_MODES";
    }

    GCChipDatabase(GCDatabaseAdapter databaseAdapter) {
        db_adapter = databaseAdapter;
    }

    @Override
    public void createTable(SQLiteDatabase db) {
        synchronized (GCDatabaseAdapter.Lock) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    TABLE_NAME +
                    " (" + GCChipEntry._ID + " INTEGER PRIMARY KEY," +
                    GCChipEntry.CHIP_TITLE_KEY + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCChipEntry.CHIP_COLORS_KEY + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCChipEntry.CHIP_MODES_KEY + GCDatabaseHelper.TEXT_TYPE +
                    " );");
        }
    }

    public void insertData(GCChip chip) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GCChipEntry.CHIP_TITLE_KEY, chip.getTitle());
        values.put(GCChipEntry.CHIP_COLORS_KEY, TextUtils.join(GCDatabaseHelper.COMMA_SEP, chip.getColors()));
        values.put(GCChipEntry.CHIP_MODES_KEY, TextUtils.join(GCDatabaseHelper.COMMA_SEP, chip.getModes()));

        db_adapter.insertEntryInDB(TABLE_NAME, values);
    }

    public void clearTable() {
        db_adapter.clearTable(TABLE_NAME);
    }

    public ArrayList<GCChip> getAllData() {
        ArrayList<GCChip> chipArrayList = new ArrayList<>();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                GCChipEntry._ID,
                GCChipEntry.CHIP_TITLE_KEY,
                GCChipEntry.CHIP_COLORS_KEY,
                GCChipEntry.CHIP_MODES_KEY
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                GCChipEntry._ID + " ASC";

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
                        String title = mCursor.getString(mCursor.getColumnIndex(GCChipEntry.CHIP_TITLE_KEY));
                        String colors = mCursor.getString(mCursor.getColumnIndex(GCChipEntry.CHIP_COLORS_KEY));
                        ArrayList<String> colorsArray = new ArrayList<>(Arrays.asList(colors.split(GCDatabaseHelper.COMMA_SEP)));
                        String modes = mCursor.getString(mCursor.getColumnIndex(GCChipEntry.CHIP_MODES_KEY));
                        ArrayList<String> modesArray = new ArrayList<>(Arrays.asList(modes.split(GCDatabaseHelper.COMMA_SEP)));

                        chipArrayList.add(new GCChip(title, colorsArray, modesArray));
                    } while (mCursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e(GCChipDatabase.class.getSimpleName(), e.getMessage());
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
            if (db_adapter.isOpen())
                db_adapter.close();
        }

        return chipArrayList;
    }

    public boolean deleteData(GCChip chip) {
        // Define 'where' part of query.
        String selection = GCChipEntry.CHIP_TITLE_KEY + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(chip.getTitle())};
        // Issue SQL statement.
        return db_adapter.deleteRow(TABLE_NAME, selection, selectionArgs) > 0;
    }

    public int updateData(GCChip oldChip, GCChip newChip) {

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(GCChipEntry.CHIP_TITLE_KEY, newChip.getTitle());
        values.put(GCChipEntry.CHIP_COLORS_KEY, TextUtils.join(GCDatabaseHelper.COMMA_SEP, newChip.getColors()));
        values.put(GCChipEntry.CHIP_MODES_KEY, TextUtils.join(GCDatabaseHelper.COMMA_SEP, newChip.getModes()));

        // Which row to update, based on the ID
        String selection = GCChipEntry.CHIP_TITLE_KEY + "=?";
        String[] selectionArgs = {String.valueOf(oldChip.getTitle())};

        return db_adapter.updateEntryInDB(TABLE_NAME, values, selection, selectionArgs);
    }
}

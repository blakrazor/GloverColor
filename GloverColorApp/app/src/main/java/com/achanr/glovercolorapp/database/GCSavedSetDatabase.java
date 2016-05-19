package com.achanr.glovercolorapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.achanr.glovercolorapp.common.GCChipUtil;
import com.achanr.glovercolorapp.common.GCConstants;
import com.achanr.glovercolorapp.common.GCModeUtil;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.models.GCSavedSet;

import java.util.ArrayList;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 1/8/16 1:48 PM
 */
public class GCSavedSetDatabase extends GCAbstractDatabase {

    static String TABLE_NAME = "savedsets";
    static final String CREATE_SAVED_SET_DATABASE =
            "CREATE TABLE IF NOT EXISTS " +
                    TABLE_NAME +
                    " (" + GCSavedSetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    GCSavedSetEntry.SAVED_SET_TITLE + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCSavedSetEntry.SAVED_SET_COLORS + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCSavedSetEntry.SAVED_SET_MODE + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCSavedSetEntry.SAVED_SET_CHIP + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCSavedSetEntry.SAVED_SET_CUSTOM_COLORS + GCDatabaseHelper.TEXT_TYPE +
                    " );";

    private Context mContext;

    public static class GCSavedSetEntry implements BaseColumns {
        public static final String SAVED_SET_TITLE = "title";
        public static final String SAVED_SET_COLORS = "colors";
        public static final String SAVED_SET_MODE = "mode";
        public static final String SAVED_SET_CHIP = "chip";
        public static final String SAVED_SET_CUSTOM_COLORS = "custom_colors";
    }

    public GCSavedSetDatabase(Context context, GCDatabaseAdapter databaseAdapter) {
        mContext = context;
        GCSavedSetDatabase.db_adapter = databaseAdapter;
    }

    @Override
    public void createTable(SQLiteDatabase db) {
        synchronized (GCDatabaseAdapter.Lock) {
            db.execSQL(CREATE_SAVED_SET_DATABASE);
        }
    }

    public long insertData(GCSavedSet savedSet) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GCSavedSetEntry.SAVED_SET_TITLE, savedSet.getTitle());
        values.put(GCSavedSetEntry.SAVED_SET_COLORS, GCUtil.convertColorListToShortenedColorString(savedSet.getColors()));
        values.put(GCSavedSetEntry.SAVED_SET_MODE, savedSet.getMode().getTitle());
        values.put(GCSavedSetEntry.SAVED_SET_CHIP, savedSet.getChipSet().getTitle());
        values.put(GCSavedSetEntry.SAVED_SET_CUSTOM_COLORS, GCUtil.convertCustomColorArrayToString(savedSet.getCustomColors()));

        return db_adapter.insertEntryInDB(TABLE_NAME, values);
    }

    public void clearTable() {
        db_adapter.clearTable(TABLE_NAME);
    }

    public ArrayList<GCSavedSet> getAllData() {
        ArrayList<GCSavedSet> savedSetList = new ArrayList<>();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                GCSavedSetEntry._ID,
                GCSavedSetEntry.SAVED_SET_TITLE,
                GCSavedSetEntry.SAVED_SET_COLORS,
                GCSavedSetEntry.SAVED_SET_MODE,
                GCSavedSetEntry.SAVED_SET_CHIP,
                GCSavedSetEntry.SAVED_SET_CUSTOM_COLORS
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                GCSavedSetEntry.SAVED_SET_TITLE + " COLLATE NOCASE";

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
                        GCSavedSet savedSet = new GCSavedSet();

                        int id = mCursor.getInt(mCursor.getColumnIndex(GCSavedSetEntry._ID));
                        String title = mCursor.getString(mCursor.getColumnIndex(GCSavedSetEntry.SAVED_SET_TITLE));
                        String shortenedColorString = mCursor.getString(mCursor.getColumnIndex(GCSavedSetEntry.SAVED_SET_COLORS));
                        String modeString = mCursor.getString(mCursor.getColumnIndex(GCSavedSetEntry.SAVED_SET_MODE));
                        String chipString = mCursor.getString(mCursor.getColumnIndex(GCSavedSetEntry.SAVED_SET_CHIP));
                        String customColorString = mCursor.getString(mCursor.getColumnIndex(GCSavedSetEntry.SAVED_SET_CUSTOM_COLORS));

                        savedSet.setId(id);
                        savedSet.setTitle(title);
                        savedSet.setColors(GCUtil.convertShortenedColorStringToColorList(shortenedColorString));
                        savedSet.setMode(GCModeUtil.getModeUsingTitle(modeString.toUpperCase()));
                        if (customColorString != null) {
                            savedSet.setCustomColors(GCUtil.convertStringToCustomColorArray(customColorString));
                        } else {
                            ArrayList<int[]> customColors = new ArrayList<>();
                            for (int i = 0; i < GCConstants.MAX_COLORS; i++) {
                                customColors.add(new int[]{255, 255, 255});
                            }
                            savedSet.setCustomColors(customColors);
                        }
                        if (chipString != null) {
                            savedSet.setChipSet(GCChipUtil.getChipUsingTitle(chipString.toUpperCase()));
                        } else {
                            savedSet.setChipSet(GCChipUtil.getChipUsingTitle("NONE"));
                        }

                        savedSetList.add(savedSet);
                    } while (mCursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e(GCSavedSetDatabase.class.getSimpleName(), e.getMessage());
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
            if (db_adapter.isOpen())
                db_adapter.close();
        }

        return savedSetList;
    }

    public boolean deleteData(GCSavedSet savedSet) {
        // Define 'where' part of query.
        String selection = GCSavedSetEntry.SAVED_SET_TITLE + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(savedSet.getTitle())};
        // Issue SQL statement.
        return db_adapter.deleteRow(TABLE_NAME, selection, selectionArgs) > 0;
    }

    public int updateData(GCSavedSet oldSavedSet, GCSavedSet newSavedSet) {

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(GCSavedSetEntry.SAVED_SET_TITLE, newSavedSet.getTitle());
        values.put(GCSavedSetEntry.SAVED_SET_COLORS, GCUtil.convertColorListToShortenedColorString(newSavedSet.getColors()));
        values.put(GCSavedSetEntry.SAVED_SET_MODE, newSavedSet.getMode().getTitle());
        values.put(GCSavedSetEntry.SAVED_SET_CHIP, newSavedSet.getChipSet().getTitle());
        values.put(GCSavedSetEntry.SAVED_SET_CUSTOM_COLORS, GCUtil.convertCustomColorArrayToString(newSavedSet.getCustomColors()));

        // Which row to update, based on the ID
        String selection = GCSavedSetEntry.SAVED_SET_TITLE + "=?";
        String[] selectionArgs = {String.valueOf(oldSavedSet.getTitle())};

        return db_adapter.updateEntryInDB(TABLE_NAME, values, selection, selectionArgs);
    }
}

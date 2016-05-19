package com.achanr.glovercolorapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.models.GCCollection;

import java.util.ArrayList;

/**
 * @author Andrew Chanrasmi
 * @created 5/19/16 3:48 PM
 */
public class GCCollectionDatabase extends GCAbstractDatabase {

    static String TABLE_NAME = "COLLECTION_TBL";
    static final String CREATE_COLLECTION_DATABASE =
            "CREATE TABLE IF NOT EXISTS " +
                    TABLE_NAME +
                    " (" + GCCollectionEntry._ID + " INTEGER PRIMARY KEY," +
                    GCCollectionEntry.COLLECTION_TITLE_KEY + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCCollectionEntry.COLLECTION_DESC_KEY + GCDatabaseHelper.TEXT_TYPE + GCDatabaseHelper.COMMA_SEP +
                    GCCollectionEntry.COLLECTION_SETS_KEY + GCDatabaseHelper.TEXT_TYPE +
                    " );";

    private Context mContext;

    public static class GCCollectionEntry implements BaseColumns {
        public static final String COLLECTION_TITLE_KEY = "COLLECTION_TITLE";
        public static final String COLLECTION_DESC_KEY = "COLLECTION_DESC";
        public static final String COLLECTION_SETS_KEY = "COLLECTION_SETS";
    }

    public GCCollectionDatabase(Context context, GCDatabaseAdapter databaseAdapter) {
        mContext = context;
        GCCollectionDatabase.db_adapter = databaseAdapter;
    }

    @Override
    public void createTable(SQLiteDatabase db) {
        synchronized (GCDatabaseAdapter.Lock) {
            db.execSQL(CREATE_COLLECTION_DATABASE);
        }
    }

    public long insertData(GCCollection collection) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(GCCollectionEntry.COLLECTION_TITLE_KEY, collection.getTitle());
        values.put(GCCollectionEntry.COLLECTION_DESC_KEY, collection.getDescription());
        values.put(GCCollectionEntry.COLLECTION_SETS_KEY, GCUtil.convertSetListToString(collection.getSavedSetList()));

        return db_adapter.insertEntryInDB(TABLE_NAME, values);
    }

    public void clearTable() {
        db_adapter.clearTable(TABLE_NAME);
    }

    public ArrayList<GCCollection> getAllData() {
        ArrayList<GCCollection> collectionArrayList = new ArrayList<>();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                GCCollectionEntry._ID,
                GCCollectionEntry.COLLECTION_TITLE_KEY,
                GCCollectionEntry.COLLECTION_DESC_KEY,
                GCCollectionEntry.COLLECTION_SETS_KEY
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                GCCollectionEntry.COLLECTION_TITLE_KEY + " COLLATE NOCASE";

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
                        GCCollection collection = new GCCollection();

                        String title = mCursor.getString(mCursor.getColumnIndex(GCCollectionEntry.COLLECTION_TITLE_KEY));
                        String desc = mCursor.getString(mCursor.getColumnIndex(GCCollectionEntry.COLLECTION_DESC_KEY));
                        String setListString = mCursor.getString(mCursor.getColumnIndex(GCCollectionEntry.COLLECTION_SETS_KEY));

                        collection.setTitle(title);
                        collection.setDescription(desc);
                        collection.setSavedSetList(GCUtil.convertStringToSetList(setListString));

                        collectionArrayList.add(collection);
                    } while (mCursor.moveToNext());
                }
            }
        } catch (Exception e) {
            Log.e(GCCollectionDatabase.class.getSimpleName(), e.getMessage());
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
            if (db_adapter.isOpen())
                db_adapter.close();
        }

        return collectionArrayList;
    }

    public boolean deleteData(GCCollection collection) {
        // Define 'where' part of query.
        String selection = GCCollectionEntry.COLLECTION_TITLE_KEY + "=?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(collection.getTitle())};
        // Issue SQL statement.
        return db_adapter.deleteRow(TABLE_NAME, selection, selectionArgs) > 0;
    }

    public int updateData(GCCollection oldCollection, GCCollection newCollection) {

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(GCCollectionEntry.COLLECTION_TITLE_KEY, newCollection.getTitle());

        // Which row to update, based on the ID
        String selection = GCCollectionEntry.COLLECTION_TITLE_KEY + "=?";
        String[] selectionArgs = {String.valueOf(oldCollection.getTitle())};

        return db_adapter.updateEntryInDB(TABLE_NAME, values, selection, selectionArgs);
    }
}
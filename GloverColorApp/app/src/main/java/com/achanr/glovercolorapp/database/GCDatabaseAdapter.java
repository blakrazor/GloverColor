package com.achanr.glovercolorapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 3/1/16 11:01 AM
 */
class GCDatabaseAdapter {

    static final String Lock = "dblock";
    private static SQLiteDatabase mSQLDb = null;
    private final GCDatabaseHelper mDbHelper;

    GCDatabaseAdapter(GCDatabaseHelper dbHelper) {
        mDbHelper = dbHelper;
    }

    public SQLiteDatabase getDatabase() {
        return mSQLDb;
    }

    private void open() throws SQLException {
        synchronized (GCDatabaseAdapter.Lock) {
            if (mSQLDb == null || !mSQLDb.isOpen())
                mSQLDb = mDbHelper.getWritableDatabase();
        }
    }

    void close() {
        synchronized (GCDatabaseAdapter.Lock) {
            mDbHelper.close();
        }
    }

    public boolean isOpen() {
        return mSQLDb != null && mSQLDb.isOpen();
    }

    public void Close() {
        synchronized (GCDatabaseAdapter.Lock) {
            if (isOpen()) close();
        }
    }

    void insertEntryInDB(String tableName, ContentValues initialValues) {
        synchronized (GCDatabaseAdapter.Lock) {
            open();
            mSQLDb.insert(tableName, null, initialValues);
            close();
        }
    }

    Cursor getEntryFromDB(String tableName, String[] columns, String selection, String[] selectionArgs,
                                 String groupBy, String having, String orderBy) {
        synchronized (GCDatabaseAdapter.Lock) {
            open();
            Cursor c = mSQLDb.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
            if (c != null) {
                c.moveToFirst();
            }
            return c;
        }
    }

    public void clearTable(String tableName) {
        synchronized (GCDatabaseAdapter.Lock) {
            open();
            mSQLDb.execSQL("delete from " + tableName);
            close();
        }
    }

    int deleteRow(String tableName, String selection, String[] whereArgs) {
        synchronized (GCDatabaseAdapter.Lock) {
            int retValue;
            open();
            retValue = mSQLDb.delete(tableName, selection, whereArgs);
            close();
            return retValue;
        }
    }

    int updateEntryInDB(String tableName, ContentValues updatedValues, String selection, String[] whereArgs) {
        synchronized (GCDatabaseAdapter.Lock) {
            int retValue;
            open();
            retValue = mSQLDb.update(tableName, updatedValues, selection, whereArgs);
            close();
            return retValue;
        }
    }
}

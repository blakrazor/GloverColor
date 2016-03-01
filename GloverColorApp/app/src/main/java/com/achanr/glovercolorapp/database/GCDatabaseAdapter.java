package com.achanr.glovercolorapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 3/1/16 11:01 AM
 */
public class GCDatabaseAdapter {

    public static String Lock = "dblock";
    private static GCDatabaseAdapter dbInstance;
    private static Context mContext;
    private static SQLiteDatabase mSQLDb = null;
    private GCDatabaseHelper mDbHelper;

    public static synchronized GCDatabaseAdapter getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new GCDatabaseAdapter(context);
        }

        return dbInstance;
    }

    private GCDatabaseAdapter(Context context) {
        mContext = context;
        if (mDbHelper == null)
            mDbHelper = new GCDatabaseHelper(mContext, GCDatabaseHelper.DATABASE_NAME, null,
                    GCDatabaseHelper.DATABASE_VERSION, this);
    }

    public SQLiteDatabase getDatabase() {
        return mSQLDb;
    }

    public GCDatabaseAdapter open() throws SQLException {
        synchronized (GCDatabaseAdapter.Lock) {
            if (mSQLDb == null || !mSQLDb.isOpen())
                mSQLDb = mDbHelper.getWritableDatabase();
            return this;
        }
    }

    public void close() {
        synchronized (GCDatabaseAdapter.Lock) {
            mDbHelper.close();
        }
    }

    public boolean isOpen() {
        return mSQLDb != null && mSQLDb.isOpen();
    }

    public static void Close() {
        synchronized (GCDatabaseAdapter.Lock) {
            if (dbInstance != null && dbInstance.isOpen())
                dbInstance.close();
            dbInstance = null;
        }
    }

    public long insertEntryInDB(String tableName, ContentValues initialValues) {
        synchronized (GCDatabaseAdapter.Lock) {
            long retValue;
            open();
            retValue = mSQLDb.insert(tableName, null, initialValues);
            close();
            return retValue;
        }
    }

    public Cursor getEntryFromDB(String tableName, String[] columns, String selection, String[] selectionArgs,
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

    public int deleteRow(String tableName, String selection, String[] whereArgs) {
        synchronized (GCDatabaseAdapter.Lock) {
            int retValue;
            open();
            retValue = mSQLDb.delete(tableName, selection, whereArgs);
            close();
            return retValue;
        }
    }

    public int updateEntryInDB(String tableName, ContentValues updatedValues, String selection, String[] whereArgs) {
        synchronized (GCDatabaseAdapter.Lock) {
            int retValue;
            open();
            retValue = mSQLDb.update(tableName, updatedValues, selection, whereArgs);
            close();
            return retValue;
        }
    }
}

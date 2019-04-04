package com.example.rodrigo.tourguide.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Locale;

public class BusinessListsDbHelper extends SQLiteOpenHelper {
    private static BusinessListsDbHelper sInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BusinessLists.db";

    private static final int NUMBER_OF_TABLES = 4;
    private static final String SQL_CREATE_ENTRY =
            "CREATE TABLE " + BusinessListsContract.BusinessListsEntry.TABLE_NAME + "%d" + " (" +
                    BusinessListsContract.BusinessListsEntry._ID + " INTEGER PRIMARY KEY," +
                    BusinessListsContract.BusinessListsEntry.COLUMN_NAME_NAME + " TEXT," +
                    BusinessListsContract.BusinessListsEntry.COLUMN_NAME_REVIEW_COUNT + " INTEGER," +
                    BusinessListsContract.BusinessListsEntry.COLUMN_NAME_RATING + " REAL," +
                    BusinessListsContract.BusinessListsEntry.COLUMN_NAME_URL + " TEXT," +
                    BusinessListsContract.BusinessListsEntry.COLUMN_NAME_PHOTO + " BLOB)";

    private static final String SQL_DELETE_ENTRY =
            "DROP TABLE IF EXISTS " + BusinessListsContract.BusinessListsEntry.TABLE_NAME + "%d";

    public static synchronized BusinessListsDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new BusinessListsDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public static void resetInstance() {
        sInstance = null;
    }

    public static boolean isInstanceNull() {
        return (sInstance == null);
    }

    private BusinessListsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < NUMBER_OF_TABLES; i++)
            db.execSQL(String.format(Locale.US, SQL_CREATE_ENTRY, i));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = 0; i < NUMBER_OF_TABLES; i++)
            db.execSQL(String.format(Locale.US, SQL_DELETE_ENTRY, i));

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

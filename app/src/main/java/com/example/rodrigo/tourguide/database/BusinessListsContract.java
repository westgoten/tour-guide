package com.example.rodrigo.tourguide.database;

import android.provider.BaseColumns;

public final class BusinessListsContract {
    private BusinessListsContract() {}

    public static class BusinessListsEntry implements BaseColumns {
        public static final String TABLE_NAME = "businesses";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_REVIEW_COUNT = "review_count";
        public static final String COLUMN_NAME_RATING = "rating";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_PHOTO = "photo";
    }
}

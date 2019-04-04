package com.example.rodrigo.tourguide.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import com.example.rodrigo.tourguide.AttractionListFragment;
import com.example.rodrigo.tourguide.MainActivityViewModel;
import com.example.rodrigo.tourguide.database.BusinessListsContract;
import com.example.rodrigo.tourguide.database.BusinessListsDbHelper;
import com.example.rodrigo.tourguide.models.Business;
import com.example.rodrigo.tourguide.models.BusinessSearch;
import retrofit2.Call;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BusinessManager {
    private int totalOfBusinessesDownloaded;
    private int totalOfPhotosDecoded;

    private final BlockingQueue<Runnable> businessPhotoDownloadWorkQueue;
    private final BlockingQueue<Runnable> businessListDownloadWorkQueue;
    private final BlockingQueue<Runnable> businessPhotoDecodeWorkQueue;

    private final ThreadPoolExecutor businessPhotoDownloadThreadPool;
    private final ThreadPoolExecutor businessListDownloadThreadPool;
    private final ThreadPoolExecutor businessPhotoDecodeThreadPool;

    private static BusinessManager sInstance;

    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
    private static final int NUMBER_OF_CORES;

    private static final String TAG = "BusinessManager";

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        sInstance = new BusinessManager();
    }

    private BusinessManager() {
        businessPhotoDownloadWorkQueue = new LinkedBlockingQueue<>();
        businessListDownloadWorkQueue = new LinkedBlockingQueue<>();
        businessPhotoDecodeWorkQueue = new LinkedBlockingQueue<>();

        businessPhotoDownloadThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, businessPhotoDownloadWorkQueue);

        businessListDownloadThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, businessListDownloadWorkQueue);

        businessPhotoDecodeThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, businessPhotoDecodeWorkQueue);
    }

    public static BusinessManager getInstance() {
        return sInstance;
    }

    public void startBusinessListDownload(Call<BusinessSearch> businessSearchCall,
                                     AttractionListFragment.AttractionType attractionType,
                                     MainActivityViewModel viewModel) {
        businessListDownloadThreadPool.execute(new BusinessListDownloadRunnable(businessSearchCall, attractionType,
                viewModel));
    }

    private void startBusinessPhotoDownload(MainActivityViewModel viewModel) {
        Map<AttractionListFragment.AttractionType, List<Business>> businessMatrix = viewModel.getBusinessMatrix();
        for (List<Business> businesses : businessMatrix.values()) {
            totalOfBusinessesDownloaded += businesses.size();
            Iterator<Business> iterator = businesses.iterator();
            while (iterator.hasNext()) {
                Business business = iterator.next();
                if (business.getName().equalsIgnoreCase("Salvador")) {
                    iterator.remove();
                    totalOfBusinessesDownloaded--;
                } else {
                    businessPhotoDownloadThreadPool.execute(new BusinessPhotoDownloadRunnable(business));
                }
            }
        }
    }

    private void writeBusinessListsToDatabase(final MainActivityViewModel viewModel, Context context) {
        final BusinessListsDbHelper dbHelper = BusinessListsDbHelper.getInstance(context);

        new Thread(new Runnable() {
            private SQLiteDatabase db;
            private Map<AttractionListFragment.AttractionType, List<Business>> businessMatrix;

            @Override
            public void run() {
                db = dbHelper.getWritableDatabase();
                businessMatrix = viewModel.getBusinessMatrix();
                insertIntoDatabase();

                dbHelper.close();
                BusinessListsDbHelper.resetInstance();
            }

            private void insertIntoDatabase() {
                ContentValues values = new ContentValues();

                db.beginTransaction();
                try {
                    for (AttractionListFragment.AttractionType attractionType : businessMatrix.keySet()) {
                        List<Business> businesses = businessMatrix.get(attractionType);
                        for (Business business : businesses) {
                            values.put(BusinessListsContract.BusinessListsEntry.COLUMN_NAME_NAME, business.getName());
                            values.put(BusinessListsContract.BusinessListsEntry.COLUMN_NAME_REVIEW_COUNT, business.getReview_count());
                            values.put(BusinessListsContract.BusinessListsEntry.COLUMN_NAME_RATING, business.getRating());
                            values.put(BusinessListsContract.BusinessListsEntry.COLUMN_NAME_URL, business.getUrl());

                            byte[] imgBytes = convertBitmapToByteArray(business.getBusinessPhoto());
                            values.put(BusinessListsContract.BusinessListsEntry.COLUMN_NAME_PHOTO, imgBytes);

                            db.insert(BusinessListsContract.BusinessListsEntry.TABLE_NAME + attractionType.ordinal(),
                                    null, values);
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            private void updateDatabase() {
                // TO DO
            }

            private byte[] convertBitmapToByteArray(Bitmap bitmap) {
                if (bitmap != null) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    return outputStream.toByteArray();
                }
                return null;
            }
        }).start();
    }

    public void fetchBusinessListsFromDatabase(final MainActivityViewModel viewModel, Context context) {
        final BusinessListsDbHelper dbHelper = BusinessListsDbHelper.getInstance(context);
        viewModel.areRequestsDone().postValue(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String[] columns = {BusinessListsContract.BusinessListsEntry.COLUMN_NAME_NAME,
                        BusinessListsContract.BusinessListsEntry.COLUMN_NAME_REVIEW_COUNT,
                        BusinessListsContract.BusinessListsEntry.COLUMN_NAME_RATING,
                        BusinessListsContract.BusinessListsEntry.COLUMN_NAME_URL,
                        BusinessListsContract.BusinessListsEntry.COLUMN_NAME_PHOTO};

                String sortOrder = BusinessListsContract.BusinessListsEntry._ID + " ASC";

                db.beginTransaction();
                try {
                    for (AttractionListFragment.AttractionType attractionType : AttractionListFragment.AttractionType.values()) {
                        Cursor cursor = db.query(BusinessListsContract.BusinessListsEntry.TABLE_NAME + attractionType.ordinal(),
                                columns,
                                null,
                                null,
                                null,
                                null,
                                sortOrder);

                        if (cursor.getCount() == 0) {
                            cursor.close();
                            viewModel.isDatabaseEmpty().postValue(true);
                            break;
                        }

                        List<Business> businesses = new ArrayList<>();
                        totalOfPhotosDecoded += cursor.getCount();

                        while (cursor.moveToNext()) {
                            String name = cursor.getString(cursor.getColumnIndexOrThrow(BusinessListsContract.BusinessListsEntry
                                    .COLUMN_NAME_NAME));
                            int reviewCount = cursor.getInt(cursor.getColumnIndexOrThrow(BusinessListsContract.BusinessListsEntry
                                    .COLUMN_NAME_REVIEW_COUNT));
                            float rating = cursor.getFloat(cursor.getColumnIndexOrThrow(BusinessListsContract.BusinessListsEntry
                                    .COLUMN_NAME_RATING));
                            String url = cursor.getString(cursor.getColumnIndexOrThrow(BusinessListsContract.BusinessListsEntry
                                    .COLUMN_NAME_URL));
                            byte[] imgBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(BusinessListsContract.BusinessListsEntry
                                    .COLUMN_NAME_PHOTO));

                            Business business = new Business(name, reviewCount, rating, url, null);
                            businesses.add(business);

                            businessPhotoDecodeThreadPool.execute(new BusinessPhotoDecodeRunnable(business, imgBytes));
                        }
                        viewModel.getBusinessMatrix().put(attractionType, businesses);

                        cursor.close();
                    }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                    keepTrackOfDatabaseFetching(viewModel);
                }
            }
        }).start();
    }

    public void keepTrackOfRequests(final MainActivityViewModel viewModel, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean running = true;
                while (running) {
                    if (businessListDownloadThreadPool.getCompletedTaskCount() == AttractionListFragment.AttractionType
                            .values().length)
                        running = false;
                }
                startBusinessPhotoDownload(viewModel);

                running = true;
                while (running) {
                    if (businessPhotoDownloadThreadPool.getCompletedTaskCount() == totalOfBusinessesDownloaded)
                        running = false;
                }

                if (!viewModel.isOffline()) {
                    viewModel.areRequestsDone().postValue(true);
                    writeBusinessListsToDatabase(viewModel, context);
                    resetInstance();
                } else
                    fetchBusinessListsFromDatabase(viewModel, context);
            }
        }).start();
    }

    private void keepTrackOfDatabaseFetching(final MainActivityViewModel viewModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean running = true;
                while (running) {
                    if (businessPhotoDecodeThreadPool.getCompletedTaskCount() == totalOfPhotosDecoded)
                        running = false;
                }

                viewModel.areRequestsDone().postValue(true);
                resetInstance();
            }
        }).start();
    }

    private void resetInstance() {
        sInstance = new BusinessManager();
    }
}

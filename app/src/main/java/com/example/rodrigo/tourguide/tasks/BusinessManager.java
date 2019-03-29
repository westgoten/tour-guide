package com.example.rodrigo.tourguide.tasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.rodrigo.tourguide.AttractionListFragment;
import com.example.rodrigo.tourguide.MainActivityViewModel;
import com.example.rodrigo.tourguide.database.BusinessListsContract;
import com.example.rodrigo.tourguide.database.BusinessListsDbHelper;
import com.example.rodrigo.tourguide.models.Business;
import com.example.rodrigo.tourguide.models.BusinessSearch;
import retrofit2.Call;

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
    private boolean isFetchingDone;

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

    public void fetchBusinessListsFromDatabase(final MainActivityViewModel viewModel, Context context) {
        final BusinessListsDbHelper dbHelper = new BusinessListsDbHelper(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                isFetchingDone = false;
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String[] columns = {BusinessListsContract.BusinessListsEntry.COLUMN_NAME_NAME,
                        BusinessListsContract.BusinessListsEntry.COLUMN_NAME_REVIEW_COUNT,
                        BusinessListsContract.BusinessListsEntry.COLUMN_NAME_RATING,
                        BusinessListsContract.BusinessListsEntry.COLUMN_NAME_URL,
                        BusinessListsContract.BusinessListsEntry.COLUMN_NAME_PHOTO};

                String sortOrder = BusinessListsContract.BusinessListsEntry._ID + "ASC";

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
                    isFetchingDone = true;
                }
            }
        }).start();
    }

    public void keepTrackOfRequests(final MainActivityViewModel viewModel) {
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

                viewModel.areRequestsDone().postValue(true);
                resetInstance();
            }
        }).start();
    }

    public void keepTrackOfDatabaseFetching(final MainActivityViewModel viewModel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean running = true;
                while (running) {
                    if (isFetchingDone && businessPhotoDecodeThreadPool.getCompletedTaskCount() == totalOfPhotosDecoded)
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

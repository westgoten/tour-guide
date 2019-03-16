package com.example.rodrigo.tourguide;

import com.example.rodrigo.tourguide.models.Business;
import com.example.rodrigo.tourguide.models.BusinessSearch;
import com.example.rodrigo.tourguide.tasks.BusinessListDownloadRunnable;
import com.example.rodrigo.tourguide.tasks.BusinessPhotoDownloadRunnable;
import retrofit2.Call;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BusinessManager {
    private int totalOfBusinessesDownloaded;

    private final BlockingQueue<Runnable> businessPhotoDownloadWorkQueue;
    private final BlockingQueue<Runnable> businessListDownloadWorkQueue;
    private final ThreadPoolExecutor businessPhotoDownloadThreadPool;
    private final ThreadPoolExecutor businessListDownloadThreadPool;

    private static BusinessManager sInstance;

    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;
    private static final int NUMBER_OF_CORES;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        sInstance = new BusinessManager();
    }

    private BusinessManager() {
        businessPhotoDownloadWorkQueue = new LinkedBlockingQueue<>();
        businessListDownloadWorkQueue = new LinkedBlockingQueue<>();

        businessPhotoDownloadThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, businessPhotoDownloadWorkQueue);

        businessListDownloadThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, businessListDownloadWorkQueue);
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

    public void startBusinessPhotoDownload(MainActivityViewModel viewModel) {
        Map<AttractionListFragment.AttractionType, Business[]> businessMatrix = viewModel.getBusinessMatrix();
        for (Business[] businesses : businessMatrix.values()) {
            for (Business business : businesses) {
                totalOfBusinessesDownloaded++;
                businessPhotoDownloadThreadPool.execute(new BusinessPhotoDownloadRunnable(business));
            }
        }
    }

    public long getBusinessPhotoThreadPoolCompletedTasks() {
        return businessPhotoDownloadThreadPool.getCompletedTaskCount();
    }

    public long getBusinessListThreadPoolCompletedTasks() {
        return businessListDownloadThreadPool.getCompletedTaskCount();
    }

    public int getTotalOfBusinessesDownloaded() {
        return totalOfBusinessesDownloaded;
    }
}

package ve.com.abicelis.planetracker.ui.home;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.data.model.TripViewModel;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;

/**
 * Created by abicelis on 4/9/2017.
 */

public class HomePresenter extends BasePresenter<HomeMvpView> {

    private DataManager mDataManager;

    public HomePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }


    public void refreshTripList(@Nullable String filter) {
        getMvpView().showLoading();
        mDataManager.getTrips(filter)
                .delay(700, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trips -> {
                    getMvpView().showTrips(trips);
//                    for (TripViewModel tvm : trips)
//                        if(tvm.getTripViewModelType() == TripViewModel.TripViewModelType.TRIP)
//                            Timber.d("Trip! %s", tvm.getTrip());
                }, throwable -> {
                    getMvpView().showMessage(Message.ERROR_LOADING_TRIPS, null);
                    Timber.e(throwable, "Error loading trips");
                });
    }

    public void deleteTrip(Trip trip) {
        new AsyncTask<Trip, Void, Long>() {
            @Override
            protected Long doInBackground(Trip... trips) {
                return mDataManager.deleteTrip(trips[0]);
            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);
                refreshTripList(null);
            }

        }.execute(trip);
    }
}

package ve.com.abicelis.planetracker.ui.home;

import android.os.AsyncTask;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.DataManager;
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


    public void refreshTripList() {
        getMvpView().showLoading();
        mDataManager.getTrips()
                .delay(1500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trips -> {
                    getMvpView().showTrips(trips);
                    for (TripViewModel tvm : trips)
                        if(tvm.getTripViewModelType() == TripViewModel.TripViewModelType.TRIP)
                            Timber.d("Trip! %s", tvm.getTrip());
                }, throwable -> {
                    getMvpView().showMessage(Message.ERROR_LOADING_TRIPS, null);
                });
    }

    public void insertFakeTrip(){
        MyTask task = new MyTask();
        task.execute();
    }


    class MyTask extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... voids) {
            mDataManager.insertFakeTrips();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            refreshTripList();
        }
    }

}

package ve.com.abicelis.planetracker.ui.tripdetail;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.FlightViewModel;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;

/**
 * Created by abicelis on 18/9/2017.
 */

public class TripDetailPresenter extends BasePresenter<TripDetailMvpView> {

    //DATA
    private DataManager mDataManager;
    private Trip mTrip;

    public TripDetailPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }


    public void getTrip(long tripId) {
        mDataManager.getTrip(tripId, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trip -> {
                    mTrip = trip;
                    if (mTrip.getFlights() != null && mTrip.getFlights().size() > 0)
                        new GenerateFlightHeadersTask().execute(mTrip.getFlights().toArray(new Flight[mTrip.getFlights().size()]));
                    else
                        getMvpView().showNoFlights();

                }, throwable -> {
                    Timber.e(throwable, "Error getting trip from DB, ID=%d", tripId);
                    getMvpView().showMessage(Message.ERROR_LOADING_TRIP, null);
                });
    }




    private class GenerateFlightHeadersTask extends AsyncTask<Flight, Void, List<FlightViewModel>> {
        @Override
        protected List<FlightViewModel> doInBackground(Flight... flights) {
            List<FlightViewModel> flightVM = new ArrayList<>();

            Flight lastFlight = null;
            for (Flight f : flights) {
                if(lastFlight != null) {
                    if(f.getOrigin().equals(lastFlight.getDestination())) {
                        flightVM.add(new FlightViewModel(lastFlight.getDestination().getCity(), lastFlight.getArrival(), f.getDeparture()));
                    }
                }
                flightVM.add(new FlightViewModel(f));
                lastFlight = f;
            }
            return flightVM;
        }

        @Override
        protected void onPostExecute(List<FlightViewModel> list) {
            super.onPostExecute(list);
            getMvpView().showTrip(mTrip, list);
        }
    }

}

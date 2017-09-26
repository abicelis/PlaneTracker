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
    private long mTripId = -1;
    private Trip mTrip;
    private boolean mTripHasFlights;
    private boolean mIsInEditMode;

    public TripDetailPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void setTripId(long tripId) {
        mTripId = tripId;
    }

    public void reloadTrip() {
        if (mTripId != -1) {
            mDataManager.getTrip(mTripId, true)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(trip -> {
                        mTrip = trip;
                        if (mTrip.getFlights() != null) {
                            mTripHasFlights = (mTrip.getFlights().size() > 0);
                            new GenerateFlightHeadersTask().execute(mTrip.getFlights().toArray(new Flight[mTrip.getFlights().size()]));
                        } else {
                            Timber.e("Error getting trip from DB, ID=%d", mTripId);
                            getMvpView().showMessage(Message.ERROR_LOADING_TRIP, null);
                        }
                    }, throwable -> {
                        Timber.e(throwable, "Error getting trip from DB, ID=%d", mTripId);
                        getMvpView().showMessage(Message.ERROR_LOADING_TRIP, null);
                    });
        } else
            Timber.e("Cant reload trip, tripId not set!");
    }

    public boolean tripHasFlights() {
        return mTripHasFlights;
    }

    public void discardEditModeChanges() {
        editModeToggled();
        reloadTrip();
    }



    public void editModeToggled() {
        mIsInEditMode = !mIsInEditMode;

        if(mIsInEditMode) {
            getMvpView().activateEditMode();
        } else {
            getMvpView().deactivateEditMode();
        }
    }
    public boolean isInEditMode(){
        return mIsInEditMode;
    }

    public Trip getLoadedTrip() {
        return mTrip;
    }

    public void changeTripName(String tripName) {
        mTrip.setName(tripName);
        new SaveTripTask().execute(mTrip);
    }

    public void reloadTripImage() {
        mDataManager.getTripImage(mTrip.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(image -> {
                    mTrip.setImage(image);
                    getMvpView().reloadTripImage(image);
                }, throwable -> {
                    Timber.e(throwable, "Error getting image of trip from DB, ID=%d", mTrip.getId());
                    getMvpView().showMessage(Message.ERROR_LOADING_IMAGE, null);
                });
    }


    public void deleteFlight(Flight flight) {
        new AsyncTask<Flight, Void, Long>() {
            @Override
            protected Long doInBackground(Flight... flights) {
                return mDataManager.deleteFlight(flights[0]);
            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);
                reloadTrip();
                editModeToggled();
            }

        }.execute(flight);
    }

    public void deleteTrip() {
        new AsyncTask<Trip, Void, Long>() {
            @Override
            protected Long doInBackground(Trip... trips) {
                return mDataManager.deleteTrip(trips[0]);
            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);
                getMvpView().tripDeletedSoFinish();
            }

        }.execute(mTrip);
    }

    private class SaveTripTask extends AsyncTask<Trip, Void, Void> {
        @Override
        protected Void doInBackground(Trip... trips) {
            mDataManager.saveTrip(trips[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getMvpView().reloadTripName(mTrip.getName());
        }
    }
    private class GenerateFlightHeadersTask extends AsyncTask<Flight, Void, List<FlightViewModel>> {
        @Override
        protected List<FlightViewModel> doInBackground(Flight... flights) {
            List<FlightViewModel> flightVM = new ArrayList<>();

            flightVM.add(new FlightViewModel());    //Add start header, which will show only when edit mode is active

            Flight lastFlight = null;
            for (Flight f : flights) {
                if(lastFlight != null) {
                    if(f.getOrigin().equals(lastFlight.getDestination())) {
                        flightVM.add(new FlightViewModel(lastFlight.getDestination().getCity(), lastFlight.getArrival(), f.getDeparture()));
                    } else {
                        flightVM.add(new FlightViewModel());
                    }
                }
                flightVM.add(new FlightViewModel(f));
                lastFlight = f;
            }

            if (flights.length > 0)                     //If at least one flight
                flightVM.add(new FlightViewModel());    //Add end header, which will show only when edit mode is active

            return flightVM;
        }

        @Override
        protected void onPostExecute(List<FlightViewModel> list) {
            super.onPostExecute(list);
            getMvpView().showTrip(mTrip, list);
        }
    }
}

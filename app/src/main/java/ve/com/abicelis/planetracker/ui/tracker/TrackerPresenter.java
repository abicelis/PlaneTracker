package ve.com.abicelis.planetracker.ui.tracker;

import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.IntegerLatLng;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;

/**
 * Created by abicelis on 27/9/2017.
 */

public class TrackerPresenter extends BasePresenter<TrackerMvpView> {

    //CONST
    private static final int AIRPORT_ZOOM_IN_DELAY = 500;
    private static final int AIRPORT_ZOOM_IN_TIME = 1000;

    //DATA
    private DataManager mDataManager;
    private Trip mTrip;
    private int currentFlight = 0;      //Starting on first flight by default

    public TrackerPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }


    public void setTripId(long tripId) {
        mDataManager.getTrip(tripId, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trip -> {
                    mTrip = trip;
                    getMvpView().initViewPager(mTrip.getFlights());
                    getMvpView().initMap(mTrip);
                }, throwable -> {
                    Timber.e("Error getting trip. ID=%s", tripId);
                    getMvpView().showMessage(Message.ERROR_LOADING_TRIP, null);
                });
    }

    public void mapReady() {
        flightChanged(0);
    }

    public void flightChanged(int index) {
        currentFlight = index;
        Flight f = mTrip.getFlights().get(currentFlight);

        switch (f.getStatus()) {
            case NOT_DEPARTED:
                new Handler().postDelayed(() -> {
                    IntegerLatLng latLng = new IntegerLatLng(f.getOrigin().getLatitude(), f.getOrigin().getLongitude());
                    //getMvpView().moveCameraToLocation(latLng, false, AIRPORT_ZOOM_IN_TIME);
                    getMvpView().drawRouteAndMoveCameraToBoundsOf(f.getOrigin(), f.getDestination());
                }, AIRPORT_ZOOM_IN_DELAY);
                break;

            case ARRIVED:
                new Handler().postDelayed(() -> {
                    IntegerLatLng latLng = new IntegerLatLng(f.getDestination().getLatitude(), f.getDestination().getLongitude());
                    getMvpView().moveCameraToLocation(latLng, false, AIRPORT_ZOOM_IN_TIME);
                }, AIRPORT_ZOOM_IN_DELAY);
                break;

            case IN_AIR:
                //TODO change this
                new Handler().postDelayed(() -> {
                    IntegerLatLng latLng = new IntegerLatLng(f.getOrigin().getLatitude(), f.getOrigin().getLongitude());
                    //getMvpView().moveCameraToLocation(latLng, false, AIRPORT_ZOOM_IN_TIME);
                    getMvpView().drawRouteAndMoveCameraToBoundsOf(f.getOrigin(), f.getDestination());
                }, AIRPORT_ZOOM_IN_DELAY);
                break;

        }
    }


    public Trip getLoadedTrip() {
        return mTrip;
    }

}

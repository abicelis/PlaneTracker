package ve.com.abicelis.planetracker.ui.flight;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.data.model.Airline;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.comparators.FlightByDepartureTimeComparator;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;

/**
 * Created by abicelis on 19/9/2017.
 */

public class FlightPresenter extends BasePresenter<FlightActivity> {

    //DATA
    private long mTripId;
    private long mFlightId;
    private long mFlightPosition;
    private FlightProcedure mStatus;
    private FlightStep mStep;
    private Flight mFlight;
    private List<Flight> mTempFlights;

    private DataManager mDataManager;


    public enum FlightProcedure {NEW_FLIGHT_IN_NEW_TRIP, NEW_FLIGHT_IN_EXISTING_TRIP, EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP}
    public enum FlightStep {SEARCHING_AIRPORTS_AIRLINES,
        ROUTE_SEARCH_SEARCHING_DESTINATION, ROUTE_SEARCH_SETTING_DATE, ROUTE_SEARCH_SEARCHING_FLIGHTS, ROUTE_SEARCH_SELECTING_FLIGHT,
        FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER, FLIGHT_SEARCH_SETTING_DATE, FLIGHT_SEARCH_SELECTING_FLIGHT }


    public FlightPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public FlightStep getStep() {
        return mStep;
    }

    public List<Flight> getTempFlights() {return mTempFlights;}

    public void initialize(long tripId, long flightId, int flightPosition) {
        mTripId = tripId;
        mFlightId = flightId;
        mFlightPosition = flightPosition;


        if(mTripId == -1) {                         //Intent came from HomeActivity, NEW_FLIGHT_IN_NEW_TRIP
            mStatus = FlightProcedure.NEW_FLIGHT_IN_NEW_TRIP;
            mStep = FlightStep.SEARCHING_AIRPORTS_AIRLINES;
            getMvpView().updateViews(mStep, null);
        } else {
            if (mFlightPosition != -1) {
                if(mFlightId != -1) {               //Intent came from TripDetailActivity, EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP
                    mStatus = FlightProcedure.EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP;
                    mStep = FlightStep.ROUTE_SEARCH_SETTING_DATE;

                    mDataManager.getFlight(mFlightId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(flight -> {
                                if(flight != null) {
                                    mFlight = flight;
                                    getMvpView().updateViews(mStep, mFlight);
                                } else {
                                    //TODO notify of error
                                }
                            }, throwable -> {
                                //TODO notify of huge error, stop thing
                            });


                    //TODO: get flight from db and save in mFlight. update mvpview to ROUTE_SEARCH_SETTING_DATE

                } else {                            //Intent came from TripDetailActivity, NEW_FLIGHT_IN_EXISTING_TRIP
                    mStatus = FlightProcedure.NEW_FLIGHT_IN_EXISTING_TRIP;
                    mStep = FlightStep.SEARCHING_AIRPORTS_AIRLINES;
                    getMvpView().updateViews(mStep, null);
                }

            }

        }

    }

    public void airportOrAirlineSelected(@NonNull AirportAirlineItem item) {
        switch (mStep) {
            case SEARCHING_AIRPORTS_AIRLINES:
                if(item instanceof Airport) {
                    //Save recent
                    new SharedPreferenceHelper().setAirportAsRecent(((Airport) item).getId());

                    mStep = FlightStep.ROUTE_SEARCH_SEARCHING_DESTINATION;
                    mFlight = new Flight();
                    mFlight.setOrigin((Airport)item);
                    getMvpView().updateViews(mStep, mFlight);
                } else if (item instanceof Airline) {
                    //Save recent
                    new SharedPreferenceHelper().setAirlineAsRecent(((Airline) item).getId());

                    mStep = FlightStep.FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER;
                    mFlight = new Flight();
                    mFlight.setAirline((Airline)item);
                    getMvpView().updateViews(mStep, mFlight);
                } else {
                    //TODO super error, notify user?
                }
                break;

            case ROUTE_SEARCH_SEARCHING_DESTINATION:
                mStep = FlightStep.ROUTE_SEARCH_SETTING_DATE;
                mFlight.setDestination((Airport)item);
                getMvpView().updateViews(mStep, mFlight);
        }

    }


    public void dateSelected(Calendar calendar) {
        switch (mStep) {
            case ROUTE_SEARCH_SETTING_DATE:
                mFlight.setDeparture(calendar);
                break;

            case FLIGHT_SEARCH_SETTING_DATE:
                mFlight.setDeparture(calendar);
                break;

            default:
                //TODO error out!
        }
    }


    public void searchByRoute() {
        mStep = FlightStep.ROUTE_SEARCH_SEARCHING_FLIGHTS;
        getMvpView().updateViews(mStep, mFlight);

        mDataManager.findFlightsByRoute(mFlight.getOrigin(), mFlight.getDestination(), mFlight.getDeparture())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(flights -> {
                    if (flights.size() > 0) {
                        mStep = FlightStep.ROUTE_SEARCH_SELECTING_FLIGHT;
                        getMvpView().updateViews(mStep, mFlight);
                        mTempFlights = flights; //These will be pulled by FlightResultsFragment.FlightSelectedListener.getFlights() when the fragment is attached and ready
                        Collections.sort(mTempFlights, new FlightByDepartureTimeComparator());

                        for (Flight f : flights)
                            Timber.d("Flight %s", f);

                    } else {
                        getMvpView().showMessage(Message.NOTICE_NO_AIRLINES_FOUND, null);
                        mStep = FlightStep.ROUTE_SEARCH_SETTING_DATE;
                        getMvpView().updateViews(mStep, mFlight);
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error getting flights by route");
                    //TODO show error
                });
    }

}

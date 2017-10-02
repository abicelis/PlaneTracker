package ve.com.abicelis.planetracker.ui.flight;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.data.model.Airline;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.data.model.comparators.FlightByDepartureTimeComparator;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;

/**
 * Created by abicelis on 19/9/2017.
 */

public class FlightPresenter extends BasePresenter<FlightActivity> {

    //DATA
    private long mTripId;
    private long mFlightId;
    private int mFlightPosition;
    private FlightProcedure mFlightProcedure;
    private FlightStep mStep;
    private Flight mFlight;
    private List<Flight> mTempFlights;
    private DataManager mDataManager;

    public enum FlightProcedure {NEW_FLIGHT_IN_NEW_TRIP, NEW_FLIGHT_IN_EXISTING_TRIP, EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP}
    public enum FlightStep {SEARCHING_AIRPORTS_AIRLINES,
        ROUTE_SEARCH_SEARCHING_ORIGIN, ROUTE_SEARCH_SEARCHING_DESTINATION, ROUTE_SEARCH_SETTING_DATE, ROUTE_SEARCH_SEARCHING_FLIGHTS, ROUTE_SEARCH_SELECTING_FLIGHT,
        FLIGHT_SEARCH_SEARCHING_AIRLINE, FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER, FLIGHT_SEARCH_SETTING_DATE, FLIGHT_SEARCH_SEARCHING_FLIGHTS, FLIGHT_SEARCH_SELECTING_FLIGHT }


    public FlightPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public FlightStep getStep() {
        return mStep;
    }
    public Flight getFlight() {
        return mFlight;
    }

    public List<Flight> getTempFlights() {return mTempFlights;}

    public void initialize(long tripId, long flightId, int flightPosition) {
        mTripId = tripId;
        mFlightId = flightId;
        mFlightPosition = flightPosition;

        checkAirportAirlineData();
    }

    private void checkAirportAirlineData() {
        mDataManager.airportAirlineDataExists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataExists -> {
                    if(dataExists)
                        doInitialize();
                    else
                        downloadAirportAirlineData();
                }, throwable -> {
                    Timber.e(throwable, "Error checking if Airport and Airline data exists");
                    getMvpView().showMessage(Message.ERROR_UNEXPECTED, null);
                });
    }

    private void doInitialize() {

        if(mTripId == -1) {                         //Intent came from HomeActivity, NEW_FLIGHT_IN_NEW_TRIP
            mFlightProcedure = FlightProcedure.NEW_FLIGHT_IN_NEW_TRIP;
            mStep = FlightStep.SEARCHING_AIRPORTS_AIRLINES;
            getMvpView().updateViews(mStep, null);
        } else {
            if (mFlightPosition != -1) {
                if(mFlightId != -1) {            //Intent came from TripDetailActivity, EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP
                    mFlightProcedure = FlightProcedure.EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP;
                    mStep = FlightStep.ROUTE_SEARCH_SETTING_DATE;

                    mDataManager.getFlight(mFlightId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(flight -> {
                                if(flight != null) {
                                    mFlight = flight;
                                    getMvpView().updateViews(mStep, mFlight);
                                    getMvpView().updateRouteFieldsWithExistingFlightInfo(mFlight);
                                } else {
                                    getMvpView().showMessage(Message.ERROR_UNEXPECTED, null);
                                    Timber.e("Process: EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP, failed to fetch flight for ID=%d", mFlight);
                                }
                            }, throwable -> {
                                getMvpView().showMessage(Message.ERROR_UNEXPECTED, null);
                                Timber.e("Process: EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP, failed to fetch flight for ID=%d", mFlight);
                            });

                } else {                            //Intent came from TripDetailActivity, NEW_FLIGHT_IN_EXISTING_TRIP
                    mFlightProcedure = FlightProcedure.NEW_FLIGHT_IN_EXISTING_TRIP;
                    mStep = FlightStep.SEARCHING_AIRPORTS_AIRLINES;
                    getMvpView().updateViews(mStep, null);
                }

            } else {
                getMvpView().showMessage(Message.ERROR_UNEXPECTED, null);
                Timber.e("Invalid EXTRA_ACTIVITY_FLIGHT_FLIGHT_POSITION parameter passed to initialize(). TripId=%d, FlightId=%d, FlightPosition=%d", mTripId, mFlight, mFlightPosition);
            }
        }
    }

    private void downloadAirportAirlineData() {
        //getMvpView().showDownloadingAirportAirlineDataDialog();
        getMvpView().showMessage(Message.NOTICE_DOWNLOADING_AIRPORT_AIRLINE_DATA, null);


        mDataManager.refreshAirlineData()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(al -> {

                    mDataManager.refreshAirportData()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(ap -> {
                                getMvpView().showMessage(Message.SUCCESS_DOWNLOADING_AIRPORT_AIRLINE_DATA, null);
                                doInitialize();
                            }, throwable -> {
                                Timber.e(throwable, "Error while updating airport data");
                                getMvpView().showMessage(Message.ERROR_UNEXPECTED, null);

                            });

                }, throwable -> {
                    Timber.e(throwable, "Error while updating airline data");
                    getMvpView().showMessage(Message.ERROR_UNEXPECTED, null);

                });
    }


    public void resetToStep(FlightStep step) {
        mStep = step;
        getMvpView().updateViews(mStep, mFlight);
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
                    Timber.e("airportOrAirlineSelected() SEARCHING_AIRPORTS_AIRLINES unexpected AirportAirlineItem");
                    getMvpView().showMessage(Message.ERROR_UNEXPECTED, null);
                }
                break;

            case ROUTE_SEARCH_SEARCHING_ORIGIN:
                new SharedPreferenceHelper().setAirportAsRecent(((Airport) item).getId());
                mStep = FlightStep.ROUTE_SEARCH_SEARCHING_DESTINATION;
                mFlight.setOrigin((Airport)item);
                getMvpView().updateViews(mStep, mFlight);
                break;

            case ROUTE_SEARCH_SEARCHING_DESTINATION:
                new SharedPreferenceHelper().setAirportAsRecent(((Airport) item).getId());
                mStep = FlightStep.ROUTE_SEARCH_SETTING_DATE;
                mFlight.setDestination((Airport)item);
                getMvpView().updateViews(mStep, mFlight);
                break;

            case FLIGHT_SEARCH_SEARCHING_AIRLINE:
                mStep = FlightStep.FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER;
                mFlight.setAirline((Airline)item);
                getMvpView().updateViews(mStep, mFlight);
                break;

            default:
                Timber.e("airportOrAirlineSelected() unexpected FlightStep. %s", mStep.name());
                getMvpView().showMessage(Message.ERROR_UNEXPECTED, null);
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
                getMvpView().showMessage(Message.ERROR_UNEXPECTED, null);
        }
    }


    public void flightNumberSet(int flightNumber) {
        if(mStep == FlightStep.FLIGHT_SEARCH_SETTING_FLIGHT_NUMBER) {
            mFlight.setCallsign(String.valueOf(flightNumber));
            mStep = FlightStep.FLIGHT_SEARCH_SETTING_DATE;
            getMvpView().updateViews(mStep, mFlight);
        } else {
            getMvpView().showMessage(Message.ERROR_UNEXPECTED, null);
        }
    }



    public void searchByRoute() {

        //TODO check if we have a valid origin, destination and date!
        //TODO otherwise error out

        //else...
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

//                        for (Flight f : flights)
//                            Timber.d("Flight %s", f);

                    } else {
                        getMvpView().showMessage(Message.NOTICE_NO_FLIGHTS_FOUND, null);
                        mStep = FlightStep.ROUTE_SEARCH_SETTING_DATE;
                        getMvpView().updateViews(mStep, mFlight);
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error getting flights by route");
                    getMvpView().showMessage(Message.ERROR_GETTING_FLIGHTS, null);
                    mStep = FlightStep.ROUTE_SEARCH_SETTING_DATE;
                    getMvpView().updateViews(mStep, mFlight);
                });
    }


    public void searchByFlightNumber() {
        //TODO check if we have a valid airline, flight number and date!
        //TODO otherwise error out

        //else...
        mStep = FlightStep.FLIGHT_SEARCH_SEARCHING_FLIGHTS;
        getMvpView().updateViews(mStep, mFlight);

        mDataManager.findFlightByFlightNumber(mFlight.getAirline(), Integer.valueOf(mFlight.getCallsign()), mFlight.getDeparture())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(flight -> {
                    if (flight != null) {
                        mStep = FlightStep.FLIGHT_SEARCH_SELECTING_FLIGHT;
                        getMvpView().updateViews(mStep, mFlight);
                        List<Flight> flights = new ArrayList<>();
                        flights.add(flight);
                        mTempFlights = flights; //These will be pulled by FlightResultsFragment.FlightSelectedListener.getFlights() when the fragment is attached and ready

                    } else {
                        getMvpView().showMessage(Message.NOTICE_NO_FLIGHTS_FOUND, null);
                        mStep = FlightStep.FLIGHT_SEARCH_SETTING_DATE;
                        getMvpView().updateViews(mStep, mFlight);
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error getting flights by flight number");
                    getMvpView().showMessage(Message.ERROR_GETTING_FLIGHTS, null);
                    mStep = FlightStep.FLIGHT_SEARCH_SETTING_DATE;
                    getMvpView().updateViews(mStep, mFlight);
                });
    }



    public void flightSelected(Flight flight) {
        new TripSaverAsyncTask().execute(flight);
    }


    private class TripSaverAsyncTask extends AsyncTask<Flight, Void, Long> {
        @Override
        protected Long doInBackground(Flight... flights) {

            switch (mFlightProcedure) {
                case NEW_FLIGHT_IN_NEW_TRIP:
                    Trip trip = new Trip(getMvpView().getApplicationContext().getString(R.string.activity_flight_new_trip_name), new byte[0], new ArrayList<>(Arrays.asList(flights)));
                    trip.getFlights().get(0).setOrderInTrip(0);
                    try{
                        //Set trip name
                        trip.setName(getMvpView().getApplicationContext().getString(R.string.activity_flight_new_trip_name_2) + " " + trip.getFlights().get(0).getDestination().getCity());
                    } catch (Exception e) {/* ignore */}
                    return mDataManager.saveTrip(trip);

                case NEW_FLIGHT_IN_EXISTING_TRIP:
                    Trip existingTrip = mDataManager.getTrip(mTripId, true).blockingGet();
                    existingTrip.getFlights().add(mFlightPosition, flights[0]);

                    for (int i = 0; i < existingTrip.getFlights().size(); i++)
                        existingTrip.getFlights().get(i).setOrderInTrip(i);

                    return mDataManager.saveTrip(existingTrip);

                case EDITING_EXISTING_FLIGHT_IN_EXISTING_TRIP:
                    flights[0].setTripId(mTripId);
                    flights[0].setOrderInTrip(mFlightPosition);
                    flights[0].setId(mFlightId);
                    return mDataManager.saveFlight(flights[0]);

                default:
                    throw new InvalidParameterException("TripSaverAsyncTask: Invalid flight procedure!");
            }
        }

        @Override
        protected void onPostExecute(Long tripId) {
            if (tripId != -1)
                getMvpView().tripSaved(tripId, mFlightProcedure);
            else {
                getMvpView().showMessage(Message.ERROR_ADDING_FLIGHT, null);
                Timber.e("Error saving trip on TripSaverAsyncTask.onPostExecute()");
            }
        }
    }

}

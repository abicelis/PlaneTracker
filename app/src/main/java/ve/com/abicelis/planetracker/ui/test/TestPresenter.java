package ve.com.abicelis.planetracker.ui.test;

import android.content.Context;

import java.util.Calendar;
import java.util.TimeZone;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.data.model.Airline;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.exception.ErrorParsingDataException;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;
import ve.com.abicelis.planetracker.util.CalendarUtil;

/**
 * Created by abicelis on 29/8/2017.
 */

public class TestPresenter extends BasePresenter<TestMvpView> {

    private DataManager mDataManager;
    private Context mContext;

    public TestPresenter(Context context, DataManager dataManager){
        mDataManager = dataManager;
        mContext = context;
    }

    public void getWelcomeMessage() {
        checkViewAttached();
        getMvpView().showWelcomeMessage("WELCOME");



//        new Thread(() -> {
//            mDataManager.insertFakeTrips();
//        }).start();

//        mDataManager.getImage(mContext, "rio de janeiro")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(bitmap -> {
//                    //TODO check if null!
//                    Bitmap bmp = bitmap;
//                }, throwable -> {
//                    Timber.e("Error getting bitmap!", throwable);
//
//                });


//        mDataManager.getDatabase().airportDao().getAll()
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(airports -> {
//                            Timber.d("Airports in db = %d", airports.size());
//
//                            if(airports.size() == 0) {
//                                Timber.d("No airports in db, refreshing airport and airline data");
//                                try {
//                                    mDataManager.refreshAirportDataOld();
//                                    mDataManager.refreshAirlineDataOld();
//
//                                    mDataManager.getDatabase().airportDao().getAll()
//                                            .subscribe(a -> {
//                                                        Timber.d("Airports in db = %d", a.size());
//                                                    },
//                                                    throwable -> {
//                                                        Timber.e("Error getting airports 2");
//                                                    });
//                                } catch (ErrorParsingDataException e) {
//                                    Timber.e("Error refreshing airport and airline data", e);
//                                }
//                            }






//                            mDataManager.findAirlines("Copa")
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(airlines -> {
//                                        Airline airline = airlines.get(0);
//
//                                        mDataManager.findFlightByFlightNumber(airline, 713, Calendar.getInstance())
//                                                .subscribeOn(Schedulers.io())
//                                                .observeOn(Schedulers.io())
//                                                .subscribe(flight -> {
//                                                    Timber.d("Found a flight! = %s", flight.toString());
//                                                    long id = mDataManager.saveFlight(flight);
//                                                    flight.setId(id);
//                                                    flight.setCallsign("POOP");
//                                                    long id2 = mDataManager.saveFlight(flight);
//                                                    long id3 = mDataManager.saveFlight(flight);
//
//                                                    mDataManager.getFlight(id).subscribe(flight1 -> {
//                                                        Timber.d("Retrieved saved flight! = %s", flight1.toString());
//                                                        long id4 = mDataManager.saveFlight(flight1);
//
//                                                        mDataManager.getFlight(id2).subscribe(flight2 -> {
//                                                            Timber.d("Retrieved saved flight! = %s", flight2.toString());
//
//                                                            mDataManager.getFlight(id3).subscribe(flight3 -> {
//                                                                Timber.d("Retrieved saved flight! = %s", flight3.toString());
//
//                                                            }, throwable -> {
//                                                                Timber.e("Error getting flight, id=%d", id3);
//
//                                                            });
//
//                                                        }, throwable -> {
//                                                            Timber.e("Error getting flight, id=%d", id2);
//
//                                                        });
//
//                                                    }, throwable -> {
//                                                        Timber.e("Error getting flight, id=%d", id);
//
//                                                    });
//
//                                                },throwable -> {
//                                                    Timber.e("Error getting flight", throwable);
//                                                });
//
//                                    });
//
//
//
//                        },
//                        throwable -> {
//                            Timber.e("Error getting airports");
//                        });





        //new Thread(() -> {

//            mDataManager.findAirports("Chinita")
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.newThread())
//                    .subscribe(originList -> {
//
//                        Airport origin = originList.get(0);
//
//                        mDataManager.findAirports("Tocumen")
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribeOn(Schedulers.newThread())
//                                .subscribe(destinationList -> {
//
//                                    Airport destination = destinationList.get(0);
//
//                                    mDataManager.findFlightsByRoute(origin, destination, Calendar.getInstance())
//                                            .observeOn(AndroidSchedulers.mainThread())
//                                            .subscribeOn(Schedulers.newThread())
//                                            .subscribe(flights -> {
//                                                for (Flight f : flights)
//                                                    Timber.d("Got a result! %s", f.toString());
//                                            });
//                                });
//
//                    }, throwable -> {
//                        Timber.e("Error fetching all airports");
//                    });




        //}).start();


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mDataManager.refreshAirportDataOld();
//                mDataManager.refreshAirlineDataOld();
//
//                mDataManager.findAirports("Chinita")
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(Schedulers.newThread())
//                        .take(1)
//                        .subscribe(origin -> {
//
//                            mDataManager.findAirports("Tocumen")
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribeOn(Schedulers.newThread())
//                                    .take(1)
//                                    .subscribe(destination -> {
//
//                                        mDataManager.findFlightsByRoute(origin, destination, Calendar.getInstance())
//                                                .observeOn(AndroidSchedulers.mainThread())
//                                                .subscribeOn(Schedulers.newThread())
//                                                .subscribe(flight -> {
//                                                    Timber.d("Got a result! %s", flight.toString());
//
//                                                });
//                                    });
//
//                        }, throwable -> {
//                            Timber.e("Error fetching all airports");
//                        });
//            }
//        }).start();



//        new Thread(() -> {
        //mDataManager.refreshAirlineDataOld();
        //mDataManager.refreshAirportDataOld();

//                mDataManager.deleteAllAirports();
//                mDataManager.insertAirports(new Airport(1, "La Chinita", "Maracaibo", "Venezuela", "MAR", "SVMC", 10, 10, 100, "GMT-4", "S", "America / Caracas?"));
//                mDataManager.insertAirports(new Airport(2, "La Chinitxa", "MaracaiboP", "Venezuela", "MAR", "SVMC", 10, 10, 100, "GMT-4", "S", "America / Caracas?"));
//                mDataManager.insertAirports(new Airport(3, "Coconasa", "Maracaibo", "Venezuela", "MAR", "SVMC", 10, 10, 100, "GMT-4", "S", "America / Caracas?"));
//
//                mDataManager.deleteAllAirlines();
//                mDataManager.insertAirlines(new Airline(1, "Copa Airlines", "", "CM", "CMP", "COPA", "Panama"));
//                mDataManager.insertAirlines(new Airline(2, "Lufthansa", "", "LH", "DLH", "LUFTHANSA", "Germany"));
//                mDataManager.insertAirlines(new Airline(3, "Marcopolo Airways", "", "", "MCP", "MARCOPOLO", "Afghanistan"));

//                mDataManager.findAirlinesOrAirports("york")
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(Schedulers.newThread())
//                        .subscribe(combinedSearchResult -> {
//                            Timber.d("Got a result! %s", combinedSearchResult.toString());
//                        }, throwable -> {
//                                                                Timber.e("Error fetching all airports");
//
//                        });
//            }).start();











        // 1. Figure out the local timezone GMT of the departure airport!, lets say departure is MAR, so my local time.
        // 2. Get

//        //Start of day at departure airport's timezone.
//        TimeZone tz = TimeZone.getTimeZone("GMT-4");
//        Calendar startToday = CalendarUtil.getNewInstanceZeroedCalendarForTimezone(tz);
//        Calendar endToday = CalendarUtil.getNewInstanceZeroedCalendarForTimezone(tz);
//        CalendarUtil.copyCalendar(startToday, endToday);
//
//        endToday.add(Calendar.DATE, 1);
//        endToday.add(Calendar.MILLISECOND, -1);
//
//        long startTodayEpoch = startToday.getTimeInMillis() / 1000L;
//        long endTodayEpoch = endToday.getTimeInMillis() / 1000L;


//        mFlightawareApi.airlineFlightSchedules(String.valueOf(startTodayEpoch), String.valueOf(endTodayEpoch), "MAR", "PTY")
//                .enqueue(new Callback<AirlineFlightSchedulesResponse>() {
//                    @Override
//                    public void onResponse(Call<AirlineFlightSchedulesResponse> call, Response<AirlineFlightSchedulesResponse> response) {
//                        if (response.code() != 200) {
//                            Timber.e("Error: %s", response.errorBody());
//                            return;
//                        }
//
//                        if(response.body() != null
//                                && response.body().getResult() != null
//                                && response.body().getResult().reloadTrip() != null)
//                            for(AirlineFlightSchedulesFlights f : response.body().getResult().reloadTrip())
//                                Timber.d("Got a flight: %s", f.toString());
//                        else
//                            Timber.d("Got an empty response, no flights.");
//                    }
//
//
//                    @Override
//                    public void onFailure(Call<AirlineFlightSchedulesResponse> call, Throwable t) {
//                        Timber.e("Failure: %s", t.getMessage());
//                    }
//                });
    }

}

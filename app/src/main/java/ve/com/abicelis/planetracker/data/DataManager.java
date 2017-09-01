package ve.com.abicelis.planetracker.data;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.local.AppDatabase;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.data.model.Airline;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.CombinedSearchResult;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesFlights;
import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesResponse;
import ve.com.abicelis.planetracker.data.remote.FlightawareApi;
import ve.com.abicelis.planetracker.data.remote.OpenFlightsApi;
import ve.com.abicelis.planetracker.util.CalendarUtil;
import ve.com.abicelis.planetracker.util.TimezoneUtil;

/**
 * Created by abicelis on 29/8/2017.
 */

public class DataManager {

    private AppDatabase mAppDatabase;
    private SharedPreferenceHelper mSharedPreferenceHelper;
    private FlightawareApi mFlightawareApi;
    private OpenFlightsApi mOpenFlightsApi;

    @Inject
    public DataManager(AppDatabase appDatabase, SharedPreferenceHelper sharedPreferenceHelper, FlightawareApi flightawareApi, OpenFlightsApi openFlightsApi) {
        mAppDatabase = appDatabase;
        mSharedPreferenceHelper = sharedPreferenceHelper;
        mFlightawareApi = flightawareApi;
        mOpenFlightsApi = openFlightsApi;
    }

    /**
     * Queries openflights.org for a recent list of Airline data and refreshes the local db
     */
    public void refreshAirlineData(){
        mAppDatabase.airlineDao().deleteAll();
        mOpenFlightsApi.getAirlines()
                .subscribe(s -> {
                    String lines[] = s.split("\\n");
                    List<Airline> airlines = new ArrayList<>();

                    for (String line : lines) {
                        String fields[] = line.split(",");
                        if(fields.length == Constants.OPENFLIGHTS_AIRLINES_FIELDS) {
                            try {
                                long id =           Long.parseLong(fields[0]);
                                String name =       (fields[1].equals("\\N") ? "" : fields[1].replace("\"", "").trim());
                                String alias =      (fields[2].equals("\\N") ? "" : fields[2].replace("\"", "").trim());
                                String iata =       (fields[3].equals("\\N") ? "" : fields[3].replace("\"", "").trim());
                                String icao =       (fields[4].equals("\\N") ? "" : fields[4].replace("\"", "").trim());
                                String callsign =   (fields[5].equals("\\N") ? "" : fields[5].replace("\"", "").trim());
                                String country =    (fields[6].equals("\\N") ? "" : fields[6].replace("\"", "").trim());

                                if(id != -1)
                                    airlines.add(new Airline(id, name, alias, iata, icao, callsign, country));
                            }catch (Exception e) {
                                    /* Just skip it */
                            }
                        }
                    }
                    insertAirlines(airlines.toArray(new Airline[airlines.size()]));

                }, throwable -> {
                    //TODO notify the error somewhere? idk
                });
    }

    /**
     * Queries openflights.org for a recent list of Airport data and refreshes the local db
     */
    public void refreshAirportData(){

        mAppDatabase.airportDao().deleteAll();
        mOpenFlightsApi.getAirports()
                .subscribe(s -> {
                    String lines[] = s.split("\\n");
                    List<Airport> airports = new ArrayList<>();

                    for (String line : lines) {
                        String fields[] = line.split(",");
                        if(fields.length == Constants.OPENFLIGHTS_AIRPORTS_FIELDS) {
                            try {
                                long id =               Long.parseLong(fields[0]);
                                String name =           (fields[1].equals("\\N") ? "" : fields[1].replace("\"", "").trim());
                                String city =           (fields[2].equals("\\N") ? "" : fields[2].replace("\"", "").trim());
                                String country =        (fields[3].equals("\\N") ? "" : fields[3].replace("\"", "").trim());
                                String iata =           (fields[4].equals("\\N") ? "" : fields[4].replace("\"", "").trim());
                                String icao =           (fields[5].equals("\\N") ? "" : fields[5].replace("\"", "").trim());
                                float latitude =        Float.parseFloat(fields[6]);
                                float longitude =       Float.parseFloat(fields[7]);
                                int altitude =          Integer.parseInt(fields[8]);
                                String timezoneOffset = (fields[9].equals("\\N") ? "" : fields[9].replace("\"", "").trim());
                                String dst =            (fields[10].equals("\\N") ? "" : fields[10].replace("\"", "").trim());
                                String timezoneOlsen =  (fields[11].equals("\\N") ? "" : fields[11].replace("\"", "").trim());
                                airports.add(new Airport(id, name, city, country, iata, icao, latitude, longitude, altitude, timezoneOffset, timezoneOlsen, dst));
                            }catch (Exception e) {
                                /* Just skip it */
                            }
                        }
                    }
                    insertAirports(airports.toArray(new Airport[airports.size()]));

                }, throwable -> {
                    //TODO notify the error somewhere? idk
                });
    }



    /**
     * Queries local sharedPreferences for recent airline ids. If they exist, this method fetches
     * them on the local db.
     * @return An Observable if Airlines
     */
    public Observable<Airline> getRecentAirlines() {
        long[] recentAirlineIds = mSharedPreferenceHelper.getRecentAirlineIds();
        return mAppDatabase.airlineDao().getByIds(recentAirlineIds)
                .toObservable()
                .flatMap(new Function<List<Airline>, ObservableSource<Airline>>() {
                    @Override
                    public ObservableSource<Airline> apply(@NonNull List<Airline> airlines) throws Exception {
                        return Observable.fromIterable(airlines);
                    }
                });
    }

    /**
     * Queries local sharedPreferences for recent airport ids. If they exist, this method fetches
     * them on the local db.
     * @return An Observable if Airports
     */
    public Observable<Airport> getRecentAirports() {
        long[] recentAirportIds = mSharedPreferenceHelper.getRecentAirportIds();
        return mAppDatabase.airportDao().getByIds(recentAirportIds)
                .toObservable()
                .flatMap(new Function<List<Airport>, ObservableSource<Airport>>() {
                    @Override
                    public ObservableSource<Airport> apply(@NonNull List<Airport> airports) throws Exception {
                        return Observable.fromIterable(airports);
                    }
                });
    }



    /**
     * Returns an Observable of {@link CombinedSearchResult}, which can return a list of
     * Airports and/or Airlines, this search queries the local db.
     * @param query A string with which Airports and Airlines will be searched for. This method will
     *              query the db by Airline/Airport name, IATA and ICAO.
     */
    public Observable<CombinedSearchResult> findAirlinesOrAirports(@NonNull String query) {
        if(query.isEmpty())
            return Observable.error(InvalidParameterException::new);

        query = "%"+query+"%";

        Observable<CombinedSearchResult> airports = mAppDatabase.airportDao().find(query)
                .toObservable()
                .flatMap(new Function<List<Airport>, Observable<Airport>>() {
                    @Override
                    public Observable<Airport> apply(@NonNull List<Airport> airports) throws Exception {
                        return Observable.fromIterable(airports);
                    }
                })
                .map(new Function<Airport, CombinedSearchResult>() {
                    @Override
                    public CombinedSearchResult apply(@NonNull Airport airport) throws Exception {
                        return new CombinedSearchResult(airport);
                    }
                });

        Observable<CombinedSearchResult> airlines = mAppDatabase.airlineDao().find(query)
                .toObservable()
                .flatMap(new Function<List<Airline>, Observable<Airline>>() {
                    @Override
                    public Observable<Airline> apply(@NonNull List<Airline> airlines) throws Exception {
                        return Observable.fromIterable(airlines);
                    }
                })
                .map(new Function<Airline, CombinedSearchResult>() {
                    @Override
                    public CombinedSearchResult apply(@NonNull Airline airline) throws Exception {
                        return new CombinedSearchResult(airline);
                    }
                });

        return Observable.concat(airports, airlines);
    }

    /**
     * Returns an Observable of {@link Airport}, which can return a list of
     * Airports, this search queries the local db.
     * @param query A string with which Airports will be searched for. This method will
     *              query the db by Airport name, IATA and ICAO.
     */
    public Observable<Airport> findAirports(@NonNull String query) {
        if(query.isEmpty())
            return Observable.error(InvalidParameterException::new);

        query = "%"+query+"%";

        return mAppDatabase.airportDao().find(query)
                .toObservable()
                .flatMap(new Function<List<Airport>, Observable<Airport>>() {
                    @Override
                    public Observable<Airport> apply(@NonNull List<Airport> airports) throws Exception {
                        return Observable.fromIterable(airports);
                    }
                });
    }






    /**
     * Given an origin Airport, a destination Airport and a day, this method looks for Flights
     * using FlightAware API's getFlightSchedulesByRoute()
     * @param origin The origin Airport
     * @param destination The destination Airport
     * @param day The day when to look for flights
     * @return An Observable of Flights
     */
    public Observable<Flight> findFlightsByRoute(Airport origin, Airport destination, Calendar day) {

        //Get origin airport's timezone, to get the start and end UNIX times based on that airport's timezone
        TimeZone originTz = origin.getTimezone();

        Calendar startToday = CalendarUtil.getZeroedCalendarFromYearMonthDay(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH));
        startToday.setTimeZone(originTz);
        Calendar endToday = CalendarUtil.getNewInstanceZeroedCalendarForTimezone(originTz);
        CalendarUtil.copyCalendar(startToday, endToday);

        endToday.add(Calendar.DATE, 1);
        endToday.add(Calendar.MILLISECOND, -1);

        long startTodayEpoch = startToday.getTimeInMillis() / 1000L;
        long endTodayEpoch = endToday.getTimeInMillis() / 1000L;



        // 1. Figure out the local timezone GMT of the departure airport!, lets say departure is MAR, so my local time.
        //Start of day at departure airport's timezone.
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


        return mFlightawareApi.getFlightSchedulesByRoute(String.valueOf(startTodayEpoch), String.valueOf(endTodayEpoch), origin.getIata(), destination.getIata())
                .flatMap(new Function<AirlineFlightSchedulesResponse, ObservableSource<Flight>>() {
                    @Override
                    public ObservableSource<Flight> apply(@NonNull AirlineFlightSchedulesResponse airlineFlightSchedulesResponse) throws Exception {
                        List<Flight> flights = new ArrayList<>();
                        for(AirlineFlightSchedulesFlights f : airlineFlightSchedulesResponse.getResult().getFlights()) {
                            Calendar departure = CalendarUtil.getNewInstanceZeroedCalendar();
                            departure.setTimeInMillis(f.getDeparturetime()*1000);

                            Calendar arrival = CalendarUtil.getNewInstanceZeroedCalendar();
                            arrival.setTimeInMillis(f.getArrivaltime()*1000);
                            flights.add(new Flight(origin, destination, null, departure, arrival, f.getIdent()));
                        }

                        return Observable.fromIterable(flights);
                    }
                });


    }






    /* AIRPORTS LOCAL DB */
    public void insertAirports(Airport ... airports) {
        mAppDatabase.airportDao().insert(airports);
    }

    public void deleteAllAirports() {
        mAppDatabase.airportDao().deleteAll();
    }


    /* AIRLINES LOCAL DB */
    public void insertAirlines(Airline ... airlines) {
        mAppDatabase.airlineDao().insert(airlines);
    }

    public void deleteAllAirlines() {
        mAppDatabase.airlineDao().deleteAll();
    }
}

package ve.com.abicelis.planetracker.data;

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.local.AppDatabase;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.data.model.Airline;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.data.model.exception.ErrorParsingDataException;
import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesFlights;
import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesResponse;
import ve.com.abicelis.planetracker.data.model.qwant.QwantResponse;
import ve.com.abicelis.planetracker.data.remote.FlightawareApi;
import ve.com.abicelis.planetracker.data.remote.OpenFlightsApi;
import ve.com.abicelis.planetracker.data.remote.QwantApi;
import ve.com.abicelis.planetracker.util.CalendarUtil;

/**
 * Created by abicelis on 29/8/2017.
 */

public class DataManager {

    private AppDatabase mAppDatabase;
    private SharedPreferenceHelper mSharedPreferenceHelper;
    private FlightawareApi mFlightawareApi;
    private OpenFlightsApi mOpenFlightsApi;
    private QwantApi mQwantApi;

    @Inject
    public DataManager(AppDatabase appDatabase, SharedPreferenceHelper sharedPreferenceHelper,
                       FlightawareApi flightawareApi, OpenFlightsApi openFlightsApi,
                       QwantApi qwantApi) {
        mAppDatabase = appDatabase;
        mSharedPreferenceHelper = sharedPreferenceHelper;
        mFlightawareApi = flightawareApi;
        mOpenFlightsApi = openFlightsApi;
        mQwantApi = qwantApi;
    }



    //TODO kill these, are only here for testing purposes
    public AppDatabase getDatabase() {
        return mAppDatabase;
    }



    /**
     * Queries openflights.org for a recent list of Airline data and refreshes the local db
     */
    public void refreshAirlineData() throws ErrorParsingDataException {
        mOpenFlightsApi.getAirlines()
                .subscribe(s -> {
                    String lines[] = s.split("\\n");
                    List<Airline> airlines = new ArrayList<>();

                    for (String line : lines) {
                        String fields[] = line.split(Constants.OPENFLIGHTS_SEPARATOR);
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
                            }catch (Exception e) {/* Just skip the line */}
                        }
                    }

                    if(airlines.size() > 0) {
                        //Delete old data
                        mAppDatabase.airlineDao().deleteAll();
                        //Insert new data
                        mAppDatabase.airlineDao().insert(airlines.toArray(new Airline[airlines.size()]));
                    } else {
                        throw new ErrorParsingDataException("Received airline data, but could not extract Airlines from it. Maybe data was corrupted");
                    }
                }, throwable -> {
                    throw new ErrorParsingDataException("Error. Did not receive airline data");
                });
    }

    /**
     * Queries openflights.org for a recent list of Airport data and refreshes the local db
     */
    public void refreshAirportData() throws ErrorParsingDataException {
        mOpenFlightsApi.getAirports()
                .subscribe(s -> {
                    String lines[] = s.split("\\n");
                    List<Airport> airports = new ArrayList<>();

                    for (String line : lines) {
                        String fields[] = line.split(Constants.OPENFLIGHTS_SEPARATOR);
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
                            }catch (Exception e) {/* Just skip the line */}
                        }
                    }

                    if(airports.size() > 0) {
                        //Delete old data
                        mAppDatabase.airportDao().deleteAll();
                        //Insert new data
                        mAppDatabase.airportDao().insert(airports.toArray(new Airport[airports.size()]));
                    } else {
                        throw new ErrorParsingDataException("Received airport data, but could not extract Airports from it. Maybe data was corrupted");
                    }
                }, throwable -> {
                    throw new ErrorParsingDataException("Error. Did not receive airport data", throwable);
                });
    }



    /**
     * Queries local sharedPreferences for recent airline ids. If they exist, this method fetches
     * them on the local db.
     * @return A Maybe if {@code List<Airline>}
     */
    public Maybe<List<Airline>> getRecentAirlines() {
        long[] recentAirlineIds = mSharedPreferenceHelper.getRecentAirlineIds();
        return mAppDatabase.airlineDao().getByIds(recentAirlineIds);
    }

    /**
     * Queries local sharedPreferences for recent airport ids. If they exist, this method fetches
     * them on the local db.
     * @return A Maybe if {@code List<Airport>}
     */
    public Maybe<List<Airport>> getRecentAirports() {
        long[] recentAirportIds = mSharedPreferenceHelper.getRecentAirportIds();
        return mAppDatabase.airportDao().getByIds(recentAirportIds);
    }


//    /**
//     * Returns an Observable of {@link CombinedSearchResult}, which can return a list of
//     * Airports and/or Airlines, this search queries the local db.
//     * @param query A string with which Airports and Airlines will be searched for. This method will
//     *              query the db by Airline/Airport name, IATA and ICAO.
//     */
//    public Maybe<List<CombinedSearchResult>> findAirlinesOrAirports(@NonNull String query) {
//        if(query.isEmpty())
//            return Maybe.error(InvalidParameterException::new);
//
//        query = "%"+query+"%";
//
//        Maybe<List<CombinedSearchResult>> result = mAppDatabase.airportDao().find(query)
//                .subscribe(airports -> {
//                    Maybe<List<Airline>> airlines = mAppDatabase.airlineDao().find(query)
//                            .subscribe(airlines1 -> )
//                },throwable -> {
//
//                },() -> {
//
//                })
//                .map(new Function<List<Airport>, List<CombinedSearchResult>>() {
//                    @Override
//                    public List<CombinedSearchResult> apply(@NonNull List<Airport> airports) throws Exception {
//                        List<CombinedSearchResult> result = new ArrayList<>();
//                        for (Airport a : airports)
//                            result.add(new CombinedSearchResult(a));
//                        return result;
//                    }
//                });
//
//
//        Maybe<List<CombinedSearchResult>> airports = mAppDatabase.airportDao().find(query)
//                .map(new Function<List<Airport>, List<CombinedSearchResult>>() {
//                    @Override
//                    public List<CombinedSearchResult> apply(@NonNull List<Airport> airports) throws Exception {
//                        List<CombinedSearchResult> result = new ArrayList<>();
//                        for (Airport a : airports)
//                            result.add(new CombinedSearchResult(a));
//                        return result;
//                    }
//                });
//
//        Maybe<List<CombinedSearchResult>> airlines = mAppDatabase.airlineDao().find(query)
//                .map(new Function<List<Airline>, List<CombinedSearchResult>>() {
//                    @Override
//                    public List<CombinedSearchResult> apply(@NonNull List<Airline> airlines) throws Exception {
//                        List<CombinedSearchResult> result = new ArrayList<>();
//                        for (Airline a : airlines)
//                            result.add(new CombinedSearchResult(a));
//                        return result;
//                    }
//                });
//
//        return Maybe.concat(airports, airlines);
//    }

    /**
     * Returns an Observable of {@link Airline}, which can return a list of
     * Airlines, this search queries the local db.
     * @param query A string with which Airlines will be searched for. This method will
     *              query the db by Airport name, IATA and ICAO.
     */
    public Maybe<List<Airline>> findAirlines(@NonNull String query) {
        if(query.isEmpty())
            return Maybe.error(InvalidParameterException::new);

        query = "%"+query+"%";
        return mAppDatabase.airlineDao().find(query);
    }


    /**
     * Returns an Observable of {@link Airport}, which can return a list of
     * Airports, this search queries the local db.
     * @param query A string with which Airports will be searched for. This method will
     *              query the db by Airport name, IATA and ICAO.
     */
    public Maybe<List<Airport>> findAirports(@NonNull String query) {
        if(query.isEmpty())
            return Maybe.error(InvalidParameterException::new);

        query = "%"+query+"%";
        return mAppDatabase.airportDao().find(query);
    }






    /**
     * Given an origin Airport, a destination Airport and a day, this method looks for Flights
     * using FlightAware API's getFlightSchedulesByRoute()
     * @param origin The origin Airport
     * @param destination The destination Airport
     * @param day The day when to look for flights
     * @return An Maybe of {@code List<Flight>}
     */
    public Maybe<List<Flight>> findFlightsByRoute(Airport origin, Airport destination, Calendar day) {

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

        return mFlightawareApi.getFlightSchedulesByRoute(String.valueOf(startTodayEpoch), String.valueOf(endTodayEpoch), origin.getIata(), destination.getIata())
                .map(new Function<AirlineFlightSchedulesResponse, List<Flight>>() {
                    @Override
                    public List<Flight> apply(@NonNull AirlineFlightSchedulesResponse airlineFlightSchedulesResponse) throws Exception {
                        List<Flight> flights = new ArrayList<>();
                        for(AirlineFlightSchedulesFlights f : airlineFlightSchedulesResponse.getResult().getFlights()) {
                            Calendar departure = CalendarUtil.getNewInstanceZeroedCalendar();
                            departure.setTimeInMillis(f.getDeparturetime()*1000);

                            Calendar arrival = CalendarUtil.getNewInstanceZeroedCalendar();
                            arrival.setTimeInMillis(f.getArrivaltime()*1000);

                            //Extract 3-letter ICAO code from f.getIdent()
                            Pattern icaoRegex = Pattern.compile("^[A-Za-z]{3}\\d*$");
                            Airline airline = null;
                            if (icaoRegex.matcher(f.getIdent()).matches()) {
                                String icao = f.getIdent().substring(0, 3);
                                airline = mAppDatabase.airlineDao().getByIcao(icao).blockingGet();
                            }

                            flights.add(new Flight(f.getFaIdent(), f.getIdent(), origin, destination, airline, departure, arrival, f.getAircraftType()));
                        }
                        return flights;
                    }
                });
    }


    /**
     * Given an Airline, a flight number and a day, this method looks for Flights
     * using FlightAware API's getFlightSchedulesByFlightNumber()
     * @param airline Fileting by this airline
     * @param flightNumber The number of the flight to search for
     * @param day The day when to look for the flight
     * @return An Maybe of {@code List<Flight>}
     */
    public Maybe<Flight> findFlightByFlightNumber(Airline airline, int flightNumber, Calendar day) {

        //Flights departure/arrival time are local to the airport.
        //Since we're searching for flights given an airline and a flight number (And we have no timezone)
        //Search +- 1day timeSpan from given date.
        //E.g.:  If day= 15th May 2017. Search from start of the 14th till the end of the 13th (in UNIX UTC)

        Calendar start = CalendarUtil.getZeroedCalendarFromYearMonthDay(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH));
        Calendar end = Calendar.getInstance();
        CalendarUtil.copyCalendar(start, end);

        start.add(Calendar.DATE, -1);
        end.add(Calendar.DATE, 2);
        end.add(Calendar.MILLISECOND, -1);

        long startEpoch = start.getTimeInMillis() / 1000L;
        long endEpoch = end.getTimeInMillis() / 1000L;

        return mFlightawareApi.getFlightScheduleByFlightNumber(String.valueOf(startEpoch), String.valueOf(endEpoch), airline.getIcao(), flightNumber)
                .map(new Function<AirlineFlightSchedulesResponse, Flight>() {
                    @Override
                    public Flight apply(@NonNull AirlineFlightSchedulesResponse airlineFlightSchedulesResponse) throws Exception {


                        //Get departing airport, get its timezone, check if the departure date of the flight is the same day as Calendar day
                        //If so... proceed to return that flight. Remember to return only one flight!
                        //The flight on "Calendar day" @param

                        List<Flight> flights = new ArrayList<>();
                        for (AirlineFlightSchedulesFlights f : airlineFlightSchedulesResponse.getResult().getFlights()) {

                            Airport origin = mAppDatabase.airportDao().getByIcao(f.getOrigin()).blockingGet();
                            if (origin != null) {

                                //Calculate the start and end of the day at the departure airport's timezone!
                                Calendar startToday = CalendarUtil.getZeroedCalendarFromYearMonthDay(day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DAY_OF_MONTH));
                                startToday.setTimeZone(origin.getTimezone());
                                Calendar endToday = CalendarUtil.getNewInstanceZeroedCalendarForTimezone(origin.getTimezone());
                                CalendarUtil.copyCalendar(startToday, endToday);
                                endToday.add(Calendar.DATE, 1);
                                endToday.add(Calendar.MILLISECOND, -1);

                                //Get Calendar of flight departure
                                Calendar flightDeparture = CalendarUtil.getNewInstanceZeroedCalendar();
                                flightDeparture.setTimeInMillis(f.getDeparturetime()*1000L);
                                flightDeparture.setTimeZone(origin.getTimezone());

                                if (flightDeparture.compareTo(startToday) >= 0 && flightDeparture.compareTo(endToday) <= 0) {
                                    //What we're looking for!
                                    Airport destination = mAppDatabase.airportDao().getByIcao(f.getDestination()).blockingGet();
                                    TimeZone tzDest = destination.getTimezone();
                                    Calendar flightArrival = CalendarUtil.getNewInstanceZeroedCalendarForTimezone(tzDest);
                                    flightArrival.setTimeInMillis(f.getArrivaltime()*1000L);

                                    return new Flight(f.getFaIdent(), f.getIdent(), origin, destination, airline, flightDeparture, flightArrival, f.getAircraftType());
                                }


                            }
                        }
                        //TODO figure how to return an empty maybe!
                        //return Maybe.empty();
                        return null;
                    }
                });
    }


    public Single<Bitmap> getImage(Context context, String query) {
        return mQwantApi.getImage(query)
                .map(new Function<QwantResponse, Bitmap>() {
                    @Override
                    public Bitmap apply(@NonNull QwantResponse qwantResponse) throws Exception {
                        if(qwantResponse.getData().getResult().getItems().size() > 0) {
                            String url = qwantResponse.getData().getResult().getItems().get(0).getMedia();

                            //TODO: maybe resize the image if too large? Use an ImageUtil for that or something.
                            return Picasso.with(context).load(url).get();
                        }
                        return null;
                    }
                });
    }


    public Maybe<List<Trip>> getTrips() {

        return mAppDatabase.tripDao().getAll()
                .map(new Function<List<Trip>, List<Trip>>() {
                    @Override
                    public List<Trip> apply(@NonNull List<Trip> trips) throws Exception {

                        for (Trip t : trips) {
                            List<Flight> flights = mAppDatabase.flightDao().getByTripId(t.getId()).blockingGet();

                            for (Flight f : flights) {

                            }

                        }
                        return trips;
                    }
                });
    }

    public long saveFlight(Flight flight) {
        return mAppDatabase.flightDao().insert(flight);
    }

    public Maybe<Flight> getFlight(long id) {
        return mAppDatabase.flightDao().getById(id);
    }
}

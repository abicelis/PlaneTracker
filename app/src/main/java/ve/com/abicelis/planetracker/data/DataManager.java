package ve.com.abicelis.planetracker.data;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.PlaneTrackerApplication;
import ve.com.abicelis.planetracker.data.local.AppDatabase;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.data.model.Airline;
import ve.com.abicelis.planetracker.data.model.AirlineRelevance;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.AirportAirlineItem;
import ve.com.abicelis.planetracker.data.model.AirportRelevance;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.data.model.TripViewModel;
import ve.com.abicelis.planetracker.data.model.exception.ErrorParsingDataException;
import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesFlights;
import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesResponse;
import ve.com.abicelis.planetracker.data.model.qwant.QwantResponse;
import ve.com.abicelis.planetracker.data.remote.FlightawareApi;
import ve.com.abicelis.planetracker.data.remote.OpenFlightsApi;
import ve.com.abicelis.planetracker.data.remote.QwantApi;
import ve.com.abicelis.planetracker.util.CalendarUtil;
import ve.com.abicelis.planetracker.util.ImageUtil;

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



    public Single<Boolean> airportAirlineDataExists() {
        return Single.zip(mAppDatabase.airportDao().count(),
                mAppDatabase.airlineDao().count(), new BiFunction<Integer, Integer, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Integer integer, @NonNull Integer integer2) throws Exception {
                        return (integer > 0) && (integer2 > 0);
                    }
                }
        );
    }

    /**
     * Queries openflights.org for a recent list of Airline data and refreshes the local db
     */
    public Single<String> refreshAirlineData() {
        return mOpenFlightsApi.getAirlines()
                .doOnSuccess(s -> {
                    String lines[] = s.split("\\n");
                    List<Airline> airlines = new ArrayList<>();

                    for (String line : lines) {
                        String fields[] = line.split(Constants.OPENFLIGHTS_SEPARATOR);
                        if(fields.length == Constants.OPENFLIGHTS_AIRLINES_FIELDS) {
                            try {
                                int id =             Integer.parseInt(fields[0]);
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
                });
    }


    /**
     * Queries openflights.org for a recent list of Airport data and refreshes the local db
     */
    public Single<String> refreshAirportData() throws ErrorParsingDataException {
        return mOpenFlightsApi.getAirports()
                .doOnSuccess(s -> {
                    String lines[] = s.split("\\n");
                    List<Airport> airports = new ArrayList<>();

                    for (String line : lines) {
                        String fields[] = line.split(Constants.OPENFLIGHTS_SEPARATOR);
                        if (fields.length == Constants.OPENFLIGHTS_AIRPORTS_FIELDS) {
                            try {
                                int id = Integer.parseInt(fields[0]);
                                String name = (fields[1].equals("\\N") ? "" : fields[1].replace("\"", "").trim());
                                String city = (fields[2].equals("\\N") ? "" : fields[2].replace("\"", "").trim());
                                String country = (fields[3].equals("\\N") ? "" : fields[3].replace("\"", "").trim());
                                String iata = (fields[4].equals("\\N") ? "" : fields[4].replace("\"", "").trim());
                                String icao = (fields[5].equals("\\N") ? "" : fields[5].replace("\"", "").trim());
                                float latitude = Float.parseFloat(fields[6]);
                                float longitude = Float.parseFloat(fields[7]);
                                int altitude = Integer.parseInt(fields[8]);
                                String timezoneOffset = (fields[9].equals("\\N") ? "" : fields[9].replace("\"", "").trim());
                                String dst = (fields[10].equals("\\N") ? "" : fields[10].replace("\"", "").trim());
                                String timezoneOlsen = (fields[11].equals("\\N") ? "" : fields[11].replace("\"", "").trim());
                                airports.add(new Airport(id, name, city, country, iata, icao, latitude, longitude, altitude, timezoneOffset, timezoneOlsen, dst));
                            } catch (Exception e) {/* Just skip the line */}
                        }
                    }

                    if (airports.size() > 0) {
                        //Delete old data
                        mAppDatabase.airportDao().deleteAll();
                        //Insert new data
                        mAppDatabase.airportDao().insert(airports.toArray(new Airport[airports.size()]));
                    } else {
                        throw new ErrorParsingDataException("Received airport data, but could not extract Airports from it. Maybe data was corrupted");
                    }
                });


    }



    /**
     * Queries local sharedPreferences for recent airline ids. If they exist, this method fetches
     * them on the local db.
     * @return A Maybe if {@code List<Airline>}
     */
    public Maybe<List<Airline>> getRecentAirlines() {
        long[] recentAirlineIds = mSharedPreferenceHelper.getRecentAirlineIds();
        if (recentAirlineIds != null && recentAirlineIds.length > 0)
            return mAppDatabase.airlineDao().getByIds(recentAirlineIds);
        else
            return Maybe.just(new ArrayList<>());
    }

    /**
     * Queries local sharedPreferences for recent airport ids. If they exist, this method fetches
     * them on the local db.
     * @return A Maybe if {@code List<Airport>}
     */
    public Maybe<List<Airport>> getRecentAirports() {
        long[] recentAirportIds = mSharedPreferenceHelper.getRecentAirportIds();
        if (recentAirportIds != null && recentAirportIds.length > 0)
            return mAppDatabase.airportDao().getByIds(recentAirportIds);
        else
            return Maybe.just(new ArrayList<>());
    }

    public Maybe<List<AirportAirlineItem>> getRecentAirportsAndAirlines() {
        return Maybe.zip(getRecentAirports(), getRecentAirlines(), (airports, airlines) -> {
            List<AirportAirlineItem> items = new ArrayList<>();
            items.addAll(airports);
            items.addAll(airlines);
            return items;
        });
    }



    /**
     * Returns a Maybe of {@link Airline}, which can return a list of
     * Airlines, this search queries the local db.
     * @param query A string with which Airlines will be searched for. This method will
     *              query the db by Airline name, IATA, ICAO and callsign.
     */
    public Maybe<List<AirlineRelevance>> findAirlines(@NonNull String query, @Nullable Integer limit) {
        if(query.isEmpty())
            return Maybe.error(InvalidParameterException::new);

        query = "%"+query+"%";

        if (limit != null)
            return mAppDatabase.airlineDao().find(query, limit);
        else
            return mAppDatabase.airlineDao().find(query);
    }

    /**
     * Returns a Maybe of {@link Airport}, which can return a list of
     * Airports, this search queries the local db.
     * @param query A string with which Airports will be searched for. This method will
     *              query the db by Airport name, IATA and ICAO.
     */
    public Maybe<List<AirportRelevance>> findAirports(@NonNull String query, @Nullable Integer limit) {
        if(query.isEmpty())
            return Maybe.error(InvalidParameterException::new);

        query = "%"+query+"%";

        if (limit != null)
            return mAppDatabase.airportDao().find(query, limit);
        else
            return mAppDatabase.airportDao().find(query);
    }


    /**
     * Returns a Flowable of {@link AirportAirlineItem}, which can return a list of
     * Airports or Airlines, this search queries the local db.
     * @param query A string with which Airports or Airlines will be searched for. This method will
     *              query the db by Airline/Airport name, IATA, ICAO and callsign.
     */
    public Maybe<List<AirportAirlineItem>> findAirportsOrAirlines(@NonNull String query, @Nullable Integer limit) {
        if(query.isEmpty())
            return Maybe.error(InvalidParameterException::new);

        query = "%"+query+"%";

        return Maybe.zip(findAirports(query, limit), findAirlines(query, limit), new BiFunction<List<AirportRelevance>, List<AirlineRelevance>, List<AirportAirlineItem>>() {
            @Override
            public List<AirportAirlineItem> apply(@NonNull List<AirportRelevance> airports, @NonNull List<AirlineRelevance> airlines) throws Exception {
                List<AirportAirlineItem> items = new ArrayList<>();

                int relevance = Math.max((airports.size() > 0 ? airports.get(0).getRelevance() : 0),(airlines.size() > 0 ? airlines.get(0).getRelevance() : 0) );
                int i = 0;
                int  j = 0;
                while (relevance > 0) {

                    while (i < airports.size() && airports.get(i).getRelevance() ==  relevance) {
                        items.add(airports.get(i));
                        i++;
                    }

                    while (j < airlines.size() && airlines.get(j).getRelevance() ==  relevance) {
                        items.add(airlines.get(j));
                        j++;
                    }

                    relevance--;
                }
                return items;
            }
        });
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
                        if (airlineFlightSchedulesResponse.getResult() != null ) {
                            for(AirlineFlightSchedulesFlights f : airlineFlightSchedulesResponse.getResult().getFlights()) {
                                Calendar departure = CalendarUtil.getNewInstanceZeroedCalendar();
                                departure.setTimeZone(origin.getTimezone());
                                departure.setTimeInMillis(f.getDeparturetime()*1000);

                                Calendar arrival = CalendarUtil.getNewInstanceZeroedCalendar();
                                arrival.setTimeZone(destination.getTimezone());
                                arrival.setTimeInMillis(f.getArrivaltime()*1000);

                                //Extract 3-letter ICAO code from f.getIdent()
                                Pattern icaoRegex = Pattern.compile("^[A-Za-z]{3}\\d*$");
                                Airline airline = null;
                                if (icaoRegex.matcher(f.getIdent()).matches()) {
                                    String icao = f.getIdent().substring(0, 3);
                                    airline = mAppDatabase.airlineDao().getByIcao(icao).blockingGet();

                                    if (airline != null)
                                        flights.add(new Flight(f.getFaIdent(), f.getIdent(), origin, destination, airline, departure, arrival, f.getAircraftType()));
                                    else
                                        Timber.e("findFlightsByRoute did not find airline for %s, extracted ICAO %s", f.getIdent(), icao);
                                }

                            }
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





    /**
     * Returns a {@code Single<Bitmap>} given a query string to search
     */
    public Single<Bitmap> getImage(String query) {
        return mQwantApi.getImage(query)
                .map(new Function<QwantResponse, Bitmap>() {
                    @Override
                    public Bitmap apply(@NonNull QwantResponse qwantResponse) throws Exception {
                        if(qwantResponse.getData().getResult().getItems().size() > 0) {
                            String url = qwantResponse.getData().getResult().getItems().get(0).getMedia();

                            //TODO: maybe resize the image if too large? Use an ImageUtil for that or something.
                            try {
                                return Picasso.with(PlaneTrackerApplication.getAppContext()).load(url).get();
                            } catch (Exception e) {
                                throw new Exception();
                            }
                        }
                        throw new Exception();
                    }
                });
    }

    /**
     * Returns a {@code Single<List<String>>} containing urls of images,
     * given a query string to search
     */
    public Single<List<String>> getImages(String query, int amount) {
        return mQwantApi.getImages(query, amount)
                .map(new Function<QwantResponse, List<String>>() {
                    @Override
                    public List<String> apply(@NonNull QwantResponse qwantResponse) throws Exception {
                        List<String> urls = new ArrayList<>();
                        if(qwantResponse.getData().getResult().getItems().size() > 0)
                            for (QwantResponse.QwantItems i : qwantResponse.getData().getResult().getItems())
                                urls.add(i.getMedia());
                        return urls;
                    }
                });
    }


    /**
     * This method inserts or replaces an existing a trip and its flights.
     * It does not insert Airports or Airlines
     * Since these should already be saved in the db.
     * @return the id of the trip
     */
    public long saveTrip(Trip t) {
        if (t.getId() != 0) {
            //Check if trip exists in db
            Trip tripCheck = mAppDatabase.tripDao().getById(t.getId()).blockingGet();
            if(tripCheck != null) {
                //Update trip
                Timber.d("Updating trip: %s", t.toString());
                mAppDatabase.tripDao().update(t);
                for (Flight f : t.getFlights()) {
                    f.setTripId(t.getId());
                    mAppDatabase.flightDao().insert(f);     //OnConflict = replace!
                }
                if(mAppDatabase.tripDao().update(t) == 1)
                    return t.getId();
                return -1;                                  //Did not update trip
            }
        }

        //Insert new trip
        Timber.d("Inserting new trip: %s", t.toString());
        long tripId = mAppDatabase.tripDao().insert(t);
        if(t.getFlights() != null) {
            for (Flight f : t.getFlights()) {
                f.setTripId(tripId);
                mAppDatabase.flightDao().insert(f);
            }
        }
        return tripId;
    }

    public long deleteTrip(Trip trip) {
        return mAppDatabase.tripDao().delete(trip);
    }

    /**
     * Returns a Trip with or without its flights, airlines, airports depending on {@code includeExtras}
     */
    public Maybe<Trip> getTrip(long tripId, boolean includeExtras) {
        if(!includeExtras)
            return mAppDatabase.tripDao().getById(tripId);
        else
            return mAppDatabase.tripDao().getById(tripId)
                    .map(t -> {
                        t.setFlights(mAppDatabase.flightDao().getByTripId(t.getId()).blockingGet());

                        for (Flight flight : t.getFlights()) {
                            flight.setOrigin(mAppDatabase.airportDao().getById(flight.getOriginId()).blockingGet());
                            flight.setDestination(mAppDatabase.airportDao().getById(flight.getDestinationId()).blockingGet());
                            flight.setAirline(mAppDatabase.airlineDao().getById(flight.getAirlineId()).blockingGet());
                        }
                        return t;
                    });
    }

    /**
     * Returns a Maybe of the image of a trip given its ID
     */
    public Maybe<byte[]> getTripImage(long tripId) {
        return mAppDatabase.tripDao().getById(tripId)
                .map((trip) -> {
                    return trip.getImage();
                });
    }

    public Maybe<List<TripViewModel>> getTrips(@Nullable String filter) {
        if(filter == null || filter.isEmpty())
            filter = "%";
        else
            filter = "%"+filter+"%";

        return mAppDatabase.tripDao().getFilteredByName(filter)
                .map(new Function<List<Trip>, List<TripViewModel>>() {
                    @Override
                    public List<TripViewModel> apply(@NonNull List<Trip> trips) throws Exception {
                        List<Trip> pastTrips = new ArrayList<>();
                        List<Trip> currentTrips = new ArrayList<>();
                        List<Trip> upcomingTrips = new ArrayList<>();


                        //Add origin, destination and airline for each trip's flight.
                        for (Trip t : trips) {
                            t.setFlights(mAppDatabase.flightDao().getByTripId(t.getId()).blockingGet());

                            for (Flight flight : t.getFlights()) {
                                flight.setOrigin(mAppDatabase.airportDao().getById(flight.getOriginId()).blockingGet());
                                flight.setDestination(mAppDatabase.airportDao().getById(flight.getDestinationId()).blockingGet());
                                flight.setAirline(mAppDatabase.airlineDao().getById(flight.getAirlineId()).blockingGet());
                            }

                            switch (t.getStatus()) {
                                case PAST:
                                    pastTrips.add(t);
                                    break;
                                case CURRENT:
                                    currentTrips.add(t);
                                    break;
                                case UPCOMING:
                                    upcomingTrips.add(t);
                                    break;
                            }
                        }


                        //Order trips by start of first flight, using internal compareTo() implementations of Comparable in Trip and Flight models
                        Collections.sort(pastTrips);
                        Collections.sort(currentTrips);
                        Collections.sort(upcomingTrips);

                        //Add to final tripsViewModel list, with headers
                        List<TripViewModel> tripVM = new ArrayList<>();

                        if(currentTrips.size() > 0)
                            tripVM.add(new TripViewModel(TripViewModel.TripViewModelType.HEADER_CURRENT));
                        for (Trip t : currentTrips) tripVM.add(new TripViewModel(t));

                        if(upcomingTrips.size() > 0)
                            tripVM.add(new TripViewModel(TripViewModel.TripViewModelType.HEADER_UPCOMING));
                        for (Trip t : upcomingTrips) tripVM.add(new TripViewModel(t));

                        if(pastTrips.size() > 0)
                            tripVM.add(new TripViewModel(TripViewModel.TripViewModelType.HEADER_PAST));
                        for (Trip t : pastTrips) tripVM.add(new TripViewModel(t));

                        return tripVM;
                    }
                });
    }




    public long saveFlight(Flight flight) {
        //Saving a flight does not save its associated airports (origin, destination) and airline because it is understood that these are already on the db
        return mAppDatabase.flightDao().insert(flight);
    }

    public long deleteFlight(Flight flight) {
        return mAppDatabase.flightDao().delete(flight);
    }

    public Maybe<Flight> getFlight(long id) {
        return mAppDatabase.flightDao().getById(id)
                .map(new Function<Flight, Flight>() {
                    @Override
                    public Flight apply(@NonNull Flight flight) throws Exception {
                        flight.setOrigin(mAppDatabase.airportDao().getById(flight.getOriginId()).blockingGet());
                        flight.setDestination(mAppDatabase.airportDao().getById(flight.getDestinationId()).blockingGet());
                        flight.setAirline(mAppDatabase.airlineDao().getById(flight.getAirlineId()).blockingGet());
                        return flight;
                    }
                });
    }








    //TODO kill these, are only here for testing purposes

    @Deprecated
    public AppDatabase getDatabase() {
        return mAppDatabase;
    }

    @Deprecated
    public void insertFakeTrips() {
        Timber.d("Inserting fake trips");

        mAppDatabase.tripDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(trips -> {
                    List<Airport> airports = getDatabase().airportDao().getAll().blockingGet();
                    if(airports == null || airports.size() == 0) {
                        Timber.d("No airports in db, refreshing airport and airline data");
                        try {
                            refreshAirportDataOld();
                            refreshAirlineDataOld();

                            airports = getDatabase().airportDao().getAll().blockingGet();
                            if(airports == null || airports.size() == 0)
                                Timber.e("Error getting airports");
                            else
                                Timber.d("Airports in db = %d", airports.size());
                        } catch (ErrorParsingDataException e) {
                            Timber.e("Error refreshing airport and airline data", e);
                        }
                    }




                    Timber.d("Building Past Trip to Maracaibo");
                    List<Trip> fakeTrips = new ArrayList<>();
                    List<Flight> flightsMar = new ArrayList<>();
                    Calendar pastDate = Calendar.getInstance();
                    pastDate.add(Calendar.MONTH, -1);

                    Airline copaAirline = findAirlines("Copa", 1).blockingGet().get(0);
                    try {
                        Flight flightToMar = findFlightByFlightNumber(copaAirline, 717, pastDate).blockingGet();
                        flightToMar.setOrderInTrip(1);
                        flightsMar.add(flightToMar);

                        Flight flightToPty = findFlightByFlightNumber(copaAirline, 713, pastDate).blockingGet();
                        flightToPty.setOrderInTrip(2);
                        flightsMar.add(flightToPty);
                        fakeTrips.add(new Trip("Trip to Maracaibo", null,  flightsMar));
                    } catch (Exception e ) {
                        Timber.e("Error finding flights for trip to Maracaibo. Could not add trip", e);
                    }

                    Timber.d("Getting image for Trip to Maracaibo");
                    try {
                        Bitmap image = getImage("Maracaibo").blockingGet();
                        image = ImageUtil.scaleBitmap(image, 500);
                        fakeTrips.get(0).setImage(ImageUtil.toByteArray(image));
                    } catch (Exception e) {
                        /*Do nothing*/
                    }





                    Timber.d("Building Upcoming Trip to Panama");
                    List<Flight> flightsPty = new ArrayList<>();
                    Calendar upcomingDate = Calendar.getInstance();
                    upcomingDate.add(Calendar.MONTH, 2);

                    try {
                        Flight flightToPty = findFlightByFlightNumber(copaAirline, 872, upcomingDate).blockingGet();
                        flightToPty.setOrderInTrip(1);
                        flightsPty.add(flightToPty);

                        Flight flightToGig = findFlightByFlightNumber(copaAirline, 873, upcomingDate).blockingGet();
                        flightToGig.setOrderInTrip(2);
                        flightsPty.add(flightToGig);
                        fakeTrips.add(new Trip("Trip to Panama", null,  flightsPty));
                    } catch (Exception e ) {
                        Timber.e("Error finding flights for trip to Panama. Could not add trip", e);
                    }

                    Timber.d("Getting image for Trip to Panama");
                    try {
                        Bitmap image = getImage("Panama").blockingGet();
                        image = ImageUtil.scaleBitmap(image, 500);
                        fakeTrips.get(1).setImage(ImageUtil.toByteArray(image));
                    } catch (Exception e) {
                        /*Do nothing*/
                    }






//                    Timber.d("Building Current Trip to Toronto");
//                    List<Flight> flightsYyz = new ArrayList<>();
//                    Calendar currentDate = Calendar.getInstance();
//                    currentDate.set(Calendar.DATE, 11);
//                    currentDate.add(Calendar.MONTH, 11);//Octubre
//
//                    Airline airCanadaAirline = findAirlines("Air Canada").blockingGet().get(0);
//                    try {
//                        Flight flightToYyz = findFlightByFlightNumber(airCanadaAirline, 471, currentDate).blockingGet();
//                        flightToYyz.setOrderInTrip(1);
//                        flightsYyz.add(flightToYyz);
//
//                        Flight flightToYow = findFlightByFlightNumber(airCanadaAirline, 470, currentDate).blockingGet();
//                        flightToYow.setOrderInTrip(2);
//                        flightsYyz.add(flightToYow);
//                        fakeTrips.add(new Trip("Trip to Toronto", null,  flightsYyz));
//                    } catch (Exception e ) {
//                        Timber.e("Error finding flights for trip to Toronto. Could not add trip", e);
//                    }
//
//                    Timber.d("Getting image for Trip to Toronto");
//                    image = getImage("Toronto").blockingGet();
//                    image = ImageUtil.scaleBitmap(image, 500);
//                    fakeTrips.get(2).setImage(ImageUtil.toByteArray(image));





                    for (Trip ft : fakeTrips)
                        saveTrip(ft);

                }, throwable -> {
                    Timber.e("Error getting trips");
                });

    }

    @Deprecated
    public void refreshAirlineDataOld() throws ErrorParsingDataException {
        mOpenFlightsApi.getAirlines()
                .subscribe(s -> {
                    String lines[] = s.split("\\n");
                    List<Airline> airlines = new ArrayList<>();

                    for (String line : lines) {
                        String fields[] = line.split(Constants.OPENFLIGHTS_SEPARATOR);
                        if(fields.length == Constants.OPENFLIGHTS_AIRLINES_FIELDS) {
                            try {
                                int id =             Integer.parseInt(fields[0]);
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

    @Deprecated
    public void refreshAirportDataOld() throws ErrorParsingDataException {
        Disposable d = mOpenFlightsApi.getAirports()
                .subscribe(s -> {
                    String lines[] = s.split("\\n");
                    List<Airport> airports = new ArrayList<>();

                    for (String line : lines) {
                        String fields[] = line.split(Constants.OPENFLIGHTS_SEPARATOR);
                        if(fields.length == Constants.OPENFLIGHTS_AIRPORTS_FIELDS) {
                            try {
                                int id =                Integer.parseInt(fields[0]);
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


}

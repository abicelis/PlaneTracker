package ve.com.abicelis.planetracker.data;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.local.AppDatabase;
import ve.com.abicelis.planetracker.data.model.Airline;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.CombinedSearchResult;
import ve.com.abicelis.planetracker.data.remote.FlightawareApi;
import ve.com.abicelis.planetracker.data.remote.OpenFlightsApi;

/**
 * Created by abicelis on 29/8/2017.
 */

public class DataManager {

    private AppDatabase mAppDatabase;
    private FlightawareApi mFlightawareApi;
    private OpenFlightsApi mOpenFlightsApi;

    @Inject
    public DataManager(AppDatabase appDatabase, FlightawareApi flightawareApi, OpenFlightsApi openFlightsApi) {
        mAppDatabase = appDatabase;
        mFlightawareApi = flightawareApi;
        mOpenFlightsApi = openFlightsApi;
    }

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
                                long id =           Long.parseLong(fields[0]);
                                String name =       (fields[1].equals("\\N") ? "" : fields[1].replace("\"", "").trim());
                                String city =       (fields[2].equals("\\N") ? "" : fields[2].replace("\"", "").trim());
                                String country =    (fields[3].equals("\\N") ? "" : fields[3].replace("\"", "").trim());
                                String iata =       (fields[4].equals("\\N") ? "" : fields[4].replace("\"", "").trim());
                                String icao =       (fields[5].equals("\\N") ? "" : fields[5].replace("\"", "").trim());
                                float latitude =    Float.parseFloat(fields[6]);
                                float longitude =   Float.parseFloat(fields[7]);
                                int altitude =      Integer.parseInt(fields[8]);
                                String timezone =   (fields[9].equals("\\N") ? "" : fields[7].replace("\"", "").trim());
                                String dst =        (fields[10].equals("\\N") ? "" : fields[8].replace("\"", "").trim());
                                String timezoneTz = (fields[11].equals("\\N") ? "" : fields[8].replace("\"", "").trim());
                                airports.add(new Airport(id, name, city, country, iata, icao, latitude, longitude, altitude, timezone, dst, timezoneTz));
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

package ve.com.abicelis.planetracker.data.remote;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

/**
 * Created by abicelis on 30/8/2017.
 * This class is used to download openflights.org airline and airport databases.
 */

public interface OpenFlightsApi {

    @Streaming
    @GET("airports.dat")
    Single<String> getAirports();

    @Streaming
    @GET("airlines.dat")
    Single<String> getAirlines();
}

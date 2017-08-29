package ve.com.abicelis.planetracker.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesResponse;

/**
 * Created by abicelis on 29/8/2017.
 */

public interface FlightawareApi {

    @GET("AirlineFlightSchedules")
    Call<AirlineFlightSchedulesResponse> airlineFlightSchedules (@Query("start_date") String startDate,
                                                                 @Query("end_date") String endDate,
                                                                 @Query("origin") String origin,
                                                                 @Query("destination") String destination);
}

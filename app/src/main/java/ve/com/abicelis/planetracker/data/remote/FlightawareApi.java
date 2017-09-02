package ve.com.abicelis.planetracker.data.remote;

import io.reactivex.Maybe;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesResponse;

/**
 * Created by abicelis on 29/8/2017.
 */

public interface FlightawareApi {

    @GET("AirlineFlightSchedules")
    Maybe<AirlineFlightSchedulesResponse> getFlightSchedulesByRoute (@Query("start_date") String startDate,
                                                                     @Query("end_date") String endDate,
                                                                     @Query("origin") String origin,
                                                                     @Query("destination") String destination);

    @GET("AirlineFlightSchedules")
    Maybe<AirlineFlightSchedulesResponse> getFlightScheduleByFlightNumber (@Query("start_date") String startDate,
                                                                           @Query("end_date") String endDate,
                                                                           @Query("airline") String airline,
                                                                           @Query("flightno") int flightNumber);
}

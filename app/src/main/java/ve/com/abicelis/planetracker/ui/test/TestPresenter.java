package ve.com.abicelis.planetracker.ui.test;

import java.util.Calendar;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesFlights;
import ve.com.abicelis.planetracker.data.model.flightaware.AirlineFlightSchedulesResponse;
import ve.com.abicelis.planetracker.data.remote.FlightawareApi;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;
import ve.com.abicelis.planetracker.util.CalendarUtil;

/**
 * Created by abicelis on 29/8/2017.
 */

public class TestPresenter extends BasePresenter<TestMvpView> {

    private FlightawareApi mFlightawareApi;

    public TestPresenter(FlightawareApi flightawareApi){
        mFlightawareApi = flightawareApi;
    }

    public void getWelcomeMessage() {
        checkViewAttached();
        getMvpView().showWelcomeMessage("WELCOME");


        // 1. Figure out the local timezone GMT of the departure airport!, lets say departure is MAR, so my local time.
        // 2. Get

        //Start of day at departure airport's timezone.
        TimeZone tz = TimeZone.getTimeZone("GMT-4");
        Calendar startToday = CalendarUtil.getNewInstanceZeroedCalendarForTimezone(tz);
        Calendar endToday = CalendarUtil.getNewInstanceZeroedCalendarForTimezone(tz);
        CalendarUtil.copyCalendar(startToday, endToday);

        endToday.add(Calendar.DATE, 1);
        endToday.add(Calendar.MILLISECOND, -1);

        long startTodayEpoch = startToday.getTimeInMillis() / 1000L;
        long endTodayEpoch = endToday.getTimeInMillis() / 1000L;


        mFlightawareApi.airlineFlightSchedules(String.valueOf(startTodayEpoch), String.valueOf(endTodayEpoch), "MAR", "PTY")
                .enqueue(new Callback<AirlineFlightSchedulesResponse>() {
                    @Override
                    public void onResponse(Call<AirlineFlightSchedulesResponse> call, Response<AirlineFlightSchedulesResponse> response) {
                        if (response.code() != 200) {
                            Timber.e("Error: %s", response.errorBody());
                            return;
                        }

                        if(response.body() != null
                                && response.body().getResult() != null
                                && response.body().getResult().getFlights() != null)
                            for(AirlineFlightSchedulesFlights f : response.body().getResult().getFlights())
                                Timber.d("Got a flight: %s", f.toString());
                        else
                            Timber.d("Got an empty response, no flights.");
                    }


                    @Override
                    public void onFailure(Call<AirlineFlightSchedulesResponse> call, Throwable t) {
                        Timber.e("Failure: %s", t.getMessage());
                    }
                });
    }

}

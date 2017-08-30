package ve.com.abicelis.planetracker.ui.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import ve.com.abicelis.planetracker.data.local.AppDatabase;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.remote.FlightawareApi;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;
import ve.com.abicelis.planetracker.util.CalendarUtil;

/**
 * Created by abicelis on 29/8/2017.
 */

public class TestPresenter extends BasePresenter<TestMvpView> {

    private FlightawareApi mFlightawareApi;
    private AppDatabase mAppDatabase;

    public TestPresenter(FlightawareApi flightawareApi, AppDatabase appDatabase){
        mFlightawareApi = flightawareApi;
        mAppDatabase = appDatabase;
    }

    public void getWelcomeMessage() {
        checkViewAttached();
        getMvpView().showWelcomeMessage("WELCOME");


        //Testing airport db

        new Thread(new Runnable() {
            @Override
            public void run() {

                getUrls()
                        .flatMap(new Function<List<String>, ObservableSource<?>>() {
                            @Override
                            public ObservableSource<?> apply(@NonNull List<String> strings) throws Exception {
                                return Observable.fromIterable(strings);
                            }
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

                Timber.e("Current thread! " + Thread.currentThread().toString());
                mAppDatabase.airportDao().deleteAll();
                mAppDatabase.airportDao().insert(new Airport(1, "La Chinita", "Maracaibo", "Venezuela", "MAR", "SVMC", 10, 10, 100, "GMT-4", "S", "America / Caracas?"));
                mAppDatabase.airportDao().insert(new Airport(2, "La Chinitxa", "MaracaiboP", "Venezuela", "MAR", "SVMC", 10, 10, 100, "GMT-4", "S", "America / Caracas?"));
                mAppDatabase.airportDao().insert(new Airport(3, "Some poop", "Maracaibo", "Venezuela", "MAR", "SVMC", 10, 10, 100, "GMT-4", "S", "America / Caracas?"));
                mAppDatabase.airportDao().getAll()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(airports -> {
                                    Timber.d("Got all airports");

                                    for (Airport a : airports)
                                        Timber.d("%s", a.toString());
                                },
                                throwable -> {
                                    Timber.e("Error fetching all airports");
                                });


                mAppDatabase.airportDao().find("%chi%")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(airports -> {
                            for (Airport a : airports)
                                Timber.d("Found an airport! %s", a.toString());
                        });
            }
        }).start();









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
//                                && response.body().getResult().getFlights() != null)
//                            for(AirlineFlightSchedulesFlights f : response.body().getResult().getFlights())
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




    public Observable<List<String>> getUrls() {
        return Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<String>> e) throws Exception {

                List<String> list = new ArrayList<String>();
                list.add("www.google.com");
                list.add("www.facebook.com");
                list.add("www.instagram.com");

                e.onNext(list);
                //e.onError(new Exception());
                e.onComplete();
            }
        });
    }

}

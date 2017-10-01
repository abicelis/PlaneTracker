package ve.com.abicelis.planetracker.injection.presenter;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.ui.changeimage.ChangeImagePresenter;
import ve.com.abicelis.planetracker.ui.flight.FlightPresenter;
import ve.com.abicelis.planetracker.ui.flight.airportairlinesearchfragment.AirportAirlineSearchPresenter;
import ve.com.abicelis.planetracker.ui.tracker.TrackerPresenter;
import ve.com.abicelis.planetracker.ui.tripdetail.TripDetailPresenter;
import ve.com.abicelis.planetracker.ui.home.HomePresenter;
import ve.com.abicelis.planetracker.ui.test.TestPresenter;

/**
 * Created by abicelis on 28/8/2017.
 */

@Module
public class PresenterModule {

    private final Activity mActivity;

    public PresenterModule(Activity activity) { mActivity = activity; }

//    @Provides
//    Context context() { return mActivity; }

    @Provides
    Activity activity() { return mActivity; }



    /* Presenters */
    @Provides
    TestPresenter testPresenter(Context context, DataManager dataManager) {
        return new TestPresenter(context, dataManager);
    }

    @Provides
    HomePresenter homePresenter(DataManager dataManager) {
        return new HomePresenter(dataManager);
    }

    @Provides
    ChangeImagePresenter changeImagePresenter(DataManager dataManager) {return new ChangeImagePresenter(dataManager); }

    @Provides
    TripDetailPresenter tripDetailPresenter(DataManager dataManager) {return new TripDetailPresenter(dataManager); }

    @Provides
    FlightPresenter flightPresenter(DataManager dataManager) {return new FlightPresenter(dataManager); }

    @Provides
    AirportAirlineSearchPresenter airportAirlineSearchPresenter(DataManager dataManager) {return new AirportAirlineSearchPresenter(dataManager); }

    @Provides
    TrackerPresenter trackerPresenter(DataManager dataManager) {return new TrackerPresenter(dataManager); }
}

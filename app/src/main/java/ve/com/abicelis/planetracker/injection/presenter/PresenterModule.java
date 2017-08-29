package ve.com.abicelis.planetracker.injection.presenter;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ve.com.abicelis.planetracker.data.remote.FlightawareApi;
import ve.com.abicelis.planetracker.ui.test.TestPresenter;

/**
 * Created by abicelis on 28/8/2017.
 */

@Module
public class PresenterModule {

    private final Activity mActivity;

    public PresenterModule(Activity activity) { mActivity = activity; }

    @Provides
    Context context() { return mActivity; }

    @Provides
    Activity activity() { return mActivity; }


    /* Provide other activity scoped objects here */



    /* Presenters */
    @Provides
    TestPresenter testPresenter(FlightawareApi flightawareApi) {
        return new TestPresenter(flightawareApi);
    }

}

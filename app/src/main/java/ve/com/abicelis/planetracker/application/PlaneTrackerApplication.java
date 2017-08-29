package ve.com.abicelis.planetracker.application;

import android.app.Application;
import android.content.Context;
import android.support.annotation.UiThread;

import timber.log.Timber;
import ve.com.abicelis.planetracker.BuildConfig;
import ve.com.abicelis.planetracker.injection.application.ApplicationComponent;
import ve.com.abicelis.planetracker.injection.application.ApplicationModule;
import ve.com.abicelis.planetracker.injection.application.DaggerApplicationComponent;

/**
 * Created by abicelis on 28/8/2017.
 */

public class PlaneTrackerApplication extends Application {

    private ApplicationComponent mApplicationComponent;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();

        getApplicationComponent().inject(this);
        mAppContext = this;


        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            //Fabric.with(this, new Crashlytics());
        }
    }

    @UiThread
    public ApplicationComponent getApplicationComponent() {
        if(mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return mApplicationComponent;
    }

    public static Context getAppContext() {
        return mAppContext;
    }
}

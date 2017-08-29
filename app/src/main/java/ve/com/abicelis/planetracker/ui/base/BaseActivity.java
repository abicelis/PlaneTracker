package ve.com.abicelis.planetracker.ui.base;

import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;

import ve.com.abicelis.planetracker.application.PlaneTrackerApplication;
import ve.com.abicelis.planetracker.injection.presenter.PresenterComponent;
import ve.com.abicelis.planetracker.injection.presenter.PresenterModule;

/**
 * Created by abicelis on 28/8/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @UiThread
    protected PresenterComponent getPresenterComponent() {
        return ((PlaneTrackerApplication)getApplication())
                .getApplicationComponent()
                .newPresenterComponent(new PresenterModule(this));
    }

}

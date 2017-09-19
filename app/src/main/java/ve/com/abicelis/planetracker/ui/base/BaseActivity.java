package ve.com.abicelis.planetracker.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;

import ve.com.abicelis.planetracker.application.PlaneTrackerApplication;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.injection.presenter.PresenterComponent;
import ve.com.abicelis.planetracker.injection.presenter.PresenterModule;

/**
 * Created by abicelis on 28/8/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSavedAppTheme();
    }

    @UiThread
    protected PresenterComponent getPresenterComponent() {
        return ((PlaneTrackerApplication)getApplication())
                .getApplicationComponent()
                .newPresenterComponent(new PresenterModule(this));
    }

    protected void setSavedAppTheme(){
        setTheme(new SharedPreferenceHelper().getAppThemeType().getTheme());
    }
}

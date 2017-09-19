package ve.com.abicelis.planetracker.injection.application;

import dagger.Component;
import ve.com.abicelis.planetracker.application.PlaneTrackerApplication;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;
import ve.com.abicelis.planetracker.injection.presenter.PresenterComponent;
import ve.com.abicelis.planetracker.injection.presenter.PresenterModule;

/**
 * Created by abicelis on 28/8/2017.
 */

@ApplicationScope
@Component(
        modules = {
                ApplicationModule.class,
                RemoteModule.class,
                LocalModule.class
        }
)
public interface ApplicationComponent {
    void inject(PlaneTrackerApplication target);





    // Services injected by ApplicationComponent should be:
    // Global dagger services, which should be instantiated only once per app lifecycle
    // A service to be injected into Application object
    // Services required by more than one sub-component of ApplicationComponent
    DataManager dataManager();




    // An instance of a PresenterComponent can be instantiated from
    // this ApplicationComponent (Since PresenterComponent is an @SubComponent of ApplicationComponent)
    // while supplying the required PresenterModule.
    PresenterComponent newPresenterComponent(PresenterModule presenterModule);
}

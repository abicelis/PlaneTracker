package ve.com.abicelis.planetracker.ui.base;

import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;

import ve.com.abicelis.planetracker.application.PlaneTrackerApplication;
import ve.com.abicelis.planetracker.injection.presenter.PresenterComponent;
import ve.com.abicelis.planetracker.injection.presenter.PresenterModule;

/**
 * Created by abicelis on 28/8/2017.
 */

public abstract class BaseFragment extends Fragment {

    @UiThread
    protected PresenterComponent getPresenterComponent() {
        return ((PlaneTrackerApplication)getActivity().getApplication())
                .getApplicationComponent()
                .newPresenterComponent(new PresenterModule(getActivity()));
    }

}

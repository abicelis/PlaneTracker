package ve.com.abicelis.planetracker.injection.presenter;

import dagger.Subcomponent;
import ve.com.abicelis.planetracker.ui.test.TestActivity;

/**
 * Created by abicelis on 28/8/2017.
 */

@Subcomponent(
        modules = {
                PresenterModule.class,
                RemoteModule.class
        }
)
public interface PresenterComponent {
    void inject(TestActivity target);
}

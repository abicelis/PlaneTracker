package ve.com.abicelis.planetracker.injection.presenter;

import dagger.Subcomponent;
import ve.com.abicelis.planetracker.injection.application.RemoteModule;
import ve.com.abicelis.planetracker.ui.changeimage.ChangeImageActivity;
import ve.com.abicelis.planetracker.ui.home.HomeActivity;
import ve.com.abicelis.planetracker.ui.test.TestActivity;

/**
 * Created by abicelis on 28/8/2017.
 */

@Subcomponent(
        modules = {
                PresenterModule.class,
        }
)
public interface PresenterComponent {
    void inject(TestActivity target);
    void inject(HomeActivity target);
    void inject(ChangeImageActivity target);

}

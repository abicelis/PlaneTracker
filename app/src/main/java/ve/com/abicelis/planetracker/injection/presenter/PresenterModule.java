package ve.com.abicelis.planetracker.injection.presenter;

import android.app.Activity;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.ui.changeimage.ChangeImagePresenter;
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
    ChangeImagePresenter changeImagePresenter(DataManager dataManager) {return new ChangeImagePresenter(dataManager);}

}

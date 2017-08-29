package ve.com.abicelis.planetracker.ui.test;

import javax.inject.Inject;

import ve.com.abicelis.planetracker.ui.base.BasePresenter;

/**
 * Created by abicelis on 29/8/2017.
 */

public class TestPresenter extends BasePresenter<TestMvpView> {


    public void getWelcomeMessage() {
        checkViewAttached();
        getMvpView().showWelcomeMessage("WELCOME");
    }
}

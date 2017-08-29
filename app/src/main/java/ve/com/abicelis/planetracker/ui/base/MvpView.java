package ve.com.abicelis.planetracker.ui.base;

import ve.com.abicelis.planetracker.application.Message;

/**
 * Created by abicelis on 29/8/2017.
 *
 * This base interface must be used by any class that wants to act as a View in the MVP (Model View Presenter)
 * pattern. As this is a base interface, you would normally extend it with a more specific interface, which is then
 * implemented by an Activity or Fragment.
 */
public interface MvpView {
    void showErrorMessage(Message message);
}

package ve.com.abicelis.planetracker.ui.base;

/**
 * Created by abicelis on 29/8/2017.
 *
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface Presenter<V extends MvpView> {
    void attachView(V mvpView);
    void detachView();
}

package ve.com.abicelis.planetracker.ui.changeimage;

import java.util.List;

import ve.com.abicelis.planetracker.ui.base.MvpView;

/**
 * Created by abicelis on 5/9/2017.
 */

public interface ChangeImageMvpView extends MvpView {

    void showLoading(boolean loading);
    void showImages(List<String> imageUrls);
    void imageSavedSoFinish();
}

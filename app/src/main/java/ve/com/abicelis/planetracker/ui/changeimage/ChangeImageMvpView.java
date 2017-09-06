package ve.com.abicelis.planetracker.ui.changeimage;

import java.util.List;
import java.util.Map;

import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.model.ImageThumbnailUrl;
import ve.com.abicelis.planetracker.ui.base.MvpView;

/**
 * Created by abicelis on 5/9/2017.
 */

public interface ChangeImageMvpView extends MvpView {

    void showLoading();
    void showImages(List<ImageThumbnailUrl> images);
}

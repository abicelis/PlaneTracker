package ve.com.abicelis.planetracker.ui.changeimage;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.application.PlaneTrackerApplication;
import ve.com.abicelis.planetracker.data.DataManager;
import ve.com.abicelis.planetracker.data.model.ImageThumbnailUrl;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.base.BasePresenter;
import ve.com.abicelis.planetracker.util.ImageUtil;

/**
 * Created by abicelis on 5/9/2017.
 */

public class ChangeImagePresenter extends BasePresenter<ChangeImageMvpView> {


    //DATA
    private DataManager mDataManager;
    private long mTripId;
    private String mTripName;

    public ChangeImagePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void setTripData(long tripId, String tripName) {
        mTripId = tripId;
        mTripName = tripName;
    }


    public void loadImages(){
        mDataManager.getImages(mTripName, Constants.QWANT_IMAGE_QUERY_DEFAULT_IMAGE_COUNT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(images -> {
                    getMvpView().showImages(images);
                }, throwable -> {
                    getMvpView().showMessage(Message.ERROR_LOADING_IMAGES, null);
                });
    }

    public void imageSelected(ImageThumbnailUrl item) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    Bitmap bitmap = Picasso.with(PlaneTrackerApplication.getAppContext()).load(item.getImageUrl()).get();
                    bitmap = ImageUtil.scaleBitmap(bitmap, 500);
                    byte[] bitmapBytes = ImageUtil.toCompressedByteArray(bitmap, Constants.IMAGE_JPEG_COMPRESSION_PERCENTAGE);
                    Trip trip = mDataManager.getTrip(mTripId).blockingGet();

                    if(trip != null) {
                        trip.setImage(bitmapBytes);
                        mDataManager.saveTrip(trip);
                        return 1;
                    }
                } catch (IOException e) {
                    return -1;
                }
                return -1;
            }

            @Override
            protected void onPostExecute(Integer result) {
                super.onPostExecute(result);

                if (result == -1) {
                    getMvpView().showMessage(Message.ERROR_SAVING_IMAGE, null);
                } else {
                    getMvpView().showMessage(Message.SUCCESS_SAVING_IMAGE, null);
                }
            }
        }.execute();
    }


}

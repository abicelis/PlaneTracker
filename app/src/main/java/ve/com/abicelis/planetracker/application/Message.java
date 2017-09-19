package ve.com.abicelis.planetracker.application;

import android.content.Context;
import android.support.annotation.StringRes;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.util.SnackbarUtil;

/**
 * Created by abicelis on 29/8/2017.
 * App-wide Messages
 * ERROR_
 * NOTICE_
 * SUCCESS_
 *
 */

public enum Message {
    ERROR_UNEXPECTED(R.string.error_unexpected, SnackbarUtil.SnackbarType.ERROR),


    NOTICE_LOADING_TRIPS(R.string.info_loading_trips, SnackbarUtil.SnackbarType.NOTICE),
    ERROR_LOADING_TRIPS(R.string.error_loading_trips, SnackbarUtil.SnackbarType.ERROR),
    ERROR_LOADING_TRIP(R.string.error_loading_trip, SnackbarUtil.SnackbarType.ERROR),

    ERROR_LOADING_IMAGES(R.string.error_loading_images, SnackbarUtil.SnackbarType.ERROR),
    ERROR_LOADING_IMAGE(R.string.error_loading_image, SnackbarUtil.SnackbarType.ERROR),
    ERROR_SAVING_IMAGE(R.string.error_saving_image, SnackbarUtil.SnackbarType.ERROR),
    SUCCESS_SAVING_IMAGE(R.string.success_saving_image, SnackbarUtil.SnackbarType.SUCCESS),


    ;

    @StringRes
    int friendlyName;
    SnackbarUtil.SnackbarType messageType;

    Message(@StringRes int friendlyName, SnackbarUtil.SnackbarType messageType) {
        this.friendlyName = friendlyName;
        this.messageType = messageType;
    }


    public @StringRes int getFriendlyNameRes() {
        return friendlyName;
    }
    public SnackbarUtil.SnackbarType getMessageType() {
        return messageType;
    }
    public String getFriendlyName(Context context) {
        return PlaneTrackerApplication.getAppContext().getString(friendlyName);
    }

}

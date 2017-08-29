package ve.com.abicelis.planetracker.application;

import android.content.Context;
import android.support.annotation.StringRes;

import ve.com.abicelis.planetracker.R;

/**
 * Created by abicelis on 29/8/2017.
 * App-wide Messages
 * ERROR_
 * INFO_
 * SUCCESS_
 *
 */

public enum Message {
    //ERROR_LOADING_SOMETHING(R.string.error_loading_something, MessageType.ERROR),

    ;

    @StringRes
    int friendlyName;
    MessageType messageType;

    Message(@StringRes int friendlyName, MessageType messageType) {
        this.friendlyName = friendlyName;
        this.messageType = messageType;
    }


    public @StringRes int getFriendlyNameRes() {
        return friendlyName;
    }
    public MessageType getMessageType() {
        return messageType;
    }
    public String getFriendlyName(Context context) {
        return PlaneTrackerApplication.getAppContext().getString(friendlyName);
    }

    public enum MessageType {SUCCESS, ERROR, NOTICE}
}

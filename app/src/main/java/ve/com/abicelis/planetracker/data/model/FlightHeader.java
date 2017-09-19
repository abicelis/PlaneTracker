package ve.com.abicelis.planetracker.data.model;

import java.util.Calendar;

import ve.com.abicelis.planetracker.util.CalendarUtil;

/**
 * Created by abicelis on 18/9/2017.
 */

public class FlightHeader {

    private State mState;
    private String mTitle;
    private String mLayover;

    public FlightHeader(String title, Calendar start, Calendar end) {
        mState = State.HEADER; //Default to header state
        mTitle = title;
        mLayover = CalendarUtil.getCuteTimeStringBetweenCalendars(start, end);
    }
    public FlightHeader() {
        mState = State.HEADER; //Default to header state
    }

    public String getTitle() {return mTitle;}
    public String getLayover() {return mLayover;}
    public State getState() {return mState;}

    public void setState(State mState) {this.mState = mState;}

    public enum State {HEADER, EDIT}
}

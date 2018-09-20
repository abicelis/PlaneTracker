package ve.com.abicelis.planetracker.application;

/**
 * Created by abicelis on 29/8/2017.
 * App-wide constants file
 * Element	                    Field Name Prefix
 * SharedPreferences	        PREF_
 * Bundle	                    BUNDLE_
 * Fragment Arguments	        ARGUMENT_
 * Intent Extra	                EXTRA_
 * Intent Action	            ACTION_
 * Start Activity for result    RESULT_
 * Constant                     CONST_
 */

public class Constants {

    /* MISC */
    public static final int    IMAGE_JPEG_COMPRESSION_PERCENTAGE = 50;
    public static final int    ALPHA_ANIMATIONS_DURATION = 200;
    public static final int    UI_DEBOUNCE_TIME_MILLISECONDS = 300;

    /* TRACKER ACTIVITY*/
    public static final String  EXTRA_ACTIVITY_TRACKER_TRIP_ID = "EXTRA_ACTIVITY_TRACKER_TRIP_ID";


    /* FLIGHT ACTIVITY */
    public static final String  EXTRA_ACTIVITY_FLIGHT_TRIP_ID = "EXTRA_ACTIVITY_FLIGHT_TRIP_ID";
    public static final String  EXTRA_ACTIVITY_FLIGHT_FLIGHT_ID = "EXTRA_ACTIVITY_FLIGHT_FLIGHT_ID";
    public static final String  EXTRA_ACTIVITY_FLIGHT_FLIGHT_POSITION = "EXTRA_ACTIVITY_FLIGHT_FLIGHT_POSITION";
    public static final String  TAG_ACTIVITY_FLIGHT_AIRPORT_AIRLINE_SEARCH_FRAGMENT = "TAG_ACTIVITY_FLIGHT_AIRPORT_AIRLINE_SEARCH_FRAGMENT";
    public static final String  TAG_ACTIVITY_FLIGHT_DATE_SELECT_FRAGMENT = "TAG_ACTIVITY_FLIGHT_DATE_SELECT_FRAGMENT";
    public static final String  TAG_ACTIVITY_FLIGHT_NUMBER_SELECT_FRAGMENT = "TAG_ACTIVITY_FLIGHT_NUMBER_SELECT_FRAGMENT";
    public static final String  TAG_ACTIVITY_FLIGHT_FLIGHT_RESULTS_FRAGMENT = "TAG_ACTIVITY_FLIGHT_FLIGHT_RESULTS_FRAGMENT";

    /* DATE SELECT FRAGMENT */
    public static final int    CONST_FRAGMENT_DATE_SELECT_MIN_DATE_MONTH_DIFFERENCE = -1;
    public static final int    CONST_FRAGMENT_DATE_SELECT_MAX_DATE_MONTH_DIFFERENCE = 6;


    /* TRIP DETAIL ACTIVITY */
    public static final String  EXTRA_ACTIVITY_TRIP_DETAIL_TRIP_ID = "EXTRA_ACTIVITY_TRIP_DETAIL_TRIP_ID";
    public static final int     RESULT_ACTIVITY_CHANGE_IMAGE_TRIP = 54;

    /* CHANGE IMAGE ACTIVITY */
    public static final String  EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_ID = "EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_ID";
    public static final String  EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_NAME = "EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_NAME";


    /* SHARED PREFERENCES */
    public static final String  SHARED_PREFERENCE_TIME_FORMAT = "SHARED_PREFERENCE_TIME_FORMAT";
    public static final String  SHARED_PREFERENCE_DATE_FORMAT = "SHARED_PREFERENCE_DATE_FORMAT";
    public static final String  SHARED_PREFERENCE_RECENT_AIRPORTS_AIRLINES_SEPARATOR = "-";
    public static final int     SHARED_PREFERENCE_MAX_RECENT_AIRPORTS_AIRLINES = 3;
    public static final String  SHARED_PREFERENCE_RECENT_AIRPORTS = "SHARED_PREFERENCE_RECENT_AIRPORTS";
    public static final String  SHARED_PREFERENCE_RECENT_AIRLINES = "SHARED_PREFERENCE_RECENT_AIRLINES";
    public static final String  SHARED_PREFERENCE_APP_THEME_TYPE = "SHARED_PREFERENCE_APP_THEME_TYPE";

    /* ROOM DATABASE */
    public static final String ROOM_DATABASE_NAME = "plane_tracker.db";
    public static final String ROOM_DATABASE_CALENDAR_CONVERTER_SEPARATOR = "!!!";
    public static final int    ROOM_DATABASE_MAX_SEARCH_RESULTS = 10;


    /* FLIGHTAWARE API */
    public static final String FLIGHTAWARE_BASE_URL = "http://flightxml.flightaware.com/json/FlightXML3/";
    public static final String FLIGHTAWARE_API_KEY = "d0c70eaf9c88fb2fdffab7034e5affed966408dc";
    public static final String FLIGHTAWARE_USER = "abicelis";

    /* QWANT API */
    public static final String QWANT_BASE_URL = "https://api.qwant.com/api/search/";
    public static final int QWANT_IMAGE_QUERY_DEFAULT_IMAGE_COUNT = 10;

    /* OPENFLIGHTS.ORG AIRPORT AND AIRLINE DATA */
    public static final String OPENFLIGHTS_BASE_URL = "https://raw.githubusercontent.com/jpatokal/openflights/master/data/";
    public static final String OPENFLIGHTS_SEPARATOR = ",";
    public static final int OPENFLIGHTS_AIRLINES_FIELDS = 8;
    public static final int OPENFLIGHTS_AIRPORTS_FIELDS = 14;
}

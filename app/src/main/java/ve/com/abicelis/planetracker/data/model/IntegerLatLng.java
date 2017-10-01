package ve.com.abicelis.planetracker.data.model;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by abicelis on 29/9/2017.
 */

public class IntegerLatLng implements Comparable<IntegerLatLng>{
    private int mLatitude;
    private int mLongitude;
    private static final int MULTIPLIER = 1000000;

    public IntegerLatLng(LatLng latLng) {
        mLatitude = (int)(latLng.latitude*MULTIPLIER);
        mLongitude = (int)(latLng.longitude*MULTIPLIER);
    }

    public IntegerLatLng(double latitude, double longitude) {
        mLatitude = (int)(latitude*MULTIPLIER);
        mLongitude = (int)(longitude*MULTIPLIER);
    }

    public LatLng toLatLng() {
        return new LatLng(((double)mLatitude)/MULTIPLIER, ((double)mLongitude)/MULTIPLIER);
    }


    public int getLatitude() {return mLatitude;}
    public int getLongitude() {return mLongitude;}
    public void setLatitude(int latitude) {mLatitude = latitude;}
    public void setLongitude(int longitude) {mLongitude = longitude;}



//    public int getLatDiff(double latitude) {
//        return mLatitude - (int)(latitude*MULTIPLIER);
//    }
//    public int getLngDiff(double longitude) {
//        return mLatitude - (int)(longitude*MULTIPLIER);
//    }


    @Override
    public int compareTo(@NonNull IntegerLatLng in) {
        if(mLatitude == in.getLatitude() && mLongitude == in.getLongitude())
            return 0;
        else return 1;
    }

    public void stepTo(int incrementStep, IntegerLatLng destination) {
        if(compareTo(destination) == 0)
            return;

        int latDiff = mLatitude - destination.getLatitude();
        int longDiff = mLongitude - destination.getLongitude();

        if(latDiff > 0) {
            if (Math.abs(latDiff) > incrementStep)
                mLatitude -= incrementStep;
            else
                mLatitude = destination.getLatitude();

        } else if(latDiff < 0) {
            if (Math.abs(latDiff) > incrementStep)
                mLatitude += incrementStep;
            else
                mLatitude = destination.getLatitude();
        }

        if(longDiff > 0) {
            if (Math.abs(longDiff) > incrementStep)
                mLongitude -= incrementStep;
            else
                mLongitude = destination.getLongitude();

        } else if(longDiff < 0) {
            if (Math.abs(longDiff) > incrementStep)
                mLongitude += incrementStep;
            else
                mLongitude = destination.getLongitude();
        }

    }

}

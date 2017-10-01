package ve.com.abicelis.planetracker.ui.tracker;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.model.Airport;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.FlightProgressViewModel;
import ve.com.abicelis.planetracker.data.model.IntegerLatLng;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.base.BaseActivity;
import ve.com.abicelis.planetracker.ui.customviews.AirplaneView;
import ve.com.abicelis.planetracker.util.ViewUtil;

/**
 * Created by abicelis on 27/9/2017.
 */

public class TrackerActivity extends BaseActivity implements
        TrackerMvpView,
        OnMapReadyCallback
        //GoogleApiClient.ConnectionCallbacks,
        //GoogleApiClient.OnConnectionFailedListener
{

    //UI
    @BindView(R.id.activity_tracker_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_tracker_viewpager)
    ViewPager mViewPager;
    private TrackerViewPagerAdapter mTrackerViewPagerAdapter;

    @BindView(R.id.activity_tracker_plane)
    AirplaneView mAirplane;

    SupportMapFragment mMapFragment;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;


    //DATA
    @Inject
    TrackerPresenter mPresenter;

    private float mOldZoom;
    private int mAirplaneBearing;
    IntegerLatLng cameraStart =new IntegerLatLng(new LatLng(41.889, -87.622));
    IntegerLatLng cameraDestination =new IntegerLatLng(new LatLng(40, -87.622));
    float zoomLevel = 13;
    //int animationDelay = 50;
    int animationDelay = 100;
    int incrementStep = 10;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        ButterKnife.bind(this);
        getPresenterComponent().inject(this);
        mPresenter.attachView(this);
        mAirplane.hideAirplane(false);

        setUpToolbar();

        long tripId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_TRACKER_TRIP_ID, -1);
        if(tripId != -1) {
            mPresenter.setTripId(tripId);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    finish();
                }
            };
            Timber.e("Missing EXTRA_ACTIVITY_TRACKER_TRIP_ID parameter, cannot open TrackerActivity without a trip");
            showMessage(Message.ERROR_UNEXPECTED, callback);
        }
    }

    private void setUpToolbar() {
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return false;
    }

    /* TrackerMvpView interface implementation */

    @Override
    public void initViewPager(List<Flight> flights) {
        List<Fragment> fragments = new ArrayList<>();

        for (int i = 0; i < flights.size(); i++) {
            FlightProgressViewModel f = new FlightProgressViewModel( (i == 0), (i == flights.size()-1), flights.get(i) );

            FlightFragment ff = new FlightFragment();
            Bundle b = new Bundle();
            b.putSerializable(FlightFragment.ARGUMENT_FLIGHT_PROGRESS_VIEW_MODEL, f);
            ff.setArguments(b);
            fragments.add(ff);
        }


        //Setup adapter and viewpager
        mTrackerViewPagerAdapter = new TrackerViewPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mTrackerViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // if(mTrackerViewPagerAdapter.getRegisteredFragment(position).mActionMode != null)
                //      mTrackerViewPagerAdapter.getRegisteredFragment(position).mActionMode.finish(); //If changing tabs, hide actionMode (ContextMenu)
            }

            @Override
            public void onPageSelected(int position) {
                //If changing tabs, hide actionMode (ContextMenu)

                mPresenter.flightChanged(position);

//                for(int i = 0; i < mTrackerViewPagerAdapter.getRegisteredFragments().size(); i++) {
//                    int key = mTrackerViewPagerAdapter.getRegisteredFragments().keyAt(i);
//
//                    if(mTrackerViewPagerAdapter.getRegisteredFragments().get(key).mActionMode != null)
//                        mTrackerViewPagerAdapter.getRegisteredFragments().get(key).mActionMode.finish();
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public void initMap(Trip trip) {
        // Request notification of MapFragment when the map is ready to be used.
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_tracker_map);
        mMapFragment.getMapAsync(this);
    }


    @Override
    public void moveCameraToLocation(@NonNull IntegerLatLng integerLatLng, boolean restrictZoom, int delay) {
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15), 1000, null);  //Zoom level 15 = Streets, 1000ms animation
        // CameraPosition cameraPos = new CameraPosition.Builder().target(latlng).zoom(13).build();

        //Point map camera to location
        // LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
        //CameraPosition cameraPos = new CameraPosition.Builder().tilt(60).target(latlng).zoom(15).build();
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 1000, null);

        setRestrictZoom(restrictZoom);

                CameraPosition cameraPosition = CameraPosition.builder()
                .target(integerLatLng.toLatLng())
                .zoom(zoomLevel)
                .bearing(0)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), delay, null);
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(integerLatLng.toLatLng()), delay, null);
    }



    @Override
    public void drawRouteAndMoveCameraToBoundsOf(Airport origin, Airport destination) {
        mMap.clear();


        //Add Markers
        mMap.addMarker(new MarkerOptions().position(origin.getLatLng()).title(origin.getName()));
        mMap.addMarker(new MarkerOptions().position(destination.getLatLng()).title(destination.getName()));

        //Add geodesic line
        int color = ContextCompat.getColor(this, R.color.geodesic_line_between_airports);
        mMap.addPolyline(new PolylineOptions().add(origin.getLatLng()).add(destination.getLatLng()).width(5).color(color).geodesic(true));

        //Calculate bounds and animate camera
        setRestrictZoom(false);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origin.getLatLng());
        builder.include(destination.getLatLng());
        LatLngBounds bounds = builder.build();


        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));


    }

    @Override
    public void setRestrictZoom(boolean restrict) {
//        if (restrict) {
//            mMap.setMinZoomPreference(zoomLevel);
//            mMap.setMaxZoomPreference(zoomLevel);
//        }
//        else {
//            mMap.setMinZoomPreference(1);
//            mMap.setMaxZoomPreference(zoomLevel);
//        }
    }

    @Override
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {

    }


//    @Override
//    public void initGoogleApiClient() {
//        // Build the Play services client for use by the Fused Location Provider and the Places API.
//        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */,
//                        this /* OnConnectionFailedListener */)
//                .addConnectionCallbacks(this)
////                .addApi(LocationServices.API)
//                //               .addApi(Places.GEO_DATA_API)
////                .addApi(Places.PLACE_DETECTION_API)
//                .build();
//        mGoogleApiClient.connect();
//    }

    /* GoogleApiClient implementations */
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        // Request notification of MapFragment when the map is ready to be used.
//        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_tracker_map);
//        mMapFragment.getMapAsync(this);
//    }
//    @Override
//    public void onConnectionSuspended(int i) {
//        Toast.makeText(this, "Connection suspended " + i, Toast.LENGTH_SHORT).show();
//    }
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Toast.makeText(this, "Connection failed " + connectionResult.toString(), Toast.LENGTH_SHORT).show();
//    }




    /* GoogleMap OnMapReady interface */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style_json));

            if (!success) {
                Timber.e("Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Timber.e(e, "Can't find style.");
        }

//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        //mMap.setIndoorEnabled(true);
//        //mMap.setBuildingsEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//
//        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        //mMap.setBuildingsEnabled(true);
//        if(mPresenter.getLoadedTrip().getFlights().size() > 0) {
//            Location loc = new Location(LocationManager.GPS_PROVIDER);
//            loc.setLatitude(mPresenter.getLoadedTrip().getFlights().get(0).getDestination().getLatitude());
//            loc.setLongitude(mPresenter.getLoadedTrip().getFlights().get(0).getDestination().getLongitude());
//            moveCameraToLocation(loc);
//
//            Toast.makeText(this, "Aiming map to " + mPresenter.getLoadedTrip().getFlights().get(0).getDestination().getName(), Toast.LENGTH_SHORT).show();
//
//        } else
//            Toast.makeText(this, "Map ready, trip has no flights!", Toast.LENGTH_SHORT).show();

        //mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setOnMarkerClickListener(marker -> {       //Disable marker clicks
            return true;
        });
        mMap.setPadding(0, 0, 0, mViewPager.getHeight());
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.setMinZoomPreference(0);
        //mMap.setMaxZoomPreference(zoomLevel);
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                float newZoom = mMap.getCameraPosition().zoom;
                if(mOldZoom != newZoom) {
                    Timber.d("Zoom changed to= %f" , mMap.getCameraPosition().zoom);
                    mOldZoom = newZoom;
                }
            }
        });

        mPresenter.mapReady();
        //moveCameraToLocation(cameraStart);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraStart.toLatLng(), zoomLevel));
//
//        // Flat markers will rotate when the map is rotated,
//        // and change perspective when the map is tilted.
//        mMap.addMarker(new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_plane_large))
//                .position(mapCenter)
//                .flat(true)
//                .rotation(245));

//        CameraPosition cameraPosition = CameraPosition.builder()
//                .target(mapCenter2)
//                //.zoom(zoomLevel)
//                //.bearing(90)
//                .build();

//
//
//        new Handler().postDelayed(() -> {
//            //CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(mapCenter2, zoomLevel);
//            //mMap.animateCamera(cu, animationDuration, null);
//            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.0810,80.2740), zoomLevel), 4000, null);
//
//            //mMap.animateCamera(CameraUpdateFactory.scrollBy(-1000, 0), animationDuration, null);
//            animateCamera(cameraDestination);
//
//            //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), animationDuration, null);
//        }, 2000);

//        new Handler().postDelayed(() -> {
//            animateCamera(cameraDestination);
//        }, 2000);

    }


    private void animateCamera(IntegerLatLng destination) {


        moveCameraToLocation(destination, true, 500);


        mAirplaneBearing++;
        if (mAirplaneBearing == 1) {
            mAirplane.showAirplane(true);
        }
        if (mAirplaneBearing == 50) {
            mAirplane.setBearing(true, 90);
        }
        if (mAirplaneBearing == 70) {
            mAirplane.setBearing(true, 240);
        }
        if (mAirplaneBearing == 100) {
            mAirplane.hideAirplane(true);
        }


//        if(mAirplaneBearing < 300) {
//            mAirplane.setBearing(mAirplaneBearing);
//            mAirplaneBearing += 1;
//            if (mAirplaneBearing == 299)
//                mAirplane.hideAirplane();
//        }

        IntegerLatLng current = new IntegerLatLng(mMap.getCameraPosition().target);

        if(current.compareTo(destination) != 0) {
            current.stepTo(incrementStep, destination);

//            Timber.d("Lat=%f, Long=%f. Animating to Lat=%d, Long=%d",
//                    mMap.getCameraPosition().target.latitude,
//                    mMap.getCameraPosition().target.longitude,
//                    current.getLatitude(),
//                    current.getLongitude());

            mMap.animateCamera(CameraUpdateFactory.newLatLng(current.toLatLng()), animationDelay, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    animateCamera(destination);
                }

                @Override
                public void onCancel() {}
            });
        }


    }

}

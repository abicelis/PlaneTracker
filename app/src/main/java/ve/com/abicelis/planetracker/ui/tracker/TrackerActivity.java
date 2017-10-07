package ve.com.abicelis.planetracker.ui.tracker;

import android.content.res.Resources;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

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
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.base.BaseActivity;
import ve.com.abicelis.planetracker.ui.customviews.AirplaneView;
import ve.com.abicelis.planetracker.util.SnackbarUtil;

/**
 * Created by abicelis on 27/9/2017.
 */

public class TrackerActivity extends BaseActivity implements
        TrackerMvpView,
        OnMapReadyCallback
{

    //UI
    @BindView(R.id.activity_tracker_container)
    RelativeLayout mContainer;
    @BindView(R.id.activity_tracker_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_tracker_plane)
    AirplaneView mAirplane;
    @BindView(R.id.activity_tracker_viewpager)
    ViewPager mViewPager;
    private TrackerViewPagerAdapter mTrackerViewPagerAdapter;

    SupportMapFragment mMapFragment;
    GoogleMap mMap;


    //DATA
    @Inject
    TrackerPresenter mPresenter;
    float zoomLevel = 13;
    int mStepMillis = 50;
    private boolean mAnimatingCamera;



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

        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.setOnMarkerClickListener(marker -> { return true;});   //Disable marker clicks
        mMap.setPadding(0, 0, 0, mViewPager.getHeight());           //Pad map with bottom viewpager

        mPresenter.mapReady();
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
    public void moveCameraToLocation(@NonNull LatLng latLng, boolean restrictZoom, int delay, @Nullable GoogleMap.CancelableCallback callback) {
        setRestrictZoom(restrictZoom);

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(latLng)
                .zoom(zoomLevel)
                .bearing(0)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), delay, callback);
    }

    @Override
    public void drawRouteAndMoveCameraToBoundsOf(Airport origin, Airport destination) {
        mMap.clear();

        //Add Markers
        mMap.addMarker(new MarkerOptions().position(origin.getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_airport_marker_origin_32)));
        mMap.addMarker(new MarkerOptions().position(destination.getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_airport_marker_destination_32)));

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
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
        SnackbarUtil.showSnackbar(mContainer, message.getMessageType(), message.getFriendlyNameRes(), SnackbarUtil.SnackbarDuration.SHORT, callback);
    }

    @Override
    public void animateCamera(Flight flight, int delay) {

        GoogleMap.CancelableCallback callback = new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                setRestrictZoom(true);
                mAnimatingCamera = true;
                doAnimateCamera(flight.getOrigin().getLatLng(),
                        flight.getDestination().getLatLng(),
                        flight.getElapsedFraction(),
                        flight.getFractionStepForMillis(mStepMillis));
            }

            @Override
            public void onCancel() {

            }
        };

        mAirplane.showAirplane(true);
        LatLng position = SphericalUtil.interpolate(flight.getOrigin().getLatLng(), flight.getDestination().getLatLng(), flight.getElapsedFraction());
        double bearing = SphericalUtil.computeHeading(position, flight.getDestination().getLatLng());
        mAirplane.setBearing(false, (int)bearing);

        moveCameraToLocation(position, false, delay, callback);
    }

    @Override
    public void stopAnimatingCamera() {
        mAnimatingCamera = false;
        mMap.stopAnimation();
        mAirplane.hideAirplane(true);
    }

    private void doAnimateCamera(LatLng origin, LatLng destination, double elapsedFraction, double fractionStep) {
        LatLng position = SphericalUtil.interpolate(origin, destination, elapsedFraction);

        double bearing = SphericalUtil.computeHeading(position, destination);
        mAirplane.setBearing(false, (int)bearing);

        if(!mAnimatingCamera)
            return;

        if (elapsedFraction > 1) {
            Toast.makeText(this, "Flight arrived!", Toast.LENGTH_SHORT).show();
            mPresenter.flightChanged(mViewPager.getCurrentItem());
            mAnimatingCamera = false;
            return;
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel), mStepMillis, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                doAnimateCamera(origin, destination, elapsedFraction+fractionStep, fractionStep);
            }

            @Override
            public void onCancel() {}
        });

    }
}

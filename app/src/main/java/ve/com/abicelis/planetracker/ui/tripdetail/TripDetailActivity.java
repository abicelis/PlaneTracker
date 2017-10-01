package ve.com.abicelis.planetracker.ui.tripdetail;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.data.model.Flight;
import ve.com.abicelis.planetracker.data.model.FlightViewModel;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.ui.base.BaseActivity;
import ve.com.abicelis.planetracker.ui.changeimage.ChangeImageActivity;
import ve.com.abicelis.planetracker.ui.common.flight.FlightAdapter;
import ve.com.abicelis.planetracker.ui.flight.FlightActivity;
import ve.com.abicelis.planetracker.ui.tracker.TrackerActivity;
import ve.com.abicelis.planetracker.util.AnimationUtil;
import ve.com.abicelis.planetracker.util.ImageUtil;
import ve.com.abicelis.planetracker.util.SnackbarUtil;

/**
 * Created by abicelis on 18/9/2017.
 */

public class TripDetailActivity extends BaseActivity implements TripDetailMvpView, AppBarLayout.OnOffsetChangedListener{

    //DATA
    private static final float PERCENTAGE_TO_SHOW_BOTTOM_FAB = 0.6f;
    private static final float PERCENTAGE_TO_SHOW_SUBTITLE = 0.05f;
    private boolean mIsTheSubtitleVisible = true;


    @Inject
    TripDetailPresenter mPresenter;

    //UI
    @BindView(R.id.activity_trip_detail_appbar)
    AppBarLayout mAppBar;
    @BindView(R.id.activity_trip_detail_subtitle)
    TextView mSubtitle;
    @BindView(R.id.activity_trip_detail_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_trip_detail_container)
    CoordinatorLayout mContainer;
    @BindView(R.id.activity_trip_detail_image)
    ImageView mImage;
    @BindView(R.id.activity_trip_detail_no_image)
    TextView mNoImage;

    @BindView(R.id.activity_trip_detail_no_items_container)
    LinearLayout mNoItemsContainer;
    @BindView(R.id.activity_trip_detail_recycler)
    RecyclerView mRecycler;
    private LinearLayoutManager mLayoutManager;
    private FlightAdapter mFlightAdapter;

    @BindView(R.id.activity_trip_detail_fab_map)
    FloatingActionButton mFabMap;
    FloatingActionButton.Behavior mFabMapBehavior;
    @BindView(R.id.activity_trip_detail_fab_map_bottom)
    FloatingActionButton mFabMapBottom;
    private boolean mFabMapBottomEnabled = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        ButterKnife.bind(this);
        getPresenterComponent().inject(this);
        mPresenter.attachView(this);

        initViews();


        long tripId = getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_TRIP_DETAIL_TRIP_ID, -1);
        if(tripId != -1) {
            mPresenter.setTripId(tripId);
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    finish();
                }
            };
            showMessage(Message.ERROR_LOADING_TRIP, callback);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.reloadTrip();
    }

    private void initViews() {

        //Hook into appbar movement to hide/show views
        mAppBar.addOnOffsetChangedListener(this);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)mFabMap.getLayoutParams();
        mFabMapBehavior = new FloatingActionButton.Behavior();
        params.setBehavior(mFabMapBehavior);

        mFabMap.setOnClickListener(view -> {
            handleGoToMap();
        });
        mFabMapBottom.setOnClickListener(view -> {
            handleGoToMap();
        });

        setUpToolbar();
        setUpRecyclerView();

    }


    private void setUpToolbar() {
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mFlightAdapter = new FlightAdapter(this);
        mFlightAdapter.setFlightClickedListener(new FlightAdapter.FlightClickedListener() {
            @Override
            public void onFlightClicked(Flight flight) {
                if(mPresenter.isInEditMode()) {
                    handleEditShowMenu(flight);
                }
            }

            @Override
            public void onFlightLongClicked(Flight flight) {
                if(mPresenter.isInEditMode()) {
                    handleEditShowMenu(flight);
                }
            }
        });

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mFlightAdapter);
    }

    private void handleEditShowMenu(Flight flight) {
        CharSequence options[] = new CharSequence[] {getString(R.string.activity_trip_detail_flight_context_edit), getString(R.string.activity_trip_detail_flight_context_delete)};

        AlertDialog.Builder contextDialog = new AlertDialog.Builder(this)
                .setItems(options, (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    switch (i){
                        case 0:
                            editModeToggled();
                            Intent addFlightIntent = new Intent(this, FlightActivity.class);
                            addFlightIntent.putExtra(Constants.EXTRA_ACTIVITY_FLIGHT_TRIP_ID, flight.getTripId());
                            addFlightIntent.putExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_ID, flight.getId());
                            addFlightIntent.putExtra(Constants.EXTRA_ACTIVITY_FLIGHT_FLIGHT_POSITION, flight.getOrderInTrip());
                            startActivity(addFlightIntent);
                            break;

                        case 1:
                            AlertDialog dialog = new AlertDialog.Builder(this)
                                    .setTitle(getString(R.string.activity_trip_detail_delete_flight_title))
                                    .setMessage(getString(R.string.activity_trip_detail_delete_flight_message))
                                    .setPositiveButton(getString(R.string.dialog_delete), (d, w) -> {
                                        mPresenter.deleteFlight(flight);
                                    })
                                    .setNegativeButton(getString(R.string.dialog_cancel), (d, w) -> {
                                        d.dismiss();
                                    })
                                    .create();
                            dialog.show();
                            break;
                    }
                });
        contextDialog.show();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(mPresenter.isInEditMode())
            getMenuInflater().inflate(R.menu.menu_activity_trip_detail_edit, menu);
        else
            getMenuInflater().inflate(R.menu.menu_activity_trip_detail, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_trip_detail_share:
                Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_trip_detail_edit:
                mPresenter.editModeToggled();
                break;

            case R.id.menu_trip_detail_change_image:
                Intent intent = new Intent(this, ChangeImageActivity.class);
                intent.putExtra(Constants.EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_NAME, mPresenter.getLoadedTrip().getName());
                intent.putExtra(Constants.EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_ID, mPresenter.getLoadedTrip().getId());
                startActivityForResult(intent, Constants.RESULT_ACTIVITY_CHANGE_IMAGE_TRIP);
                break;

            case R.id.menu_trip_detail_change_name:
                EditTripNameDialogFragment dialog = EditTripNameDialogFragment.newInstance(mPresenter.getLoadedTrip().getName());
                dialog.setListener(tripName -> {
                    mPresenter.changeTripName(tripName);
                });
                dialog.show(getSupportFragmentManager(), "EditTripNameDialogFragment");
                break;

            case R.id.menu_trip_detail_delete:
                AlertDialog deleteDiag = new AlertDialog.Builder(TripDetailActivity.this)
                        .setTitle(getResources().getString(R.string.dialog_trip_detail_activity_delete_trip_title))
                        .setMessage(getResources().getString(R.string.dialog_trip_detail_activity_delete_trip_message))
                        .setPositiveButton(getResources().getString(R.string.dialog_delete),  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.deleteTrip();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                deleteDiag.show();
                break;


            // Used in edit mode
            case R.id.menu_trip_detail_edit_cancel:
                mPresenter.discardEditModeChanges();
                //handleEditModeDiscard(false);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if(mFabMapBottomEnabled) {
            if (percentage >= PERCENTAGE_TO_SHOW_BOTTOM_FAB)
                mFabMapBottom.show();
            else
                mFabMapBottom.hide();
        }

        if (percentage <= PERCENTAGE_TO_SHOW_SUBTITLE) {
            if(!mIsTheSubtitleVisible) {
                AnimationUtil.startAlphaAnimation(mSubtitle, Constants.ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheSubtitleVisible = true;
            }

        } else {
            if (mIsTheSubtitleVisible) {
                AnimationUtil.startAlphaAnimation(mSubtitle, Constants.ALPHA_ANIMATIONS_DURATION, View.GONE);
                mIsTheSubtitleVisible = false;
            }
        }

    }

    private void handleGoToMap(){
        Intent goToTrackerActivity = new Intent(this, TrackerActivity.class);
        goToTrackerActivity.putExtra(Constants.EXTRA_ACTIVITY_TRACKER_TRIP_ID, mPresenter.getLoadedTrip().getId());
        startActivity(goToTrackerActivity);
    }

    public void showNoFlights(boolean show) {
        if(show) {
            mNoItemsContainer.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        } else {
            mNoItemsContainer.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.RESULT_ACTIVITY_CHANGE_IMAGE_TRIP){
            if (resultCode == RESULT_OK) {
                //Reload the image
                mPresenter.reloadTripImage();
            }
        }
    }

    public void editModeToggled(){
        mPresenter.editModeToggled();
    }

    /* TripDetailMvpView implementation */

    @Override
    public void showTrip(Trip trip, List<FlightViewModel> flights) {
        mToolbar.setTitle(trip.getName());
        mSubtitle.setText(trip.getInfo(this));

        reloadTripImage(trip.getImage());

        mFlightAdapter.setTripId(trip.getId());
        mFlightAdapter.getItems().clear();
        mFlightAdapter.getItems().addAll(flights);
        mFlightAdapter.notifyDataSetChanged();

        if (mPresenter.tripHasFlights())
            showNoFlights(false);
        else
            showNoFlights(true);
    }


    @Override
    public void reloadTripImage(byte[] image) {
        if(image != null && image.length > 0) {
            mImage.setImageBitmap(ImageUtil.getBitmap(image));
            mNoImage.setVisibility(View.GONE);

        } else {
            mImage.setImageResource(R.drawable.error);
            mNoImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void reloadTripName(String tripName) {
        //TODO: fix this eventually? recreate() is a hack but it works.
        //Apparently setting the toolbar title does not work. Maybe because it is linked to an appbar/collapsetoolbarlayout?
        //Who knows...
        recreate();
        //mToolbar.setTitle(tripName);
    }

    @Override
    public void activateEditMode() {
        if(mPresenter.tripHasFlights())
            showMessage(Message.NOTICE_LONG_CLICK_FLIGHT, null);

        if (!mPresenter.tripHasFlights())
            showNoFlights(false);

        mFlightAdapter.toggleEditMode(true);
        invalidateOptionsMenu();

        //Handle fabs
        mFabMapBehavior.setAutoHideEnabled(false);
        mFabMap.hide();
        mFabMapBottomEnabled = false;
        mFabMapBottom.hide();
    }

    @Override
    public void deactivateEditMode() {
        mFlightAdapter.toggleEditMode(false);
        invalidateOptionsMenu();

        if (!mPresenter.tripHasFlights())
            showNoFlights(true);

        //Handle fabs
        mFabMapBehavior.setAutoHideEnabled(true);
        mFabMap.show();
        mFabMapBottomEnabled = true;
        mFabMapBottom.show();

    }

    @Override
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
        SnackbarUtil.showSnackbar(mContainer, message.getMessageType(), message.getFriendlyNameRes(), SnackbarUtil.SnackbarDuration.SHORT, callback);
    }

    @Override
    public void tripDeletedSoFinish() {
        BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                finish();
            }
        };
        showMessage(Message.SUCCESS_TRIP_DELETED, callback);
    }
}

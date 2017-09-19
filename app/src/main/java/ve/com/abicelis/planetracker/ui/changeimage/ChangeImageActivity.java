package ve.com.abicelis.planetracker.ui.changeimage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.application.Message;
import ve.com.abicelis.planetracker.ui.base.BaseActivity;
import ve.com.abicelis.planetracker.util.SnackbarUtil;

/**
 * Created by abicelis on 5/9/2017.
 */

public class ChangeImageActivity extends BaseActivity implements ChangeImageMvpView {


    //DATA
    @Inject
    ChangeImagePresenter mPresenter;

    @BindView(R.id.activity_change_image_container)
    LinearLayout mContainer;

    @BindView(R.id.activity_change_image_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_change_image_no_items_container)
    RelativeLayout mNoItemsContainer;
    @BindView(R.id.activity_change_image_swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;
    @BindView(R.id.activity_change_image_recycler)
    RecyclerView mRecycler;
    private GridLayoutManager mLayoutManager;
    private ImageAdapter mImageAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_image);
        ButterKnife.bind(this);
        getPresenterComponent().inject(this);
        mPresenter.attachView(this);

        setUpRecyclerView();

        if(getIntent().hasExtra(Constants.EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_ID) && getIntent().hasExtra(Constants.EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_NAME)) {
            mPresenter.setTripData(
                    getIntent().getLongExtra(Constants.EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_ID, -1),
                    getIntent().getStringExtra(Constants.EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_NAME));
            setUpToolbar();
            mPresenter.loadImages();
        } else {
            BaseTransientBottomBar.BaseCallback<Snackbar> callback = new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    finish();
                }
            };
            showMessage(Message.ERROR_UNEXPECTED, callback);
            Timber.e("Unexpected error. Invalid parameters sent to this activity. Need EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_ID and EXTRA_ACTIVITY_CHANGE_IMAGE_TRIP_NAME");
        }
    }



    private void setUpToolbar() {
        //Setup toolbar
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(String.format(getString(R.string.activity_change_image_title), mPresenter.getTripName()));
    }

    private void setUpRecyclerView() {

        mLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        mImageAdapter = new ImageAdapter(this);
        mImageAdapter.setImageSelectedListener(new ImageAdapter.ImageSelectedListener() {
            @Override
            public void onImageSelected(String imageUrl) {
                mPresenter.imageSelected(imageUrl);
            }
        });

        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mImageAdapter);

        mSwipeRefresh.setEnabled(false);
        mSwipeRefresh.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                               @Override
                                               public void onRefresh() {
                                                   mPresenter.loadImages();
                                               }
                                           }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_change_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_change_image_edit:
                showEditImageSearchQueryDialog();
                return true;
        }
        return false;
    }

    private void showEditImageSearchQueryDialog() {
        FragmentManager fm = this.getSupportFragmentManager();

        EditImageSearchQueryDialogFragment dialog = EditImageSearchQueryDialogFragment.newInstance(mPresenter.getTripName());

        dialog.setListener(new EditImageSearchQueryDialogFragment.SearchQuerySetListener() {
            @Override
            public void onSearchQuerySet(String query) {
                mPresenter.setTripName(query);
                mPresenter.loadImages();
            }
        });
        dialog.show(fm, "EditImageSearchQueryDialogFragment");
    }


    /* ChangeImageMvpView implementation */

    @Override
    public void showLoading(boolean loading) {
        mSwipeRefresh.setRefreshing(loading);
    }

    @Override
    public void showMessage(Message message, @Nullable BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
        SnackbarUtil.showSnackbar(mContainer, message.getMessageType(), message.getFriendlyNameRes(), SnackbarUtil.SnackbarDuration.SHORT, callback);
    }

    @Override
    public void showImages(List<String> imageUrls) {
        mSwipeRefresh.setRefreshing(false);
        mImageAdapter.getItems().clear();
        mImageAdapter.getItems().addAll(imageUrls);
        mImageAdapter.notifyDataSetChanged();

        if(mImageAdapter.getItems().size() == 0) {
            mNoItemsContainer.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        } else {
            mNoItemsContainer.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void imageSavedSoFinish() {
        setResult(RESULT_OK);
        finish();
    }
}

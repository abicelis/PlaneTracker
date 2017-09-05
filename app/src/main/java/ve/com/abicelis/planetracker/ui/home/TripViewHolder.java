package ve.com.abicelis.planetracker.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.application.Constants;
import ve.com.abicelis.planetracker.data.model.Trip;
import ve.com.abicelis.planetracker.util.ImageUtil;

/**
 * Created by abicelis on 4/9/2017.
 */

public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //DATA
    private TripAdapter mAdapter;
    private Activity mActivity;
    private Trip mCurrent;
    private int mPosition;

    //UI
    @BindView(R.id.list_item_trip_container)
    RelativeLayout mContainer;
    @BindView(R.id.list_item_trip_image)
    ImageView mImage;
    @BindView(R.id.list_item_trip_name)
    TextView mName;
    @BindView(R.id.list_item_trip_info)
    TextView mInfo;
    @BindView(R.id.list_item_trip_menu)
    ImageView mMenu;


    public TripViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(TripAdapter adapter, Activity activity, Trip current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        if(mCurrent.getImage() != null && mCurrent.getImage().length != 0)
            mImage.setImageBitmap(ImageUtil.getBitmap(mCurrent.getImage()));
        mName.setText(mCurrent.getName());
        mInfo.setText(mCurrent.getInfo(activity));
    }

    public void setListeners() {
        mContainer.setOnClickListener(this);
        mMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.list_item_trip_container:
                Toast.makeText(mActivity, "Trip clicked! "+ mCurrent.getName(), Toast.LENGTH_SHORT).show();

//                Intent viewRecipeDetailIntent = new Intent(mActivity, TripDetailActivity.class);
//                viewRecipeDetailIntent.putExtra(Constants.SOMECONSTANT, mCurrent);
//                mActivity.startActivity(viewRecipeDetailIntent);
                break;
            case R.id.list_item_trip_menu:
                Toast.makeText(mActivity, "Menu clicked!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
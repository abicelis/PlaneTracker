package ve.com.abicelis.planetracker.ui.changeimage;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import ve.com.abicelis.planetracker.R;

/**
 * Created by abicelis on 4/9/2017.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //DATA
    private ImageAdapter mAdapter;
    private Activity mActivity;
    private String mCurrent;
    private int mPosition;

    //UI
    @BindView(R.id.list_item_image)
    ImageView mImage;


    public ImageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setData(ImageAdapter adapter, Activity activity, String current, int position) {
        mAdapter = adapter;
        mActivity = activity;
        mCurrent = current;
        mPosition = position;

        Picasso.with(activity)
                .load(mCurrent)
                .error(R.drawable.error)
                .fit().centerCrop()
                .into(mImage);
    }

    public void setListeners() {
        mImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.list_item_image:
                mAdapter.triggerImageSelected(mCurrent);
                break;
        }
    }
}
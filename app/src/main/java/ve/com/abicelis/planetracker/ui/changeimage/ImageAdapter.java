package ve.com.abicelis.planetracker.ui.changeimage;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.planetracker.R;

/**
 * Created by abicelis on 4/9/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageViewHolder> {

    //DATA
    private ImageSelectedListener mListener;
    private List<String> mImageUrls = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity mActivity;


    public ImageAdapter(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(mInflater.inflate(R.layout.list_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.setData(this, mActivity, mImageUrls.get(position), position);
        holder.setListeners();
    }

    public List<String> getItems() {
        return mImageUrls;
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }


    public void triggerImageSelected(String imageUrl) {
        if(mListener != null)
            mListener.onImageSelected(imageUrl);
    }

    public void setImageSelectedListener(ImageSelectedListener mListener) {
        this.mListener = mListener;
    }

    interface ImageSelectedListener{
        void onImageSelected(String imageUrl);
    }
}

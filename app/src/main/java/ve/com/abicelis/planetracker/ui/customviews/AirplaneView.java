package ve.com.abicelis.planetracker.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import ve.com.abicelis.planetracker.R;

/**
 * Created by abicelis on 29/9/2017.
 */

public class AirplaneView extends RelativeLayout {

    //CONST
    private static final int VISIBILITY_DURATION = 300;
    private static final int ROTATION_DURATION = 200;

    //UI
    @BindView(R.id.airplane_view_airplane)
    ImageView mPlane;
    @DrawableRes int mPlaneSrc;
    Matrix mMatrix;
    int mPlaneSize;
    int mPlaneBearing = 0;
    float mPlaneScale;              //Scaled size 1.0 to 0.0 of plane with respect to RelativeLayout container.
    boolean initDone = false;

    /* Constructors and init */
    public AirplaneView(Context context) {
        super(context);
        init(context, null);
    }

    public AirplaneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AirplaneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.airplane_view, this);
        ButterKnife.bind(this);

        //Get/apply custom xml configs
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.airplane_view);
        mPlaneScale = a.getFloat(R.styleable.airplane_view_plane_size, 0.8f);
        mPlaneSrc = a.getResourceId(R.styleable.airplane_view_src, -1);
        a.recycle();

        if(mPlaneSrc == -1)
            Timber.e("Src not defined, must define a drawable source!");

        mPlane.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mPlane.setImageDrawable(ContextCompat.getDrawable(getContext(), mPlaneSrc));

        //mPlane.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.swipe_refresh_green));
        //setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent_loading));


        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // method called more than once, but the values only need to be initialized one time
                if (!initDone) {
                    initDone = true;

                    //Grab the shortest of the container dimensions
                    int minContainerDimen = Math.min(getHeight(), getWidth());
                    mPlaneSize = (int)((float)minContainerDimen*mPlaneScale);

                    //Resize the plane's imageview
                    mPlane.getLayoutParams().height = mPlaneSize;
                    mPlane.getLayoutParams().width = mPlaneSize;
                    mPlane.requestLayout();

//                    //Resize the src plane drawable and set it
//                    Drawable src = ContextCompat.getDrawable(getContext(), mPlaneSrc);
//                    Drawable scaledSrc = resizeDrawable(src, mPlaneSize);
//                    mPlane.setImageDrawable(scaledSrc);

                }
            }
        });



    }

    public void setBearing(boolean animate, int bearing) {
        //Normalize
        bearing = bearing % 360;
        bearing = (bearing + 360) % 360;

        if(mPlaneBearing == bearing)
            return;
        if(animate)
            mPlane.animate().setInterpolator(new LinearInterpolator()).rotation(bearing).setDuration(ROTATION_DURATION).start();
        else
            mPlane.animate().setInterpolator(new LinearInterpolator()).rotation(bearing).setDuration(0).start();

        mPlaneBearing = bearing;
    }

    public void hideAirplane(boolean animate) {
        if(animate) {
            mPlane.animate().scaleX(0).scaleY(0).setDuration(VISIBILITY_DURATION).start();
        } else {
            mPlane.animate().scaleX(0).scaleY(0).setDuration(0).start();
        }
    }
    public void showAirplane(boolean animate) {
       if(animate) {
            mPlane.animate().scaleX(1).scaleY(1).setDuration(VISIBILITY_DURATION).start();
       } else {
           mPlane.animate().scaleX(1).scaleY(1).setDuration(0).start();
       }
    }


    /* Internal methods */
    private Drawable resizeDrawable(Drawable image, int destinationSize) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, destinationSize, destinationSize, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
    private float getScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }
}

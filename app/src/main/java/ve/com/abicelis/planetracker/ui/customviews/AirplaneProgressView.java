package ve.com.abicelis.planetracker.ui.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.data.model.FlightProgressViewModel;
import ve.com.abicelis.planetracker.util.ViewUtil;

/**
 * Created by abicelis on 30/9/2017.
 */

public class AirplaneProgressView extends FrameLayout {



    //
    //
    //     ----o-------PLANE------------------o----
    //
    //     /---/                              /---/
    //       ^                                  ^
    //       |----------------------------------|
    //       |
    //     mCircleMargin
    //
    //
    //


    //CONST
    private static final int LINE_WIDTH_DP = 2;
    private static final int TEXT_SIZE_SP = 12;

    //DATA
    private FlightProgressViewModel mFlightProgressModel;

    //UI
    private ImageView mAirplane;
    private int mCircleRadius = (int)ViewUtil.convertDpToPixel(10);
    private int mCircleMargin = (int)ViewUtil.convertDpToPixel(48);
    private int mAirplaneMargin = (int)ViewUtil.convertDpToPixel(80);
    private Paint mLinePaint;
    private Paint mTextPaint;
    private boolean initDone;
    private float mCanvasHeight;
    private float mCanvasVerticalCenter;
    private float mCanvasWidth;



    public AirplaneProgressView(Context context) {
        super(context);
        init(context, null);
    }
    public AirplaneProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public AirplaneProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, @Nullable AttributeSet attrs) {
        mLinePaint = new Paint();
        mLinePaint.setColor(ContextCompat.getColor(getContext(), R.color.white_text));
        mLinePaint.setStrokeWidth(ViewUtil.convertDpToPixel(LINE_WIDTH_DP));
        mLinePaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.white_text));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(ViewUtil.convertDpToPixel(TEXT_SIZE_SP));
        //mTextPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));   //Roboto light


        setWillNotDraw(false);  //Force viewgroup to call onDraw!








        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // method called more than once, but the values only need to be initialized one time
                if (!initDone) {
                    initDone = true;

                    //Grab container dimensions
                    mCanvasHeight = getHeight();
                    mCanvasWidth = getWidth();

                    mCanvasVerticalCenter = mCanvasHeight/2;


                    mAirplane = new ImageView(context);
                    mAirplane.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_plane));
                    mAirplane.setRotation(90);
                    // mAirplane.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    addView(mAirplane);

                    LayoutParams lp = (LayoutParams) mAirplane.getLayoutParams();
                    lp.height = (int)mCanvasHeight*8/10;
                    lp.width = (int)mCanvasHeight*8/10;
                    lp.gravity = Gravity.CENTER_VERTICAL;
                    mAirplane.setLayoutParams(lp);

                }
            }
        });
    }

    public void setProgressModel(FlightProgressViewModel flightProgressModel) {
        mFlightProgressModel = flightProgressModel;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(mCircleMargin+mCircleRadius, mCanvasVerticalCenter, mCanvasWidth-mCircleMargin-mCircleRadius, mCanvasVerticalCenter, mLinePaint);
        canvas.drawCircle(mCircleMargin, mCanvasVerticalCenter, mCircleRadius, mLinePaint);
        canvas.drawCircle(mCanvasWidth-mCircleMargin, mCanvasVerticalCenter, mCircleRadius, mLinePaint);
        canvas.drawText(mFlightProgressModel.getFlight().getOrigin().getIata(), mCircleMargin, mCanvasVerticalCenter/2, mTextPaint);
        canvas.drawText(mFlightProgressModel.getFlight().getDestination().getIata(), mCanvasWidth-mCircleMargin, mCanvasVerticalCenter/2, mTextPaint);

        if (!mFlightProgressModel.isFirstFlight())
            canvas.drawLine(0, mCanvasVerticalCenter, mCircleMargin-mCircleRadius, mCanvasVerticalCenter, mLinePaint);

        if (!mFlightProgressModel.isLastFlight())
            canvas.drawLine(mCanvasWidth-mCircleMargin+mCircleRadius, mCanvasVerticalCenter, mCanvasWidth, mCanvasVerticalCenter, mLinePaint);



        switch (mFlightProgressModel.getFlight().getStatus()) {

            case NOT_DEPARTED:
                LayoutParams lp = (LayoutParams) mAirplane.getLayoutParams();
                lp.setMarginStart((int)(mAirplaneMargin - mAirplane.getWidth()/2));
                mAirplane.setLayoutParams(lp);
                break;
            case ARRIVED:
                LayoutParams lp2 = (LayoutParams) mAirplane.getLayoutParams();
                lp2.setMarginStart((int)(mCanvasWidth - mAirplaneMargin - mAirplane.getWidth()/2));
                mAirplane.setLayoutParams(lp2);
                break;
            case IN_AIR:
                LayoutParams lp3 = (LayoutParams) mAirplane.getLayoutParams();
                int progress = (int) ((mCanvasWidth - (mAirplaneMargin*2)) * ((float)mFlightProgressModel.getFlight().getTheoreticalProgress()/100));
                lp3.setMarginStart(mAirplaneMargin + progress - mAirplane.getWidth()/2);
                mAirplane.setLayoutParams(lp3);
                break;
        }

    }


}

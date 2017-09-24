package ve.com.abicelis.planetracker.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.util.ViewUtil;

/**
 * Created by abicelis on 20/9/2017.
 */

public class CustomTextView2 extends AppCompatTextView {

    //DATA
    @DrawableRes int mIconRes = -1;
    @ColorRes int mIconTintRes = -1;
    int mPadding = (int)ViewUtil.convertDpToPixel(6);   //Default 6dp padding


    public CustomTextView2(Context context) {
        super(context);
        init(context, null);
    }
    public CustomTextView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public CustomTextView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        //Get/apply custom xml configs
        TypedArray a =      context.obtainStyledAttributes(attrs, R.styleable.custom_text_view);
        int hintColor =     a.getResourceId(R.styleable.custom_text_view_hintcolor, -1);
        int textColor =     a.getResourceId(R.styleable.custom_text_view_textcolor, -1);
        int iconId =        a.getResourceId(R.styleable.custom_text_view_icon, -1);
        int iconTintId =    a.getResourceId(R.styleable.custom_text_view_icontint, -1);
        int textId =        a.getResourceId(R.styleable.custom_text_view_text, -1);
        int hintId =        a.getResourceId(R.styleable.custom_text_view_hint, -1);
        int maxLines =      a.getInt(R.styleable.custom_text_view_maxlines, -1);


        setHintTextColor(hintColor);
        setTextColor(textColor);

        mIconRes = iconId;
        mIconTintRes = iconTintId;
        setIcon(mIconRes, mIconTintRes);
        if(textId != -1)
            setText(textId);
        if(hintId != -1)
            setHint(hintId);
        setLinesMax(maxLines);
        a.recycle();

        //Set default attributes
        setPadding(mPadding, mPadding, mPadding, mPadding);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundResource(R.drawable.round_edges_background);
    }





    public void setIcon(@DrawableRes int iconRes, @ColorRes int iconTintRes) {
        if (iconRes != -1) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), iconRes);
            setCompoundDrawablePadding(mPadding);   //padding between editText and its icon

            if(iconTintRes != -1) {         //Get the drawable and tint it
                Drawable wrapDrawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(wrapDrawable.mutate(), ContextCompat.getColor(getContext(), iconTintRes));
                setCompoundDrawablesWithIntrinsicBounds(wrapDrawable, null, null, null);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        }
    }

    public void setMaxLength(int length) {
        setFilters( new InputFilter[] { new InputFilter.LengthFilter(length) } );
    }

    public void setLinesMax(int maxLines) {
        if(maxLines != -1) {
            setMaxLines(maxLines);
            if(maxLines == 1)
                setSingleLine();
        }
    }



    public void setDim(boolean dim) {
        if(dim) {


            setIcon(mIconRes, R.color.custom_edit_text_dim_foreground);

//            Drawable[] drawables = getCompoundDrawables();
//            Drawable wrapDrawable = DrawableCompat.wrap(drawables[0]);
//            DrawableCompat.setTint(wrapDrawable.mutate(), ContextCompat.getColor(getContext(),R.color.custom_edit_text_dim_foreground));
//            setCompoundDrawablesWithIntrinsicBounds(wrapDrawable, null, null, null);

            setBackgroundResource(R.drawable.round_edges_background_dim);
            setTextColor(ContextCompat.getColor(getContext(), R.color.custom_edit_text_dim_foreground));
            setHintTextColor(ContextCompat.getColor(getContext(), R.color.custom_edit_text_dim_foreground));
        } else {

//            Drawable[] drawables = getCompoundDrawables();
//            Drawable wrapDrawable = DrawableCompat.wrap(drawables[0]);
//            DrawableCompat.setTint(wrapDrawable.mutate(), ContextCompat.getColor(getContext(), R.color.custom_edit_text_icon));
//            setCompoundDrawablesWithIntrinsicBounds(wrapDrawable, null, null, null);

            setIcon(mIconRes, mIconTintRes);

            setBackgroundResource(R.drawable.round_edges_background);
            setTextColor(ContextCompat.getColor(getContext(), R.color.custom_edit_text_text));
            setHintTextColor(ContextCompat.getColor(getContext(), R.color.custom_edit_text_hint));
        }
    }
}

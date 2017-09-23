package ve.com.abicelis.planetracker.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;

/**
 * Created by abicelis on 20/9/2017.
 */

public class CustomTextView extends LinearLayout {


    @BindView(R.id.custom_textview_icon)
    ImageView mIcon;

    @BindView(R.id.custom_textview_text)
    TextView mTextView;

    public CustomTextView(Context context) {
        super(context);
        init(context, null);
    }
    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_textview, this);

        ButterKnife.bind(this);

        //Get/apply custom xml configs
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.custom_text_view);
        int hintColor = a.getResourceId(R.styleable.custom_text_view_hintcolor, -1);
        int textColor = a.getResourceId(R.styleable.custom_text_view_textcolor, -1);
        int iconId = a.getResourceId(R.styleable.custom_text_view_icon, -1);
        int textId = a.getResourceId(R.styleable.custom_text_view_text, -1);
        int hintId = a.getResourceId(R.styleable.custom_text_view_hint, -1);
        int maxLines = a.getInt(R.styleable.custom_text_view_maxlines, -1);


        setHintColor(hintColor);
        setTextColor(textColor);
        setIcon(iconId);
        setText(textId);
        setHint(hintId);
        setMaxLines(maxLines);
        a.recycle();

        //Set attributes of this (LinearLayout)
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        this.setBackgroundResource(R.drawable.round_edges_background);
    }

    public void setHintColor(@ColorRes int colorRes) {
        if (colorRes != -1) {
            int color = ContextCompat.getColor(getContext(), colorRes);
            mTextView.setHintTextColor(color);
        }
    }

    public void setTextColor(int colorRes) {
        if (colorRes != -1) {
            int color = ContextCompat.getColor(getContext(), colorRes);
            mTextView.setTextColor(color);
        }
    }


    public void setIcon(@DrawableRes int iconRes) {
        if (iconRes != -1) {
            mIcon.setVisibility(View.VISIBLE);
            mIcon.setImageResource(iconRes);
        } else {
            mIcon.setVisibility(View.GONE);
        }
    }


    public void setMaxLines(int maxLines) {
        if(maxLines != -1) {
            mTextView.setMaxLines(maxLines);
            if(maxLines == 1)
                mTextView.setSingleLine();
        }
    }

    public void setText(String text) {
        mTextView.setText(text);
    }
    public void setText(@StringRes int textId) {
        if (textId != -1)
            mTextView.setText(textId);
    }
    public String getText() {
        return mTextView.getText().toString();
    }


    public void setHint(String hint) {
        mTextView.setHint(hint);
    }
    public void setHint(@StringRes int hintId) {
        if (hintId != -1)
            mTextView.setHint(hintId);
    }

    public void setDim(boolean dim) {
        if(dim) {
            setBackgroundResource(R.drawable.round_edges_background_dim);
            mTextView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.custom_edit_text_dim_background));

            mIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.custom_edit_text_dim_foreground), android.graphics.PorterDuff.Mode.MULTIPLY);
            mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.custom_edit_text_dim_foreground));
            mTextView.setHintTextColor(ContextCompat.getColor(getContext(), R.color.custom_edit_text_dim_foreground));
        } else {
            setBackgroundResource(R.drawable.round_edges_background);
            mTextView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.custom_edit_text_background));

            mIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.custom_edit_text_icon), android.graphics.PorterDuff.Mode.MULTIPLY);
            mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.custom_edit_text_text));
            mTextView.setHintTextColor(ContextCompat.getColor(getContext(), R.color.custom_edit_text_hint));
        }
    }
}

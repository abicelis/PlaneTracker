package ve.com.abicelis.planetracker.ui.changeimage;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.ui.base.BaseDialogFragment;
import ve.com.abicelis.planetracker.util.ViewUtil;


/**
 * Created by abice on 16/3/2017.
 */

public class EditImageSearchQueryDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    //DATA
    private static final String ARGUMENT_QUERY= "ARGUMENT_QUERY";
    private SearchQuerySetListener mListener;

    //UI
    @BindView(R.id.dialog_edit_image_search_query_text)
    EditText mText;

    @BindView(R.id.dialog_edit_image_search_query_search)
    Button mSearch;

    @BindView(R.id.dialog_edit_image_search_query_cancel)
    Button mCancel;


    public EditImageSearchQueryDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditImageSearchQueryDialogFragment newInstance(String query) {
        EditImageSearchQueryDialogFragment frag = new EditImageSearchQueryDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_QUERY, query);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView =  inflater.inflate(R.layout.dialog_edit_image_search_query, container);
        ButterKnife.bind(this, dialogView);

        //Show keyboard
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mText.append(getArguments().getString(ARGUMENT_QUERY, ""));
        mSearch.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        return dialogView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.dialog_edit_image_search_query_search:
                if(mListener != null) {
                    if(mText.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), getString(R.string.dialog_edit_image_search_query_empty_query), Toast.LENGTH_SHORT).show();
                    } else {
                        mListener.onSearchQuerySet(mText.getText().toString());
                        dismiss();
                    }
                    ViewUtil.hideKeyboard(getActivity());
                }
                break;

            case R.id.dialog_edit_image_search_query_cancel:
                ViewUtil.hideKeyboard(getActivity());
                dismiss();
                break;

        }
    }


    public void setListener(SearchQuerySetListener listener) {
        mListener = listener;
    }


    public interface SearchQuerySetListener {
        void onSearchQuerySet(String query);
    }
}

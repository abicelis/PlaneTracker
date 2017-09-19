package ve.com.abicelis.planetracker.ui.tripdetail;

import android.os.Bundle;
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

public class EditTripNameDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    //DATA
    private static final String ARGUMENT_TRIP_NAME = "ARGUMENT_TRIP_NAME";
    private TripNameChangedListener mListener;

    //UI
    @BindView(R.id.dialog_edit_trip_name_text)
    EditText mText;

    @BindView(R.id.dialog_edit_trip_name_set)
    Button mSet;

    @BindView(R.id.dialog_edit_trip_name_cancel)
    Button mCancel;


    public EditTripNameDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditTripNameDialogFragment newInstance(String tripName) {
        EditTripNameDialogFragment frag = new EditTripNameDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_TRIP_NAME, tripName);
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
        View dialogView =  inflater.inflate(R.layout.dialog_edit_trip_name, container);
        ButterKnife.bind(this, dialogView);

        //Show keyboard
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mText.append(getArguments().getString(ARGUMENT_TRIP_NAME, ""));
        mSet.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        return dialogView;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.dialog_edit_trip_name_set:
                if(mListener != null) {
                    if(mText.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), getString(R.string.dialog_edit_trip_name_title_empty), Toast.LENGTH_SHORT).show();
                    } else {
                        mListener.onTripNameChanged(mText.getText().toString());
                        dismiss();
                    }
                    ViewUtil.hideKeyboard(getActivity());
                }
                break;

            case R.id.dialog_edit_trip_name_cancel:
                ViewUtil.hideKeyboard(getActivity());
                dismiss();
                break;

        }
    }


    public void setListener(TripNameChangedListener listener) {
        mListener = listener;
    }


    public interface TripNameChangedListener {
        void onTripNameChanged(String tripName);
    }
}

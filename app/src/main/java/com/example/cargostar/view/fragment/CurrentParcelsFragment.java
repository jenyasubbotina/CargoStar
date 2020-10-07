package com.example.cargostar.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cargostar.R;
import com.example.cargostar.model.TransportationStatus;
import com.example.cargostar.model.database.SharedPrefs;
import com.example.cargostar.model.location.City;
import com.example.cargostar.model.location.TransitPoint;
import com.example.cargostar.model.shipping.Parcel;
import com.example.cargostar.model.shipping.Receipt;
import com.example.cargostar.view.Constants;
import com.example.cargostar.view.activity.CalculatorActivity;
import com.example.cargostar.view.activity.CreateUserActivity;
import com.example.cargostar.view.activity.MainActivity;
import com.example.cargostar.view.activity.NotificationsActivity;
import com.example.cargostar.view.activity.ProfileActivity;
import com.example.cargostar.view.activity.ScanQrActivity;
import com.example.cargostar.view.adapter.ParcelAdapter;
import com.example.cargostar.view.adapter.SpinnerAdapter;
import com.example.cargostar.view.callback.ParcelCallback;
import com.example.cargostar.viewmodel.CurrentParcelsViewModel;
import com.example.cargostar.viewmodel.HeaderViewModel;
import com.example.cargostar.viewmodel.PopulateViewModel;

import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CurrentParcelsFragment extends Fragment implements ParcelCallback {
    private Context context;
    private FragmentActivity activity;
    //viewModel
    private HeaderViewModel headerViewModel;
    private CurrentParcelsViewModel currentParcelsViewModel;
    //transportation statuses -> all
    private static TransportationStatus[] statusArray;
    //header views
    private TextView fullNameTextView;
    private TextView branchTextView;
    private EditText parcelSearchEditText;
    private ImageView parcelSearchImageView;
    private ImageView profileImageView;
    private ImageView editImageView;
    private ImageView createUserImageView;
    private ImageView calculatorImageView;
    private ImageView notificationsImageView;
    private TextView badgeCounterTextView;
    //main content views
    private int statusFlag;

    private CheckBox inTransitCheckBox;
    private CheckBox onTheWayCheckBox;
    private CheckBox deliveredCheckBox;
    private CheckBox lostCheckBox;
    private CheckBox allCheckBox;

    private RelativeLayout cityField;
    private Spinner citySpinner;
    private List<TransitPoint> transitPointList;

    private EditText qrCodeEditText;
    private ImageView scanQrImageView;

    private RecyclerView currentParcelsRecyclerView;
    private ParcelAdapter parcelAdapter;

    public CurrentParcelsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();

        if (getArguments() != null) {
            statusFlag = getArguments().getInt(Constants.STATUS_FLAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_current_parcels, container, false);

        //header views
        fullNameTextView = activity.findViewById(R.id.full_name_text_view);
        branchTextView = activity.findViewById(R.id.branch_text_view);
        parcelSearchEditText = activity.findViewById(R.id.search_edit_text);
        parcelSearchImageView = activity.findViewById(R.id.search_btn);
        profileImageView = activity.findViewById(R.id.profile_image_view);
        editImageView = activity.findViewById(R.id.edit_image_view);
        createUserImageView = activity.findViewById(R.id.create_user_image_view);
        calculatorImageView = activity.findViewById(R.id.calculator_image_view);
        notificationsImageView = activity.findViewById(R.id.notifications_image_view);
        badgeCounterTextView = activity.findViewById(R.id.badge_counter_text_view);
        //main content views
        inTransitCheckBox = root.findViewById(R.id.in_transit_check_box);
        onTheWayCheckBox = root.findViewById(R.id.on_the_way_check_box);
        deliveredCheckBox = root.findViewById(R.id.delivered_check_box);
        lostCheckBox = root.findViewById(R.id.lost_check_box);
        allCheckBox = root.findViewById(R.id.all_check_box);

        qrCodeEditText = root.findViewById(R.id.qr_code_edit_text);
        scanQrImageView = root.findViewById(R.id.camera_image_view);
        cityField = root.findViewById(R.id.city_field);
        citySpinner = root.findViewById(R.id.city_spinner);
        currentParcelsRecyclerView = root.findViewById(R.id.current_parcels_recycler_view);

        parcelAdapter = new ParcelAdapter(context, this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        currentParcelsRecyclerView.setLayoutManager(layoutManager);
        currentParcelsRecyclerView.setAdapter(parcelAdapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //header views
        profileImageView.setOnClickListener(v -> {
            startActivity(new Intent(context, MainActivity.class));
        });
        editImageView.setOnClickListener(v -> {
            startActivity(new Intent(context, ProfileActivity.class));
        });
        createUserImageView.setOnClickListener(v -> {
            startActivity(new Intent(context, CreateUserActivity.class));
        });
        notificationsImageView.setOnClickListener(v -> {
            startActivity(new Intent(context, NotificationsActivity.class));
        });
        calculatorImageView.setOnClickListener(v -> {
            startActivity(new Intent(context, CalculatorActivity.class));
        });

        scanQrImageView.setOnClickListener(v -> {
            final Intent scanQrIntent = new Intent(context, ScanQrActivity.class);
            startActivityForResult(scanQrIntent, Constants.REQUEST_SCAN_QR_MENU);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        headerViewModel = new ViewModelProvider(this).get(HeaderViewModel.class);
        currentParcelsViewModel = new ViewModelProvider(this).get(CurrentParcelsViewModel.class);

        headerViewModel.selectCourierByLogin(SharedPrefs.getInstance(context).getString(SharedPrefs.LOGIN)).observe(getViewLifecycleOwner(), courier -> {
            if (courier != null) {
                fullNameTextView.setText(courier.getFirstName() + " " + courier.getLastName());
            }
        });
        headerViewModel.selectBranchByCourierId(SharedPrefs.getInstance(context).getLong(SharedPrefs.ID)).observe(getViewLifecycleOwner(), branch -> {
            if (branch != null) {
                branchTextView.setText(getString(R.string.branch) + " \"" + branch.getName() + "\"");
            }
        });
        headerViewModel.selectNewNotificationsCount().observe(getViewLifecycleOwner(), newNotificationsCount -> {
            if (newNotificationsCount != null) {
                badgeCounterTextView.setText(String.valueOf(newNotificationsCount));
            }
        });

        parcelSearchImageView.setOnClickListener(v -> {
            final String parcelIdStr = parcelSearchEditText.getText().toString();
            if (TextUtils.isEmpty(parcelIdStr)) {
                Toast.makeText(context, "Введите ID перевозки или номер накладной", Toast.LENGTH_SHORT).show();
                return;
            }

            final long parcelId = Long.parseLong(parcelIdStr);

            headerViewModel.selectRequest(parcelId).observe(getViewLifecycleOwner(), receiptWithCargoList -> {
                if (receiptWithCargoList == null) {
                    Toast.makeText(context, "Накладной не существует", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (receiptWithCargoList.getReceipt() == null) {
                    Toast.makeText(context, "Накладной не существует", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.putExtra(Constants.INTENT_REQUEST_KEY, Constants.REQUEST_FIND_PARCEL);
                mainIntent.putExtra(Constants.INTENT_REQUEST_VALUE, parcelId);
                startActivity(mainIntent);
            });
        });
        currentParcelsViewModel.selectAllTransitPoints().observe(getViewLifecycleOwner(), transitPointList -> {
            Log.i(TAG, "onActivityCreated(): " + transitPointList);
            if (transitPointList != null) {
                this.transitPointList = transitPointList;
                initCitySpinner(transitPointList);
            }
        });

        inTransitCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            initParcels(b, onTheWayCheckBox.isChecked(), deliveredCheckBox.isChecked(), lostCheckBox.isChecked(), allCheckBox.isChecked());
        });

        onTheWayCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            initParcels(inTransitCheckBox.isChecked(), b, deliveredCheckBox.isChecked(), lostCheckBox.isChecked(), allCheckBox.isChecked());
        });

        deliveredCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            initParcels(inTransitCheckBox.isChecked(), onTheWayCheckBox.isChecked(), b, lostCheckBox.isChecked(), allCheckBox.isChecked());
        });

        lostCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            initParcels(inTransitCheckBox.isChecked(), onTheWayCheckBox.isChecked(), deliveredCheckBox.isChecked(), b, allCheckBox.isChecked());
        });

        allCheckBox.setOnCheckedChangeListener((compoundButton, b) -> {
            initParcels(inTransitCheckBox.isChecked(), onTheWayCheckBox.isChecked(), deliveredCheckBox.isChecked(), lostCheckBox.isChecked(), b);
        });

        //default checkBox values
        inTransitCheckBox.setChecked(true);
        onTheWayCheckBox.setChecked(true);

        //main content views
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final TextView itemTextView = (TextView) view;
                final TransitPoint currentItem = (TransitPoint) adapterView.getItemAtPosition(i);

                if (itemTextView != null) {
                    if (i < adapterView.getCount()) {
                        itemTextView.setTextColor(context.getColor(R.color.colorBlack));
                        cityField.setBackgroundResource(R.drawable.edit_text_active);
                    }
                }
                if (inTransitCheckBox.isChecked()) {
                    currentParcelsViewModel.selectParcelsByLocationAndStatus(SharedPrefs.getInstance(context).getLong(SharedPrefs.ID), statusArray, currentItem.getTransitPointId()).observe(getViewLifecycleOwner(), parcelList -> {
                        Log.i(TAG, "parcelList: " + parcelList);
                        if (parcelList != null) {
                            parcelAdapter.setParcelList(parcelList);
                            parcelAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initParcels(final boolean inTransit, final boolean onTheWay, final boolean delivered, final boolean lost, final boolean all) {
        if (all || (inTransit && onTheWay && delivered && lost)) {
            //in transit + on the way + delivered + lost
            Log.i(TAG, "statusList: in transit + on the way + delivered + lost");
            statusArray = new TransportationStatus[] {TransportationStatus.IN_TRANSIT, TransportationStatus.ON_THE_WAY, TransportationStatus.DELIVERED, TransportationStatus.LOST};
            initCitySpinner(transitPointList);
        }
        else if (inTransit && !onTheWay && !delivered && !lost) {
            //in transit
            Log.i(TAG, "statusList: in transit");
            statusArray = new TransportationStatus[] {TransportationStatus.IN_TRANSIT};
            initCitySpinner(transitPointList);
        }
        else if (inTransit && onTheWay && !delivered && !lost) {
            //in transit + on the way
            statusArray = new TransportationStatus[] {TransportationStatus.IN_TRANSIT, TransportationStatus.ON_THE_WAY};
            Log.i(TAG, "statusList: in transit + on the way, transitPointList=" + transitPointList);
            initCitySpinner(transitPointList);
        }
        else if (inTransit && onTheWay && delivered) {
            //in transit + on the way + delivered
            Log.i(TAG, "statusList: in transit + on the way + delivered");
            statusArray = new TransportationStatus[] {TransportationStatus.IN_TRANSIT, TransportationStatus.ON_THE_WAY, TransportationStatus.DELIVERED};
            initCitySpinner(transitPointList);
        }
        else if (inTransit && onTheWay) {
            //in transit + on the way + lost
            Log.i(TAG, "statusList: in transit + on the way + lost");
            statusArray = new TransportationStatus[] {TransportationStatus.IN_TRANSIT, TransportationStatus.ON_THE_WAY, TransportationStatus.LOST};
            initCitySpinner(transitPointList);
        }
        else if (inTransit && delivered && !lost) {
            //in transit + delivered
            Log.i(TAG, "statusList: in transit + delivered");
            statusArray = new TransportationStatus[] {TransportationStatus.IN_TRANSIT, TransportationStatus.DELIVERED};
            initCitySpinner(transitPointList);
        }
        else if (inTransit && !delivered) {
            //in transit + lost
            Log.i(TAG, "statusList: in transit + lost");
            statusArray = new TransportationStatus[] {TransportationStatus.IN_TRANSIT, TransportationStatus.LOST};
            initCitySpinner(transitPointList);
        }
        else if (inTransit) {
            //in transit + delivered + lost
            Log.i(TAG, "statusList: in transit + delivered + lost");
            statusArray = new TransportationStatus[] {TransportationStatus.IN_TRANSIT, TransportationStatus.DELIVERED, TransportationStatus.LOST};
            initCitySpinner(transitPointList);
        }
        else if (onTheWay && !delivered && !lost) {
            //on the way
            Log.i(TAG, "statusList: on the way");
            statusArray = new TransportationStatus[] {TransportationStatus.ON_THE_WAY};
            initCitySpinner(null);
        }
        else if (onTheWay && delivered && !lost) {
            //on the way + delivered
            Log.i(TAG, "statusList: on the way + delivered");
            statusArray = new TransportationStatus[] {TransportationStatus.ON_THE_WAY, TransportationStatus.DELIVERED};
            initCitySpinner(null);
        }
        else if (onTheWay && delivered) {
            //on the way + delivered + lost
            Log.i(TAG, "statusList: on the way + delivered + lost");
            statusArray = new TransportationStatus[] {TransportationStatus.ON_THE_WAY, TransportationStatus.DELIVERED, TransportationStatus.LOST};
            initCitySpinner(null);
        }
        else if (onTheWay) {
            //on the way + lost
            Log.i(TAG, "statusList: on the way + lost");
            statusArray = new TransportationStatus[] {TransportationStatus.ON_THE_WAY, TransportationStatus.LOST};
            initCitySpinner(null);
        }
        else if (delivered && !lost) {
            //delivered
            Log.i(TAG, "statusList: delivered");
            statusArray = new TransportationStatus[] {TransportationStatus.DELIVERED};
            initCitySpinner(null);
        }
        else if (delivered) {
            //delivered + lost
            Log.i(TAG, "statusList: delivered + lost");
            statusArray = new TransportationStatus[] {TransportationStatus.DELIVERED, TransportationStatus.LOST};
            initCitySpinner(null);
        }
        else if (lost) {
            //lost
            Log.i(TAG, "statusList: lost");
            statusArray = new TransportationStatus[] {TransportationStatus.LOST};
            initCitySpinner(null);
        }
        else {
            //nothing
            Log.i(TAG, "statusList: nothing");
            statusArray = new TransportationStatus[] {};
            initCitySpinner(null);
        }
        currentParcelsViewModel.selectParcelsByStatus(SharedPrefs.getInstance(context).getLong(SharedPrefs.ID), statusArray).observe(getViewLifecycleOwner(), parcelList -> {
            parcelAdapter.setParcelList(parcelList);
            parcelAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onParcelSelected(Parcel currentItem) {
        final CurrentParcelsFragmentDirections.ActionCurrentParcelsFragmentToParcelStatusFragment action =
                CurrentParcelsFragmentDirections.actionCurrentParcelsFragmentToParcelStatusFragment();
        action.setParcelId(currentItem.getReceipt().getId());
        NavHostFragment.findNavController(this).navigate(action);
    }

    private void initCitySpinner(final List<TransitPoint> transitPointList) {
        final SpinnerAdapter<TransitPoint> cityAdapter = new SpinnerAdapter<>(context, R.layout.spinner_item, transitPointList);
        cityAdapter.setDropDownViewResource(R.layout.spinner_item);
        citySpinner.setAdapter(cityAdapter);
        cityAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == Constants.REQUEST_SCAN_QR_MENU) {
                final CurrentParcelsFragmentDirections.ActionCurrentParcelsFragmentToParcelDataFragment action =
                        CurrentParcelsFragmentDirections.actionCurrentParcelsFragmentToParcelDataFragment();
                action.setParcelId(1);
                action.setParcelId(Constants.INTENT_PARCEL);
                NavHostFragment.findNavController(this).navigate(action);
            }
        }
    }

    private static final String TAG = CurrentParcelsFragment.class.toString();
}
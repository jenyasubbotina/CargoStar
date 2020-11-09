package uz.alexits.cargostar.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import uz.alexits.cargostar.R;
import uz.alexits.cargostar.utils.Constants;
import uz.alexits.cargostar.utils.IntentConstants;
import uz.alexits.cargostar.view.fragment.MainFragmentDirections;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_EXIT = 0x00;
    public static final int PICK_IMAGE = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null) {
            if (getIntent().getIntExtra(IntentConstants.INTENT_REQUEST_KEY, -1) == IntentConstants.REQUEST_PUBLIC_REQUESTS) {
                Navigation.findNavController(this, R.id.main_fragment_container).navigate(R.id.action_mainFragment_to_publicBidsFragment);
                return;
            }
            if (getIntent().getIntExtra(IntentConstants.INTENT_REQUEST_KEY, -1) == IntentConstants.REQUEST_MY_REQUESTS) {
                Navigation.findNavController(this, R.id.main_fragment_container).navigate(R.id.action_mainFragment_to_myBidsFragment);
                return;
            }
            if (getIntent().getIntExtra(IntentConstants.INTENT_REQUEST_KEY, -1) == IntentConstants.REQUEST_FIND_PARCEL) {
                final MainFragmentDirections.ActionMainFragmentToParcelDataFragment action =
                        MainFragmentDirections.actionMainFragmentToParcelDataFragment();
                action.setParcelId(getIntent().getLongExtra(IntentConstants.INTENT_REQUEST_VALUE, -1));
                action.setInvoiceId(getIntent().getLongExtra(Constants.KEY_INVOICE_ID, -1L));
                action.setClientId(getIntent().getLongExtra(Constants.KEY_CLIENT_ID, -1L));
                action.setRequestId(getIntent().getLongExtra(Constants.KEY_REQUEST_ID, -1L));
                action.setProviderId(getIntent().getLongExtra(Constants.KEY_PROVIDER_ID, -1L));
//                mainIntent.putExtra(Constants.KEY_NUMBER, number);
//                mainIntent.putExtra(Constants.KEY_SENDER_ID, senderId);
//                mainIntent.putExtra(Constants.KEY_RECIPIENT_ID, recipientId);
//                mainIntent.putExtra(Constants.KEY_PAYER_ID, payerId);
//                mainIntent.putExtra(Constants.KEY_PRICE, price);
//                mainIntent.putExtra(Constants.KEY_TARIFF_ID, tariffId);
//                mainIntent.putExtra(Constants.KEY_STATUS, status);
//                mainIntent.putExtra(Constants.KEY_CREATED_AT, createdAt);
//                mainIntent.putExtra(Constants.KEY_UPDATED_AT, updatedAt);
                Navigation.findNavController(this, R.id.main_fragment_container).navigate(action);
                return;
            }

            final String pushIntentKey = getIntent().getStringExtra(Constants.MAIN_ACTIVITY_FRAGMENT);

            if (pushIntentKey != null) {
                if (pushIntentKey.equalsIgnoreCase(Constants.KEY_PUBLIC_REQUESTS)) {
                    Navigation.findNavController(this, R.id.main_fragment_container).navigate(R.id.action_mainFragment_to_publicBidsFragment);
                    return;
                }
                if (pushIntentKey.equalsIgnoreCase(Constants.KEY_MY_REQUESTS)) {
                    Navigation.findNavController(this, R.id.main_fragment_container).navigate(R.id.action_mainFragment_to_myBidsFragment);
                    return;
                }
                if (pushIntentKey.equalsIgnoreCase(Constants.KEY_CURRENT_TRANSPORTATION)) {
                    Navigation.findNavController(this, R.id.main_fragment_container).navigate(R.id.action_mainFragment_to_currentParcelsFragment);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(MainActivity.class.toString(), "onActivityResult: " + requestCode + " " + resultCode + " " + data);
        if (requestCode == PICK_IMAGE) {

        }
        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }
}
package uz.alexits.cargostar.workers.invoice;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import uz.alexits.cargostar.database.cache.LocalCache;
import uz.alexits.cargostar.model.shipping.Invoice;
import uz.alexits.cargostar.model.shipping.Request;
import uz.alexits.cargostar.utils.Constants;

public class GetInvoiceHeaderWorker extends Worker {
    private final Long invoiceId;

    public GetInvoiceHeaderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.invoiceId = getInputData().getLong(Constants.KEY_INVOICE_ID, -1L);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (invoiceId == -1L) {
            Log.e(TAG, "selectInvoiceById(): invoiceId is empty");
            return Result.failure();
        }
        final Request request = LocalCache.getInstance(getApplicationContext()).invoiceDao().selectRequestByInvoiceId(invoiceId);

        if (request == null) {
            Log.e(TAG, "selectInvoiceById(): invoice is NULL " + invoiceId);
            return Result.failure();
        }

        final Data.Builder outputData = new Data.Builder();
        outputData.putLong(Constants.KEY_REQUEST_ID, request.getId());
        outputData.putLong(Constants.KEY_INVOICE_ID, request.getInvoiceId());
        outputData.putLong(Constants.KEY_COURIER_ID, request.getCourierId() != null ? request.getCourierId() : 0);
        outputData.putLong(Constants.KEY_CLIENT_ID, request.getClientId() != null ? request.getClientId() : 0);
        outputData.putLong(Constants.KEY_SENDER_COUNTRY_ID, request.getSenderCountryId() != null ? request.getSenderCountryId() : 0);
        outputData.putLong(Constants.KEY_SENDER_REGION_ID, request.getSenderRegionId() != null ? request.getSenderRegionId() : 0);
        outputData.putLong(Constants.KEY_SENDER_CITY_ID, request.getSenderCityId() != null ? request.getSenderCityId() : 0);
        outputData.putLong(Constants.KEY_RECIPIENT_COUNTRY_ID, request.getRecipientCountryId() != null ? request.getRecipientCountryId() : 0);
        outputData.putLong(Constants.KEY_RECIPIENT_CITY_ID, request.getRecipientCityId() != null ? request.getRecipientCityId() : 0);
        outputData.putLong(Constants.KEY_PROVIDER_ID, request.getProviderId() != null ? request.getProviderId() : 0);
        outputData.putInt(Constants.KEY_DELIVERY_TYPE, request.getDeliveryType());
        outputData.putString(Constants.KEY_COMMENT, request.getComment());
        outputData.putInt(Constants.KEY_CONSIGNMENT_QUANTITY, request.getConsignmentQuantity());

        return Result.success(outputData.build());
    }

    private static final String TAG = GetInvoiceHeaderWorker.class.toString();
}

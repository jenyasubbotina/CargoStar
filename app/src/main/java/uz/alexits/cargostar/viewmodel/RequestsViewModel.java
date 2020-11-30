package uz.alexits.cargostar.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import uz.alexits.cargostar.model.actor.AddressBook;
import uz.alexits.cargostar.model.actor.Customer;
import uz.alexits.cargostar.model.calculation.Packaging;
import uz.alexits.cargostar.model.calculation.Provider;
import uz.alexits.cargostar.model.location.City;
import uz.alexits.cargostar.model.location.Country;
import uz.alexits.cargostar.model.location.Region;
import uz.alexits.cargostar.model.transportation.Consignment;
import uz.alexits.cargostar.model.transportation.Invoice;
import uz.alexits.cargostar.model.transportation.Request;
import uz.alexits.cargostar.database.cache.Repository;
import uz.alexits.cargostar.model.transportation.Transportation;

import java.util.List;

public class RequestsViewModel extends AndroidViewModel {
    private final Repository repository;

    private final MutableLiveData<Long> courierId;
    private final MutableLiveData<Long> requestId;
    private final MutableLiveData<Long> invoiceId;

    /* sender data */
    private final MutableLiveData<Long> senderId;
    private final MutableLiveData<Long> senderCountryId;
    private final MutableLiveData<Long> senderRegionId;
    private final MutableLiveData<Long> senderCityId;

    /* recipient data */
    private final MutableLiveData<Long> recipientId;
    private final MutableLiveData<Long> recipientCountryId;
    private final MutableLiveData<Long> recipientRegionId;
    private final MutableLiveData<Long> recipientCityId;

    /* payer data */
    private final MutableLiveData<Long> payerId;
    private final MutableLiveData<Long> payerCountryId;
    private final MutableLiveData<Long> payerRegionId;
    private final MutableLiveData<Long> payerCityId;

    /* invoice data */
    private final MutableLiveData<Long> providerId;
    private final MutableLiveData<Long> tariffId;

    private final LiveData<List<Request>> requestList;

    public RequestsViewModel(@NonNull Application application) {
        super(application);
        this.repository = Repository.getInstance(application);

        this.requestList = repository.selectAllRequests();

        this.courierId = new MutableLiveData<>();
        this.requestId = new MutableLiveData<>();
        this.invoiceId = new MutableLiveData<>();

        this.senderId = new MutableLiveData<>();
        this.senderCountryId = new MutableLiveData<>();
        this.senderCityId = new MutableLiveData<>();
        this.senderRegionId = new MutableLiveData<>();

        this.recipientId = new MutableLiveData<>();
        this.recipientCountryId = new MutableLiveData<>();
        this.recipientRegionId = new MutableLiveData<>();
        this.recipientCityId = new MutableLiveData<>();

        this.payerId = new MutableLiveData<>();
        this.payerCountryId = new MutableLiveData<>();
        this.payerRegionId = new MutableLiveData<>();
        this.payerCityId = new MutableLiveData<>();

        this.providerId = new MutableLiveData<>();
        this.tariffId =  new MutableLiveData<>();

    }

    public LiveData<List<Request>> getPublicRequests() {
        return requestList;
    }

    public LiveData<List<Request>> getMyRequests(final long courierId) {
        return repository.selectRequestsByCourierId(courierId);
    }

    public void readReceipt(final long requestId) {
        repository.readRequest(requestId);
    }

    /* invoice data */
    public void setRequestId(final Long requestId) {
        this.requestId.setValue(requestId);
    }

    public void setInvoiceId(final Long invoiceId) {
        if (invoiceId == null) {
            return;
        }
        this.invoiceId.setValue(invoiceId);
    }

    public void setProviderId(final Long providerId) {
        if (providerId == null) {
            return;
        }
        this.providerId.setValue(providerId);
    }

    public void setTariffId(final Long tariffId) {
        if (tariffId == null) {
            return;
        }
        this.tariffId .setValue(tariffId);
    }

    public void setCourierId(final Long courierId) {
        if (courierId == null) {
            return;
        }
        this.courierId.setValue(courierId);
    }

    public void setSenderId(final Long senderId) {
        if (senderId == null) {
            return;
        }
        this.senderId.setValue(senderId);
    }

    public void setRecipientId(final Long recipientId) {
        if (recipientId == null) {
            return;
        }
        this.recipientId.setValue(recipientId);
    }

    public void setPayerId(final Long payerId) {
        if (payerId == null) {
            return;
        }
        this.payerId.setValue(payerId);
    }

    public void setSenderCountryId(final Long senderCountryId) {
        if (senderCountryId == null) {
            return;
        }
        this.senderCountryId.setValue(senderCountryId);
    }

    public void setSenderRegionId(final Long senderRegionId) {
        if (senderRegionId == null) {
            return;
        }
        this.senderRegionId.setValue(senderRegionId);
    }

    public void setSenderCityId(final Long senderCityId) {
        if (senderCityId == null) {
            return;
        }
        this.senderCityId.setValue(senderCityId);
    }

    public void setRecipientCountryId(final Long recipientCountryId) {
        if (recipientCountryId == null) {
            return;
        }
        this.recipientCountryId.setValue(recipientCountryId);
    }

    public void setRecipientRegionId(final Long recipientRegionId) {
        if (recipientRegionId == null) {
            return;
        }
        this.recipientRegionId.setValue(recipientRegionId);
    }

    public void setRecipientCityId(final Long recipientCityId) {
        if (recipientCityId == null) {
            return;
        }
        this.recipientCityId.setValue(recipientCityId);
    }

    public void setPayerCountryId(final Long payerCountryId) {
        if (payerCountryId == null) {
            return;
        }
        this.payerCountryId.setValue(payerCountryId);
    }

    public void setPayerRegionId(final Long payerRegionId) {
        if (payerRegionId == null) {
            return;
        }
        this.payerRegionId.setValue(payerRegionId);
    }

    public void setPayerCityId(final Long payerCityId) {
        if (payerCityId == null) {
            return;
        }
        this.payerCityId.setValue(payerCityId);
    }

//    /* getters */
    public LiveData<Invoice> getInvoice() {
        return Transformations.switchMap(invoiceId, repository::selectInvoiceById);
    }

    public LiveData<Provider> getProvider() {
        return Transformations.switchMap(providerId, repository::selectProviderById);
    }

    public LiveData<Customer> getSender() {
        return Transformations.switchMap(senderId, repository::selectCustomerById);
    }

    public LiveData<AddressBook> getRecipient() {
        return Transformations.switchMap(recipientId, repository::selectAddressBookEntryById);
    }

    public LiveData<AddressBook> getPayer() {
        return Transformations.switchMap(payerId, repository::selectAddressBookEntryById);
    }

    public LiveData<Country> getSenderCountry() {
        return Transformations.switchMap(senderCountryId, repository::selectCountryById);
    }

    public LiveData<Region> getSenderRegion() {
        return Transformations.switchMap(senderRegionId, repository::selectRegionById);
    }
    public LiveData<City> getSenderCity() {
        return Transformations.switchMap(senderCityId, repository::selectCityById);
    }

    public LiveData<Country> getRecipientCountry() {
        return Transformations.switchMap(recipientCountryId, repository::selectCountryById);
    }

    public LiveData<Region> getRecipientRegion() {
        return Transformations.switchMap(recipientRegionId, repository::selectRegionById);
    }

    public LiveData<City> getRecipientCity() {
        return Transformations.switchMap(recipientCityId, repository::selectCityById);
    }

    public LiveData<Country> getPayerCountry() {
        return Transformations.switchMap(payerCountryId, repository::selectCountryById);
    }

    public LiveData<Region> getPayerRegion() {
        return Transformations.switchMap(payerRegionId, repository::selectRegionById);
    }

    public LiveData<City> getPayerCity() {
        return Transformations.switchMap(payerCityId, repository::selectCityById);
    }

    public LiveData<Packaging> getTariff() {
        return Transformations.switchMap(tariffId, repository::selectPackagingById);
    }

    public LiveData<Transportation> getTransportation() {
        return Transformations.switchMap(invoiceId, repository::selectTransportationByInvoiceId);
    }

    public LiveData<List<Consignment>> getConsignmentList() {
        return Transformations.switchMap(requestId, repository::selectConsignmentListByRequestId);
    }
}

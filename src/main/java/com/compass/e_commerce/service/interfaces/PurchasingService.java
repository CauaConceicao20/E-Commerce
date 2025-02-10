package com.compass.e_commerce.service.interfaces;

public interface PurchasingService<D> {

    void buy(D dto);

    void purchaseConfirmation(String message);
}

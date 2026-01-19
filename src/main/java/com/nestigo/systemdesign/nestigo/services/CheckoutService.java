package com.nestigo.systemdesign.nestigo.services;

public interface CheckoutService {

    String getCheckoutSession(Long bookingId, String successUrl, String failureUrl);
}

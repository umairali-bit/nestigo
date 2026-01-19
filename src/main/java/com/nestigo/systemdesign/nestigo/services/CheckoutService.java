package com.nestigo.systemdesign.nestigo.services;

import com.nestigo.systemdesign.nestigo.entities.BookingEntity;

public interface CheckoutService {

    String getCheckoutSession(BookingEntity booking, String successUrl, String failureUrl);
}

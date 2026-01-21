package com.nestigo.systemdesign.nestigo.services;


import com.nestigo.systemdesign.nestigo.entities.BookingEntity;
import com.nestigo.systemdesign.nestigo.repositories.BookingRepository;
import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImp implements CheckoutService {

    private final BookingRepository bookingRepository;


    @Override
    public String getCheckoutSession(BookingEntity booking, String successUrl, String failureUrl) {


        log.info("Creating session for booking with ID:{}", booking.getId());
//         getting user
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//        creating a customer
        try {

//       Customer customer = Customer.create(
//                    CustomerCreateParams.builder()
//                            .setName(user.getName())
//                            .setEmail(user.getEmail())
//                            .build()
//            );

//            better way to do it

            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();

            Customer customer = Customer.create(customerCreateParams);


            SessionCreateParams sessionParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("usd")
                                                    .setUnitAmount(booking.getPrice()
                                                            .multiply(BigDecimal.valueOf(100))
                                                            .longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(booking.getHotel().getName() + " " + booking.getRoom().getType())
                                                                    .setDescription("BookingEntity ID: " + booking.getId() )
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();
            log.info("bookingId={}", booking.getId());
            log.info("booking.price(dollars)={}", booking.getPrice());
            log.info("unitAmount(cents)={}", booking.getPrice().multiply(BigDecimal.valueOf(100)));

            Session session = Session.create(sessionParams);

            booking.setPaymentSessionId(session.getId());
            log.info("Session created successfully for booking with ID:{}", booking.getId());
            bookingRepository.save(booking);

            return session.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }
}

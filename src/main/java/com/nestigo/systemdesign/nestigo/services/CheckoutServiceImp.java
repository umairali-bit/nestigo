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

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImp implements CheckoutService {

    private final BookingRepository bookingRepository;


    @Override
    public String getCheckoutSession(BookingEntity booking, String successUrl, String failureUrl) {

//        getting user
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
                    .build();

            Session session = Session.create(sessionParams);

            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);

            return session.getUrl();

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }
}

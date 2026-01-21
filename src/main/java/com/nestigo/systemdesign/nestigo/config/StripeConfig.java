package com.nestigo.systemdesign.nestigo.config;


import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeConfig {

    public  StripeConfig(@Value("${stripe.secret.key}") String stripeSecretKey) {

        Stripe.apiKey = stripeSecretKey;
    }


//  to check if the key is working

        @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void checkStripeKey() {
        System.out.println("Stripe key loaded: " +
                (stripeSecretKey != null && stripeSecretKey.startsWith("sk_")));
    }


}

package com.nestigo.systemdesign.nestigo.controllers;


import com.nestigo.systemdesign.nestigo.services.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            System.out.println("Webhook HIT ✅");
            System.out.println("Event type=" + event.getType());
            System.out.println("Event id=" + event.getId());

            bookingService.capturePayments(event);
            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            System.out.println("Invalid signature ❌ " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}

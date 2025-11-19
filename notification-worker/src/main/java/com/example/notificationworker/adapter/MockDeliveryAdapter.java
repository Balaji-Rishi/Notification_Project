package com.example.notificationworker.adapter;

import com.example.notificationworker.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class MockDeliveryAdapter implements DeliveryAdapter {

    @Override
    public void send(Notification notification) throws Exception {
        String rcpt = notification.getRecipient();
        if (rcpt == null) throw new Exception("recipient missing");

        // Always fail for recipients containing 'always_fail'
        if (rcpt.contains("always_fail")) {
            throw new Exception("Simulated permanent failure");
        }

        // Flaky recipients fail half the time
        if (rcpt.contains("flaky")) {
            if (Math.random() < 0.5) {
                throw new Exception("Simulated transient failure");
            }
        }

        // Otherwise, success
        System.out.println("MockDeliveryAdapter: delivered to " + rcpt + " channel=" + notification.getChannel());
    }
}

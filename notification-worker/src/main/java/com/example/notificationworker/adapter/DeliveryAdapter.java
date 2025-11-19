package com.example.notificationworker.adapter;

import com.example.notificationworker.entity.Notification;

public interface DeliveryAdapter {
    void send(Notification notification) throws Exception;
}

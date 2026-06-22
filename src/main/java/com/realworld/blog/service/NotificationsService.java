package com.realworld.blog.service;

import org.springframework.stereotype.Component;

public interface NotificationsService {
    Boolean saveNotification(String username,String message);
}

package com.realworld.blog.service.impl;

import com.realworld.blog.service.NotificationsService;
import org.springframework.stereotype.Service;

/**
 * @author jiaolei
 * @date 2026-06-20 15:06
 * @description TODO
 */
@Service
public class NotificationServiceImpl implements NotificationsService {
    @Override
    public Boolean saveNotification(String username, String message) {
        //把username和message存到notification表
        return null;
    }
}
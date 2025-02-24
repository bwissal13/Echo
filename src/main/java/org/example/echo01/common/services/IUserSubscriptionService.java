package org.example.echo01.common.services;

import org.example.echo01.common.dtos.UserSubscriptionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserSubscriptionService {
    UserSubscriptionDto subscribeToUser(Long targetUserId);
    void unsubscribeFromUser(Long targetUserId);
    Page<UserSubscriptionDto> getMySubscriptions(Pageable pageable);
    Page<UserSubscriptionDto> getMySubscribers(Pageable pageable);
    boolean isSubscribedToUser(Long targetUserId);
} 
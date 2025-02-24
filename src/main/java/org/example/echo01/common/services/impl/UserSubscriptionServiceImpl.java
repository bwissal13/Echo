package org.example.echo01.common.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.auth.repositories.UserRepository;
import org.example.echo01.auth.services.IAuthenticationService;
import org.example.echo01.common.dtos.UserSubscriptionDto;
import org.example.echo01.common.entities.UserSubscription;
import org.example.echo01.common.mappers.UserSubscriptionMapper;
import org.example.echo01.common.repositories.UserSubscriptionRepository;
import org.example.echo01.common.services.IUserSubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements IUserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRepository userRepository;
    private final IAuthenticationService authenticationService;
    private final UserSubscriptionMapper userSubscriptionMapper;

    @Override
    @Transactional
    public UserSubscriptionDto subscribeToUser(Long targetUserId) {
        User currentUser = authenticationService.getCurrentUser();
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (currentUser.getId().equals(targetUserId)) {
            throw new IllegalArgumentException("Cannot subscribe to yourself");
        }

        if (userSubscriptionRepository.existsByTargetUserAndSubscriber(targetUser, currentUser)) {
            throw new IllegalArgumentException("Already subscribed to this user");
        }

        UserSubscription subscription = UserSubscription.builder()
                .targetUser(targetUser)
                .subscriber(currentUser)
                .notifyOnNewBook(true)
                .build();

        return userSubscriptionMapper.toDto(userSubscriptionRepository.save(subscription));
    }

    @Override
    @Transactional
    public void unsubscribeFromUser(Long targetUserId) {
        User currentUser = authenticationService.getCurrentUser();
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserSubscription subscription = userSubscriptionRepository
                .findByTargetUserAndSubscriber(targetUser, currentUser)
                .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));

        userSubscriptionRepository.delete(subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSubscriptionDto> getMySubscriptions(Pageable pageable) {
        User currentUser = authenticationService.getCurrentUser();
        return userSubscriptionRepository.findBySubscriber(currentUser, pageable)
                .map(userSubscriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSubscriptionDto> getMySubscribers(Pageable pageable) {
        User currentUser = authenticationService.getCurrentUser();
        return userSubscriptionRepository.findByTargetUser(currentUser, pageable)
                .map(userSubscriptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSubscribedToUser(Long targetUserId) {
        User currentUser = authenticationService.getCurrentUser();
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userSubscriptionRepository.existsByTargetUserAndSubscriber(targetUser, currentUser);
    }
} 
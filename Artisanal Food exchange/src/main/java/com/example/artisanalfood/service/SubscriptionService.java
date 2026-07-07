package com.example.artisanalfood.service;

import com.example.artisanalfood.model.Subscription;
import com.example.artisanalfood.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription createSubscription(Subscription subscription) {
        subscription.setStatus(Subscription.Status.ACTIVE);
        subscription.setNextDeliveryDate(calculateNextDelivery(LocalDate.now(), subscription.getFrequency()));
        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> getSubscriptionsByUser(String userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    public Optional<Subscription> toggleSubscriptionStatus(String id, Subscription.Status status) {
        return subscriptionRepository.findById(id).map(sub -> {
            sub.setStatus(status);
            if (status == Subscription.Status.ACTIVE) {
                sub.setNextDeliveryDate(calculateNextDelivery(LocalDate.now(), sub.getFrequency()));
            }
            return subscriptionRepository.save(sub);
        });
    }

    public void cancelSubscription(String id) {
        subscriptionRepository.deleteById(id);
    }

    private LocalDate calculateNextDelivery(LocalDate fromDate, Subscription.Frequency frequency) {
        switch (frequency) {
            case WEEKLY:
                return fromDate.plusWeeks(1);
            case BIWEEKLY:
                return fromDate.plusWeeks(2);
            case MONTHLY:
                return fromDate.plusMonths(1);
            default:
                return fromDate.plusWeeks(1);
        }
    }
}

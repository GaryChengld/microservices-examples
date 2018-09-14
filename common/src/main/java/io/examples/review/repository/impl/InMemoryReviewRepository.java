package io.examples.review.repository.impl;

import io.examples.review.domain.Review;
import io.examples.review.repository.ReviewRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implement review repository in memory
 *
 * @author Gary Cheng
 */
public class InMemoryReviewRepository implements ReviewRepository {

    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private final Map<Integer, Review> reviewMap = new ConcurrentHashMap<>();
    private static ReviewRepository instance = new InMemoryReviewRepository();

    public static ReviewRepository getInstance() {
        return instance;
    }

    @Override
    public List<Review> getReviewByProductId(Integer productId) {
        return reviewMap.values().stream()
                .filter(review -> productId.equals(review.getProductId()))
                .sorted(Comparator.comparing(Review::getReviewDateTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Review> getReviewById(Integer id) {
        return reviewMap.values().stream().filter(review -> id.equals(review.getId())).findFirst();
    }

    @Override
    public Review addReview(Review review) {
        review.setId(idGenerator.incrementAndGet());
        reviewMap.put(review.getId(), review);
        return review;
    }

    @Override
    public void updateReview(Review review) {
        if (review.getId() == null) {
            review.setId(idGenerator.incrementAndGet());
        }
        reviewMap.put(review.getId(), review);
    }

    @Override
    public void deleteReview(Integer id) {
        reviewMap.remove(id);
    }
}

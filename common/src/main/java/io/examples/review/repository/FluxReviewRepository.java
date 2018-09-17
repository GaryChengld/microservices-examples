package io.examples.review.repository;

import io.examples.review.domain.Review;
import java.util.Optional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactor style review repository
 *
 * @author Gary Cheng
 */
public class FluxReviewRepository {
    private static FluxReviewRepository instance = new FluxReviewRepository();
    private ReviewRepository repository = ReviewRepository.instance();

    public static FluxReviewRepository getInstance() {
        return instance;
    }

    /**
     * Get reviews by product Id
     *
     * @param productId the product ID
     * @return
     */
    public Flux<Review> getReviewByProductId(Integer productId) {
        return Flux.create(emitter -> {
            repository.getReviewByProductId(productId).forEach(emitter::next);
            emitter.complete();
        });
    }

    /**
     * Return review by review Id
     *
     * @param id
     * @return
     */
    public Mono<Review> getReviewById(Integer id) {
        return Mono.create(emitter -> {
            Optional<Review> review = repository.getReviewById(id);
            if (review.isPresent()) {
                emitter.success(review.get());
            } else {
                emitter.success();
            }
        });
    }

    /**
     * Add a new Review
     *
     * @param review
     * @return
     */
    public Mono<Review> addReview(Review review) {
        return Mono.create(emitter -> emitter.success(repository.addReview(review)));
    }

    /**
     * Update a review
     *
     * @param review
     * @return
     */
    public Mono<Boolean> updateReview(Review review) {
        return Mono.create(emitter -> {
            repository.updateReview(review);
            emitter.success(Boolean.TRUE);
        });
    }

    /**
     * Delete a review
     *
     * @param id
     * @return
     */
    public Mono<Boolean> deleteReview(Integer id) {
        return Mono.create(emitter -> {
            repository.deleteReview(id);
            emitter.success(Boolean.TRUE);
        });
    }
}

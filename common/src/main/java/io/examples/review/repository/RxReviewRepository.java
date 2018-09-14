package io.examples.review.repository;

import io.examples.review.domain.Review;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.util.Optional;


import static io.reactivex.BackpressureStrategy.DROP;

/**
 * ReactiveX style review repository
 *
 * @author Gary Cheng
 */
public class RxReviewRepository {
    private static RxReviewRepository instance = new RxReviewRepository();
    private ReviewRepository repository = ReviewRepository.instance();

    public static RxReviewRepository getInstance() {
        return instance;
    }

    /**
     * Get reviews by product Id
     *
     * @param productId the product ID
     * @return
     */
    public Flowable<Review> getReviewByProductId(Integer productId) {
        return Flowable.create(emitter -> {
            repository.getReviewByProductId(productId).forEach(emitter::onNext);
            emitter.onComplete();
        }, DROP);
    }

    /**
     * Return review by review Id
     *
     * @param id
     * @return
     */
    public Maybe<Review> getReviewById(Integer id) {
        return Maybe.create(emitter -> {
            Optional<Review> review = repository.getReviewById(id);
            if (review.isPresent()) {
                emitter.onSuccess(review.get());
            } else {
                emitter.onComplete();
            }
        });
    }

    /**
     * Add a new Review
     *
     * @param review
     * @return
     */
    public Single<Review> addReview(Review review) {
        return Single.create(emitter -> emitter.onSuccess(repository.addReview(review)));
    }

    /**
     * Update a review
     *
     * @param review
     * @return
     */
    public Single<Boolean> updateReview(Review review) {
        return Single.create(emitter -> {
            repository.updateReview(review);
            emitter.onSuccess(Boolean.TRUE);
        });
    }

    /**
     * Delete a review
     *
     * @param id
     * @return
     */
    public Single<Boolean> deleteReview(Integer id) {
        return Single.create(emitter -> {
            repository.deleteReview(id);
            emitter.onSuccess(Boolean.TRUE);
        });
    }
}

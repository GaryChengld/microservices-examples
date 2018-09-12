package io.examples.review.repository;

import io.examples.review.domain.Review;

import java.util.List;
import java.util.Optional;

/**
 * Customer review repository
 *
 * @author Gary Cheng
 */
public interface ReviewRepository {
    /**
     * Return customer reviews by product id
     *
     * @param productId product id
     * @return customer reviews
     */
    List<Review> getReviewBYProductId(Integer productId);

    /**
     * Find review by id
     *
     * @param Id review id
     * @return
     */
    Optional<Review> getReviewById(Integer Id);
}

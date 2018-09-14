package io.examples.review.repository;


import io.examples.review.domain.Review;
import io.examples.review.repository.impl.InMemoryReviewRepository;
import java.util.List;
import java.util.Optional;

/**
 * Customer review repository
 *
 * @author Gary Cheng
 */
public interface ReviewRepository {
    static ReviewRepository instance() {
        return InMemoryReviewRepository.getInstance();
    }

    /**
     * Return customer reviews by product id
     *
     * @param productId product id
     * @return customer reviews
     */
    List<Review> getReviewByProductId(Integer productId);

    /**
     * Find review by id
     *
     * @param id review id
     * @return
     */
    Optional<Review> getReviewById(Integer id);

    /**
     * Add a review
     *
     * @param review the review
     * @return returen the review with generated id
     */
    Review addReview(Review review);

    /**
     * Update a review
     *
     * @param review the review
     */
    void updateReview(Review review);

    /**
     * Delete a review by provided review ID
     *
     * @param id
     */
    void deleteReview(Integer id);
}

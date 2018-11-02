package io.examples.graphql.repository;

import io.examples.graphql.entity.Movie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author Gary Cheng
 */
public interface MovieRepository extends ElasticsearchRepository<Movie, String> {
    /**
     * Search movie by title
     *
     * @param title
     * @return
     */
    List<Movie> findByTitle(String title);

    /**
     * Search movie by description
     *
     * @param description
     * @return
     */

    List<Movie> findByDescription(String description);
}

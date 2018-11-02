package io.examples.graphql.service;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.examples.graphql.entity.Movie;
import io.examples.graphql.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Gary Cheng
 */
@Component
public class FindByIdDataFetcher implements DataFetcher<Movie> {
    @Autowired
    private MovieRepository movieRepository;

    @Override
    public Movie get(DataFetchingEnvironment dataFetchingEnvironment) {
        String id = dataFetchingEnvironment.getArgument("id");
        return movieRepository.findById(id).get();
    }
}

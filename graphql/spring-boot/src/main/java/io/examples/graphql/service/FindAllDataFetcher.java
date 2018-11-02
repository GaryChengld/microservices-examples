package io.examples.graphql.service;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.examples.graphql.entity.Movie;
import io.examples.graphql.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gary Cheng
 */
@Component
public class FindAllDataFetcher implements DataFetcher<List<Movie>> {
    @Autowired
    private MovieRepository movieRepository;

    @Override
    public List<Movie> get(DataFetchingEnvironment dataFetchingEnvironment) {
        List<Movie> movies = new ArrayList<>();
        movieRepository.findAll().forEach(movies::add);
        return movies;
    }
}

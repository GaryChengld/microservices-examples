package io.examples.rest.vertx;

import io.examples.rest.vertx.common.ApiResponse;
import io.examples.rest.vertx.domain.Movie;
import io.examples.rest.vertx.repository.MovieRepository;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.examples.rest.vertx.common.HttpResponseCodes.*;

/**
 * @author Gary Cheng
 */
public class ApiHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);

    private static final String BASE_MOVIE_URL = "/movie/";
    private static final String MOVIE_BY_ID_URL = "/movie/:id";
    private static final String GET_MOVIE_BY_IMDB_ID_URL = "/movie/imdbId/:id";
    private static final String SEARCH_MOVIE_URL = "/movie/search/:criteria";

    private static final ApiResponse RESPONSE_MOVIE_NOT_FOUND = new ApiResponse(1, "error", "Movie not found");
    private static final ApiResponse RESPONSE_MOVIE_DELETED = new ApiResponse(101, "message", "Movie deleted");

    private Vertx vertx;
    private MovieRepository movieRepository;

    private ApiHandler(Vertx vertx, MovieRepository movieRepository) {
        this.vertx = vertx;
        this.movieRepository = movieRepository;
    }

    /**
     * Create API router for Movie Urls
     *
     * @param vertx           the vertx instance
     * @param movieRepository movie Repository object
     * @return
     */
    public static Router apiRouter(Vertx vertx, MovieRepository movieRepository) {
        logger.debug("Creating ApiHandler");
        ApiHandler apiHandler = new ApiHandler(vertx, movieRepository);
        Router apiRouter = Router.router(vertx);
        apiRouter.post(BASE_MOVIE_URL).handler(apiHandler::createMovie);
        apiRouter.put(BASE_MOVIE_URL).handler(apiHandler::saveMovie);
        apiRouter.delete(MOVIE_BY_ID_URL).handler(apiHandler::deleteMovie);
        apiRouter.get(MOVIE_BY_ID_URL).handler(apiHandler::getMovieById);
        apiRouter.get(GET_MOVIE_BY_IMDB_ID_URL).handler(apiHandler::getMovieByImdbId);
        apiRouter.get(SEARCH_MOVIE_URL).handler(apiHandler::searchMovie);
        return apiRouter;
    }

    private void createMovie(RoutingContext context) {
        logger.debug("Received createMovie request");
        MovieResponseHandler responseHandler = new MovieResponseHandler(context);
        context.request().bodyHandler(buffer -> {
            logger.debug("Request body:{}", buffer.toString());
            Movie movie = buffer.toJsonObject().mapTo(Movie.class);
            movieRepository.createMovie(movie).subscribe(responseHandler::handleMovie, responseHandler::handleException);
        });
        logger.debug("Handling createMovie request completed");
    }

    private void saveMovie(RoutingContext context) {
        logger.debug("Received saveMovie request");
        MovieResponseHandler responseHandler = new MovieResponseHandler(context);
        context.request().bodyHandler(buffer -> {
            logger.debug("Request body:{}", buffer.toString());
            Movie movie = buffer.toJsonObject().mapTo(Movie.class);
            movieRepository.updateMovie(movie).subscribe(responseHandler::handleMovie, responseHandler::handleException, responseHandler::handleNotFound);
        });
        logger.debug("Handling saveMovie request completed");
    }

    private void deleteMovie(RoutingContext context) {
        logger.debug("Received deleteMovie request, id={}", context.request().getParam("id"));
        MovieResponseHandler responseHandler = new MovieResponseHandler(context);
        try {
            long id = Long.valueOf(context.request().getParam("id"));
            movieRepository.deleteMovie(id)
                    .subscribe(deleted -> responseHandler.handleApiResponse(SC_OK, RESPONSE_MOVIE_DELETED), responseHandler::handleException, responseHandler::handleNotFound);
        } catch (NumberFormatException e) {
            responseHandler.handleNotFound();
        }
        logger.debug("Handling deleteMovie request completed");
    }

    private void getMovieById(RoutingContext context) {
        logger.debug("Received getMovieById request, id={}", context.request().getParam("id"));
        MovieResponseHandler responseHandler = new MovieResponseHandler(context);
        try {
            long id = Long.valueOf(context.request().getParam("id"));
            movieRepository.getMovieById(id)
                    .subscribeOn(Schedulers.computation()) // subscriptions happen on another thread
                    .subscribe(responseHandler::handleMovie, responseHandler::handleException, responseHandler::handleNotFound);
        } catch (NumberFormatException e) {
            responseHandler.handleNotFound();
        }
        logger.debug("Handling getMovieById request completed");
    }

    private void getMovieByImdbId(RoutingContext context) {
        logger.debug("Received getMovieByImdbId request");
        MovieResponseHandler responseHandler = new MovieResponseHandler(context);
        String id = context.request().getParam("id");
        movieRepository.getMovieByImdb(id)
                .subscribe(responseHandler::handleMovie, responseHandler::handleException, responseHandler::handleNotFound);
        logger.debug("Handling getMovieByImdbId request completed");
    }

    private void searchMovie(RoutingContext context) {
        logger.debug("Received searchMovie request");
        MovieResponseHandler responseHandler = new MovieResponseHandler(context);
        String criteria = context.request().getParam("criteria");
        movieRepository.searchMovie(criteria)
                .subscribe(responseHandler::handleMovieList, responseHandler::handleException);
        logger.debug("Handling searchMovie request completed");
    }

    class MovieResponseHandler {
        RoutingContext context;

        MovieResponseHandler(RoutingContext context) {
            this.context = context;
        }

        void handleMovie(Movie movie) {
            logger.debug("handling movie response");
            context.response()
                    .setStatusCode(SC_OK)
                    .putHeader("Content-Type", "application/json")
                    .end(JsonObject.mapFrom(movie).encode());
        }

        void handleMovieList(List<Movie> movies) {
            logger.debug("handling movie list response");
            JsonArray jsonArray = new JsonArray();
            movies.stream().map(movie -> JsonObject.mapFrom(movie)).forEach(json -> jsonArray.add(json));
            context.response()
                    .setStatusCode(SC_OK)
                    .putHeader("Content-Type", "application/json")
                    .end(jsonArray.encode());
        }

        void handleNotFound() {
            logger.debug("handling movie not found response");
            handleApiResponse(SC_NOT_FOUND, RESPONSE_MOVIE_NOT_FOUND);
        }

        void handleException(Throwable t) {
            logger.debug("handling exception response");
            logger.error(t.getLocalizedMessage());
            handleApiResponse(SC_INTERNAL_SERVER_ERROR, new ApiResponse(99, "error", t.getLocalizedMessage()));
        }

        private void handleApiResponse(int statusCode, ApiResponse apiResponse) {
            context.response()
                    .setStatusCode(statusCode)
                    .putHeader("Content-Type", "application/json")
                    .end(JsonObject.mapFrom(apiResponse).encode());
        }
    }
}

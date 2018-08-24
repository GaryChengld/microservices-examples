package io.examples.rest.vertx.repository;

import io.examples.rest.vertx.domain.Movie;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Movie Repository
 *
 * @author Gary Cheng
 */
public class MovieRepository {
    private static final Logger logger = LoggerFactory.getLogger(MovieRepository.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE IF NOT EXISTS Movie(Id integer identity primary key, " +
            "ImdbId VARCHAR(32) UNIQUE, Title VARCHAR(256) NOT NULL, Language VARCHAR(20), ReleaseDate DATE, Genre VARCHAR(60))";
    private static final String SQL_FIND_MOVIE_BY_ID = "SELECT * FROM Movie WHERE id=?";
    private static final String SQL_FIND_MOVIE_BY_IMDB_ID = "SELECT * FROM Movie WHERE ImdbId=?";
    private static final String SQL_SEARCH_MOVIE = "SELECT * FROM Movie WHERE Title LIKE ? OR Genre LIKE ? ORDER BY Title";
    private static final String SQL_CREATE_MOVIE = "INSERT INTO Movie(ImdbId, Title, Language, releaseDate, Genre) VALUES(?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_MOVIE = "UPDATE Movie SET ImdbId=?, Title=?, Language=?, releaseDate=?, Genre=? WHERE Id=?";
    private static final String SQL_DELETE_MOVIE = "DELETE FROM Movie WHERE id=?";

    private JDBCClient jdbc;

    private MovieRepository(JDBCClient jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Create instance of MovieDepository
     *
     * @param vertx    the vertx
     * @param dbConfig the DB config
     * @return
     */
    public static Single<MovieRepository> create(Vertx vertx, JsonObject dbConfig) {
        return Single.create(emitter -> {
            logger.debug("Creating JDBC client");
            JDBCClient jdbc = JDBCClient.createShared(vertx, dbConfig);
            MovieRepository movieRepository = new MovieRepository(jdbc);
            movieRepository.initDB().subscribe(rs -> emitter.onSuccess(movieRepository), emitter::onError);
        });
    }

    /**
     * Find Movie by Id
     *
     * @param id movie id
     * @return
     */
    public Maybe<Movie> getMovieById(Long id) {
        logger.debug("getMovieById invoked, id:{}", id);
        return jdbc.rxGetConnection()
                .flatMapMaybe(conn -> jdbc.rxQuerySingleWithParams(SQL_FIND_MOVIE_BY_ID, new JsonArray().add(id))
                        .doAfterTerminate(conn::close)
                        .map(this::mapToMovie));
    }

    /**
     * Find Movie by IMDB Id
     *
     * @param imdbId movie IMDB Id
     * @return
     */
    public Maybe<Movie> getMovieByImdb(String imdbId) {
        logger.debug("getMovieByImdb invoked, imdbId:{}", imdbId);
        return jdbc.rxGetConnection()
                .flatMapMaybe(conn -> jdbc.rxQuerySingleWithParams(SQL_FIND_MOVIE_BY_IMDB_ID, new JsonArray().add(imdbId))
                        .doAfterTerminate(conn::close)
                        .map(this::mapToMovie));
    }

    /**
     * Search movie by keyword
     *
     * @param criteria
     * @return
     */
    public Single<List<Movie>> searchMovie(String criteria) {
        logger.debug("searchMovie invoked, criteria:{}", criteria);
        JsonArray parameters = new JsonArray();
        parameters.add("%" + criteria.trim() + "%");
        parameters.add("%" + criteria.trim() + "%");
        return jdbc.rxGetConnection().flatMap(conn -> jdbc.rxQueryWithParams(SQL_SEARCH_MOVIE, parameters)
                .doAfterTerminate(conn::close)
                .map(rs -> rs.getResults().stream().map(this::mapToMovie).collect(Collectors.toList()))
        );
    }

    /**
     * Create a new Movie data
     *
     * @param movie the Movie to create
     * @return
     */
    public Single<Movie> createMovie(Movie movie) {
        logger.debug("createMovie invoked, movie:{}", movie);
        JsonArray parameters = new JsonArray();
        this.addParameters(parameters, movie.getImdbId(), movie.getTitle(), movie.getLanguage(), movie.getReleaseDate(), movie.getGenre());
        return jdbc.rxGetConnection()
                .flatMap(conn -> jdbc.rxUpdateWithParams(SQL_CREATE_MOVIE, parameters)
                        .doAfterTerminate(conn::close)
                        .map(result -> movie));
    }

    /**
     * Update a Movie
     *
     * @param movie the Movie to Update
     * @return
     */
    public Maybe<Movie> updateMovie(Movie movie) {
        logger.debug("updateMovie invoked, movie:{}", movie);
        JsonArray parameters = new JsonArray();
        this.addParameters(parameters, movie.getImdbId(), movie.getTitle(), movie.getLanguage(), movie.getReleaseDate(), movie.getGenre(), movie.getId());
        return jdbc.rxGetConnection().flatMapMaybe(conn -> jdbc.rxUpdateWithParams(SQL_UPDATE_MOVIE, parameters)
                .doAfterTerminate(conn::close)
                .flatMapMaybe(updateResult -> Maybe.create(emitter -> {
                    if (updateResult.getUpdated() > 0) {
                        emitter.onSuccess(movie);
                    } else {
                        emitter.onComplete();
                    }
                }))
        );
    }

    public Maybe<Boolean> deleteMovie(Long id) {
        logger.debug("deleteMovie invoked, id:{}", id);
        return jdbc.rxGetConnection().flatMapMaybe(conn -> jdbc.rxUpdateWithParams(SQL_DELETE_MOVIE, new JsonArray().add(id))
                .doAfterTerminate(conn::close)
                .flatMapMaybe(updateResult -> Maybe.create(emitter -> {
                    if (updateResult.getUpdated() > 0) {
                        emitter.onSuccess(Boolean.TRUE);
                    } else {
                        emitter.onComplete();
                    }
                }))
        );
    }

    private Single<UpdateResult> initDB() {
        logger.debug("Init Database");
        return jdbc.rxGetConnection().flatMap(
                conn -> jdbc.rxUpdate(SQL_CREATE_MOVIE_TABLE)
                        .doAfterTerminate(conn::close));
    }

    private Movie mapToMovie(JsonArray jsonArray) {
        return Movie.builder()
                .id(jsonArray.getLong(0))
                .imdbId(jsonArray.getString(1))
                .title(jsonArray.getString(2))
                .language(jsonArray.getString(3))
                .releaseDate(this.stringToLocalDate(jsonArray.getString(4)))
                .genre(jsonArray.getString(5))
                .build();
    }

    private void addParameters(JsonArray parameters, Object... values) {
        Arrays.stream(values).forEach(value -> this.addParameter(parameters, value));
    }

    private void addParameter(JsonArray parameters, Object value) {
        if (null == value) {
            parameters.addNull();
        } else if (value instanceof Date) {
            parameters.add(((Date) value).toInstant());
        } else if (value instanceof LocalDate) {
            parameters.add(this.localDateToInstant((LocalDate) value));
        } else {
            parameters.add(value);
        }
    }

    private LocalDate stringToLocalDate(String date) {
        return null == date ? null : LocalDate.parse(date, DATE_FORMATTER);
    }

    private LocalDate instantToLocalDate(Instant instant) {
        logger.debug("instant:{}", instant.toString());
        return null != instant ? instant.atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }

    private Instant localDateToInstant(LocalDate localDate) {
        return null != localDate ? localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant() : null;
    }
}

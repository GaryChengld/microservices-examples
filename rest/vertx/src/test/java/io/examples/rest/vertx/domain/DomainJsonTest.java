package io.examples.rest.vertx.domain;

import com.fasterxml.jackson.databind.SerializationFeature;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Gary Cheng
 */
public class DomainJsonTest {
    private static final Logger logger = LoggerFactory.getLogger(DomainJsonTest.class);

    @Before
    public void init() {
        Json.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Json.prettyMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void movieJson() {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setImdbId("tt0076759");
        movie.setTitle("Star wars");
        movie.setReleaseDate(LocalDate.of(1977, 5, 25));
        JsonObject jsonObject = JsonObject.mapFrom(movie);
        logger.debug(jsonObject.encodePrettily());
        assertThat(jsonObject.getString("releaseDate"), is("1977-05-25"));
        String jsonStr = jsonObject.encodePrettily();
        movie = new JsonObject(jsonStr).mapTo(Movie.class);
        logger.debug(movie.toString());
        assertThat(movie.getReleaseDate().getYear(), is(1977));
        assertThat(movie.getReleaseDate().getMonthValue(), is(5));
        assertThat(movie.getReleaseDate().getDayOfMonth(), is(25));
    }
}

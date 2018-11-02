package io.examples.graphql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author Gary Cheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "moviedb", type = "movies", shards = 1, replicas = 0, refreshInterval = "-1")
public class Movie {
    @Id
    private String id;
    @Field
    private String title;
    @Field
    private Integer year;
    @Field
    private String director;
    @Field
    private String writer;
    @Field
    private String stars;
    @Field
    private String description;
}

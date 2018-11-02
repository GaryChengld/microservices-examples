package io.examples.graphql.common;

/**
 * ApiResponses
 *
 * @author Gary Cheng
 */
public class ApiResponses {
    public static final ApiResponse ERR_MOVIE_NOT_FOUND = ApiResponse.error(1001, "Movie not found");
    public static final ApiResponse MSG_UPDATE_SUCCESS = ApiResponse.message(2001, "Update movie successfully");
    public static final ApiResponse MSG_DELETE_SUCCESS = ApiResponse.message(2002, "Delete movie successfully");
}


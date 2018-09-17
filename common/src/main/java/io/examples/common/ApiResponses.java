package io.examples.common;

import io.examples.common.ApiResponse;

/**
 * ApiResponses
 *
 * @author Gary Cheng
 */
public class ApiResponses {
    public static final ApiResponse ERR_PET_NOT_FOUND = ApiResponse.error(1001, "Pet not found");
    public static final ApiResponse ERR_REVIEW_NOT_FOUND = ApiResponse.error(1101, "Review not found");

    public static final ApiResponse MSG_UPDATE_SUCCESS = ApiResponse.message(2001, "Update pet successfully");
    public static final ApiResponse MSG_DELETE_SUCCESS = ApiResponse.message(2002, "Delete pet successfully");
    public static final ApiResponse MSG_UPDATE_REVIEW_SUCCESS = ApiResponse.message(2101, "Update review successfully");
    public static final ApiResponse MSG_DELETE_REVIEW_SUCCESS = ApiResponse.message(2102, "Delete review successfully");
}

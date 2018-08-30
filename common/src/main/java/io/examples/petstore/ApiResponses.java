package io.examples.petstore;

import io.examples.common.ApiResponse;

/**
 * ApiResponses
 *
 * @author Gary Cheng
 */
public class ApiResponses {
    public static final ApiResponse ERR_PET_NOT_FOUND = ApiResponse.error(101, "Pet not found");
    public static final ApiResponse MSG_UPDATE_SUCCESS = ApiResponse.message(1, "Update pet successfully");
    public static final ApiResponse MSG_DELETE_SUCCESS = ApiResponse.message(2, "Delete pet successfully");
}

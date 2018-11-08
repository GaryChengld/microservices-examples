package io.examples.spring.hateoas.common;

/**
 * ApiResponses
 *
 * @author Gary Cheng
 */
public class ApiResponses {
    public static final ApiResponse ERR_PET_NOT_FOUND = ApiResponse.error(1001, "Pet not found");
    public static final ApiResponse MSG_UPDATE_SUCCESS = ApiResponse.message(2001, "Update pet successfully");
    public static final ApiResponse MSG_DELETE_SUCCESS = ApiResponse.message(2002, "Delete pet successfully");
}


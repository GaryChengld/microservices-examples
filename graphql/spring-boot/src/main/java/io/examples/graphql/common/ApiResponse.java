package io.examples.graphql.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic Api Response
 *
 * @author Gary Cheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    public static final String TYPE_MESSAGE = "Message";
    public static final String TYPE_ERROR = "Error";

    private Integer code;
    private String type;
    private String message;

    public static ApiResponse message(Integer code, String message) {
        return new ApiResponse(code, TYPE_MESSAGE, message);
    }

    public static ApiResponse error(Integer code, String message) {
        return new ApiResponse(code, TYPE_ERROR, message);
    }
}

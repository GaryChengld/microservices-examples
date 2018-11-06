package io.examples.micronaut.common;

/**
 * Generic Api Response
 *
 * @author Gary Cheng
 */
public class ApiResponse {
    public static final String TYPE_MESSAGE = "Message";
    public static final String TYPE_ERROR = "Error";
    private Integer code;
    private String type;
    private String message;

    public ApiResponse() {
    }

    public ApiResponse(Integer code, String type, String message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }

    public static ApiResponse message(Integer code, String message) {
        return new ApiResponse(code, TYPE_MESSAGE, message);
    }

    public static ApiResponse error(Integer code, String message) {
        return new ApiResponse(code, TYPE_ERROR, message);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

/**
 * This ApiResponse class provides a common format for API responses.
 * Format is based on JSend (https://github.com/omniti-labs/jsend).
 */

package com.example.rhymedetectorbackend;

public class ApiResponse<T> {

    private String status;   // "success", "fail", "error"
    private T data;          // response data (optional for "error")
    private String message;  // message for "error" responses
    private Integer code;    // (Optional) HTTP code or custom code

    public ApiResponse(String status, T data, String message, Integer code) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    // Success response without message or code
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null, null);
    }

    // Fail response
    public static <T> ApiResponse<T> fail(String message, T data) {
        return new ApiResponse<>("fail", data, message, null);
    }

    // Error response
    public static <T> ApiResponse<T> error(String message, Integer code, T data) {
        return new ApiResponse<>("error", data, message, code);
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}

package packagee;

public final class Response<T> {

    private final StatusCode statusCode;
    private final String message;
    private final T data;

    private Response(StatusCode statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> ok(String message, T data) {
        return new Response<>(StatusCode.OK, message, data);
    }

    public static <T> Response<T> badRequest(String message) {
        return new Response<>(StatusCode.BAD_REQUEST, message, null);
    }

    public static <T> Response<T> notFound(String message) {
        return new Response<>(StatusCode.NOT_FOUND, message, null);
    }

    public static <T> Response<T> unauthorized(String message) {
        return new Response<>(StatusCode.UNAUTHORIZED, message, null);
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return statusCode == StatusCode.OK;
    }
}

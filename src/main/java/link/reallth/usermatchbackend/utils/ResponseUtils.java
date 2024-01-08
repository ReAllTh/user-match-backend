package link.reallth.usermatchbackend.utils;

import link.reallth.usermatchbackend.common.BaseResponse;
import link.reallth.usermatchbackend.constants.enums.CODES;
import link.reallth.usermatchbackend.exception.BaseException;

/**
 * response utils
 *
 * @author ReAllTh
 */
public class ResponseUtils {

    private ResponseUtils() {
    }

    /**
     * generate a success response without data
     *
     * @param <T> response data type
     * @return base response without data
     */
    public static <T> BaseResponse<T> success() {
        CODES codes = CODES.SUCCESS;
        int code = codes.getCode();
        String msg = codes.getMsg();
        return new BaseResponse<>(code, msg, "operation success", null);
    }

    /**
     * generate a success response with data
     *
     * @param data response data
     * @param <T>  response data type
     * @return base response with data
     */
    public static <T> BaseResponse<T> success(T data) {
        CODES codes = CODES.SUCCESS;
        int code = codes.getCode();
        String msg = codes.getMsg();
        return new BaseResponse<>(code, msg, "operation success", data);
    }

    /**
     * generate a BaseException response with info
     *
     * @param e   BaseException
     * @param <T> T
     * @return base response with error info
     */
    public static <T> BaseResponse<T> baseError(BaseException e) {
        int code = e.getCode();
        String msg = e.getMsg();
        String description = e.getDescription();
        return new BaseResponse<>(code, msg, description, null);
    }

    /**
     * generate a NullPointerException response with info
     *
     * @param e   NullPointerException
     * @param <T> T
     * @return base response with error info
     */
    public static <T> BaseResponse<T> nullPointerError(NullPointerException e) {
        CODES codes = CODES.SYSTEM_ERR;
        int code = codes.getCode();
        String msg = codes.getMsg();
        String description = rootCauseMessage(e);
        return new BaseResponse<>(code, msg, description, null);
    }

    /**
     * generate an Exception response with info
     *
     * @param e   Exception
     * @param <T> T
     * @return base response with error info
     */
    public static <T> BaseResponse<T> error(Exception e) {
        CODES codes = CODES.ERROR;
        int code = codes.getCode();
        String msg = codes.getMsg();
        String description = rootCauseMessage(e);
        return new BaseResponse<>(code, msg, description, null);
    }

    /**
     * get a Throwable`s root cause message
     *
     * @param throwable throwable
     * @return root cause message of throwable
     */
    private static String rootCauseMessage(Throwable throwable) {
        while (throwable.getCause() != null)
            throwable = throwable.getCause();
        return throwable.getMessage();
    }
}

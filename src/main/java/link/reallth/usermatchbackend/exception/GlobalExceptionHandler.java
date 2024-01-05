package link.reallth.usermatchbackend.exception;

import link.reallth.usermatchbackend.common.BaseResponse;
import link.reallth.usermatchbackend.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * global exception handler
 *
 * @author ReAllTh
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * generate a BaseException response with info
     *
     * @param e   BaseException
     * @param <T> T
     * @return base response with error info
     */
    @ExceptionHandler(BaseException.class)
    public <T> BaseResponse<T> baseExceptionHandler(BaseException e) {
        return ResponseUtils.baseError(e);
    }

    /**
     * generate a NullPointerException response with info
     *
     * @param e   NullPointerException
     * @param <T> T
     * @return base response with error info
     */
    @ExceptionHandler(NullPointerException.class)
    public <T> BaseResponse<T> baseExceptionHandler(NullPointerException e) {
        return ResponseUtils.nullPointerError(e);
    }

    /**
     * generate an Exception response with info
     *
     * @param e   Exception
     * @param <T> T
     * @return base response with error info
     */
    @ExceptionHandler(Exception.class)
    public <T> BaseResponse<T> baseExceptionHandler(Exception e) {
        return ResponseUtils.error(e);
    }
}

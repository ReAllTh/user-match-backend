package link.reallth.usermatchbackend.exception;

import link.reallth.usermatchbackend.constants.enums.CODES;
import lombok.AllArgsConstructor;

/**
 * global exception
 *
 * @author ReAllTh
 */
public class GlobalException extends RuntimeException {
    private final int code;
    private final String msg;
    private final String description;

    public GlobalException(CODES code, String description) {
        this.code = code.getCode();
        this.msg = code.getMsg();
        this.description = description;
    }
}

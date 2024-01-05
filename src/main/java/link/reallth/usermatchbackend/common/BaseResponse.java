package link.reallth.usermatchbackend.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * base response
 *
 * @author ReAllTh
 */
@Data
@AllArgsConstructor
public class BaseResponse<T> {
    private final int code;
    private final String msg;
    private final String description;
    private final T data;
}

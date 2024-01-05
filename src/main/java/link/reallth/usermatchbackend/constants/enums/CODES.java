package link.reallth.usermatchbackend.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * response codes enum
 *
 * @author ReAllTh
 */
@Getter
@AllArgsConstructor
public enum CODES {
    // base code
    SUCCESS(0, "ok"),
    ERROR(-1, "undefined error"),

    // permission err - 10000
    PERMISSION_ERR(10001, "permission error"),

    // param err - 20000
    PARAM_ERR(20001, "parameters error"),

    // business err - 40000
    BUSINESS_ERR(40001, "business error"),

    // system err - 50000
    SYSTEM_ERR(50001, "system error");

    private final int code;
    private final String msg;
}

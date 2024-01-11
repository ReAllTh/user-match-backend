package link.reallth.usermatchbackend.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * team status enums
 *
 * @author ReAllTh
 */
@Getter
@AllArgsConstructor
public enum STATUS {

    PUBLIC(0),
    PRIVATE(1);

    private final int val;
}

package link.reallth.usermatchbackend.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * team position enums
 *
 * @author ReAllTh
 */
@Getter
@AllArgsConstructor
public enum POSITIONS {

    MEMBER(0),
    CREATOR(1);

    private final int val;
}

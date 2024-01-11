package link.reallth.usermatchbackend.constants.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * user role enums
 *
 * @author ReAllTh
 */
@Getter
@AllArgsConstructor
public enum ROLE {
    DEFAULT(0),
    ADMIN(1);
    private final int val;
}

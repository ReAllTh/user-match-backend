package link.reallth.usermatchbackend.model.dto;

import lombok.Data;

/**
 * sign in dto
 *
 * @author ReAllTh
 */
@Data
public class SignInDTO {
    private final String username;
    private final String email;
    private final String password;
}

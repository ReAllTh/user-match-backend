package link.reallth.usermatchbackend.model.dto;

import lombok.Data;

/**
 * sign in data transfer object
 *
 * @author ReAllTh
 */
@Data
public class UserSignInDTO {
    private String username;
    private String email;
    private String password;
}

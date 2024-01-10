package link.reallth.usermatchbackend.model.ro;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

/**
 * sign in request object
 *
 * @author ReAllTh
 */
@Data
public class UserSignInRO {

    private String username;
    private String email;

    @Parameter(required = true)
    private String password;
}
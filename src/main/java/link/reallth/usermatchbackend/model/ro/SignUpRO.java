package link.reallth.usermatchbackend.model.ro;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

import java.util.List;

/**
 * sign up request object
 *
 * @author ReAllTh
 */
@Data
public class SignUpRO {

    private String username;

    private String email;

    @Parameter(required = true)
    private String password;

    private String avatar;

    private List<String> tags;
}
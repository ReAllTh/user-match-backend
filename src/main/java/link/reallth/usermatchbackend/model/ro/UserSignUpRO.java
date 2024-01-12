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
public class UserSignUpRO {

    private String username;

    @Parameter(required = true)
    private String password;

    private String email;
    private String avatar;

    @Parameter(example = "c,c++,java")
    private List<String> tags;
}
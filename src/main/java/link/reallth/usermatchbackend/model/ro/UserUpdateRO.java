package link.reallth.usermatchbackend.model.ro;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateRO {

    private String id;
    private String username;
    private String email;
    private String password;
    private String avatar;

    @Parameter(description = "default - 0 | admin - 1")
    private Integer role;

    @Parameter(example = "c,c++,java")
    private List<String> tags;
}

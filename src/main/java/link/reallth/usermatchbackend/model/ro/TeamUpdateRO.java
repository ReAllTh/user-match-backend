package link.reallth.usermatchbackend.model.ro;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

@Data
public class TeamUpdateRO {

    @Parameter(required = true)
    private String id;

    private String teamName;

    @Parameter(description = "null is fine but max length is 2048 characters")
    private String description;

    @Parameter(description = "2-5 allowed")
    private Integer maxUser;

    @Parameter(description = "public - 0 | private - 1")
    private Integer status;

    @Parameter(description = "required only team private")
    private String password;

    @Parameter(example = "2024-01-01 00:00:00")
    private String expireTime;
}
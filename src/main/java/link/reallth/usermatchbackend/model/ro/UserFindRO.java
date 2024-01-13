package link.reallth.usermatchbackend.model.ro;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

import java.util.List;

/**
 * user find request object
 *
 * @author ReAllTh
 */
@Data
public class UserFindRO {

    private String id;
    private String username;
    private String email;

    @Parameter(description = "default - 0 | admin - 1")
    private Integer role;

    @Parameter(example = "c,java")
    private List<String> tags;

    @Parameter(example = "2024-01-01 10:24:52")
    private String createTimeFrom;

    @Parameter(example = "2024-01-02 00:00:00")
    private String createTimeTo;

    @Parameter(required = true)
    private Integer page;

    @Parameter(required = true)
    private Integer pageSize;
}

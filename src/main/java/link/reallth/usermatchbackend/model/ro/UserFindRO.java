package link.reallth.usermatchbackend.model.ro;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

import java.util.Date;
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
    private Integer role;
    private List<String> tags;
    private Date createTime;

    @Parameter(required = true)
    private int page;
    @Parameter(required = true)
    private int pageSize;
}

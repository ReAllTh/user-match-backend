package link.reallth.usermatchbackend.model.ro;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

import java.util.Date;

/**
 * team find request object
 *
 * @author ReAllTh
 */
@Data
public class TeamFindRO {

    private String id;
    private String creatorName;
    private String searchText;
    private Boolean isFull;

    @Parameter(description = "public - 0 | private - 1")
    private Integer status;

    @Parameter(example = "2024-01-02 00:00:00")
    private Date createTime;

    @Parameter(required = true)
    private Integer page;

    @Parameter(required = true)
    private Integer pageSize;
}
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

    @Parameter(example = "0-365 allowed")
    private Integer recentDay;

    @Parameter(required = true)
    private Integer page;

    @Parameter(required = true)
    private Integer pageSize;
}
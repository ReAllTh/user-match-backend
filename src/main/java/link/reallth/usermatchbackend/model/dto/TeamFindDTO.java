package link.reallth.usermatchbackend.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * team find data transfer object
 *
 * @author ReAllTh
 */
@Data
public class TeamFindDTO {

    private String id;
    private String creatorName;
    private String searchText;
    private Boolean isFull;
    private Integer status;
    private Date createTimeFrom;

    private Integer page;
    private Integer pageSize;
}

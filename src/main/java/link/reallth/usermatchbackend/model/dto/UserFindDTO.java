package link.reallth.usermatchbackend.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * user find data transfer object
 *
 * @author ReAllTh
 */
@Data
public class UserFindDTO {

    private String id;
    private String username;
    private String email;
    private Integer role;
    private List<String> tags;
    private Date createTimeFrom;
    private Date createTimeTo;

    private int page;
    private int pageSize;
}

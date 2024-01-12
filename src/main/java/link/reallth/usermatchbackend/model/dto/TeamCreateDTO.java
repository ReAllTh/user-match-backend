package link.reallth.usermatchbackend.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * team create data transfer object
 *
 * @author ReAllTh
 */
@Data
public class TeamCreateDTO {

    private String teamName;
    private String description;
    private String password;
    private Integer maxUser;
    private Date expireTime;
    private Integer status;
}

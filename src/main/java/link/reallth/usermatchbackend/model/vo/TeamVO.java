package link.reallth.usermatchbackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * team view object
 *
 * @author ReAllTh
 */
@Data
public class TeamVO implements Serializable {

    private String id;
    private UserVO creator;
    private String teamName;
    private String description;
    private Integer maxUser;
    private List<UserVO> members;
    private Date expireTime;
    private Integer status;
    private Date createTime;

    @Serial
    private static final long serialVersionUID = 1L;
}
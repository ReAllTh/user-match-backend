package link.reallth.usermatchbackend.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * user vo
 *
 * @author ReAllTh
 */
@Data
public class UserVO implements Serializable {
    private String id;
    private String username;
    private String email;
    private String avatar;
    private Integer role;
    private List<String> tags;
    private Date createTime;

    @Serial
    private static final long serialVersionUID = 1L;
}
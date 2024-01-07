package link.reallth.usermatchbackend.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * user persistent object
 *
 * @author ReAllTh
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    /**
     * user id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * username
     */
    private String username;

    /**
     * user email
     */
    private String email;

    /**
     * user password
     */
    @TableField(value = "passwd")
    private String password;

    /**
     * user avatar url
     */
    private String avatar;

    /**
     * user role
     * -1 admin
     * -0 normal
     */
    private Integer role;

    /**
     * user tags json
     */
    private String tags;

    /**
     * logical deleted
     * -1 deleted
     * -0 normal
     */
    private Integer deleted;

    /**
     * record create time
     */
    private Date createTime;

    /**
     * record update time
     */
    private Date updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
package link.reallth.usermatchbackend.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * user table
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * user id
     */
    @TableId
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
    private String passwd;

    /**
     * user avatar url
     */
    private String avatar;

    /**
     * user role
-1 admin
-0 normal
     */
    private Integer role;

    /**
     * user tags json
     */
    private String tags;

    /**
     * logical deleted
-1 deleted
-0 normal
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
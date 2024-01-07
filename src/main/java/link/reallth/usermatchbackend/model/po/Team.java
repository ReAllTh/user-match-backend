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
 * team persistent object
 *
 * @author ReAllTh
 */
@TableName(value = "team")
@Data
public class Team implements Serializable {

    /**
     * team id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * team creator id
     */
    private String creatorId;

    /**
     * team name
     */
    private String teamName;

    /**
     * team description
     */
    private String description;

    /**
     * team password(if private)
     */
    @TableField(value = "passwd")
    private String password;

    /**
     * team max user
     */
    private Integer maxUser;

    /**
     * team expire date
     */
    private Date expireTime;

    /**
     * team status
     * -1 private
     * -0 public
     */
    private Integer status;

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
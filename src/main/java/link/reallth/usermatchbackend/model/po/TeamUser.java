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
 * team_user po
 *
 * @author ReAllTh
 */
@TableName(value = "team_user")
@Data
public class TeamUser implements Serializable {

    /**
     * team_user id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * team creator id
     */
    private String userId;

    /**
     * team id
     */
    private String teamId;

    /**
     * user join date
     */
    private Date joinTime;

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
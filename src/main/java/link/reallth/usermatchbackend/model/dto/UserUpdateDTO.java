package link.reallth.usermatchbackend.model.dto;

import lombok.Data;

/**
 * user update data transfer object
 *
 * @author ReAllTh
 */
@Data
public class UserUpdateDTO {

    private String id;
    private String username;
    private String email;
    private String password;
    private String avatar;
    private Integer role;
    private String tags;
}